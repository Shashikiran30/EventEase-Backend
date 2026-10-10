package com.eventease.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;
    private final Environment environment;

    public HealthController(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "EventEase Backend is running smoothly.");

        String[] activeProfiles = environment.getActiveProfiles();
        response.put("activeProfiles", activeProfiles.length > 0 ? activeProfiles : new String[]{"default (MySQL)"});

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            response.put("databaseProduct", metaData.getDatabaseProductName());
            response.put("databaseVersion", metaData.getDatabaseProductVersion());
            response.put("databaseConnected", true);
        } catch (Exception e) {
            response.put("databaseConnected", false);
            response.put("databaseError", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
