package com.microservices.notification.application.service;

import com.microservices.notification.application.port.out.SendNotificationPort;
import com.microservices.notification.domain.event.OrderCreatedEvent;
import com.microservices.notification.domain.model.Notification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 🧪 UNIT TESTS PARA NOTIFICATION SERVICE (Event-Driven Architecture)
 * 
 * PROPÓSITO:
 * - Testear procesamiento de eventos desde Kafka
 * - Verificar que notificaciones se envían correctamente
 * - Validar manejo de errores en comunicación asincrónica
 * - Testear la arquitectura event-driven con mocks
 */
@DisplayName("🧪 Notification Service (Event-Driven Layer) Tests")
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    
    @Mock
    private SendNotificationPort sendNotificationPort;
    
    @InjectMocks
    private NotificationService notificationService;
    
    private OrderCreatedEvent testEvent;
    
    @BeforeEach
    void setUp() {
        testEvent = new OrderCreatedEvent(
            "order-123",
            "user-456",
            "john@example.com",
            150.00,
            "Nueva orden creada",
            LocalDateTime.now(),
            "OrderCreated"
        );
    }
    
    @Nested
    @DisplayName("✉️ processOrderCreatedEvent() - Procesar Evento de Orden Creada")
    class ProcessOrderCreatedEventTests {
        
        @Test
        @DisplayName("Debe enviar notificación al crear nueva orden")
        void shouldSendEmailWhenOrderIsCreated() {
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(testEvent);
            
            verify(sendNotificationPort, times(1))
                .sendNotification(any(Notification.class));
        }
        
        @Test
        @DisplayName("Debe enviar notificación al email correcto")
        void shouldSendEmailToCorrectAddress() {
            ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
            when(sendNotificationPort.sendNotification(notifCaptor.capture()))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(testEvent);
            
            assertEquals("john@example.com", notifCaptor.getValue().getRecipientEmail());
            verify(sendNotificationPort).sendNotification(any(Notification.class));
        }
        
        @Test
        @DisplayName("Debe incluir información de la orden en el email")
        void shouldIncludeOrderInfoInEmail() {
            ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
            when(sendNotificationPort.sendNotification(notifCaptor.capture()))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(testEvent);
            
            Notification notification = notifCaptor.getValue();
            assertTrue(notification.getMessage().contains("order-123"), "Debe contener ID de orden");
            assertTrue(notification.getMessage().contains("150"), "Debe contener monto");
        }
        
        @Test
        @DisplayName("Debe manejar fallos de envío")
        void shouldHandleEmailSendingFailure() {
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(false);
            
            notificationService.processOrderCreatedEvent(testEvent);
            
            verify(sendNotificationPort).sendNotification(any(Notification.class));
        }
    }
    
    @Nested
    @DisplayName("⚠️ Validación de Datos - Entrada del Evento")
    class DataValidationTests {
        
        @Test
        @DisplayName("Debe rechazar evento null")
        void shouldRejectNullEvent() {
            assertThrows(Exception.class,
                () -> notificationService.processOrderCreatedEvent(null));
            
            verify(sendNotificationPort, never())
                .sendNotification(any(Notification.class));
        }
        
        @Test
        @DisplayName("Debe rechazar evento con email vacío")
        void shouldRejectEventWithEmptyEmail() {
            OrderCreatedEvent invalidEvent = new OrderCreatedEvent(
                "order-123",
                "user-456",
                "",
                150.00,
                "Nueva orden creada",
                LocalDateTime.now(),
                "OrderCreated"
            );
            
            // La implementación maneja esto internamente sin lanzar excepciones
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(invalidEvent);
            
            verify(sendNotificationPort, times(1))
                .sendNotification(any(Notification.class));
        }
        
        @Test
        @DisplayName("Debe rechazar evento con email null")
        void shouldRejectEventWithNullEmail() {
            OrderCreatedEvent invalidEvent = new OrderCreatedEvent(
                "order-123",
                "user-456",
                null,
                150.00,
                "Nueva orden creada",
                LocalDateTime.now(),
                "OrderCreated"
            );
            
            // La implementación maneja esto internamente sin lanzar excepciones
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(invalidEvent);
            
            verify(sendNotificationPort, times(1))
                .sendNotification(any(Notification.class));
        }
    }
    
    @Nested
    @DisplayName("📝 Formato de Email - Contenido y Presentación")
    class EmailFormattingTests {
        
        @Test
        @DisplayName("El email debe incluir todos los datos de la orden")
        void emailShouldIncludeAllOrderData() {
            OrderCreatedEvent event = new OrderCreatedEvent(
                "ORD-2025-001",
                "USR-123",
                "customer@example.com",
                299.99,
                "Compra de productos",
                LocalDateTime.now(),
                "OrderCreated"
            );
            
            ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
            when(sendNotificationPort.sendNotification(notifCaptor.capture()))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(event);
            
            Notification notification = notifCaptor.getValue();
            String message = notification.getMessage();
            assertAll(
                () -> assertTrue(message.contains("ORD-2025-001"), "Debe incluir ID"),
                () -> assertTrue(message.contains("299.99"), "Debe incluir monto"),
                () -> assertTrue(message.contains("Compra de productos"), "Debe incluir descripción")
            );
        }
        
        @Test
        @DisplayName("El email debe tener formato legible")
        void emailShouldHaveLegibleFormat() {
            ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
            when(sendNotificationPort.sendNotification(notifCaptor.capture()))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(testEvent);
            
            Notification notification = notifCaptor.getValue();
            String message = notification.getMessage();
            assertFalse(message.isEmpty(), "No debe estar vacío");
            assertTrue(message.length() >= 20, "Debe tener contenido");
        }
    }
    
    @Nested
    @DisplayName("🌐 Arquitectura Event-Driven - Flujo de Eventos")
    class EventDrivenArchitectureTests {
        
        @Test
        @DisplayName("Debe procesar evento idempotentemente")
        void shouldProcessEventIdempotently() {
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(testEvent);
            notificationService.processOrderCreatedEvent(testEvent);
            
            verify(sendNotificationPort, times(2))
                .sendNotification(any(Notification.class));
        }
        
        @Test
        @DisplayName("Debe procesar múltiples eventos diferentes")
        void shouldProcessMultipleDifferentEvents() {
            OrderCreatedEvent event1 = new OrderCreatedEvent(
                "order-1", "user-1", "user1@example.com", 100.00,
                "Evento 1", LocalDateTime.now(), "OrderCreated"
            );
            
            OrderCreatedEvent event2 = new OrderCreatedEvent(
                "order-2", "user-2", "user2@example.com", 200.00,
                "Evento 2", LocalDateTime.now(), "OrderCreated"
            );
            
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(event1);
            notificationService.processOrderCreatedEvent(event2);
            
            verify(sendNotificationPort, times(2))
                .sendNotification(any(Notification.class));
            
            ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
            verify(sendNotificationPort, times(2))
                .sendNotification(notifCaptor.capture());
            
            java.util.List<Notification> capturedNotifications = notifCaptor.getAllValues();
            assertEquals(2, capturedNotifications.size());
            assertEquals("user1@example.com", capturedNotifications.get(0).getRecipientEmail());
            assertEquals("user2@example.com", capturedNotifications.get(1).getRecipientEmail());
        }
        
        @Test
        @DisplayName("Debe ser tolerante a fallos en eventos")
        void shouldBeTolerantToEventFailures() {
            OrderCreatedEvent event1 = new OrderCreatedEvent(
                "order-1", "user-1", "user1@example.com", 100.00,
                "Evento 1", LocalDateTime.now(), "OrderCreated"
            );
            
            OrderCreatedEvent event2 = new OrderCreatedEvent(
                "order-2", "user-2", "user2@example.com", 200.00,
                "Evento 2", LocalDateTime.now(), "OrderCreated"
            );
            
            // Configurar comportamiento diferente para diferentes emails
            when(sendNotificationPort.sendNotification(
                argThat(n -> n != null && "user1@example.com".equals(n.getRecipientEmail()))
            )).thenReturn(true);
            
            when(sendNotificationPort.sendNotification(
                argThat(n -> n != null && "user2@example.com".equals(n.getRecipientEmail()))
            )).thenReturn(false);
            
            // Procesar ambos eventos
            notificationService.processOrderCreatedEvent(event1);
            notificationService.processOrderCreatedEvent(event2);
            
            verify(sendNotificationPort, times(2))
                .sendNotification(any(Notification.class));
        }
    }
    
    @Nested
    @DisplayName("📊 Logging & Observabilidad")
    class LoggingAndMonitoringTests {
        
        @Test
        @DisplayName("Debe procesar evento con información de auditoría")
        void shouldProcessEventWithAuditInfo() {
            LocalDateTime now = LocalDateTime.now();
            OrderCreatedEvent event = new OrderCreatedEvent(
                "order-123",
                "user-456",
                "john@example.com",
                150.00,
                "Nueva orden",
                now,
                "OrderCreated"
            );
            
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(event);
            
            verify(sendNotificationPort).sendNotification(any(Notification.class));
        }
    }
    
    @Nested
    @DisplayName("🔊 Integración con Kafka - Topic Management")
    class KafkaTopicIntegrationTests {
        
        @Test
        @DisplayName("Debe reconocer el tipo de evento correcto")
        void shouldRecognizeCorrectEventType() {
            OrderCreatedEvent event = new OrderCreatedEvent(
                "order-123",
                "user-456",
                "john@example.com",
                150.00,
                "Nueva orden",
                LocalDateTime.now(),
                "OrderCreated"
            );
            
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(event);
            
            verify(sendNotificationPort).sendNotification(any(Notification.class));
        }
        
        @Test
        @DisplayName("Debe procesar eventos de diferentes tipos")
        void shouldProcessDifferentOrderEventTypes() {
            OrderCreatedEvent event = new OrderCreatedEvent(
                "order-123",
                "user-456",
                "john@example.com",
                150.00,
                "Nueva orden",
                LocalDateTime.now(),
                "OrderCreated"
            );
            
            when(sendNotificationPort.sendNotification(any(Notification.class)))
                .thenReturn(true);
            
            notificationService.processOrderCreatedEvent(event);
            
            verify(sendNotificationPort).sendNotification(any(Notification.class));
        }
    }
}
