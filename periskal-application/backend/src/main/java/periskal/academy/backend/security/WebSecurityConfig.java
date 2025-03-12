package periskal.academy.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions
            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/", "/login", "/api/auth/authenticate").permitAll() // Public endpoints
//                .anyRequest().authenticated()
                  .anyRequest().permitAll() // Allow all requests temporarily (remove in prod later) , Just keep this until we fully develop the authentication system
            )
            .httpBasic(withDefaults()); // Keep Basic Auth for testing (remove in prod)

        return http.build();
    }

}

    


