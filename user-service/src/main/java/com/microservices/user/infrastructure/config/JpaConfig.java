package com.microservices.user.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ⚙️ JPA CONFIGURATION
 * 
 * Configuración de JPA y auditoría automática.
 * 
 * @EnableJpaAuditing: Habilita la auditoría automática
 * - @CreatedDate se llena automáticamente al crear
 * - @LastModifiedDate se actualiza automáticamente
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // La anotación @EnableJpaAuditing es suficiente
    // No necesitamos configuración adicional
}
