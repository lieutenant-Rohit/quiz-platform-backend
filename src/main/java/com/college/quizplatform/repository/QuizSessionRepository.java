package com.college.quizplatform.repository;

import com.college.quizplatform.model.QuizSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizSessionRepository extends MongoRepository<QuizSession, String> {

    List<QuizSession> findByQuizId(String quizId);
}