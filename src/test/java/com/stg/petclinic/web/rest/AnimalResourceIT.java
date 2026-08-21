package com.stg.petclinic.web.rest;

import static com.stg.petclinic.domain.AnimalAsserts.*;
import static com.stg.petclinic.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stg.petclinic.IntegrationTest;
import com.stg.petclinic.domain.Animal;
import com.stg.petclinic.domain.Client;
import com.stg.petclinic.domain.enumeration.Espece;
import com.stg.petclinic.domain.enumeration.Sexe;
import com.stg.petclinic.repository.AnimalRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AnimalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AnimalResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Espece DEFAULT_ESPECE = Espece.CHIEN;
    private static final Espece UPDATED_ESPECE = Espece.CHAT;

    private static final LocalDate DEFAULT_DATE_NAISSANCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_NAISSANCE = LocalDate.parse("2026-08-17");
    private static final LocalDate SMALLER_DATE_NAISSANCE = LocalDate.ofEpochDay(-1L);

    private static final Sexe DEFAULT_SEXE = Sexe.MALE;
    private static final Sexe UPDATED_SEXE = Sexe.FEMELLE;

    private static final String ENTITY_API_URL = "/api/animals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAnimalMockMvc;

    private Animal animal;

    private Animal insertedAnimal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Animal createEntity(EntityManager em) {
        Animal animal = new Animal().nom(DEFAULT_NOM).espece(DEFAULT_ESPECE).dateNaissance(DEFAULT_DATE_NAISSANCE).sexe(DEFAULT_SEXE);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity();
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        animal.setClient(client);
        return animal;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Animal createUpdatedEntity(EntityManager em) {
        Animal updatedAnimal = new Animal()
            .nom(UPDATED_NOM)
            .espece(UPDATED_ESPECE)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .sexe(UPDATED_SEXE);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity();
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        updatedAnimal.setClient(client);
        return updatedAnimal;
    }

    @BeforeEach
    void initTest() {
        animal = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAnimal != null) {
            animalRepository.delete(insertedAnimal);
            insertedAnimal = null;
        }
    }

    @Test
    @Transactional
    void createAnimal() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Animal
        var returnedAnimal = om.readValue(
            restAnimalMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(animal)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Animal.class
        );

        // Validate the Animal in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAnimalUpdatableFieldsEquals(returnedAnimal, getPersistedAnimal(returnedAnimal));

        insertedAnimal = returnedAnimal;
    }

    @Test
    @Transactional
    void createAnimalWithExistingId() throws Exception {
        // Create the Animal with an existing ID
        animal.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnimalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(animal)))
            .andExpect(status().isBadRequest());

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        animal.setNom(null);

        // Create the Animal, which fails.

        restAnimalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(animal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEspeceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        animal.setEspece(null);

        // Create the Animal, which fails.

        restAnimalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(animal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateNaissanceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        animal.setDateNaissance(null);

        // Create the Animal, which fails.

        restAnimalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(animal)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAnimals() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList
        restAnimalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(animal.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].espece").value(hasItem(DEFAULT_ESPECE.toString())))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())));
    }

    @Test
    @Transactional
    void getAnimal() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get the animal
        restAnimalMockMvc
            .perform(get(ENTITY_API_URL_ID, animal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(animal.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.espece").value(DEFAULT_ESPECE.toString()))
            .andExpect(jsonPath("$.dateNaissance").value(DEFAULT_DATE_NAISSANCE.toString()))
            .andExpect(jsonPath("$.sexe").value(DEFAULT_SEXE.toString()));
    }

    @Test
    @Transactional
    void getAnimalsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        Long id = animal.getId();

        defaultAnimalFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAnimalFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAnimalFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAnimalsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where nom equals to
        defaultAnimalFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAnimalsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where nom in
        defaultAnimalFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAnimalsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where nom is not null
        defaultAnimalFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllAnimalsByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where nom contains
        defaultAnimalFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAnimalsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where nom does not contain
        defaultAnimalFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllAnimalsByEspeceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where espece equals to
        defaultAnimalFiltering("espece.equals=" + DEFAULT_ESPECE, "espece.equals=" + UPDATED_ESPECE);
    }

    @Test
    @Transactional
    void getAllAnimalsByEspeceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where espece in
        defaultAnimalFiltering("espece.in=" + DEFAULT_ESPECE + "," + UPDATED_ESPECE, "espece.in=" + UPDATED_ESPECE);
    }

    @Test
    @Transactional
    void getAllAnimalsByEspeceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where espece is not null
        defaultAnimalFiltering("espece.specified=true", "espece.specified=false");
    }

    @Test
    @Transactional
    void getAllAnimalsByDateNaissanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where dateNaissance equals to
        defaultAnimalFiltering("dateNaissance.equals=" + DEFAULT_DATE_NAISSANCE, "dateNaissance.equals=" + UPDATED_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllAnimalsByDateNaissanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where dateNaissance in
        defaultAnimalFiltering(
            "dateNaissance.in=" + DEFAULT_DATE_NAISSANCE + "," + UPDATED_DATE_NAISSANCE,
            "dateNaissance.in=" + UPDATED_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAnimalsByDateNaissanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where dateNaissance is not null
        defaultAnimalFiltering("dateNaissance.specified=true", "dateNaissance.specified=false");
    }

    @Test
    @Transactional
    void getAllAnimalsByDateNaissanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where dateNaissance is greater than or equal to
        defaultAnimalFiltering(
            "dateNaissance.greaterThanOrEqual=" + DEFAULT_DATE_NAISSANCE,
            "dateNaissance.greaterThanOrEqual=" + UPDATED_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAnimalsByDateNaissanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where dateNaissance is less than or equal to
        defaultAnimalFiltering(
            "dateNaissance.lessThanOrEqual=" + DEFAULT_DATE_NAISSANCE,
            "dateNaissance.lessThanOrEqual=" + SMALLER_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAnimalsByDateNaissanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where dateNaissance is less than
        defaultAnimalFiltering("dateNaissance.lessThan=" + UPDATED_DATE_NAISSANCE, "dateNaissance.lessThan=" + DEFAULT_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllAnimalsByDateNaissanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where dateNaissance is greater than
        defaultAnimalFiltering(
            "dateNaissance.greaterThan=" + SMALLER_DATE_NAISSANCE,
            "dateNaissance.greaterThan=" + DEFAULT_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAnimalsBySexeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where sexe equals to
        defaultAnimalFiltering("sexe.equals=" + DEFAULT_SEXE, "sexe.equals=" + UPDATED_SEXE);
    }

    @Test
    @Transactional
    void getAllAnimalsBySexeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where sexe in
        defaultAnimalFiltering("sexe.in=" + DEFAULT_SEXE + "," + UPDATED_SEXE, "sexe.in=" + UPDATED_SEXE);
    }

    @Test
    @Transactional
    void getAllAnimalsBySexeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        // Get all the animalList where sexe is not null
        defaultAnimalFiltering("sexe.specified=true", "sexe.specified=false");
    }

    @Test
    @Transactional
    void getAllAnimalsByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            animalRepository.saveAndFlush(animal);
            client = ClientResourceIT.createEntity();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        animal.setClient(client);
        animalRepository.saveAndFlush(animal);
        Long clientId = client.getId();
        // Get all the animalList where client equals to clientId
        defaultAnimalShouldBeFound("clientId.equals=" + clientId);

        // Get all the animalList where client equals to (clientId + 1)
        defaultAnimalShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    private void defaultAnimalFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAnimalShouldBeFound(shouldBeFound);
        defaultAnimalShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAnimalShouldBeFound(String filter) throws Exception {
        restAnimalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(animal.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].espece").value(hasItem(DEFAULT_ESPECE.toString())))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())));

        // Check, that the count call also returns 1
        restAnimalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAnimalShouldNotBeFound(String filter) throws Exception {
        restAnimalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAnimalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAnimal() throws Exception {
        // Get the animal
        restAnimalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAnimal() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the animal
        Animal updatedAnimal = animalRepository.findById(animal.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAnimal are not directly saved in db
        em.detach(updatedAnimal);
        updatedAnimal.nom(UPDATED_NOM).espece(UPDATED_ESPECE).dateNaissance(UPDATED_DATE_NAISSANCE).sexe(UPDATED_SEXE);

        restAnimalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAnimal.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAnimal))
            )
            .andExpect(status().isOk());

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAnimalToMatchAllProperties(updatedAnimal);
    }

    @Test
    @Transactional
    void putNonExistingAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnimalMockMvc
            .perform(put(ENTITY_API_URL_ID, animal.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(animal)))
            .andExpect(status().isBadRequest());

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnimalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(animal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnimalMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(animal)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAnimalWithPatch() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the animal using partial update
        Animal partialUpdatedAnimal = new Animal();
        partialUpdatedAnimal.setId(animal.getId());

        partialUpdatedAnimal.nom(UPDATED_NOM).espece(UPDATED_ESPECE).sexe(UPDATED_SEXE);

        restAnimalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnimal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAnimal))
            )
            .andExpect(status().isOk());

        // Validate the Animal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnimalUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAnimal, animal), getPersistedAnimal(animal));
    }

    @Test
    @Transactional
    void fullUpdateAnimalWithPatch() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the animal using partial update
        Animal partialUpdatedAnimal = new Animal();
        partialUpdatedAnimal.setId(animal.getId());

        partialUpdatedAnimal.nom(UPDATED_NOM).espece(UPDATED_ESPECE).dateNaissance(UPDATED_DATE_NAISSANCE).sexe(UPDATED_SEXE);

        restAnimalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnimal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAnimal))
            )
            .andExpect(status().isOk());

        // Validate the Animal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnimalUpdatableFieldsEquals(partialUpdatedAnimal, getPersistedAnimal(partialUpdatedAnimal));
    }

    @Test
    @Transactional
    void patchNonExistingAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnimalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, animal.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(animal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnimalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(animal))
            )
            .andExpect(status().isBadRequest());

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAnimal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        animal.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnimalMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(animal)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Animal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAnimal() throws Exception {
        // Initialize the database
        insertedAnimal = animalRepository.saveAndFlush(animal);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the animal
        restAnimalMockMvc
            .perform(delete(ENTITY_API_URL_ID, animal.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return animalRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Animal getPersistedAnimal(Animal animal) {
        return animalRepository.findById(animal.getId()).orElseThrow();
    }

    protected void assertPersistedAnimalToMatchAllProperties(Animal expectedAnimal) {
        assertAnimalAllPropertiesEquals(expectedAnimal, getPersistedAnimal(expectedAnimal));
    }

    protected void assertPersistedAnimalToMatchUpdatableProperties(Animal expectedAnimal) {
        assertAnimalAllUpdatablePropertiesEquals(expectedAnimal, getPersistedAnimal(expectedAnimal));
    }
}
