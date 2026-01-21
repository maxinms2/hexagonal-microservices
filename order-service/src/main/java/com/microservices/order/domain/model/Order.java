package com.microservices.order.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 🛒 ORDER - Entidad de Dominio
 * 
 * Representa una orden de compra en el sistema.
 * 
 * IMPORTANTE: Esta clase NO tiene anotaciones de JPA (@Entity)
 * porque pertenece al DOMINIO, no a la infraestructura.
 * 
 * El dominio debe ser independiente de frameworks y bases de datos.
 * 
 * Reglas de negocio:
 * - Una orden siempre se crea en estado CREATED
 * - El total debe ser siempre mayor que cero
 * - Una orden cancelada no puede cambiar de estado
 */
public class Order {

    private OrderId id;
    private UUID userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order() {
    }

    public Order(OrderId id, UUID userId, BigDecimal totalAmount, OrderStatus status,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ============================================
    // FACTORY METHOD: Crear nueva orden
    // ============================================
    
    /**
     * Crea una nueva orden con valores por defecto.
     * Este es un método de fábrica que garantiza que
     * las nuevas órdenes se crean de forma consistente.
     * 
     * @param userId ID del usuario que crea la orden
     * @param totalAmount Monto total de la orden
     * @return Nueva orden en estado CREATED
     */
    public static Order create(UUID userId, BigDecimal totalAmount) {
        if (userId == null) {
            throw new IllegalArgumentException("userId es obligatorio");
        }
        if (totalAmount == null || totalAmount.signum() <= 0) {
            throw new IllegalArgumentException("totalAmount debe ser mayor que cero");
        }

        Order order = new Order();
        order.setId(OrderId.generate());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    // ============================================
    // BUSINESS LOGIC: Lógica de Negocio
    // ============================================
    
    /**
     * Marca la orden como pagada.
     * Solo órdenes en estado CREATED pueden ser pagadas.
     */
    public void markPaid() {
        this.status = OrderStatus.PAID;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancela la orden.
     * Una orden cancelada no puede volver a cambiar de estado.
     */
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza el monto total de la orden.
     * 
     * @param newTotal Nuevo monto total (debe ser mayor que cero)
     */
    public void updateTotal(BigDecimal newTotal) {
        if (newTotal == null || newTotal.signum() <= 0) {
            throw new IllegalArgumentException("totalAmount debe ser mayor que cero");
        }
        this.totalAmount = newTotal;
        this.updatedAt = LocalDateTime.now();
    }

    public OrderId getId() {
        return id;
    }

    public void setId(OrderId id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
