// routes/appRoutes.js
const express = require("express");
const router = express.Router();
const path = require("path");
const fs = require("fs-extra");
const { hashPassword } = require("../utils/authHelpers");

const deliveriesPath = path.join(__dirname, "../data/deliveries.json");
const usersPath = path.join(__dirname, "../data/users.json");
const archivedPath = path.join(__dirname, "../data/archivedDeliveries.json");

// -------------------------------
// Helper functions
// -------------------------------
async function readDeliveries() {
  return (await fs.pathExists(deliveriesPath)) ? await fs.readJson(deliveriesPath) : [];
}
async function writeDeliveries(data) {
  return await fs.writeJson(deliveriesPath, data, { spaces: 2 });
}

async function readUsers() {
  return (await fs.pathExists(usersPath)) ? await fs.readJson(usersPath) : {};
}
async function writeUsers(users) {
  return await fs.writeJson(usersPath, users, { spaces: 2 });
}

async function readArchived() {
  return (await fs.pathExists(archivedPath)) ? await fs.readJson(archivedPath) : [];
}
async function writeArchived(data) {
  return await fs.writeJson(archivedPath, data, { spaces: 2 });
}

// -------------------------------
// Middleware
// -------------------------------
function requireAuth(req, res, next) {
  if (!req.session.user) return res.redirect("/login");
  next();
}

function requireAdmin(req, res, next) {
  if (req.session.role !== "admin")
    return res.status(403).render("403", { title: "Forbidden" });
  next();
}

// -------------------------------
// Root redirect
// -------------------------------
router.get("/", (req, res) => {
  if (req.session.user) return res.redirect("/dashboard");
  return res.redirect("/login");
});

// -------------------------------
// Dashboard
// -------------------------------
router.get("/dashboard", requireAuth, async (req, res) => {
  const deliveries = await readDeliveries();

  if (["admin", "manager"].includes(req.session.role)) {
    // For admin/manager: Just count active deliveries (no need for complex stats)
    const activeDeliveriesCount = deliveries.length;
    
    return res.render("dashboard", {
      title: "Dashboard",
      user: req.session.user,
      deliveriesCount: activeDeliveriesCount,
      stats: { totalDeliveries: activeDeliveriesCount } // Simple stat for template
    });
  }

  // For drivers: Just count their assigned deliveries
  const assigned = deliveries.filter((d) => d.driver === req.session.user);

  return res.render("dashboard", {
    title: "Dashboard",
    user: req.session.user,
    deliveriesCount: assigned.length,
    stats: { totalDeliveries: assigned.length } // Simple stat for template consistency
  });
});

// -------------------------------
// Deliveries (with filtering)
// -------------------------------
router.get("/deliveries", requireAuth, async (req, res) => {
  const deliveries = await readDeliveries();
  const role = req.session.role;
  const user = req.session.user;
  const query = req.query || {};

  let visibleDeliveries = deliveries;

  if (role === "driver") {
    visibleDeliveries = deliveries.filter((d) => d.driver === user);
  }

  // Apply filters (ID, recipient, address, item, driver (admin/manager only), status, date)
  visibleDeliveries = visibleDeliveries.filter((d) => {
    const idMatch = query.id ? String(d.id).includes(String(query.id).trim()) : true;
    const recipientMatch = query.recipient
      ? d.recipient.toLowerCase().includes(String(query.recipient).toLowerCase())
      : true;
    const addressMatch = query.address
      ? d.address.toLowerCase().includes(String(query.address).toLowerCase())
      : true;
    const itemMatch = query.item
      ? d.item.toLowerCase().includes(String(query.item).toLowerCase())
      : true;
    const driverMatch =
      role !== "driver" && query.driver
        ? d.driver && d.driver.toLowerCase().includes(String(query.driver).toLowerCase())
        : true;
    const statusMatch = query.status ? d.status === query.status : true;
    const dateMatch = query.date
      ? new Date(d.createdAt).toISOString().slice(0, 10) === query.date
      : true;

    return (
      idMatch &&
      recipientMatch &&
      addressMatch &&
      itemMatch &&
      driverMatch &&
      statusMatch &&
      dateMatch
    );
  });

  return res.render("deliveries", {
    title: "Deliveries",
    deliveries: visibleDeliveries,
    role,
    user,
    query,
  });
});

// -------------------------------
// Delivery Details Page
// -------------------------------
router.get("/deliveries/:id", requireAuth, async (req, res) => {
  const deliveries = await readDeliveries();
  const del = deliveries.find((d) => String(d.id) === String(req.params.id));

  if (!del) return res.status(404).render("404", { title: "Not Found" });

  if (["admin", "manager"].includes(req.session.role) || del.driver === req.session.user) {
    return res.render("delivery-details", {
      title: "Delivery Details",
      del,
      role: req.session.role,
    });
  } else {
    return res.status(403).render("403", { title: "Forbidden" });
  }
});

// -------------------------------
// Update Delivery Status (archive on delivered)
// -------------------------------
router.post("/update-status/:id", requireAuth, async (req, res) => {
  try {
    const { id } = req.params;
    const { status } = req.body;

    const deliveries = await readDeliveries();
    const delivery = deliveries.find((d) => String(d.id) === String(id));

    if (!delivery) return res.status(404).render("404", { title: "Not Found" });

    const userRole = req.session.role;
    const username = req.session.user;

    // Drivers can only update their own deliveries
    if (userRole === "driver" && delivery.driver !== username) {
      return res.status(403).render("403", { title: "Forbidden" });
    }

    // Allowed transitions for drivers (prevents skipping/reverting)
    const validTransitions = {
      assigned: ["in transit"],
      "in transit": ["delivered"],
      delivered: [],
    };

    if (userRole === "driver") {
      const allowedNext = validTransitions[delivery.status] || [];
      if (!allowedNext.includes(status)) {
        return res.status(403).render("403", { title: "Invalid Status Change" });
      }
    } else {
      // Admins/managers can set any valid status
      const validStatuses = ["assigned", "in transit", "delivered"];
      if (!validStatuses.includes(status)) {
        return res.status(400).send("Invalid status");
      }
    }

    // Update timestamp and either update or archive
    const now = new Date().toISOString();
    delivery.status = status;
    delivery.updatedAt = now;

    if (status === "delivered") {
      // move to archive
      const archived = await readArchived();
      archived.push({ ...delivery, archivedAt: now });
      const remaining = deliveries.filter((d) => String(d.id) !== String(id));

      await writeArchived(archived);
      await writeDeliveries(remaining);
    } else {
      // just update active deliveries
      await writeDeliveries(deliveries);
    }

    return res.redirect("/deliveries");
  } catch (err) {
    console.error("Error updating status:", err);
    return res.status(500).render("500", { title: "Server error" });
  }
});

// -------------------------------
// Assign Delivery (GET/POST)
// -------------------------------
router.get("/assign-delivery", requireAuth, (req, res) => {
  if (!["admin", "manager"].includes(req.session.role))
    return res.status(403).render("403", { title: "Forbidden" });

  return res.render("assign-delivery", { title: "Assign Delivery" });
});

router.post("/assign-delivery", requireAuth, async (req, res) => {
  if (!["admin", "manager"].includes(req.session.role))
    return res.status(403).render("403", { title: "Forbidden" });

  const { recipient, address, item, driver } = req.body;
  if (!recipient || !address || !item) return res.redirect("/assign-delivery");

  const deliveries = await readDeliveries();
  const id = Date.now();

  deliveries.push({
    id,
    recipient,
    address,
    item,
    driver: driver || null,
    status: driver ? "assigned" : "unassigned",
    createdAt: new Date().toISOString(),
  });

  await writeDeliveries(deliveries);
  return res.redirect("/deliveries");
});

// -------------------------------
// Archived Deliveries (with filtering) - admin/manager only
// -------------------------------
router.get("/archived", requireAuth, async (req, res) => {
  if (!["admin", "manager"].includes(req.session.role))
    return res.status(403).render("403", { title: "Forbidden" });

  const archived = await readArchived();
  const query = req.query || {};

  const filtered = archived.filter((d) => {
    const idMatch = query.id ? String(d.id).includes(String(query.id).trim()) : true;
    const recipientMatch = query.recipient
      ? d.recipient.toLowerCase().includes(String(query.recipient).toLowerCase())
      : true;
    const addressMatch = query.address
      ? d.address.toLowerCase().includes(String(query.address).toLowerCase())
      : true;
    const itemMatch = query.item
      ? d.item.toLowerCase().includes(String(query.item).toLowerCase())
      : true;
    const driverMatch = query.driver
      ? d.driver && d.driver.toLowerCase().includes(String(query.driver).toLowerCase())
      : true;
    const statusMatch = query.status ? d.status === query.status : true;
    const dateMatch = query.date
      ? new Date(d.createdAt).toISOString().slice(0, 10) === query.date
      : true;

    return (
      idMatch &&
      recipientMatch &&
      addressMatch &&
      itemMatch &&
      driverMatch &&
      statusMatch &&
      dateMatch
    );
  });

  return res.render("archived", {
    title: "Archived Deliveries",
    deliveries: filtered,
    query,
  });
});

// -------------------------------
// Restore Archived Delivery (admin/manager)
// -------------------------------
router.post("/restore-archived/:id", requireAuth, async (req, res) => {
  if (!["admin", "manager"].includes(req.session.role))
    return res.status(403).render("403", { title: "Forbidden" });

  const { id } = req.params;
  const archived = await readArchived();
  const deliveries = await readDeliveries();

  const delivery = archived.find((d) => String(d.id) === String(id));
  if (!delivery) return res.status(404).render("404", { title: "Not Found" });

  const remaining = archived.filter((d) => String(d.id) !== String(id));

  delivery.status = "assigned";
  delete delivery.archivedAt;
  deliveries.push(delivery);

  await writeArchived(remaining);
  await writeDeliveries(deliveries);

  return res.redirect("/archived");
});

// -------------------------------
// Delete Archived Delivery (admin/manager)
// -------------------------------
router.post("/delete-archived/:id", requireAuth, async (req, res) => {
  if (!["admin", "manager"].includes(req.session.role))
    return res.status(403).render("403", { title: "Forbidden" });

  const { id } = req.params;
  const archived = await readArchived();
  const remaining = archived.filter((d) => String(d.id) !== String(id));
  await writeArchived(remaining);

  return res.redirect("/archived");
});

// -------------------------------
// Manage Users (Admin only)  <-- restored logic
// -------------------------------
router.get("/manage-users", requireAuth, requireAdmin, async (req, res) => {
  const users = await readUsers();
  return res.render("manage-users", { title: "Manage Users", users });
});

router.post("/manage-users", requireAuth, requireAdmin, async (req, res) => {
  const { username, role, password } = req.body;

  if (!username || !role || !password) return res.redirect("/manage-users");
  if (role === "admin") return res.redirect("/manage-users"); // prevent creating another admin

  const users = await readUsers();

  if (!users[username]) {
    const hashed = await hashPassword(password);
    users[username] = { role, password: hashed };
    await writeUsers(users);
  }

  return res.redirect("/manage-users");
});

// -------------------------------
// Delete User (Admin only)
// -------------------------------
router.post("/delete-user/:username", requireAuth, requireAdmin, async (req, res) => {
  const username = req.params.username;
  if (username === "admin") return res.redirect("/manage-users");

  const users = await readUsers();

  if (users[username]) {
    delete users[username];
    await writeUsers(users);
  }

  return res.redirect("/manage-users");
});

module.exports = router;
