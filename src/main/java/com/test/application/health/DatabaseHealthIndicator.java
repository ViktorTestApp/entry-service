package com.test.application.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Настройки индикаторов для мониторинга приложения
 */
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            Long result = jdbcTemplate.queryForObject("SELECT 1", Long.class);
            return Health.up()
                    .withDetail("database", "postgresql")
                    .withDetail("status", "connected")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "postgresql")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}