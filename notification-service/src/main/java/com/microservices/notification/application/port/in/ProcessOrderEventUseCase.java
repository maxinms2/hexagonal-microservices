package com.microservices.notification.application.port.in;

import com.microservices.notification.domain.event.OrderCreatedEvent;

/**
 * Puerto de Entrada: ProcessOrderEventUseCase
 * 
 * Este es un puerto de ENTRADA (Input Port).
 * Define QUÉ operaciones puede realizar el servicio de notificaciones.
 * 
 * En este caso: procesar eventos de órdenes creadas.
 * 
 * 🏛️ En términos de arquitectura hexagonal:
 *    - Este puerto es el "contrato" que un adaptador debe implementar
 *    - El adaptador de Kafka lo implementará
 *    - El core del negocio lo usa (sin conocer que viene de Kafka)
 */
public interface ProcessOrderEventUseCase {
    
    /**
     * Procesa un evento de orden creada
     * 
     * @param event El evento que viene de Kafka
     */
    void processOrderCreatedEvent(OrderCreatedEvent event);
}
