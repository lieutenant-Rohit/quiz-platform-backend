package com.college.quizplatform.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "attempts")

// Unique index → prevents duplicate attempts per student per session
@CompoundIndex(
        name = "session_student_unique_idx",
        def = "{'sessionId': 1, 'studentId': 1}",
        unique = true
)

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {

    @Id
    private String id;

    // Indexed for fast filtering
    @Indexed
    private String sessionId;

    @Indexed
    private String quizId;

    @Indexed
    private String studentId;

    // questionId -> selectedOptionIndex
    private Map<String, Integer> answers;

    // questionId -> questionStartTime
    private Map<String, Instant> questionStartTimes;

    private Instant startedAt;

    private Instant submittedAt;

    private boolean submitted;

    private int score;
}