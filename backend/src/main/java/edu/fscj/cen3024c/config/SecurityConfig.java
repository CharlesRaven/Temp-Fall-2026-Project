package edu.fscj.cen3024c.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Each protected request supplies a Bearer token.
        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        http.csrf(csrf -> {
            csrf.ignoringRequestMatchers("/api/**");
        });

        http.authorizeHttpRequests(auth -> {
            auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();

            auth.requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll();
            auth.requestMatchers(HttpMethod.GET, "/api/courses/*/prerequisites",
                "/v3/api-docs", "/v3/api-docs/**",
                "/swagger-ui.html", "/swagger-ui/**").permitAll();

            auth.anyRequest().authenticated();
        });

        // Spring Security verifies the token for us.
        http.oauth2ResourceServer(oauth -> {
            oauth.jwt(Customizer.withDefaults());
        });

        return http.build();
    }
}
