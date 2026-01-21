package com.microservices.order.infrastructure.adapter.output.persistence.inmemory;

import com.microservices.order.domain.model.Order;
import com.microservices.order.domain.model.OrderId;
import com.microservices.order.domain.model.OrderStatus;
import com.microservices.order.domain.repository.OrderRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 💾 IN-MEMORY ORDER REPOSITORY
 * 
 * Implementación del repositorio en memoria para desarrollo y testing.
 * 
 * Características:
 * - Usa un ConcurrentHashMap para almacenamiento thread-safe
 * - Solo activo en perfil 'dev'
 * - Los datos se pierden al reiniciar la aplicación
 * - Útil para pruebas rápidas sin base de datos
 * 
 * En producción, reemplazar por implementación JPA con PostgreSQL.
 */
@Repository
@Profile("dev")
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> store = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return store.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(OrderId id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(OrderId id) {
        return store.containsKey(id);
    }
}
