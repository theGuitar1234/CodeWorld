package az.codeworld.springboot.utilities.services.paymentservices;

import java.util.List;

import az.codeworld.springboot.admin.records.PaymentOverDueRecord;
import az.codeworld.springboot.utilities.constants.paymentDueStatus;

public interface PaymentOverDueService {
    void synchAllTeacherPayDues();
    void synchTeacherPayDue(Long teacherId);
    List<PaymentOverDueRecord> listPaymentOverDues(paymentDueStatus paymentDueStatus);
}
