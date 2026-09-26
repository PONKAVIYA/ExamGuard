package com.examguard.web.rest;

import static com.examguard.domain.ProctorEventAsserts.*;
import static com.examguard.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.examguard.IntegrationTest;
import com.examguard.domain.ProctorEvent;
import com.examguard.repository.ProctorEventRepository;
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
 * Integration tests for the {@link ProctorEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProctorEventResourceIT {

    private static final String DEFAULT_EVENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.ofEpochMilli(1703161763127L);

    private static final String ENTITY_API_URL = "/api/proctor-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProctorEventRepository proctorEventRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProctorEventMockMvc;

    private ProctorEvent proctorEvent;

    private ProctorEvent insertedProctorEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProctorEvent createEntity() {
        return new ProctorEvent().eventType(DEFAULT_EVENT_TYPE).timestamp(DEFAULT_TIMESTAMP);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProctorEvent createUpdatedEntity() {
        return new ProctorEvent().eventType(UPDATED_EVENT_TYPE).timestamp(UPDATED_TIMESTAMP);
    }

    @BeforeEach
    void initTest() {
        proctorEvent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProctorEvent != null) {
            proctorEventRepository.delete(insertedProctorEvent);
            insertedProctorEvent = null;
        }
    }

    @Test
    @Transactional
    void createProctorEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProctorEvent
        var returnedProctorEvent = om.readValue(
            restProctorEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEvent)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProctorEvent.class
        );

        // Validate the ProctorEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProctorEventUpdatableFieldsEquals(returnedProctorEvent, getPersistedProctorEvent(returnedProctorEvent));

        insertedProctorEvent = returnedProctorEvent;
    }

    @Test
    @Transactional
    void createProctorEventWithExistingId() throws Exception {
        // Create the ProctorEvent with an existing ID
        proctorEvent.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProctorEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEvent)))
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEventTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        proctorEvent.setEventType(null);

        // Create the ProctorEvent, which fails.

        restProctorEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEvent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        proctorEvent.setTimestamp(null);

        // Create the ProctorEvent, which fails.

        restProctorEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEvent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProctorEvents() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get all the proctorEventList
        restProctorEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proctorEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }

    @Test
    @Transactional
    void getProctorEvent() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        // Get the proctorEvent
        restProctorEventMockMvc
            .perform(get(ENTITY_API_URL_ID, proctorEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(proctorEvent.getId().intValue()))
            .andExpect(jsonPath("$.eventType").value(DEFAULT_EVENT_TYPE))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProctorEvent() throws Exception {
        // Get the proctorEvent
        restProctorEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProctorEvent() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorEvent
        ProctorEvent updatedProctorEvent = proctorEventRepository.findById(proctorEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProctorEvent are not directly saved in db
        em.detach(updatedProctorEvent);
        updatedProctorEvent.eventType(UPDATED_EVENT_TYPE).timestamp(UPDATED_TIMESTAMP);

        restProctorEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProctorEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProctorEvent))
            )
            .andExpect(status().isOk());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProctorEventToMatchAllProperties(updatedProctorEvent);
    }

    @Test
    @Transactional
    void putNonExistingProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, proctorEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proctorEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proctorEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proctorEvent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProctorEventWithPatch() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorEvent using partial update
        ProctorEvent partialUpdatedProctorEvent = new ProctorEvent();
        partialUpdatedProctorEvent.setId(proctorEvent.getId());

        partialUpdatedProctorEvent.timestamp(UPDATED_TIMESTAMP);

        restProctorEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProctorEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProctorEvent))
            )
            .andExpect(status().isOk());

        // Validate the ProctorEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProctorEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProctorEvent, proctorEvent),
            getPersistedProctorEvent(proctorEvent)
        );
    }

    @Test
    @Transactional
    void fullUpdateProctorEventWithPatch() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proctorEvent using partial update
        ProctorEvent partialUpdatedProctorEvent = new ProctorEvent();
        partialUpdatedProctorEvent.setId(proctorEvent.getId());

        partialUpdatedProctorEvent.eventType(UPDATED_EVENT_TYPE).timestamp(UPDATED_TIMESTAMP);

        restProctorEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProctorEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProctorEvent))
            )
            .andExpect(status().isOk());

        // Validate the ProctorEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProctorEventUpdatableFieldsEquals(partialUpdatedProctorEvent, getPersistedProctorEvent(partialUpdatedProctorEvent));
    }

    @Test
    @Transactional
    void patchNonExistingProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, proctorEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proctorEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proctorEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProctorEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proctorEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProctorEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(proctorEvent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProctorEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProctorEvent() throws Exception {
        // Initialize the database
        insertedProctorEvent = proctorEventRepository.saveAndFlush(proctorEvent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the proctorEvent
        restProctorEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, proctorEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return proctorEventRepository.count();
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

    protected ProctorEvent getPersistedProctorEvent(ProctorEvent proctorEvent) {
        return proctorEventRepository.findById(proctorEvent.getId()).orElseThrow();
    }

    protected void assertPersistedProctorEventToMatchAllProperties(ProctorEvent expectedProctorEvent) {
        assertProctorEventAllPropertiesEquals(expectedProctorEvent, getPersistedProctorEvent(expectedProctorEvent));
    }

    protected void assertPersistedProctorEventToMatchUpdatableProperties(ProctorEvent expectedProctorEvent) {
        assertProctorEventAllUpdatablePropertiesEquals(expectedProctorEvent, getPersistedProctorEvent(expectedProctorEvent));
    }
}
