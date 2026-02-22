package az.codeworld.springboot.utilities.services.paymentservices.paymentserviceImpl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.entities.PaymentOverDue;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.admin.records.PaymentOverDueRecord;
import az.codeworld.springboot.admin.repositories.PaymentOverDueRepository;
import az.codeworld.springboot.admin.repositories.TeacherRepository;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.utilities.DateUtil;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.paymentDueStatus;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.transactionstatus;
import az.codeworld.springboot.utilities.services.paymentservices.PaymentOverDueService;
import jakarta.transaction.Transactional;

@Service
public class PaymentOverDueServiceImpl implements PaymentOverDueService {

    private final TeacherRepository teacherRepository;
    private final PaymentOverDueRepository paymentOverDueRepository;
    private final TransactionService transactionService;
    private final ApplicationProperties applicationProperties;

    private static final int GRACE_DAYS = 7;
    private static final int MAX_SYNCH_PAYMENT_OVER_DUE_MONTHS = 12 * 10;

    public PaymentOverDueServiceImpl(
            TeacherRepository teacherRepository,
            PaymentOverDueRepository paymentOverDueRepository,
            TransactionService transactionService,
            ApplicationProperties applicationProperties
    ) {
        this.teacherRepository = teacherRepository;
        this.paymentOverDueRepository = paymentOverDueRepository;
        this.transactionService = transactionService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Transactional
    public void synchAllTeacherPayDues() {
        List<Long> ids = teacherRepository.findAllIds();
        ids.forEach(this::synchTeacherPayDue);
    }

    @Override
    @Transactional
    public void synchTeacherPayDue(Long teacherId) {
        final ZoneId zone = ZoneId.of(applicationProperties.getTime().getZone());

        Teacher teacher = teacherRepository.lockById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found by ID"));

        int loops = 0;

        while (Instant.now().isAfter(teacher.getNextDate().plus(Duration.ofDays(GRACE_DAYS)))) {
            if (++loops > MAX_SYNCH_PAYMENT_OVER_DUE_MONTHS) {
                throw new IllegalStateException("Can't synch months beyond safety limit for Teacher ID: " + teacherId);
            }

            Instant dueDate = teacher.getNextDate();
            int year = dueDate.atZone(zone).toLocalDate().getYear();
            int month = dueDate.atZone(zone).toLocalDate().getMonthValue();

            boolean exists = paymentOverDueRepository.existsByTeacher_IdAndCycleYearAndCycleMonth(
                    teacher.getId(), year, month
            );

            if (!exists) {
                PaymentOverDue pod = new PaymentOverDue();
                pod.setTeacher(teacher);
                pod.setCycleYear(year);
                pod.setCycleMonth(month);
                pod.setDueDate(dueDate);
                pod.setAmount(teacher.getPayment().getAmount());
                pod.setStatus(paymentDueStatus.DUE);
                pod.setCreatedAt(Instant.now());

                paymentOverDueRepository.save(pod);
            }

            teacher.setNextDate(
                    DateUtil.addOneMonthClamped(dueDate.atZone(zone).toLocalDate())
                            .atStartOfDay(zone)
                            .toInstant()
            );
        }
    }

    @Override
    public List<PaymentOverDueRecord> listPaymentOverDues(paymentDueStatus status) {
        final ZoneId zone = ZoneId.of(applicationProperties.getTime().getZone());
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(applicationProperties.getTime().getDateTimeFormat());

        return paymentOverDueRepository
                .findByStatusWithTeacher(status, PageRequest.of(0, 10))
                .stream()
                .map(p -> new PaymentOverDueRecord(
                        p.getId(),
                        p.getTeacher().getFirstName() + " " + p.getTeacher().getLastName(),
                        p.getCycleYear(),
                        p.getCycleMonth(),
                        formatInstant(p.getDueDate(), zone, fmt),
                        p.getAmount(),
                        p.getStatus().name(),
                        formatInstant(p.getCreatedAt(), zone, fmt),
                        formatInstant(p.getPaidAt(), zone, fmt)
                ))
                .toList();
    }

    @Transactional
    public void payOverDue(Long overDueId, String paidBy, BigDecimal fee, String description) {
        if (fee == null) fee = BigDecimal.ZERO;
        if (fee.signum() < 0) throw new IllegalArgumentException("Fee can't be negative.");

        PaymentOverDue pod = paymentOverDueRepository.lockById(overDueId)
                .orElseThrow(() -> new RuntimeException("PaymentOverDue not found: " + overDueId));

        if (pod.getStatus() != paymentDueStatus.DUE) {
            throw new IllegalStateException("Overdue is not DUE (current: " + pod.getStatus() + ")");
        }

        Teacher teacher = pod.getTeacher();

        Transaction tx = new Transaction();
        tx.setBelongsTo(roles.TEACHER);
        tx.setCurrency(teacher.getPayment().getCurrency());
        tx.setStatus(transactionstatus.CHECKED);
        tx.setTransactionPaidBy((paidBy == null || paidBy.isBlank()) ? "ADMIN" : paidBy);

        tx.setTransactionAmount(pod.getAmount());
        tx.setTransactionFee(fee);
        tx.setTransactionTotal(pod.getAmount().add(fee));

        tx.setTransactionDescription(
                (description == null || description.isBlank())
                        ? ("Teacher overdue payment: " + pod.getCycleYear() + "-" + String.format("%02d", pod.getCycleMonth()))
                        : description
        );

        tx.setTransactionDetails("overDueId=" + pod.getId() + ", dueDate=" + pod.getDueDate());
        tx.setUser(teacher);

        transactionService.saveTransaction(tx);
        transactionService.addTransactionsToUser(teacher.getUserName(), Set.of(tx.getTransactionId()));

        pod.setStatus(paymentDueStatus.PAID);
        pod.setPaidAt(Instant.now());

        paymentOverDueRepository.save(pod);
    }

    private static String formatInstant(Instant i, ZoneId zone, DateTimeFormatter fmt) {
        return (i == null) ? "" : i.atZone(zone).toLocalDateTime().format(fmt);
    }
}