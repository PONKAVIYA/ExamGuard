package com.examguard.web.rest;

import static com.examguard.domain.QuestionOptionAsserts.*;
import static com.examguard.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.examguard.IntegrationTest;
import com.examguard.domain.QuestionOption;
import com.examguard.repository.QuestionOptionRepository;
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
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link QuestionOptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QuestionOptionResourceIT {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER_INDEX = 1;
    private static final Integer UPDATED_ORDER_INDEX = 2;

    private static final String ENTITY_API_URL = "/api/question-options";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestionOptionMockMvc;

    private QuestionOption questionOption;

    private QuestionOption insertedQuestionOption;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionOption createEntity() {
        return new QuestionOption().text(DEFAULT_TEXT).orderIndex(DEFAULT_ORDER_INDEX);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionOption createUpdatedEntity() {
        return new QuestionOption().text(UPDATED_TEXT).orderIndex(UPDATED_ORDER_INDEX);
    }

    @BeforeEach
    void initTest() {
        questionOption = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedQuestionOption != null) {
            questionOptionRepository.delete(insertedQuestionOption);
            insertedQuestionOption = null;
        }
    }

    @Test
    @Transactional
    void createQuestionOption() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuestionOption
        var returnedQuestionOption = om.readValue(
            restQuestionOptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOption)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuestionOption.class
        );

        // Validate the QuestionOption in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertQuestionOptionUpdatableFieldsEquals(returnedQuestionOption, getPersistedQuestionOption(returnedQuestionOption));

        insertedQuestionOption = returnedQuestionOption;
    }

    @Test
    @Transactional
    void createQuestionOptionWithExistingId() throws Exception {
        // Create the QuestionOption with an existing ID
        questionOption.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOption)))
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        questionOption.setText(null);

        // Create the QuestionOption, which fails.

        restQuestionOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOption)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        questionOption.setOrderIndex(null);

        // Create the QuestionOption, which fails.

        restQuestionOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOption)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQuestionOptions() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get all the questionOptionList
        restQuestionOptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionOption.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)));
    }

    @Test
    @Transactional
    void getQuestionOption() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        // Get the questionOption
        restQuestionOptionMockMvc
            .perform(get(ENTITY_API_URL_ID, questionOption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(questionOption.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT))
            .andExpect(jsonPath("$.orderIndex").value(DEFAULT_ORDER_INDEX));
    }

    @Test
    @Transactional
    void getNonExistingQuestionOption() throws Exception {
        // Get the questionOption
        restQuestionOptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuestionOption() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionOption
        QuestionOption updatedQuestionOption = questionOptionRepository.findById(questionOption.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuestionOption are not directly saved in db
        em.detach(updatedQuestionOption);
        updatedQuestionOption.text(UPDATED_TEXT).orderIndex(UPDATED_ORDER_INDEX);

        restQuestionOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuestionOption.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedQuestionOption))
            )
            .andExpect(status().isOk());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuestionOptionToMatchAllProperties(updatedQuestionOption);
    }

    @Test
    @Transactional
    void putNonExistingQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionOption.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionOption))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionOption))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionOption)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuestionOptionWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionOption using partial update
        QuestionOption partialUpdatedQuestionOption = new QuestionOption();
        partialUpdatedQuestionOption.setId(questionOption.getId());

        restQuestionOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionOption))
            )
            .andExpect(status().isOk());

        // Validate the QuestionOption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionOptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuestionOption, questionOption),
            getPersistedQuestionOption(questionOption)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuestionOptionWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionOption using partial update
        QuestionOption partialUpdatedQuestionOption = new QuestionOption();
        partialUpdatedQuestionOption.setId(questionOption.getId());

        partialUpdatedQuestionOption.text(UPDATED_TEXT).orderIndex(UPDATED_ORDER_INDEX);

        restQuestionOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionOption))
            )
            .andExpect(status().isOk());

        // Validate the QuestionOption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionOptionUpdatableFieldsEquals(partialUpdatedQuestionOption, getPersistedQuestionOption(partialUpdatedQuestionOption));
    }

    @Test
    @Transactional
    void patchNonExistingQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionOption))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionOption))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuestionOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionOption.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionOptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(questionOption)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuestionOption() throws Exception {
        // Initialize the database
        insertedQuestionOption = questionOptionRepository.saveAndFlush(questionOption);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the questionOption
        restQuestionOptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, questionOption.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return questionOptionRepository.count();
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

    protected QuestionOption getPersistedQuestionOption(QuestionOption questionOption) {
        return questionOptionRepository.findById(questionOption.getId()).orElseThrow();
    }

    protected void assertPersistedQuestionOptionToMatchAllProperties(QuestionOption expectedQuestionOption) {
        assertQuestionOptionAllPropertiesEquals(expectedQuestionOption, getPersistedQuestionOption(expectedQuestionOption));
    }

    protected void assertPersistedQuestionOptionToMatchUpdatableProperties(QuestionOption expectedQuestionOption) {
        assertQuestionOptionAllUpdatablePropertiesEquals(expectedQuestionOption, getPersistedQuestionOption(expectedQuestionOption));
    }
}
