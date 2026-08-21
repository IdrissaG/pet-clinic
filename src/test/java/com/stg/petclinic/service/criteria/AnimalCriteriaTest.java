package com.stg.petclinic.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AnimalCriteriaTest {

    @Test
    void newAnimalCriteriaHasAllFiltersNullTest() {
        var animalCriteria = new AnimalCriteria();
        assertThat(animalCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void animalCriteriaFluentMethodsCreatesFiltersTest() {
        var animalCriteria = new AnimalCriteria();

        setAllFilters(animalCriteria);

        assertThat(animalCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void animalCriteriaCopyCreatesNullFilterTest() {
        var animalCriteria = new AnimalCriteria();
        var copy = animalCriteria.copy();

        assertThat(animalCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(animalCriteria)
        );
    }

    @Test
    void animalCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var animalCriteria = new AnimalCriteria();
        setAllFilters(animalCriteria);

        var copy = animalCriteria.copy();

        assertThat(animalCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(animalCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var animalCriteria = new AnimalCriteria();

        assertThat(animalCriteria).hasToString("AnimalCriteria{}");
    }

    private static void setAllFilters(AnimalCriteria animalCriteria) {
        animalCriteria.id();
        animalCriteria.nom();
        animalCriteria.espece();
        animalCriteria.dateNaissance();
        animalCriteria.sexe();
        animalCriteria.clientId();
        animalCriteria.distinct();
    }

    private static Condition<AnimalCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getEspece()) &&
                condition.apply(criteria.getDateNaissance()) &&
                condition.apply(criteria.getSexe()) &&
                condition.apply(criteria.getClientId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AnimalCriteria> copyFiltersAre(AnimalCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getEspece(), copy.getEspece()) &&
                condition.apply(criteria.getDateNaissance(), copy.getDateNaissance()) &&
                condition.apply(criteria.getSexe(), copy.getSexe()) &&
                condition.apply(criteria.getClientId(), copy.getClientId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
