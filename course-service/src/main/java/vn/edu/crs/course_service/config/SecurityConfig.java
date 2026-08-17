package vn.edu.crs.course_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF để Postman có thể gửi POST, PUT, DELETE
                .csrf(csrf -> csrf.disable())

                // 2. Không lưu session (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Cho phép tất cả request vào /courses/** và /internal/**
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/courses/**", "/internal/**").permitAll()
                        .anyRequest().permitAll() // Cho phép tất cả trong buổi 2
                );

        return http.build();
    }
}