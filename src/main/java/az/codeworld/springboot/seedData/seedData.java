package az.codeworld.springboot.seedData;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.admin.entities.Request;
import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;

import az.codeworld.springboot.security.controllers.SecurityController;
import az.codeworld.springboot.security.entities.Authority;
import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.security.services.JpaUserDetailsService;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.security.services.authservices.authservicesImpl.RegistrationServiceImpl;
import az.codeworld.springboot.security.services.rbacservices.AuthorityService;
import az.codeworld.springboot.security.services.rbacservices.RoleService;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.authorities;
import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.notificationtype;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import az.codeworld.springboot.utilities.generators.TokenGenerator;
import az.codeworld.springboot.utilities.generators.UsernameGenerator;
import az.codeworld.springboot.web.entities.ClassSection;
import az.codeworld.springboot.web.entities.CourseEnrollment;
import az.codeworld.springboot.web.entities.CourseOffering;
import az.codeworld.springboot.web.entities.Enrollment;
import az.codeworld.springboot.web.entities.ProfilePicture;
import az.codeworld.springboot.web.entities.Subject;
import az.codeworld.springboot.web.entities.SubjectEnrollment;
import az.codeworld.springboot.web.entities.TeachingAssignment;
import az.codeworld.springboot.web.repositories.CourseEnrollmentRepository;
import az.codeworld.springboot.web.repositories.CourseOfferingRepository;
import az.codeworld.springboot.web.repositories.SubjectEntrollmentRepository;
import az.codeworld.springboot.web.services.GenericWebService;
import az.codeworld.springboot.web.services.NotificationService;
import az.codeworld.springboot.web.services.ProfileService;

@Component
@Profile("dev")
public class SeedData implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final RoleService roleService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final JpaUserDetailsService jpaUserDetailsService;
    private final ProfileService profileService;

    private final RequestService requestService;

    private final GenericWebService genericWebService;

    private final RegistrationServiceImpl registrationService;

    private final NotificationService notificationService;

    private final SubjectEntrollmentRepository subjectEntrollmentRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CourseOfferingRepository courseOfferingRepository;

    public SeedData(
            PasswordEncoder passwordEncoder,
            AuthorityService authorityService,
            RoleService roleService,
            UserService userService,
            JpaUserDetailsService jpaUserDetailsService,
            TransactionService transactionService,
            GenericWebService genericWebService,
            RequestService requestService,
            RegistrationServiceImpl registrationService,
            ProfileService profileService,
            NotificationService notificationService,
            SubjectEntrollmentRepository subjectEntrollmentRepository,
            CourseEnrollmentRepository courseEnrollmentRepository,
            CourseOfferingRepository courseOfferingRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authorityService = authorityService;
        this.roleService = roleService;
        this.userService = userService;
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.transactionService = transactionService;
        this.genericWebService = genericWebService;
        this.requestService = requestService;
        this.registrationService = registrationService;
        this.profileService = profileService;
        this.notificationService = notificationService;
        this.subjectEntrollmentRepository = subjectEntrollmentRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.courseOfferingRepository = courseOfferingRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Seeding Data...");

        try {
            if (authorityService.getAuthorityById(authorities.ACCESS_ADMIN_PANEL.getAuthorityId()) != null)
                return;
        } catch (RuntimeException e) {
        }

        for (authorities auth : authorities.values()) {
            Authority authority = new Authority();
            authority.setAuthorityId(auth.getAuthorityId());
            authority.setAuthorityNameString(auth.getAuthorityString());

            authorityService.saveAuthority(authority);
        }

        for (roles role : roles.values()) {
            Role roleTemp = new Role();
            roleTemp.setRoleId(role.getRoleId());
            roleTemp.setRoleNameString(role.getRoleNameString());

            Set<Long> authorityIds;
            switch (roleTemp.getRoleNameString()) {
                case "ADMIN":
                    authorityIds = Set.of(
                            authorities.ACCESS_ADMIN_PANEL.getAuthorityId(),
                            authorities.RESET_ANY_USER_PASSWORD.getAuthorityId(),
                            authorities.DELETE_ANY_USER.getAuthorityId(),
                            authorities.SET_ANY_ROLE.getAuthorityId());
                    break;
                case "USER":
                    authorityIds = Set.of(authorities.READ_ANY_CONTENT.getAuthorityId());
                    break;
                case "STUDENT":
                    authorityIds = Set.of(
                            authorities.READ_ANY_CONTENT.getAuthorityId());
                    break;
                case "TEACHER":
                    authorityIds = Set.of(
                            authorities.READ_ANY_CONTENT.getAuthorityId());
                    break;
                case "PRE_2FA":
                    authorityIds = Set.of(authorities.NO_AUTHORITIES.getAuthorityId());
                    break;
                case "BANNED":
                    authorityIds = Set.of(authorities.NO_AUTHORITIES.getAuthorityId());
                    break;
                default:
                    throw new RuntimeException("Invalid roleString");
            }
            for (Long authorityId : authorityIds) {
                Authority authority = authorityService.getAuthorityById(authorityId);
                roleTemp.getAuthorities().add(authority);
                authority.getRoles().add(roleTemp);
            }
            roleService.saveRole(roleTemp);
        }

        Random random = new Random();

        Subject math = new Subject();
        Subject physics = new Subject();
        Subject computerScience = new Subject();
        Subject biology = new Subject();

        math.setSubjectTitle("Mathematics");
        math.setSubjectBody("Subject Body for Mathematics");

        physics.setSubjectTitle("Physics");
        physics.setSubjectBody("Subject Body for Physics");

        computerScience.setSubjectTitle("Computer Science");
        computerScience.setSubjectBody("Subject Body for Computer Science");

        biology.setSubjectTitle("Biology");
        biology.setSubjectBody("Subject Body for Biology");

        genericWebService.saveType(Subject.class, math);
        genericWebService.saveType(Subject.class, physics);
        genericWebService.saveType(Subject.class, computerScience);
        genericWebService.saveType(Subject.class, biology);

        List<Subject> allSubjects = new ArrayList<>(List.of(math, physics, computerScience, biology));

        Transaction transaction;
        ProfilePicture profilePicture;

        Teacher teacher;

        teacher = new Teacher();
        teacher.setUserName("T-TTTT-TTTT-T");

        teacher.setFirstName("Thomas");
        teacher.setLastName("Dhones");
        teacher.setEmail("exampleTEACHER@email.com");
        teacher.setPassword(passwordEncoder.encode("1234Aa@"));
        teacher.setPhoneNumber("+994519889192");

        teacher.setDepartment("Biology");
        teacher.setTitle("Biology");
        teacher.setAffiliationDate(LocalDate.of(2005, 12, 4));
        teacher.setOfficeRoom("457-Jackson");
        teacher.setPayment(new Money(new BigDecimal("200.00"), currency.AZN));
        teacher.setNextDate(Instant.now().minus(Duration.ofDays(1)));
        teacher.setBillingEnabled(true);

        teacher.setBirthDate(LocalDate.of(1969, 1, 1));
        teacher.setStreet("Example st.");
        teacher.setCity("Example");
        teacher.setRegion("CA");
        teacher.setPostalCode(99999);
        teacher.setCountry("Example");
        teacher.setLanguage("English (United States)");
        teacher.setZoneId("America/Guatemala");

        profilePicture = new ProfilePicture();
        profilePicture.setProfileTitle("Silly Profile Picture");
        profilePicture.setDescription("Silly Profile Picture Description");

        teacher.setProfilePicture(profilePicture);
        profilePicture.setUser(teacher);

        userService.saveUser(teacher);

        roleService.addRolesToUser(teacher.getId(), Set.of(roles.TEACHER.getRoleId()));
        jpaUserDetailsService.loadUserByUsername(teacher.getUserName());

        List<CourseOffering> offerings = new ArrayList<>();
        Map<Long, List<CourseOffering>> offeringsBySubjectId = new HashMap<>();

        int num = 1 + random.nextInt(allSubjects.size());
        Collections.shuffle(allSubjects, random);
        List<Subject> chosenSubjects = allSubjects.subList(0, num);

        for (Subject subject : chosenSubjects) {
            CourseOffering courseOffering = new CourseOffering();
            courseOffering.setSubject(subject);
            teacher.addCourseOfferings(List.of(courseOffering));

            genericWebService.saveType(CourseOffering.class, courseOffering);

            offerings.add(courseOffering);
            offeringsBySubjectId
                    .computeIfAbsent(subject.getId(), k -> new ArrayList<>())
                    .add(courseOffering);
        }

        for (int j = 0; j < 20; j++) {
            transaction = new Transaction();
            transaction.setTransactionPaidBy("TTT - HDFC TEACHER Bank");
            transaction.setTransactionDescription("Withdraw to TEACHER Bank account");
            transaction.setTransactionDetails("Transfer to HDFC Bank via Secure3D");
            transaction.setStatus(transactionstatus.PENDING);
            transaction.setTransactionFee(new BigDecimal("4.82"));
            transaction.setTransactionAmount(new BigDecimal(random.nextInt(100)).add(BigDecimal.valueOf(j)));
            transaction.setTransactionTotal(new BigDecimal("562"));
            transaction.setCurrency(currency.USD);
            transaction.setBelongsTo(roles.TEACHER);
            transaction.setTransactionTime(Instant.now().minus(Duration.ofHours(j)));

            transactionService.saveTransaction(transaction);
            transactionService.addTransactionsToUser(teacher.getUserName(), Set.of(transaction.getTransactionId()));

            notificationService.notify(
                    notificationtype.BILLING,
                    "Transaction Been Made",
                    teacher.getFirstName() + " " + teacher.getLastName() + " just made a transaction",
                    "/user/dashboard",
                    teacher.getId());
        }

        for (int i = 0; i < 5; i++) {
            teacher = new Teacher();
            if (teacher.getUserName() == null)
                teacher.setUserName(UsernameGenerator.generateUsername(roles.TEACHER.getRoleNameString()));

            teacher.setFirstName("Thomas");
            teacher.setLastName("Dhones");
            teacher.setEmail("example" + i + "@email.com");
            teacher.setPassword(passwordEncoder.encode("1234Aa@"));
            teacher.setPhoneNumber("+994519889192");

            teacher.setDepartment("Biology");
            teacher.setTitle("Biology");
            teacher.setAffiliationDate(LocalDate.of(2005, 12, 4));
            teacher.setOfficeRoom("457-Jackson");
            teacher.setPayment(new Money(new BigDecimal("200.00"), currency.AZN));
            teacher.setNextDate(Instant.now().minus(Duration.ofDays(1)));
            teacher.setBillingEnabled(true);

            teacher.setBirthDate(LocalDate.of(1969, 1, 1));
            teacher.setStreet("Example st.");
            teacher.setCity("Example");
            teacher.setRegion("CA");
            teacher.setPostalCode(99999);
            teacher.setCountry("Example");
            teacher.setLanguage("English (United States)");
            teacher.setZoneId("America/Guatemala");

            profilePicture = new ProfilePicture();
            profilePicture.setProfileTitle("Silly Profile Picture");
            profilePicture.setDescription("Silly Profile Picture Description");

            teacher.setProfilePicture(profilePicture);
            profilePicture.setUser(teacher);

            userService.saveUser(teacher);

            roleService.addRolesToUser(teacher.getId(), Set.of(roles.TEACHER.getRoleId()));
            jpaUserDetailsService.loadUserByUsername(teacher.getUserName());

            num = 1 + random.nextInt(allSubjects.size());
            Collections.shuffle(allSubjects, random);
            chosenSubjects = allSubjects.subList(0, num);

            for (Subject subject : chosenSubjects) {
                CourseOffering courseOffering = new CourseOffering();
                courseOffering.setSubject(subject);
                teacher.addCourseOfferings(List.of(courseOffering));

                genericWebService.saveType(CourseOffering.class, courseOffering);

                offerings.add(courseOffering);
                offeringsBySubjectId
                        .computeIfAbsent(subject.getId(), k -> new ArrayList<>())
                        .add(courseOffering);
            }

            for (int j = 0; j < 20; j++) {
                transaction = new Transaction();
                transaction.setTransactionPaidBy("HDFC TEACHER Bank");
                transaction.setTransactionDescription("Withdraw to TEACHER Bank account");
                transaction.setTransactionDetails("Transfer to HDFC Bank via Secure3D");
                transaction.setStatus(transactionstatus.PENDING);
                transaction.setTransactionFee(new BigDecimal("4.82"));
                transaction.setTransactionAmount(new BigDecimal(random.nextInt(100)).add(BigDecimal.valueOf(j)));
                transaction.setTransactionTotal(new BigDecimal("562"));
                transaction.setCurrency(currency.USD);
                transaction.setBelongsTo(roles.TEACHER);
                transaction.setTransactionTime(Instant.now().minus(Duration.ofHours(j)));

                transactionService.saveTransaction(transaction);
                transactionService.addTransactionsToUser(teacher.getUserName(), Set.of(transaction.getTransactionId()));

                notificationService.notify(
                        notificationtype.BILLING,
                        "Transaction Been Made",
                        teacher.getFirstName() + " " + teacher.getLastName() + " just made a transaction",
                        "/user/dashboard",
                        teacher.getId());
            }
        }

        Student student;

        student = new Student();
        student.setUserName("S-SSSS-SSSS-S");
        student.setFirstName("James");
        student.setLastName("Dhones");
        student.setEmail("exampleSTUDENT@email.com");
        student.setPassword(passwordEncoder.encode("1234Aa@"));
        student.setPhoneNumber("+994519889192");

        student.setGroupName("684.23e");
        student.setYear(2);
        student.setMajor("IT");
        student.setAffiliationDate(LocalDate.now());
        student.setGpa(89.99);
        student.setPayment(new Money(new BigDecimal("200.89"), currency.EURO));
        student.setNextDate(Instant.now().minus(Duration.ofDays(1)));
        student.setBillingEnabled(true);

        student.setBirthDate(LocalDate.of(2000, 1, 1));
        student.setStreet("Example st.");
        student.setCity("Example");
        student.setRegion("CA");
        student.setPostalCode(99999);
        student.setCountry("Example");
        student.setLanguage("English (United States)");
        student.setZoneId("America/Guatemala");

        profilePicture = new ProfilePicture();
        profilePicture.setProfileTitle("Silly Profile Picture");
        profilePicture.setDescription("Silly Profile Picture Description");

        student.setProfilePicture(profilePicture);
        profilePicture.setUser(student);

        userService.saveUser(student);

        roleService.addRolesToUser(student.getId(), Set.of(roles.STUDENT.getRoleId()));
        jpaUserDetailsService.loadUserByUsername(student.getUserName());

        num = 1 + random.nextInt(allSubjects.size());
        Collections.shuffle(allSubjects, random);
        chosenSubjects = allSubjects.subList(0, num);

        for (Subject subject : chosenSubjects) {
            if (!subjectEntrollmentRepository.existsByStudent_idAndSubject_id(student.getId(), subject.getId())) {
                SubjectEnrollment subjectEnrollment = new SubjectEnrollment();
                subjectEnrollment.setStudent(student);
                subjectEnrollment.setSubject(subject);
                genericWebService.saveType(SubjectEnrollment.class, subjectEnrollment);
            }

            List<CourseOffering> possibleOfferings = offeringsBySubjectId.get(subject.getId());

            if (possibleOfferings != null && !possibleOfferings.isEmpty()) {

                CourseOffering chosenCourseOffering = possibleOfferings.get(random.nextInt(possibleOfferings.size()));

                if (!courseEnrollmentRepository.existsByStudent_idAndCourseOffering_id(student.getId(), chosenCourseOffering.getId())) {
                    CourseEnrollment courseEnrollment = new CourseEnrollment();
                    courseEnrollment.setStudent(student);
                    chosenCourseOffering.addCourseEnrollments(List.of(courseEnrollment));
                    genericWebService.saveType(CourseEnrollment.class, courseEnrollment);
                }
            }
        }

        for (int j = 0; j < 2; j++) {
            transaction = new Transaction();
            transaction.setTransactionPaidBy("SSS - HDFC STUDENT Bank");
            transaction.setTransactionDescription("Withdraw to STUDENT Bank account");
            transaction.setTransactionDetails("Transfer to HDFC STUDENT Bank via Secure3D");
            transaction.setStatus(transactionstatus.PENDING);
            transaction.setTransactionFee(new BigDecimal("4.82"));
            transaction.setTransactionAmount(new BigDecimal(random.nextInt(100)).add(BigDecimal.valueOf(j)));
            transaction.setTransactionTotal(new BigDecimal("562"));
            transaction.setCurrency(currency.USD);
            transaction.setBelongsTo(roles.STUDENT);
            transaction.setTransactionTime(Instant.now().minus(Duration.ofHours(j)));

            transactionService.saveTransaction(transaction);
            transactionService.addTransactionsToUser(student.getUserName(), Set.of(transaction.getTransactionId()));

            notificationService.notify(
                    notificationtype.BILLING,
                    "Transaction Been Made",
                    student.getFirstName() + " " + student.getLastName() + " just made a transaction",
                    "/user/dashboard",
                    student.getId());
        }

        for (int i = 0; i < 20; i++) {
            student = new Student();
            if (student.getUserName() == null)
                student.setUserName(UsernameGenerator.generateUsername(roles.STUDENT.getRoleNameString()));
            student.setFirstName("James");
            student.setLastName("Dhones");
            student.setEmail("example" + i + 5 + "@email.com");
            student.setPassword(passwordEncoder.encode("1234Aa@"));
            student.setPhoneNumber("+994519889192");

            student.setGroupName("684.23e");
            student.setYear(2);
            student.setMajor("IT");
            student.setAffiliationDate(LocalDate.now());
            student.setGpa(89.99);
            student.setPayment(new Money(new BigDecimal("200.89"), currency.EURO));
            student.setNextDate(Instant.now().minus(Duration.ofDays(1)));
            student.setBillingEnabled(true);

            student.setBirthDate(LocalDate.of(2000, 1, 1));
            student.setStreet("Example st.");
            student.setCity("Example");
            student.setRegion("CA");
            student.setPostalCode(99999);
            student.setCountry("Example");
            student.setLanguage("English (United States)");
            student.setZoneId("America/Guatemala");

            profilePicture = new ProfilePicture();
            profilePicture.setProfileTitle("Silly Profile Picture");
            profilePicture.setDescription("Silly Profile Picture Description");

            student.setProfilePicture(profilePicture);
            profilePicture.setUser(student);

            userService.saveUser(student);

            roleService.addRolesToUser(student.getId(), Set.of(roles.STUDENT.getRoleId()));
            jpaUserDetailsService.loadUserByUsername(student.getUserName());

            num = 1 + random.nextInt(allSubjects.size());
            Collections.shuffle(allSubjects, random);
            chosenSubjects = allSubjects.subList(0, num);

            for (Subject subject : chosenSubjects) {
                if (!subjectEntrollmentRepository.existsByStudent_idAndSubject_id(student.getId(), subject.getId())) {
                    SubjectEnrollment subjectEnrollment = new SubjectEnrollment();
                    subjectEnrollment.setStudent(student);
                    subjectEnrollment.setSubject(subject);
                    genericWebService.saveType(SubjectEnrollment.class, subjectEnrollment);
                }

                List<CourseOffering> possibleOfferings = offeringsBySubjectId.get(subject.getId());

                if (possibleOfferings != null && !possibleOfferings.isEmpty()) {

                    CourseOffering chosenCourseOffering = possibleOfferings.get(random.nextInt(possibleOfferings.size()));

                    if (!courseEnrollmentRepository.existsByStudent_idAndCourseOffering_id(student.getId(), chosenCourseOffering.getId())) {
                        CourseEnrollment courseEnrollment = new CourseEnrollment();
                        courseEnrollment.setStudent(student);
                        chosenCourseOffering.addCourseEnrollments(List.of(courseEnrollment));
                        genericWebService.saveType(CourseEnrollment.class, courseEnrollment);
                    }
                }
            }

            for (int j = 0; j < 2; j++) {
                transaction = new Transaction();
                transaction.setTransactionPaidBy("HDFC STUDENT Bank");
                transaction.setTransactionDescription("Withdraw to STUDENT Bank account");
                transaction.setTransactionDetails("Transfer to HDFC STUDENT Bank via Secure3D");
                transaction.setStatus(transactionstatus.PENDING);
                transaction.setTransactionFee(new BigDecimal("4.82"));
                transaction.setTransactionAmount(new BigDecimal(random.nextInt(100)).add(BigDecimal.valueOf(j)));
                transaction.setTransactionTotal(new BigDecimal("562"));
                transaction.setCurrency(currency.USD);
                transaction.setBelongsTo(roles.STUDENT);
                transaction.setTransactionTime(Instant.now().minus(Duration.ofHours(j)));

                transactionService.saveTransaction(transaction);
                transactionService.addTransactionsToUser(student.getUserName(), Set.of(transaction.getTransactionId()));

                notificationService.notify(
                        notificationtype.BILLING,
                        "Transaction Been Made",
                        student.getFirstName() + " " + student.getLastName() + " just made a transaction",
                        "/user/dashboard",
                        student.getId());
            }
        }

        TeachingAssignment teachingAssignment;
        Enrollment enrollment;

        for (CourseOffering offering : offerings) {

            ClassSection classSection = new ClassSection();
            classSection.setClassTitle(offering.getSubject().getSubjectTitle() + " - Week 1");
            classSection.setClassDate(LocalDate.now());
            classSection.setSubject(offering.getSubject());

            genericWebService.saveType(ClassSection.class, classSection);

            teachingAssignment = new TeachingAssignment();
            teachingAssignment.setTeacher(offering.getTeacher());
            classSection.addAssignment(teachingAssignment);

            genericWebService.saveType(TeachingAssignment.class, teachingAssignment);

            List<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.findByCourseOffering_Id(offering.getId());
            for (CourseEnrollment courseEnrollment : courseEnrollments) {
                enrollment = new Enrollment();
                enrollment.setStudent(courseEnrollment.getStudent());
                enrollment.setPresent(random.nextBoolean());
                classSection.addEnrollment(enrollment);
                genericWebService.saveType(Enrollment.class, enrollment);
            }
        }

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setUserName("A-AAAA-AAAA-A");
        admin.setEmail("admin@admin.com");
        admin.setPassword(passwordEncoder.encode("1234Aa@"));
        admin.setNextDate(Instant.now().minus(Duration.ofDays(1)));
        admin.setBillingEnabled(false);
        admin.setPhoneNumber("+994519889192");

        admin.setBirthDate(LocalDate.of(2000, 1, 1));
        admin.setStreet("Example st.");
        admin.setCity("Example");
        admin.setRegion("CA");
        admin.setPostalCode(99999);
        admin.setCountry("Example");
        admin.setLanguage("English (United States)");
        admin.setZoneId("America/Guatemala");

        // admin.setTwoFactorEnabled(true);

        profilePicture = new ProfilePicture();
        profilePicture.setProfileTitle("Silly Profile Picture");
        profilePicture.setDescription("Silly Profile Picture Description");

        admin.setProfilePicture(profilePicture);
        profilePicture.setUser(admin);

        userService.saveUser(admin);

        roleService.addRolesToUser(admin.getId(), Set.of(roles.ADMIN.getRoleId()));

        for (int j = 0; j < 2; j++) {
            transaction = new Transaction();
            transaction.setTransactionPaidBy("HDFC ADMIN Bank");
            transaction.setTransactionDescription("Withdraw to ADMIN Bank account");
            transaction.setTransactionDetails("Transfer to HDFC ADMIN Bank via Secure3D");
            transaction.setStatus(transactionstatus.PENDING);
            transaction.setTransactionFee(new BigDecimal("4.82"));
            transaction.setTransactionAmount(new BigDecimal(random.nextInt(100)).add(BigDecimal.valueOf(j)));
            transaction.setTransactionTotal(new BigDecimal("562"));
            transaction.setCurrency(currency.USD);
            transaction.setBelongsTo(roles.ADMIN);
            transaction.setTransactionTime(Instant.now().minus(Duration.ofHours(j)));

            transactionService.saveTransaction(transaction);
            transactionService.addTransactionsToUser(admin.getUserName(), Set.of(transaction.getTransactionId()));

            notificationService.notify(
                    notificationtype.BILLING,
                    "Transaction Been Made",
                    admin.getFirstName() + " " + admin.getLastName() + " just made a transaction",
                    "/user/dashboard",
                    admin.getId());
        }

        Request request;

        for (int i = 0; i < 50; i++) {
            request = new Request();

            request.setEmail("example" + i + 1000 + "@gmail.com");
            request.setFirstName("Thomas");
            request.setLastName("Billy");

            request.setRole(roles.STUDENT);
            request.setRequestToken(TokenGenerator.generateToken());

            requestService.saveRequest(request);
        }

    }

}
