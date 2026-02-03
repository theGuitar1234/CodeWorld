package az.codeworld.springboot.web.services;

import az.codeworld.springboot.utilities.constants.source;
import jakarta.servlet.http.HttpServletRequest;

public interface ImpressionService {
    void recordImpression(HttpServletRequest request, String path, source source);
    long countTotalImpressionsThisMonth();
}
