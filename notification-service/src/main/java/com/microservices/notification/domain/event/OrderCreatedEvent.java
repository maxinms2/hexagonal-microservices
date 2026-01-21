package com.microservices.notification.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento: OrderCreatedEvent
 * 
 * Este es el evento que VIENE DE order-service a través de Kafka.
 * 
 * Cuando se crea una orden en order-service, se produce este evento a Kafka.
 * El notification-service lo consume y envía una notificación.
 * 
 * ⚠️ IMPORTANTE: Usa @JsonProperty para que Jackson pueda deserializar
 *    correctamente desde JSON aunque tenga diferentes nombres.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    
    /**
     * ID único de la orden
     */
    @JsonProperty("orderId")
    private String orderId;
    
    /**
     * ID del cliente que realizó la orden
     */
    @JsonProperty("customerId")
    private String customerId;
    
    /**
     * Email del cliente para enviar la notificación
     */
    @JsonProperty("customerEmail")
    private String customerEmail;
    
    /**
     * Monto total de la orden
     */
    @JsonProperty("totalAmount")
    private Double totalAmount;
    
    /**
     * Descripción de los items
     */
    @JsonProperty("description")
    private String description;
    
    /**
     * Timestamp cuando se creó el evento
     */
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    /**
     * Tipo de evento (para identificarlo en el topic de Kafka)
     */
    @JsonProperty("eventType")
    private String eventType;
}
