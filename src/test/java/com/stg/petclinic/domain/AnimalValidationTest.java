package com.stg.petclinic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.stg.petclinic.domain.enumeration.Espece;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests de validation métier sur l'entité Animal
 * Complète AnimalTest.java (généré par JHipster, ne teste que equals/hashCode et les relations).
 */
class AnimalValidationTest {

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

    private Animal buildValidAnimal() {
        Animal animal = new Animal();
        animal.setNom("Rex");
        animal.setEspece(Espece.CHIEN);
        animal.setDateNaissance(LocalDate.now().minusYears(2));
        return animal;
    }

    @Test
    void shouldRejectFutureBirthDate() {
        Animal animal = buildValidAnimal();
        animal.setDateNaissance(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<Animal>> violations = validator.validate(animal);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dateNaissance"));
    }

    @Test
    void shouldAcceptTodayAsBirthDate() {
        Animal animal = buildValidAnimal();
        animal.setDateNaissance(LocalDate.now());

        Set<ConstraintViolation<Animal>> violations = validator.validate(animal);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("dateNaissance"));
    }

    @Test
    void shouldAcceptPastBirthDate() {
        Animal animal = buildValidAnimal();
        animal.setDateNaissance(LocalDate.now().minusYears(5));

        Set<ConstraintViolation<Animal>> violations = validator.validate(animal);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("dateNaissance"));
    }

    @Test
    void shouldRejectNullBirthDate() {
        Animal animal = buildValidAnimal();
        animal.setDateNaissance(null);

        Set<ConstraintViolation<Animal>> violations = validator.validate(animal);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dateNaissance"));
    }

    @Test
    void shouldRejectNullEspece() {
        Animal animal = buildValidAnimal();
        animal.setEspece(null);

        Set<ConstraintViolation<Animal>> violations = validator.validate(animal);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("espece"));
    }
}
