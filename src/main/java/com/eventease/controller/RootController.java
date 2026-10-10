package com.eventease.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping({"/", "/api"})
    public ResponseEntity<Map<String, Object>> rootEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "EventEase Backend API (MySQL)");
        response.put("message", "The backend API is running. Access the web interface at http://localhost:5173");
        response.put("frontendUrl", "http://localhost:5173");
        response.put("healthUrl", "http://localhost:8080/api/health");
        return ResponseEntity.ok(response);
    }
}
