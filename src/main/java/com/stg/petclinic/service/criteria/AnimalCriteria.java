package com.stg.petclinic.service.criteria;

import com.stg.petclinic.domain.enumeration.Espece;
import com.stg.petclinic.domain.enumeration.Sexe;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.stg.petclinic.domain.Animal} entity. This class is used
 * in {@link com.stg.petclinic.web.rest.AnimalResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /animals?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AnimalCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Espece
     */
    public static class EspeceFilter extends Filter<Espece> {

        public EspeceFilter() {}

        public EspeceFilter(EspeceFilter filter) {
            super(filter);
        }

        @Override
        public EspeceFilter copy() {
            return new EspeceFilter(this);
        }
    }

    /**
     * Class for filtering Sexe
     */
    public static class SexeFilter extends Filter<Sexe> {

        public SexeFilter() {}

        public SexeFilter(SexeFilter filter) {
            super(filter);
        }

        @Override
        public SexeFilter copy() {
            return new SexeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private EspeceFilter espece;

    private LocalDateFilter dateNaissance;

    private SexeFilter sexe;

    private LongFilter clientId;

    private Boolean distinct;

    public AnimalCriteria() {}

    public AnimalCriteria(AnimalCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.espece = other.optionalEspece().map(EspeceFilter::copy).orElse(null);
        this.dateNaissance = other.optionalDateNaissance().map(LocalDateFilter::copy).orElse(null);
        this.sexe = other.optionalSexe().map(SexeFilter::copy).orElse(null);
        this.clientId = other.optionalClientId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AnimalCriteria copy() {
        return new AnimalCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNom() {
        return nom;
    }

    public Optional<StringFilter> optionalNom() {
        return Optional.ofNullable(nom);
    }

    public StringFilter nom() {
        if (nom == null) {
            setNom(new StringFilter());
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public EspeceFilter getEspece() {
        return espece;
    }

    public Optional<EspeceFilter> optionalEspece() {
        return Optional.ofNullable(espece);
    }

    public EspeceFilter espece() {
        if (espece == null) {
            setEspece(new EspeceFilter());
        }
        return espece;
    }

    public void setEspece(EspeceFilter espece) {
        this.espece = espece;
    }

    public LocalDateFilter getDateNaissance() {
        return dateNaissance;
    }

    public Optional<LocalDateFilter> optionalDateNaissance() {
        return Optional.ofNullable(dateNaissance);
    }

    public LocalDateFilter dateNaissance() {
        if (dateNaissance == null) {
            setDateNaissance(new LocalDateFilter());
        }
        return dateNaissance;
    }

    public void setDateNaissance(LocalDateFilter dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public SexeFilter getSexe() {
        return sexe;
    }

    public Optional<SexeFilter> optionalSexe() {
        return Optional.ofNullable(sexe);
    }

    public SexeFilter sexe() {
        if (sexe == null) {
            setSexe(new SexeFilter());
        }
        return sexe;
    }

    public void setSexe(SexeFilter sexe) {
        this.sexe = sexe;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public Optional<LongFilter> optionalClientId() {
        return Optional.ofNullable(clientId);
    }

    public LongFilter clientId() {
        if (clientId == null) {
            setClientId(new LongFilter());
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AnimalCriteria that = (AnimalCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(espece, that.espece) &&
            Objects.equals(dateNaissance, that.dateNaissance) &&
            Objects.equals(sexe, that.sexe) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, espece, dateNaissance, sexe, clientId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AnimalCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalEspece().map(f -> "espece=" + f + ", ").orElse("") +
            optionalDateNaissance().map(f -> "dateNaissance=" + f + ", ").orElse("") +
            optionalSexe().map(f -> "sexe=" + f + ", ").orElse("") +
            optionalClientId().map(f -> "clientId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
