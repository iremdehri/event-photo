package photoUpload.organization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // BU SATIRA YENİ YOLLARI EKLE:
                        .requestMatchers(
                                "/api/users/register",
                                "/api/events/**",
                                "/api/photos/**",
                                "/uploads/**",
                                "/api/users/**",
                                "/api/users/login",
                                "/api/users/update/**", // Profil güncelleme yolu
                                "/api/users/*/change-password", // Şifre değiştirme yolu
                                "/api/users/forgot-password",
                                "/api/users/verify-code",
                                "/api/users/reset-password",
                                "/api/users/delete/**"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {});

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Varsayılan güçte (10 log rounds) oluşturur
    }

}