package az.codeworld.springboot.security.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import az.codeworld.springboot.security.services.JpaUserDetailsService;
import az.codeworld.springboot.utilities.constants.authorities;
import az.codeworld.springboot.utilities.constants.roles;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

        private final JpaUserDetailsService jpaUserDetailsService;
        private PasswordEncoder passwordEncoder;

        public SecurityConfiguration(
                PasswordEncoder passwordEncoder,
                JpaUserDetailsService jpaUserDetailsService
        ) {
                this.passwordEncoder = passwordEncoder;
                this.jpaUserDetailsService = jpaUserDetailsService;
        }

        private static final String[] WHITELIST = {
                        "/login/**",
                        "/db-console/**",
                        "/css/**",
                        "/fonts/**",
                        "/assets/**",
                        "/js/**",
                        "/user/signIn",
                        "/lib/**",
                        "/scss/**",
                        "/uploads/**",
                        "/health/**",
                        "/actuator/**",
                        "/error/**",
                        "/favicon.ico", "/favicon.png", "/favicon.*",
                        "/robots.txt", "/manifest.webmanifest",
                        "/.well-known/**"
        };

        private static final String[] PUBLIC = {
                        "/",
                        "/about",
                        "/course",
                        "/contact",
                        "/detail",
                        "/feature",
                        "/team",
                        "/testimonial",
                        "/register",
                        "/user/signUp"
        };

        @Bean
        SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {
                return http
                                .securityMatcher("/**")
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers(WHITELIST).permitAll()
                                                .requestMatchers(PUBLIC).permitAll()
                                                .requestMatchers("/admin/**").hasRole(roles.ADMIN.getRoleNameString())
                                                .anyRequest().access((authentication, context) -> {
                                                        Authentication auth = authentication.get();

                                                        if (auth == null ||
                                                                        !auth.isAuthenticated() ||
                                                                        auth instanceof AnonymousAuthenticationToken) {
                                                                return new AuthorizationDecision(false);
                                                        }
                                                        return new AuthorizationDecision(true);
                                                }))
                                .formLogin(login -> login
                                                .loginPage("/restricted/")
                                                .loginProcessingUrl("/restricted/authenticate")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .failureUrl("/restricted/?error")
                                                .defaultSuccessUrl("/", true).permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/user/logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))
                                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                                                (request, response, authenticationException) -> {
                                                        if (authenticationException instanceof BadCredentialsException) {
                                                                response.sendRedirect("/restricted/?error="
                                                                                + authenticationException.getMessage());
                                                        } else {
                                                                response.sendRedirect("/restricted/");
                                                        }
                                                }))
                                .securityContext(securityContext -> securityContext
                                                .requireExplicitSave(true))
                                .userDetailsService(jpaUserDetailsService)
                                .sessionManagement(session -> session
                                                .maximumSessions(50))
                                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/db-console/**")
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                                .build();
        }
}
