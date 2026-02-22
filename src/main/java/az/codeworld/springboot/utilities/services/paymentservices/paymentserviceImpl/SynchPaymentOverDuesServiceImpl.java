package az.codeworld.springboot.utilities.services.paymentservices.paymentserviceImpl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.services.paymentservices.PaymentOverDueService;
import az.codeworld.springboot.utilities.services.paymentservices.SynchPaymentOverDuesService;
import jakarta.transaction.Transactional;

@Component
public class SynchPaymentOverDuesServiceImpl implements SynchPaymentOverDuesService {
    
    private final PaymentOverDueService paymentOverDueService;

    public SynchPaymentOverDuesServiceImpl(
        PaymentOverDueService paymentOverDueService
    ) {
        this.paymentOverDueService = paymentOverDueService;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 5 0 * * *")
    public void synchPaymentOverDues() {
        paymentOverDueService.synchAllTeacherPayDues();
    }
}
