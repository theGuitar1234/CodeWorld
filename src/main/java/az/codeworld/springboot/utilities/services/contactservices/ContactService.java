package az.codeworld.springboot.utilities.services.contactservices;

import az.codeworld.springboot.admin.entities.Teacher;

public interface ContactService {
    void notifyStudents();
    void notifyStudentsPastDueDate();
    void sendNotifyAdminEmail(String userName);
    void sendNotifyStudentEmail(Long studentId);
}
