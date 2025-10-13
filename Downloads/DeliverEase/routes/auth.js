// routes/auth.js
import express from "express";
import bcrypt from "bcrypt";
import { readData, writeData, generateId } from "../utils/dataHandler.js";

const router = express.Router();
const USERS_FILE = "users.json";
const SALT_ROUNDS = 10;

/* ==========================================
   GET /login – Render login page
========================================== */
router.get("/login", (req, res) => {
  // If already logged in, redirect to appropriate dashboard
  if (req.session && req.session.user) {
    const { role } = req.session.user;
    if (role === "manager") return res.redirect("/manager/dashboard");
    if (role === "driver" || role === "delivery") return res.redirect("/delivery/dashboard");
    if (role === "consumer") return res.redirect("/consumer/dashboard");
  }

  res.render("login", { title: "Login", error: null });
});

/* ==========================================
   POST /login – Authenticate user
========================================== */
router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res
        .status(400)
        .render("login", { title: "Login", error: "Email and password are required." });
    }

    const users = await readData(USERS_FILE);
    const user = users.find(
      (u) => u.email.toLowerCase() === (email || "").toLowerCase()
    );

    if (!user) {
      return res
        .status(401)
        .render("login", { title: "Login", error: "Invalid email or password." });
    }

    const match = await bcrypt.compare(password, user.password_hash);
    if (!match) {
      return res
        .status(401)
        .render("login", { title: "Login", error: "Invalid email or password." });
    }

    // ✅ Successful login — save minimal info to session
    req.session.user = {
      id: user.id,
      name: user.name,
      email: user.email,
      role: user.role,
    };

    // ✅ Redirect user by role
    switch (user.role) {
      case "manager":
        return res.redirect("/manager/dashboard");
      case "driver":
      case "delivery":
        return res.redirect("/delivery/dashboard");
      case "consumer":
        return res.redirect("/consumer/dashboard");
      default:
        return res.redirect("/");
    }
  } catch (err) {
    console.error("Login error:", err);
    return res
      .status(500)
      .render("login", { title: "Login", error: "Server error. Please try again later." });
  }
});

/* ==========================================
   GET /logout – Destroy session and redirect
========================================== */
router.get("/logout", (req, res) => {
  req.session.destroy((err) => {
    if (err) console.error("Session destroy error:", err);
    res.clearCookie("connect.sid");
    return res.redirect("/login");
  });
});

/* ==========================================
   GET /register – Render consumer registration page
========================================== */
router.get("/register", (req, res) => {
  res.render("register", { title: "Register", error: null });
});

/* ==========================================
   POST /register – Handle consumer registration
========================================== */
router.post("/register", async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res
        .status(400)
        .render("register", { title: "Register", error: "All fields are required." });
    }

    const users = await readData(USERS_FILE);
    const exists = users.some(
      (u) => u.email.toLowerCase() === email.toLowerCase()
    );
    if (exists) {
      return res
        .status(400)
        .render("register", { title: "Register", error: "Email is already in use." });
    }

    // ✅ Hash password before saving
    const hash = await bcrypt.hash(password, SALT_ROUNDS);

    const newUser = {
      id: generateId("u", 4), // e.g. u_4821
      name,
      email,
      password_hash: hash,
      role: "consumer",
      created_at: new Date().toISOString(),
    };

    users.push(newUser);
    await writeData(USERS_FILE, users);

    // ✅ Auto-login new consumer
    req.session.user = {
      id: newUser.id,
      name: newUser.name,
      email: newUser.email,
      role: newUser.role,
    };

    return res.redirect("/consumer/dashboard");
  } catch (err) {
    console.error("Registration error:", err);
    return res
      .status(500)
      .render("register", { title: "Register", error: "Server error. Please try again later." });
  }
});

export default router;
