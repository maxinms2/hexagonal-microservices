package com.microservices.order.domain.repository;

import com.microservices.order.domain.model.Order;
import com.microservices.order.domain.model.OrderId;
import com.microservices.order.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
    List<Order> findAll();
    List<Order> findByStatus(OrderStatus status);
    void deleteById(OrderId id);
    boolean existsById(OrderId id);
}
