package com.gad.msvc_oauth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${custom.path}")
public class AppController {
    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "Logout successful");
    }
}
