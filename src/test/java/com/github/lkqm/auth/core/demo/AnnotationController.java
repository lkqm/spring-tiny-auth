package com.github.lkqm.auth.core.demo;

import com.github.lkqm.auth.annotation.Auth;
import com.github.lkqm.auth.annotation.AuthConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Auth
public class AnnotationController {

    @Auth(AuthConstants.anno)
    @PostMapping("/auth/login")
    public void login() {
    }

    @Auth(AuthConstants.authen)
    @PostMapping("/admin/menu")
    public void menu() {
    }

    @PostMapping("/admin/info")
    public void info() {
    }

    @Auth("hasRole('admin')")
    @PostMapping("/project/add")
    public void addProject() {
    }

    @Auth("hasPermission('project:update')")
    @PostMapping("/project/update")
    public void updateProject() {
    }
}
