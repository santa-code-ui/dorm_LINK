/* ============================================================
   complaints.js — student complaint submission & tracking
   ============================================================ */
(function () {
  document.addEventListener("DOMContentLoaded", () => {
    const { $, esc, toast, get, apiFetch } = window.DL;
    if (document.body.dataset.page !== "student-complaints") return;

    const DEMO = [
      { complaintId:1, type:"ELECTRICITY", subject:"Tube light flickering", status:"OPEN",
        description:"The tube light in A-101 flickers every evening.", createdAt:"2025-03-01" },
      { complaintId:2, type:"WATER", subject:"Low pressure", status:"IN_PROGRESS",
        description:"Water pressure drops after 9 PM.", createdAt:"2025-02-27" }
    ];
    const badge = s => {
      const m = { OPEN:"pending", IN_PROGRESS:"info", RESOLVED:"ok", CLOSED:"bad" };
      return `<span class="badge ${m[s]||"info"}">${esc(s.replace("_"," "))}</span>`;
    };
    const list = $("#complaintList");
    const render = rows => list.innerHTML = rows.length ? rows.map(c => `
      <div class="row-item">
        <div class="ri-main">
          <div class="avatar">${esc(c.type.charAt(0))}</div>
          <div>
            <div class="ri-title">${esc(c.subject)} <span class="badge info">${esc(c.type)}</span></div>
            <div class="ri-sub">${esc(c.description)}</div>
            <div class="ri-sub">Filed ${esc(c.createdAt)}</div>
          </div>
        </div>
        ${badge(c.status)}
      </div>`).join("") : `<div class="empty">No complaints filed. Hopefully all is well!</div>`;

    const load = () => get("/complaints/mine", DEMO).then(render);
    load();

    const form = $("#complaintForm");
    form?.addEventListener("submit", e => {
      e.preventDefault();
      const payload = { type: form.type.value, subject: form.subject.value.trim(),
        description: form.description.value.trim() };
      if (!payload.subject || !payload.description) return toast("Fill in the complaint details", "err");
      const btn = form.querySelector("button[type=submit]"); btn.disabled = true;
      apiFetch("/complaints", { method:"POST", body: payload })
        .then(() => { toast("Complaint submitted", "ok"); form.reset(); load(); })
        .catch(err => toast(err.message, "err"))
        .finally(() => btn.disabled = false);
    });
  });
})();
