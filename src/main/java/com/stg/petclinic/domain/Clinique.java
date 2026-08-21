package com.stg.petclinic.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Clinique.
 */
@Entity
@Table(name = "clinique")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Clinique implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotBlank
    @Column(name = "adresse", nullable = false)
    private String adresse;

    @NotNull
    @Pattern(regexp = "^\+221[0-9]{9}$", message = "Le telephone doit etre au format senegalais : +221 suivi de 9 chiffres")
    @Column(name = "telephone", nullable = false)
    private String telephone;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clinique")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "rendezVouses", "clinique" }, allowSetters = true)
    private Set<Medecin> medecins = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clinique")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "animal", "clinique", "medecin", "peserAnimal" }, allowSetters = true)
    private Set<RendezVous> rendezVouses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Clinique id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Clinique nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public Clinique adresse(String adresse) {
        this.setAdresse(adresse);
        return this;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Clinique telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Set<Medecin> getMedecins() {
        return this.medecins;
    }

    public void setMedecins(Set<Medecin> medecins) {
        if (this.medecins != null) {
            this.medecins.forEach(i -> i.setClinique(null));
        }
        if (medecins != null) {
            medecins.forEach(i -> i.setClinique(this));
        }
        this.medecins = medecins;
    }

    public Clinique medecins(Set<Medecin> medecins) {
        this.setMedecins(medecins);
        return this;
    }

    public Clinique addMedecin(Medecin medecin) {
        this.medecins.add(medecin);
        medecin.setClinique(this);
        return this;
    }

    public Clinique removeMedecin(Medecin medecin) {
        this.medecins.remove(medecin);
        medecin.setClinique(null);
        return this;
    }

    public Set<RendezVous> getRendezVouses() {
        return this.rendezVouses;
    }

    public void setRendezVouses(Set<RendezVous> rendezVouses) {
        if (this.rendezVouses != null) {
            this.rendezVouses.forEach(i -> i.setClinique(null));
        }
        if (rendezVouses != null) {
            rendezVouses.forEach(i -> i.setClinique(this));
        }
        this.rendezVouses = rendezVouses;
    }

    public Clinique rendezVouses(Set<RendezVous> rendezVouses) {
        this.setRendezVouses(rendezVouses);
        return this;
    }

    public Clinique addRendezVous(RendezVous rendezVous) {
        this.rendezVouses.add(rendezVous);
        rendezVous.setClinique(this);
        return this;
    }

    public Clinique removeRendezVous(RendezVous rendezVous) {
        this.rendezVouses.remove(rendezVous);
        rendezVous.setClinique(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Clinique)) {
            return false;
        }
        return getId() != null && getId().equals(((Clinique) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Clinique{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", adresse='" + getAdresse() + "'" +
            ", telephone='" + getTelephone() + "'" +
            "}";
    }
}
