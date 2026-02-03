package az.codeworld.springboot.web.filters;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import az.codeworld.springboot.utilities.constants.source;
import az.codeworld.springboot.web.services.ImpressionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ImpressionFilter extends OncePerRequestFilter {

    private final ImpressionService impressionService;

    public ImpressionFilter(
        ImpressionService impressionService
    ) {
        this.impressionService = impressionService;
    }

    private boolean isPublic(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }
        return uri.startsWith("/css")
                || uri.startsWith("/scss")
                || uri.startsWith("/js")
                || uri.startsWith("/lib")
                || uri.startsWith("/img")
                || uri.startsWith("/fonts")
                || uri.startsWith("/uploads")
                || uri.startsWith("/.well-known")
                || uri.startsWith("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        filterChain.doFilter(request, response);

        //if (!"GET".equals(request.getMethod())) return;
        if (response.getStatus() >= 400) return;

        if (isPublic(request)) return;

        if ("true".equals(request.getParameter("fragment"))) return;

        String contentType = response.getContentType();
        if (contentType == null || !contentType.contains("text/html")) return;

        String path = request.getRequestURI() /*+ (request.getQueryString() != null ? "?" + request.getQueryString() : "")*/;
        impressionService.recordImpression(request, path, source.SERVER);
    }
    
}
