package com.microservices.order.application.service;

import com.microservices.order.application.dto.CreateOrderRequest;
import com.microservices.order.application.dto.OrderResponse;
import com.microservices.order.application.dto.UpdateOrderStatusRequest;
import com.microservices.order.application.port.output.PublishOrderEventPort;
import com.microservices.order.application.port.output.UserValidationPort;
import com.microservices.order.application.usecase.CreateOrderUseCase;
import com.microservices.order.application.usecase.DeleteOrderUseCase;
import com.microservices.order.application.usecase.FindAllOrdersUseCase;
import com.microservices.order.application.usecase.FindOrderByIdUseCase;
import com.microservices.order.application.usecase.UpdateOrderStatusUseCase;
import com.microservices.order.domain.event.OrderCreatedEvent;
import com.microservices.order.domain.exception.InvalidOrderStateException;
import com.microservices.order.domain.exception.OrderNotFoundException;
import com.microservices.order.domain.model.Order;
import com.microservices.order.domain.model.OrderId;
import com.microservices.order.domain.model.OrderStatus;
import com.microservices.order.domain.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 🎯 ORDER SERVICE - Implementación de los Casos de Uso
 * 
 * Esta clase implementa TODOS los casos de uso relacionados con órdenes.
 * Contiene la lógica de aplicación pura (orquestación).
 *
 * Flujo:
 * Controller → UseCase (interface) → Service (implementación) → Repository (interface) → DB
 * 
 * IMPORTANTE: 
 * - Esta clase NO depende de Spring (no tiene anotaciones @Service ni @Transactional)
 * - Es agnóstica del framework
 * - Se registra como bean en ApplicationServiceConfig (infraestructura)
 * - Implementa las interfaces de USE CASES (agnósticas, públicas)
 * - Infrastructure solo conoce estas interfaces, no la clase OrderService
 */
public class OrderService implements
        CreateOrderUseCase,
        FindOrderByIdUseCase,
        FindAllOrdersUseCase,
        UpdateOrderStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserValidationPort userValidationPort;
    private final PublishOrderEventPort publishOrderEventPort;

    public OrderService(OrderRepository orderRepository, UserValidationPort userValidationPort, PublishOrderEventPort publishOrderEventPort) {
        this.orderRepository = orderRepository;
        this.userValidationPort = userValidationPort;
        this.publishOrderEventPort = publishOrderEventPort;
    }

    // ============================================
    // CREATE ORDER
    // ============================================

    @Override
    public OrderResponse execute(CreateOrderRequest request) {
        log.info("🔹 Creando orden para userId {}", request.userId());

        UUID userId = UUID.fromString(request.userId());
        BigDecimal total = request.totalAmount();

        // ============================================
        // COMUNICACIÓN INTER-MICROSERVICIOS
        // Validar que el usuario existe en user-service (vía puerto de salida)
        // ============================================
        userValidationPort.validateUserExists(request.userId());

        Order order = Order.create(userId, total);
        Order saved = orderRepository.save(order);

        // ============================================
        // PUBLICAR EVENTO DE ORDEN CREADA
        // El evento se publica a Kafka para que otros microservicios
        // (como notification-service) puedan reaccionar
        // ============================================
        OrderCreatedEvent event = new OrderCreatedEvent(
            saved.getId().value().toString(),
            userId.toString(),
            null, // El email se podría obtener del user-service si es necesario
            total.doubleValue(),
            "Nueva orden creada",
            java.time.LocalDateTime.now(),
            "OrderCreated"
        );
        publishOrderEventPort.publishOrderCreatedEvent(event);

        log.info("✅ Orden {} creada y evento publicado", saved.getId());
        return OrderResponse.from(saved);
    }

    // ============================================
    // FIND ORDER BY ID
    // ============================================

    @Override
    public OrderResponse execute(String orderId) {
        OrderId id = OrderId.of(orderId);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderResponse.from(order);
    }

    // ============================================
    // FIND ALL ORDERS
    // ============================================

    @Override
    public List<OrderResponse> execute() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    // ============================================
    // UPDATE ORDER STATUS
    // ============================================

    @Override
    public OrderResponse execute(String orderId, UpdateOrderStatusRequest request) {
        OrderId id = OrderId.of(orderId);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        OrderStatus target = request.status();
        OrderStatus current = order.getStatus();

        if (current == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException(current, target);
        }

        switch (target) {
            case PAID -> order.markPaid();
            case CANCELLED -> order.cancel();
            case CREATED -> order.setStatus(OrderStatus.CREATED);
        }

        Order updated = orderRepository.save(order);
        return OrderResponse.from(updated);
    }

    // ============================================
    // DELETE ORDER
    // ============================================

    public void executeDelete(String orderId) {
        OrderId id = OrderId.of(orderId);
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(orderId);
        }
        orderRepository.deleteById(id);
    }
}
