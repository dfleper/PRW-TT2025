package es.prw.features.auth.validation;

import es.prw.features.auth.dto.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String p1 = value.getPassword();
        String p2 = value.getConfirmPassword();

        if (p1 == null || p2 == null) return true; // @NotBlank lo gestiona

        boolean ok = p1.equals(p2);
        if (!ok) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Las contraseñas no coinciden")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return ok;
    }
}
