package com.college.quizplatform.repository;

import com.college.quizplatform.model.Attempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends MongoRepository<Attempt, String> {

    Optional<Attempt> findBySessionIdAndStudentId(String sessionId, String studentId);

    // Paginated leaderboard query
    Page<Attempt> findBySessionIdAndSubmittedTrue(
            String sessionId,
            Pageable pageable
    );

    List<Attempt> findBySessionIdAndSubmittedTrueOrderByScoreDescSubmittedAtAsc(String sessionId);

    // FIX: Added method to fetch only active attempts for the auto-submit job
    List<Attempt> findBySubmittedFalse();
}