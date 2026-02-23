package com.college.quizplatform; // Make sure this matches your package

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // FIX: Required to wake up the AutoSubmitService
public class QuizPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizPlatformApplication.class, args);
	}

}