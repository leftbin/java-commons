package com.lbn.commons.db.flyway;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.flywaydb.core.internal.exception.FlywaySqlException;
import org.flywaydb.core.internal.info.MigrationInfoDumper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "database")
public class MigrationConfig {
    @Data
    public static class FlywayConfigDetails {
        private String name;
        private String url;
        private String user;
        private String password;
        private List<String> schemas;
        private List<String> locations;
        private FlywayOtherConfigDetails configuration;
        private boolean baselineMigration;
        private Flyway flyway;
    }

    @Data
    public static class Schema {
        private String name;
    }

    @Data
    public static class FlywayOtherConfigDetails {
        private String driver;
    }

    private List<FlywayConfigDetails> migrations = new ArrayList<>();


    @Value("${spring.flyway.table}")
    private String flywayTable;

    Predicate<Flyway> checkIfMigrationEnabled = flyway -> System.getenv("FLYWAY_RUN_TYPE").equals("migrate");
    Predicate<Flyway> checkIfThereAreAnyPendingMigrations = flyway -> flyway.info().pending().length > 0;

    @Bean
    public List<MigrateResult> init() {
        log.info("initiating flyway to run db scripts");
        return migrations.stream()
                .map(configDetails -> {
                    configDetails.setFlyway(getFlyway(configDetails));
                    return getFlyway(configDetails);
                })
                .map(flyway -> {
                    dryRunScripts(flyway);
                    return flyway;
                })
                .filter(checkIfMigrationEnabled)
                .filter(checkIfThereAreAnyPendingMigrations)
                .map(Flyway::migrate)
                .toList();
    }

    private Flyway getFlyway(FlywayConfigDetails configDetails) {
        return Flyway
                .configure()
                .table(flywayTable)
                .configuration(Map.of("driver", configDetails.getConfiguration().getDriver()))
                .dataSource(configDetails.getUrl(), configDetails.getUser(), configDetails.getPassword())
                .schemas(configDetails.getSchemas().toArray(String[]::new))
                .locations(configDetails.getLocations().toArray(String[]::new))
                .baselineOnMigrate(configDetails.baselineMigration)
                .load();
    }

    private void dryRunScripts(Flyway flyway) {
        try {
            log.info("\n {} - {} migration plan:\n {}", flyway.getConfiguration().getUrl(), flyway.getConfiguration().getSchemas(), MigrationInfoDumper.dumpToAsciiTable(flyway.info().pending()));
        } catch (FlywaySqlException e) {
            log.error("found exception to get plan: {}", e.getMessage());
        }
    }
}

