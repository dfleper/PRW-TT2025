package es.prw.config.security;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.prw.features.iam.repository.UserRepository;

@Component
public class LastLoginListener {

  private final UserRepository userRepository;

  public LastLoginListener(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @EventListener
  @Transactional
  public void onAuthSuccess(AuthenticationSuccessEvent event) {
    Authentication auth = event.getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
      return;
    }

    // Opcional: solo login de formulario (usuario/contraseña)
    if (!(auth instanceof UsernamePasswordAuthenticationToken)) {
      return;
    }

    String email = auth.getName(); // en tu caso es el email
    userRepository.updateLastLoginAtByEmail(email, LocalDateTime.now());
  }
}
