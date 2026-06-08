/* ============================================================
   qr.js — Entry/Exit QR logging (student panel)
   ============================================================ */
(function () {
  document.addEventListener("DOMContentLoaded", () => {
    const { $, $$, esc, toast, get, apiFetch, getSession } = window.DL;
    if (document.body.dataset.page !== "student-entry-exit-log") return;

    const session = getSession() || {};
    const DEMO_LOGS = [
      { logId: 3, type: "ENTRY", timestamp: "2025-03-02 19:42", gate: "Block A — Main Gate" },
      { logId: 2, type: "EXIT",  timestamp: "2025-03-02 08:10", gate: "Block A — Main Gate" },
      { logId: 1, type: "ENTRY", timestamp: "2025-03-01 21:05", gate: "Block A — Main Gate" }
    ];

    const qrStage = $("#qrStage");
    if (qrStage) {
      const encoded = location.origin + location.pathname + "?gate=BLOCK-A-MAIN";
      qrStage.innerHTML = `
        <div class="qr-frame" title="${esc(encoded)}">${qrGrid()}</div>
        <div class="text-soft" style="font-size:.85rem;max-width:320px">
          Scan the QR pasted on your hostel entrance, or use the verification
          panel below to log your in-time / out-time.
        </div>`;
    }

    const verifyBox = $("#verifyBox");
    const logBox = $("#logActions");
    const rollInput = $("#rollInput");

    $("#verifyBtn")?.addEventListener("click", () => {
      const roll = (rollInput.value || "").trim();
      if (!roll) return toast("Enter your registered roll number", "err");
      const details = {
        rollNo: roll,
        fullName: session.fullName || "Student",
        hostel: session.hostel || "Brahmaputra House (A)",
        room: session.room || "A-101",
        gate: "Block A — Main Gate",
        time: new Date().toLocaleString("en-IN", { hour12: true })
      };
      verifyBox.innerHTML = `
        <div class="nm-well" style="margin-top:14px">
          <div class="ri-title" style="margin-bottom:8px">Verify your details</div>
          ${row("Roll No.", details.rollNo)}
          ${row("Name", details.fullName)}
          ${row("Hostel", details.hostel)}
          ${row("Room", details.room)}
          ${row("Gate", details.gate)}
          ${row("Time", details.time)}
        </div>`;
      logBox.style.display = "flex";
      logBox.dataset.roll = roll;
    });

    function logEvent(type) {
      const roll = logBox.dataset.roll;
      if (!roll) return;
      apiFetch("/logs", { method: "POST", body: { rollNo: roll, type, gate: "BLOCK-A-MAIN" } })
        .then(() => toast(type === "ENTRY" ? "Entry logged. Welcome back!" : "Exit logged. Stay safe!", "ok"))
        .catch(() => toast("Backend offline — " + type + " recorded in preview only", ""))
        .finally(loadLogs);
    }
    $("#logEntryBtn")?.addEventListener("click", () => logEvent("ENTRY"));
    $("#logExitBtn") ?.addEventListener("click", () => logEvent("EXIT"));

    const logList = $("#logList");
    function loadLogs() {
      get("/logs/mine", DEMO_LOGS).then(raw => {
        // API may return array of JSON strings — parse each if needed
        const rows = Array.isArray(raw) ? raw.map(l => {
          if (typeof l === "string") { try { return JSON.parse(l); } catch(e) { return {}; } }
          return l;
        }) : [];
        logList.innerHTML = rows.length ? rows.map(l => `
          <div class="row-item">
            <div class="ri-main">
              <div class="avatar" style="background:${l.type === "ENTRY" ? "var(--ok-bg,#d6f5e3)" : "var(--bad-bg,#ffe0e3)"}">
                ${l.type === "ENTRY" ? "↓" : "↑"}
              </div>
              <div>
                <div class="ri-title">${esc(l.type || "")}</div>
                <div class="ri-sub">${esc(l.gate || "")}</div>
              </div>
            </div>
            <div class="ri-sub">${esc(l.timestamp || l["at"] || "")}</div>
          </div>`).join("") : `<div class="empty">No entry/exit logs yet.</div>`;
      });
    }
    loadLogs();

    function row(k, v) {
      return `<div class="slot" style="display:flex;justify-content:space-between;padding:6px 0">
        <b style="min-width:90px;color:var(--ink-soft)">${esc(k)}</b><span>${esc(v)}</span></div>`;
    }
    function qrGrid() {
      let cells = "";
      const n = 11;
      for (let i = 0; i < n * n; i++) {
        const on = ((i * 37 + (i % n) * 13 + Math.floor(i / n) * 7) % 5) < 2;
        cells += `<i style="background:${on ? "var(--ink)" : "transparent"}"></i>`;
      }
      return `<div class="qr-grid" style="display:grid;grid-template-columns:repeat(${n},1fr);
        width:160px;height:160px;gap:2px">${cells}</div>`;
    }
  });
})();
