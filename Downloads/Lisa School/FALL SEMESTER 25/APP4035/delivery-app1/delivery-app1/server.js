// server.js
const express = require("express");
const path = require("path");
const session = require("express-session");
const FileStore = require("session-file-store")(session);
const { engine } = require("express-handlebars");
const fs = require("fs-extra");
const { hashPassword } = require("./utils/authHelpers");
const hbsHelpers = require("./utils/helpers"); // ✅ Import centralized helpers

const app = express();
const PORT = process.env.PORT || 3000;

// --------------------
// Handlebars setup
// --------------------
app.engine(
  "hbs",
  engine({
    extname: ".hbs",
    defaultLayout: "main",
    layoutsDir: path.join(__dirname, "views", "layouts"),
    partialsDir: path.join(__dirname, "views", "partials"),
    helpers: hbsHelpers, // ✅ Load all helpers from helpers.js
  })
);

app.set("view engine", "hbs");
app.set("views", path.join(__dirname, "views"));

// --------------------
// Middleware
// --------------------
app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(express.static(path.join(__dirname, "public")));

// Ensure sessions directory exists before session init
fs.ensureDirSync(path.join(__dirname, "sessions"));

// --------------------
// Session setup
// --------------------
const fileStore = new FileStore({
  path: path.join(__dirname, "sessions"),
  ttl: 60 * 30, // 30 minutes
});

app.use(
  session({
    store: fileStore,
    secret: "supersecretkey",
    resave: false,
    saveUninitialized: false,
    cookie: { maxAge: 1000 * 60 * 30 },
  })
);

// Make session data available in templates
app.use((req, res, next) => {
  res.locals.session = req.session || {};
  res.locals.activePage = ""; // Initialize active page
  next();
});

// --------------------
// Compute assignedCount (for drivers)
// --------------------
app.use(async (req, res, next) => {
  res.locals.assignedCount = 0;
  res.locals.showDeliveriesLink = false;

  try {
    if (req.session && req.session.user) {
      const deliveriesPath = path.join(__dirname, "data", "deliveries.json");
      const deliveries = (await fs.pathExists(deliveriesPath))
        ? await fs.readJson(deliveriesPath)
        : [];

      const assigned = deliveries.filter(
        (d) => d.driver === req.session.user
      );

      res.locals.assignedCount = assigned.length;

      // Navigation visibility logic
      if (["admin", "manager"].includes(req.session.role)) {
        res.locals.showDeliveriesLink = true;
      } else if (req.session.role === "driver" && assigned.length > 0) {
        res.locals.showDeliveriesLink = true;
      }
    }
  } catch (err) {
    console.error("⚠️ Error computing assignedCount:", err.message);
  }

  next();
});

// --------------------
// Seed initial data
// --------------------
(async () => {
  const dataDir = path.join(__dirname, "data");
  await fs.ensureDir(dataDir);

  const usersPath = path.join(dataDir, "users.json");
  const deliveriesPath = path.join(dataDir, "deliveries.json");

  if (!(await fs.pathExists(usersPath))) {
    const adminPass = await hashPassword("admin123");
    const managerPass = await hashPassword("manager123");
    const driverPass = await hashPassword("driver123");

    const seedUsers = {
      admin: { role: "admin", password: adminPass },
      manager1: { role: "manager", password: managerPass },
      driver1: { role: "driver", password: driverPass },
      driver2: { role: "driver", password: driverPass },
    };

    await fs.writeJson(usersPath, seedUsers, { spaces: 2 });
    console.log("✅ users.json seeded");
  }

  if (!(await fs.pathExists(deliveriesPath))) {
    const seedDeliveries = [
      {
        id: Date.now(),
        recipient: "Alice",
        address: "1 Main St",
        item: "Parcel A",
        driver: "driver1",
        status: "assigned",
        createdAt: new Date().toISOString(),
      },
    ];
    await fs.writeJson(deliveriesPath, seedDeliveries, { spaces: 2 });
    console.log("✅ deliveries.json seeded");
  }
})().catch((err) => console.error("Init error:", err));

// --------------------
// Landing Page Route
// --------------------
app.get("/", (req, res) => {
  // If user is already logged in, redirect to dashboard
  if (req.session.user) {
    return res.redirect("/dashboard");
  }
  
  // Otherwise show landing page
  res.render("landing", { 
    title: "DeliverEase | Move Better, Deliver Faster",
    layout: false // Don't use the main layout for landing page
  });
});

// --------------------
// Routes
// --------------------
app.use("/", require("./routes/auth"));
app.use("/", require("./routes/appRoutes"));

// --------------------
// Error handlers
// --------------------
app.use((err, req, res, next) => {
  console.error("❌ Server Error:", err);
  res.status(500).send("Server error");
});

app.use((req, res) => {
  res.status(404).render("404", { title: "Not Found" });
});

// --------------------
// Start server
// --------------------
app.listen(PORT, () =>
  console.log(`Server running on http://localhost:${PORT}`)
);