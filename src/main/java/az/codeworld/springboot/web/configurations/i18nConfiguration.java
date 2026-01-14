package az.codeworld.springboot.web.configurations;

import java.time.Duration;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class i18nConfiguration {
     @Bean 
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setCookieHttpOnly(true);
        resolver.setCookieMaxAge(Duration.ofDays(1));
        resolver.setCookieSameSite("Strict");
        resolver.setCookieSecure(true);
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    // @Bean 
    // public MessageSource messageSource() {
    //     ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    //     messageSource.setBasename("file:i18n/messages");
    //     messageSource.setDefaultEncoding("UTF-8");
    //     messageSource.setUseCodeAsDefaultMessage(true);
    //     messageSource.setCacheSeconds(5);
    //     return messageSource;
    // }
}
