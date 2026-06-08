/* ============================================================
   fees.js — student fees & receipts + admin fee clearance
   ============================================================ */
(function () {
  document.addEventListener("DOMContentLoaded", () => {
    const { $, $$, esc, toast, get, apiFetch, modal } = window.DL;
    const page = document.body.dataset.page;

    const inr = n => "₹" + Number(n || 0).toLocaleString("en-IN");
    const feeBadge = s => {
      const m = { PAID:"ok", PARTIAL:"pending", UNPAID:"bad" };
      return `<span class="badge ${m[s]||"info"}">${esc(s)}</span>`;
    };

    /* ---- STUDENT: fees ---- */
    if (page === "student-fees") {
      const DEMO = [{ feeId:1, session:"2024-2025", hostelFee:45000, messFee:36000,
        totalDue:81000, amountPaid:30000, status:"PARTIAL", dueDate:"2024-09-15" }];
      const wrap = $("#feeWrap");
      const render = rows => wrap.innerHTML = rows.map(f => {
        const bal = f.totalDue - f.amountPaid;
        return `<div class="panel">
          <div class="flex-between">
            <h3>Session ${esc(f.session)}</h3>${feeBadge(f.status)}
          </div>
          <div class="grid grid-2 mt">
            <div class="info-pill"><div class="k">Hostel Fee</div><div class="v">${inr(f.hostelFee)}</div></div>
            <div class="info-pill"><div class="k">Mess Fee</div><div class="v">${inr(f.messFee)}</div></div>
            <div class="info-pill"><div class="k">Total</div><div class="v">${inr(f.totalDue)}</div></div>
            <div class="info-pill"><div class="k">Balance Due</div><div class="v" style="color:${bal>0?"var(--danger)":"var(--success)"}">${inr(bal)}</div></div>
          </div>
          ${bal > 0 ? `<button class="btn btn-primary mt" data-pay="${f.feeId}" data-bal="${bal}">Pay ${inr(bal)} now</button>`
                    : `<div class="badge ok mt">Fully paid — thank you!</div>`}
        </div>`;
      }).join("");
      get("/fees/mine", DEMO).then(render);

      wrap.addEventListener("click", e => {
        const p = e.target.closest("[data-pay]"); if (!p) return;
        modal("Pay Hostel & Mess Fee", `
          <div class="field"><label>Amount</label><input class="input" id="payAmt" type="number" value="${p.dataset.bal}"></div>
          <div class="field"><label>Method</label>
            <select class="input" id="payMethod"><option>UPI</option><option>CARD</option><option>NETBANKING</option></select></div>`,
          (bk) => {
            apiFetch("/fees/pay", { method:"POST",
              body:{ feeId:+p.dataset.pay, amount:+$("#payAmt",bk).value, method:$("#payMethod",bk).value } })
              .then(r => { toast("Payment successful · " + (r.receiptNo||""), "ok");
                get("/fees/mine", DEMO).then(render); })
              .catch(err => toast(err.message, "err"));
          }, "Pay now");
      });
    }

    /* ---- STUDENT: receipts ---- */
    if (page === "student-receipts") {
      const DEMO = [{ receiptNo:"RCPT-2024-0001", amount:30000, method:"UPI", paidAt:"2024-09-10", session:"2024-2025" }];
      const wrap = $("#receiptWrap");
      get("/fees/receipts", DEMO).then(rows => wrap.innerHTML = rows.length ? rows.map(r => `
        <div class="row-item">
          <div class="ri-main">
            <div class="avatar">₹</div>
            <div><div class="ri-title">${esc(r.receiptNo)}</div>
              <div class="ri-sub">${inr(r.amount)} · ${esc(r.method)} · ${esc(r.paidAt)}</div></div>
          </div>
          <button class="btn btn-sm" data-dl="${r.receiptNo}">Download</button>
        </div>`).join("") : `<div class="empty">No receipts yet.</div>`);

      wrap.addEventListener("click", e => {
        const d = e.target.closest("[data-dl]"); if (!d) return;
        // Client-side printable receipt (works offline)
        const w = window.open("", "_blank");
        w.document.write(`<pre style="font-family:monospace;padding:30px;font-size:14px">
   dorm_LINK · Hostel & Mess Fee Receipt
   ---------------------------------------
   Receipt No : ${d.dataset.dl}
   Status     : PAID
   Generated  : ${new Date().toLocaleString()}
   ---------------------------------------
   Thank you for your payment.
        </pre>`);
        w.print();
      });
    }

    /* ---- ADMIN: fee clearance ---- */
    if (page === "admin-fee-clearance") {
      const DEMO = [
        { studentName:"Ankita Das", rollNo:"USTM2024CS002", session:"2024-2025", totalDue:96000, amountPaid:50000, status:"PARTIAL", studentId:2 },
        { studentName:"Rahul Nath", rollNo:"USTM2024EC003", session:"2024-2025", totalDue:74000, amountPaid:0, status:"UNPAID", studentId:3 },
        { studentName:"Priya Sharma", rollNo:"USTM2024BB004", session:"2024-2025", totalDue:81000, amountPaid:0, status:"UNPAID", studentId:4 }
      ];
      const tbody = $("#feeClearBody");
      get("/fees/dues", DEMO).then(rows => tbody.innerHTML = rows.length ? rows.map(f => `
        <tr>
          <td><b>${esc(f.studentName)}</b><br><small>${esc(f.rollNo)}</small></td>
          <td>${esc(f.session)}</td>
          <td>${inr(f.totalDue)}</td>
          <td>${inr(f.totalDue - f.amountPaid)}</td>
          <td>${feeBadge(f.status)}</td>
          <td><button class="btn btn-sm" data-remind="${f.studentId}">Send reminder</button></td>
        </tr>`).join("") : `<tr><td colspan="6"><div class="empty">All dues cleared.</div></td></tr>`);

      tbody.addEventListener("click", e => {
        const r = e.target.closest("[data-remind]"); if (!r) return;
        apiFetch("/fees/remind/" + r.dataset.remind, { method:"POST" })
          .then(() => toast("Reminder sent", "ok"))
          .catch(() => toast("Reminder queued (demo)", "ok"));
      });
    }
  });
})();
