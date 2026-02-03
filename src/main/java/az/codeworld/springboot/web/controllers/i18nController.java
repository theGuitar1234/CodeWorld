package az.codeworld.springboot.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/i18n")
public class i18nController {

    @GetMapping("/changeLanguage")
    public String changeLanguage(
            @RequestParam("lang") String lang,
            @RequestParam(value = "redirectUrl", required = false) String redirectUrl,
            HttpServletRequest request) {
        Locale newLocale = Locale.forLanguageTag(lang);

        request.getSession(true)
                .setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, newLocale);

        String target = (redirectUrl == null || redirectUrl.isBlank()) ? "/" : redirectUrl;

        if (!target.startsWith("/"))
            target = "/";

        return "redirect:" + target;
    }

}
