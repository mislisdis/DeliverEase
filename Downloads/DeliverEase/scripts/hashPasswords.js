// utils/hashPasswords.js
import fs from "fs/promises";
import bcrypt from "bcrypt";
import path from "path";
import readline from "readline";

const dataPath = path.resolve("./data/users.json");
const SALT_ROUNDS = 10;

// Helper to prompt in terminal
function ask(question) {
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
  });
  return new Promise((resolve) =>
    rl.question(question, (ans) => {
      rl.close();
      resolve(ans.trim());
    })
  );
}

async function hashPasswordsAndAddUsers() {
  try {
    // 1️⃣ Read existing users
    const data = await fs.readFile(dataPath, "utf-8");
    const users = JSON.parse(data);

    // 2️⃣ Hash existing plaintext passwords
    let updated = false;
    for (let user of users) {
      if (user.password_hash && !user.password_hash.startsWith("$2b$")) {
        console.log(`Hashing password for ${user.name}...`);
        user.password_hash = await bcrypt.hash(user.password_hash, SALT_ROUNDS);
        updated = true;
      }
    }
    if (updated) console.log("✅ Existing plaintext passwords hashed!");

    // 3️⃣ Loop to add multiple users
    let addMore = true;
    while (addMore) {
      const addUserAnswer = (await ask("Do you want to add a new user? (y/n): ")).toLowerCase();
      if (addUserAnswer !== "y") break;

      const name = await ask("Name: ");
      let email = await ask("Email: ");

      // ❌ Check for duplicate email
      while (users.some(u => u.email.toLowerCase() === email.toLowerCase())) {
        console.log("⚠️ This email already exists. Please enter a different email.");
        email = await ask("Email: ");
      }

      const role = await ask("Role (manager/driver/consumer): ");
      const password = await ask("Password: ");

      const hash = await bcrypt.hash(password, SALT_ROUNDS);

      // Generate unique ID
      const maxId = users.reduce((max, u) => {
        const num = parseInt(u.id.split("_")[1], 10);
        return num > max ? num : max;
      }, 0);

      const newUser = {
        id: `u_${String(maxId + 1).padStart(3, "0")}`,
        name,
        email,
        password_hash: hash,
        role,
        created_at: new Date().toISOString(),
      };

      users.push(newUser);
      console.log(`✅ User ${name} (${role}) added successfully!`);
    }

    // 4️⃣ Save users.json
    await fs.writeFile(dataPath, JSON.stringify(users, null, 2));
    console.log("✅ users.json updated successfully!");

  } catch (err) {
    console.error("❌ Error:", err);
  }
}

hashPasswordsAndAddUsers();
