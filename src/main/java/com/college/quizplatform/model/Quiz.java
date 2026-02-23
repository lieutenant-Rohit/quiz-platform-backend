package com.college.quizplatform.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "quizzes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    private String id;

    private String title;

    // NEW: Added topic to match CreateQuizRequest
    private String topic;

    private String description;

    // NEW: Added duration for the quiz timer (in minutes)
    private int duration;

    // Store question IDs (not full objects)
    private List<String> questionIds;

    private boolean active;

    private int marksPerQuestion;
    private int negativeMarks;

    // NEW: Added totalMarks field used in your Service
    private int totalMarks;

    private Instant createdAt;
}