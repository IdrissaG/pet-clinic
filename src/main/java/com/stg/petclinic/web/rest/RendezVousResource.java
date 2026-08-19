package com.stg.petclinic.web.rest;

import com.stg.petclinic.domain.RendezVous;
import com.stg.petclinic.repository.RendezVousRepository;
import com.stg.petclinic.service.RendezVousService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.stg.petclinic.domain.RendezVous}.
 */
@RestController
@RequestMapping("/api/rendez-vous")
public class RendezVousResource {

    private static final Logger LOG = LoggerFactory.getLogger(RendezVousResource.class);

    private static final String ENTITY_NAME = "rendezVous";

    @Value("${jhipster.clientApp.name:petclinic}")
    private String applicationName;

    private final RendezVousService rendezVousService;

    private final RendezVousRepository rendezVousRepository;

    public RendezVousResource(RendezVousService rendezVousService, RendezVousRepository rendezVousRepository) {
        this.rendezVousService = rendezVousService;
        this.rendezVousRepository = rendezVousRepository;
    }

    /**
     * {@code POST  /rendez-vous} : Create a new rendezVous.
     *
     * @param rendezVous the rendezVous to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rendezVous, or with status {@code 400 (Bad Request)} if the rendezVous has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RendezVous> createRendezVous(@Valid @RequestBody RendezVous rendezVous) throws URISyntaxException {
        LOG.debug("REST request to save RendezVous : {}", rendezVous);
        if (rendezVous.getId() != null) {
            throw new BadRequestAlertException("A new rendezVous cannot already have an ID", ENTITY_NAME, "idexists");
        }
        rendezVous = rendezVousService.save(rendezVous);
        return ResponseEntity.created(new URI("/api/rendez-vous/" + rendezVous.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, rendezVous.getId().toString()))
            .body(rendezVous);
    }

    /**
     * {@code PUT  /rendez-vous/:id} : Updates an existing rendezVous.
     *
     * @param id the id of the rendezVous to save.
     * @param rendezVous the rendezVous to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rendezVous,
     * or with status {@code 400 (Bad Request)} if the rendezVous is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rendezVous couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RendezVous> updateRendezVous(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RendezVous rendezVous
    ) throws URISyntaxException {
        LOG.debug("REST request to update RendezVous : {}, {}", id, rendezVous);
        if (rendezVous.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rendezVous.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rendezVousRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        rendezVous = rendezVousService.update(rendezVous);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rendezVous.getId().toString()))
            .body(rendezVous);
    }

    /**
     * {@code PATCH  /rendez-vous/:id} : Partial updates given fields of an existing rendezVous, field will ignore if it is null
     *
     * @param id the id of the rendezVous to save.
     * @param rendezVous the rendezVous to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rendezVous,
     * or with status {@code 400 (Bad Request)} if the rendezVous is not valid,
     * or with status {@code 404 (Not Found)} if the rendezVous is not found,
     * or with status {@code 500 (Internal Server Error)} if the rendezVous couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RendezVous> partialUpdateRendezVous(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RendezVous rendezVous
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RendezVous partially : {}, {}", id, rendezVous);
        if (rendezVous.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rendezVous.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rendezVousRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RendezVous> result = rendezVousService.partialUpdate(rendezVous);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rendezVous.getId().toString())
        );
    }

    /**
     * {@code GET  /rendez-vous} : get all the Rendez Vous.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Rendez Vous in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RendezVous>> getAllRendezVouses(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("peseranimal-is-null".equals(filter)) {
            LOG.debug("REST request to get all RendezVouss where peserAnimal is null");
            return new ResponseEntity<>(rendezVousService.findAllWherePeserAnimalIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of RendezVouses");
        Page<RendezVous> page = rendezVousService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rendez-vous/today} : get today's Rendez Vous (utilisé par le dashboard G6).
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of today's Rendez Vous in body.
     */
    @GetMapping("/today")
    public ResponseEntity<List<RendezVous>> getRendezVousDuJour() {
        LOG.debug("REST request to get today's RendezVouses");
        return ResponseEntity.ok(rendezVousService.findRendezVousDuJour());
    }

    /**
     * {@code GET  /rendez-vous/:id} : get the "id" rendezVous.
     *
     * @param id the id of the rendezVous to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rendezVous, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RendezVous> getRendezVous(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RendezVous : {}", id);
        Optional<RendezVous> rendezVous = rendezVousService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rendezVous);
    }

    /**
     * {@code DELETE  /rendez-vous/:id} : delete the "id" rendezVous.
     *
     * @param id the id of the rendezVous to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RendezVous : {}", id);
        rendezVousService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
