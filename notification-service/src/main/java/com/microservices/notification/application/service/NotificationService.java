package com.microservices.notification.application.service;

import com.microservices.notification.application.port.in.ProcessOrderEventUseCase;
import com.microservices.notification.application.port.out.SendNotificationPort;
import com.microservices.notification.domain.event.OrderCreatedEvent;
import com.microservices.notification.domain.model.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * NotificationService - Lógica de Negocio (Application Service)
 * 
 * Este servicio:
 * 1. Recibe un evento de orden creada (a través de un puerto de entrada)
 * 2. Crea una notificación (lógica de negocio)
 * 3. La envía (usando un puerto de salida)
 * 
 * ✨ Lo genial: No sabe ni le importa de dónde vienen los eventos
 *    ni cómo se envían. Solo implementa la lógica de negocio.
 * 
 * 🔄 Flujo:
 *    Evento Kafka → KafkaConsumerAdapter → ProcessOrderEventUseCase (este servicio)
 *                                       → SendNotificationPort → EmailAdapter
 */
@Service
@AllArgsConstructor
@Slf4j
public class NotificationService implements ProcessOrderEventUseCase {
    
    /**
     * Puerto de salida inyectado
     * La implementación concreta viene del adaptador
     */
    private final SendNotificationPort sendNotificationPort;
    
    /**
     * Procesa un evento de orden creada y envía una notificación
     */
    @Override
    public void processOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("📩 Procesando evento de orden creada: {}", event.getOrderId());
        
        try {
            // Lógica de negocio: crear la notificación
            Notification notification = buildNotification(event);
            
            // Usar el puerto de salida para enviar
            boolean sent = sendNotificationPort.sendNotification(notification);
            
            if (sent) {
                log.info("✅ Notificación enviada exitosamente para orden: {}", event.getOrderId());
            } else {
                log.warn("⚠️ Falló el envío de notificación para orden: {}", event.getOrderId());
            }
        } catch (Exception e) {
            log.error("❌ Error procesando evento de orden: {}", event.getOrderId(), e);
        }
    }
    
    /**
     * Construye una notificación a partir del evento de orden
     */
    private Notification buildNotification(OrderCreatedEvent event) {
        Notification notification = new Notification();
        notification.setId(java.util.UUID.randomUUID().toString());
        notification.setOrderId(event.getOrderId());
        notification.setRecipientEmail(event.getCustomerEmail());
        notification.setSubject("📦 Tu orden ha sido creada!");
        
        String message = String.format(
            "Hola,\n\nTu orden #%s ha sido procesada exitosamente.\n" +
            "Monto: $%.2f\n" +
            "Items: %s\n\n" +
            "Gracias por tu compra!",
            event.getOrderId(),
            event.getTotalAmount(),
            event.getDescription()
        );
        
        notification.setMessage(message);
        notification.setStatus(Notification.NotificationStatus.PENDING);
        
        return notification;
    }
}
