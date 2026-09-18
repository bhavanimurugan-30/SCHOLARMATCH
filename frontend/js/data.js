
/* =========================================================
   ScholarMatch — Sample Data Layer
   Shaped to mirror the MySQL schema (student, scholarship,
   scholarship_match_score, institution, certificate_type, etc.)
   ========================================================= */

const SM_DATA = {

    currentStudent: {
        studentId: 101,
        fullName: "Priya Ramanathan",
        email: "priya.r@example.com",
        category: "OBC",
        state: "Tamil Nadu",
        district: "Madurai",
        accountStatus: "ACTIVE",
        academic: {
            educationLevel: "UNDERGRADUATE",
            courseName: "B.Tech Computer Science",
            institutionName: "Sri Lakshmi Engineering College",
            currentYearSemester: "3rd Year",
            qualifyingExamPercentage: 84.5
        },
        financial: {
            annualFamilyIncome: 268000,
            bplStatus: false
        },
        specialStatus: {
            singleGirlChild: false,
            minorityCommunity: false,
            sportsQuota: true
        }
    },

    currentAdmin: {
        adminId: 1,
        fullName: "Arun Mehta",
        email: "arun.admin@scholarmatch.in",
        role: "SUPER_ADMIN"
    },

    currentInstitution: {
        institutionId: 501,
        institutionName: "Sri Lakshmi Engineering College",
        institutionType: "College",
        email: "scholarships@slec.edu.in",
        verificationStatus: "APPROVED"
    },

    scholarships: [
        {
            scholarshipId: 1,
            title: "National Merit-cum-Means Scholarship",
            sourceType: "GOVERNMENT",
            fundingBodyName: "Ministry of Education, Govt. of India",
            amount: 12000,
            description:
                "Financial assistance for meritorious students from economically weaker sections to pursue undergraduate studies.",
            eligibleCategories: [
                "GENERAL",
                "OBC",
                "SC",
                "ST",
                "EWS"
            ],
            minAnnualIncome: null,
            maxAnnualIncome: 350000,
            eligibleCourses: [
                "B.Tech",
                "B.Sc",
                "B.A",
                "B.Com"
            ],
            educationLevel: "UNDERGRADUATE",
            minMarksCgpa: 80,
            eligibleStates: [],
            eligibleSpecialStatus: [],
            deadline: "2026-09-18",
            requiredDocuments: [
                "Aadhaar Card",
                "Income Certificate",
                "Latest Marksheet",
                "Bank Passbook copy"
            ],
            applyLink: "https://scholarships.gov.in",
            matchPercentage: 95
        },

        {
            scholarshipId: 2,
            title: "AICTE Pragati Scholarship for Girls",
            sourceType: "GOVERNMENT",
            fundingBodyName: "AICTE",
            amount: 50000,
            description:
                "Supports girl students pursuing technical diploma or degree courses at AICTE-approved institutions.",
            eligibleCategories: [],
            minAnnualIncome: null,
            maxAnnualIncome: 800000,
            eligibleCourses: [
                "B.Tech",
                "Diploma Engineering"
            ],
            educationLevel: "UNDERGRADUATE",
            minMarksCgpa: null,
            eligibleStates: [],
            eligibleSpecialStatus: [
                "SINGLE_GIRL_CHILD"
            ],
            deadline: "2026-09-05",
            requiredDocuments: [
                "Aadhaar Card",
                "Income Certificate",
                "Bonafide Student Certificate"
            ],
            applyLink:
                "https://www.aicte-pragati-saksham-gov.in",
            matchPercentage: 62
        },

        {
            scholarshipId: 3,
            title: "Tamil Nadu State Merit Scholarship",
            sourceType: "GOVERNMENT",
            fundingBodyName: "Govt. of Tamil Nadu",
            amount: 15000,
            description:
                "State-funded merit scholarship for students domiciled in Tamil Nadu enrolled in recognized institutions.",
            eligibleCategories: [
                "OBC",
                "SC",
                "ST",
                "EWS"
            ],
            minAnnualIncome: null,
            maxAnnualIncome: 250000,
            eligibleCourses: [],
            educationLevel: "UNDERGRADUATE",
            minMarksCgpa: 75,
            eligibleStates: [
                "Tamil Nadu"
            ],
            eligibleSpecialStatus: [],
            deadline: "2026-09-02",
            requiredDocuments: [
                "Domicile Certificate",
                "Income Certificate",
                "Latest Marksheet"
            ],
            applyLink:
                "https://tnscholarship.tn.gov.in",
            matchPercentage: 88
        },

        {
            scholarshipId: 4,
            title: "Reliance Foundation Undergraduate Scholarship",
            sourceType: "PRIVATE_CORPORATE",
            fundingBodyName: "Reliance Foundation",
            amount: 200000,
            description:
                "Multi-year scholarship for meritorious undergraduate students demonstrating financial need.",
            eligibleCategories: [],
            minAnnualIncome: null,
            maxAnnualIncome: 1500000,
            eligibleCourses: [],
            educationLevel: "UNDERGRADUATE",
            minMarksCgpa: 85,
            eligibleStates: [],
            eligibleSpecialStatus: [],
            deadline: "2026-10-30",
            requiredDocuments: [
                "Latest Marksheet",
                "Income Certificate",
                "Passport-size Photo"
            ],
            applyLink:
                "https://scholarships.reliancefoundation.org",
            matchPercentage: 71
        },

        {
            scholarshipId: 5,
            title: "SLEC Merit Fee Concession",
            sourceType: "INSTITUTION",
            fundingBodyName: "Sri Lakshmi Engineering College",
            amount: 40000,
            description:
                "Institution-funded fee concession for top-ranking students within the college each semester.",
            eligibleCategories: [],
            minAnnualIncome: null,
            maxAnnualIncome: null,
            eligibleCourses: [
                "B.Tech Computer Science"
            ],
            educationLevel: "UNDERGRADUATE",
            minMarksCgpa: 80,
            eligibleStates: [],
            eligibleSpecialStatus: [],
            deadline: "2026-09-25",
            requiredDocuments: [
                "Latest Marksheet",
                "Bonafide Student Certificate"
            ],
            applyLink: "#",
            matchPercentage: 90
        },

        {
            scholarshipId: 6,
            title: "Minority Community Post-Matric Scholarship",
            sourceType: "GOVERNMENT",
            fundingBodyName: "Ministry of Minority Affairs",
            amount: 18000,
            description:
                "For students from notified minority communities pursuing post-matric education.",
            eligibleCategories: [
                "MINORITY"
            ],
            minAnnualIncome: null,
            maxAnnualIncome: 200000,
            eligibleCourses: [],
            educationLevel: "UNDERGRADUATE",
            minMarksCgpa: null,
            eligibleStates: [],
            eligibleSpecialStatus: [
                "MINORITY_COMMUNITY"
            ],
            deadline: "2026-08-30",
            requiredDocuments: [
                "Income Certificate",
                "Minority Certificate"
            ],
            applyLink:
                "https://scholarships.gov.in",
            matchPercentage: 18
        },

        {
            scholarshipId: 7,
            title: "L&T Build India Scholarship",
            sourceType: "PRIVATE_CORPORATE",
            fundingBodyName: "Larsen & Toubro",
            amount: 100000,
            description:
                "For engineering students showing academic excellence and leadership potential.",
            eligibleCategories: [],
            minAnnualIncome: null,
            maxAnnualIncome: 600000,
            eligibleCourses: [
                "B.Tech"
            ],
            educationLevel: "UNDERGRADUATE",
            minMarksCgpa: 82,
            eligibleStates: [],
            eligibleSpecialStatus: [],
            deadline: "2026-11-15",
            requiredDocuments: [
                "Latest Marksheet",
                "Income Certificate"
            ],
            applyLink:
                "https://www.lntbuildindiascholarship.com",
            matchPercentage: 84
        },

        {
            scholarshipId: 8,
            title: "AICTE Saksham Scholarship (Divyangjan)",
            sourceType: "GOVERNMENT",
            fundingBodyName: "AICTE",
            amount: 50000,
            description:
                "For differently-abled students pursuing technical education at AICTE-approved institutes.",
            eligibleCategories: [],
            minAnnualIncome: null,
            maxAnnualIncome: 800000,
            eligibleCourses: [],
            educationLevel: "UNDERGRADUATE",
            minMarksCgpa: null,
            eligibleStates: [],
            eligibleSpecialStatus: [],
            deadline: "2026-09-10",
            requiredDocuments: [
                "Disability Certificate",
                "Income Certificate"
            ],
            applyLink:
                "https://www.aicte-pragati-saksham-gov.in",
            matchPercentage: 12
        }
    ],

    /*
     * Saved scholarships are still sample data.
     * Real bookmark/application state comes from the backend.
     */
    savedScholarshipIds: [
        1,
        3,
        5
    ],

    /*
     * Sample application data.
     * Real application data should come from the backend.
     */
    applications: [
        {
            scholarshipId: 3,
            status: "IN_PROGRESS",
            updatedAt: "2026-08-22",
            note: "Awaiting domicile certificate upload"
        },
        {
            scholarshipId: 1,
            status: "SUBMITTED",
            updatedAt: "2026-08-18",
            note: "Submitted on official NSP portal"
        },
        {
            scholarshipId: 5,
            status: "APPROVED",
            updatedAt: "2026-08-10",
            note: "Fee concession applied for Sem 5"
        }
    ],

    documents: [
        {
            name: "Aadhaar Card",
            type: "Identity",
            status: "VALID",
            uploadedOn: "2026-02-10",
            expiry: null
        },
        {
            name: "Latest Marksheet",
            type: "Academic",
            status: "VALID",
            uploadedOn: "2026-06-01",
            expiry: null
        },
        {
            name: "Income Certificate",
            type: "Financial",
            status: "EXPIRING_SOON",
            uploadedOn: "2025-09-14",
            expiry: "2026-09-14"
        },
        {
            name: "Caste / Category Certificate",
            type: "Category",
            status: "VALID",
            uploadedOn: "2025-11-02",
            expiry: null
        },
        {
            name: "Domicile Certificate",
            type: "Residence",
            status: "EXPIRED",
            uploadedOn: "2024-07-19",
            expiry: "2025-07-19"
        },
        {
            name: "Bank Passbook Copy",
            type: "Financial",
            status: "MISSING",
            uploadedOn: null,
            expiry: null
        }
    ],

    certificateGuide: [
        {
            name: "Income Certificate",
            authority: "State e-District Portal",
            days: "7–21 days"
        },
        {
            name: "Domicile Certificate",
            authority: "State e-District Portal",
            days: "7–21 days"
        },
        {
            name: "Caste / Category Certificate",
            authority: "State e-District",
            days: "7–15 days"
        },
        {
            name: "Passport",
            authority: "Passport Seva Kendra",
            days: "30–45 days (Tatkaal: 24–72 hrs)"
        }
    ],

    /*
     * IMPORTANT:
     *
     * Notifications are intentionally NOT stored here anymore.
     *
     * Student notifications must come from:
     *
     * GET /api/notifications/student/{studentId}
     *
     * Unread count must come from:
     *
     * GET /api/notifications/student/{studentId}/unread-count
     *
     * This prevents old hardcoded notifications from appearing
     * instead of the real database notifications.
     */

    institutions: [
        {
            id: 501,
            name: "Sri Lakshmi Engineering College",
            type: "College",
            state: "Tamil Nadu",
            status: "APPROVED",
            submitted: "2026-06-02"
        },
        {
            id: 502,
            name: "Bright Future Coaching Centre",
            type: "Coaching",
            state: "Karnataka",
            status: "PENDING",
            submitted: "2026-08-20"
        },
        {
            id: 503,
            name: "Deccan Institute of Technology",
            type: "College",
            state: "Telangana",
            status: "PENDING",
            submitted: "2026-08-24"
        },
        {
            id: 504,
            name: "Unity Welfare NGO",
            type: "NGO",
            state: "Maharashtra",
            status: "REJECTED",
            submitted: "2026-08-11"
        },
        {
            id: 505,
            name: "St. Xavier's Degree College",
            type: "College",
            state: "Kerala",
            status: "APPROVED",
            submitted: "2026-05-28"
        }
    ],

    adminScholarshipQueue: [
        {
            id: 9,
            title: "Deccan Merit Scholarship 2026",
            institution: "Deccan Institute of Technology",
            amount: 25000,
            submitted: "2026-08-25",
            status: "PENDING_APPROVAL"
        },
        {
            id: 10,
            title: "Xavier's Need-Based Grant",
            institution: "St. Xavier's Degree College",
            amount: 30000,
            submitted: "2026-08-23",
            status: "PENDING_APPROVAL"
        }
    ],

    institutionOwnScholarships: [
        {
            id: 5,
            title: "SLEC Merit Fee Concession",
            amount: 40000,
            deadline: "2026-09-25",
            status: "APPROVED",
            views: 214,
            matched: 38
        },
        {
            id: 11,
            title: "SLEC Sports Excellence Grant",
            amount: 20000,
            deadline: "2026-10-05",
            status: "PENDING_APPROVAL",
            views: 0,
            matched: 0
        },
        {
            id: 12,
            title: "SLEC Alumni Legacy Award",
            amount: 15000,
            deadline: "2026-07-01",
            status: "REJECTED",
            views: 45,
            matched: 9,
            reason: "Missing eligibility documentation"
        }
    ],

    analytics: {
        totalStudents: 4820,
        activeScholarships: 132,
        totalInstitutions: 68,
        applicationsThisMonth: 1046,

        monthlySignups: [
            320,
            410,
            380,
            500,
            610,
            740,
            690,
            820,
            910,
            1046
        ],

        monthLabels: [
            "Nov",
            "Dec",
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug"
        ],

        sourceSplit: {
            Government: 58,
            "Private / Corporate": 34,
            Institution: 26,
            NGO: 14
        },

        topScholarships: [
            {
                title: "National Merit-cum-Means Scholarship",
                views: 3120,
                matches: 1890
            },
            {
                title: "Tamil Nadu State Merit Scholarship",
                views: 2440,
                matches: 1510
            },
            {
                title: "AICTE Pragati Scholarship for Girls",
                views: 2010,
                matches: 990
            },
            {
                title: "L&T Build India Scholarship",
                views: 1780,
                matches: 860
            }
        ]
    }
};


/* =========================================================
   Helpers shared across pages
   ========================================================= */

function smDaysLeft(dateStr) {

    const d = new Date(dateStr);

    /*
     * Sample-data date is kept fixed so the existing demo
     * scholarship cards behave consistently.
     */
    const now = new Date("2026-08-29");

    return Math.ceil(
        (d - now) / 86400000
    );
}


function smDeadlineBadge(dateStr) {

    const days = smDaysLeft(dateStr);

    if (days < 0) {
        return {
            cls: "neutral",
            label: "Closed"
        };
    }

    if (days <= 7) {
        return {
            cls: "dl-urgent",
            label:
                days === 0
                    ? "Closes today"
                    : days + " days left"
        };
    }

    if (days <= 30) {
        return {
            cls: "dl-soon",
            label: days + " days left"
        };
    }

    return {
        cls: "dl-open",
        label: days + " days left"
    };
}


function smFormatDate(dateStr) {

    return new Date(dateStr).toLocaleDateString(
        "en-IN",
        {
            day: "numeric",
            month: "short",
            year: "numeric"
        }
    );
}


function smCurrency(n) {

    return "₹" +
        Number(n).toLocaleString("en-IN");
}


function smSourceTagClass(sourceType) {

    if (sourceType === "GOVERNMENT") {
        return "gov";
    }

    if (sourceType === "INSTITUTION") {
        return "inst";
    }

    return "private";
}


function smSourceLabel(sourceType) {

    return {
        GOVERNMENT: "Government",
        PRIVATE_CORPORATE: "Private",
        PRIVATE_NGO: "NGO",
        INSTITUTION: "Institution"
    }[sourceType] || sourceType;
}