// package az.codeworld.springboot.security.auth.providers;

// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// import az.codeworld.springboot.security.services.JpaUserDetailsService;

// @Component
// public class DaoAuthenticationProvider implements AuthenticationProvider {

//     private final JpaUserDetailsService jpaUserDetailsService;
//     private final PasswordEncoder passwordEncoder;

//     public DaoAuthenticationProvider(JpaUserDetailsService jpaUserDetailsService, PasswordEncoder passwordEncoder) {
//         this.jpaUserDetailsService = jpaUserDetailsService;
//         this.passwordEncoder = passwordEncoder;
//     }

//     @Override
//     public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//         String email = authentication.getName();
//         String password = authentication.getCredentials().toString();

//         UserDetails userDetails;
        
//         try {
//             userDetails = jpaUserDetailsService.loadUserByUsername(email);
//         } catch (RuntimeException e) {
//             throw new BadCredentialsException("Email Not Found");
//         }

//         if (!passwordEncoder.matches(password, userDetails.getPassword())) {
//             throw new BadCredentialsException("Invalid Password");
//         }

//         return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
//     }

//     @Override
//     public boolean supports(Class<?> authentication) {
//         return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
//     }
    
// }
