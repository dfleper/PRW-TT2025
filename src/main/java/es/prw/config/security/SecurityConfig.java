package es.prw.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

		http.authorizeHttpRequests(
				auth -> auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

						// Swagger / OpenAPI -> SOLO ADMIN
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
						.hasAnyAuthority("ADMIN", "ROLE_ADMIN")

						// Actuator -> SOLO ADMIN
						.requestMatchers("/actuator/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

						// ============================================================
						// (TEST 500): Permitir endpoint /dev/** (SOLO ADMIN)
						// ============================================================
						.requestMatchers("/dev/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

						// Público
						.requestMatchers("/", "/index", "/servicios", "/servicios/**", "/login", "/register", "/error",
								"/error/**", "/css/**", "/js/**", "/img/**")
						.permitAll()

						// Público
						.requestMatchers("/api/availability").permitAll()

						// Mis citas -> SOLO CLIENTE
						.requestMatchers("/cliente/citas/**").hasAnyAuthority("CLIENTE", "ROLE_CLIENTE")

						// Zona cliente
						.requestMatchers("/cliente/**").hasAnyAuthority("CLIENTE", "ROLE_CLIENTE")

						// ============================================================
						// Restricción real MECÁNICO
						// IMPORTANTE: reglas específicas ANTES de "/backoffice/**"
						// ============================================================

						// --- OTs: cerrar / piezas -> solo roles NO-mecánico ---
						.requestMatchers(HttpMethod.POST, "/backoffice/workorders/*/close",
								"/backoffice/workorders/*/parts", "/backoffice/workorders/*/parts/**")
						.hasAnyAuthority("RECEPCION", "ROLE_RECEPCION", "JEFE_TALLER", "ROLE_JEFE_TALLER", "ADMIN",
								"ROLE_ADMIN")

						// --- OTs: ver listado/detalle (GET) + guardar notas (POST /{id}) -> taller
						// completo ---
						.requestMatchers(HttpMethod.GET, "/backoffice/workorders", "/backoffice/workorders/*")
						.hasAnyAuthority("RECEPCION", "ROLE_RECEPCION", "MECANICO", "ROLE_MECANICO", "JEFE_TALLER",
								"ROLE_JEFE_TALLER", "ADMIN", "ROLE_ADMIN")
						.requestMatchers(HttpMethod.POST, "/backoffice/workorders/*")
						.hasAnyAuthority("RECEPCION", "ROLE_RECEPCION", "MECANICO", "ROLE_MECANICO", "JEFE_TALLER",
								"ROLE_JEFE_TALLER", "ADMIN", "ROLE_ADMIN")

						// --- Abrir OT desde cita: solo recepción/jefe/admin (mecánico no abre OTs) ---
						.requestMatchers(HttpMethod.POST, "/backoffice/citas/*/workorder")
						.hasAnyAuthority("RECEPCION", "ROLE_RECEPCION", "JEFE_TALLER", "ROLE_JEFE_TALLER", "ADMIN",
								"ROLE_ADMIN")

						// Backoffice general (sin mecánico)
						.requestMatchers("/backoffice/**")
						.hasAnyAuthority("RECEPCION", "ROLE_RECEPCION", "JEFE_TALLER", "ROLE_JEFE_TALLER", "ADMIN",
								"ROLE_ADMIN")

						// Admin
						.requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

						.anyRequest().authenticated());

		http.formLogin(
				form -> form.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/", true).permitAll());

		http.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").invalidateHttpSession(true)
				.deleteCookies("JSESSIONID").permitAll());

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
