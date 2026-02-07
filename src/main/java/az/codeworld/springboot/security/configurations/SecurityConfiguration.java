package az.codeworld.springboot.security.configurations;

import javax.security.auth.login.AccountExpiredException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
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

import az.codeworld.springboot.exceptions.UserBlockedException;
import az.codeworld.springboot.security.auth.handlers.LoginFailureHandler;
import az.codeworld.springboot.security.auth.handlers.LoginSuccessHandler;
import az.codeworld.springboot.security.auth.providers.DaoAuthenticationProvider;
import az.codeworld.springboot.security.filters.UserActivityFilter;
import az.codeworld.springboot.security.services.JpaUserDetailsService;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.exceptionmessages;
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

        private final DaoAuthenticationProvider daoAuthenticationProvider;

        public SecurityConfiguration(
                PasswordEncoder passwordEncoder,
                JpaUserDetailsService jpaUserDetailsService,
                SessionRegistry sessionRegistry, 
                JwtAuthenticationConverter jwtAuthenticationConverter,
                LoginSuccessHandler loginSuccessHandler,
                LoginFailureHandler loginFailureHandler,
                UserActivityFilter userActivityFilter,
                ApplicationProperties applicationProperties,
                DaoAuthenticationProvider daoAuthenticationProvider
        ) {
                this.passwordEncoder = passwordEncoder;
                this.jpaUserDetailsService = jpaUserDetailsService;
                this.sessionRegistry = sessionRegistry;
                this.jwtAuthenticationConverter = jwtAuthenticationConverter;
                this.loginSuccessHandler = loginSuccessHandler;
                this.loginFailureHandler = loginFailureHandler;
                this.userActivityFilter = userActivityFilter;
                this.applicationProperties = applicationProperties;
                this.daoAuthenticationProvider = daoAuthenticationProvider;
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
                "/.well-known/**",
                "/i18n/changeLanguage"
        };

        private static final String[] PUBLIC = {
                "/",
                "/about",
                "/restricted",
                "/register",
                "/user/register",
                "/user/login",
                "/user/logout",
                "/i18n/changeLanguage"
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

        private static final String[] PRE2FA = {
                "/restricted/2fa",
                "/restricted/2fa/",
                "/restricted/2fa/**"
        };

        private static final String[] PAYRIFF = {
                "/admin/payriff/callback",
                "/admin/payriff/callback"
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
                                .requestMatchers(PAYRIFF).permitAll()
                                .requestMatchers(PRE2FA).hasRole(roles.PRE_2FA.getRoleNameString())
                                .requestMatchers(ADMIN).hasRole(roles.ADMIN.getRoleNameString())
                                .anyRequest().access((authentication, context) -> {
                                        Authentication auth = authentication.get();
                                        if (auth == null || 
                                           !auth.isAuthenticated() || 
                                            auth instanceof AnonymousAuthenticationToken ||
                                            auth instanceof LockedException) 
                                                return new AuthorizationDecision(false);
                                        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_BANNED")))
                                                return new AuthorizationDecision(false);
                                        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_" + roles.PRE_2FA.getRoleNameString())))
                                                return new AuthorizationDecision(false);
                                        return new AuthorizationDecision(true);
                                }))
                        .formLogin(login -> login
                                .loginPage("/restricted/")
                                .loginProcessingUrl("/restricted/authenticate")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .failureUrl("/restricted/?error=Login%20Failure")
                                .defaultSuccessUrl("/?continue", true).permitAll()
                                .successHandler(loginSuccessHandler)
                                .failureHandler(loginFailureHandler)
                        )
                        .logout(logout -> logout
                                .logoutUrl("/user/logout")
                                .logoutSuccessUrl("/restricted/?success=Account+Deleted")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID", "remember-me")
                        )
                        .authenticationProvider(daoAuthenticationProvider)
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
                                        if (authenticationException instanceof BadCredentialsException ||
                                            authenticationException instanceof LockedException
                                        ) {
                                                response.sendRedirect("/restricted/?error=" + authenticationException.getMessage());
                                        } else {
                                                response.sendRedirect("/restricted/?error=" + exceptionmessages.getDefault());
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
