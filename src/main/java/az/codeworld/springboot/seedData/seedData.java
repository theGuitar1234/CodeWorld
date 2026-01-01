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
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.security.services.authservices.authservicesImpl.RegistrationServiceImplDev;
import az.codeworld.springboot.security.services.rbacservices.AuthorityService;
import az.codeworld.springboot.security.services.rbacservices.JpaUserDetailsService;
import az.codeworld.springboot.security.services.rbacservices.RoleService;
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

    private final RegistrationServiceImplDev registrationService;

    public SeedData(
            PasswordEncoder passwordEncoder,
            AuthorityService authorityService,
            RoleService roleService,
            UserService userService,
            JpaUserDetailsService jpaUserDetailsService,
            TransactionService transactionService,
            GenericWebService genericWebService,
            RequestService requestService,
            RegistrationServiceImplDev registrationService
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
        } catch (RuntimeException e) {}

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

        for (int i = 0; i < 5; i++) {
            teacher = new Teacher();
            if (teacher.getUsername() == null) teacher.setUsername(UsernameGenerator.generateUsername(roles.TEACHER.getRoleNameString()));
            System.out.println(teacher.getUsername());
            teacher.setFirstName("Thomas");
            teacher.setLastName("Dhones");
            teacher.setEmail("example" + i + "@email.com");
            teacher.setPassword(passwordEncoder.encode("1234@Aa"));
            teacher.setCreatedAt(LocalDateTime.now());
            teacher.setPhoneNumber("+994519889192");

            teacher.setDepartment("Biology");
            teacher.setTitle("Biology");
            teacher.setHiredAt(LocalDate.of(2005, 12, 4));
            teacher.setOfficeRoom("457-Jackson");
            teacher.setWage(20.53);
            teacher.setSalary(0.0);

            userService.saveUser(teacher);

            roleService.addRolesToUser(teacher.getUsername(), Set.of(roles.TEACHER.getRoleId()));
            jpaUserDetailsService.loadUserByUsername(teacher.getUsername());

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
                transaction.setTransactionFee(BigDecimal.valueOf(4.82));
                transaction.setTransactionAmount(BigDecimal.valueOf(62).add(BigDecimal.valueOf(j)));
                transaction.setTransactionTotal(BigDecimal.valueOf(562));
                transaction.setCurrency(currency.USD);
                transaction.setRole(roles.TEACHER);

                transactionService.saveTransaction(transaction);
                transactionService.addTransactionsToUser(teacher.getUsername(), Set.of(transaction.getTransactionId()));
            }
        }

        Student student;
        Enrollment enrollment;

        for (int i = 0; i < 20; i++) {
            student = new Student();
            if (student.getUsername() == null) student.setUsername(UsernameGenerator.generateUsername(roles.STUDENT.getRoleNameString()));
            student.setFirstName("James");
            student.setLastName("Dhones");
            student.setEmail("example" + i + 5 + "@email.com");
            student.setPassword(passwordEncoder.encode("1234@Aa"));
            student.setCreatedAt(LocalDateTime.now());
            student.setPhoneNumber("+994519889192");

            student.setGroupName("684.23e");
            student.setYear(2);
            student.setMajor("IT");
            student.setEnrollmentDate(LocalDate.now());
            student.setGpa(89.99);

            userService.saveUser(student);

            roleService.addRolesToUser(student.getUsername(), Set.of(roles.STUDENT.getRoleId()));
            jpaUserDetailsService.loadUserByUsername(student.getUsername());

            enrollment = new Enrollment();
            enrollment.setStudent(student);

            int clssctn = random.nextInt(3);

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
                transaction.setTransactionFee(BigDecimal.valueOf(4.82));
                transaction.setTransactionAmount(BigDecimal.valueOf(62));
                transaction.setTransactionTotal(BigDecimal.valueOf(562));
                transaction.setCurrency(currency.USD);
                transaction.setRole(roles.STUDENT);

                transactionService.saveTransaction(transaction);
                transactionService.addTransactionsToUser(student.getUsername(), Set.of(transaction.getTransactionId()));
            }
        }

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setUsername("A-AAAA-AAAA-A");
        admin.setEmail("admin@admin.com");
        admin.setPassword(passwordEncoder.encode("1234Aa@"));

        userService.saveUser(admin);
        roleService.addRolesToUser(admin.getUsername(), Set.of(roles.ADMIN.getRoleId()));

        for (int j = 0; j < 2; j++) {
            transaction = new Transaction();
            transaction.setTransactionPaidBy("HDFC ADMIN Bank");
            transaction.setTransactionDescription("Withdraw to ADMIN Bank account");
            transaction.setTransactionDetails("Transfer to HDFC ADMIN Bank via Secure3D");
            transaction.setStatus(transactionstatus.PENDING);
            transaction.setTransactionFee(BigDecimal.valueOf(4.82));
            transaction.setTransactionAmount(BigDecimal.valueOf(62));
            transaction.setTransactionTotal(BigDecimal.valueOf(562));
            transaction.setCurrency(currency.USD);
            transaction.setRole(roles.ADMIN);

            transactionService.saveTransaction(transaction);
            transactionService.addTransactionsToUser(admin.getUsername(), Set.of(transaction.getTransactionId()));
        }

        Request request;

        for (int i = 0; i < 50; i++) {
            request = new Request();

            request.setEmail("example" + i + 1000 + "@gmail.com");
            request.setFirstname("Thomas");
            request.setLastname("Billy");

            request.setRole(roles.STUDENT);
            request.setRequestToken(TokenGenerator.generateToken());

            requestService.saveRequest(request);
        }

    }

}
