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
import az.codeworld.springboot.admin.controllers.UserController;
import az.codeworld.springboot.admin.dtos.create.TransactionCreateDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.projections.AdminContactProjection;
import az.codeworld.springboot.admin.projections.UserIdProjection;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.web.controllers.HomeController;
import az.codeworld.springboot.web.records.NotificationRecord;
import az.codeworld.springboot.web.services.NotificationService;

@ControllerAdvice(assignableTypes = { AdminController.class, UserController.class, HomeController.class, AccountController.class })
public class UserAdvicer {

    private UserService userService;

    public UserAdvicer(
        UserService userService
    ) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addUserData(Principal principal, Model model) {

        UserDashboardDTO userDashboardDTO = new UserDashboardDTO();
        AdminContactProjection adminContactProjection = (AdminContactProjection) userService.getUserProjectionByUserName("A-AAAA-AAAA-A", AdminContactProjection.class);

        if (principal != null) {
            userDashboardDTO = (UserDashboardDTO) userService.getUserByUserName(principal.getName(), dtotype.DASHBOARD);
        }

        model.addAllAttributes(
            Map.of(
                "user", userDashboardDTO,
                "adminContact", adminContactProjection,
                "transaction", new TransactionCreateDTO()
            )
        );
    }

}
