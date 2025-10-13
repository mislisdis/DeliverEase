import fs from "fs/promises";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const dataDir = path.join(__dirname, "../data");

const getFilePath = (file) => path.join(dataDir, file);

// --- Read JSON data ---
export async function readData(file) {
  try {
    const filePath = getFilePath(file);
    const data = await fs.readFile(filePath, "utf8");
    return JSON.parse(data);
  } catch (err) {
    console.error(`❌ Error reading ${file}:`, err.message);
    return [];
  }
}

// --- Write JSON data ---
export async function writeData(file, data) {
  try {
    const filePath = getFilePath(file);
    await fs.writeFile(filePath, JSON.stringify(data, null, 2), "utf8");
    console.log(`✅ Data written to ${file}`);
  } catch (err) {
    console.error(`❌ Error writing ${file}:`, err.message);
  }
}

// --- Generate Unique IDs ---
export function generateId(prefix, length = 4) {
  const random = Math.floor(Math.random() * Math.pow(10, length));
  return `${prefix}_${String(random).padStart(length, "0")}`;
}

// --- Find record by ID ---
export async function findById(file, id) {
  const data = await readData(file);
  return data.find((item) => item.id === id) || null;
}

// --- Update record by ID ---
export async function updateById(file, id, updatedFields) {
  const data = await readData(file);
  const index = data.findIndex((item) => item.id === id);

  if (index === -1) {
    console.error(`❌ Record with ID ${id} not found in ${file}`);
    return false;
  }

  data[index] = { ...data[index], ...updatedFields };
  await writeData(file, data);
  return true;
}

// --- Create New Delivery (Standardized Fields) ---
export async function createDelivery({ consumerEmail, packageDetails, pickupLocation, dropoffLocation }) {
  const deliveries = await readData("deliveries.json");

  const newDelivery = {
    id: generateId("d"),
    consumer_email: consumerEmail,
    packageDetails,
    pickup_address: pickupLocation,
    delivery_address: dropoffLocation,
    status: "pending",
    assigned_to: null,
    created_at: new Date().toISOString(),
  };

  deliveries.push(newDelivery);
  await writeData("deliveries.json", deliveries);
  return newDelivery;
}
