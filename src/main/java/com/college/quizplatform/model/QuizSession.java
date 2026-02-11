package com.college.quizplatform.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document(collection = "quiz_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSession {

    @Id
    private String id;

    private String quizId;

    private String sessionName;

    private Instant scheduledStartTime;

    private Instant scheduledEndTime;

    // MANUAL CONTROL
    private boolean started;

    private boolean ended;

    // Students allowed to join (optional restriction)
    private Set<String> allowedStudentIds;

    private Instant createdAt;
}