package com.gaming.player_service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gaming.player_service.infrastructure.adapter.output.persistence")
@EnableMongoAuditing
public class MongoConfig {
    // Spring Boot auto-configura MongoDB con application.yml
}