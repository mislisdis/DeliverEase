// utils/helpers.js
// -----------------------------------------
// Centralized Handlebars helpers
// -----------------------------------------

module.exports = {
  // 🔁 Iterate object keys (for user lists, etc.)
  eachObject(context, options) {
    let result = "";
    for (const key in context) {
      if (Object.prototype.hasOwnProperty.call(context, key)) {
        result += options.fn({ key, ...context[key] });
      }
    }
    return result;
  },

  // ⚖️ Simple equality
  eq(a, b) {
    return a === b;
  },

  // ⚖️ Simple inequality (not equal)
  ne(a, b) {
    return a !== b;
  },

  // ⚖️ Block equality for conditional blocks
  ifEquals(a, b, options) {
    return a === b ? options.fn(this) : options.inverse(this);
  },

  // 🔍 Check if value exists in comma-separated list
  ifIn(value, list, options) {
    const arr = (list || "").toString().split(",").map((s) => s.trim());
    return arr.includes(String(value))
      ? options.fn(this)
      : options.inverse(this);
  },

  // 🔢 Greater-than comparison
  gt(a, b) {
    const na = Number(a);
    const nb = Number(b);
    if (!Number.isNaN(na) && !Number.isNaN(nb)) {
      return na > nb;
    }
    return a > b;
  },

  // 🕒 Date formatting (East Africa Time)
  formatDate(isoString) {
    if (!isoString) return "";
    const d = new Date(isoString);
    return d.toLocaleString("en-US", {
      dateStyle: "medium", // e.g. Oct 7, 2025
      timeStyle: "short",  // e.g. 2:17 PM
      timeZone: "Africa/Nairobi", // East Africa Time (EAT)
    });
  },
};
