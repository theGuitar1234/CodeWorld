// package com.example.demo.web.configurations;
// package com.example.demo.Configurations;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Primary;
// import org.springframework.context.annotation.Profile;

// import com.example.demo.Repositories.UserRepository;
// import com.example.demo.ServiceImpl.JpaUserServiceImpl;
// import com.example.demo.ServiceImpl.JpaUserServiceImplDev;
// import com.example.demo.ServiceImpl.JpaUserServiceImplProd;
// import com.example.demo.ServiceImpl.JpaUserServiceImplTest;
// import com.example.demo.Services.UserService;

// @Configuration
// public class ApplicationConfiguration {

//     @Bean
//     // @Profile("default")
//     public UserService userService(UserRepository userRepository) {
//         return new JpaUserServiceImpl(userRepository);
//     }

//     @Bean
//     // @Profile("prod")
//     public UserService prodService(UserRepository userRepository) {
//         return new JpaUserServiceImplProd(userRepository);
//     }

//     @Bean
//     // @Profile("dev")
//     public UserService devService(UserRepository userRepository) {
//         return new JpaUserServiceImplDev(userRepository);
//     }

//     @Bean
//     // @Profile("test")
//     public UserService testService(UserRepository userRepository) {
//         return new JpaUserServiceImplTest(userRepository);
//     }
// }

