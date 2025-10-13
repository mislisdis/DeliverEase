import express from "express";
import session from "express-session";
import exphbs from "express-handlebars";
import path from "path";
import bodyParser from "body-parser";
import { fileURLToPath } from "url";
import hbs from "hbs";

// --- Setup for __dirname (ES Module fix) ---
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// --- Initialize app ---
const app = express();
const PORT = process.env.PORT || 3000;

// --- Middleware ---
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

// --- Session Configuration ---
app.use(session({
  secret: process.env.SESSION_SECRET || "DeliverEaseDevSecret",
  resave: false,
  saveUninitialized: false,
  cookie: {
    maxAge: 1000 * 60 * 60 * 8, // 8 hours
    httpOnly: true,
    secure: false // set to true in production with HTTPS
  }
}));

// --- Make user available in all templates ---
app.use((req, res, next) => {
  res.locals.user = req.session.user || null;
  next();
});

// --- Static Files ---
app.use(express.static(path.join(__dirname, "public")));

// --- Handlebars Setup ---
app.engine(
  "hbs",
  exphbs.engine({
    extname: "hbs",
    defaultLayout: "main",
    layoutsDir: path.join(__dirname, "views", "layouts"),
    helpers: {
      eq: (a, b) => a === b,
      ifCond: (v1, v2, options) => (v1 === v2 ? options.fn(this) : options.inverse(this))
    }
  })
);
app.set("view engine", "hbs");
app.set("views", path.join(__dirname, "views"));

// --- Register partials ---
hbs.registerPartials(path.join(__dirname, "views", "partials"));

// --- Routes ---
import authRoutes from "./routes/auth.js";
import managerRoutes from "./routes/manager.js";
import deliveryRoutes from "./routes/delivery.js";
import consumerRoutes from "./routes/consumer.js";

app.use("/", authRoutes);
app.use("/manager", managerRoutes);
app.use("/delivery", deliveryRoutes);
app.use("/consumer", consumerRoutes);

// --- Home Route (Redirect to proper dashboard) ---
app.get("/", (req, res) => {
  if (req.session.user) {
    switch (req.session.user.role) {
      case "manager":
        return res.redirect("/manager/dashboard");
      case "driver":
        return res.redirect("/delivery/dashboard");
      case "consumer":
        return res.redirect("/consumer/dashboard");
      default:
        return res.redirect("/login");
    }
  }
  res.redirect("/login");
});

// --- Start Server ---
app.listen(PORT, () => {
  console.log(`🚀 DeliverEase server running on http://localhost:${PORT}`);
});
