package com.microservices.notification.application.port.out;

import com.microservices.notification.domain.model.Notification;

/**
 * Puerto de Salida: SendNotificationPort
 * 
 * Este es un puerto de SALIDA (Output Port).
 * Define las operaciones que el servicio NECESITA hacer
 * en sistemas externos (pero no especifica CÓMO hacerlas).
 * 
 * 🏛️ Beneficios:
 *    - El core no depende de un proveedor específico de email
 *    - Podemos cambiar de Gmail a SendGrid sin tocar el core
 *    - Fácil de testear: creamos un mock del puerto
 */
public interface SendNotificationPort {
    
    /**
     * Envía una notificación
     * 
     * @param notification La notificación a enviar
     * @return true si se envió exitosamente, false en caso contrario
     */
    boolean sendNotification(Notification notification);
    
    /**
     * Envía una notificación por email
     * 
     * @param email Destinatario
     * @param subject Asunto
     * @param message Cuerpo del mensaje
     * @return true si se envió exitosamente
     */
    boolean sendEmail(String email, String subject, String message);
}
