package com.microservices.order.infrastructure.config;

import com.microservices.order.application.port.output.UserValidationPort;
import com.microservices.order.application.service.OrderService;
import com.microservices.order.domain.repository.OrderRepository;
import com.microservices.order.infrastructure.adapter.output.client.UserServiceClient;
import com.microservices.order.infrastructure.adapter.output.client.UserValidationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ⚙️ APPLICATION SERVICE CONFIG
 * 
 * Registra los servicios de aplicación como beans sin acoplarlos a Spring.
 * 
 * ¿Por qué?
 * - El dominio y la aplicación NO conocen Spring
 * - Esta clase de infraestructura los registra en el contenedor
 * - Facilita testing (puedes instanciar servicios sin Spring)
 * - Mantiene la arquitectura hexagonal pura
 */
@Configuration
public class ApplicationServiceConfig {

    @Bean
    public UserValidationPort userValidationPort(UserServiceClient userServiceClient) {
        return new UserValidationAdapter(userServiceClient);
    }

    @Bean
    public OrderService orderService(OrderRepository orderRepository, UserValidationPort userValidationPort) {
        return new OrderService(orderRepository, userValidationPort);
    }
}
