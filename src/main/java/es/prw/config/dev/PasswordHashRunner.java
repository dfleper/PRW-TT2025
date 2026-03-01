// Test contraseña texto plano a encriptada.

package es.prw.config.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component

@Profile("dev")
public class PasswordHashRunner implements CommandLineRunner {

	@Override
	public void run(String... args) {
		String plain = "1234";
		String hash = new BCryptPasswordEncoder().encode(plain);
		System.out.println("[BCrypt] " + plain + " -> " + hash);
	}
}
