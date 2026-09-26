package com.examguard.web.rest;

import static com.examguard.domain.AttemptAsserts.*;
import static com.examguard.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.examguard.IntegrationTest;
import com.examguard.domain.Attempt;
import com.examguard.repository.AttemptRepository;
import com.examguard.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link AttemptResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AttemptResourceIT {

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.ofEpochMilli(1703161763127L);

    private static final Instant DEFAULT_ENDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENDED_AT = Instant.ofEpochMilli(1703161763127L);

    private static final Boolean DEFAULT_SUBMITTED = false;
    private static final Boolean UPDATED_SUBMITTED = true;

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final String ENTITY_API_URL = "/api/attempts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAttemptMockMvc;

    private Attempt attempt;

    private Attempt insertedAttempt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attempt createEntity() {
        return new Attempt().startedAt(DEFAULT_STARTED_AT).endedAt(DEFAULT_ENDED_AT).submitted(DEFAULT_SUBMITTED).score(DEFAULT_SCORE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attempt createUpdatedEntity() {
        return new Attempt().startedAt(UPDATED_STARTED_AT).endedAt(UPDATED_ENDED_AT).submitted(UPDATED_SUBMITTED).score(UPDATED_SCORE);
    }

    @BeforeEach
    void initTest() {
        attempt = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAttempt != null) {
            attemptRepository.delete(insertedAttempt);
            insertedAttempt = null;
        }
    }

    @Test
    @Transactional
    void createAttempt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Attempt
        var returnedAttempt = om.readValue(
            restAttemptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attempt)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Attempt.class
        );

        // Validate the Attempt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAttemptUpdatableFieldsEquals(returnedAttempt, getPersistedAttempt(returnedAttempt));

        insertedAttempt = returnedAttempt;
    }

    @Test
    @Transactional
    void createAttemptWithExistingId() throws Exception {
        // Create the Attempt with an existing ID
        attempt.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attempt)))
            .andExpect(status().isBadRequest());

        // Validate the Attempt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attempt.setStartedAt(null);

        // Create the Attempt, which fails.

        restAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attempt)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attempt.setEndedAt(null);

        // Create the Attempt, which fails.

        restAttemptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attempt)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAttempts() throws Exception {
        // Initialize the database
        insertedAttempt = attemptRepository.saveAndFlush(attempt);

        // Get all the attemptList
        restAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].endedAt").value(hasItem(DEFAULT_ENDED_AT.toString())))
            .andExpect(jsonPath("$.[*].submitted").value(hasItem(DEFAULT_SUBMITTED)))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)));
    }

    @Test
    @Transactional
    void getAttempt() throws Exception {
        // Initialize the database
        insertedAttempt = attemptRepository.saveAndFlush(attempt);

        // Get the attempt
        restAttemptMockMvc
            .perform(get(ENTITY_API_URL_ID, attempt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(attempt.getId().intValue()))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.endedAt").value(DEFAULT_ENDED_AT.toString()))
            .andExpect(jsonPath("$.submitted").value(DEFAULT_SUBMITTED))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE));
    }

    @Test
    @Transactional
    void getNonExistingAttempt() throws Exception {
        // Get the attempt
        restAttemptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAttempt() throws Exception {
        // Initialize the database
        insertedAttempt = attemptRepository.saveAndFlush(attempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attempt
        Attempt updatedAttempt = attemptRepository.findById(attempt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAttempt are not directly saved in db
        em.detach(updatedAttempt);
        updatedAttempt.startedAt(UPDATED_STARTED_AT).endedAt(UPDATED_ENDED_AT).submitted(UPDATED_SUBMITTED).score(UPDATED_SCORE);

        restAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAttempt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAttempt))
            )
            .andExpect(status().isOk());

        // Validate the Attempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAttemptToMatchAllProperties(updatedAttempt);
    }

    @Test
    @Transactional
    void putNonExistingAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attempt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttemptMockMvc
            .perform(put(ENTITY_API_URL_ID, attempt.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attempt)))
            .andExpect(status().isBadRequest());

        // Validate the Attempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttemptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attempt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Attempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedAttempt = attemptRepository.saveAndFlush(attempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attempt using partial update
        Attempt partialUpdatedAttempt = new Attempt();
        partialUpdatedAttempt.setId(attempt.getId());

        partialUpdatedAttempt.score(UPDATED_SCORE);

        restAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttempt))
            )
            .andExpect(status().isOk());

        // Validate the Attempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttemptUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAttempt, attempt), getPersistedAttempt(attempt));
    }

    @Test
    @Transactional
    void fullUpdateAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedAttempt = attemptRepository.saveAndFlush(attempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attempt using partial update
        Attempt partialUpdatedAttempt = new Attempt();
        partialUpdatedAttempt.setId(attempt.getId());

        partialUpdatedAttempt.startedAt(UPDATED_STARTED_AT).endedAt(UPDATED_ENDED_AT).submitted(UPDATED_SUBMITTED).score(UPDATED_SCORE);

        restAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttempt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttempt))
            )
            .andExpect(status().isOk());

        // Validate the Attempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttemptUpdatableFieldsEquals(partialUpdatedAttempt, getPersistedAttempt(partialUpdatedAttempt));
    }

    @Test
    @Transactional
    void patchNonExistingAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attempt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, attempt.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(attempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attempt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attempt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttemptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(attempt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Attempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAttempt() throws Exception {
        // Initialize the database
        insertedAttempt = attemptRepository.saveAndFlush(attempt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the attempt
        restAttemptMockMvc
            .perform(delete(ENTITY_API_URL_ID, attempt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return attemptRepository.count();
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

    protected Attempt getPersistedAttempt(Attempt attempt) {
        return attemptRepository.findById(attempt.getId()).orElseThrow();
    }

    protected void assertPersistedAttemptToMatchAllProperties(Attempt expectedAttempt) {
        assertAttemptAllPropertiesEquals(expectedAttempt, getPersistedAttempt(expectedAttempt));
    }

    protected void assertPersistedAttemptToMatchUpdatableProperties(Attempt expectedAttempt) {
        assertAttemptAllUpdatablePropertiesEquals(expectedAttempt, getPersistedAttempt(expectedAttempt));
    }
}
