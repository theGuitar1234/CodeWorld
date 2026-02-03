package az.codeworld.springboot.security.filters;

import io.github.bucket4j.Bucket;
//import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
// import io.github.bucket4j.Bandwidth;
// import io.github.bucket4j.Refill;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String key = resolveClientKey(request);

        //boolean isRestricted = request.getRequestURI().startsWith("/restricted");
        boolean isRestricted = "POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().equals("/restricted/authenticate");
        String bucketKey = (isRestricted ? "LOGIN:" : "GENERAL:") + key;

        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> newBucketFor(isRestricted));

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            chain.doFilter(request, response);
            return;
        }

        long waitSeconds = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", String.valueOf(waitSeconds));
        response.setContentType("text/plain");
        response.getWriter().write("Too Many Requests - rate limit exceeded.");
    }

    private Bucket newBucketFor(boolean isLogin) {
        //boolean isLogin = request.getRequestURI().startsWith("/restricted");

        if (isLogin) {
            return Bucket
                    .builder()
                    .addLimit(l -> l
                            .capacity(5)
                            .refillGreedy(5, Duration.ofMinutes(1)))
                    .build();
        }

        // Bandwidth limit = isLogin
        // ? Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)))
        // : Bandwidth.classic(120, Refill.intervally(120, Duration.ofMinutes(1)));

        // return Bucket4j.builder().addLimit(limit).build();
        return Bucket.builder()
                .addLimit(l -> l.capacity(120).refillGreedy(120, Duration.ofMinutes(1)))
                .build();
    }

    private String resolveClientKey(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return p.startsWith("/css/")
                || p.startsWith("/js/")
                || p.startsWith("/assets/")
                || p.startsWith("/webjars/")
                || p.equals("/favicon.ico")
                || p.startsWith("/.well-known/")
                || p.startsWith("/error");
    }

}
