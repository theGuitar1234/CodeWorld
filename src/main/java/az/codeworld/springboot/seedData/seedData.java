package az.codeworld.springboot.seedData;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import az.codeworld.springboot.utilities.generators.TokenGenerator;
import az.codeworld.springboot.utilities.generators.UsernameGenerator;
import az.codeworld.springboot.web.entities.ClassSection;
import az.codeworld.springboot.web.entities.Enrollment;
import az.codeworld.springboot.web.entities.Subject;
import az.codeworld.springboot.web.entities.TeachingAssignment;
import az.codeworld.springboot.web.services.GenericWebService;

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

    private final RequestService requestService;

    private final GenericWebService genericWebService;

    private final RegistrationServiceImpl registrationService;

    public SeedData(
            PasswordEncoder passwordEncoder,
            AuthorityService authorityService,
            RoleService roleService,
            UserService userService,
            JpaUserDetailsService jpaUserDetailsService,
            TransactionService transactionService,
            GenericWebService genericWebService,
            RequestService requestService,
            RegistrationServiceImpl registrationService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.authorityService = authorityService;
        this.roleService = roleService;
        this.userService = userService;
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.transactionService = transactionService;
        this.genericWebService = genericWebService;
        this.requestService = requestService;
        this.registrationService = registrationService;
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

        math.setSubjectTitleString("Mathematics");
        math.setSubjectBodyString("Subject Body for Mathematics");

        physics.setSubjectTitleString("Physics");
        physics.setSubjectBodyString("Subject Body for Physics");

        computerScience.setSubjectTitleString("Computer Science");
        computerScience.setSubjectBodyString("Subject Body for Computer Science");

        genericWebService.saveType(Subject.class, math);
        genericWebService.saveType(Subject.class, physics);
        genericWebService.saveType(Subject.class, computerScience);

        ClassSection mathSection1 = new ClassSection();
        ClassSection physicsSection1 = new ClassSection();
        ClassSection computerScienceSection1 = new ClassSection();

        mathSection1.setClassTitle("Math Section 1");
        mathSection1.setSubject(math);
        physicsSection1.setClassTitle("Physics Section 1");
        physicsSection1.setSubject(physics);
        computerScienceSection1.setClassTitle("Computer Science Section 1");
        computerScienceSection1.setSubject(computerScience);

        genericWebService.saveType(ClassSection.class, mathSection1);
        genericWebService.saveType(ClassSection.class, physicsSection1);
        genericWebService.saveType(ClassSection.class, computerScienceSection1);

        Transaction transaction;

        Teacher teacher;
        TeachingAssignment teachingAssignment;

        teacher = new Teacher();
        teacher.setUserName("T-TTTT-TTTT-T");

        teacher.setFirstName("Thomas");
        teacher.setLastName("Dhones");
        teacher.setEmail("exampleTEACHER@email.com");
        teacher.setPassword(passwordEncoder.encode("1234Aa@"));
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setPhoneNumber("+994519889192");

        teacher.setDepartment("Biology");
        teacher.setTitle("Biology");
        teacher.setHiredAt(LocalDate.of(2005, 12, 4));
        teacher.setOfficeRoom("457-Jackson");
        teacher.setPayment(new Money(new BigDecimal("200.00"), currency.AZN));
        teacher.setNextDate((byte) 18);

        teacher.setBirthDate(LocalDate.of(1969, 1, 1));
        teacher.setStreet("Example st.");
        teacher.setCity("Example");
        teacher.setRegion("CA");
        teacher.setPostalCode(99999);
        teacher.setCountry("Example");
        teacher.setLanguage("English (United States)");
        teacher.setZoneId("America/Guatemala");

        userService.saveUser(teacher);

        roleService.addRolesToUser(teacher.getUserName(), Set.of(roles.TEACHER.getRoleId()));
        jpaUserDetailsService.loadUserByUsername(teacher.getUserName());

        teachingAssignment = new TeachingAssignment();
        teachingAssignment.setTeacher(teacher);

        int clssctn = random.nextInt(3);

        switch (clssctn) {
            case 0:
                physicsSection1.addAssignment(teachingAssignment);
                break;
            case 1:
                mathSection1.addAssignment(teachingAssignment);
                break;
            case 2:
                computerScienceSection1.addAssignment(teachingAssignment);
                break;
            default:
                break;
        }

        genericWebService.saveType(TeachingAssignment.class, teachingAssignment);

        for (int j = 0; j < 20; j++) {
            transaction = new Transaction();
            transaction.setTransactionPaidBy("HDFC TEACHER Bank");
            transaction.setTransactionDescription("Withdraw to TEACHER Bank account");
            transaction.setTransactionDetails("Transfer to HDFC Bank via Secure3D");
            transaction.setStatus(transactionstatus.PENDING);
            transaction.setTransactionFee(new BigDecimal("4.82"));
            transaction.setTransactionAmount(new BigDecimal("62").add(BigDecimal.valueOf(j)));
            transaction.setTransactionTotal(new BigDecimal("562"));
            transaction.setCurrency(currency.USD);
            transaction.setBelongsTo(roles.TEACHER);

            transactionService.saveTransaction(transaction);
            transactionService.addTransactionsToUser(teacher.getUserName(), Set.of(transaction.getTransactionId()));
        }

        for (int i = 0; i < 5; i++) {
            teacher = new Teacher();
            if (teacher.getUserName() == null)
                teacher.setUserName(UsernameGenerator.generateUsername(roles.TEACHER.getRoleNameString()));

            teacher.setFirstName("Thomas");
            teacher.setLastName("Dhones");
            teacher.setEmail("example" + i + "@email.com");
            teacher.setPassword(passwordEncoder.encode("1234Aa@"));
            teacher.setCreatedAt(LocalDateTime.now());
            teacher.setPhoneNumber("+994519889192");

            teacher.setDepartment("Biology");
            teacher.setTitle("Biology");
            teacher.setHiredAt(LocalDate.of(2005, 12, 4));
            teacher.setOfficeRoom("457-Jackson");
            teacher.setPayment(new Money(new BigDecimal("200.00"), currency.AZN));
            teacher.setNextDate((byte) 18);

            teacher.setBirthDate(LocalDate.of(1969, 1, 1));
            teacher.setStreet("Example st.");
            teacher.setCity("Example");
            teacher.setRegion("CA");
            teacher.setPostalCode(99999);
            teacher.setCountry("Example");
            teacher.setLanguage("English (United States)");
            teacher.setZoneId("America/Guatemala");

            userService.saveUser(teacher);

            roleService.addRolesToUser(teacher.getUserName(), Set.of(roles.TEACHER.getRoleId()));
            jpaUserDetailsService.loadUserByUsername(teacher.getUserName());

            teachingAssignment = new TeachingAssignment();
            teachingAssignment.setTeacher(teacher);

            clssctn = random.nextInt(3);

            switch (clssctn) {
                case 0:
                    physicsSection1.addAssignment(teachingAssignment);
                    break;
                case 1:
                    mathSection1.addAssignment(teachingAssignment);
                    break;
                case 2:
                    computerScienceSection1.addAssignment(teachingAssignment);
                    break;
                default:
                    break;
            }

            genericWebService.saveType(TeachingAssignment.class, teachingAssignment);

            for (int j = 0; j < 20; j++) {
                transaction = new Transaction();
                transaction.setTransactionPaidBy("HDFC TEACHER Bank");
                transaction.setTransactionDescription("Withdraw to TEACHER Bank account");
                transaction.setTransactionDetails("Transfer to HDFC Bank via Secure3D");
                transaction.setStatus(transactionstatus.PENDING);
                transaction.setTransactionFee(new BigDecimal("4.82"));
                transaction.setTransactionAmount(new BigDecimal("62").add(BigDecimal.valueOf(j)));
                transaction.setTransactionTotal(new BigDecimal("562"));
                transaction.setCurrency(currency.USD);
                transaction.setBelongsTo(roles.TEACHER);

                transactionService.saveTransaction(transaction);
                transactionService.addTransactionsToUser(teacher.getUserName(), Set.of(transaction.getTransactionId()));
            }
        }

        Student student;
        Enrollment enrollment;

        student = new Student();
        student.setUserName("S-SSSS-SSSS-S");
        student.setFirstName("James");
        student.setLastName("Dhones");
        student.setEmail("exampleSTUDENT@email.com");
        student.setPassword(passwordEncoder.encode("1234Aa@"));
        student.setCreatedAt(LocalDateTime.now());
        student.setPhoneNumber("+994519889192");

        student.setGroupName("684.23e");
        student.setYear(2);
        student.setMajor("IT");
        student.setEnrollmentDate(LocalDate.now());
        student.setGpa(89.99);
        student.setPayment(new Money(new BigDecimal("200.89"), currency.EURO));
        student.setNextDate((byte) 17);

        student.setBirthDate(LocalDate.of(2000, 1, 1));
        student.setStreet("Example st.");
        student.setCity("Example");
        student.setRegion("CA");
        student.setPostalCode(99999);
        student.setCountry("Example");
        student.setLanguage("English (United States)");
        student.setZoneId("America/Guatemala");

        userService.saveUser(student);

        roleService.addRolesToUser(student.getUserName(), Set.of(roles.STUDENT.getRoleId()));
        jpaUserDetailsService.loadUserByUsername(student.getUserName());

        enrollment = new Enrollment();
        enrollment.setStudent(student);

        clssctn = random.nextInt(3);

        switch (clssctn) {
            case 0:
                physicsSection1.addEnrollment(enrollment);
                break;
            case 1:
                mathSection1.addEnrollment(enrollment);
                break;
            case 2:
                computerScienceSection1.addEnrollment(enrollment);
                break;
            default:
                break;
        }

        genericWebService.saveType(Enrollment.class, enrollment);

        for (int j = 0; j < 2; j++) {
            transaction = new Transaction();
            transaction.setTransactionPaidBy("HDFC STUDENT Bank");
            transaction.setTransactionDescription("Withdraw to STUDENT Bank account");
            transaction.setTransactionDetails("Transfer to HDFC STUDENT Bank via Secure3D");
            transaction.setStatus(transactionstatus.PENDING);
            transaction.setTransactionFee(new BigDecimal("4.82"));
            transaction.setTransactionAmount(new BigDecimal("62").add(BigDecimal.valueOf(j)));
            transaction.setTransactionTotal(new BigDecimal("562"));
            transaction.setCurrency(currency.USD);
            transaction.setBelongsTo(roles.STUDENT);

            transactionService.saveTransaction(transaction);
            transactionService.addTransactionsToUser(student.getUserName(), Set.of(transaction.getTransactionId()));
        }

        for (int i = 0; i < 20; i++) {
            student = new Student();
            if (student.getUserName() == null)
                student.setUserName(UsernameGenerator.generateUsername(roles.STUDENT.getRoleNameString()));
            student.setFirstName("James");
            student.setLastName("Dhones");
            student.setEmail("example" + i + 5 + "@email.com");
            student.setPassword(passwordEncoder.encode("1234Aa@"));
            student.setCreatedAt(LocalDateTime.now());
            student.setPhoneNumber("+994519889192");

            student.setGroupName("684.23e");
            student.setYear(2);
            student.setMajor("IT");
            student.setEnrollmentDate(LocalDate.now());
            student.setGpa(89.99);
            student.setPayment(new Money(new BigDecimal("200.89"), currency.EURO));
            student.setNextDate((byte) 17);

            student.setBirthDate(LocalDate.of(2000, 1, 1));
            student.setStreet("Example st.");
            student.setCity("Example");
            student.setRegion("CA");
            student.setPostalCode(99999);
            student.setCountry("Example");
            student.setLanguage("English (United States)");
            student.setZoneId("America/Guatemala");

            userService.saveUser(student);

            roleService.addRolesToUser(student.getUserName(), Set.of(roles.STUDENT.getRoleId()));
            jpaUserDetailsService.loadUserByUsername(student.getUserName());

            enrollment = new Enrollment();
            enrollment.setStudent(student);

            clssctn = random.nextInt(3);

            switch (clssctn) {
                case 0:
                    physicsSection1.addEnrollment(enrollment);
                    break;
                case 1:
                    mathSection1.addEnrollment(enrollment);
                    break;
                case 2:
                    computerScienceSection1.addEnrollment(enrollment);
                    break;
                default:
                    break;
            }

            genericWebService.saveType(Enrollment.class, enrollment);

            for (int j = 0; j < 2; j++) {
                transaction = new Transaction();
                transaction.setTransactionPaidBy("HDFC STUDENT Bank");
                transaction.setTransactionDescription("Withdraw to STUDENT Bank account");
                transaction.setTransactionDetails("Transfer to HDFC STUDENT Bank via Secure3D");
                transaction.setStatus(transactionstatus.PENDING);
                transaction.setTransactionFee(new BigDecimal("4.82"));
                transaction.setTransactionAmount(new BigDecimal("62").add(BigDecimal.valueOf(j)));
                transaction.setTransactionTotal(new BigDecimal("562"));
                transaction.setCurrency(currency.USD);
                transaction.setBelongsTo(roles.STUDENT);

                transactionService.saveTransaction(transaction);
                transactionService.addTransactionsToUser(student.getUserName(), Set.of(transaction.getTransactionId()));
            }
        }

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setUserName("A-AAAA-AAAA-A");
        admin.setEmail("admin@admin.com");
        admin.setPassword(passwordEncoder.encode("1234Aa@"));

        userService.saveUser(admin);
        roleService.addRolesToUser(admin.getUserName(), Set.of(roles.ADMIN.getRoleId()));

        for (int j = 0; j < 2; j++) {
            transaction = new Transaction();
            transaction.setTransactionPaidBy("HDFC ADMIN Bank");
            transaction.setTransactionDescription("Withdraw to ADMIN Bank account");
            transaction.setTransactionDetails("Transfer to HDFC ADMIN Bank via Secure3D");
            transaction.setStatus(transactionstatus.PENDING);
            transaction.setTransactionFee(new BigDecimal("4.82"));
            transaction.setTransactionAmount(new BigDecimal("62").add(BigDecimal.valueOf(j)));
            transaction.setTransactionTotal(new BigDecimal("562"));
            transaction.setCurrency(currency.USD);
            transaction.setBelongsTo(roles.ADMIN);

            transactionService.saveTransaction(transaction);
            transactionService.addTransactionsToUser(admin.getUserName(), Set.of(transaction.getTransactionId()));
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
