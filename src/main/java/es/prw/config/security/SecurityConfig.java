package es.prw.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(auth -> auth
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

        .requestMatchers("/", "/index", "/servicios", "/servicios/**", "/login", "/register",
                "/error", "/error/**", "/css/**", "/js/**", "/img/**").permitAll()

        .requestMatchers("/api/availability").permitAll()

        .requestMatchers("/cliente/**").hasAnyAuthority("CLIENTE", "ROLE_CLIENTE")

        .requestMatchers("/backoffice/**").hasAnyAuthority(
            "RECEPCION", "ROLE_RECEPCION",
            "MECANICO", "ROLE_MECANICO",
            "JEFE_TALLER", "ROLE_JEFE_TALLER",
            "ADMIN", "ROLE_ADMIN"
        )

        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

        .anyRequest().authenticated()
    );

    http.formLogin(form -> form
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .defaultSuccessUrl("/", true)
        .permitAll()
    );

    http.logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .permitAll()
    );

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
