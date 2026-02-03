package az.codeworld.springboot.utilities.services.paymentservices;

public interface PaymentOverDueService {
    
    void synchPaymentOverDues();
    void synchAllTeacherPayDues();
    void synchTeacherPayDue(Long teacherId);
}
