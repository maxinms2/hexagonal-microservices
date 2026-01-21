package com.microservices.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Notification (Entidad de Dominio)
 * 
 * Representa una notificación que debe ser enviada.
 * 
 * Aunque en este ejemplo no la persistimos en BD,
 * en un caso real podríamos guardar un registro de qué
 * notificaciones se han enviado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    private String id;
    private String orderId;
    private String recipientEmail;
    private String subject;
    private String message;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private String failureReason;
    
    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED
    }
    
    /**
     * Factory method para crear una notificación a partir de un evento
     */
    public static Notification fromOrderCreatedEvent(String orderId, String email, String subject) {
        Notification notification = new Notification();
        notification.setId(java.util.UUID.randomUUID().toString());
        notification.setOrderId(orderId);
        notification.setRecipientEmail(email);
        notification.setSubject(subject);
        notification.setStatus(NotificationStatus.PENDING);
        return notification;
    }
}
