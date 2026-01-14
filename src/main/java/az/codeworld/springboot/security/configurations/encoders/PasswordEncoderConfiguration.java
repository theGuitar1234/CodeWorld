package az.codeworld.springboot.security.configurations.encoders;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfiguration {
    
    // @Bean
    // PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    // @Bean
    // PasswordEncoder passwordEncoder() {
    //     return new Argon2PasswordEncoder(
    //             16,       // Salt length in bytes
    //             32,      // Hash length in bytes
    //             1,      // Parallelism in number of threads
    //             16384, // Memory cost in kilobytes
    //             3     // The number of iterations
    //     );
    // }

    @Bean PasswordEncoder passwordEncoder() {
        //String defaultEncoder = "argon2";
        String defaultEncoder = "bcrypt";
        
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder(10));
        encoders.put("argon2id", new Argon2PasswordEncoder(16, 32, 1, 16384, 3));
        
        //return new DelegatingPasswordEncoder(defaultEncoder, encoders);
        
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(defaultEncoder, encoders);
        //delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(defaultEncoder));
        return delegatingPasswordEncoder;
    }
}
