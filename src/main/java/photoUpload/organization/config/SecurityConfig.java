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
            .cors(cors -> cors.configure(http)) 
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/users/**",
                    "/api/events/**",
                    "/api/photos/**",
                    "/uploads/**"
                ).permitAll() 
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {});

        return http.build();
  }
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
      org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
      configuration.setAllowedOrigins(java.util.List.of("*")); // Tüm kaynaklara izin ver
      configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // DELETE'i ekle
      configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
      
      org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
}
