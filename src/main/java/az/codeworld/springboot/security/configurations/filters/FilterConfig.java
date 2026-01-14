// package az.codeworld.springboot.security.configurations.filters;

// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import az.codeworld.springboot.admin.services.UserService;
// import az.codeworld.springboot.security.filters.UserActivityFilter;

// @Configuration
// public class FilterConfig {

//     private final UserService userService;

//     FilterConfig(UserService userService) {
//         this.userService = userService;
//     }
    
//     @Bean
//     public FilterRegistrationBean<UserActivityFilter> userActivityFilter() {
//         FilterRegistrationBean<UserActivityFilter> registrationBean = new FilterRegistrationBean<>();
//         registrationBean.setFilter(new UserActivityFilter(userService));
//         return registrationBean;
//     }
// }
