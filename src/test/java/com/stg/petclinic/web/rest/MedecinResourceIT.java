package com.stg.petclinic.web.rest;

import static com.stg.petclinic.domain.MedecinAsserts.*;
import static com.stg.petclinic.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stg.petclinic.IntegrationTest;
import com.stg.petclinic.domain.Clinique;
import com.stg.petclinic.domain.Medecin;
import com.stg.petclinic.repository.MedecinRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link MedecinResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MedecinResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIALITE = "AAAAAAAAAA";
    private static final String UPDATED_SPECIALITE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "_`%,@i.lzRh:";
    private static final String UPDATED_EMAIL = "~7CPI.@P7q.j";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/medecins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MedecinRepository medecinRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMedecinMockMvc;

    private Medecin medecin;

    private Medecin insertedMedecin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medecin createEntity(EntityManager em) {
        Medecin medecin = new Medecin()
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .specialite(DEFAULT_SPECIALITE)
            .email(DEFAULT_EMAIL)
            .telephone(DEFAULT_TELEPHONE);
        // Add required entity
        Clinique clinique;
        if (TestUtil.findAll(em, Clinique.class).isEmpty()) {
            clinique = CliniqueResourceIT.createEntity();
            em.persist(clinique);
            em.flush();
        } else {
            clinique = TestUtil.findAll(em, Clinique.class).get(0);
        }
        medecin.setClinique(clinique);
        return medecin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medecin createUpdatedEntity(EntityManager em) {
        Medecin updatedMedecin = new Medecin()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE);
        // Add required entity
        Clinique clinique;
        if (TestUtil.findAll(em, Clinique.class).isEmpty()) {
            clinique = CliniqueResourceIT.createUpdatedEntity();
            em.persist(clinique);
            em.flush();
        } else {
            clinique = TestUtil.findAll(em, Clinique.class).get(0);
        }
        updatedMedecin.setClinique(clinique);
        return updatedMedecin;
    }

    @BeforeEach
    void initTest() {
        medecin = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMedecin != null) {
            medecinRepository.delete(insertedMedecin);
            insertedMedecin = null;
        }
    }

    @Test
    @Transactional
    void createMedecin() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Medecin
        var returnedMedecin = om.readValue(
            restMedecinMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Medecin.class
        );

        // Validate the Medecin in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMedecinUpdatableFieldsEquals(returnedMedecin, getPersistedMedecin(returnedMedecin));

        insertedMedecin = returnedMedecin;
    }

    @Test
    @Transactional
    void createMedecinWithExistingId() throws Exception {
        // Create the Medecin with an existing ID
        medecin.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMedecinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        medecin.setNom(null);

        // Create the Medecin, which fails.

        restMedecinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        medecin.setPrenom(null);

        // Create the Medecin, which fails.

        restMedecinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSpecialiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        medecin.setSpecialite(null);

        // Create the Medecin, which fails.

        restMedecinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        medecin.setEmail(null);

        // Create the Medecin, which fails.

        restMedecinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsInvalid() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set an email that does not match the expected pattern
        medecin.setEmail("not-an-email");

        // Create the Medecin, which fails.

        restMedecinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMedecins() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        // Get all the medecinList
        restMedecinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medecin.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].specialite").value(hasItem(DEFAULT_SPECIALITE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)));
    }

    @Test
    @Transactional
    void getMedecin() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        // Get the medecin
        restMedecinMockMvc
            .perform(get(ENTITY_API_URL_ID, medecin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(medecin.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.specialite").value(DEFAULT_SPECIALITE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE));
    }

    @Test
    @Transactional
    void getNonExistingMedecin() throws Exception {
        // Get the medecin
        restMedecinMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMedecin() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medecin
        Medecin updatedMedecin = medecinRepository.findById(medecin.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMedecin are not directly saved in db
        em.detach(updatedMedecin);
        updatedMedecin
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE);

        restMedecinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMedecin.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMedecin))
            )
            .andExpect(status().isOk());

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMedecinToMatchAllProperties(updatedMedecin);
    }

    @Test
    @Transactional
    void putNonExistingMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(put(ENTITY_API_URL_ID, medecin.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(medecin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMedecinWithPatch() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medecin using partial update
        Medecin partialUpdatedMedecin = new Medecin();
        partialUpdatedMedecin.setId(medecin.getId());

        partialUpdatedMedecin.email(UPDATED_EMAIL);

        restMedecinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMedecin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMedecin))
            )
            .andExpect(status().isOk());

        // Validate the Medecin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMedecinUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMedecin, medecin), getPersistedMedecin(medecin));
    }

    @Test
    @Transactional
    void fullUpdateMedecinWithPatch() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medecin using partial update
        Medecin partialUpdatedMedecin = new Medecin();
        partialUpdatedMedecin.setId(medecin.getId());

        partialUpdatedMedecin
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE);

        restMedecinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMedecin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMedecin))
            )
            .andExpect(status().isOk());

        // Validate the Medecin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMedecinUpdatableFieldsEquals(partialUpdatedMedecin, getPersistedMedecin(partialUpdatedMedecin));
    }

    @Test
    @Transactional
    void patchNonExistingMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, medecin.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(medecin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(medecin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMedecin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medecin.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMedecinMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(medecin)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Medecin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMedecin() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the medecin
        restMedecinMockMvc
            .perform(delete(ENTITY_API_URL_ID, medecin.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return medecinRepository.count();
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

    protected Medecin getPersistedMedecin(Medecin medecin) {
        return medecinRepository.findById(medecin.getId()).orElseThrow();
    }

    protected void assertPersistedMedecinToMatchAllProperties(Medecin expectedMedecin) {
        assertMedecinAllPropertiesEquals(expectedMedecin, getPersistedMedecin(expectedMedecin));
    }

    protected void assertPersistedMedecinToMatchUpdatableProperties(Medecin expectedMedecin) {
        assertMedecinAllUpdatablePropertiesEquals(expectedMedecin, getPersistedMedecin(expectedMedecin));
    }
}
