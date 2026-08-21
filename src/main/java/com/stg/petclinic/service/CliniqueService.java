package com.stg.petclinic.service;

import com.stg.petclinic.domain.Clinique;
import com.stg.petclinic.repository.CliniqueRepository;
import com.stg.petclinic.repository.MedecinRepository;
import com.stg.petclinic.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.stg.petclinic.domain.Clinique}.
 */
@Service
@Transactional
public class CliniqueService {

    private static final Logger LOG = LoggerFactory.getLogger(CliniqueService.class);

    private final CliniqueRepository cliniqueRepository;
    private final MedecinRepository medecinRepository;

    public CliniqueService(CliniqueRepository cliniqueRepository, MedecinRepository medecinRepository) {
        this.cliniqueRepository = cliniqueRepository;
        this.medecinRepository = medecinRepository;
    }

    /**
     * Save a clinique.
     *
     * @param clinique the entity to save.
     * @return the persisted entity.
     */
    public Clinique save(Clinique clinique) {
        LOG.debug("Request to save Clinique : {}", clinique);
        return cliniqueRepository.save(clinique);
    }

    /**
     * Update a clinique.
     *
     * @param clinique the entity to save.
     * @return the persisted entity.
     */
    public Clinique update(Clinique clinique) {
        LOG.debug("Request to update Clinique : {}", clinique);
        return cliniqueRepository.save(clinique);
    }

    /**
     * Partially update a clinique.
     *
     * @param clinique the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Clinique> partialUpdate(Clinique clinique) {
        LOG.debug("Request to partially update Clinique : {}", clinique);

        return cliniqueRepository
            .findById(clinique.getId())
            .map(existingClinique -> {
                updateIfPresent(existingClinique::setNom, clinique.getNom());
                updateIfPresent(existingClinique::setAdresse, clinique.getAdresse());
                updateIfPresent(existingClinique::setTelephone, clinique.getTelephone());

                return existingClinique;
            })
            .map(cliniqueRepository::save);
    }

    /**
     * Get all the cliniques.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Clinique> findAll(Pageable pageable) {
        LOG.debug("Request to get all Cliniques");
        return cliniqueRepository.findAll(pageable);
    }

    /**
     * Get one clinique by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Clinique> findOne(Long id) {
        LOG.debug("Request to get Clinique : {}", id);
        return cliniqueRepository.findById(id);
    }

    /**
     * Delete the clinique by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Clinique : {}", id);
        if (medecinRepository.existsByCliniqueId(id)) {
            throw new BadRequestAlertException(
                "Impossible de supprimer cette clinique : des medecins y sont rattaches",
                "clinique",
                "cliniquehasmedecins"
            );
        }
        cliniqueRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
