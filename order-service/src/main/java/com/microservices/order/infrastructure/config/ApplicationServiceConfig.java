package com.microservices.order.infrastructure.config;

import com.microservices.order.application.port.output.PublishOrderEventPort;
import com.microservices.order.application.port.output.UserValidationPort;
import com.microservices.order.application.service.OrderService;
import com.microservices.order.application.usecase.CreateOrderUseCase;
import com.microservices.order.application.usecase.DeleteOrderUseCase;
import com.microservices.order.application.usecase.FindAllOrdersUseCase;
import com.microservices.order.application.usecase.FindOrderByIdUseCase;
import com.microservices.order.application.usecase.UpdateOrderStatusUseCase;
import com.microservices.order.domain.repository.OrderRepository;
import com.microservices.order.infrastructure.adapter.output.client.UserServiceClient;
import com.microservices.order.infrastructure.adapter.output.client.UserValidationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/**
 * ⚙️ APPLICATION SERVICE CONFIG
 * 
 * Registra los servicios de aplicación como beans sin acoplarlos a Spring.
 * 
 * Arquitectura de Inyección:
 * ├─ Application Layer (OrderService)
 * │  └─ Implementa interfaces de USE CASES (agnósticas, públicas)
 * │  └─ SIN anotaciones Spring
 * │  └─ Lógica pura de negocio
 * │
 * └─ Infrastructure Layer (esta clase)
 *    ├─ Crea UNA sola instancia de OrderService
 *    ├─ La expone mediante sus interfaces (CreateOrderUseCase, etc.)
 *    ├─ Envuelve con @Transactional (coordinación transaccional en infraestructura)
 *    └─ Controllers inyectan SOLO las interfaces, NO la clase concreta
 * 
 * Principios respetados:
 * ✅ Infrastructure SOLO conoce las interfaces públicas de Application
 * ✅ Application NO tiene anotaciones de Spring (@Service, @Transactional)
 * ✅ Cada capa tiene una responsabilidad clara y aislada
 */
@Configuration
public class ApplicationServiceConfig {

    @Bean
    public UserValidationPort userValidationPort(UserServiceClient userServiceClient) {
        return new UserValidationAdapter(userServiceClient);
    }

    /**
     * Registra UNA sola instancia de OrderService que implementa todos los casos de uso.
     * Este bean NO tiene @Transactional porque la coordinación transaccional
     * se hace en los beans de los USE CASES (abajo).
     * 
     * Infrastructure mantiene el control de las transacciones en la capa correcta.
     */
    @Bean
    public OrderService orderService(
            OrderRepository orderRepository,
            UserValidationPort userValidationPort,
            PublishOrderEventPort publishOrderEventPort) {
        return new OrderService(orderRepository, userValidationPort, publishOrderEventPort);
    }

    // Exponer OrderService mediante sus interfaces para que los controllers inyecten interfaces
    // Se crean adapters agnósticos que delegan a OrderService sin contaminar con Spring
    // Las transacciones se coordinan aquí en infraestructura, no en application
    
    @Bean
    @Transactional  // Escritura: requiere transacción
    public CreateOrderUseCase createOrderUseCase(OrderService orderService) {
        return request -> orderService.execute(request);
    }

    @Bean
    @Transactional(readOnly = true)  // Lectura: transacción de solo lectura
    public FindOrderByIdUseCase findOrderByIdUseCase(OrderService orderService) {
        return orderId -> orderService.execute(orderId);
    }

    @Bean
    @Transactional(readOnly = true)  // Lectura: transacción de solo lectura
    public FindAllOrdersUseCase findAllOrdersUseCase(OrderService orderService) {
        return () -> orderService.execute();
    }

    @Bean
    @Transactional  // Escritura: requiere transacción
    public UpdateOrderStatusUseCase updateOrderStatusUseCase(OrderService orderService) {
        return (orderId, request) -> orderService.execute(orderId, request);
    }

    @Bean
    @Transactional  // Escritura: requiere transacción
    public DeleteOrderUseCase deleteOrderUseCase(OrderService orderService) {
        return orderId -> orderService.executeDelete(orderId);
    }
}
