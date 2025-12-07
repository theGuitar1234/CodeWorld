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

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.controllers.SecurityController;
import az.codeworld.springboot.security.entities.Authority;
import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.security.services.AuthorityService;
import az.codeworld.springboot.security.services.JpaUserDetailsService;
import az.codeworld.springboot.security.services.RoleService;
import az.codeworld.springboot.utilities.constants.authorities;
import az.codeworld.springboot.utilities.constants.currency;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.status;
import az.codeworld.springboot.web.entities.Student;
import az.codeworld.springboot.web.entities.Teacher;
import az.codeworld.springboot.web.entities.Transaction;
import az.codeworld.springboot.web.services.TransactionService;

@Component
public class SeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final RoleService roleService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final JpaUserDetailsService jpaUserDetailsService;

    public SeedData(
        PasswordEncoder passwordEncoder,
        AuthorityService authorityService,
        RoleService roleService,
        UserService userService, 
        JpaUserDetailsService jpaUserDetailsService,
        TransactionService transactionService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.authorityService = authorityService;
        this.roleService = roleService;
        this.userService = userService;
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.transactionService = transactionService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Seeding Data...");

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

        Transaction transaction;

        Teacher teacher;

        for (int i = 0; i<5; i++) {
            teacher = new Teacher();
            teacher.setUsername("T-AAAA-AAAA-" + i);
            teacher.setFirstName("Thomas");
            teacher.setLastName("Dhones");
            teacher.setEmail("example" + i + "@email.com");
            teacher.setPassword(passwordEncoder.encode("1234@Aa"));
            teacher.setCreatedAt(LocalDateTime.now());
            teacher.setPhoneNumber("+994 051 999 99 99");
            
            teacher.setDepartment("Biology");
            teacher.setTitle("Biology");
            teacher.setHiredAt(LocalDate.of(2005, 12, 4));
            teacher.setOfficeRoom("457-Jackson");
            teacher.setWage(20.53);
            teacher.setSalary(0.0);

            userService.saveUser(teacher);

            roleService.addRolesToUser(teacher.getUsername(), Set.of(roles.TEACHER.getRoleId()));
            jpaUserDetailsService.loadUserByUsername(teacher.getUsername());

            for (int j = 0; j<20; j++) {
                transaction = new Transaction();
                transaction.setTransactionPaidBy("HDFC Bank");
                transaction.setTransactionDescription("Withdraw to Bank account");
                transaction.setTransactionDetails("Transfer to HDFC Bank via Secure3D");
                transaction.setStatus(status.PENDING);
                transaction.setTransactionFee(BigDecimal.valueOf(4.82));
                transaction.setTransactionAmount(BigDecimal.valueOf(62));
                transaction.setTransactionTotal(BigDecimal.valueOf(562));
                transaction.setCurrency(currency.USD);

                transactionService.saveTransaction(transaction);
                transactionService.addTransactionsToUser(teacher.getUsername(), Set.of(transaction.getTransactionId()));
            }
        }

        Student student;
        
        for (int i = 0; i<20; i++) {
            student = new Student();
            int key = i+5;
            student.setUsername("S-AAAA-AAAA-" + key);
            student.setFirstName("James");
            student.setLastName("Dhones");
            student.setEmail("example" + i + 5 + "@email.com");
            student.setPassword(passwordEncoder.encode("1234@Aa"));
            student.setCreatedAt(LocalDateTime.now());
            student.setPhoneNumber("+9940519999999");

            student.setGroupName("684.23e");
            student.setYear(2);
            student.setMajor("IT");
            student.setEnrollmentDate(LocalDate.now());
            student.setGpa(89.99);

            userService.saveUser(student);

            roleService.addRolesToUser(student.getUsername(), Set.of(roles.STUDENT.getRoleId()));
            jpaUserDetailsService.loadUserByUsername(student.getUsername());

            // for (int j = 0; j<20; j++) {
            //     transaction = new Transaction();
            //     transaction.setTransactionTitle("HDFC Bank");
            //     transaction.setTransactionDescription("Withdraw to Bank account");
            //     transaction.setStatus(status.PENDING);
            //     transaction.setTransactionAmount(BigDecimal.valueOf(62));
            //     transaction.setCurrency(currency.USD);

            //     transaction.setUser(student);
            //     transactionService.saveTransaction(transaction);
            //     student.getTransactions().add(transaction);
            // }
        }
    }
    
}
