/* =========================================================
   ScholarMatch — Student Portal Logic
   Talks only to endpoints that exist in the Spring Boot
   controllers (see js/api.js for the exact paths).

   Key rules enforced here:
   - Saved and Applied are independent. Unsaving never clears
     an application; the applied date is set once and never
     re-sent to the backend.
   - Expired scholarships are hidden from the 5 category views
     and from Find Scholarship, but remain visible in Saved,
     My Applications and Recently Viewed.
   - Apply is disabled when the student is ineligible, the
     scholarship is expired, or a mandatory certificate is
     missing/expired (the backend decides via canApply).
   ========================================================= */

const SM_SESSION = smGetSession();

if (!SM_SESSION || SM_SESSION.role !== "student") {
    window.location.href = "student-login.html";
}

const SM_STUDENT_ID = SM_SESSION && SM_SESSION.profile
    ? (SM_SESSION.profile.studentId || SM_SESSION.profile.id)
    : null;

const SM_CATEGORIES = [
    { key: "WOMENS",  label: "Women's Scholarship",  view: "cat-womens",  icon: "♀" },
    { key: "COLLEGE", label: "College Scholarship",  view: "cat-college", icon: "🎓" },
    { key: "COMPANY", label: "Company Scholarship",  view: "cat-company", icon: "🏢" },
    { key: "STATE",   label: "State Scholarship",    view: "cat-state",   icon: "📍" },
    { key: "CENTRAL", label: "Central Scholarship",  view: "cat-central", icon: "🏛" }
];

const ST = {
    student: {},
    academic: {},
    financial: {},
    special: {},
    documents: [],
    certTypes: [],
    notifications: [],
    saved: [],          // bookmark rows with saved_at
    applied: [],        // bookmark rows with applied_at
    scholarshipsById: {},
    categoryCache: {},
    eligible: [],
    recentlyViewed: [],
    photoUrl: null,
    lastView: "home"
};

/* ---------------- small helpers ---------------- */

function smIsExpired(deadline) {
    if (!deadline) return false;
    const d = new Date(deadline);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return d < today;
}

function smDaysUntil(deadline) {
    if (!deadline) return null;
    const d = new Date(deadline);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return Math.ceil((d - today) / 86400000);
}

function smIsSaved(scholarshipId) {
    return ST.saved.some(b => b.scholarshipId === scholarshipId);
}

function smIsApplied(scholarshipId) {
    return ST.applied.some(b => b.scholarshipId === scholarshipId);
}

function smCacheScholarships(list) {
    (list || []).forEach(s => {
        if (s && s.scholarshipId != null) ST.scholarshipsById[s.scholarshipId] = s;
    });
}

async function smGetScholarship(id) {
    if (ST.scholarshipsById[id]) return ST.scholarshipsById[id];
    // trackView=false: viewing is only recorded from the "View" button.
    const res = await smApi.getScholarship(id, false);
    if (res && res.data) ST.scholarshipsById[id] = res.data;
    return res && res.data;
}

/* ---------------- user chip ---------------- */

function smInitials(name) {
    const clean = (name || "").trim();
    if (!clean) return "S";
    const parts = clean.split(/\s+/).filter(Boolean);
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
}

function smRenderUserChip() {
    const profile = (SM_SESSION && SM_SESSION.profile) || {};
    const fullName = ST.student.fullName || profile.fullName || "Student";
    const email = ST.student.email || profile.email || "";
    const initials = smInitials(fullName);
    const photo = ST.photoUrl;

    ["studentAvatar", "studentAvatarTop", "studentAvatarMenu"].forEach(id => {
        const el = document.getElementById(id);
        if (!el) return;
        if (photo) {
            el.innerHTML = `<img src="${photo}" alt="">`;
            el.classList.add("has-photo");
        } else {
            el.textContent = initials;
            el.classList.remove("has-photo");
        }
    });

    ["studentNameDisplay", "studentNameTop", "studentNameMenu"].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = fullName;
    });
    const emailEl = document.getElementById("studentEmailMenu");
    if (emailEl) emailEl.textContent = email;
}

/**
 * Loads the profile photo once per page load and caches the object URL.
 * The uploads folder is not publicly served, so the photo comes through the
 * authenticated /students/{id}/photo endpoint.
 */
async function smLoadPhotoUrl(force) {
    if (!ST.student.profilePhotoUrl) {
        ST.photoUrl = null;
        return null;
    }
    if (ST.photoUrl && !force) return ST.photoUrl;
    ST.photoUrl = await smApi.getStudentPhotoObjectUrl(SM_STUDENT_ID);
    return ST.photoUrl;
}

/* ---------------- dropdown menus ---------------- */

function smCloseAllMenus() {
    const n = document.getElementById("notifDropdown");
    const p = document.getElementById("profileDropdown");
    if (n) n.classList.remove("open");
    if (p) p.classList.remove("open");
}

function smToggleNotifications() {
    const dd = document.getElementById("notifDropdown");
    const p = document.getElementById("profileDropdown");
    if (p) p.classList.remove("open");
    if (dd) dd.classList.toggle("open");
}

function smToggleProfileMenu() {
    const dd = document.getElementById("profileDropdown");
    const n = document.getElementById("notifDropdown");
    if (n) n.classList.remove("open");
    if (dd) dd.classList.toggle("open");
}

document.addEventListener("click", (e) => {
    if (!e.target.closest(".notif-wrap")) smCloseAllMenus();
});

function smConfirmLogout() {
    smCloseAllMenus();
    smOpenModal("logoutModal");
}

/* ---------------- notifications ---------------- */

async function smLoadNotifications() {
    try {
        const res = await smApi.getStudentNotifications(SM_STUDENT_ID);
        ST.notifications = res.data || [];
    } catch (e) {
        ST.notifications = [];
    }
    smRenderNotifications();
}

function smRenderNotifications() {
    const list = ST.notifications || [];
    const unread = list.filter(n => !n.isRead).length;

    const countEl = document.getElementById("notifCount");
    if (countEl) {
        countEl.textContent = unread > 99 ? "99+" : String(unread);
        countEl.style.display = unread > 0 ? "flex" : "none";
    }
    const label = document.getElementById("notifUnreadLabel");
    if (label) label.textContent = unread + " unread";

    const html = list.slice(0, 8).map(smNotificationRow).join("")
        || `<div class="empty-state">No notifications yet</div>`;
    const dd = document.getElementById("notifList");
    if (dd) dd.innerHTML = html;

    const all = document.getElementById("allNotificationsList");
    if (all) {
        all.innerHTML = list.map(smNotificationRow).join("")
            || `<div class="empty-state">No notifications yet</div>`;
    }
}

function smNotificationRow(n) {
    const target = n.relatedScholarshipId
        ? `onclick="smOpenDetails(${n.relatedScholarshipId})"`
        : "";

    return `
        <div class="notif-item ${n.isRead ? "" : "unread"}" ${target}>
            <div class="ni-dot">${n.isRead ? "" : "●"}</div>
            <div>
                <div class="ni-title">${smEscapeHtml(n.title)}</div>
                <div class="ni-msg">${smEscapeHtml(n.message)}</div>
            </div>
        </div>`;
}
async function smMarkAllNotificationsRead() {
    try {
        await smApi.markAllNotificationsRead(SM_STUDENT_ID);
        smToast("All notifications marked as read", "success");
        await smLoadNotifications();
    } catch (err) {
        smToast(err.message, "error");
    }
}

function smShowAllNotifications() {
    smCloseAllMenus();
    smShowView("notifications-all");
}

/* ---------------- scholarship card ---------------- */

function smEligibilityBadge(scholarshipId) {
    const isEligible = ST.eligible.some(e => e.scholarshipId === scholarshipId);
    return isEligible
        ? `<span class="badge success">Eligible</span>`
        : `<span class="badge muted">Not Eligible</span>`;
}

/**
 * Card used by the 5 category views, Find Scholarship and Saved.
 * No Apply button here by design — applying happens in View Details only.
 */
function smScholarshipCard(s, opts) {
    opts = opts || {};
    const id = s.scholarshipId;
    const saved = smIsSaved(id);
    const expired = smIsExpired(s.deadline);

    const statusBadge = expired
        ? `<span class="badge danger">Expired</span>`
        : (opts.hideEligibility ? "" : smEligibilityBadge(id));

    return `
        <div class="sch-card">
            <div class="sc-head">
                <h4>${smEscapeHtml(s.title || "Scholarship")}</h4>
                ${statusBadge}
            </div>
            <div class="sc-provider">${smEscapeHtml(s.fundingBodyName || "—")}</div>
            <div class="sc-meta">
                <span class="sc-amount">${smCurrency(s.amount)}</span>
                <span class="sc-deadline">Deadline: ${smFormatDate(s.deadline)}</span>
            </div>
            <div class="sc-actions">
                <button class="btn btn-primary btn-sm" type="button" onclick="smViewScholarship(${id})">View</button>
                <button class="btn ${saved ? "btn-outline" : "btn-ghost"} btn-sm" type="button"
                        onclick="smToggleSave(${id})">
                    ${saved ? "Saved ✓ · Unsave" : "♡ Save"}
                </button>
            </div>
        </div>`;
}

/* ---------------- category views ---------------- */

async function smLoadCategory(categoryKey) {
    const grid = document.getElementById("grid-" + categoryKey);
    if (!grid) return;
    grid.innerHTML = `<div class="empty-state">Loading…</div>`;
    try {
        const res = await smApi.getScholarshipsByCategory(categoryKey);
        // Backend already filters approved + active + deadline >= today,
        // this second guard keeps expired items out if the row slips through.
        const list = (res.data || []).filter(s => !smIsExpired(s.deadline));
        smCacheScholarships(list);
        ST.categoryCache[categoryKey] = list;
        grid.innerHTML = list.map(s => smScholarshipCard(s)).join("")
            || `<div class="empty-state">No open scholarships in this category right now.</div>`;
    } catch (err) {
        grid.innerHTML = `<div class="empty-state">${smEscapeHtml(err.message)}</div>`;
    }
}

function smRenderHomeCategories() {
    const grid = document.getElementById("homeCategoryGrid");
    if (!grid) return;
    grid.innerHTML = SM_CATEGORIES.map(c => `
        <div class="cat-card" onclick="smShowView('${c.view}')">
            <div class="cc-icon">${c.icon}</div>
            <div class="cc-label">${smEscapeHtml(c.label)}</div>
            <div class="cc-count" id="cc-count-${c.key}">—</div>
        </div>`).join("");
}

/* ---------------- find scholarship (eligible only) ---------------- */

async function smLoadEligible() {
    const grid = document.getElementById("findScholarshipGrid");
    if (grid) grid.innerHTML = `<div class="empty-state">Checking eligibility…</div>`;
    try {
        const res = await smApi.getEligibleScholarships(SM_STUDENT_ID);
        ST.eligible = res.data || [];
    } catch (err) {
        ST.eligible = [];
        if (grid) grid.innerHTML = `<div class="empty-state">${smEscapeHtml(err.message)}</div>`;
        return;
    }

    // Missing profile fields reported by the eligibility engine.
    const missing = new Set();
    ST.eligible.forEach(e => (e.missingProfileFields || []).forEach(f => missing.add(f)));
    const alertBox = document.getElementById("findProfileAlert");
    const missingEl = document.getElementById("findMissingFields");
    const profileMissing = smComputeProfileCompletion().missing;
    const show = profileMissing.length > 0 || missing.size > 0;
    if (alertBox) alertBox.style.display = show ? "flex" : "none";
    if (missingEl) {
        const all = Array.from(new Set([...profileMissing, ...missing]));
        missingEl.textContent = "Missing: " + all.join(", ");
    }

    if (grid) {
        const cards = ST.eligible
            .filter(e => !e.expired)
            .map(e => {
                const s = ST.scholarshipsById[e.scholarshipId] || {
                    scholarshipId: e.scholarshipId,
                    title: e.scholarshipTitle,
                    fundingBodyName: e.fundingBodyName,
                    amount: e.amount,
                    deadline: e.deadline
                };
                smCacheScholarships([s]);
                return smScholarshipCard(s);
            });
        grid.innerHTML = cards.join("")
            || `<div class="empty-state">No scholarships match your profile yet. Completing your profile and documents may reveal more.</div>`;
    }
}

/* ---------------- save / unsave ---------------- */

async function smToggleSave(scholarshipId) {
    try {
        if (smIsSaved(scholarshipId)) {
            // Unsave clears only saved_at on the backend; an application is preserved.
            await smApi.removeBookmark(SM_STUDENT_ID, scholarshipId);
            smToast("Removed from Saved Scholarships", "success");
        } else {
            await smApi.saveBookmark(SM_STUDENT_ID, scholarshipId);
            smToast("Scholarship saved successfully", "success");
            await smLoadBookmarks();
            smRefreshVisibleLists();
            smShowView("saved");
            return;
        }
        await smLoadBookmarks();
        smRefreshVisibleLists();
    } catch (err) {
        smToast(err.message, "error");
    }
}
async function smLoadBookmarks() {
    try {
        const [savedRes, appliedRes] = await Promise.all([
            smApi.getSavedBookmarks(SM_STUDENT_ID).catch(() => ({ data: [] })),
            smApi.getAppliedScholarships(SM_STUDENT_ID).catch(() => ({ data: [] }))
        ]);
        ST.saved = savedRes.data || [];
        ST.applied = appliedRes.data || [];
    } catch (e) {
        ST.saved = [];
        ST.applied = [];
    }
}

/* ---------------- saved view ---------------- */

async function smRenderSaved() {
    const grid = document.getElementById("savedGrid");
    if (!grid) return;
    if (!ST.saved.length) {
        grid.innerHTML = `<div class="empty-state">You haven't saved any scholarships yet.</div>`;
        return;
    }
    // Expired scholarships stay in Saved, flagged as Expired.
    const items = await Promise.all(ST.saved.map(b => smGetScholarship(b.scholarshipId).catch(() => null)));
    grid.innerHTML = items.filter(Boolean).map(s => smScholarshipCard(s)).join("")
        || `<div class="empty-state">You haven't saved any scholarships yet.</div>`;
}

/* ---------------- my applications ---------------- */

async function smRenderApplications() {
    const body = document.getElementById("applications-body");
    if (!body) return;
    if (!ST.applied.length) {
        body.innerHTML = `<tr><td colspan="5" class="text-soft" style="text-align:center;padding:16px;">
            You haven't recorded any applications yet.</td></tr>`;
        return;
    }
    const rows = await Promise.all(ST.applied.map(async b => {
        const s = await smGetScholarship(b.scholarshipId).catch(() => null);
        const title = s ? s.title : "Scholarship #" + b.scholarshipId;
        const cat = s && s.primaryCategory ? s.primaryCategory : "—";
        const amt = s ? smCurrency(s.amount) : "—";
        return `<tr>
            <td class="row-title">${smEscapeHtml(title)}</td>
            <td><span class="tag">${smEscapeHtml(cat)}</span></td>
            <td>${amt}</td>
            <td>${smFormatDate(b.appliedAt)}</td>
            <td><button class="btn btn-ghost btn-sm" type="button" onclick="smViewScholarship(${b.scholarshipId})">View</button></td>
        </tr>`;
    }));
    body.innerHTML = rows.join("");

    const homeList = document.getElementById("homeApplicationsList");
    if (homeList) {
        const recent = ST.applied.slice(0, 3);
        homeList.innerHTML = recent.map(b => {
            const s = ST.scholarshipsById[b.scholarshipId];
            return `<div class="doc-row">
                <div class="doc-info"><div class="doc-ic">📝</div><div>
                    <div class="doc-name">${smEscapeHtml(s ? s.title : "Scholarship #" + b.scholarshipId)}</div>
                    <div class="doc-sub">Applied on ${smFormatDate(b.appliedAt)}</div>
                </div></div>
                <button class="btn btn-ghost btn-sm" type="button" onclick="smViewScholarship(${b.scholarshipId})">View</button>
            </div>`;
        }).join("") || `<div class="empty-state">No applications recorded yet.</div>`;
    }
}

/* ---------------- recently viewed ---------------- */

async function smLoadRecentlyViewed() {
    try {
        const res = await smApi.getStudentRecentlyViewed(SM_STUDENT_ID);
        ST.recentlyViewed = res.data || [];   // backend: deduplicated, newest first
    } catch (e) {
        ST.recentlyViewed = [];
    }
    const box = document.getElementById("homeRecentlyViewed");
    if (!box) return;
    if (!ST.recentlyViewed.length) {
        box.innerHTML = `<div class="empty-state">Nothing viewed yet.</div>`;
        return;
    }
    const items = await Promise.all(ST.recentlyViewed.slice(0, 8).map(async v => {
        const s = await smGetScholarship(v.scholarshipId).catch(() => null);
        const title = s ? s.title : "Scholarship #" + v.scholarshipId;
        const expired = s && smIsExpired(s.deadline);
        return `<div class="doc-row">
            <div class="doc-info"><div class="doc-ic">👁</div><div>
                <div class="doc-name">${smEscapeHtml(title)}
                    ${expired ? '<span class="badge danger">Expired</span>' : ""}</div>
                <div class="doc-sub">Viewed ${smFormatDate(v.viewedAt)}</div>
            </div></div>
            <button class="btn btn-ghost btn-sm" type="button" onclick="smViewScholarship(${v.scholarshipId})">View</button>
        </div>`;
    }));
    box.innerHTML = items.join("");
}

/* ---------------- view details ---------------- */

/** "View" is the only action that records a view, per the requirement. */
async function smViewScholarship(scholarshipId) {
    try {
        await smApi.recordScholarshipView(scholarshipId, SM_STUDENT_ID);
    } catch (e) { /* view logging must never block opening details */ }
    smOpenDetails(scholarshipId);
}

async function smOpenDetails(scholarshipId) {
    smCloseAllMenus();
    const current = document.querySelector(".view[style*='block']");
    if (current && current.id !== "view-details") {
        ST.lastView = current.id.replace("view-", "");
    }
    smShowView("details");
    const box = document.getElementById("detailsContent");
    box.innerHTML = `<div class="empty-state">Loading…</div>`;

    const nav = document.getElementById("detailsSectionNav");
    if (nav) nav.style.display = "none";

    try {
        const [s, detRes] = await Promise.all([
            smGetScholarship(scholarshipId),
            smApi.getScholarshipEligibilityDetails(SM_STUDENT_ID, scholarshipId)
        ]);
        const d = detRes.data || {};
        box.innerHTML = smRenderDetails(s, d);
        if (nav) nav.style.display = "flex";
        smInitSectionNav();
    } catch (err) {
        box.innerHTML = `<div class="empty-state">${smEscapeHtml(err.message)}</div>`;
    }
}

function smBackFromDetails() {
    smTeardownSectionNav();
    const nav = document.getElementById("detailsSectionNav");
    if (nav) nav.style.display = "none";
    smShowView(ST.lastView || "home");
}

/* ---------------- section navigation (details page) ---------------- */

let smSectionObserver = null;

/** Smoothly scrolls to a details section and marks its nav link active right away. */
function smScrollToSection(sectionId) {
    const el = document.getElementById(sectionId);
    if (!el) return;
    smSetActiveSectionLink(sectionId);
    el.scrollIntoView({ behavior: "smooth", block: "start" });
}

function smSetActiveSectionLink(sectionId) {
    const nav = document.getElementById("detailsSectionNav");
    if (!nav) return;
    nav.querySelectorAll("a").forEach(a => {
        a.classList.toggle("active", a.dataset.target === sectionId);
    });
}

/** Observes each rendered section so the nav highlights whichever is currently in view. */
function smInitSectionNav() {
    smTeardownSectionNav();
    const sections = document.querySelectorAll("#detailsContent .details-section");
    if (!sections.length || typeof IntersectionObserver === "undefined") return;

    smSetActiveSectionLink(sections[0].id);
    smSectionObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) smSetActiveSectionLink(entry.target.id);
        });
    }, { root: null, rootMargin: "-140px 0px -60% 0px", threshold: 0 });

    sections.forEach(sec => smSectionObserver.observe(sec));
}

function smTeardownSectionNav() {
    if (smSectionObserver) {
        smSectionObserver.disconnect();
        smSectionObserver = null;
    }
}

function smCertStatusBadge(status) {
    switch (status) {
        case "VALID":          return `<span class="badge success">Valid</span>`;
        case "EXPIRING_SOON":  return `<span class="badge warning">Expiring Soon</span>`;
        case "EXPIRED":        return `<span class="badge danger">Expired</span>`;
        default:               return `<span class="badge muted">Missing</span>`;
    }
}

/**
 * "Get Certificate" block.
 * Links come only from the backend (certificate_type / certificate_portal_link).
 * College/School/Bank documents deliberately show guidance and no links.
 */
function smGetCertificateBlock(c) {
    if (c.status === "VALID" || c.status === "EXPIRING_SOON") return "";

    if (c.sourceCategory === "PROFILE") {
        return `<div class="cert-help">
            <button class="btn btn-outline btn-sm" type="button" onclick="smShowView('profile')">
                Upload Profile Photo in Profile &amp; Documents
            </button>
        </div>`;
    }

    if (c.sourceCategory === "ISSUER_PROVIDED") {
        return `<div class="cert-help"><span class="cert-guidance">${smEscapeHtml(c.guidance || "Get it from your College/School/Bank")}</span></div>`;
    }

    const links = (c.applyLinks || []).map(l => `
        <a class="btn btn-outline btn-sm" href="${smEscapeHtml(l.url)}" target="_blank" rel="noopener">
            ${smEscapeHtml(l.label || "Apply Online")}
        </a>`).join("");

    return `<div class="cert-help">
        ${links}
        <span class="cert-guidance">${smEscapeHtml(c.guidance || "")}</span>
    </div>`;
}

/**
 * Matches each backend-generated reason string (eligible or ineligible) to a
 * named eligibility criterion, purely by the fixed phrasing EligibilityEngineService
 * uses (see evaluateCategory/evaluateIncome/evaluateCourse/evaluateMarks/evaluateState/
 * evaluateSpecialStatus/evaluateGender). No new backend field or endpoint is needed —
 * this only organizes the exact text the API already returns.
 */
function smCriterionLabel(text) {
    const t = text.toLowerCase();
    if (t.startsWith("gender") || t.includes("female gender")) return "Gender";
    if (t.startsWith("community")) return "Community / Category";
    if (t.includes("family annual income") || t.includes("family income restriction")) return "Annual Family Income";
    if (t.startsWith("course")) return "Course / Stream";
    if (t.startsWith("marks") || t.includes("minimum marks")) return "Minimum CGPA / Marks";
    if (t.includes("state / domicile") || t.includes("states")) return "Domicile State";
    if (t.includes("special status")) return "Other Conditions (Special Status)";
    return "Other";
}

/** Builds one row per eligibility criterion, each tagged Met / Not Met with the exact backend reason. */
function smBuildEligibilityRows(d) {
    const rows = {};
    (d.eligibleReasons || []).forEach(r => {
        const label = smCriterionLabel(r);
        rows[label] = { label, met: true, text: r };
    });
    (d.ineligibleReasons || []).forEach(r => {
        const label = smCriterionLabel(r);
        rows[label] = { label, met: false, text: r }; // a failure always overrides a trivial pass
    });
    // Fixed order matches the criteria the eligibility engine actually evaluates.
    const order = ["Gender", "Community / Category", "Course / Stream", "Minimum CGPA / Marks",
        "Annual Family Income", "Domicile State", "Other Conditions (Special Status)", "Other"];
    return order.map(l => rows[l]).filter(Boolean);
}

function smEligibilityRow(row) {
    return `
        <div class="elig-row ${row.met ? "met" : "not-met"}">
            <div class="elig-icon">${row.met ? "✓" : "✕"}</div>
            <div>
                <div class="elig-label">${smEscapeHtml(row.label)}</div>
                <div class="elig-text">${smEscapeHtml(row.text)}</div>
            </div>
        </div>`;
}

/** Overview "Who can apply" line, built only from real scholarship fields already on the record. */
function smWhoCanApply(s) {
    const parts = [];
    if (s.primaryCategory === "WOMENS") parts.push("Female students");
    if (s.educationLevel && s.educationLevel !== "ANY") parts.push(s.educationLevel.replace(/_/g, " ").toLowerCase() + " level students");
    if (s.eligibleCourses && s.eligibleCourses.length) parts.push("studying " + s.eligibleCourses.join(", "));
    if (s.eligibleCategories && s.eligibleCategories.length) parts.push("in category " + s.eligibleCategories.join(", "));
    if (s.eligibleStates && s.eligibleStates.length) parts.push("domiciled in " + s.eligibleStates.join(", "));
    if (s.minMarksCgpa) parts.push("with marks/CGPA of at least " + s.minMarksCgpa);
    if (s.maxAnnualIncome) parts.push("family annual income up to " + smCurrency(s.maxAnnualIncome));
    return parts.length ? parts.join(", ") : "Open to all eligible students who meet the criteria below.";
}

function smRenderDetails(s, d) {
    const mandatory = (d.certificates || []).filter(c => c.mandatory);
    const optional = (d.certificates || []).filter(c => !c.mandatory);
    const saved = smIsSaved(s.scholarshipId);
    const applied = d.alreadyApplied || smIsApplied(s.scholarshipId);
    const eligRows = smBuildEligibilityRows(d);

    const certRow = c => `
        <div class="cert-row">
            <div>
                <div class="cert-name">${smEscapeHtml(c.certificateName)} ${smCertStatusBadge(c.status)}</div>
                ${c.expiryDate ? `<div class="doc-sub">Expires ${smFormatDate(c.expiryDate)}</div>` : ""}
                ${smGetCertificateBlock(c)}
            </div>
        </div>`;

    const applyDisabled = !d.canApply;
    const applyTitle = applyDisabled
        ? "Apply is unavailable: " + (d.blockingReasons || []).join(" ")
        : "Opens the official application website in a new tab";

    return `
    <!-- 1. OVERVIEW -->
    <div class="card details-section" id="sec-overview">
        <div class="details-head">
            <div>
                <h2>${smEscapeHtml(s.title || d.scholarshipTitle || "Scholarship")}</h2>
                <div class="text-soft">${smEscapeHtml(s.fundingBodyName || "—")}</div>
            </div>
            <span class="tag">${smEscapeHtml(s.primaryCategory || "—")}</span>
        </div>

        <div class="details-meta">
            <div><span class="dm-lab">Source</span><span class="dm-val">${smEscapeHtml((s.sourceType || "—").replace(/_/g, " "))}</span></div>
            <div><span class="dm-lab">Application Mode</span><span class="dm-val">${smEscapeHtml((s.applicationMode || "—").replace(/_/g, " "))}</span></div>
            <div><span class="dm-lab">Status</span><span class="dm-val">
                ${d.eligible ? '<span class="badge success">Eligible</span>' : '<span class="badge muted">Not Eligible</span>'}
            </span></div>
        </div>

        ${s.description ? `<p class="mt-16">${smEscapeHtml(s.description)}</p>` : `<p class="text-soft mt-16">No description provided for this scholarship.</p>`}

        <h4 class="cert-group-title mt-16">Who Can Apply</h4>
        <p class="text-soft">${smEscapeHtml(smWhoCanApply(s))}</p>
    </div>

    <!-- 2. ELIGIBILITY -->
    <div class="card mt-24 details-section" id="sec-eligibility">
        <div class="card-head"><h3>Eligibility — ${d.eligible ? "You are Eligible" : "You are Not Eligible"}</h3></div>
        ${eligRows.map(smEligibilityRow).join("") || `<div class="empty-state">No eligibility criteria set for this scholarship.</div>`}
    </div>

    <!-- 3. SCHOLARSHIP BENEFITS -->
    <div class="card mt-24 details-section" id="sec-amount">
        <div class="card-head"><h3>Scholarship Benefits</h3></div>
        <div class="details-meta">
            <div><span class="dm-lab">Amount</span><span class="dm-val">${smCurrency(s.amount)}</span></div>
        </div>
    </div>

    <!-- 4. REQUIRED CERTIFICATES -->
    <div class="card mt-24 details-section" id="sec-certificates">
        <div class="card-head">
            <h3>Required Certificates</h3>
            <span class="text-soft">${d.mandatorySatisfiedCount || 0} / ${d.mandatoryCertificateCount || 0} mandatory ready</span>
        </div>
        <h4 class="cert-group-title">Mandatory</h4>
        ${mandatory.map(certRow).join("") || `<div class="empty-state">No mandatory certificates listed.</div>`}
        <h4 class="cert-group-title mt-16">Optional</h4>
        ${optional.map(certRow).join("") || `<div class="empty-state">No optional certificates listed.</div>`}
        <p class="text-soft mt-8">Missing or expired mandatory certificates block Apply Now. Optional certificates never block applying.</p>
    </div>

    <!-- 5. IMPORTANT DATES -->
    <div class="card mt-24 details-section" id="sec-deadline">
        <div class="card-head"><h3>Important Dates</h3></div>
        <div class="details-meta">
            <div><span class="dm-lab">Deadline</span><span class="dm-val">${smFormatDate(s.deadline)}
                ${d.expired ? '<span class="badge danger">Expired</span>' : ""}</span></div>
        </div>
    </div>

    ${(d.blockingReasons || []).length ? `
    <div class="card mt-24 blocking-card">
        <div class="card-head"><h3>Before you can apply</h3></div>
        ${d.blockingReasons.map(r => `<div class="reason-chip bad">${smEscapeHtml(r)}</div>`).join("")}
    </div>` : ""}

    <!-- 6. APPLICATION -->
    <div class="card mt-24 details-section" id="sec-apply">
        <div class="card-head"><h3>Application</h3></div>
        <div class="details-actions">
            ${s.officialApplicationLink && !applyDisabled ? `
                <a class="btn btn-primary" href="${smEscapeHtml(s.officialApplicationLink)}"
                   target="_blank" rel="noopener" title="${smEscapeHtml(applyTitle)}">Apply Now ↗</a>
            ` : `
                <button class="btn btn-primary" type="button" disabled
                        title="${smEscapeHtml(applyTitle)}">Apply Now ↗</button>
            `}

            ${applied
        ? `<button class="btn btn-outline" type="button" disabled>Already Applied ✓</button>`
        : `<button class="btn btn-outline" type="button" onclick="smAskIHaveApplied(${s.scholarshipId})">I Have Applied</button>`}

            <button class="btn ${saved ? "btn-outline" : "btn-ghost"}" type="button" onclick="smToggleSave(${s.scholarshipId})">
                ${saved ? "Saved ✓ · Unsave" : "♡ Save Scholarship"}
            </button>

            <button class="btn btn-ghost" type="button" onclick="smBackFromDetails()">← Back to Scholarships</button>
        </div>
        ${!s.officialApplicationLink ? `<p class="text-soft mt-8">No official application link is set for this scholarship.</p>` : ""}
    </div>`;
}

/* ---------------- "I have applied" confirmation ---------------- */

function smAskIHaveApplied(scholarshipId) {
    const s = ST.scholarshipsById[scholarshipId];
    const titleEl = document.getElementById("appliedModalTitle");
    if (titleEl) titleEl.textContent = s ? s.title : "";
    const btn = document.getElementById("appliedConfirmBtn");
    btn.onclick = () => smConfirmApplied(scholarshipId);
    smOpenModal("appliedModal");
}

async function smConfirmApplied(scholarshipId) {
    try {
        // Backend sets applied_at once; repeat calls are ignored so the date stays immutable.
        await smApi.markApplied(SM_STUDENT_ID, scholarshipId);
        smCloseModal("appliedModal");
        smToast("Recorded in My Applications", "success");
        await smLoadBookmarks();
        await smRenderApplications();
        smOpenDetails(scholarshipId);
        smRefreshStats();
    } catch (err) {
        smToast(err.message, "error");
    }
}

/* ---------------- profile completion ---------------- */

function smComputeProfileCompletion() {
    const s = ST.student || {};
    const a = ST.academic || {};
    const f = ST.financial || {};

    const fields = [
        ["Full Name", s.fullName],
        ["Date of Birth", s.dateOfBirth],
        ["Gender", s.gender],
        ["Mobile Number", s.mobileNumber],
        ["Email", s.email],
        ["Profile Photo", s.profilePhotoUrl],
        ["Address", s.permanentAddress],
        ["State", s.state],
        ["District", s.district],
        ["Community / Category", s.category],
        ["Domicile", s.domicile],
        ["Income Category", s.incomeCategory],
        ["College / Institution", a.institutionName],
        ["University / Board", a.universityBoard],
        ["Course", a.courseName],
        ["Current Year", a.currentYear],
        ["Current Semester", a.currentSemester],
        ["CGPA / Qualifying %", a.qualifyingExamPercentage],
        ["10th Percentage", a.tenthPercentage],
        ["12th Percentage", a.twelfthPercentage],
        ["Admission Year", a.admissionYear],
        ["Family Annual Income", f.annualFamilyIncome]
    ];

    const missing = fields
        .filter(([, v]) => v === null || v === undefined || v === "" )
        .map(([k]) => k);

    const pct = Math.round(((fields.length - missing.length) / fields.length) * 100);
    return { pct, missing, total: fields.length };
}

function smRenderProgressRing(elId, pctId, pct) {
    const ring = document.getElementById(elId);
    const label = document.getElementById(pctId);
    if (label) label.textContent = pct + "%";
    if (ring) {
        ring.style.background =
            `conic-gradient(var(--brand, #4f46e5) ${pct * 3.6}deg, var(--line, #e5e7eb) 0deg)`;
    }
}

function smRenderProfileCompletion() {
    const { pct, missing } = smComputeProfileCompletion();
    smRenderProgressRing("profileProgressRing", "profileProgressPct", pct);
    smRenderProgressRing("homeProgressRing", "homeProgressPct", pct);

    const title = document.getElementById("profileCompletionTitle");
    if (title) title.textContent = pct + "% complete";

    const text = missing.length
        ? "Missing: " + missing.join(", ")
        : "All profile fields are filled.";
    const el1 = document.getElementById("profileMissingFields");
    const el2 = document.getElementById("homeMissingFields");
    if (el1) el1.textContent = text;
    if (el2) el2.textContent = text;
}

/* ---------------- profile load / save ---------------- */

function smSetVal(id, value) {
    const el = document.getElementById(id);
    if (el) el.value = value === null || value === undefined ? "" : value;
}

function smSetChecked(id, value) {
    const el = document.getElementById(id);
    if (el) el.checked = !!value;
}

async function smLoadProfile() {
    try {
        const res = await smApi.getStudent(SM_STUDENT_ID);
        ST.student = (res && res.data) || {};
    } catch (e) { ST.student = {}; }

    try {
        const res = await smApi.getAcademicInfo(SM_STUDENT_ID);
        ST.academic = (res && res.data) || {};
    } catch (e) { ST.academic = {}; }

    try {
        const res = await smApi.getFinancialInfo(SM_STUDENT_ID);
        ST.financial = (res && res.data) || {};
    } catch (e) { ST.financial = {}; }

    try {
        const res = await smApi.getSpecialStatus(SM_STUDENT_ID);
        ST.special = (res && res.data) || {};
    } catch (e) { ST.special = {}; }

    const s = ST.student, a = ST.academic, f = ST.financial, sp = ST.special;

    smSetVal("pName", s.fullName || (SM_SESSION.profile && SM_SESSION.profile.fullName));
    smSetVal("pEmail", s.email || (SM_SESSION.profile && SM_SESSION.profile.email));
    smSetVal("pDob", s.dateOfBirth);
    smSetVal("pGender", s.gender || "");
    smSetVal("pMobile", s.mobileNumber);
    smSetVal("pAddress", s.permanentAddress);
    smSetVal("pState", s.state);
    smSetVal("pDistrict", s.district);
    smSetVal("pCategory", s.category);
    smSetVal("pDomicile", s.domicile);
    smSetVal("pIncomeCategory", s.incomeCategory);
    smSetVal("pDisability", s.disabilityPercentage);
    smSetChecked("pPwd", s.pwd);

    smSetVal("pInstitution", a.institutionName);
    smSetVal("pUniversity", a.universityBoard);
    smSetVal("pCourse", a.courseName);
    smSetVal("pSpecialization", a.specialization);
    smSetVal("pCurrentYear", a.currentYear);
    smSetVal("pCurrentSemester", a.currentSemester);
    smSetVal("pMarks", a.qualifyingExamPercentage);
    smSetVal("pTenth", a.tenthPercentage);
    smSetVal("pTwelfth", a.twelfthPercentage);
    smSetVal("pAdmissionYear", a.admissionYear);

    smSetVal("pIncome", f.annualFamilyIncome);
    smSetVal("pBpl", f.bplStatus ? "true" : "false");

    smSetChecked("pFirstGraduate", sp.firstGraduate);
    smSetChecked("pSingleGirlChild", sp.singleGirlChild);
    smSetChecked("pOrphanSingleParent", sp.orphanSingleParent);
    smSetChecked("pExServicemen", sp.exServicemenDependent);
    smSetChecked("pSportsQuota", sp.sportsQuota);
    smSetChecked("pMinority", sp.minorityCommunity);

    await smLoadPhotoUrl(true);

    const preview = document.getElementById("photoPreview");
    if (preview) {
        preview.innerHTML = ST.photoUrl
            ? `<img src="${ST.photoUrl}" alt="Profile photo">`
            : "No photo";
    }

    smRenderUserChip();
    smRenderProfileCompletion();
}

function smNum(id) {
    const v = document.getElementById(id) ? document.getElementById(id).value : "";
    if (v === "" || v === null) return null;
    const n = parseFloat(v);
    return isNaN(n) ? null : n;
}

function smStr(id) {
    const el = document.getElementById(id);
    return el ? el.value.trim() : "";
}

async function smSaveProfile() {
    const s = ST.student || {};
    const a = ST.academic || {};
    const f = ST.financial || {};

    const studentPayload = {
        fullName: s.fullName || (SM_SESSION.profile && SM_SESSION.profile.fullName) || "",
        dateOfBirth: smStr("pDob") || null,
        gender: smStr("pGender"),
        mobileNumber: smStr("pMobile"),
        email: s.email || (SM_SESSION.profile && SM_SESSION.profile.email) || "",
        category: smStr("pCategory").toUpperCase(),
        state: smStr("pState"),
        district: smStr("pDistrict"),
        aadhaarNumber: s.aadhaarNumber || "",
        permanentAddress: smStr("pAddress"),
        religion: s.religion || "",
        pwd: document.getElementById("pPwd").checked,
        disabilityPercentage: smNum("pDisability"),
        annualIncome: smNum("pIncome") || 0,
        marksCgpa: smNum("pMarks") || 0,
        domicile: smStr("pDomicile"),
        incomeCategory: smStr("pIncomeCategory")
    };

    const pCurrentYearVal = smNum("pCurrentYear");
    const pCurrentSemesterVal = smNum("pCurrentSemester");
    const currentYearSemesterParts = [];
    if (pCurrentYearVal !== null) currentYearSemesterParts.push(`Year ${pCurrentYearVal}`);
    if (pCurrentSemesterVal !== null) currentYearSemesterParts.push(`Semester ${pCurrentSemesterVal}`);
    const currentYearSemesterValue = currentYearSemesterParts.length
        ? currentYearSemesterParts.join(", ")
        : (a.currentYearSemester || "");

    const academicPayload = {
        educationLevel: a.educationLevel || "UNDERGRADUATE",
        courseName: smStr("pCourse"),
        institutionName: smStr("pInstitution"),
        currentYearSemester: currentYearSemesterValue,
        qualifyingExamPercentage: smNum("pMarks") || 0,
        specialization: smStr("pSpecialization"),
        universityBoard: smStr("pUniversity"),
        admissionYear: smNum("pAdmissionYear"),
        modeOfStudy: a.modeOfStudy || "",
        rollNumber: a.rollNumber || "",
        tenthPercentage: smNum("pTenth"),
        twelfthPercentage: smNum("pTwelfth"),
        currentYear: pCurrentYearVal,
        currentSemester: pCurrentSemesterVal
    };

    const financialPayload = {
        annualFamilyIncome: smNum("pIncome") || 0,
        fatherOccupation: f.fatherOccupation || "",
        motherOccupation: f.motherOccupation || "",
        bplStatus: smStr("pBpl") === "true",
        bankAccountNumber: f.bankAccountNumber || "",
        bankIfsc: f.bankIfsc || "",
        bankName: f.bankName || ""
    };

    const specialPayload = {
        singleGirlChild: document.getElementById("pSingleGirlChild").checked,
        orphanSingleParent: document.getElementById("pOrphanSingleParent").checked,
        exServicemenDependent: document.getElementById("pExServicemen").checked,
        sportsQuota: document.getElementById("pSportsQuota").checked,
        minorityCommunity: document.getElementById("pMinority").checked,
        firstGraduate: document.getElementById("pFirstGraduate").checked
    };

    try {
        await smApi.updateStudent(SM_STUDENT_ID, studentPayload);

        try { await smApi.updateAcademicInfo(SM_STUDENT_ID, academicPayload); }
        catch (e) { await smApi.saveAcademicInfo(SM_STUDENT_ID, academicPayload); }

        try { await smApi.updateFinancialInfo(SM_STUDENT_ID, financialPayload); }
        catch (e) { await smApi.saveFinancialInfo(SM_STUDENT_ID, financialPayload); }

        try { await smApi.updateSpecialStatus(SM_STUDENT_ID, specialPayload); }
        catch (e) { await smApi.saveSpecialStatus(SM_STUDENT_ID, specialPayload); }

        smToast("Profile updated", "success");
        await smLoadProfile();
        await smLoadEligible();      // eligibility is re-evaluated after any profile change
        smRefreshStats();
    } catch (err) {
        smToast(err.message, "error");
    }
}

/* ---------------- profile photo ---------------- */

async function smUploadPhoto() {
    const input = document.getElementById("photoFile");
    if (!input.files || !input.files[0]) {
        smToast("Choose a photo first", "error");
        return;
    }
    const btn = document.getElementById("photoUploadBtn");
    btn.disabled = true;
    btn.textContent = "Uploading…";
    try {
        await smApi.uploadStudentPhoto(SM_STUDENT_ID, input.files[0]);
        smToast("Profile photo updated", "success");
        input.value = "";
        await smLoadProfile();
        await smLoadEligible();
        smRefreshStats();
    } catch (err) {
        smToast(err.message, "error");
    } finally {
        btn.disabled = false;
        btn.textContent = "Upload Photo";
    }
}

/* ---------------- documents ---------------- */

/** Certificate types whose name implies a validity period get the expiry inputs. */
function smTypeHasExpiry(name) {
    const n = (name || "").toLowerCase();
    return n.includes("income") || n.includes("community") || n.includes("caste")
        || n.includes("nativity") || n.includes("domicile") || n.includes("ews")
        || n.includes("disability") || n.includes("bpl");
}

function smToggleExpiryFields() {
    const sel = document.getElementById("docCertType");
    const type = ST.certTypes.find(t => String(t.certificateTypeId) === String(sel.value));
    const hasExpiry = type ? smTypeHasExpiry(type.name) : false;

    const issue = document.getElementById("issueDateField");
    const expiry = document.getElementById("expiryDateField");
    const hint = document.getElementById("docExpiryHint");

    if (issue) issue.style.display = hasExpiry ? "" : "none";
    if (expiry) expiry.style.display = hasExpiry ? "" : "none";
    if (hint) {
        hint.textContent = hasExpiry
            ? "This certificate has a validity period, so issue and expiry dates are required."
            : "This certificate does not expire, so no dates are needed.";
    }
    if (!hasExpiry) {
        smSetVal("docIssueDate", "");
        smSetVal("docExpiryDate", "");
    }
}

async function smLoadCertTypes() {
    try {
        const res = await smApi.getCertificateTypes();
        ST.certTypes = res.data || [];
    } catch (e) { ST.certTypes = []; }

    const sel = document.getElementById("docCertType");
    if (sel) {
        sel.innerHTML = ST.certTypes
            .map(c => `<option value="${c.certificateTypeId}">${smEscapeHtml(c.name)}</option>`)
            .join("") || `<option value="">No certificate types available</option>`;
        smToggleExpiryFields();
    }
}

function smCertTypeName(id) {
    const t = ST.certTypes.find(c => c.certificateTypeId === id);
    return t ? t.name : "Certificate #" + id;
}

/** Live status: a document that expired after upload is shown as Expired. */
function smLiveDocStatus(d) {
    if (!d.expiryDate) return d.status || "VALID";
    const days = smDaysUntil(d.expiryDate);
    if (days < 0) return "EXPIRED";
    if (days <= 30) return "EXPIRING_SOON";
    return "VALID";
}

async function smLoadDocuments() {
    try {
        const res = await smApi.getStudentDocuments(SM_STUDENT_ID);
        ST.documents = res.data || [];
    } catch (e) { ST.documents = []; }

    const box = document.getElementById("documentsList");
    if (!box) return;

    const uploadedIds = new Set(ST.documents.map(d => d.certificateTypeId));

    const uploaded = ST.documents.map(d => {
        const status = smLiveDocStatus(d);
        const expiredOrInvalid = status === "EXPIRED";
        return `<div class="doc-row">
            <div class="doc-info"><div class="doc-ic">📄</div><div>
                <div class="doc-name">${smEscapeHtml(smCertTypeName(d.certificateTypeId))}
                    ${smCertStatusBadge(status)}</div>
                <div class="doc-sub">
                    ${d.issueDate ? "Issued " + smFormatDate(d.issueDate) : "No issue date"}
                    ${d.expiryDate ? " · Expires " + smFormatDate(d.expiryDate) : ""}
                </div>
                ${expiredOrInvalid ? `<div class="doc-sub bad">This certificate is invalid. Upload a new one to replace it.</div>` : ""}
            </div></div>
            <div style="display:flex; gap:8px;">
                <button class="btn btn-ghost btn-sm" type="button" onclick="smViewDocument(${d.documentId})">View</button>
                <button class="btn ${expiredOrInvalid ? "btn-primary" : "btn-outline"} btn-sm" type="button"
                        onclick="smPrepareReplace(${d.certificateTypeId})">
                    ${expiredOrInvalid ? "Upload New Certificate" : "Replace"}
                </button>
            </div>
        </div>`;
    }).join("");

    const missing = ST.certTypes
        .filter(t => !uploadedIds.has(t.certificateTypeId))
        .map(t => `<div class="doc-row">
            <div class="doc-info"><div class="doc-ic">➕</div><div>
                <div class="doc-name">${smEscapeHtml(t.name)} <span class="badge muted">Missing</span></div>
                <div class="doc-sub">Not uploaded yet</div>
            </div></div>
            <button class="btn btn-outline btn-sm" type="button" onclick="smPrepareReplace(${t.certificateTypeId})">Upload</button>
        </div>`).join("");

    box.innerHTML = (uploaded + missing) || `<div class="empty-state">No certificate types configured.</div>`;
}

function smPrepareReplace(certificateTypeId) {
    const sel = document.getElementById("docCertType");
    if (sel) sel.value = String(certificateTypeId);
    smToggleExpiryFields();
    const fileInput = document.getElementById("docFile");
    if (fileInput) fileInput.scrollIntoView({ behavior: "smooth", block: "center" });
    smToast("Choose a file to upload for " + smCertTypeName(certificateTypeId), "success");
}

async function smUploadDocument() {
    const certTypeId = smStr("docCertType");
    const fileInput = document.getElementById("docFile");
    const issueDate = smStr("docIssueDate") || null;
    const expiryDate = smStr("docExpiryDate") || null;

    if (!certTypeId || !fileInput.files || !fileInput.files[0]) {
        smToast("Choose a certificate type and a file", "error");
        return;
    }

    const btn = document.getElementById("docUploadBtn");
    btn.disabled = true;
    btn.textContent = "Uploading…";
    try {
        // Re-uploading an existing certificate type replaces the old record on the backend.
        await smApi.uploadStudentDocumentFile(
            SM_STUDENT_ID, Number(certTypeId), fileInput.files[0], issueDate, expiryDate);
        smToast("Certificate uploaded", "success");
        fileInput.value = "";
        smSetVal("docIssueDate", "");
        smSetVal("docExpiryDate", "");
        await smLoadDocuments();
        await smLoadEligible();      // documents affect eligibility
        smRefreshStats();
    } catch (err) {
        smToast(err.message, "error");
    } finally {
        btn.disabled = false;
        btn.textContent = "Upload";
    }
}

async function smViewDocument(documentId) {
    try {
        await smApi.viewStudentDocumentFile(SM_STUDENT_ID, documentId);
    } catch (err) {
        smToast(err.message, "error");
    }
}

/* ---------------- stats ---------------- */

function smCountMissingDocuments() {
    const uploaded = new Set(
        ST.documents.filter(d => smLiveDocStatus(d) !== "EXPIRED").map(d => d.certificateTypeId));
    let missing = ST.certTypes.filter(t => !uploaded.has(t.certificateTypeId)).length;
    if (!ST.student.profilePhotoUrl) missing += 1;   // profile photo is mandatory
    return missing;
}

function smCountUpcomingDeadlines() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const maxDate = new Date(today);
    maxDate.setDate(maxDate.getDate() + 30);

    // Use scholarships loaded into the 5 category caches.
    // Category APIs already return approved + active + non-expired scholarships.
    const scholarships = Object.values(ST.categoryCache || {})
        .flat()
        .filter(Boolean);

    // Remove duplicates because a scholarship should belong to only one category.
    const unique = new Map();
    scholarships.forEach(s => {
        if (s.scholarshipId != null) {
            unique.set(s.scholarshipId, s);
        }
    });

    return Array.from(unique.values()).filter(s => {
        if (!s.deadline) return false;

        const deadline = new Date(s.deadline);
        if (isNaN(deadline.getTime())) return false;

        deadline.setHours(0, 0, 0, 0);

        return deadline >= today && deadline <= maxDate;
    }).length;
}

function smRefreshStats() {
    const eligible = ST.eligible.length;
    const saved = ST.saved.length;
    const applied = ST.applied.length;
    const deadlines = smCountUpcomingDeadlines();
    const missingDocs = smCountMissingDocuments();

    const set = (id, v) => { const el = document.getElementById(id); if (el) el.textContent = v; };
    set("statEligible", eligible);   set("sbEligible", eligible);
    set("statSaved", saved);         set("sbSaved", saved);
    set("statApplied", applied);     set("sbApplied", applied);
    set("statDeadlines", deadlines); set("sbDeadlines", deadlines);
    set("statMissingDocs", missingDocs); set("sbMissingDocs", missingDocs);

    const summary = document.getElementById("homeEligibleSummary");
    if (summary) {
        summary.textContent = eligible
            ? `You are currently eligible for ${eligible} open scholarship${eligible === 1 ? "" : "s"}.`
            : "No scholarships match your profile yet. Completing your profile and documents may reveal more.";
    }

    SM_CATEGORIES.forEach(c => {
        const el = document.getElementById("cc-count-" + c.key);
        if (el && ST.categoryCache[c.key]) {
            const n = ST.categoryCache[c.key].length;
            el.textContent = n + (n === 1 ? " scholarship" : " scholarships");
        }
    });
}

/* ---------------- saved / home lists ---------------- */

async function smRenderHomeSaved() {
    const box = document.getElementById("homeSavedList");
    if (!box) return;
    if (!ST.saved.length) {
        box.innerHTML = `<div class="empty-state">You haven't saved any scholarships yet.</div>`;
        return;
    }
    const items = await Promise.all(ST.saved.slice(0, 4).map(b => smGetScholarship(b.scholarshipId).catch(() => null)));
    box.innerHTML = items.filter(Boolean).map(s => `
        <div class="doc-row">
            <div class="doc-info"><div class="doc-ic">♡</div><div>
                <div class="doc-name">${smEscapeHtml(s.title)}
                    ${smIsExpired(s.deadline) ? '<span class="badge danger">Expired</span>' : ""}</div>
                <div class="doc-sub">${smCurrency(s.amount)} · Deadline ${smFormatDate(s.deadline)}</div>
            </div></div>
            <button class="btn btn-ghost btn-sm" type="button" onclick="smViewScholarship(${s.scholarshipId})">View</button>
        </div>`).join("");
}

/** Re-renders whichever list is currently on screen after a save/unsave. */
function smRefreshVisibleLists() {
    const current = document.querySelector(".view[style*='block']");
    if (!current) return;
    const id = current.id;
    if (id === "view-saved") smRenderSaved();
    else if (id === "view-home") { smRenderHomeSaved(); smRefreshStats(); }
    else if (id === "view-find-scholarship") smLoadEligible();
    else if (id.startsWith("view-cat-")) {
        const cat = SM_CATEGORIES.find(c => "view-" + c.view === id);
        if (cat) smLoadCategory(cat.key);
    }
    smRefreshStats();
}

/* ---------------- view routing ---------------- */

/* ---------------- view routing + back navigation ---------------- */

const smBaseShowView = smShowView;

let SM_VIEW_HISTORY = [];
let SM_GOING_BACK = false;

/**
 * Returns the currently visible student portal view.
 */
function smGetCurrentViewId() {
    const current = document.querySelector(".view[style*='block']");
    return current ? current.id.replace("view-", "") : "home";
}

/**
 * Adds a Back button to the current page.
 * Home itself never gets a Back button.
 */
function smAddGlobalBackButton(view) {
    if (!view || view === "home") return;

    const viewEl = document.getElementById("view-" + view);
    if (!viewEl) return;

    // Avoid duplicate button
    if (viewEl.querySelector(".student-global-back")) return;

    const backBtn = document.createElement("button");
    backBtn.type = "button";
    backBtn.className = "student-global-back";
    backBtn.innerHTML = `
        <span class="back-arrow">←</span>
        <span>Back</span>
    `;

    backBtn.addEventListener("click", smGoBack);

    // Put it at the beginning of the page
    viewEl.insertBefore(backBtn, viewEl.firstChild);
}

/**
 * Navigate back to the actual previous student portal view.
 */
function smGoBack() {
    if (!SM_VIEW_HISTORY.length) {
        smBaseShowView("home");
        return;
    }

    const previousView = SM_VIEW_HISTORY.pop();

    SM_GOING_BACK = true;

    try {
        smShowView(previousView);
    } finally {
        SM_GOING_BACK = false;
    }
}

/**
 * Wrapped view navigation.
 * Stores the previous page before moving to another page.
 */
window.smShowView = function (view) {
    const currentView = smGetCurrentViewId();

    // Store actual navigation history.
    // Do not store the same page repeatedly.
    if (
        !SM_GOING_BACK &&
        currentView &&
        currentView !== view
    ) {
        SM_VIEW_HISTORY.push(currentView);
    }

    smBaseShowView(view);
    smCloseAllMenus();

    // Add Back button to every non-home view.
    smAddGlobalBackButton(view);

    const cat = SM_CATEGORIES.find(c => c.view === view);

    if (cat) {
        smLoadCategory(cat.key);
        return;
    }

    switch (view) {
        case "home":
            smRenderHomeSaved();
            smLoadRecentlyViewed();
            smRenderApplications();
            smRefreshStats();
            break;

        case "dashboard":
            smRefreshStats();
            break;

        case "find-scholarship":
            smLoadEligible().then(smRefreshStats);
            break;

        case "saved":
            smRenderSaved();
            break;

        case "applications":
            smRenderApplications();
            break;

        case "profile":
            smLoadDocuments();
            smRenderProfileCompletion();
            break;

        default:
            break;
    }
};

/* ---------------- boot ---------------- */

async function smBootStudentPortal() {
    smRenderUserChip();
    smRenderHomeCategories();

    await smLoadProfile();
    await smLoadCertTypes();
    await smLoadDocuments();
    await smLoadBookmarks();
    await smLoadNotifications();
    await smLoadEligible();

    // Warm the category counts shown on Home.
    await Promise.all(SM_CATEGORIES.map(c => smLoadCategory(c.key)));

    await smRenderApplications();
    await smRenderHomeSaved();
    await smLoadRecentlyViewed();

    smRefreshStats();
    smRenderProfileCompletion();
}

smInitShell();
smBootStudentPortal();