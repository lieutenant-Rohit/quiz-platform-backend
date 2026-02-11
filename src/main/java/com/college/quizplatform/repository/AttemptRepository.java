package com.college.quizplatform.repository;

import com.college.quizplatform.model.Attempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends MongoRepository<Attempt, String> {

    Optional<Attempt> findBySessionIdAndStudentId(String sessionId, String studentId);
    List<Attempt> findBySessionIdAndSubmittedTrueOrderByScoreDescSubmittedAtAsc(String sessionId);
}