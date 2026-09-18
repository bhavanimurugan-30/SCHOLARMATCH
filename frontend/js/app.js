/* =========================================================
   ScholarMatch — Shared App Shell Logic
   Section-switching SPA-per-role pattern: each role's HTML
   file loads all its <section data-view="..."> blocks once;
   the sidebar swaps which one is visible. This keeps file
   count low while still giving each screen its own URL hash
   for deep-linking (e.g. student.html#documents).
   ========================================================= */

function smInitShell(){
    // Sidebar nav click -> show matching view
    document.querySelectorAll(".nav-item[data-view]").forEach(item=>{
        item.addEventListener("click", ()=>{
            const view = item.getAttribute("data-view");
            smShowView(view);
            history.replaceState(null, "", "#" + view);
            document.querySelectorAll(".sidebar").forEach(s=>s.classList.remove("open"));
        });
    });

    // Hamburger (mobile)
    const burger = document.querySelector(".hamburger");
    if (burger){
        burger.addEventListener("click", ()=>{
            document.querySelector(".sidebar").classList.toggle("open");
        });
    }

    // Modal close on backdrop click
    document.querySelectorAll(".modal-overlay").forEach(ov=>{
        ov.addEventListener("click", (e)=>{ if(e.target === ov) smCloseModal(ov.id); });
    });

    // Initial view from hash
    const initial = (location.hash || "").replace("#","") || document.querySelector(".nav-item[data-view]").dataset.view;
    smShowView(initial);
}

function smShowView(view){
    if (view !== "details" && typeof smTeardownSectionNav === "function") smTeardownSectionNav();
    document.querySelectorAll(".view").forEach(v=> v.style.display = "none");
    const target = document.getElementById("view-" + view);
    if (target) target.style.display = "block";

    document.querySelectorAll(".nav-item[data-view]").forEach(item=>{
        item.classList.toggle("active", item.getAttribute("data-view") === view);
    });

    const titleEl = document.getElementById("page-title");
    const clicked = document.querySelector('.nav-item[data-view="'+view+'"] .label');
    if (titleEl && clicked) titleEl.textContent = clicked.textContent;
}

function smOpenModal(id){
    document.getElementById(id).classList.add("open");
}

function smCloseModal(id){
    document.getElementById(id).classList.remove("open");
}


/* =========================================================
   HTML Escape Helper
   Prevents HTML injection when dynamic backend data is
   rendered using innerHTML/template literals.
   ========================================================= */

function smEscapeHtml(value){
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


function smToast(message, type){
    const stack = document.getElementById("toast-stack");
    if (!stack) return;
    const el = document.createElement("div");
    el.className = "toast " + (type || "");
    el.textContent = message;
    stack.appendChild(el);
    setTimeout(()=>{
        el.style.opacity = "0";
        el.style.transition="opacity .3s";
        setTimeout(()=>el.remove(), 300);
    }, 2800);
}


/* Renders an SVG progress ring for match percentage into a container */
function smRenderMatchRing(container, pct, small){
    const size = small ? 44 : 58;
    const r = size/2 - 5;
    const c = 2 * Math.PI * r;
    const offset = c - (pct/100) * c;
    container.classList.add("match-ring");
    if (small) container.classList.add("sm");

    container.innerHTML = `
    <svg viewBox="0 0 ${size} ${size}">
      <defs>
        <linearGradient id="ringGrad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#4F46E5"/>
          <stop offset="100%" stop-color="#06B6D4"/>
        </linearGradient>
      </defs>
      <circle class="bg" cx="${size/2}" cy="${size/2}" r="${r}"></circle>
      <circle class="fg" cx="${size/2}" cy="${size/2}" r="${r}"
        stroke-dasharray="${c}" stroke-dashoffset="${offset}"></circle>
    </svg>
    <div class="pct">${pct}%</div>
  `;
}


/* Simple canvas line chart (no external chart lib needed) */
function smDrawLineChart(canvas, labels, values, color){
    const ctx = canvas.getContext("2d");
    const w = canvas.width = canvas.clientWidth * 2;
    const h = canvas.height = canvas.clientHeight * 2;
    ctx.scale(1,1);
    ctx.clearRect(0,0,w,h);
    const pad = 40 * 2;
    const max = Math.max(...values) * 1.15;
    const stepX = (w - pad*1.2) / (values.length - 1);

    // grid lines
    ctx.strokeStyle = "#EEF0FB";
    ctx.lineWidth = 2;

    for(let i=0;i<=4;i++){
        const y = pad*0.4 + i*( (h-pad) /4 );
        ctx.beginPath();
        ctx.moveTo(pad*0.6,y);
        ctx.lineTo(w-20,y);
        ctx.stroke();
    }

    // area
    ctx.beginPath();

    values.forEach((v,i)=>{
        const x = pad*0.6 + i*stepX;
        const y = h - pad*0.6 - (v/max)*(h-pad);

        if(i===0) ctx.moveTo(x,y);
        else ctx.lineTo(x,y);
    });

    const lastX = pad*0.6 + (values.length-1)*stepX;

    ctx.lineTo(lastX, h-pad*0.6);
    ctx.lineTo(pad*0.6, h-pad*0.6);
    ctx.closePath();

    const grad = ctx.createLinearGradient(0,0,0,h);
    grad.addColorStop(0, color + "33");
    grad.addColorStop(1, color + "02");

    ctx.fillStyle = grad;
    ctx.fill();

    // line
    ctx.beginPath();

    values.forEach((v,i)=>{
        const x = pad*0.6 + i*stepX;
        const y = h - pad*0.6 - (v/max)*(h-pad);

        if(i===0) ctx.moveTo(x,y);
        else ctx.lineTo(x,y);
    });

    ctx.strokeStyle = color;
    ctx.lineWidth = 5;
    ctx.lineJoin="round";
    ctx.stroke();

    // points
    values.forEach((v,i)=>{
        const x = pad*0.6 + i*stepX;
        const y = h - pad*0.6 - (v/max)*(h-pad);

        ctx.beginPath();
        ctx.arc(x,y,6,0,Math.PI*2);
        ctx.fillStyle="#fff";
        ctx.fill();

        ctx.lineWidth=4;
        ctx.strokeStyle=color;
        ctx.stroke();
    });

    // x labels
    ctx.fillStyle = "#8886A8";
    ctx.font = "22px Inter";
    ctx.textAlign="center";

    labels.forEach((l,i)=>{
        const x = pad*0.6 + i*stepX;
        ctx.fillText(l, x, h-8);
    });
}


/* Simple canvas donut chart */
function smDrawDonutChart(canvas, dataObj, colors){
    const ctx = canvas.getContext("2d");
    const w = canvas.width = canvas.clientWidth * 2;
    const h = canvas.height = canvas.clientHeight * 2;
    const cx = w/2,
        cy = h/2,
        rOuter = Math.min(w,h)/2 - 10,
        rInner = rOuter*0.62;

    const entries = Object.entries(dataObj);
    const total = entries.reduce((s,[,v])=>s+v,0);

    let start = -Math.PI/2;

    entries.forEach(([label,val],i)=>{
        const angle = (val/total) * Math.PI*2;

        ctx.beginPath();
        ctx.moveTo(cx,cy);
        ctx.arc(cx,cy,rOuter,start,start+angle);
        ctx.closePath();

        ctx.fillStyle = colors[i % colors.length];
        ctx.fill();

        start += angle;
    });

    ctx.beginPath();
    ctx.arc(cx,cy,rInner,0,Math.PI*2);
    ctx.fillStyle="#fff";
    ctx.fill();
}


/* Basic login stub — swap body of this function for a real fetch() to
   POST /api/students/login (or /api/admins/login, /api/institutions/login)
   against the Spring Boot backend. */

function smMockLogin(role, redirectTo){
    smToast("Signing in…");
    setTimeout(()=>{
        window.location.href = redirectTo;
    }, 500);
}

function smLogout(redirectTo){
    smClearSession();
    window.location.href = redirectTo;
}