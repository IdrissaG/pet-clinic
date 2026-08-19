package com.stg.petclinic.web.rest;

import com.stg.petclinic.domain.Medecin;
import com.stg.petclinic.repository.MedecinRepository;
import com.stg.petclinic.service.MedecinService;
import com.stg.petclinic.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.stg.petclinic.domain.Medecin}.
 */
@RestController
@RequestMapping("/api/medecins")
public class MedecinResource {

    private static final Logger LOG = LoggerFactory.getLogger(MedecinResource.class);

    private static final String ENTITY_NAME = "medecin";

    @Value("${jhipster.clientApp.name:petclinic}")
    private String applicationName;

    private final MedecinService medecinService;

    private final MedecinRepository medecinRepository;

    public MedecinResource(MedecinService medecinService, MedecinRepository medecinRepository) {
        this.medecinService = medecinService;
        this.medecinRepository = medecinRepository;
    }

    /**
     * {@code POST  /medecins} : Create a new medecin.
     *
     * @param medecin the medecin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new medecin, or with status {@code 400 (Bad Request)} if the medecin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Medecin> createMedecin(@Valid @RequestBody Medecin medecin) throws URISyntaxException {
        LOG.debug("REST request to save Medecin : {}", medecin);
        if (medecin.getId() != null) {
            throw new BadRequestAlertException("A new medecin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        medecin = medecinService.save(medecin);
        return ResponseEntity.created(new URI("/api/medecins/" + medecin.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, medecin.getId().toString()))
            .body(medecin);
    }

    /**
     * {@code PUT  /medecins/:id} : Updates an existing medecin.
     *
     * @param id the id of the medecin to save.
     * @param medecin the medecin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated medecin,
     * or with status {@code 400 (Bad Request)} if the medecin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the medecin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Medecin> updateMedecin(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Medecin medecin
    ) throws URISyntaxException {
        LOG.debug("REST request to update Medecin : {}, {}", id, medecin);
        if (medecin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, medecin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!medecinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        medecin = medecinService.update(medecin);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, medecin.getId().toString()))
            .body(medecin);
    }

    /**
     * {@code PATCH  /medecins/:id} : Partial updates given fields of an existing medecin, field will ignore if it is null
     *
     * @param id the id of the medecin to save.
     * @param medecin the medecin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated medecin,
     * or with status {@code 400 (Bad Request)} if the medecin is not valid,
     * or with status {@code 404 (Not Found)} if the medecin is not found,
     * or with status {@code 500 (Internal Server Error)} if the medecin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Medecin> partialUpdateMedecin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Medecin medecin
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Medecin partially : {}, {}", id, medecin);
        if (medecin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, medecin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!medecinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Medecin> result = medecinService.partialUpdate(medecin);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, medecin.getId().toString())
        );
    }

    /**
     * {@code GET  /medecins} : get all the Medecins.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Medecins in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Medecin>> getAllMedecins(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) Long cliniqueId,
        @RequestParam(required = false) String specialite
    ) {
        LOG.debug("REST request to get a page of Medecins");
        Page<Medecin> page = medecinService.findAll(pageable, cliniqueId, specialite);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /medecins/:id} : get the "id" medecin.
     *
     * @param id the id of the medecin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the medecin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Medecin> getMedecin(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Medecin : {}", id);
        Optional<Medecin> medecin = medecinService.findOne(id);
        return ResponseUtil.wrapOrNotFound(medecin);
    }

    /**
     * {@code DELETE  /medecins/:id} : delete the "id" medecin.
     *
     * @param id the id of the medecin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedecin(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Medecin : {}", id);
        medecinService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
