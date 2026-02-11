package com.college.quizplatform.repository;

import com.college.quizplatform.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {

    List<Question> findByQuizId(String quizId);
}