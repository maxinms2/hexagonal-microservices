package com.microservices.order.application.port.output;

import com.microservices.order.domain.event.OrderCreatedEvent;

/**
 * PublishOrderEventPort - Puerto de Salida
 * 
 * Define la capacidad del servicio de PUBLICAR eventos.
 * 
 * Esta es una abstracción: el service NO sabe si se publica
 * en Kafka, RabbitMQ, SNS, o cualquier otro broker de eventos.
 * 
 * Solo sabe que necesita publicar y alguien lo hará.
 */
public interface PublishOrderEventPort {
    
    /**
     * Publica un evento de orden creada
     * 
     * @param event El evento a publicar
     */
    void publishOrderCreatedEvent(OrderCreatedEvent event);
}
