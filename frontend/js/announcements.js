/* ============================================================
   announcements.js — admin announcement management
   Post hostel/campus/university updates; list + toggle active.
   ============================================================ */
(function () {
  document.addEventListener("DOMContentLoaded", () => {
    const { $, esc, toast, get, apiFetch } = window.DL;
    if (document.body.dataset.page !== "admin-announcements") return;

    const DEMO = [
      { announcementId: 4, category: "HOSTEL", title: "Hostel Day 2025",
        body: "Cultural night on 20th March at the central lawn.", active: 1, createdAt: "2025-03-01" },
      { announcementId: 3, category: "UNIVERSITY", title: "Mess Fee Window Open",
        body: "Pay your 2024-25 mess fees before the 15th.", active: 1, createdAt: "2025-02-28" },
      { announcementId: 2, category: "CAMPUS", title: "New Library Hours",
        body: "Library now open till 11 PM during exams.", active: 1, createdAt: "2025-02-20" },
      { announcementId: 1, category: "HOSTEL", title: "Water Tank Cleaning",
        body: "Supply paused 10 AM–12 PM on Sunday in Block C.", active: 0, createdAt: "2025-02-10" }
    ];

    const list = $("#annList");
    const catBadge = c => `<span class="ann-cat">${esc(c)}</span>`;

    function render(rows) {
      list.innerHTML = rows.length ? rows.map(a => `
        <div class="row-item">
          <div class="ri-main">
            <div>
              <div class="ri-title">${esc(a.title)} ${catBadge(a.category)}
                ${a.active ? `<span class="badge ok">Live</span>` : `<span class="badge bad">Hidden</span>`}</div>
              <div class="ri-sub">${esc(a.body)}</div>
              <div class="ri-sub">Posted ${esc(a.createdAt)}</div>
            </div>
          </div>
          <button class="btn btn-ghost btn-sm" data-toggle="${a.announcementId}">
            ${a.active ? "Hide" : "Show"}
          </button>
        </div>`).join("") : `<div class="empty">No announcements posted yet.</div>`;

      list.querySelectorAll("[data-toggle]").forEach(b => b.onclick = () => {
        const id = b.dataset.toggle;
        apiFetch("/announcements/" + id + "/toggle", { method: "PUT" })
          .then(() => { toast("Visibility updated", "ok"); load(); })
          .catch(() => { toast("Backend offline — preview only", ""); });
      });
    }
    const load = () => get("/announcements?all=1", DEMO).then(render);
    load();

    const form = $("#annForm");
    form?.addEventListener("submit", e => {
      e.preventDefault();
      const payload = {
        category: form.category.value,
        title: form.title.value.trim(),
        body: form.body.value.trim()
      };
      if (!payload.title || !payload.body) return toast("Add a title and message", "err");
      const btn = form.querySelector("button[type=submit]"); btn.disabled = true;
      apiFetch("/announcements", { method: "POST", body: payload })
        .then(() => { toast("Announcement posted", "ok"); form.reset(); load(); })
        .catch(err => toast(err.message, "err"))
        .finally(() => btn.disabled = false);
    });
  });
})();
