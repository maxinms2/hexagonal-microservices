package com.microservices.notification.infrastructure.adapter.in.kafka;

import com.microservices.notification.application.port.in.ProcessOrderEventUseCase;
import com.microservices.notification.domain.event.OrderCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * KafkaConsumerAdapter - Adaptador de Entrada (Driven Adapter)
 * 
 * Este adaptador:
 * 1. Escucha mensajes de Kafka (del topic "order-events")
 * 2. Los deserializa a OrderCreatedEvent
 * 3. Los pasa al caso de uso (ProcessOrderEventUseCase)
 * 
 * 🏛️ Es un "Driven Adapter" porque implementa un puerto de entrada
 *    y recibe mensajes del mundo exterior (Kafka).
 * 
 * Kafka Topics:
 * - "order-events": Topic donde order-service produce eventos de órdenes creadas
 * - group-id: "notification-service-group": Permite que múltiples instancias
 *   trabajen juntas sin procesar el mismo mensaje dos veces
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaConsumerAdapter {
    
    /**
     * Puerto de entrada: el caso de uso que queremos ejecutar
     */
    private final ProcessOrderEventUseCase processOrderEventUseCase;
    
    /**
     * Escucha el topic "order-events" de Kafka
     * 
     * @KafkaListener: Anotación de Spring que:
     *   - Escucha automáticamente el topic especificado
     *   - Deserializa el mensaje JSON a OrderCreatedEvent
     *   - Maneja errores automáticamente
     * 
     * @param event: El evento deserializado
     * @param partition: La partición de Kafka de donde vino el mensaje
     * @param offset: El offset del mensaje en la partición
     */
    @KafkaListener(
        topics = "order-events",
        groupId = "${spring.kafka.consumer.group-id:notification-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCreatedEvent(
        OrderCreatedEvent event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("🎧 Mensaje recibido de Kafka - Partición: {}, Offset: {}", partition, offset);
        log.info("📨 Evento de orden recibido: {}", event.getOrderId());
        
        // Pasar el evento al caso de uso
        processOrderEventUseCase.processOrderCreatedEvent(event);
    }
}
