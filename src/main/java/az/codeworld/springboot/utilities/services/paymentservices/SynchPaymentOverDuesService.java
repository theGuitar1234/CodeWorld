package az.codeworld.springboot.utilities.services.paymentservices;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.services.paymentservices.PaymentOverDueService;

@Component
public interface SynchPaymentOverDuesService {
    public void synchPaymentOverDues();
}
