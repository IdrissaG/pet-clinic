package com.stg.petclinic.web.rest;

import static com.stg.petclinic.domain.MedecinAsserts.*;
import static com.stg.petclinic.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stg.petclinic.IntegrationTest;
import com.stg.petclinic.domain.Animal;
import com.stg.petclinic.domain.Client;
import com.stg.petclinic.domain.Clinique;
import com.stg.petclinic.domain.Medecin;
import com.stg.petclinic.domain.RendezVous;
import com.stg.petclinic.domain.enumeration.Espece;
import com.stg.petclinic.domain.enumeration.Sexe;
import com.stg.petclinic.repository.MedecinRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    void getAllMedecinsByCliniqueIdFilter() throws Exception {
        // Initialize the database with a medecin attached to its own clinique
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        // Create a second medecin attached to a different clinique
        Clinique otherClinique = CliniqueResourceIT.createUpdatedEntity();
        em.persist(otherClinique);
        em.flush();
        Medecin otherMedecin = new Medecin()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE)
            .clinique(otherClinique);
        medecinRepository.saveAndFlush(otherMedecin);

        // Filter by the first medecin's clinique: only it should be returned
        restMedecinMockMvc
            .perform(get(ENTITY_API_URL + "?cliniqueId=" + medecin.getClinique().getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medecin.getId().intValue())))
            .andExpect(jsonPath("$.[*].id").value(org.hamcrest.Matchers.not(hasItem(otherMedecin.getId().intValue()))));

        medecinRepository.delete(otherMedecin);
    }

    @Test
    @Transactional
    void getAllMedecinsBySpecialiteFilter() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        // Filter with a partial, differently-cased match: should still find it
        String partialLowerCase = DEFAULT_SPECIALITE.substring(0, 4).toLowerCase();
        restMedecinMockMvc
            .perform(get(ENTITY_API_URL + "?specialite=" + partialLowerCase))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medecin.getId().intValue())));

        // Filter with a specialite that does not match: should not find it
        restMedecinMockMvc
            .perform(get(ENTITY_API_URL + "?specialite=zzz-inexistant"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(org.hamcrest.Matchers.not(hasItem(medecin.getId().intValue()))));
    }

    @Test
    @Transactional
    void getAllMedecinsByCliniqueIdAndSpecialiteCombinedFilter() throws Exception {
        // medecinA matches both filters
        Medecin medecinA = (insertedMedecin = medecinRepository.saveAndFlush(medecin));

        // medecinB matches the specialite filter but belongs to a different clinique
        Clinique otherClinique = CliniqueResourceIT.createUpdatedEntity();
        em.persist(otherClinique);
        em.flush();
        Medecin medecinB = new Medecin()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(DEFAULT_SPECIALITE)
            .email(UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE)
            .clinique(otherClinique);
        medecinRepository.saveAndFlush(medecinB);

        // medecinC belongs to the same clinique but has a different specialite
        Medecin medecinC = new Medecin()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email("other." + UPDATED_EMAIL)
            .telephone(UPDATED_TELEPHONE)
            .clinique(medecinA.getClinique());
        medecinRepository.saveAndFlush(medecinC);

        String partialLowerCase = DEFAULT_SPECIALITE.substring(0, 4).toLowerCase();
        restMedecinMockMvc
            .perform(get(ENTITY_API_URL + "?cliniqueId=" + medecinA.getClinique().getId() + "&specialite=" + partialLowerCase))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(medecinA.getId().intValue())))
            .andExpect(jsonPath("$.[*].id").value(org.hamcrest.Matchers.not(hasItem(medecinB.getId().intValue()))))
            .andExpect(jsonPath("$.[*].id").value(org.hamcrest.Matchers.not(hasItem(medecinC.getId().intValue()))));

        medecinRepository.delete(medecinB);
        medecinRepository.delete(medecinC);
    }

    @Test
    @Transactional
    void getAllMedecinsByNonExistentCliniqueIdFilter() throws Exception {
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        restMedecinMockMvc
            .perform(get(ENTITY_API_URL + "?cliniqueId=999999999"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(org.hamcrest.Matchers.not(hasItem(medecin.getId().intValue()))));
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

    @Test
    @Transactional
    void deleteMedecinWithRendezVousShouldFail() throws Exception {
        // Initialize the database
        insertedMedecin = medecinRepository.saveAndFlush(medecin);

        // Attach a rendez-vous to this medecin
        // NB: built manually rather than via ClientResourceIT/AnimalResourceIT.createEntity() because the
        // default test telephone no longer matches Client's validation pattern (unrelated regression from G3).
        Client client = new Client()
            .nom("Test")
            .prenom("Client")
            .adresse("1 rue du Test")
            .telephone("781234567")
            .email("test.client@petclinic.fr");
        em.persist(client);
        Animal animal = new Animal()
            .nom("Rex")
            .espece(Espece.CHIEN)
            .dateNaissance(LocalDate.now().minusYears(2))
            .sexe(Sexe.MALE)
            .client(client);
        em.persist(animal);
        RendezVous rendezVous = new RendezVous()
            .date(Instant.now().plus(1, ChronoUnit.DAYS))
            .motif("Consultation")
            .duree(30.0)
            .animal(animal)
            .clinique(medecin.getClinique())
            .medecin(medecin);
        em.persist(rendezVous);
        em.flush();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Attempt to delete the medecin: must fail with a clear error, not a raw 500
        restMedecinMockMvc
            .perform(delete(ENTITY_API_URL_ID, medecin.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.medecinAvecRendezVous"));

        // Validate the medecin was not deleted
        assertSameRepositoryCount(databaseSizeBeforeDelete);

        em.remove(rendezVous);
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
