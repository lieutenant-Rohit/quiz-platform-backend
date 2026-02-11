package com.college.quizplatform.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    private String id;

    private String quizId;

    private String questionText;

    // MCQ options
    private List<String> options;

    // Index of correct option (0-based)
    private int correctAnswerIndex;

    private String category;

    private Instant createdAt;
}