package az.codeworld.springboot.web.services;

import java.util.Map;

public interface ThymeleafService {
    String render(String templateName, Map<String, Object> variablies);
}
