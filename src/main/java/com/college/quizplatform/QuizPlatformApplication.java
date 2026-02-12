package com.college.quizplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuizPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizPlatformApplication.class, args);
	}

}
