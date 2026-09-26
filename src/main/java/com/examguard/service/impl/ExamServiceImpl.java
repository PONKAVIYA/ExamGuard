package com.examguard.service.impl;

import com.examguard.domain.Exam;
import com.examguard.repository.ExamRepository;
import com.examguard.service.ExamService;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.examguard.domain.Exam}.
 */
@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamServiceImpl.class);

    private final ExamRepository examRepository;

    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam save(Exam exam) {
        LOG.debug("Request to save Exam : {}", exam);
        return examRepository.save(exam);
    }

    @Override
    public Exam update(Exam exam) {
        LOG.debug("Request to update Exam : {}", exam);
        return examRepository.save(exam);
    }

    @Override
    public Optional<Exam> partialUpdate(Exam exam) {
        LOG.debug("Request to partially update Exam : {}", exam);

        return examRepository
            .findById(exam.getId())
            .map(existingExam -> {
                updateIfPresent(existingExam::setTitle, exam.getTitle());
                updateIfPresent(existingExam::setDurationSeconds, exam.getDurationSeconds());
                updateIfPresent(existingExam::setActive, exam.getActive());

                return existingExam;
            })
            .map(examRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> findAll() {
        LOG.debug("Request to get all Exams");
        return examRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Exam> findOne(Long id) {
        LOG.debug("Request to get Exam : {}", id);
        return examRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Exam : {}", id);
        examRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
