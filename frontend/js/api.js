/* =========================================================
   ScholarMatch — Real Backend API Client
   Talks to the Spring Boot + MySQL backend. Every function
   here maps 1:1 to an endpoint that actually exists in the
   Spring Boot controllers.

   Response envelope from backend: { success, message, data }
   ========================================================= */

const SM_API_BASE = window.SM_API_BASE || "http://localhost:8080/api";

const SM_LOGIN_PAGES = {
    student: "student-login.html",
    admin: "admin-login.html",
    institution: "institution-login.html"
};

/* ---------- session (role, profile, JWT) ---------- */
const SM_SESSION_KEY = "sm_session";
function smSaveSession(role, profile, token){
    localStorage.setItem(SM_SESSION_KEY, JSON.stringify({ role, profile, token }));
}
function smGetSession(){
    try{ return JSON.parse(localStorage.getItem(SM_SESSION_KEY)); }catch(e){ return null; }
}
function smGetToken(){
    const s = smGetSession();
    return s ? s.token : null;
}
function smClearSession(){ localStorage.removeItem(SM_SESSION_KEY); }

function smHandleUnauthorized(){
    const session = smGetSession();
    const loginPage = SM_LOGIN_PAGES[session && session.role] || "student-login.html";
    smClearSession();
    if (!window.location.pathname.endsWith(loginPage)) {
        window.location.href = loginPage;
    }
}

/* ---------- low-level fetch helper ---------- */
async function smApiCall(method, path, body){
    let res;
    const token = smGetToken();
    const headers = {};
    if (body !== undefined) headers["Content-Type"] = "application/json";
    if (token) headers["Authorization"] = "Bearer " + token;

    try{
        res = await fetch(SM_API_BASE + path, {
            method,
            headers: Object.keys(headers).length ? headers : undefined,
            body: body !== undefined ? JSON.stringify(body) : undefined
        });
    }catch(err){
        throw new Error("Cannot reach the ScholarMatch server. Is the Spring Boot app running on " + SM_API_BASE + "?");
    }

    const isAuthEndpoint = path.startsWith("/auth/");

    // Expired / invalid / missing JWT on a protected endpoint -> force logout.
    if ((res.status === 401 || res.status === 403) && !isAuthEndpoint){
        smHandleUnauthorized();
        throw new Error("Session expired. Please sign in again.");
    }

    if (res.status === 204) return { success: true, message: "OK", data: null };

    let json;
    try{ json = await res.json(); }
    catch(err){ json = null; }

    if (!res.ok){
        const msg = (json && (json.message || json.error)) || ("Request failed (" + res.status + ")");
        throw new Error(msg);
    }
    return json;
}

/* ---------- low-level multipart upload helper ---------- */
async function smUploadFile(path, formData){
    const token = smGetToken();
    const headers = {};
    if (token) headers["Authorization"] = "Bearer " + token;

    let res;
    try{
        res = await fetch(SM_API_BASE + path, { method: "POST", headers, body: formData });
    }catch(err){
        throw new Error("Cannot reach the ScholarMatch server. Is the Spring Boot app running on " + SM_API_BASE + "?");
    }

    if (res.status === 401 || res.status === 403){
        smHandleUnauthorized();
        throw new Error("Session expired. Please sign in again.");
    }

    let json;
    try{ json = await res.json(); }catch(e){ json = null; }

    if (!res.ok){
        const msg = (json && (json.message || json.error)) || ("Upload failed (" + res.status + ")");
        throw new Error(msg);
    }
    return json;
}

const smGet    = (path)       => smApiCall("GET", path);
const smPost   = (path, body) => smApiCall("POST", path, body === undefined ? {} : body);
const smPut    = (path, body) => smApiCall("PUT", path, body);
const smPatch  = (path)       => smApiCall("PATCH", path);
const smDelete = (path)       => smApiCall("DELETE", path);

const smApi = {

    /* ===================== AUTH ===================== */
    async studentLogin(email, password){
        const res = await smPost("/auth/student/login", { email, password });
        const data = res.data;
        const profile = { studentId: data.id, email: data.email, fullName: data.name, accountStatus: "ACTIVE" };
        smSaveSession("student", profile, data.token);
        return profile;
    },
    async adminLogin(email, password){
        const res = await smPost("/auth/admin/login", { email, password });
        const data = res.data;
        const profile = { adminId: data.id, email: data.email, fullName: data.name };
        smSaveSession("admin", profile, data.token);
        return profile;
    },
    async institutionLogin(email, password){
        const res = await smPost("/auth/institution/login", { email, password });
        const data = res.data;
        const profile = { institutionId: data.id, email: data.email, institutionName: data.name, verificationStatus: "APPROVED" };
        smSaveSession("institution", profile, data.token);
        return profile;
    },
    forgotPassword: (email, role) => smPost("/auth/forgot-password", { email, role }),
    resetPassword: (token, newPassword) => smPost("/auth/reset-password", { token, newPassword }),

    /* ===================== STUDENT ===================== */
    registerStudent: (payload)         => smPost("/students", payload),
    getStudent:       (id)             => smGet(`/students/${id}`),
    updateStudent:    (id, payload)    => smPut(`/students/${id}`, payload),
    suspendStudent:   (id)             => smPatch(`/students/${id}/suspend`),
    activateStudent:  (id)             => smPatch(`/students/${id}/activate`),
    deleteStudent:    (id)             => smDelete(`/students/${id}`),
    getAllStudents:   ()               => smGet("/students"),

    getAcademicInfo:    (studentId)          => smGet(`/students/${studentId}/academic-info`),
    saveAcademicInfo:   (studentId, payload) => smPost(`/students/${studentId}/academic-info`, payload),
    updateAcademicInfo: (studentId, payload) => smPut(`/students/${studentId}/academic-info`, payload),

    getFinancialInfo:    (studentId)          => smGet(`/students/${studentId}/financial-info`),
    saveFinancialInfo:   (studentId, payload) => smPost(`/students/${studentId}/financial-info`, payload),
    updateFinancialInfo: (studentId, payload) => smPut(`/students/${studentId}/financial-info`, payload),

    getSpecialStatus:    (studentId)          => smGet(`/students/${studentId}/special-status`),
    saveSpecialStatus:   (studentId, payload) => smPost(`/students/${studentId}/special-status`, payload),
    updateSpecialStatus: (studentId, payload) => smPut(`/students/${studentId}/special-status`, payload),

    /* ===================== SCHOLARSHIPS ===================== */
    getScholarship:         (id, trackView=true) => smGet(`/scholarships/${id}?trackView=${trackView}`),
    getAllScholarships:      (params={})          => {
        const q = new URLSearchParams(params).toString();
        return smGet("/scholarships" + (q ? "?" + q : ""));
    },
    getActiveApprovedScholarships: () => smApi.getAllScholarships({ activeOnly: true }),
    getScholarshipsByStatus:       (status) => smApi.getAllScholarships({ status }),
    getScholarshipsByInstitution:  (institutionId) => smGet(`/scholarships/institution/${institutionId}`),
    createScholarshipByAdmin:      (adminId, payload) => smPost(`/scholarships/admin/${adminId}`, payload),
    submitScholarshipByInstitution:(institutionId, payload) => smPost(`/scholarships/institution/${institutionId}`, payload),
    updateScholarship:             (id, payload) => smPut(`/scholarships/${id}`, payload),
    approveScholarship:            (id, adminId) => smPatch(`/scholarships/${id}/approve?adminId=${adminId}`),
    rejectScholarship:             (id, adminId, reason) => smPatch(`/scholarships/${id}/reject?adminId=${adminId}&reason=${encodeURIComponent(reason)}`),
    deactivateScholarship:         (id) => smPatch(`/scholarships/${id}/deactivate`),
    activateScholarship:           (id) => smPatch(`/scholarships/${id}/activate`),
    deleteScholarship:             (id) => smDelete(`/scholarships/${id}`),

    searchScholarships: (params={}) => {
        const q = new URLSearchParams(Object.fromEntries(Object.entries(params).filter(([,v]) => v !== "" && v != null))).toString();
        return smGet("/scholarships/search" + (q ? "?" + q : ""));
    },

    extractScholarshipWithAI:      (description) => smPost("/ai/extract-scholarship", { description }),

    getRequiredDocuments: (scholarshipId) => smGet(`/scholarships/${scholarshipId}/required-documents`),
    addRequiredDocument:  (scholarshipId, payload) => smPost(`/scholarships/${scholarshipId}/required-documents`, payload),
    removeRequiredDocument: (scholarshipId, certificateTypeId) => smDelete(`/scholarships/${scholarshipId}/required-documents/${certificateTypeId}`),

    recordScholarshipView: (scholarshipId, studentId) => smPost(`/scholarships/${scholarshipId}/views${studentId ? "?studentId=" + studentId : ""}`),
    getScholarshipViews:   (scholarshipId) => smGet(`/scholarships/${scholarshipId}/views`),
    getRecentScholarshipViews: (limit=10) => smGet(`/scholarships/views/recent?limit=${limit}`),
    getMostViewedScholarships: (limit=10) => smGet(`/scholarships/most-viewed?limit=${limit}`),
    getRecentSearches: (limit=10) => smGet(`/search-log/recent?limit=${limit}`),
    getUpcomingDeadlines: (days=30, limit=10) => {
        const d = new Date(); d.setDate(d.getDate() + days);
        return smGet(`/scholarships/search?deadlineBefore=${d.toISOString().slice(0,10)}&sortBy=deadline&sortDir=asc`);
    },

    /* ===================== CATEGORY LISTING ===================== */
    // Backend: GET /api/scholarships?primaryCategory=WOMENS (active + approved + non-expired only)
    getScholarshipsByCategory: (category) => smApi.getAllScholarships({ primaryCategory: category }),

    /* ===================== RECENTLY VIEWED ===================== */
    // Backend: GET /api/scholarships/views/student/{studentId} — deduplicated, newest first
    getStudentRecentlyViewed: (studentId) => smGet(`/scholarships/views/student/${studentId}`),

    /* ===================== PROFILE PHOTO ===================== */
    uploadStudentPhoto: (studentId, file) => {
        const formData = new FormData();
        formData.append("file", file);
        return smUploadFile(`/students/${studentId}/photo`, formData);
    },

    /* The uploads folder is not public, so the photo is fetched with the JWT
       and turned into an object URL for <img src>. Returns null if none exists. */
    async getStudentPhotoObjectUrl(studentId){
        const token = smGetToken();
        const headers = {};
        if (token) headers["Authorization"] = "Bearer " + token;
        try{
            const res = await fetch(`${SM_API_BASE}/students/${studentId}/photo`, { headers });
            if (res.status === 401 || res.status === 403){ smHandleUnauthorized(); return null; }
            if (!res.ok) return null;
            const blob = await res.blob();
            return URL.createObjectURL(blob);
        }catch(err){
            return null;
        }
    },

    /* ===================== MATCH SCORE / ELIGIBILITY ===================== */
    // Backend: GET /api/students/{id}/scholarships/{id}/eligibility-details
    getScholarshipEligibilityDetails: (studentId, scholarshipId) =>
        smGet(`/students/${studentId}/scholarships/${scholarshipId}/eligibility-details`),

    // Backend: GET /api/students/{id}/eligible-scholarships
    getEligibleScholarships: (studentId) => smGet(`/students/${studentId}/eligible-scholarships`),

    getMatchScores:        (studentId) => smGet(`/students/${studentId}/match-scores`),
    recomputeAllMatchScores:(studentId) => smGet(`/students/${studentId}/match-scores/recompute`),
    getOneMatchScore:      (studentId, scholarshipId) => smGet(`/students/${studentId}/scholarships/${scholarshipId}/match-score`),
    recomputeOneMatchScore:(studentId, scholarshipId) => smGet(`/students/${studentId}/scholarships/${scholarshipId}/match-score/recompute`),

    /* ===================== SAVED SCHOLARSHIPS (BOOKMARKS) =====================
       Saved and Applied are independent on the backend:
         - saveBookmark / removeBookmark only touch saved_at
         - markApplied only sets applied_at (immutable once set)
       Unsaving never clears an existing application.                            */
    getBookmarks:    (studentId) => smGet(`/students/${studentId}/bookmarks`),
    getSavedBookmarks:   (studentId) => smGet(`/students/${studentId}/bookmarks?savedOnly=true`),
    getAppliedScholarships: (studentId) => smGet(`/students/${studentId}/bookmarks?appliedOnly=true`),
    addBookmark:     (studentId, scholarshipId) => smPost(`/students/${studentId}/bookmarks/${scholarshipId}`),
    saveBookmark:    (studentId, scholarshipId) => smPost(`/students/${studentId}/bookmarks/${scholarshipId}`),
    removeBookmark:  (studentId, scholarshipId) => smDelete(`/students/${studentId}/bookmarks/${scholarshipId}`),
    markApplied:     (studentId, scholarshipId) => smPatch(`/students/${studentId}/bookmarks/${scholarshipId}/apply`),

    /* ===================== SEARCH LOG ===================== */
    recordSearch:    (payload) => smPost("/search-log", payload),
    getSearchHistory:(studentId) => smGet(`/search-log/student/${studentId}`),

    /* ===================== STUDENT DOCUMENTS ===================== */
    getStudentDocuments: (studentId) => smGet(`/students/${studentId}/documents`),
    getStudentDocument:  (studentId, documentId) => smGet(`/students/${studentId}/documents/${documentId}`),
    uploadStudentDocument:(studentId, payload) => smPost(`/students/${studentId}/documents`, payload),
    updateStudentDocument:(studentId, documentId, payload) => smPut(`/students/${studentId}/documents/${documentId}`, payload),
    deleteStudentDocument:(studentId, documentId) => smDelete(`/students/${studentId}/documents/${documentId}`),

    uploadStudentDocumentFile: (studentId, certificateTypeId, file, issueDate, expiryDate) => {
        const formData = new FormData();
        formData.append("certificateTypeId", certificateTypeId);
        formData.append("file", file);
        if (issueDate) formData.append("issueDate", issueDate);
        if (expiryDate) formData.append("expiryDate", expiryDate);
        return smUploadFile(`/students/${studentId}/documents/upload`, formData);
    },
    async viewStudentDocumentFile(studentId, documentId){
        const token = smGetToken();
        const headers = {};
        if (token) headers["Authorization"] = "Bearer " + token;
        const res = await fetch(`${SM_API_BASE}/students/${studentId}/documents/${documentId}/file`, { headers });
        if (res.status === 401 || res.status === 403){ smHandleUnauthorized(); throw new Error("Session expired. Please sign in again."); }
        if (!res.ok) {
            let serverMsg = null;
            try { const errJson = await res.json(); serverMsg = errJson && (errJson.message || errJson.error); } catch(e) {}
            throw new Error(serverMsg || ("Could not load document (" + res.status + ")"));
        }
        const blob = await res.blob();
        window.open(URL.createObjectURL(blob), "_blank");
    },

    /* ===================== CERTIFICATE TYPES / STAGES / PORTAL LINKS ===================== */
    getCertificateTypes: () => smGet("/certificate-types"),
    getCertificateStages:(certificateTypeId) => smGet(`/certificate-types/${certificateTypeId}/stages`),
    getPortalLinks:      (certificateTypeId) => smGet(`/certificate-types/${certificateTypeId}/portal-links`),

    /* ===================== NOTIFICATIONS ===================== */
    getStudentNotifications:    (studentId) => smGet(`/notifications/student/${studentId}`),
    getInstitutionNotifications:(institutionId) => smGet(`/notifications/institution/${institutionId}`),
    createNotification:         (payload) => smPost("/notifications", payload),
    markNotificationRead:       (id) => smPatch(`/notifications/${id}/read`),
    markAllNotificationsRead:   (studentId) => smPatch(`/notifications/student/${studentId}/read-all`),
    getUnreadNotificationCount: (studentId) => smGet(`/notifications/student/${studentId}/unread-count`),
    deleteNotification:         (id) => smDelete(`/notifications/${id}`),
    getInstitutionUnreadCount: (institutionId) => smGet(`/notifications/institution/${institutionId}/unread-count`),
    markAllInstitutionNotificationsRead: (institutionId) => smPatch(`/notifications/institution/${institutionId}/read-all`),
    getAdminNotifications: (unreadOnly = false) => smGet(`/notifications/admin?unreadOnly=${unreadOnly}`),
    getAdminUnreadCount: () => smGet(`/notifications/admin/unread-count`),
    markAllAdminNotificationsRead: () => smPatch(`/notifications/admin/read-all`),
    /* ===================== ADMIN ===================== */
    registerAdmin:  (payload) => smPost("/admins", payload),
    getAdmin:       (id) => smGet(`/admins/${id}`),
    getAllAdmins:   () => smGet("/admins"),
    getScholarshipAnalytics: () => smGet("/admins/analytics/scholarships"),
    updateAdmin:    (id, payload) => smPut(`/admins/${id}`, payload),
    deactivateAdmin:(id) => smPatch(`/admins/${id}/deactivate`),
    activateAdmin:  (id) => smPatch(`/admins/${id}/activate`),
    deleteAdmin:    (id) => smDelete(`/admins/${id}`),

    getAdminActivityLog:      () => smGet("/admin-activity-log"),
    getAdminActivityByAdmin:  (adminId) => smGet(`/admin-activity-log/admin/${adminId}`),
    recordAdminActivity:      (payload) => smPost("/admin-activity-log", payload),

    /* ===================== INSTITUTION ===================== */
    registerInstitution: (payload) => smPost("/institutions", payload),
    getInstitution:      (id) => smGet(`/institutions/${id}`),
    getAllInstitutions:  (status) => smGet("/institutions" + (status ? `?status=${status}` : "")),
    updateInstitution:   (id, payload) => smPut(`/institutions/${id}`, payload),
    deleteInstitution:   (id) => smDelete(`/institutions/${id}`),

    getInstitutionContacts: (institutionId) => smGet(`/institutions/${institutionId}/contacts`),
    addInstitutionContact:  (institutionId, payload) => smPost(`/institutions/${institutionId}/contacts`, payload),
    updateInstitutionContact:(institutionId, contactId, payload) => smPut(`/institutions/${institutionId}/contacts/${contactId}`, payload),
    removeInstitutionContact:(institutionId, contactId) => smDelete(`/institutions/${institutionId}/contacts/${contactId}`),

    getInstitutionDocuments: (institutionId) => smGet(`/institutions/${institutionId}/documents`),
    uploadInstitutionDocument:(institutionId, payload) => smPost(`/institutions/${institutionId}/documents`, payload),
    removeInstitutionDocument:(institutionId, documentId) => smDelete(`/institutions/${institutionId}/documents/${documentId}`),

    uploadInstitutionDocumentFile: (institutionId, documentType, file) => {
        const formData = new FormData();
        formData.append("documentType", documentType);
        formData.append("file", file);
        return smUploadFile(`/institutions/${institutionId}/documents/upload`, formData);
    },
    async viewInstitutionDocumentFile(institutionId, documentId){
        const token = smGetToken();
        const headers = {};
        if (token) headers["Authorization"] = "Bearer " + token;
        const res = await fetch(`${SM_API_BASE}/institutions/${institutionId}/documents/${documentId}/file`, { headers });
        if (res.status === 401 || res.status === 403){ smHandleUnauthorized(); throw new Error("Session expired. Please sign in again."); }
        if (!res.ok) throw new Error("Could not load document (" + res.status + ")");
        const blob = await res.blob();
        window.open(URL.createObjectURL(blob), "_blank");
    },

    submitInstitutionForVerification: (institutionId) => smPost(`/institutions/${institutionId}/verification-log/submit`),
    decideInstitutionVerification:    (institutionId, adminId, action, reason) =>
        smPost(`/institutions/${institutionId}/verification-log/decision`, { adminId, action, reason }),
    getInstitutionVerificationLog:    (institutionId) => smGet(`/institutions/${institutionId}/verification-log`)
};