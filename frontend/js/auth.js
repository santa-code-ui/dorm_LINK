/* ============================================================
   auth.js — sign-in / sign-up / forgot / reset (Email OTP)
   ============================================================ */

// API base — change if you deploy under a different context path.
const API = window.DORMLINK_API || "/dormlink/api";

/* ---------- tiny helpers ---------- */
function toast(msg, type = "") {
  let t = document.getElementById("toast");
  if (!t) { t = document.createElement("div"); t.id = "toast"; document.body.appendChild(t); }
  t.className = type; t.textContent = msg;
  requestAnimationFrame(() => t.classList.add("show", type));
  clearTimeout(t._t); t._t = setTimeout(() => t.classList.remove("show"), 3200);
}
async function apiFetch(path, opts = {}) {
  const res = await fetch(API + path, {
    headers: { "Content-Type": "application/json" },
    ...opts,
    body: opts.body ? JSON.stringify(opts.body) : undefined
  });
  let data = {};
  try { data = await res.json(); } catch (_) {}
  if (!res.ok) throw new Error(data.message || ("Request failed (" + res.status + ")"));
  return data;
}
function saveSession(s) { localStorage.setItem("dl_session", JSON.stringify(s)); }

/* ---------- password eye toggle ---------- */
document.addEventListener("click", e => {
  const eye = e.target.closest(".pw-eye");
  if (!eye) return;
  const input = eye.parentElement.querySelector("input");
  const open = input.type === "password";
  input.type = open ? "text" : "password";
  eye.innerHTML = open ? EYE_OFF : EYE_ON;
});
const EYE_ON  = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z"/><circle cx="12" cy="12" r="3"/></svg>`;
const EYE_OFF = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20C5 20 1 13 1 13a18.5 18.5 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 7 11 7a18.5 18.5 0 0 1-2.16 3.19M1 1l22 22"/></svg>`;

/* ---------- role switch (sign-in) ---------- */
function initRoleSwitch() {
  const sw = document.querySelector(".role-switch");
  if (!sw) return;
  sw.addEventListener("click", e => {
    const r = e.target.closest(".role"); if (!r) return;
    sw.querySelectorAll(".role").forEach(x => x.classList.remove("active"));
    r.classList.add("active");
    sw.dataset.role = r.dataset.role;
  });
}

/* ---------- OTP input auto-advance ---------- */
function initOtpBoxes() {
  const boxes = [...document.querySelectorAll(".otp-box")];
  if (!boxes.length) return;
  boxes.forEach((b, i) => {
    b.addEventListener("input", () => {
      b.value = b.value.replace(/\D/g, "").slice(0, 1);
      if (b.value && boxes[i + 1]) boxes[i + 1].focus();
    });
    b.addEventListener("keydown", e => {
      if (e.key === "Backspace" && !b.value && boxes[i - 1]) boxes[i - 1].focus();
    });
  });
}
function readOtp() { return [...document.querySelectorAll(".otp-box")].map(b => b.value).join(""); }

/* ---------- SIGN IN ---------- */
function initSignin() {
  const form = document.getElementById("signinForm"); if (!form) return;
  initRoleSwitch();
  const btn = form.querySelector("button[type=submit]");
  form.addEventListener("submit", async e => {
    e.preventDefault();
    const role = document.querySelector(".role-switch")?.dataset.role || "STUDENT";
    const identifier = form.identifier.value.trim();
    const password = form.password.value;
    if (!identifier || !password) return toast("Enter your credentials", "err");
    btn.disabled = true;
    try {
      const data = await apiFetch("/auth/login", { method: "POST",
        body: { role, identifier, password } });
      saveSession(data);
      toast("Welcome back, " + (data.fullName || "") + "!", "ok");
      setTimeout(() => location.href = role === "ADMIN"
        ? "../admin/dashboard.html" : "../student/dashboard.html", 700);
    } catch (err) { toast(err.message, "err"); btn.disabled = false; }
  });
}

/* ---------- SIGN UP ---------- */
function initSignup() {
  const form = document.getElementById("signupForm"); if (!form) return;
  loadCoursesInto(form.course);
  const btn = form.querySelector("button[type=submit]");
  form.addEventListener("submit", async e => {
    e.preventDefault();
    const p = form.password.value, c = form.confirm.value;
    if (p.length < 6) return toast("Password must be at least 6 characters", "err");
    if (p !== c) return toast("Passwords do not match", "err");
    const payload = {
      rollNo: form.rollNo.value.trim(),
      fullName: form.fullName.value.trim(),
      email: form.email.value.trim(),
      phone: form.phone.value.trim(),
      dob: form.dob.value,
      gender: form.gender.value,
      address: form.address.value.trim(),
      courseId: form.course.value ? Number(form.course.value) : null,
      password: p
    };
    btn.disabled = true;
    try {
      await apiFetch("/auth/register", { method: "POST", body: payload });
      toast("Account created! Please sign in.", "ok");
      setTimeout(() => location.href = "signin.html", 900);
    } catch (err) { toast(err.message, "err"); btn.disabled = false; }
  });
}
async function loadCoursesInto(select) {
  if (!select) return;
  try {
    const list = await apiFetch("/students/courses");
    (list.data || []).forEach(c => {
      const o = document.createElement("option");
      o.value = c.courseId; o.textContent = c.courseName; select.appendChild(o);
    });
  } catch (_) { /* backend offline — leave default option */ }
}

/* ---------- FORGOT PASSWORD (request OTP) ---------- */
function initForgot() {
  const form = document.getElementById("forgotForm"); if (!form) return;
  const btn = form.querySelector("button[type=submit]");
  form.addEventListener("submit", async e => {
    e.preventDefault();
    const email = form.email.value.trim();
    const role = form.role.value;
    if (!email) return toast("Enter your registered email", "err");
    btn.disabled = true;
    try {
      await apiFetch("/auth/forgot", { method: "POST", body: { email, role } });
      sessionStorage.setItem("dl_reset_email", email);
      sessionStorage.setItem("dl_reset_role", role);
      toast("OTP sent to " + email, "ok");
      setTimeout(() => location.href = "reset_password.html", 900);
    } catch (err) { toast(err.message, "err"); btn.disabled = false; }
  });
}

/* ---------- RESET PASSWORD (verify OTP + set new) ---------- */
function initReset() {
  const form = document.getElementById("resetForm"); if (!form) return;
  initOtpBoxes();
  const email = sessionStorage.getItem("dl_reset_email");
  const role = sessionStorage.getItem("dl_reset_role") || "STUDENT";
  const lbl = document.getElementById("resetEmailLabel");
  if (lbl && email) lbl.textContent = email;
  const btn = form.querySelector("button[type=submit]");
  form.addEventListener("submit", async e => {
    e.preventDefault();
    const otp = readOtp();
    const p = form.password.value, c = form.confirm.value;
    if (otp.length !== 6) return toast("Enter the 6-digit OTP", "err");
    if (p.length < 6) return toast("Password must be at least 6 characters", "err");
    if (p !== c) return toast("Passwords do not match", "err");
    btn.disabled = true;
    try {
      await apiFetch("/auth/reset", { method: "POST",
        body: { email, role, otp, newPassword: p } });
      toast("Password updated! Please sign in.", "ok");
      sessionStorage.removeItem("dl_reset_email");
      setTimeout(() => location.href = "signin.html", 1000);
    } catch (err) { toast(err.message, "err"); btn.disabled = false; }
  });
}

/* ---------- boot ---------- */
document.addEventListener("DOMContentLoaded", () => {
  initSignin(); initSignup(); initForgot(); initReset();
});
