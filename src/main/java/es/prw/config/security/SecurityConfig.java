package es.prw.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

        // ✅ Swagger / OpenAPI -> SOLO ADMIN
        .requestMatchers(
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
        ).hasAnyAuthority("ADMIN", "ROLE_ADMIN")

        // ✅ Actuator -> SOLO ADMIN
        .requestMatchers("/actuator/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

        // ✅ Dev tools -> SOLO ADMIN
        .requestMatchers("/dev/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

        // Público
        .requestMatchers(
            "/", "/index",
            "/servicios", "/servicios/**",
            "/login", "/register",
            "/error", "/error/**",
            "/css/**", "/js/**", "/img/**"
        ).permitAll()

        // Público (si lo quieres así)
        .requestMatchers("/api/availability").permitAll()

        // ✅ CHECKLIST: Mis citas -> SOLO CLIENTE (regla específica)
        .requestMatchers("/cliente/citas/**").hasAnyAuthority("CLIENTE", "ROLE_CLIENTE")

        // Zona cliente
        .requestMatchers("/cliente/**").hasAnyAuthority("CLIENTE", "ROLE_CLIENTE")

        // ✅ TAREA 15: Work Orders en Backoffice (roles del taller)
        .requestMatchers(
            "/backoffice/citas/*/workorder",
            "/backoffice/workorders/**"
        ).hasAnyAuthority(
            "RECEPCION", "ROLE_RECEPCION",
            "MECANICO", "ROLE_MECANICO",
            "JEFE_TALLER", "ROLE_JEFE_TALLER",
            "ADMIN", "ROLE_ADMIN"
        )

        // Backoffice general
        .requestMatchers("/backoffice/**").hasAnyAuthority(
            "RECEPCION", "ROLE_RECEPCION",
            "MECANICO", "ROLE_MECANICO",
            "JEFE_TALLER", "ROLE_JEFE_TALLER",
            "ADMIN", "ROLE_ADMIN"
        )

        // Admin
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
