package az.codeworld.springboot.web.controlleradvicers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import az.codeworld.springboot.admin.controllers.AccountController;
import az.codeworld.springboot.admin.controllers.AdminController;
import az.codeworld.springboot.admin.controllers.StudentController;
import az.codeworld.springboot.admin.controllers.UserController;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.projections.UserIdProjection;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.web.controllers.HomeController;
import az.codeworld.springboot.web.records.NotificationRecord;
import az.codeworld.springboot.web.services.NotificationService;

@ControllerAdvice(assignableTypes = { AdminController.class, UserController.class, HomeController.class, AccountController.class, StudentController.class })
public class NotificationAdvicer {

    private UserService userService;
    private NotificationService notificationService;  

    public NotificationAdvicer(
        UserService userService,
        NotificationService notificationService
    ) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addNotificationData(Principal principal, Model model) {

        List<NotificationRecord> notificationProjections = new ArrayList<>();
        
        if (principal != null) {
            notificationProjections = notificationService.getLatestNotifications(
                userService.getUserProjectionByUserName(principal.getName(), UserIdProjection.class).getId()
            );
        }

        model.addAllAttributes(
            Map.of(
                "latestNotifications", notificationProjections
            )
        );
    }
}
