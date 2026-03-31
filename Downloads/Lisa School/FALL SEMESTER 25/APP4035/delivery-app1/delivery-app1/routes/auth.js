// routes/auth.js
const express = require("express");
const router = express.Router();
const path = require("path");
const fs = require("fs-extra");
const { comparePassword } = require("../utils/authHelpers");

const usersPath = path.join(__dirname, "../data/users.json");

// GET /login
router.get("/login", (req, res) => {
  if (req.session.user) return res.redirect("/dashboard");
  res.render("login", { title: "Login" });
});

// POST /login
router.post("/login", async (req, res) => {
  const { username, password } = req.body;
  try {
    const users = await fs.readJson(usersPath);
    const user = users[username];
    if (!user) return res.render("login", { title: "Login", error: "Invalid credentials" });

    const ok = await comparePassword(password, user.password);
    if (!ok) return res.render("login", { title: "Login", error: "Invalid credentials" });

    // success
    req.session.user = username;
    req.session.role = user.role || "driver";
    return res.redirect("/dashboard");
  } catch (err) {
    console.error(err);
    res.render("login", { title: "Login", error: "Server error" });
  }
});

// GET /logout
router.get("/logout", (req, res) => {
  req.session.destroy(() => {
    res.clearCookie("connect.sid");
    res.redirect("/login");
  });
});

module.exports = router;
