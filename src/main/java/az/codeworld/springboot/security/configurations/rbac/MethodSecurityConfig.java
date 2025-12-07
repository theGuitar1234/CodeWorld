package az.codeworld.springboot.security.configurations.rbac;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;

@Configuration
public class MethodSecurityConfig {

    @Bean
    MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        var factory = new DefaultAuthorizationManagerFactory<MethodInvocation>();
        factory.setRoleHierarchy(roleHierarchy);

        var handler = new DefaultMethodSecurityExpressionHandler();
        handler.setAuthorizationManagerFactory(factory);
        return handler;
    }
}
