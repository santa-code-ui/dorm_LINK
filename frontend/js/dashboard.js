/* ============================================================
   dashboard.js — app shell + shared helpers (window.DL)
   Loaded by every admin/student page. Exposes DL.* utilities
   used by room.js, complaints.js, fees.js, qr.js, announcements.js
   ============================================================ */
(function () {
  const API = window.DORMLINK_API || "/dormlink/api";

  /* ---------- session ---------- */
  function getSession() {
    try { return JSON.parse(localStorage.getItem("dl_session")) || null; }
    catch { return null; }
  }
  // Demo session so static preview (no backend) still renders nicely.
  function ensureSession(role) {
    let s = getSession();
    if (!s) {
      s = role === "ADMIN"
        ? { role: "ADMIN", adminId: 1, fullName: "System Administrator", username: "admin", demo: true }
        : { role: "STUDENT", studentId: 1, fullName: "Samboraa Borgohain", rollNo: "USTM2024CS001",
            room: "A-101", hostel: "Brahmaputra House (A)", department: "B.Tech CSE", demo: true };
      localStorage.setItem("dl_session", JSON.stringify(s));
    }
    return s;
  }

  /* ---------- helpers ---------- */
  const $  = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => [...root.querySelectorAll(sel)];
  const esc = s => String(s ?? "").replace(/[&<>"']/g, c =>
    ({ "&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#39;" }[c]));

  function toast(msg, type = "") {
    let t = $("#toast");
    if (!t) { t = document.createElement("div"); t.id = "toast"; document.body.appendChild(t); }
    t.className = type; t.textContent = msg;
    requestAnimationFrame(() => t.classList.add("show", type));
    clearTimeout(t._t); t._t = setTimeout(() => t.classList.remove("show"), 3200);
  }

  async function apiFetch(path, opts = {}) {
    const s = getSession();
    const res = await fetch(API + path, {
      headers: {
        "Content-Type": "application/json",
        ...(s ? { "X-Session-Role": s.role,
                  "X-Session-Id": String(s.studentId || s.adminId || "") } : {})
      },
      ...opts,
      body: opts.body ? JSON.stringify(opts.body) : undefined
    });
    let data = {}; try { data = await res.json(); } catch {}
    if (!res.ok) throw new Error(data.message || ("Request failed (" + res.status + ")"));
    return data;
  }

  /* get() with demo fallback: returns API rows or supplied demo rows on failure */
  async function get(path, demo) {
    try { const d = await apiFetch(path); return d.data ?? d; }
    catch (e) {
      if (demo !== undefined) { console.warn("[demo fallback]", path, e.message); return demo; }
      throw e;
    }
  }

  function modal(title, bodyHtml, onConfirm, confirmLabel = "Save") {
    let bk = $("#dl-modal");
    if (!bk) {
      bk = document.createElement("div"); bk.id = "dl-modal"; bk.className = "modal-backdrop";
      document.body.appendChild(bk);
    }
    bk.innerHTML = `<div class="modal glass">
        <h3>${esc(title)}</h3>
        <div class="modal-body">${bodyHtml}</div>
        <div class="modal-actions">
          <button class="btn btn-ghost" data-x>Cancel</button>
          <button class="btn btn-primary" data-ok>${esc(confirmLabel)}</button>
        </div></div>`;
    bk.classList.add("open");
    const close = () => bk.classList.remove("open");
    bk.querySelector("[data-x]").onclick = close;
    bk.onclick = e => { if (e.target === bk) close(); };
    bk.querySelector("[data-ok]").onclick = () => { if (onConfirm(bk) !== false) close(); };
    return bk;
  }

  /* ---------- shell wiring ---------- */
  function buildShell() {
    const body = document.body;
    const role = body.dataset.role || "STUDENT";
    const session = ensureSession(role);

    // topbar identity
    const who = $(".topbar .who");
    if (who) {
      who.innerHTML = `<b>${esc(session.fullName)}</b><br><small>${esc(
        role === "ADMIN" ? (session.username || "admin") : (session.rollNo || ""))}</small>`;
    }
    const av = $(".topbar .avatar");
    if (av) av.textContent = (session.fullName || "U").trim().charAt(0).toUpperCase();

    // active nav link by filename
    const here = location.pathname.split("/").pop();
    $$(".nav a").forEach(a => {
      if (a.getAttribute("href") === here) a.classList.add("active");
    });

    // logout
    $$("[data-logout]").forEach(b => b.onclick = e => {
      e.preventDefault(); localStorage.removeItem("dl_session"); location.href = "../auth/signin.html";
    });

    // mobile drawer
    const sb = $(".sidebar"), tog = $(".menu-toggle");
    if (tog && sb) {
      let scrim = $(".sidebar-scrim");
      if (!scrim) { scrim = document.createElement("div"); scrim.className = "sidebar-scrim"; document.body.appendChild(scrim); }
      const openD = () => { sb.classList.add("open"); scrim.classList.add("show"); };
      const closeD = () => { sb.classList.remove("open"); scrim.classList.remove("show"); };
      tog.onclick = openD; scrim.onclick = closeD;
    }

    // banner for demo mode
    if (session.demo) {
      const c = $(".content");
      if (c && !$(".demo-banner")) {
        const d = document.createElement("div");
        d.className = "demo-banner badge info";
        d.style.cssText = "margin:0 0 16px;padding:8px 14px;display:inline-flex";
        d.textContent = "Preview mode — backend offline, showing sample data";
        c.prepend(d);
      }
    }
    return session;
  }

  /* ---------- announcement ticker ---------- */
  async function initTicker() {
    const box = $(".ann-ticker"); if (!box) return;
    const demo = [
      { category: "HOSTEL", title: "Hostel Day 2025", body: "Cultural night on 20th March at the central lawn." },
      { category: "UNIVERSITY", title: "Mess Fee Window Open", body: "Pay your 2024-25 mess fees before the 15th." },
      { category: "CAMPUS", title: "New Library Hours", body: "Library now open till 11 PM during exams." }
    ];
    const list = await get("/announcements?active=1", demo);
    if (!list.length) { box.innerHTML = `<div class="empty">No announcements yet.</div>`; return; }
    box.innerHTML = list.map((a, i) =>
      `<div class="ann-item ${i === 0 ? "active" : ""}">
         <span class="ann-cat">${esc(a.category)}</span>
         <div style="font-weight:700;margin-top:4px">${esc(a.title)}</div>
         <div class="text-soft" style="font-size:.88rem">${esc(a.body)}</div>
       </div>`).join("");
    const items = $$(".ann-item", box); let i = 0;
    if (items.length > 1) setInterval(() => {
      items[i].classList.remove("active");
      i = (i + 1) % items.length;
      items[i].classList.add("active");
    }, 4000);
  }

  /* ---------- dashboard pages ---------- */
  async function initAdminDashboard() {
    if (!document.body.matches('[data-page="admin-dashboard"]')) return;
    const demo = { students: 5, rooms: 10, pendingRequests: 2, openComplaints: 2, feesDue: 251000, occupancy: 62 };
    const s = await get("/students/stats", demo);
    setStat("st-students", s.students);
    setStat("st-rooms", s.rooms);
    setStat("st-requests", s.pendingRequests);
    setStat("st-complaints", s.openComplaints);
    const occ = $("#st-occupancy"); if (occ) occ.textContent = (s.occupancy ?? 0) + "%";
    const due = $("#st-fees"); if (due) due.textContent = "₹" + (s.feesDue ?? 0).toLocaleString("en-IN");
  }
  function setStat(id, v) { const el = document.getElementById(id); if (el) el.textContent = v ?? "—"; }

  async function initStudentDashboard() {
    if (!document.body.matches('[data-page="student-dashboard"]')) return;
    const s = getSession();
    const pill = (k, v) => `<div class="info-pill"><div class="k">${k}</div><div class="v">${esc(v)}</div></div>`;
    const wrap = $("#quickInfo");
    if (wrap) wrap.innerHTML =
      pill("Room No.", s.room || "—") +
      pill("Hostel", s.hostel || "—") +
      pill("Roll No.", s.rollNo || "—") +
      pill("Department", s.department || "—");
  }

  /* ---------- expose ---------- */
  window.DL = { API, $, $$, esc, toast, apiFetch, get, modal, getSession, buildShell };

  document.addEventListener("DOMContentLoaded", () => {
    buildShell();
    initTicker();
    initAdminDashboard();
    initStudentDashboard();
  });
})();
