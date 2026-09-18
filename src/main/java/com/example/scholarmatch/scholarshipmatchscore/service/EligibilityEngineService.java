package com.example.scholarmatch.scholarshipmatchscore.service;

import com.example.scholarmatch.academicinfo.model.AcademicInfo;
import com.example.scholarmatch.academicinfo.repository.AcademicInfoRepository;
import com.example.scholarmatch.bookmark.repository.BookmarkRepository;
import com.example.scholarmatch.certificateportallink.model.CertificatePortalLink;
import com.example.scholarmatch.certificateportallink.repository.CertificatePortalLinkRepository;
import com.example.scholarmatch.certificatetype.model.CertificateType;
import com.example.scholarmatch.certificatetype.repository.CertificateTypeRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.scholarshipmatchscore.dto.CertificateStatusDetail;
import com.example.scholarmatch.scholarshipmatchscore.dto.EligibilityDetailsResponse;
import com.example.scholarmatch.scholarshiprequireddocument.model.ScholarshipRequiredDocument;
import com.example.scholarmatch.scholarshiprequireddocument.repository.ScholarshipRequiredDocumentRepository;
import com.example.scholarmatch.studentdocument.model.StudentDocument;
import com.example.scholarmatch.studentdocument.repository.StudentDocumentRepository;
import com.example.scholarmatch.financialinfo.model.FinancialInfo;
import com.example.scholarmatch.financialinfo.repository.FinancialInfoRepository;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.scholarshipmatchscore.model.ScholarshipMatchScore;
import com.example.scholarmatch.scholarshipmatchscore.repository.ScholarshipMatchScoreRepository;
import com.example.scholarmatch.specialstatus.model.SpecialStatus;
import com.example.scholarmatch.specialstatus.repository.SpecialStatusRepository;
import com.example.scholarmatch.student.model.Student;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;
import com.example.scholarmatch.ai.service.AIService;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EligibilityEngineService {

    // Weights exactly as specified in Section 5.1
    private static final double WEIGHT_CATEGORY = 25.0;
    private static final double WEIGHT_INCOME = 25.0;
    private static final double WEIGHT_COURSE = 20.0;
    private static final double WEIGHT_MARKS = 15.0;
    private static final double WEIGHT_STATE = 10.0;
    private static final double WEIGHT_SPECIAL_STATUS = 5.0;

    /*
     * Course aliases.
     * This prevents valid students from being rejected because
     * the student profile and scholarship use different course names.
     */
    private static final Map<String, String> COURSE_ALIASES = new HashMap<>();

    static {
        COURSE_ALIASES.put("CSE", "COMPUTER SCIENCE ENGINEERING");
        COURSE_ALIASES.put("COMPUTER SCIENCE", "COMPUTER SCIENCE ENGINEERING");
        COURSE_ALIASES.put("COMPUTER SCIENCE AND ENGINEERING", "COMPUTER SCIENCE ENGINEERING");

        COURSE_ALIASES.put("ECE", "ELECTRONICS COMMUNICATION ENGINEERING");
        COURSE_ALIASES.put(
                "ELECTRONICS AND COMMUNICATION ENGINEERING",
                "ELECTRONICS COMMUNICATION ENGINEERING"
        );

        COURSE_ALIASES.put("EEE", "ELECTRICAL ELECTRONICS ENGINEERING");
        COURSE_ALIASES.put(
                "ELECTRICAL AND ELECTRONICS ENGINEERING",
                "ELECTRICAL ELECTRONICS ENGINEERING"
        );

        COURSE_ALIASES.put("IT", "INFORMATION TECHNOLOGY");
        COURSE_ALIASES.put("INFORMATION TECHNOLOGY", "INFORMATION TECHNOLOGY");
    }

    private final StudentRepository studentRepository;
    private final AcademicInfoRepository academicInfoRepository;
    private final FinancialInfoRepository financialInfoRepository;
    private final SpecialStatusRepository specialStatusRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final ScholarshipMatchScoreRepository matchScoreRepository;
    private final AIService aiService;
    private final ScholarshipRequiredDocumentRepository requiredDocumentRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final CertificateTypeRepository certificateTypeRepository;
    private final CertificatePortalLinkRepository certificatePortalLinkRepository;
    private final BookmarkRepository bookmarkRepository;

    public EligibilityEngineService(
            StudentRepository studentRepository,
            AcademicInfoRepository academicInfoRepository,
            FinancialInfoRepository financialInfoRepository,
            SpecialStatusRepository specialStatusRepository,
            ScholarshipRepository scholarshipRepository,
            ScholarshipMatchScoreRepository matchScoreRepository,
            AIService aiService,
            ScholarshipRequiredDocumentRepository requiredDocumentRepository,
            StudentDocumentRepository studentDocumentRepository,
            CertificateTypeRepository certificateTypeRepository,
            CertificatePortalLinkRepository certificatePortalLinkRepository,
            BookmarkRepository bookmarkRepository) {

        this.studentRepository = studentRepository;
        this.academicInfoRepository = academicInfoRepository;
        this.financialInfoRepository = financialInfoRepository;
        this.specialStatusRepository = specialStatusRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.matchScoreRepository = matchScoreRepository;
        this.aiService = aiService;
        this.requiredDocumentRepository = requiredDocumentRepository;
        this.studentDocumentRepository = studentDocumentRepository;
        this.certificateTypeRepository = certificateTypeRepository;
        this.certificatePortalLinkRepository = certificatePortalLinkRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    /**
     * Computes (and upserts) the match score for one student against one scholarship.
     */
    public ScholarshipMatchScore computeAndSave(Long studentId, Long scholarshipId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId));

        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Scholarship not found with id: " + scholarshipId));

        Optional<AcademicInfo> academicInfo =
                academicInfoRepository.findByStudentId(studentId);

        Optional<FinancialInfo> financialInfo =
                financialInfoRepository.findByStudentId(studentId);

        Optional<SpecialStatus> specialStatus =
                specialStatusRepository.findByStudentId(studentId);

        List<String> matched = new ArrayList<>();
        List<String> unmatched = new ArrayList<>();
        double totalScore = 0.0;

        // 1. Category — 25%
        if (isCategoryEligible(student, scholarship)) {
            matched.add("Category");
            totalScore += WEIGHT_CATEGORY;
        } else {
            unmatched.add("Category");
        }

        // 2. Annual family income — 25%
        if (isIncomeEligible(financialInfo, scholarship)) {
            matched.add("Annual Family Income");
            totalScore += WEIGHT_INCOME;
        } else {
            unmatched.add("Annual Family Income");
        }

        // 3. Course / stream match — 20%
        if (isCourseEligible(academicInfo, scholarship)) {
            matched.add("Course / Stream");
            totalScore += WEIGHT_COURSE;
        } else {
            unmatched.add("Course / Stream");
        }

        // 4. Marks / CGPA threshold — 15%
        if (isMarksEligible(academicInfo, scholarship)) {
            matched.add("Marks / CGPA");
            totalScore += WEIGHT_MARKS;
        } else {
            unmatched.add("Marks / CGPA");
        }

        // 5. State / domicile — 10%
        if (isStateEligible(student, scholarship)) {
            matched.add("State / Domicile");
            totalScore += WEIGHT_STATE;
        } else {
            unmatched.add("State / Domicile");
        }

        // 6. Special status — 5%
        if (isSpecialStatusEligible(specialStatus, scholarship)) {
            matched.add("Special Status");
            totalScore += WEIGHT_SPECIAL_STATUS;
        } else {
            unmatched.add("Special Status");
        }

        ScholarshipMatchScore result = new ScholarshipMatchScore();

        result.setStudentId(studentId);
        result.setScholarshipId(scholarshipId);
        result.setMatchPercentage(totalScore);
        result.setMatchedCriteria(matched);
        result.setUnmatchedCriteria(unmatched);

        String fallbackExplanation =
                buildExplanation(matched, unmatched, totalScore);

        result.setAiExplanationText(
                aiService.generateEligibilityExplanation(
                        student,
                        scholarship,
                        matched,
                        unmatched,
                        totalScore,
                        fallbackExplanation
                )
        );

        return matchScoreRepository.upsert(result);
    }

    /**
     * Recomputes match scores for a student against every active/approved scholarship.
     */
    public List<ScholarshipMatchScore> computeAllForStudent(Long studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId));

        List<Scholarship> activeScholarships =
                scholarshipRepository.findActiveApproved();

        for (Scholarship scholarship : activeScholarships) {
            computeAndSave(studentId, scholarship.getScholarshipId());
        }

        return matchScoreRepository
                .findByStudentIdOrderByMatchDesc(studentId);
    }

    public ScholarshipMatchScore getExisting(
            Long studentId,
            Long scholarshipId) {

        return matchScoreRepository
                .findByStudentIdAndScholarshipId(studentId, scholarshipId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No match score has been computed yet for this student and scholarship"));
    }

    public List<ScholarshipMatchScore> getForStudent(Long studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId));

        return matchScoreRepository
                .findByStudentIdOrderByMatchDesc(studentId);
    }

    // ================= structured eligibility details =================

    private static final DecimalFormat MONEY =
            new DecimalFormat("#,##,##0");

    private static final DecimalFormat PCT =
            new DecimalFormat("0.##");

    /**
     * Full eligibility breakdown for one student against one scholarship.
     */
    public EligibilityDetailsResponse getEligibilityDetails(
            Long studentId,
            Long scholarshipId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId));

        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Scholarship not found with id: " + scholarshipId));

        Optional<AcademicInfo> academicInfo =
                academicInfoRepository.findByStudentId(studentId);

        Optional<FinancialInfo> financialInfo =
                financialInfoRepository.findByStudentId(studentId);

        Optional<SpecialStatus> specialStatus =
                specialStatusRepository.findByStudentId(studentId);

        EligibilityDetailsResponse res =
                new EligibilityDetailsResponse();

        res.setStudentId(studentId);
        res.setScholarshipId(scholarshipId);
        res.setScholarshipTitle(scholarship.getTitle());
        res.setPrimaryCategory(scholarship.getPrimaryCategory());
        res.setFundingBodyName(scholarship.getFundingBodyName());
        res.setAmount(scholarship.getAmount());
        res.setDeadline(scholarship.getDeadline());
        res.setOfficialApplicationLink(
                scholarship.getOfficialApplicationLink());

        List<String> ok = res.getEligibleReasons();
        List<String> bad = res.getIneligibleReasons();
        List<String> missingFields = res.getMissingProfileFields();

        evaluateCategory(
                student,
                scholarship,
                ok,
                bad,
                missingFields
        );

        evaluateIncome(
                financialInfo,
                scholarship,
                ok,
                bad,
                missingFields
        );

        evaluateCourse(
                academicInfo,
                scholarship,
                ok,
                bad,
                missingFields
        );

        evaluateMarks(
                academicInfo,
                scholarship,
                ok,
                bad,
                missingFields
        );

        evaluateState(
                student,
                scholarship,
                ok,
                bad,
                missingFields
        );

        evaluateSpecialStatus(
                specialStatus,
                scholarship,
                ok,
                bad,
                missingFields
        );

        evaluateGender(
                student,
                scholarship,
                ok,
                bad,
                missingFields
        );

        boolean eligible = bad.isEmpty();

        res.setEligible(eligible);

        buildCertificateStatuses(
                student,
                scholarship,
                res
        );

        boolean expired =
                scholarship.getDeadline() != null
                        && scholarship.getDeadline()
                        .isBefore(LocalDate.now());

        res.setExpired(expired);

        res.setAlreadyApplied(
                bookmarkRepository.isApplied(
                        studentId,
                        scholarshipId
                )
        );

        List<String> blocking = res.getBlockingReasons();

        if (expired) {
            blocking.add(
                    "The application deadline ("
                            + scholarship.getDeadline()
                            + ") has already passed."
            );
        }

        if (!eligible) {
            blocking.add(
                    "You do not currently meet all eligibility criteria for this scholarship."
            );
        }

        for (CertificateStatusDetail c : res.getCertificates()) {

            if (c.isBlocking()) {

                if ("MISSING".equals(c.getStatus())) {

                    blocking.add(
                            "Mandatory certificate \""
                                    + c.getCertificateName()
                                    + "\" has not been uploaded."
                    );

                } else if ("EXPIRED".equals(c.getStatus())) {

                    blocking.add(
                            "Mandatory certificate \""
                                    + c.getCertificateName()
                                    + "\" expired on "
                                    + c.getExpiryDate()
                                    + " and must be replaced."
                    );
                }
            }
        }

        if (!scholarship.isActive()
                || !"APPROVED".equalsIgnoreCase(
                String.valueOf(
                        scholarship.getApprovalStatus()))) {

            blocking.add(
                    "This scholarship is not currently open for applications."
            );
        }

        res.setCanApply(blocking.isEmpty());

        matchScoreRepository
                .findByStudentIdAndScholarshipId(
                        studentId,
                        scholarshipId
                )
                .ifPresent(ms ->
                        res.setMatchPercentage(
                                ms.getMatchPercentage()
                        )
                );

        return res;
    }

    /**
     * Active, non-expired scholarships this student is actually eligible for.
     */
    public List<EligibilityDetailsResponse> getEligibleScholarships(
            Long studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId));

        List<EligibilityDetailsResponse> eligible =
                new ArrayList<>();

        for (Scholarship s :
                scholarshipRepository.findActiveApproved()) {

            EligibilityDetailsResponse d =
                    getEligibilityDetails(
                            studentId,
                            s.getScholarshipId()
                    );

            if (d.isEligible()) {
                eligible.add(d);
            }
        }

        return eligible;
    }

    // ---------- reason builders ----------

    private void evaluateCategory(
            Student student,
            Scholarship s,
            List<String> ok,
            List<String> bad,
            List<String> missingFields) {

        List<String> eligible =
                s.getEligibleCategories();

        if (eligible == null || eligible.isEmpty()) {
            ok.add(
                    "Open to all communities / categories."
            );
            return;
        }

        if (isBlank(student.getCategory())) {

            bad.add(
                    "Community / category is required ("
                            + String.join(", ", eligible)
                            + ") but your profile does not have one."
            );

            missingFields.add(
                    "Community / Category"
            );

            return;
        }

        if (eligible.stream().anyMatch(
                c -> c.equalsIgnoreCase(
                        student.getCategory()))) {

            ok.add(
                    "Community "
                            + student.getCategory()
                            + " matches the eligible categories ["
                            + String.join(", ", eligible)
                            + "]."
            );

        } else {

            bad.add(
                    "Community "
                            + student.getCategory()
                            + " does not match eligible categories ["
                            + String.join(", ", eligible)
                            + "]."
            );
        }
    }

    private void evaluateIncome(
            Optional<FinancialInfo> financialInfo,
            Scholarship s,
            List<String> ok,
            List<String> bad,
            List<String> missingFields) {

        if (s.getMinAnnualIncome() == null
                && s.getMaxAnnualIncome() == null) {

            ok.add(
                    "No family income restriction for this scholarship."
            );

            return;
        }

        if (financialInfo.isEmpty()
                || financialInfo.get()
                .getAnnualFamilyIncome() == null) {

            bad.add(
                    "Family annual income is required but is not filled in your profile."
            );

            missingFields.add(
                    "Family Annual Income"
            );

            return;
        }

        double income =
                financialInfo.get()
                        .getAnnualFamilyIncome();

        String incomeText =
                "Rs." + MONEY.format(income);

        if (s.getMinAnnualIncome() != null
                && income < s.getMinAnnualIncome()) {

            bad.add(
                    "Family annual income "
                            + incomeText
                            + " is below the minimum Rs."
                            + MONEY.format(
                            s.getMinAnnualIncome())
                            + " required."
            );

            return;
        }

        if (s.getMaxAnnualIncome() != null
                && income > s.getMaxAnnualIncome()) {

            bad.add(
                    "Family annual income "
                            + incomeText
                            + " exceeds the Rs."
                            + MONEY.format(
                            s.getMaxAnnualIncome())
                            + " limit."
            );

            return;
        }

        if (s.getMaxAnnualIncome() != null) {

            ok.add(
                    "Family annual income "
                            + incomeText
                            + " is within the maximum Rs."
                            + MONEY.format(
                            s.getMaxAnnualIncome())
                            + " limit."
            );

        } else {

            ok.add(
                    "Family annual income "
                            + incomeText
                            + " meets the minimum Rs."
                            + MONEY.format(
                            s.getMinAnnualIncome())
                            + " requirement."
            );
        }
    }

    // ============================================================
    // COURSE MATCHING WITH NORMALIZATION
    // ============================================================

    private String normalizeCourse(String raw) {

        if (raw == null) {
            return "";
        }

        String key = raw
                .trim()
                .toUpperCase()
                .replaceAll("[^A-Z ]", "")
                .replaceAll("\\s+", " ")
                .trim();

        return COURSE_ALIASES.getOrDefault(
                key,
                key
        );
    }

    private boolean coursesMatch(
            String studentCourse,
            String scholarshipCourse) {

        if (studentCourse == null
                || scholarshipCourse == null) {

            return false;
        }

        if (normalizeCourse(studentCourse)
                .equals(
                        normalizeCourse(scholarshipCourse))) {

            return true;
        }

        // Keep the old loose-substring behaviour as fallback.
        String s =
                studentCourse.trim().toLowerCase();

        String c =
                scholarshipCourse.trim().toLowerCase();

        return s.contains(c)
                || c.contains(s);
    }

    private void evaluateCourse(
            Optional<AcademicInfo> academicInfo,
            Scholarship s,
            List<String> ok,
            List<String> bad,
            List<String> missingFields) {

        List<String> courses =
                s.getEligibleCourses();

        if (courses == null || courses.isEmpty()) {

            ok.add(
                    "Open to all courses / streams."
            );

            return;
        }

        if (academicInfo.isEmpty()
                || isBlank(
                academicInfo.get().getCourseName())) {

            bad.add(
                    "Course is required ("
                            + String.join(", ", courses)
                            + ") but your academic details do not have one."
            );

            missingFields.add(
                    "Course Name"
            );

            return;
        }

        String studentCourse =
                academicInfo.get().getCourseName();

        boolean match =
                courses.stream()
                        .anyMatch(
                                c -> coursesMatch(
                                        studentCourse,
                                        c
                                )
                        );

        if (match) {

            ok.add(
                    "Course "
                            + studentCourse
                            + " matches the eligible courses ["
                            + String.join(", ", courses)
                            + "]."
            );

        } else {

            bad.add(
                    "Course "
                            + studentCourse
                            + " is not in the eligible courses ["
                            + String.join(", ", courses)
                            + "]."
            );
        }
    }

    private void evaluateMarks(
            Optional<AcademicInfo> academicInfo,
            Scholarship s,
            List<String> ok,
            List<String> bad,
            List<String> missingFields) {

        if (s.getMinMarksCgpa() == null) {

            ok.add(
                    "No minimum marks / CGPA requirement."
            );

            return;
        }

        if (academicInfo.isEmpty()
                || academicInfo.get()
                .getQualifyingExamPercentage() == null) {

            bad.add(
                    "Minimum "
                            + PCT.format(
                            s.getMinMarksCgpa())
                            + "% marks required but your qualifying exam percentage is not filled."
            );

            missingFields.add(
                    "Qualifying Exam Percentage"
            );

            return;
        }

        double marks =
                academicInfo.get()
                        .getQualifyingExamPercentage();

        if (marks >= s.getMinMarksCgpa()) {

            ok.add(
                    "Marks "
                            + PCT.format(marks)
                            + "% meets the minimum "
                            + PCT.format(
                            s.getMinMarksCgpa())
                            + "% requirement."
            );

        } else {

            bad.add(
                    "Marks "
                            + PCT.format(marks)
                            + "% is below the minimum "
                            + PCT.format(
                            s.getMinMarksCgpa())
                            + "% required."
            );
        }
    }

    // ============================================================
    // STATE / DOMICILE MATCHING
    // ============================================================

    private void evaluateState(
            Student student,
            Scholarship s,
            List<String> ok,
            List<String> bad,
            List<String> missingFields) {

        List<String> states =
                s.getEligibleStates();

        if (states == null || states.isEmpty()) {

            ok.add(
                    "Open to students from all states."
            );

            return;
        }

        String studentState =
                !isBlank(student.getDomicile())
                        ? student.getDomicile()
                        : student.getState();

        if (isBlank(studentState)) {

            bad.add(
                    "State / domicile is required ("
                            + String.join(", ", states)
                            + ") but your profile does not have one."
            );

            missingFields.add(
                    "State / Domicile"
            );

            return;
        }

        final String toCheck =
                studentState;

        if (states.stream().anyMatch(
                st -> st.equalsIgnoreCase(toCheck))) {

            ok.add(
                    "State / domicile "
                            + toCheck
                            + " matches the eligible states ["
                            + String.join(", ", states)
                            + "]."
            );

        } else {

            bad.add(
                    "State / domicile "
                            + toCheck
                            + " does not match eligible states ["
                            + String.join(", ", states)
                            + "]."
            );
        }
    }

    private void evaluateSpecialStatus(
            Optional<SpecialStatus> specialStatus,
            Scholarship s,
            List<String> ok,
            List<String> bad,
            List<String> missingFields) {

        List<String> required =
                s.getEligibleSpecialStatus();

        if (required == null || required.isEmpty()) {
            return;
        }

        if (specialStatus.isEmpty()) {

            bad.add(
                    "Requires one of ["
                            + String.join(", ", required)
                            + "] but your special status details are not filled."
            );

            missingFields.add(
                    "Special Status"
            );

            return;
        }

        SpecialStatus ss =
                specialStatus.get();

        String matched = null;

        for (String req : required) {

            boolean has;

            switch (req.toUpperCase()) {

                case "SINGLE_GIRL_CHILD":
                    has = ss.isSingleGirlChild();
                    break;

                case "ORPHAN_SINGLE_PARENT":
                    has = ss.isOrphanSingleParent();
                    break;

                case "EX_SERVICEMEN_DEPENDENT":
                    has = ss.isExServicemenDependent();
                    break;

                case "SPORTS_QUOTA":
                    has = ss.isSportsQuota();
                    break;

                case "MINORITY_COMMUNITY":
                    has = ss.isMinorityCommunity();
                    break;

                case "FIRST_GRADUATE":
                    has = ss.isFirstGraduate();
                    break;

                default:
                    has = false;
                    break;
            }

            if (has) {
                matched = req;
                break;
            }
        }

        if (matched != null) {

            ok.add(
                    "Special status "
                            + matched
                            + " matches this scholarship's requirement."
            );

        } else {

            bad.add(
                    "Requires one of the special statuses ["
                            + String.join(", ", required)
                            + "], none of which is set on your profile."
            );
        }
    }

    /**
     * Women's scholarships are gender restricted by their primary category.
     */
    private void evaluateGender(
            Student student,
            Scholarship s,
            List<String> ok,
            List<String> bad,
            List<String> missingFields) {

        if (!"WOMENS".equalsIgnoreCase(
                String.valueOf(
                        s.getPrimaryCategory()))) {

            return;
        }

        if (isBlank(student.getGender())) {

            bad.add(
                    "Requires FEMALE gender but your profile does not have a gender set."
            );

            missingFields.add(
                    "Gender"
            );

            return;
        }

        if ("FEMALE".equalsIgnoreCase(
                student.getGender())) {

            ok.add(
                    "Gender FEMALE matches this Women's scholarship requirement."
            );

        } else {

            bad.add(
                    "Requires FEMALE gender; your profile has "
                            + student.getGender()
                            + "."
            );
        }
    }

    // ---------- certificate requirement evaluation ----------

    private void buildCertificateStatuses(
            Student student,
            Scholarship scholarship,
            EligibilityDetailsResponse res) {

        Map<Long, Boolean> required =
                new LinkedHashMap<>();

        for (ScholarshipRequiredDocument rd :
                requiredDocumentRepository
                        .findByScholarshipId(
                                scholarship.getScholarshipId())) {

            required.put(
                    rd.getCertificateTypeId(),
                    rd.isMandatory()
            );
        }

        if (scholarship.getRequiredDocuments() != null) {

            for (Long id :
                    scholarship.getRequiredDocuments()) {

                required.putIfAbsent(
                        id,
                        true
                );
            }
        }

        Map<Long, StudentDocument> uploaded =
                new LinkedHashMap<>();

        for (StudentDocument d :
                studentDocumentRepository
                        .findByStudentId(
                                student.getStudentId())) {

            uploaded.putIfAbsent(
                    d.getCertificateTypeId(),
                    d
            );
        }

        int mandatoryCount = 0;
        int mandatorySatisfied = 0;

        for (Map.Entry<Long, Boolean> entry :
                required.entrySet()) {

            Long typeId = entry.getKey();

            boolean mandatory =
                    Boolean.TRUE.equals(
                            entry.getValue());

            Optional<CertificateType> type =
                    certificateTypeRepository
                            .findById(typeId);

            String name =
                    type.map(
                            CertificateType::getName
                    ).orElse(
                            "Certificate #" + typeId
                    );

            CertificateStatusDetail detail =
                    new CertificateStatusDetail();

            detail.setCertificateTypeId(typeId);
            detail.setCertificateName(name);
            detail.setMandatory(mandatory);

            type.ifPresent(t ->
                    detail.setIssuingAuthority(
                            t.getIssuingAuthority()
                    )
            );

            StudentDocument doc =
                    uploaded.get(typeId);

            if (doc == null) {

                detail.setStatus("MISSING");

            } else {

                detail.setDocumentId(
                        doc.getDocumentId()
                );

                detail.setIssueDate(
                        doc.getIssueDate()
                );

                detail.setExpiryDate(
                        doc.getExpiryDate()
                );

                detail.setStatus(
                        currentDocumentStatus(doc)
                );
            }

            applyGetCertificateGuidance(
                    detail,
                    type.orElse(null),
                    student
            );

            boolean satisfied =
                    !"MISSING".equals(
                            detail.getStatus())
                            && !"EXPIRED".equals(
                            detail.getStatus());

            detail.setBlocking(
                    mandatory && !satisfied
            );

            if (mandatory) {

                mandatoryCount++;

                if (satisfied) {
                    mandatorySatisfied++;
                }
            }

            res.getCertificates()
                    .add(detail);
        }

        // Profile photo is mandatory.
        CertificateStatusDetail photo =
                new CertificateStatusDetail();

        photo.setCertificateName(
                "Profile Photo"
        );

        photo.setMandatory(true);
        photo.setSourceCategory("PROFILE");

        photo.setGuidance(
                "Upload your profile photo in Profile & Documents."
        );

        boolean hasPhoto =
                !isBlank(
                        student.getProfilePhotoUrl()
                );

        photo.setStatus(
                hasPhoto
                        ? "VALID"
                        : "MISSING"
        );

        photo.setBlocking(!hasPhoto);

        mandatoryCount++;

        if (hasPhoto) {
            mandatorySatisfied++;
        }

        res.getCertificates()
                .add(photo);

        res.setMandatoryCertificateCount(
                mandatoryCount
        );

        res.setMandatorySatisfiedCount(
                mandatorySatisfied
        );
    }

    /** Recomputes live status for a document. */
    private String currentDocumentStatus(
            StudentDocument doc) {

        if (doc.getExpiryDate() == null) {

            return doc.getStatus() == null
                    ? "VALID"
                    : doc.getStatus();
        }

        LocalDate today =
                LocalDate.now();

        if (doc.getExpiryDate()
                .isBefore(today)) {

            return "EXPIRED";
        }

        if (!doc.getExpiryDate()
                .isAfter(
                        today.plusDays(30))) {

            return "EXPIRING_SOON";
        }

        return "VALID";
    }

    private void applyGetCertificateGuidance(
            CertificateStatusDetail detail,
            CertificateType type,
            Student student) {

        String name =
                detail.getCertificateName() == null
                        ? ""
                        : detail.getCertificateName()
                        .toLowerCase();

        if (name.contains("photo")
                || name.contains("photograph")) {

            detail.setSourceCategory(
                    "PROFILE"
            );

            detail.setGuidance(
                    "Upload your profile photo in Profile & Documents."
            );

            return;
        }

        if (isIssuerProvided(name)) {

            detail.setSourceCategory(
                    "ISSUER_PROVIDED"
            );

            detail.setGuidance(
                    "Get it from your College/School/Bank"
                            + (isBlank(
                            detail.getIssuingAuthority())
                            ? "."
                            : " (" + detail.getIssuingAuthority() + ").")
            );

            return;
        }

        detail.setSourceCategory(
                "GOVERNMENT"
        );

        if (type != null
                && !isBlank(
                type.getOfficialApplyLink())) {

            detail.getApplyLinks()
                    .add(
                            new CertificateStatusDetail.PortalLink(
                                    "Apply Online",
                                    type.getOfficialApplyLink(),
                                    null
                            )
                    );
        }

        if (detail.getCertificateTypeId() != null) {

            List<CertificatePortalLink> links =
                    isBlank(student.getState())
                            ? certificatePortalLinkRepository
                            .findByCertificateTypeId(
                                    detail.getCertificateTypeId()
                            )
                            : certificatePortalLinkRepository
                            .findByCertificateTypeIdAndState(
                                    detail.getCertificateTypeId(),
                                    student.getState()
                            );

            if (links.isEmpty()) {

                links =
                        certificatePortalLinkRepository
                                .findByCertificateTypeId(
                                        detail.getCertificateTypeId()
                                );
            }

            for (CertificatePortalLink l : links) {

                if (!isBlank(l.getPortalUrl())) {

                    detail.getApplyLinks()
                            .add(
                                    new CertificateStatusDetail.PortalLink(
                                            isBlank(l.getPortalName())
                                                    ? "Official Portal"
                                                    : l.getPortalName(),
                                            l.getPortalUrl(),
                                            l.getState()
                                    )
                            );
                }
            }
        }

        if (detail.getApplyLinks().isEmpty()) {

            detail.setGuidance(
                    "Apply at your nearest e-Sevai / Common Service Centre"
                            + (isBlank(
                            detail.getIssuingAuthority())
                            ? "."
                            : " (issued by "
                            + detail.getIssuingAuthority()
                            + ").")
            );

        } else {

            detail.setGuidance(
                    "Apply online using the official portal below, or visit your nearest e-Sevai / Common Service Centre."
            );
        }
    }

    private boolean isIssuerProvided(
            String lowerName) {

        return lowerName.contains("marksheet")
                || lowerName.contains("mark sheet")
                || lowerName.contains("bonafide")
                || lowerName.contains("bona fide")
                || lowerName.contains("passbook")
                || lowerName.contains("bank")
                || lowerName.contains("transfer certificate")
                || lowerName.contains("fee receipt")
                || lowerName.contains("admission")
                || lowerName.contains("id card")
                || lowerName.contains("college")
                || lowerName.contains("school")
                || lowerName.contains("degree certificate");
    }

    private boolean isBlank(String s) {
        return s == null
                || s.trim().isEmpty();
    }

    // ---------- individual criterion checks ----------

    private boolean isCategoryEligible(
            Student student,
            Scholarship scholarship) {

        List<String> eligible =
                scholarship.getEligibleCategories();

        if (eligible == null
                || eligible.isEmpty()) {

            return true;
        }

        return eligible.stream()
                .anyMatch(
                        c -> c.equalsIgnoreCase(
                                student.getCategory()
                        )
                );
    }

    private boolean isIncomeEligible(
            Optional<FinancialInfo> financialInfo,
            Scholarship scholarship) {

        if (scholarship.getMinAnnualIncome() == null
                && scholarship.getMaxAnnualIncome() == null) {

            return true;
        }

        if (financialInfo.isEmpty()
                || financialInfo.get()
                .getAnnualFamilyIncome() == null) {

            return false;
        }

        double income =
                financialInfo.get()
                        .getAnnualFamilyIncome();

        boolean aboveMin =
                scholarship.getMinAnnualIncome() == null
                        || income >= scholarship.getMinAnnualIncome();

        boolean belowMax =
                scholarship.getMaxAnnualIncome() == null
                        || income <= scholarship.getMaxAnnualIncome();

        return aboveMin && belowMax;
    }

    // ============================================================
    // UPDATED COURSE ELIGIBILITY
    // ============================================================

    private boolean isCourseEligible(
            Optional<AcademicInfo> academicInfo,
            Scholarship scholarship) {

        List<String> eligibleCourses =
                scholarship.getEligibleCourses();

        if (eligibleCourses == null
                || eligibleCourses.isEmpty()) {

            return true;
        }

        if (academicInfo.isEmpty()
                || academicInfo.get()
                .getCourseName() == null) {

            return false;
        }

        String studentCourse =
                academicInfo.get()
                        .getCourseName();

        return eligibleCourses.stream()
                .anyMatch(
                        c -> coursesMatch(
                                studentCourse,
                                c
                        )
                );
    }

    private boolean isMarksEligible(
            Optional<AcademicInfo> academicInfo,
            Scholarship scholarship) {

        if (scholarship.getMinMarksCgpa() == null) {
            return true;
        }

        if (academicInfo.isEmpty()
                || academicInfo.get()
                .getQualifyingExamPercentage() == null) {

            return false;
        }

        return academicInfo.get()
                .getQualifyingExamPercentage()
                >= scholarship.getMinMarksCgpa();
    }

    // ============================================================
    // UPDATED STATE ELIGIBILITY
    // ============================================================

    private boolean isStateEligible(
            Student student,
            Scholarship scholarship) {

        List<String> eligibleStates =
                scholarship.getEligibleStates();

        if (eligibleStates == null
                || eligibleStates.isEmpty()) {

            return true;
        }

        String studentState =
                !isBlank(student.getDomicile())
                        ? student.getDomicile()
                        : student.getState();

        if (isBlank(studentState)) {
            return false;
        }

        return eligibleStates.stream()
                .anyMatch(
                        s -> s.equalsIgnoreCase(
                                studentState
                        )
                );
    }

    // ============================================================
    // UPDATED SPECIAL STATUS
    // ============================================================

    private boolean isSpecialStatusEligible(
            Optional<SpecialStatus> specialStatus,
            Scholarship scholarship) {

        List<String> required =
                scholarship.getEligibleSpecialStatus();

        if (required == null
                || required.isEmpty()) {

            return true;
        }

        if (specialStatus.isEmpty()) {
            return false;
        }

        SpecialStatus s =
                specialStatus.get();

        for (String req : required) {

            switch (req.toUpperCase()) {

                case "SINGLE_GIRL_CHILD":

                    if (s.isSingleGirlChild()) {
                        return true;
                    }

                    break;

                case "ORPHAN_SINGLE_PARENT":

                    if (s.isOrphanSingleParent()) {
                        return true;
                    }

                    break;

                case "EX_SERVICEMEN_DEPENDENT":

                    if (s.isExServicemenDependent()) {
                        return true;
                    }

                    break;

                case "SPORTS_QUOTA":

                    if (s.isSportsQuota()) {
                        return true;
                    }

                    break;

                case "MINORITY_COMMUNITY":

                    if (s.isMinorityCommunity()) {
                        return true;
                    }

                    break;

                case "FIRST_GRADUATE":

                    if (s.isFirstGraduate()) {
                        return true;
                    }

                    break;

                default:
                    break;
            }
        }

        return false;
    }

    private String buildExplanation(
            List<String> matched,
            List<String> unmatched,
            double totalScore) {

        StringBuilder sb =
                new StringBuilder();

        sb.append("Match score: ")
                .append(totalScore)
                .append("%. ");

        sb.append("Why eligible: ");

        sb.append(
                matched.isEmpty()
                        ? "You currently meet none of the criteria."
                        : "You meet "
                        + String.join(", ", matched)
                        + "."
        );

        sb.append(" Why not eligible: ");

        sb.append(
                unmatched.isEmpty()
                        ? "You meet all eligibility criteria for this scholarship."
                        : "You do not currently meet "
                        + String.join(", ", unmatched)
                        + "."
        );

        return sb.toString();
    }
}