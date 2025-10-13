import express from "express";
import { ensureAuthenticated, ensureRole } from "../utils/authMiddleware.js";
import { readData, writeData, createDelivery } from "../utils/dataHandler.js";

const router = express.Router();

// Protect all consumer routes
router.use(ensureAuthenticated);
router.use(ensureRole("consumer"));

/* =============================
   🟢 Consumer Dashboard
   ============================= */
router.get("/dashboard", async (req, res) => {
  try {
    const deliveries = (await readData("deliveries.json")) || [];
    const myDeliveries = deliveries.filter(
      (d) => d.consumer_email === req.session.user.email
    );

    res.render("consumerDashboard", {
      title: "My Dashboard",
      user: req.session.user,
      deliveries: myDeliveries,
    });
  } catch (error) {
    console.error("❌ Error loading dashboard:", error);
    res.status(500).send("Error loading dashboard.");
  }
});

/* =============================
   🟢 Show Delivery Request Form
   ============================= */
router.get("/request-form", (req, res) => {
  res.render("consumerRequestForm", { user: req.session.user });
});

/* =============================
   🟢 Create a New Delivery Request
   ============================= */
router.post("/request", async (req, res) => {
  try {
    const { pickupLocation, dropoffLocation, packageDetails } = req.body;

    if (!pickupLocation || !dropoffLocation || !packageDetails) {
      return res.status(400).send("All fields are required.");
    }

    // ✅ Use standardized structure
    await createDelivery({
      consumerEmail: req.session.user.email,
      packageDetails,
      pickupLocation,
      dropoffLocation,
    });

    console.log(`✅ New delivery request added by ${req.session.user.username}`);
    res.redirect("/consumer/deliveries");
  } catch (error) {
    console.error("❌ Error creating delivery:", error);
    res.status(500).send("Error creating delivery request.");
  }
});

/* =============================
   🟢 View All Deliveries
   ============================= */
router.get("/deliveries", async (req, res) => {
  try {
    const deliveries = (await readData("deliveries.json")) || [];
    const myDeliveries = deliveries.filter(
      (d) => d.consumer_email === req.session.user.email
    );

    res.render("consumerDeliveries", {
      title: "My Deliveries",
      deliveries: myDeliveries,
      user: req.session.user,
    });
  } catch (error) {
    console.error("❌ Error fetching deliveries:", error);
    res.status(500).send("Error fetching deliveries.");
  }
});

/* =============================
   🟢 View a Specific Delivery
   ============================= */
router.get("/deliveries/:id", async (req, res) => {
  try {
    const deliveries = (await readData("deliveries.json")) || [];
    const delivery = deliveries.find((d) => d.id === req.params.id);

    if (!delivery || delivery.consumer_email !== req.session.user.email) {
      return res.status(404).send("Delivery not found or unauthorized.");
    }

    // ✅ Normalize field names for the template
    const formattedDelivery = {
      id: delivery.id,
      pickupLocation: delivery.pickup_address,
      dropoffLocation: delivery.delivery_address,
      packageDetails: delivery.packageDetails,
      status: delivery.status,
      assignedDriver: delivery.assigned_to || "Not yet assigned",
      createdAt: new Date(delivery.created_at).toLocaleString(),
    };

    res.render("consumerDeliveryDetails", {
      delivery: formattedDelivery,
      user: req.session.user,
    });
  } catch (error) {
    console.error("❌ Error fetching delivery details:", error);
    res.status(500).send("Error fetching delivery details.");
  }
});


/* =============================
   🟢 Cancel a Pending Delivery
   ============================= */
router.post("/deliveries/:id/cancel", async (req, res) => {
  try {
    const deliveries = (await readData("deliveries.json")) || [];
    const deliveryIndex = deliveries.findIndex((d) => d.id === req.params.id);

    if (
      deliveryIndex === -1 ||
      deliveries[deliveryIndex].consumer_email !== req.session.user.email
    ) {
      return res.status(404).send("Delivery not found or unauthorized.");
    }

    if (deliveries[deliveryIndex].status !== "pending") {
      return res.status(400).send("You can only cancel pending deliveries.");
    }

    deliveries[deliveryIndex].status = "cancelled";
    deliveries[deliveryIndex].cancelledAt = new Date().toISOString();

    await writeData("deliveries.json", deliveries);

    res.redirect("/consumer/deliveries");
  } catch (error) {
    console.error("❌ Error cancelling delivery:", error);
    res.status(500).send("Error cancelling delivery.");
  }
});

export default router;
