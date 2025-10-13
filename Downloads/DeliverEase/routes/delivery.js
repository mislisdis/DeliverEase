import express from "express";
import { readData, writeData } from "../utils/dataHandler.js";
import { ensureAuthenticated, ensureRole } from "../utils/authMiddleware.js";

const router = express.Router();
const deliveriesFile = "deliveries.json";

/* ================================
   🚚 DRIVER DASHBOARD — My Deliveries
================================ */
router.get("/dashboard", ensureAuthenticated, ensureRole("driver"), async (req, res) => {
  try {
    const deliveries = await readData(deliveriesFile);

    // ✅ Only show deliveries assigned to this logged-in driver
    const myDeliveries = deliveries.filter(
      (d) => d.assigned_to === req.session.user.email
    );

    res.render("delivery_dashboard", {
      title: "My Deliveries",
      user: req.session.user,
      deliveries: myDeliveries,
    });
  } catch (err) {
    console.error("❌ Error loading driver dashboard:", err);
    res.status(500).send("Error loading dashboard");
  }
});

/* ================================
   🟡 UPDATE DELIVERY STATUS
================================ */
router.post("/update-status", ensureAuthenticated, ensureRole("driver"), async (req, res) => {
  try {
    const { deliveryId, status } = req.body;
    const deliveries = await readData(deliveriesFile);

    const delivery = deliveries.find((d) => d.id === deliveryId);
    if (!delivery) {
      console.warn(`⚠️ Delivery ${deliveryId} not found.`);
      return res.status(404).send("Delivery not found");
    }

    // ✅ Ensure only assigned driver can update
    if (delivery.assigned_to !== req.session.user.email) {
      return res.status(403).send("You can only update your assigned deliveries.");
    }

    delivery.status = status;

    if (status === "picked") delivery.picked_at = new Date().toISOString();
    if (status === "in_transit") delivery.in_transit_at = new Date().toISOString();
    if (status === "delivered") delivery.delivered_at = new Date().toISOString();

    await writeData(deliveriesFile, deliveries);
    console.log(`✅ Delivery ${deliveryId} status updated to ${status}`);

    res.redirect("/delivery/dashboard");
  } catch (err) {
    console.error("❌ Error updating status:", err);
    res.status(500).send("Error updating status");
  }
});

/* ================================
   🟢 MARK DELIVERY AS ARRIVED
================================ */
router.post("/mark-arrived", ensureAuthenticated, ensureRole("driver"), async (req, res) => {
  try {
    const { deliveryId } = req.body;
    const deliveries = await readData(deliveriesFile);

    const index = deliveries.findIndex((d) => d.id === deliveryId);
    if (index === -1) {
      console.warn(`⚠️ Delivery ${deliveryId} not found.`);
      return res.status(404).send(`Delivery ${deliveryId} not found`);
    }

    // ✅ Ensure driver owns this delivery
    if (deliveries[index].assigned_to !== req.session.user.email) {
      return res.status(403).send("You can only mark your own deliveries as arrived.");
    }

    deliveries[index].status = "arrived";
    deliveries[index].arrived_at = new Date().toISOString();

    await writeData(deliveriesFile, deliveries);
    console.log(`🚚 Delivery ${deliveryId} marked as arrived.`);

    res.redirect("/delivery/dashboard");
  } catch (err) {
    console.error("❌ Error marking delivery as arrived:", err);
    res.status(500).send("Error marking delivery as arrived");
  }
});

export default router;
