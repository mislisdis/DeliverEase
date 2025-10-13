// utils/authMiddleware.js

// Protect routes that require any authenticated user
export function ensureAuthenticated(req, res, next) {
  if (req.session && req.session.user) {
    return next();
  }
  return res.redirect("/login");
}

// Protect routes for specific role(s)
// usage: ensureRole("manager") or ensureRole("driver", "manager")
export function ensureRole(...allowedRoles) {
  return (req, res, next) => {
    if (!req.session || !req.session.user) {
      return res.redirect("/login");
    }
    const { role } = req.session.user;
    if (allowedRoles.includes(role)) {
      return next();
    }
    // unauthorized
    return res.status(403).send("Forbidden — insufficient permissions.");
  };
}
