package com.gaming.match_service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gaming.match_service.infrastructure.adapter.output.persistence")
@EnableMongoAuditing
public class MongoConfig {
}