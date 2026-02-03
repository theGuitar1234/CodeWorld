package az.codeworld.springboot.utilities.services.contactservices.contactservicesImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.StudentRepository;
import az.codeworld.springboot.admin.repositories.TeacherRepository;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.admin.services.StudentService;
import az.codeworld.springboot.admin.services.TeacherService;
import az.codeworld.springboot.aop.LogExecutionTime;
import az.codeworld.springboot.security.entities.EmailOutbox;
import az.codeworld.springboot.security.records.EmailRequestedEvent;
import az.codeworld.springboot.security.services.emailservices.EmailOutboxService;
import az.codeworld.springboot.security.services.emailservices.emailservicesImpl.EmailOutboxServiceImpl;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.services.contactservices.ContactService;
import az.codeworld.springboot.web.services.ThymeleafService;
import jakarta.transaction.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    private final UserRepository userRepository;

    private final EmailOutboxService emailOutboxService;
    private final ThymeleafService thymeleafService;
    private final StudentRepository studentRepository;
    private final ApplicationProperties applicationProperties;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TeacherRepository teacherRepository;

    public ContactServiceImpl(
            ThymeleafService thymeleafService,
            StudentRepository studentRepository,
            ApplicationProperties applicationProperties,
            EmailOutboxService emailOutboxService,
            ApplicationEventPublisher applicationEventPublisher,
            UserRepository userRepository,
            TeacherRepository teacherRepository) {
        this.thymeleafService = thymeleafService;
        this.studentRepository = studentRepository;
        this.applicationProperties = applicationProperties;
        this.emailOutboxService = emailOutboxService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    @LogExecutionTime("notifyStudents")
    @Scheduled(cron = "0 0 0 * * *")
    public void notifyStudents() {
        Instant cutoff = Instant.now();
        List<Student> students = studentRepository.findByNextDateBefore(cutoff);
        students.forEach(s -> {
            sendNotifyEmail(s);
        });
    }

    @Override
    @LogExecutionTime("notifyStudentsPastDueDate")
    @Scheduled(cron = "0 0 0 * * *")
    public void notifyStudentsPastDueDate() {
        Instant cutoff = Instant.now();
        List<Student> students = studentRepository.findByNextDateAfter(cutoff);
        students.forEach(s -> {
            sendNotifyPastDueDateEmail(s);
        });
    }

    @Transactional
    private void sendNotifyEmail(Student student) {

        String html = thymeleafService.render(
                "buss/notify",
                Map.of(
                        "name", student.getFirstName(),
                        "date", LocalDate.now().plusDays(7)));

        EmailOutbox emailOutbox = EmailOutbox.pending(
                student.getEmail(),
                "Your Payment Date is Near!",
                html);

        emailOutboxService.saveEmailOutbox(emailOutbox);
        applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));
    }

    @Transactional
    private void sendNotifyPastDueDateEmail(Student student) {

        String html = thymeleafService.render(
                "buss/notifyPastDueDate",
                Map.of(
                        "name", student.getFirstName(),
                        "date", student.getNextDate()));

        EmailOutbox emailOutbox = EmailOutbox.pending(
                student.getEmail(),
                "Your Payment Date is Passed!",
                html);

        emailOutboxService.saveEmailOutbox(emailOutbox);
        applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));
    }

    @Override
    @Transactional
    public void sendNotifyAdminEmail(String userName) {

        Optional<User> userOptional = userRepository.findByUserName("A-AAAA-AAAA-A");
        Optional<Teacher> teacherOptional = teacherRepository.findByUserName(userName);

        if (userOptional.isPresent() && teacherOptional.isPresent()) {

            User admin = userOptional.get();
            Teacher teacher = teacherOptional.get();

            String html = thymeleafService.render(
                    "buss/notifyAdmin",
                    Map.of(
                            "name", teacher.getFirstName(),
                            "date", teacher.getNextDate()));

            EmailOutbox emailOutbox = EmailOutbox.pending(
                    admin.getEmail(),
                    "You forgot to pay a teacher!",
                    html);

            emailOutboxService.saveEmailOutbox(emailOutbox);
            applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));

        }

    }

    @Override
    @Transactional
    public void sendNotifyStudentEmail(Long studentId) {

        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();

            String html = thymeleafService.render(
                    "buss/notifyStudent",
                    Map.of(
                            "name", student.getFirstName(),
                            "date", student.getNextDate()));

            EmailOutbox emailOutbox = EmailOutbox.pending(
                    student.getEmail(),
                    "You forgot your pay due!",
                    html);

            emailOutboxService.saveEmailOutbox(emailOutbox);
            applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));

        }

    }
}
