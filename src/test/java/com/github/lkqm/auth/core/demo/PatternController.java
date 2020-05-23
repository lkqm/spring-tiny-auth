package com.github.lkqm.auth.core.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatternController {

    @PostMapping("/auth/login")
    public void login() {
    }

    @PostMapping("/admin/menu")
    public void menu() {
    }

    @PostMapping("/admin/info")
    public void info() {
    }

    @PostMapping("/project/add")
    public void addProject() {
    }

    @PostMapping("/project/update")
    public void updateProject() {
    }

    @PostMapping("/project/delete/{id}")
    public void deleteProject() {
    }
}
