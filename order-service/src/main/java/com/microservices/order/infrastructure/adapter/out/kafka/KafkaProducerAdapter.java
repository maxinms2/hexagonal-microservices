package com.microservices.order.infrastructure.adapter.out.kafka;

import com.microservices.order.application.port.output.PublishOrderEventPort;
import com.microservices.order.domain.event.OrderCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * KafkaProducerAdapter - Adaptador de Salida para Kafka
 * 
 * Implementa PublishOrderEventPort usando Kafka como broker.
 * 
 * 🏛️ Arquitectura Hexagonal:
 *    - El OrderService llama a PublishOrderEventPort
 *    - NO sabe que está usando Kafka
 *    - Este adaptador es la "glue" que conecta ambos mundos
 * 
 * 🔄 Flujo:
 *    OrderService → PublishOrderEventPort → KafkaProducerAdapter → Kafka Broker
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaProducerAdapter implements PublishOrderEventPort {
    
    /**
     * KafkaTemplate: Cliente de Spring para enviar mensajes a Kafka
     * 
     * Spring inyecta automáticamente una instancia configurada
     * según application.yml
     */
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    
    /**
     * Topic de Kafka donde se publican los eventos
     */
    private static final String TOPIC = "order-events";
    
    @Override
    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            log.info("📤 Publicando evento OrderCreated a Kafka - Orden: {}", event.getOrderId());
            
            // Enviar a Kafka usando el orderId como clave (para particionamiento)
            // KafkaTemplate maneja automáticamente la serialización JSON
            kafkaTemplate.send(TOPIC, event.getOrderId(), event);
            
            log.info("✅ Evento publicado exitosamente - Orden: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("❌ Error publicando evento a Kafka: {}", e.getMessage(), e);
            // En producción, podrías reintentar, alertar, etc.
        }
    }
}
