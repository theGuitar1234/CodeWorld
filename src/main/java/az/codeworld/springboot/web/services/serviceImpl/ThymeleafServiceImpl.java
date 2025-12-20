package az.codeworld.springboot.web.services.serviceImpl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import az.codeworld.springboot.web.services.ThymeleafService;

@Service
public class ThymeleafServiceImpl implements ThymeleafService {

    private final TemplateEngine templateEngine;

    public ThymeleafServiceImpl(
        TemplateEngine templateEngine
    ) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String render(String templateName, Map<String, Object> variablies) {
        Context context = new Context();
        context.setVariables(variablies);
        return templateEngine.process(templateName, context);
    }

}
