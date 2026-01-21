package com.microservices.order.application.service;

import com.microservices.order.application.dto.CreateOrderRequest;
import com.microservices.order.application.dto.OrderResponse;
import com.microservices.order.application.dto.UpdateOrderStatusRequest;
import com.microservices.order.application.port.output.UserValidationPort;
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
 * Contiene la lógica de aplicación (orquestación).
 *
 * Flujo:
 * Controller → UseCase (interface) → Service (implementación) → Repository (interface) → DB
 * 
 * IMPORTANTE: Esta clase NO depende de Spring (no tiene anotaciones @Service).
 * Se registra como bean en ApplicationServiceConfig.
 */
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserValidationPort userValidationPort;

    public OrderService(OrderRepository orderRepository, UserValidationPort userValidationPort) {
        this.orderRepository = orderRepository;
        this.userValidationPort = userValidationPort;
    }

    // ============================================
    // CREATE ORDER
    // ============================================

    public OrderResponse create(CreateOrderRequest request) {
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

        log.info("✅ Orden {} creada", saved.getId());
        return OrderResponse.from(saved);
    }

    // ============================================
    // FIND ORDER BY ID
    // ============================================

    public OrderResponse findById(String orderId) {
        OrderId id = OrderId.of(orderId);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderResponse.from(order);
    }

    // ============================================
    // FIND ALL ORDERS
    // ============================================

    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    // ============================================
    // UPDATE ORDER STATUS
    // ============================================

    public OrderResponse updateStatus(String orderId, UpdateOrderStatusRequest request) {
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

    public void delete(String orderId) {
        OrderId id = OrderId.of(orderId);
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(orderId);
        }
        orderRepository.deleteById(id);
    }
}
