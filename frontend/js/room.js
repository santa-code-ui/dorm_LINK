/* ============================================================
   room.js — rooms, room requests, hostel browsing, mess menu
   Dispatches on document.body[data-page].
   ============================================================ */
(function () {
  document.addEventListener("DOMContentLoaded", () => {
    const { $, $$, esc, toast, get, apiFetch, modal } = window.DL;
    const page = document.body.dataset.page;

    /* demo datasets (used when backend offline) ------------------- */
    const DEMO_HOSTELS = [
      { hostelId:1, hostelName:"Brahmaputra House", block:"A", gender:"MALE",  avgRating:4.3, available:2,
        description:"Riverside block with wide balconies and a quiet reading lounge.", coverImage:"" },
      { hostelId:2, hostelName:"Kamakhya House", block:"B", gender:"FEMALE", avgRating:4.6, available:2,
        description:"Central block close to the mess and library, 24x7 security.", coverImage:"" },
      { hostelId:3, hostelName:"Kaziranga House", block:"C", gender:"MALE",  avgRating:4.1, available:2,
        description:"Newest block with high-speed Wi-Fi and a rooftop court.", coverImage:"" },
      { hostelId:4, hostelName:"Majuli House", block:"D", gender:"FEMALE", avgRating:4.4, available:2,
        description:"Garden-facing rooms, dedicated study halls on every floor.", coverImage:"" }
    ];
    const DEMO_ROOMS = [
      { roomId:1, hostelName:"Brahmaputra House", block:"A", roomNo:"A-101", floor:1, roomType:"DOUBLE", capacity:2, occupied:1, rentAmount:45000, status:"AVAILABLE" },
      { roomId:2, hostelName:"Brahmaputra House", block:"A", roomNo:"A-102", floor:1, roomType:"DOUBLE", capacity:2, occupied:2, rentAmount:45000, status:"FULL" },
      { roomId:3, hostelName:"Brahmaputra House", block:"A", roomNo:"A-201", floor:2, roomType:"TRIPLE", capacity:3, occupied:0, rentAmount:38000, status:"AVAILABLE" },
      { roomId:5, hostelName:"Kamakhya House", block:"B", roomNo:"B-102", floor:1, roomType:"DOUBLE", capacity:2, occupied:1, rentAmount:45000, status:"AVAILABLE" },
      { roomId:7, hostelName:"Kaziranga House", block:"C", roomNo:"C-110", floor:1, roomType:"TRIPLE", capacity:3, occupied:1, rentAmount:38000, status:"AVAILABLE" }
    ];
    const DEMO_REQUESTS = [
      { requestId:1, studentName:"Imran Hussain", rollNo:"USTM2024CS005", roomNo:"A-201", hostelName:"Brahmaputra House", status:"PENDING", note:"Prefer ground floor.", requestedAt:"2025-03-01" },
      { requestId:2, studentName:"Imran Hussain", rollNo:"USTM2024CS005", roomNo:"D-101", hostelName:"Majuli House", status:"PENDING", note:"Alternate choice.", requestedAt:"2025-03-01" }
    ];
    const statusBadge = s => {
      const map = { AVAILABLE:"ok", FULL:"bad", MAINTENANCE:"pending",
                    PENDING:"pending", APPROVED:"ok", REJECTED:"bad" };
      return `<span class="badge ${map[s]||"info"}">${esc(s)}</span>`;
    };

    /* ---- ADMIN: rooms ---- */
    if (page === "admin-rooms") {
      const tbody = $("#roomsBody");
      const render = rows => tbody.innerHTML = rows.length ? rows.map(r => `
        <tr>
          <td><b>${esc(r.roomNo)}</b></td>
          <td>${esc(r.hostelName)} (${esc(r.block)})</td>
          <td>${esc(r.roomType)}</td>
          <td>${r.occupied}/${r.capacity}</td>
          <td>₹${Number(r.rentAmount).toLocaleString("en-IN")}</td>
          <td>${statusBadge(r.status)}</td>
          <td><button class="btn btn-sm" data-edit="${r.roomId}">Edit</button></td>
        </tr>`).join("") : `<tr><td colspan="7"><div class="empty">No rooms yet.</div></td></tr>`;
      get("/rooms", DEMO_ROOMS).then(render);

      $("#addRoomBtn")?.addEventListener("click", () => {
        modal("Add Room", `
          <div class="field"><label>Room No.</label><input class="input" id="m_no" placeholder="A-103"></div>
          <div class="grid grid-2">
            <div class="field"><label>Type</label>
              <select class="input" id="m_type"><option>SINGLE</option><option selected>DOUBLE</option><option>TRIPLE</option><option>QUAD</option></select></div>
            <div class="field"><label>Capacity</label><input class="input" id="m_cap" type="number" value="2"></div>
          </div>
          <div class="field"><label>Rent (₹)</label><input class="input" id="m_rent" type="number" value="45000"></div>`,
          (bk) => {
            const payload = { roomNo: $("#m_no", bk).value, roomType: $("#m_type", bk).value,
              capacity: +$("#m_cap", bk).value, rentAmount: +$("#m_rent", bk).value };
            if (!payload.roomNo) { toast("Room number required", "err"); return false; }
            apiFetch("/rooms", { method:"POST", body: payload })
              .then(() => { toast("Room added", "ok"); get("/rooms", DEMO_ROOMS).then(render); })
              .catch(e => toast(e.message, "err"));
          }, "Add Room");
      });
    }

    /* ---- ADMIN: room requests ---- */
    if (page === "admin-room-requests") {
      const wrap = $("#requestsWrap");
      const render = rows => wrap.innerHTML = rows.length ? rows.map(r => `
        <div class="row-item">
          <div class="ri-main">
            <div class="avatar">${esc(r.studentName.charAt(0))}</div>
            <div>
              <div class="ri-title">${esc(r.studentName)} · ${esc(r.rollNo)}</div>
              <div class="ri-sub">Wants ${esc(r.roomNo)} — ${esc(r.hostelName)} · ${esc(r.note||"")}</div>
            </div>
          </div>
          <div class="flex" style="align-items:center">
            ${statusBadge(r.status)}
            ${r.status === "PENDING" ? `
              <button class="btn btn-sm btn-primary" data-approve="${r.requestId}">Approve</button>
              <button class="btn btn-sm btn-danger" data-reject="${r.requestId}">Reject</button>` : ""}
          </div>
        </div>`).join("") : `<div class="empty">No room requests.</div>`;
      get("/rooms/requests", DEMO_REQUESTS).then(render);

      wrap.addEventListener("click", e => {
        const ap = e.target.closest("[data-approve]"), rj = e.target.closest("[data-reject]");
        const id = ap?.dataset.approve || rj?.dataset.reject; if (!id) return;
        const decision = ap ? "APPROVED" : "REJECTED";
        apiFetch("/rooms/requests/" + id, { method:"PUT", body:{ status: decision } })
          .then(() => { toast("Request " + decision.toLowerCase(), "ok");
            get("/rooms/requests", DEMO_REQUESTS).then(render); })
          .catch(err => toast(err.message, "err"));
      });
    }

    /* ---- STUDENT: book hostel ---- */
    if (page === "student-book-hostel") {
      const grid = $("#hostelCards");
      const render = rows => grid.innerHTML = rows.map(h => `
        <div class="hostel-card">
          <div class="thumb" style="background-image:url('${esc(h.coverImage||"")}')">
            <span class="rating">★ ${Number(h.avgRating).toFixed(1)}</span>
          </div>
          <div class="body">
            <h3>${esc(h.hostelName)} <span class="badge info">Block ${esc(h.block)}</span></h3>
            <div class="meta">${esc(h.gender)} · ${h.available ?? "?"} rooms available</div>
            <p class="text-soft" style="font-size:.88rem;min-height:38px">${esc(h.description)}</p>
            <button class="btn btn-primary" style="width:100%;margin-top:12px"
              data-view="${h.hostelId}">View rooms & request</button>
          </div>
        </div>`).join("");
      get("/rooms/hostels", DEMO_HOSTELS).then(render);

      grid.addEventListener("click", e => {
        const v = e.target.closest("[data-view]"); if (!v) return;
        openHostelRooms(+v.dataset.view);
      });
    }
    function openHostelRooms(hostelId) {
      get("/rooms?hostelId=" + hostelId, DEMO_ROOMS.filter((_,i)=>i<3)).then(rooms => {
        const html = rooms.map(r => `
          <div class="row-item" style="margin-bottom:10px">
            <div><b>${esc(r.roomNo)}</b> · ${esc(r.roomType)} · ${r.occupied}/${r.capacity}
              <div class="ri-sub">₹${Number(r.rentAmount).toLocaleString("en-IN")}/yr</div></div>
            ${r.status === "AVAILABLE"
              ? `<button class="btn btn-sm btn-primary" data-req="${r.roomId}">Request</button>`
              : statusBadge(r.status)}
          </div>`).join("");
        const bk = window.DL.modal("Available Rooms", html, () => true, "Close");
        bk.querySelectorAll("[data-req]").forEach(b => b.onclick = () => {
          window.DL.apiFetch("/rooms/requests", { method:"POST", body:{ roomId:+b.dataset.req } })
            .then(() => window.DL.toast("Request sent to admin for approval", "ok"))
            .catch(e => window.DL.toast(e.message, "err"));
        });
      });
    }

    /* ---- STUDENT: room details + rating ---- */
    if (page === "student-room-details") {
      const demo = { roomNo:"A-101", hostelName:"Brahmaputra House", block:"A", floor:1, roomType:"DOUBLE",
        capacity:2, rentAmount:45000, status:"AVAILABLE", hostelId:1,
        roommates:[{ fullName:"Samboraa Borgohain", rollNo:"USTM2024CS001" }] };
      get("/rooms/mine", demo).then(r => {
        const box = $("#roomDetail"); if (!box) return;
        if (!r || !r.roomNo) { box.innerHTML = `<div class="empty">No room allocated yet. Request one from <b>Book Hostel</b>.</div>`; return; }
        box.innerHTML = `
          <div class="grid grid-2">
            <div class="info-pill"><div class="k">Room</div><div class="v">${esc(r.roomNo)}</div></div>
            <div class="info-pill"><div class="k">Hostel</div><div class="v">${esc(r.hostelName)} (${esc(r.block)})</div></div>
            <div class="info-pill"><div class="k">Type</div><div class="v">${esc(r.roomType)}</div></div>
            <div class="info-pill"><div class="k">Rent</div><div class="v">₹${Number(r.rentAmount).toLocaleString("en-IN")}</div></div>
          </div>
          <h3 class="mt">Roommates</h3>
          ${(r.roommates||[]).map(m => `<div class="row-item"><div class="ri-main">
              <div class="avatar">${esc(m.fullName.charAt(0))}</div>
              <div><div class="ri-title">${esc(m.fullName)}</div><div class="ri-sub">${esc(m.rollNo)}</div></div>
            </div></div>`).join("") || `<div class="empty">No roommates yet.</div>`}
          <h3 class="mt">Rate your hostel</h3>
          <div class="stars" id="starInput" data-val="0">
            ${[1,2,3,4,5].map(n => `<span class="s" data-n="${n}">★</span>`).join("")}
          </div>
          <textarea class="input mt" id="review" placeholder="Share a quick review (optional)"></textarea>
          <button class="btn btn-primary mt" id="submitRating" data-hostel="${r.hostelId}">Submit rating</button>`;
        wireStars(box);
      });
    }
    function wireStars(box) {
      const wrap = box.querySelector("#starInput"); if (!wrap) return;
      const stars = [...wrap.querySelectorAll(".s")];
      const paint = v => stars.forEach((s,i) => s.classList.toggle("on", i < v));
      stars.forEach(s => {
        s.onmouseenter = () => paint(+s.dataset.n);
        s.onclick = () => { wrap.dataset.val = s.dataset.n; paint(+s.dataset.n); };
      });
      wrap.onmouseleave = () => paint(+wrap.dataset.val);
      box.querySelector("#submitRating").onclick = () => {
        const stars = +wrap.dataset.val;
        if (!stars) return window.DL.toast("Pick a star rating first", "err");
        window.DL.apiFetch("/rooms/rate", { method:"POST",
          body:{ hostelId:+box.querySelector("#submitRating").dataset.hostel,
                 stars, review: box.querySelector("#review").value } })
          .then(() => window.DL.toast("Thanks! Rating updated in real time.", "ok"))
          .catch(e => window.DL.toast(e.message, "err"));
      };
    }

    /* ---- STUDENT: mess menu ---- */
    if (page === "student-mess-menu") {
      const days = ["MON","TUE","WED","THU","FRI","SAT","SUN"];
      const dayName = { MON:"Monday",TUE:"Tuesday",WED:"Wednesday",THU:"Thursday",FRI:"Friday",SAT:"Saturday",SUN:"Sunday" };
      const today = days[(new Date().getDay()+6)%7];
      const DEMO_MENU = {
        MON:{breakfast:"Aloo Paratha, Curd, Tea",lunch:"Rice, Dal, Mixed Veg",snacks:"Samosa, Tea",dinner:"Roti, Paneer Butter Masala"},
        TUE:{breakfast:"Idli, Sambar, Coffee",lunch:"Rice, Rajma, Bhindi",snacks:"Pakora, Tea",dinner:"Roti, Egg Curry, Rice"},
        WED:{breakfast:"Poha, Banana, Tea",lunch:"Rice, Dal, Aloo Gobi",snacks:"Biscuits, Tea",dinner:"Roti, Chicken Curry"},
        THU:{breakfast:"Bread Omelette, Tea",lunch:"Rice, Sambar, Cabbage",snacks:"Maggi, Tea",dinner:"Roti, Mix Dal, Rice"},
        FRI:{breakfast:"Upma, Chutney",lunch:"Rice, Dal Fry, Fish Curry",snacks:"Veg Roll, Tea",dinner:"Roti, Aloo Matar"},
        SAT:{breakfast:"Puri, Sabji, Tea",lunch:"Veg Biryani, Raita",snacks:"Samosa, Tea",dinner:"Roti, Chana Masala"},
        SUN:{breakfast:"Chole Bhature",lunch:"Special Thali",snacks:"Ice Cream",dinner:"Fried Rice, Manchurian"}
      };
      get("/rooms/mess", DEMO_MENU).then(menu => {
        $("#messGrid").innerHTML = days.map(d => {
          const m = menu[d] || {};
          return `<div class="menu-day ${d===today?"today":""}">
            <h4>${dayName[d]} ${d===today?'<span class="badge ok">Today</span>':""}</h4>
            <div class="slot"><b>Breakfast</b><span>${esc(m.breakfast||"—")}</span></div>
            <div class="slot"><b>Lunch</b><span>${esc(m.lunch||"—")}</span></div>
            <div class="slot"><b>Snacks</b><span>${esc(m.snacks||"—")}</span></div>
            <div class="slot"><b>Dinner</b><span>${esc(m.dinner||"—")}</span></div>
          </div>`;
        }).join("");
      });
    }
  });
})();
