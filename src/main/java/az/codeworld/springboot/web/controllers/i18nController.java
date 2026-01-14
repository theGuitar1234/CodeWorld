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

    @GetMapping("/change-language")
    public String changeLanguage(@RequestParam("lang") String lang, @RequestParam("redirectUrl") String redirectUrl, HttpServletRequest request) {
        Locale newLocale = Locale.forLanguageTag(lang);
        HttpSession session = request.getSession();

        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, newLocale);

        return "redirect:" + redirectUrl;
    }

}

