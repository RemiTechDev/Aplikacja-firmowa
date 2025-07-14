package com.aplikacja.Aplikacja.firmowa.Validation;

import com.aplikacja.Aplikacja.firmowa.Dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {}

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDto user = (UserDto) obj;
        return user.getPassword() != null &&
                user.getPassword().equals(user.getConfirmPassword());
    }
}