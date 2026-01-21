package com.microservices.user.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 🗄️ USER ENTITY - Entidad JPA
 * 
 * Esta es la representación de la tabla en la base de datos.
 * Pertenece a la capa de INFRAESTRUCTURA, no al dominio.
 * 
 * ¿Por qué separar la entidad JPA de la entidad de dominio?
 * 1. El dominio no depende de JPA
 * 2. Puedes cambiar de base de datos sin tocar el dominio
 * 3. Puedes testear el dominio sin base de datos
 * 4. El dominio tiene lógica de negocio, la entidad JPA no
 * 
 * Anotaciones JPA:
 * - @Entity: Marca como entidad de base de datos
 * - @Table: Define el nombre de la tabla
 * - @Id: Llave primaria
 * - @Column: Configura la columna
 * - @EntityListeners: Auditoría automática
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "active", nullable = false)
    private boolean active = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
