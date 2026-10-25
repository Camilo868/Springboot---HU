package com.eventify.config;

import org.springframework.context.annotation.Configuration;

/**
 * El poblado de datos y control de esquema ha sido delegado al ciclo
 * de migraciones versionadas de Flyway (V1, V2 y V3 en db/migration).
 */
@Configuration
public class DataSeederConfig {
    // Migración y Seed gestionados por Flyway
}
