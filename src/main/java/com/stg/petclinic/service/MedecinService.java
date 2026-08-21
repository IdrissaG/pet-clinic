package com.stg.petclinic.service;

import com.stg.petclinic.domain.Medecin;
import com.stg.petclinic.repository.MedecinRepository;
import com.stg.petclinic.repository.RendezVousRepository;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.stg.petclinic.domain.Medecin}.
 */
@Service
@Transactional
public class MedecinService {

    private static final Logger LOG = LoggerFactory.getLogger(MedecinService.class);

    private final MedecinRepository medecinRepository;

    private final RendezVousRepository rendezVousRepository;

    public MedecinService(MedecinRepository medecinRepository, RendezVousRepository rendezVousRepository) {
        this.medecinRepository = medecinRepository;
        this.rendezVousRepository = rendezVousRepository;
    }

    /**
     * Save a medecin.
     *
     * @param medecin the entity to save.
     * @return the persisted entity.
     */
    public Medecin save(Medecin medecin) {
        LOG.debug("Request to save Medecin : {}", medecin);
        return medecinRepository.save(medecin);
    }

    /**
     * Update a medecin.
     *
     * @param medecin the entity to save.
     * @return the persisted entity.
     */
    public Medecin update(Medecin medecin) {
        LOG.debug("Request to update Medecin : {}", medecin);
        return medecinRepository.save(medecin);
    }

    /**
     * Partially update a medecin.
     *
     * @param medecin the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Medecin> partialUpdate(Medecin medecin) {
        LOG.debug("Request to partially update Medecin : {}", medecin);

        return medecinRepository
            .findById(medecin.getId())
            .map(existingMedecin -> {
                updateIfPresent(existingMedecin::setNom, medecin.getNom());
                updateIfPresent(existingMedecin::setPrenom, medecin.getPrenom());
                updateIfPresent(existingMedecin::setSpecialite, medecin.getSpecialite());
                updateIfPresent(existingMedecin::setEmail, medecin.getEmail());
                updateIfPresent(existingMedecin::setTelephone, medecin.getTelephone());

                return existingMedecin;
            })
            .map(medecinRepository::save);
    }

    /**
     * Get all the medecins.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Medecin> findAll(Pageable pageable) {
        LOG.debug("Request to get all Medecins");
        return medecinRepository.findAll(pageable);
    }

    /**
     * Get all the medecins matching the given filters.
     *
     * @param pageable the pagination information.
     * @param cliniqueId optional clinique id filter.
     * @param specialite optional specialite filter (partial, case-insensitive).
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Medecin> findAll(Pageable pageable, Long cliniqueId, String specialite) {
        LOG.debug("Request to get all Medecins with cliniqueId={}, specialite={}", cliniqueId, specialite);
        return medecinRepository.findByFilters(cliniqueId, specialite, pageable);
    }

    /**
     * Get one medecin by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Medecin> findOne(Long id) {
        LOG.debug("Request to get Medecin : {}", id);
        return medecinRepository.findById(id);
    }

    /**
     * Delete the medecin by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Medecin : {}", id);
        if (rendezVousRepository.existsByMedecinId(id)) {
            throw new MedecinAvecRendezVousException();
        }
        medecinRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
