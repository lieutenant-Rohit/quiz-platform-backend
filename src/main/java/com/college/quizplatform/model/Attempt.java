package com.college.quizplatform.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "attempts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {

    @Id
    private String id;

    private String sessionId;

    private String quizId;

    private String studentId;

    // questionId -> selectedOptionIndex
    private Map<String, Integer> answers;

    private Instant startedAt;

    private Instant submittedAt;

    private boolean submitted;

    private int score;
}