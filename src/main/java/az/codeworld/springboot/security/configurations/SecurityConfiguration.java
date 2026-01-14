package az.codeworld.springboot.security.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import az.codeworld.springboot.security.auth.handlers.LoginFailureHandler;
import az.codeworld.springboot.security.auth.handlers.LoginSuccessHandler;
import az.codeworld.springboot.security.filters.UserActivityFilter;
import az.codeworld.springboot.security.services.JpaUserDetailsService;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.roles;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

        private final JwtAuthenticationConverter jwtAuthenticationConverter;
        private final JpaUserDetailsService jpaUserDetailsService;
        private final PasswordEncoder passwordEncoder;
        private final SessionRegistry sessionRegistry;

        private final LoginSuccessHandler loginSuccessHandler;
        private final LoginFailureHandler loginFailureHandler;

        private final UserActivityFilter userActivityFilter;

        private final ApplicationProperties applicationProperties;

        public SecurityConfiguration(
                PasswordEncoder passwordEncoder,
                JpaUserDetailsService jpaUserDetailsService,
                SessionRegistry sessionRegistry, 
                JwtAuthenticationConverter jwtAuthenticationConverter,
                LoginSuccessHandler loginSuccessHandler,
                LoginFailureHandler loginFailureHandler,
                UserActivityFilter userActivityFilter,
                ApplicationProperties applicationProperties
        ) {
                this.passwordEncoder = passwordEncoder;
                this.jpaUserDetailsService = jpaUserDetailsService;
                this.sessionRegistry = sessionRegistry;
                this.jwtAuthenticationConverter = jwtAuthenticationConverter;
                this.loginSuccessHandler = loginSuccessHandler;
                this.loginFailureHandler = loginFailureHandler;
                this.userActivityFilter = userActivityFilter;
                this.applicationProperties = applicationProperties;
        }

        private static final String[] WHITELIST = {
                "/restricted/**",
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
                "/restricted",
                "/register",
                "/user/register"
        };

        private static final String[] ADMIN = {
                "/admin/**",
                "/admin",
                "/photos/**"
        };

        private static final String[] API = {
                "/api/**",
                "/invoices/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        };

        @Value("${REMEMBER_ME_KEY:}")
        private String key;

        @Bean
        @Order(1)
        SecurityFilterChain apiSecurityFilterChain(
                HttpSecurity http
        ) throws Exception {
                return http
                        .securityMatcher(API)
                        .authorizeHttpRequests(requests -> requests
                                .requestMatchers(API).permitAll()
                                .anyRequest().denyAll()
                        )
                        .exceptionHandling(e -> e
                                .authenticationEntryPoint((request, response, exception) -> response.sendError(401))
                        )
                        .securityContext(securityContext -> securityContext
                                .requireExplicitSave(true))
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .oauth2ResourceServer(oauth2 -> oauth2
                                .jwt(jwt -> jwt
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter)
                                ))
                        .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                        .csrf(csrf -> csrf
                                        .ignoringRequestMatchers(
                                                "/db-console/**",
                                                "/swagger-ui.html",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**"
                                        )
                                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                        .build();
        }

        @Bean
        @Order(2)
        SecurityFilterChain webSecurityFilterChain(
                HttpSecurity http
        ) throws Exception {
                return http
                        .securityMatcher("/**")
                        .authorizeHttpRequests(requests -> requests
                                .requestMatchers(WHITELIST).permitAll()
                                .requestMatchers(PUBLIC).permitAll()
                                .requestMatchers(ADMIN).hasRole(roles.ADMIN.getRoleNameString())
                                .anyRequest().access((authentication, context) -> {
                                        Authentication auth = authentication.get();
                                        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) 
                                                return new AuthorizationDecision(false);
                                        return new AuthorizationDecision(true);
                                }))
                        .formLogin(login -> login
                                .loginPage("/restricted/")
                                .loginProcessingUrl("/restricted/authenticate")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .failureUrl("/restricted/?error")
                                .defaultSuccessUrl("/", true).permitAll()
                                .successHandler(loginSuccessHandler)
                                .failureHandler(loginFailureHandler)
                        )
                        .logout(logout -> logout
                                .logoutUrl("/user/logout")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                        )
                        .rememberMe(rememberMe -> rememberMe
                                .userDetailsService(jpaUserDetailsService)
                                .key(key)
                                .tokenValiditySeconds(applicationProperties.getRememberMe().getExpirySeconds())
                                .rememberMeParameter("remember-me")
                                .rememberMeCookieName("remember-me")
                                //.rememberMeCookieDomain("/**")
                                .useSecureCookie(true)
                        )
                        .exceptionHandling(exception -> exception.authenticationEntryPoint(
                                (request, response, authenticationException) -> {
                                        if (authenticationException instanceof BadCredentialsException) {
                                                response.sendRedirect("/restricted/?error=" + authenticationException.getMessage());
                                        } else {
                                                response.sendRedirect("/restricted/");
                                        }
                                }))
                        .securityContext(securityContext -> securityContext
                                .requireExplicitSave(true))
                        .userDetailsService(jpaUserDetailsService)
                        .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .maximumSessions(50)
                                .sessionRegistry(sessionRegistry)
                        )
                        .addFilterBefore(userActivityFilter, UsernamePasswordAuthenticationFilter.class)
                        .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                        .csrf(csrf -> csrf
                                        .ignoringRequestMatchers("/db-console/**")
                                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                        .build();
        }
}
