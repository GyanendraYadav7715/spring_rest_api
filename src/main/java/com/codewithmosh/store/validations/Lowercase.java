package com.codewithmosh.store.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation definition
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LowercaseValidator.class) // You need to implement this validator
public @interface Lowercase {
    String message() default "must be in lowercase";

    Class<?>[] groups() default {}; // Fixed typo from "deafult" to "default"

    Class<? extends Payload>[] payload() default {}; // Fixed case: "Payload" to "payload"
}
