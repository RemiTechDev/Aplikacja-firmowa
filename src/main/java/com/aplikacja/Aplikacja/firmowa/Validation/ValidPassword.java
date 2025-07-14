package com.aplikacja.Aplikacja.firmowa.Validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Hasło musi zawierać min. 8 znaków, dużą literę, cyfrę i znak specjalny";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}