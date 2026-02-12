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

    private String description;

    // Store question IDs (not full objects)
    private List<String> questionIds;

    private boolean active;

    private int marksPerQuestion;
    private int negativeMarks;

    private Instant createdAt;
}