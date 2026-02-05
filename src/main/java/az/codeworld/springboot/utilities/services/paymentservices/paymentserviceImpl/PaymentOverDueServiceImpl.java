package az.codeworld.springboot.utilities.services.paymentservices.paymentserviceImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.entities.PaymentOverDue;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.records.PaymentOverDueRecord;
import az.codeworld.springboot.admin.repositories.PaymentOverDueRepository;
import az.codeworld.springboot.admin.repositories.TeacherRepository;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.paymentDueStatus;
import az.codeworld.springboot.utilities.services.paymentservices.PaymentOverDueService;
import jakarta.transaction.Transactional;

@Service
public class PaymentOverDueServiceImpl implements PaymentOverDueService {

    private final TeacherRepository teacherRepository;
    private final PaymentOverDueRepository paymentOverDueRepository;
    private final ApplicationProperties applicationProperties;

    private static final int GRACE_DAYS = 7;
    private static final int MAX_SYNCH_PAYMENT_OVER_DUE_MONTHS = 12*10;

    public PaymentOverDueServiceImpl(
        TeacherRepository teacherRepository,
        PaymentOverDueRepository paymentOverDueRepository,
        ApplicationProperties applicationProperties
    ) {
        this.teacherRepository = teacherRepository;
        this.paymentOverDueRepository = paymentOverDueRepository;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void synchAllTeacherPayDues() {
        List<Long> ids = teacherRepository.findAllIds();
        ids.forEach(i -> synchTeacherPayDue(i));
    }

    @Override
    @Transactional
    public void synchTeacherPayDue(Long teacherId) {
        Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);
        Teacher teacher = teacherOptional.orElseThrow(() -> new RuntimeException("Teacher not found by ID"));

        int loops = 0;

        while (Instant.now().isAfter(teacher.getNextDate().plus(Duration.ofDays(GRACE_DAYS)))) {
            if (++loops > MAX_SYNCH_PAYMENT_OVER_DUE_MONTHS) throw new IllegalStateException("Can't synch months beyond the safety limits for Teacher with ID of : " + teacherId);
            
            Instant dueDate = teacher.getNextDate();
            int year = dueDate.atZone(ZoneId.of(applicationProperties.getTime().getZone())).toLocalDate().getYear();
            int month = dueDate.atZone(ZoneId.of(applicationProperties.getTime().getZone())).toLocalDate().getMonthValue();
            
            PaymentOverDue paymentOverDue = new PaymentOverDue();

            paymentOverDue.setTeacher(teacher);
            paymentOverDue.setCycleYear(year);
            paymentOverDue.setCycleMonth(month);
            paymentOverDue.setDueDate(dueDate);
            paymentOverDue.setAmount(teacher.getPayment().getAmount());

            paymentOverDueRepository.save(paymentOverDue);

            teacher.updateNextPaymentDate();
        }

        // teacherRepository.save(teacher);
        // teacherRepository.flush();
    }

    @Override
    public List<PaymentOverDueRecord> listPaymentOverDues(paymentDueStatus paymentDueStatus) {
        return paymentOverDueRepository.findTop10ByPaymentDueStatusOrderByDueDateAsc(paymentDueStatus)
            .stream()
            .map(p -> new PaymentOverDueRecord(
                            p.getId(), 
                            p.getTeacher().getId(), 
                            p.getCycleYear(), 
                            p.getCycleMonth(), 
                            p.getDueDate()
                                .atZone(ZoneId.of(applicationProperties.getTime().getZone()))
                                .toLocalDateTime().format(DateTimeFormatter.ofPattern(applicationProperties.getTime().getDateTimeFormat())), 
                            p.getAmount(), 
                            p.getPaymentDueStatus().name(), 
                            p.getCreatedAt()
                                .atZone(ZoneId.of(applicationProperties.getTime().getZone()))
                                .toLocalDateTime().format(DateTimeFormatter.ofPattern(applicationProperties.getTime().getDateTimeFormat())), 
                            p.getPaidAt()
                                .atZone(ZoneId.of(applicationProperties.getTime().getZone()))
                                .toLocalDateTime().format(DateTimeFormatter.ofPattern(applicationProperties.getTime().getDateTimeFormat()))
                        ))
            .toList();
    }

    

    
    
}
