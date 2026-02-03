// package az.codeworld.springboot.security.filters;

// import java.io.IOException;
// import java.security.SecureRandom;
// import java.util.Base64;

// import org.springframework.web.filter.OncePerRequestFilter;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// public class CspNonceFilter extends OncePerRequestFilter {

//     private static final SecureRandom secureRandom = new SecureRandom();

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {
        
//         byte[] bytes = new byte[16];
//         secureRandom.nextBytes(bytes);

//         String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
//         request.setAttribute("cspNonce", nonce);

//         String csp = 
//             """
//                 default-src 'self'; 
//                 script-src 'self' 'nonce-'%s;
//                 style-src '*';
//                 img-src 'self';
//                 font-src 'self';
//                 connect-src 'self';
//                 frame-src 'self';
//                 object-src 'self';
//                 media-src 'self';
//                 frame-ancestors 'none';
//                 form-action 'self';
//                 base-uri 'self';
//             """.formatted(nonce);
        
//         response.setHeader("Content-Security-Policy", csp);

//         filterChain.doFilter(request, response);
//     }
    
// }
