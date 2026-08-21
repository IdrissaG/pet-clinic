package com.stg.petclinic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests de validation métier sur l'entité PeserAnimal
 * Complète PeserAnimalTest.java (généré par JHipster, ne teste que equals/hashCode et les relations).
 */
class PeserAnimalValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldRejectNegativeWeight() {
        PeserAnimal peserAnimal = new PeserAnimal();
        peserAnimal.setPoids(-5.0);

        Set<ConstraintViolation<PeserAnimal>> violations = validator.validate(peserAnimal);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("poids"));
    }

    @Test
    void shouldRejectZeroWeight() {
        PeserAnimal peserAnimal = new PeserAnimal();
        peserAnimal.setPoids(0.0);

        Set<ConstraintViolation<PeserAnimal>> violations = validator.validate(peserAnimal);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("poids"));
    }

    @Test
    void shouldRejectNullWeight() {
        PeserAnimal peserAnimal = new PeserAnimal();
        peserAnimal.setPoids(null);

        Set<ConstraintViolation<PeserAnimal>> violations = validator.validate(peserAnimal);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("poids"));
    }

    @Test
    void shouldAcceptPositiveWeight() {
        PeserAnimal peserAnimal = new PeserAnimal();
        peserAnimal.setPoids(4.5);

        Set<ConstraintViolation<PeserAnimal>> violations = validator.validate(peserAnimal);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("poids"));
    }

    @Test
    void shouldAcceptSmallPositiveWeight() {
        // cas limite : très petit poids positif (ex. un chaton, un oisillon)
        PeserAnimal peserAnimal = new PeserAnimal();
        peserAnimal.setPoids(0.05);

        Set<ConstraintViolation<PeserAnimal>> violations = validator.validate(peserAnimal);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("poids"));
    }
}
