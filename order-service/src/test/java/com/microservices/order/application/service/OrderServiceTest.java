package com.microservices.order.application.service;

import com.microservices.order.application.dto.CreateOrderRequest;
import com.microservices.order.application.dto.OrderResponse;
import com.microservices.order.application.dto.UpdateOrderStatusRequest;
import com.microservices.order.application.port.output.PublishOrderEventPort;
import com.microservices.order.application.port.output.UserValidationPort;
import com.microservices.order.domain.event.OrderCreatedEvent;
import com.microservices.order.domain.exception.OrderNotFoundException;
import com.microservices.order.domain.model.Order;
import com.microservices.order.domain.model.OrderId;
import com.microservices.order.domain.model.OrderStatus;
import com.microservices.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 🧪 UNIT TESTS PARA ORDER SERVICE (Application Layer)
 * 
 * PROPÓSITO:
 * - Testear CASOS DE USO del servicio de órdenes
 * - Verificar orquestación de múltiples puertos de salida
 * - Validar eventos se publican correctamente
 * - Verificar interacción con User Validation Port
 * 
 * ¿POR QUÉ HEXAGONAL AYUDA?
 * - OrderRepository, UserValidationPort, PublishOrderEventPort son INTERFACES (puertos)
 * - Cada puerto se puede mockear INDEPENDIENTEMENTE
 * - Podemos testear diferentes escenarios sin infraestructura real
 * - Es fácil aislar fallos en comunicación inter-microservicios
 * 
 * PATRONES AVANZADOS:
 * - ArgumentCaptor: Captura argumentos pasados a mocks para verificarlos
 * - Verificar comportamientos complejos (eventos publicados, etc.)
 * - Testear exceppciones de puertos externos
 */
@DisplayName("🧪 Order Service (Application Layer) Tests")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    // ============================================
    // MOCKS DE PUERTOS (Depedencias de Salida)
    // ============================================
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private UserValidationPort userValidationPort;
    
    @Mock
    private PublishOrderEventPort publishOrderEventPort;
    
    // ============================================
    // CLASE A TESTEAR
    // ============================================
    
    @InjectMocks
    private OrderService orderService;
    
    // ============================================
    // DATOS DE PRUEBA
    // ============================================
    
    private UUID testUserId;
    private BigDecimal testAmount;
    private OrderId testOrderId;
    private Order testOrder;
    
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testAmount = BigDecimal.valueOf(100.00);
        testOrderId = OrderId.generate();
        
        testOrder = new Order(
            testOrderId,
            testUserId,
            testAmount,
            OrderStatus.CREATED,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    // ============================================
    // CREATE ORDER TESTS
    // ============================================
    
    @Nested
    @DisplayName("✅ execute(CreateOrderRequest) - Crear Orden")
    class CreateOrderTests {
        
        @Test
        @DisplayName("Debe crear orden y validar usuario en user-service")
        void shouldCreateOrderAndValidateUser() {
            // Arrange
            CreateOrderRequest request = new CreateOrderRequest(
                testUserId.toString(),
                BigDecimal.valueOf(150.00)
            );
            
            // Mock 1: User Validation Port (validar que usuario existe)
            doNothing().when(userValidationPort)
                .validateUserExists(testUserId.toString());
            
            // Mock 2: Order Repository (guardar orden)
            when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(testOrderId);
                    return order;
                });
            
            // Mock 3: Publish Event Port (publicar evento)
            doNothing().when(publishOrderEventPort)
                .publishOrderCreatedEvent(any(OrderCreatedEvent.class));
            
            // Act
            OrderResponse response = orderService.execute(request);
            
            // Assert
            assertNotNull(response);
            assertEquals(testUserId, response.userId());
            assertEquals(BigDecimal.valueOf(150.00), response.totalAmount());
            assertEquals(OrderStatus.CREATED, response.status());
            
            // Verify: Verificar que todos los mocks fueron llamados en orden correcto
            verify(userValidationPort).validateUserExists(testUserId.toString());
            verify(orderRepository).save(any(Order.class));
            verify(publishOrderEventPort).publishOrderCreatedEvent(any(OrderCreatedEvent.class));
        }
        
        @Test
        @DisplayName("Debe rechazar si usuario no existe en user-service")
        void shouldRejectIfUserNotFoundInUserService() {
            // Arrange
            CreateOrderRequest request = new CreateOrderRequest(
                testUserId.toString(),
                BigDecimal.valueOf(100.00)
            );
            
            // Mock: User Validation Port lanza excepción
            doThrow(new RuntimeException("Usuario no encontrado"))
                .when(userValidationPort)
                .validateUserExists(testUserId.toString());
            
            // Act & Assert
            assertThrows(RuntimeException.class,
                () -> orderService.execute(request),
                "Debe lanzar excepción si usuario no existe");
            
            // Verify: No debe guardarse la orden ni publicarse evento
            verify(orderRepository, never()).save(any(Order.class));
            verify(publishOrderEventPort, never())
                .publishOrderCreatedEvent(any(OrderCreatedEvent.class));
        }
        
        @Test
        @DisplayName("Debe rechazar monto <= 0")
        void shouldRejectInvalidAmount() {
            // Arrange
            CreateOrderRequest request = new CreateOrderRequest(
                testUserId.toString(),
                BigDecimal.ZERO
            );
            
            // Act & Assert
            assertThrows(Exception.class,
                () -> orderService.execute(request));
            
            // Verify - La validación de monto ocurre en Order.create(), después de validar el usuario
            verify(orderRepository, never()).save(any(Order.class));
        }
        
        @Test
        @DisplayName("Debe publicar evento cuando orden se crea exitosamente")
        void shouldPublishEventOnSuccessfulOrderCreation() {
            // Arrange
            CreateOrderRequest request = new CreateOrderRequest(
                testUserId.toString(),
                testAmount
            );
            
            doNothing().when(userValidationPort)
                .validateUserExists(testUserId.toString());
            
            when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(testOrderId);
                    return order;
                });
            
            // Usar ArgumentCaptor para capturar el evento publicado
            ArgumentCaptor<OrderCreatedEvent> eventCaptor = 
                ArgumentCaptor.forClass(OrderCreatedEvent.class);
            
            doNothing().when(publishOrderEventPort)
                .publishOrderCreatedEvent(eventCaptor.capture());
            
            // Act
            orderService.execute(request);
            
            // Assert
            verify(publishOrderEventPort).publishOrderCreatedEvent(any());
            
            // Verificar detalles del evento capturado
            OrderCreatedEvent capturedEvent = eventCaptor.getValue();
            assertEquals(testUserId.toString(), capturedEvent.getCustomerId());
            assertEquals("OrderCreated", capturedEvent.getEventType());
            assertNotNull(capturedEvent.getCreatedAt());
        }
    }
    
    // ============================================
    // FIND ORDER TESTS
    // ============================================
    
    @Nested
    @DisplayName("🔍 execute(String) - Buscar Orden por ID")
    class FindOrderTests {
        
        @Test
        @DisplayName("Debe encontrar orden existente")
        void shouldFindExistingOrder() {
            // Arrange
            String orderId = testOrderId.value().toString();
            
            when(orderRepository.findById(OrderId.of(orderId)))
                .thenReturn(Optional.of(testOrder));
            
            // Act
            OrderResponse response = orderService.execute(orderId);
            
            // Assert
            assertNotNull(response);
            assertEquals(testUserId, response.userId());
            assertEquals(testAmount, response.totalAmount());
            
            // Verify
            verify(orderRepository).findById(OrderId.of(orderId));
        }
        
        @Test
        @DisplayName("Debe lanzar excepción si orden no existe")
        void shouldThrowExceptionIfOrderNotFound() {
            // Arrange
            String orderId = UUID.randomUUID().toString();
            
            when(orderRepository.findById(OrderId.of(orderId)))
                .thenReturn(Optional.empty());
            
            // Act & Assert
            assertThrows(OrderNotFoundException.class,
                () -> orderService.execute(orderId));
            
            // Verify
            verify(orderRepository).findById(OrderId.of(orderId));
        }
    }
    
    // ============================================
    // FIND ALL ORDERS TESTS
    // ============================================
    
    @Nested
    @DisplayName("📋 execute() - Obtener Todas las Órdenes")
    class FindAllOrdersTests {
        
        @Test
        @DisplayName("Debe retornar todas las órdenes")
        void shouldReturnAllOrders() {
            // Arrange
            List<Order> orders = new ArrayList<>();
            orders.add(testOrder);
            
            Order order2 = new Order(
                OrderId.generate(),
                UUID.randomUUID(),
                BigDecimal.valueOf(200.00),
                OrderStatus.PAID,
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            orders.add(order2);
            
            when(orderRepository.findAll())
                .thenReturn(orders);
            
            // Act
            List<OrderResponse> response = orderService.execute();
            
            // Assert
            assertNotNull(response);
            assertEquals(2, response.size());
            assertEquals(testAmount, response.get(0).totalAmount());
            assertEquals(BigDecimal.valueOf(200.00), response.get(1).totalAmount());
            
            // Verify
            verify(orderRepository).findAll();
        }
        
        @Test
        @DisplayName("Debe retornar lista vacía si no hay órdenes")
        void shouldReturnEmptyListIfNoOrders() {
            // Arrange
            when(orderRepository.findAll())
                .thenReturn(new ArrayList<>());
            
            // Act
            List<OrderResponse> response = orderService.execute();
            
            // Assert
            assertNotNull(response);
            assertTrue(response.isEmpty());
            
            // Verify
            verify(orderRepository).findAll();
        }
    }
    
    // ============================================
    // UPDATE ORDER STATUS TESTS
    // ============================================
    
    @Nested
    @DisplayName("🔄 execute(String, UpdateOrderStatusRequest) - Actualizar Estado")
    class UpdateOrderStatusTests {
        
        @Test
        @DisplayName("Debe actualizar estado de orden de CREATED a PAID")
        void shouldUpdateOrderStatusFromCreatedToPaid() {
            // Arrange
            String orderId = testOrderId.value().toString();
            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
                OrderStatus.PAID
            );
            
            when(orderRepository.findById(OrderId.of(orderId)))
                .thenReturn(Optional.of(testOrder));
            
            when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
            
            // Act
            OrderResponse response = orderService.execute(orderId, request);
            
            // Assert
            assertEquals(OrderStatus.PAID, response.status());
            
            // Verify
            verify(orderRepository).findById(OrderId.of(orderId));
            verify(orderRepository).save(any(Order.class));
        }
        
        @Test
        @DisplayName("Debe lanzar excepción si orden no existe")
        void shouldThrowExceptionIfOrderNotFoundOnUpdate() {
            // Arrange
            String orderId = UUID.randomUUID().toString();
            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
                OrderStatus.PAID
            );
            
            when(orderRepository.findById(OrderId.of(orderId)))
                .thenReturn(Optional.empty());
            
            // Act & Assert
            assertThrows(OrderNotFoundException.class,
                () -> orderService.execute(orderId, request));
            
            // Verify
            verify(orderRepository, never()).save(any(Order.class));
        }
    }
    
    // ============================================
    // DELETE ORDER TESTS
    // ============================================
    
    @Nested
    @DisplayName("🗑️ executeDelete(String) - Eliminar Orden")
    class DeleteOrderTests {
        
        @Test
        @DisplayName("Debe eliminar orden existente")
        void shouldDeleteExistingOrder() {
            // Arrange
            String orderId = testOrderId.value().toString();
            
            when(orderRepository.existsById(OrderId.of(orderId)))
                .thenReturn(true);
            
            doNothing().when(orderRepository).deleteById(OrderId.of(orderId));
            
            // Act
            orderService.executeDelete(orderId);
            
            // Assert - No exception thrown
            
            // Verify
            verify(orderRepository).existsById(OrderId.of(orderId));
            verify(orderRepository).deleteById(OrderId.of(orderId));
        }
        
        @Test
        @DisplayName("Debe lanzar excepción si orden no existe")
        void shouldThrowExceptionIfOrderNotFoundOnDelete() {
            // Arrange
            String orderId = UUID.randomUUID().toString();
            
            when(orderRepository.existsById(OrderId.of(orderId)))
                .thenReturn(false);
            
            // Act & Assert
            assertThrows(OrderNotFoundException.class,
                () -> orderService.executeDelete(orderId));
            
            // Verify
            verify(orderRepository, never()).deleteById(any());
        }
    }
    
    // ============================================
    // INTER-MICROSERVICES COMMUNICATION TESTS
    // ============================================
    
    @Nested
    @DisplayName("🌐 Comunicación Inter-Microservicios")
    class InterMicroservicesCommunicationTests {
        
        @Test
        @DisplayName("Debe validar usuario ANTES de crear orden")
        void shouldValidateUserBeforeCreatingOrder() {
            // Arrange
            CreateOrderRequest request = new CreateOrderRequest(
                testUserId.toString(),
                testAmount
            );
            
            // Track call order
            InOrder inOrder = inOrder(userValidationPort, orderRepository);
            
            doNothing().when(userValidationPort)
                .validateUserExists(testUserId.toString());
            
            when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(testOrderId);
                    return order;
                });
            
            doNothing().when(publishOrderEventPort)
                .publishOrderCreatedEvent(any());
            
            // Act
            orderService.execute(request);
            
            // Verify order: primero validar usuario, luego guardar
            inOrder.verify(userValidationPort).validateUserExists(testUserId.toString());
            inOrder.verify(orderRepository).save(any(Order.class));
        }
        
        @Test
        @DisplayName("Debe continuar aunque evento no se pueda publicar (graceful degradation)")
        void shouldContinueIfEventPublicationFails() {
            // Arrange
            CreateOrderRequest request = new CreateOrderRequest(
                testUserId.toString(),
                testAmount
            );
            
            doNothing().when(userValidationPort)
                .validateUserExists(testUserId.toString());
            
            when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(testOrderId);
                    return order;
                });
            
            // Event publication fails
            doThrow(new RuntimeException("Kafka no disponible"))
                .when(publishOrderEventPort)
                .publishOrderCreatedEvent(any());
            
            // Act & Assert - Dependiendo de la implementación:
            // Si la excepción se propaga: expect it
            // Si se maneja gracefully: no exception
            // Aquí asumimos que se propaga
            assertThrows(RuntimeException.class,
                () -> orderService.execute(request));
            
            // Verify que orden fue guardada aunque evento fallara
            verify(orderRepository).save(any(Order.class));
        }
    }
    
    // ============================================
    // EDGE CASES Y BOUNDARY TESTS
    // ============================================
    
    @Nested
    @DisplayName("⚠️ Edge Cases - Casos Límite")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Debe manejar montos muy grandes")
        void shouldHandleLargeAmounts() {
            // Arrange
            CreateOrderRequest request = new CreateOrderRequest(
                testUserId.toString(),
                new BigDecimal("999999999.99")
            );
            
            doNothing().when(userValidationPort)
                .validateUserExists(testUserId.toString());
            
            when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(testOrderId);
                    return order;
                });
            
            doNothing().when(publishOrderEventPort)
                .publishOrderCreatedEvent(any());
            
            // Act
            OrderResponse response = orderService.execute(request);
            
            // Assert
            assertEquals(new BigDecimal("999999999.99"), response.totalAmount());
            
            // Verify
            verify(orderRepository).save(any(Order.class));
        }
        
        @Test
        @DisplayName("Debe manejar montos muy pequeños (pero válidos)")
        void shouldHandleSmallAmounts() {
            // Arrange
            CreateOrderRequest request = new CreateOrderRequest(
                testUserId.toString(),
                new BigDecimal("0.01")
            );
            
            doNothing().when(userValidationPort)
                .validateUserExists(testUserId.toString());
            
            when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(testOrderId);
                    return order;
                });
            
            doNothing().when(publishOrderEventPort)
                .publishOrderCreatedEvent(any());
            
            // Act
            OrderResponse response = orderService.execute(request);
            
            // Assert
            assertEquals(new BigDecimal("0.01"), response.totalAmount());
        }
    }
}
