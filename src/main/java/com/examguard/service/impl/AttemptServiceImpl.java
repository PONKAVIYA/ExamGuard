package com.examguard.service.impl;

import com.examguard.domain.Attempt;
import com.examguard.repository.AttemptRepository;
import com.examguard.service.AttemptService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.examguard.domain.Attempt}.
 */
@Service
@Transactional
public class AttemptServiceImpl implements AttemptService {

    private static final Logger LOG = LoggerFactory.getLogger(AttemptServiceImpl.class);

    private final AttemptRepository attemptRepository;

    public AttemptServiceImpl(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @Override
    public Attempt save(Attempt attempt) {
        LOG.debug("Request to save Attempt : {}", attempt);
        return attemptRepository.save(attempt);
    }

    @Override
    public Attempt update(Attempt attempt) {
        LOG.debug("Request to update Attempt : {}", attempt);
        return attemptRepository.save(attempt);
    }

    @Override
    public Optional<Attempt> partialUpdate(Attempt attempt) {
        LOG.debug("Request to partially update Attempt : {}", attempt);

        return attemptRepository
            .findById(attempt.getId())
            .map(existingAttempt -> {
                updateIfPresent(existingAttempt::setStartedAt, attempt.getStartedAt());
                updateIfPresent(existingAttempt::setEndedAt, attempt.getEndedAt());
                updateIfPresent(existingAttempt::setSubmitted, attempt.getSubmitted());
                updateIfPresent(existingAttempt::setScore, attempt.getScore());

                return existingAttempt;
            })
            .map(attemptRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Attempt> findAll(Pageable pageable) {
        LOG.debug("Request to get all Attempts");
        return attemptRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attempt> findOne(Long id) {
        LOG.debug("Request to get Attempt : {}", id);
        return attemptRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Attempt : {}", id);
        attemptRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
