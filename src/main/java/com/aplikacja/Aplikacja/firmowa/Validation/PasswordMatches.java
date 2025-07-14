package com.aplikacja.Aplikacja.firmowa.Validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "Hasła nie są zgodne";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
