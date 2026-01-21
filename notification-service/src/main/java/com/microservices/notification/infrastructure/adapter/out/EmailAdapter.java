package com.microservices.notification.infrastructure.adapter.out;

import com.microservices.notification.application.port.out.SendNotificationPort;
import com.microservices.notification.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * EmailAdapter - Adaptador de Salida (Driver Adapter)
 * 
 * Este adaptador:
 * 1. Implementa el puerto SendNotificationPort
 * 2. Simula el envío de emails (en un caso real usaría SendGrid, Gmail, etc.)
 * 3. Permite que el core no dependa de ningún proveedor específico
 * 
 * 🏛️ Es un "Driver Adapter" porque implementa un puerto de salida
 *    e interactúa con sistemas externos.
 * 
 * En un caso real, aquí conectarías con:
 * - SendGrid API: https://sendgrid.com
 * - AWS SES: Amazon Simple Email Service
 * - Gmail SMTP
 * - Cualquier servicio de email
 * 
 * Para este ejemplo, simulamos el envío imprimiendo en logs.
 */
@Component
@Slf4j
public class EmailAdapter implements SendNotificationPort {
    
    @Override
    public boolean sendNotification(Notification notification) {
        log.info("📧 Enviando notificación por email a: {}", notification.getRecipientEmail());
        
        try {
            // En un caso real, aquí conectarías con un servicio de email
            boolean result = sendEmail(
                notification.getRecipientEmail(),
                notification.getSubject(),
                notification.getMessage()
            );
            
            if (result) {
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("✅ Notificación enviada exitosamente para orden: {}", notification.getOrderId());
            }
            
            return result;
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
            log.error("❌ Error enviando notificación: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendEmail(String email, String subject, String message) {
        try {
            // Simular envío de email
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("📬 EMAIL ENVIADO (SIMULADO)");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("Para: {}", email);
            log.info("Asunto: {}", subject);
            log.info("Mensaje:\n{}", message);
            log.info("Timestamp: {}", LocalDateTime.now());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // En un caso real, esto podría fallar
            return true;
        } catch (Exception e) {
            log.error("Error al enviar email: {}", e.getMessage());
            return false;
        }
    }
}
