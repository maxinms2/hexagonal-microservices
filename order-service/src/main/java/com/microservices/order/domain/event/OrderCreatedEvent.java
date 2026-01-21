package com.microservices.order.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * OrderCreatedEvent - Evento de Dominio
 * 
 * Representa el evento que se produce cuando se crea una orden.
 * Este evento se publica a Kafka para que otros microservicios
 * (como notification-service) puedan reaccionar.
 * 
 * 🍎 Analogía: Es como cuando alguien en el restaurante grita
 *    "ORDEN #123 LISTA" - todos se enteran y pueden reaccionar
 *    (el mesero la sirve, el contador la registra, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    
    @JsonProperty("orderId")
    private String orderId;
    
    @JsonProperty("customerId")
    private String customerId;
    
    @JsonProperty("customerEmail")
    private String customerEmail;
    
    @JsonProperty("totalAmount")
    private Double totalAmount;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("eventType")
    private String eventType = "OrderCreated";
}
