package com.college.quizplatform.controller.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student/test")
public class StudentTestController {

    @GetMapping
    public String studentAccess() {
        return "STUDENT access granted ✅";
    }
}