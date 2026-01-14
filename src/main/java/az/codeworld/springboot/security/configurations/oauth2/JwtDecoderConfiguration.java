// package az.codeworld.springboot.security.configurations.oauth2;

// import java.net.Proxy;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.client.SimpleClientHttpRequestFactory;
// import org.springframework.security.oauth2.jwt.JwtDecoder;
// import org.springframework.security.oauth2.jwt.JwtValidators;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
// import org.springframework.web.client.RestOperations;
// import org.springframework.web.client.RestTemplate;

// @Configuration
// public class JwtDecoderConfiguration {

//     @Bean
//     RestOperations noProxyRestOperations() {
//         SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
//         f.setProxy(Proxy.NO_PROXY); 
//         return new RestTemplate(f);
//     }

//     @Bean
//     JwtDecoder jwtDecoder(RestOperations noProxyRestOperations) {
//         String issuer = "http://localhost:8080/realms/codeworld";

//         String jwks = "http://127.0.0.1:8080/realms/codeworld/protocol/openid-connect/certs";

//         NimbusJwtDecoder decoder = NimbusJwtDecoder
//                 .withJwkSetUri(jwks)
//                 .restOperations(noProxyRestOperations)
//                 .build();

//         decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));

//         return decoder;
//     }
// }
