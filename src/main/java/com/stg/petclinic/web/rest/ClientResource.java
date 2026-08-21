package com.stg.petclinic.web.rest;

import com.stg.petclinic.domain.Client;
import com.stg.petclinic.repository.AnimalRepository;
import com.stg.petclinic.repository.ClientRepository;
import com.stg.petclinic.service.ClientService;
import com.stg.petclinic.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.stg.petclinic.domain.Client}.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientResource.class);

    private static final String ENTITY_NAME = "client";

    @Value("${jhipster.clientApp.name:petclinic}")
    private String applicationName;

    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final AnimalRepository animalRepository;

    public ClientResource(ClientService clientService, ClientRepository clientRepository, AnimalRepository animalRepository) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.animalRepository = animalRepository;
    }

    @PostMapping("")
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) throws URISyntaxException {
        LOG.debug("REST request to save Client : {}", client);
        if (client.getId() != null) {
            throw new BadRequestAlertException("A new client cannot already have an ID", ENTITY_NAME, "idexists");
        }
        client = clientService.save(client);
        return ResponseEntity.created(new URI("/api/clients/" + client.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, client.getId().toString()))
            .body(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Client client
    ) throws URISyntaxException {
        LOG.debug("REST request to update Client : {}, {}", id, client);
        if (client.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, client.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        client = clientService.update(client);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, client.getId().toString()))
            .body(client);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Client> partialUpdateClient(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Client client
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Client partially : {}, {}", id, client);
        if (client.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, client.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Client> result = clientService.partialUpdate(client);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, client.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<Client>> getAllClients(
        @RequestParam(name = "query", required = false) String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of Clients with query: {}", query);
        Page<Client> page = clientService.findAll(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /clients/:id} : get the "id" client with his animals.
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        LOG.debug("REST request to get Client : {}", id);

        Optional<Client> client = clientRepository.findById(id);

        client.ifPresent(c -> c.setAnimals(new HashSet<>(animalRepository.findByClientId(id))));

        return ResponseUtil.wrapOrNotFound(client);
    }

    @DeleteMapping("/{id}")
    @Transactional // <--- Ajouté pour sécuriser la suppression en cascade
    public ResponseEntity<Void> deleteClient(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Client : {}", id);
        clientService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
