package com.college.quizplatform.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/test")
public class AdminTestController {

    @GetMapping
    public String adminAccess() {
        return "ADMIN access granted ✅";
    }
}