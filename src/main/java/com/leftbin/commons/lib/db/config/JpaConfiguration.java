package com.leftbin.commons.lib.db.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.leftbin.commons.lib.timeline.repo")
public class JpaConfiguration {
    // Add any additional JPA-related configurations here
}
