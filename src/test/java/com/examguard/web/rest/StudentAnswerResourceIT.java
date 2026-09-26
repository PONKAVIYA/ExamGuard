package com.examguard.web.rest;

import static com.examguard.domain.StudentAnswerAsserts.*;
import static com.examguard.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.examguard.IntegrationTest;
import com.examguard.domain.StudentAnswer;
import com.examguard.repository.StudentAnswerRepository;
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
 * Integration tests for the {@link StudentAnswerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StudentAnswerResourceIT {

    private static final Integer DEFAULT_SELECTED_OPTION_INDEX = 1;
    private static final Integer UPDATED_SELECTED_OPTION_INDEX = 2;

    private static final String ENTITY_API_URL = "/api/student-answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StudentAnswerRepository studentAnswerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudentAnswerMockMvc;

    private StudentAnswer studentAnswer;

    private StudentAnswer insertedStudentAnswer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentAnswer createEntity() {
        return new StudentAnswer().selectedOptionIndex(DEFAULT_SELECTED_OPTION_INDEX);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StudentAnswer createUpdatedEntity() {
        return new StudentAnswer().selectedOptionIndex(UPDATED_SELECTED_OPTION_INDEX);
    }

    @BeforeEach
    void initTest() {
        studentAnswer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStudentAnswer != null) {
            studentAnswerRepository.delete(insertedStudentAnswer);
            insertedStudentAnswer = null;
        }
    }

    @Test
    @Transactional
    void createStudentAnswer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StudentAnswer
        var returnedStudentAnswer = om.readValue(
            restStudentAnswerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentAnswer)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StudentAnswer.class
        );

        // Validate the StudentAnswer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStudentAnswerUpdatableFieldsEquals(returnedStudentAnswer, getPersistedStudentAnswer(returnedStudentAnswer));

        insertedStudentAnswer = returnedStudentAnswer;
    }

    @Test
    @Transactional
    void createStudentAnswerWithExistingId() throws Exception {
        // Create the StudentAnswer with an existing ID
        studentAnswer.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudentAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentAnswer)))
            .andExpect(status().isBadRequest());

        // Validate the StudentAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSelectedOptionIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        studentAnswer.setSelectedOptionIndex(null);

        // Create the StudentAnswer, which fails.

        restStudentAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentAnswer)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStudentAnswers() throws Exception {
        // Initialize the database
        insertedStudentAnswer = studentAnswerRepository.saveAndFlush(studentAnswer);

        // Get all the studentAnswerList
        restStudentAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studentAnswer.getId().intValue())))
            .andExpect(jsonPath("$.[*].selectedOptionIndex").value(hasItem(DEFAULT_SELECTED_OPTION_INDEX)));
    }

    @Test
    @Transactional
    void getStudentAnswer() throws Exception {
        // Initialize the database
        insertedStudentAnswer = studentAnswerRepository.saveAndFlush(studentAnswer);

        // Get the studentAnswer
        restStudentAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, studentAnswer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(studentAnswer.getId().intValue()))
            .andExpect(jsonPath("$.selectedOptionIndex").value(DEFAULT_SELECTED_OPTION_INDEX));
    }

    @Test
    @Transactional
    void getNonExistingStudentAnswer() throws Exception {
        // Get the studentAnswer
        restStudentAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudentAnswer() throws Exception {
        // Initialize the database
        insertedStudentAnswer = studentAnswerRepository.saveAndFlush(studentAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentAnswer
        StudentAnswer updatedStudentAnswer = studentAnswerRepository.findById(studentAnswer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStudentAnswer are not directly saved in db
        em.detach(updatedStudentAnswer);
        updatedStudentAnswer.selectedOptionIndex(UPDATED_SELECTED_OPTION_INDEX);

        restStudentAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStudentAnswer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStudentAnswer))
            )
            .andExpect(status().isOk());

        // Validate the StudentAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStudentAnswerToMatchAllProperties(updatedStudentAnswer);
    }

    @Test
    @Transactional
    void putNonExistingStudentAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentAnswer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentAnswer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentAnswer))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudentAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentAnswer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(studentAnswer))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudentAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentAnswer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentAnswerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(studentAnswer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudentAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedStudentAnswer = studentAnswerRepository.saveAndFlush(studentAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentAnswer using partial update
        StudentAnswer partialUpdatedStudentAnswer = new StudentAnswer();
        partialUpdatedStudentAnswer.setId(studentAnswer.getId());

        restStudentAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentAnswer))
            )
            .andExpect(status().isOk());

        // Validate the StudentAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentAnswerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStudentAnswer, studentAnswer),
            getPersistedStudentAnswer(studentAnswer)
        );
    }

    @Test
    @Transactional
    void fullUpdateStudentAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedStudentAnswer = studentAnswerRepository.saveAndFlush(studentAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the studentAnswer using partial update
        StudentAnswer partialUpdatedStudentAnswer = new StudentAnswer();
        partialUpdatedStudentAnswer.setId(studentAnswer.getId());

        partialUpdatedStudentAnswer.selectedOptionIndex(UPDATED_SELECTED_OPTION_INDEX);

        restStudentAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudentAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStudentAnswer))
            )
            .andExpect(status().isOk());

        // Validate the StudentAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStudentAnswerUpdatableFieldsEquals(partialUpdatedStudentAnswer, getPersistedStudentAnswer(partialUpdatedStudentAnswer));
    }

    @Test
    @Transactional
    void patchNonExistingStudentAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentAnswer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studentAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentAnswer))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudentAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentAnswer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(studentAnswer))
            )
            .andExpect(status().isBadRequest());

        // Validate the StudentAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudentAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        studentAnswer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentAnswerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(studentAnswer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StudentAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudentAnswer() throws Exception {
        // Initialize the database
        insertedStudentAnswer = studentAnswerRepository.saveAndFlush(studentAnswer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the studentAnswer
        restStudentAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, studentAnswer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return studentAnswerRepository.count();
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

    protected StudentAnswer getPersistedStudentAnswer(StudentAnswer studentAnswer) {
        return studentAnswerRepository.findById(studentAnswer.getId()).orElseThrow();
    }

    protected void assertPersistedStudentAnswerToMatchAllProperties(StudentAnswer expectedStudentAnswer) {
        assertStudentAnswerAllPropertiesEquals(expectedStudentAnswer, getPersistedStudentAnswer(expectedStudentAnswer));
    }

    protected void assertPersistedStudentAnswerToMatchUpdatableProperties(StudentAnswer expectedStudentAnswer) {
        assertStudentAnswerAllUpdatablePropertiesEquals(expectedStudentAnswer, getPersistedStudentAnswer(expectedStudentAnswer));
    }
}
