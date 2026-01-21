package com.microservices.order.infrastructure.adapter.input.rest;

import com.microservices.order.application.dto.CreateOrderRequest;
import com.microservices.order.application.dto.OrderResponse;
import com.microservices.order.application.dto.UpdateOrderStatusRequest;
import com.microservices.order.application.usecase.CreateOrderUseCase;
import com.microservices.order.application.usecase.DeleteOrderUseCase;
import com.microservices.order.application.usecase.FindAllOrdersUseCase;
import com.microservices.order.application.usecase.FindOrderByIdUseCase;
import com.microservices.order.application.usecase.UpdateOrderStatusUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 🌐 ORDER CONTROLLER - Adaptador de Entrada REST
 * 
 * Controlador REST que expone los endpoints HTTP para gestionar órdenes.
 * 
 * Este es un ADAPTADOR DE ENTRADA en la Arquitectura Hexagonal:
 * - Convierte peticiones HTTP a llamadas de casos de uso
 * - Maneja la serialización JSON
 * - Gestiona códigos de estado HTTP
 * - Valida datos de entrada con @Valid
 * 
 * Base URL: /orders
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final FindOrderByIdUseCase findOrderByIdUseCase;
    private final FindAllOrdersUseCase findAllOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           FindOrderByIdUseCase findOrderByIdUseCase,
                           FindAllOrdersUseCase findAllOrdersUseCase,
                           UpdateOrderStatusUseCase updateOrderStatusUseCase,
                           DeleteOrderUseCase deleteOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.findOrderByIdUseCase = findOrderByIdUseCase;
        this.findAllOrdersUseCase = findAllOrdersUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.deleteOrderUseCase = deleteOrderUseCase;
    }

    /**
     * Crea una nueva orden
     * POST /orders
     */
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = createOrderUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene una orden por ID
     * GET /orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> findById(@PathVariable String orderId) {
        OrderResponse response = findOrderByIdUseCase.execute(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todas las órdenes
     * GET /orders
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(findAllOrdersUseCase.execute());
    }

    /**
     * Actualiza el estado de una orden
     * PATCH /orders/{orderId}/status
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable String orderId,
                                                      @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = updateOrderStatusUseCase.execute(orderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina una orden
     * DELETE /orders/{orderId}
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(@PathVariable String orderId) {
        deleteOrderUseCase.execute(orderId);
        return ResponseEntity.noContent().build();
    }
}
