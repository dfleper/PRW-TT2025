package es.prw.features.auth.validation;

import org.springframework.stereotype.Component;

import es.prw.features.iam.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

	private final UserRepository userRepository;

	public UniqueEmailValidator(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		if (email == null || email.isBlank())
			return true;
		return !userRepository.existsByEmailIgnoreCase(email.trim());
	}
}
