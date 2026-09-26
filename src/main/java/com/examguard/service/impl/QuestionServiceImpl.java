package com.examguard.service.impl;

import com.examguard.domain.Question;
import com.examguard.repository.QuestionRepository;
import com.examguard.service.QuestionService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.examguard.domain.Question}.
 */
@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionServiceImpl.class);

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question save(Question question) {
        LOG.debug("Request to save Question : {}", question);
        return questionRepository.save(question);
    }

    @Override
    public Question update(Question question) {
        LOG.debug("Request to update Question : {}", question);
        return questionRepository.save(question);
    }

    @Override
    public Optional<Question> partialUpdate(Question question) {
        LOG.debug("Request to partially update Question : {}", question);

        return questionRepository
            .findById(question.getId())
            .map(existingQuestion -> {
                updateIfPresent(existingQuestion::setText, question.getText());
                updateIfPresent(existingQuestion::setCorrectOptionIndex, question.getCorrectOptionIndex());

                return existingQuestion;
            })
            .map(questionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Question> findAll(Pageable pageable) {
        LOG.debug("Request to get all Questions");
        return questionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findOne(Long id) {
        LOG.debug("Request to get Question : {}", id);
        return questionRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Question : {}", id);
        questionRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
