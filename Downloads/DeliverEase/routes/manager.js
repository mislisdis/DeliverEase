// routes/manager.js
import express from "express";
import { readData, writeData } from "../utils/dataHandler.js";
import { ensureAuthenticated, ensureRole } from "../utils/authMiddleware.js";

const router = express.Router();

// Protect all routes under /manager
router.use(ensureAuthenticated);
router.use(ensureRole("manager"));

const deliveriesFile = "deliveries.json";
const usersFile = "users.json";

/* =============================
   🟢 Manager Dashboard
   ============================= */
router.get("/dashboard", async (req, res) => {
  try {
    const deliveries = await readData(deliveriesFile);
    const users = await readData(usersFile);

    // Filter drivers for assignment dropdown
    const drivers = users.filter((u) => u.role === "driver");

    // Sort deliveries by creation time (most recent first)
    const sortedDeliveries = deliveries.sort(
      (a, b) => new Date(b.created_at) - new Date(a.created_at)
    );

    res.render("manager_dashboard", {
      title: "Manager Dashboard",
      user: req.session.user,
      deliveries: sortedDeliveries,
      drivers,
    });
  } catch (err) {
    console.error("❌ Error loading manager dashboard:", err);
    res.status(500).send("Error loading dashboard");
  }
});

/* =============================
   🟢 Assign delivery to driver
   ============================= */
router.post("/assign", async (req, res) => {
  try {
    const { deliveryId, driverId } = req.body;

    if (!deliveryId || !driverId) {
      return res.status(400).send("Missing delivery or driver ID.");
    }

    const deliveries = await readData(deliveriesFile);
    const users = await readData(usersFile);

    const deliveryIndex = deliveries.findIndex((d) => d.id === deliveryId);
    const driver = users.find((u) => u.id === driverId);

    if (deliveryIndex === -1) {
      console.warn(`⚠️ Delivery not found for ID: ${deliveryId}`);
      return res.status(404).send("Delivery not found.");
    }

    if (!driver) {
      console.warn(`⚠️ Driver not found for ID: ${driverId}`);
      return res.status(404).send("Driver not found.");
    }

    // ✅ Update delivery info (using email for driver lookup consistency)
    deliveries[deliveryIndex].assigned_to = driver.email;
    deliveries[deliveryIndex].status = "assigned";
    deliveries[deliveryIndex].assigned_by = req.session.user.username;
    deliveries[deliveryIndex].assigned_at = new Date().toISOString();

    await writeData(deliveriesFile, deliveries);

    console.log(`✅ Delivery ${deliveryId} assigned to ${driver.email}`);
    res.redirect("/manager/dashboard");
  } catch (err) {
    console.error("❌ Error assigning delivery:", err);
    res.status(500).send("Error assigning delivery");
  }
});

export default router;
