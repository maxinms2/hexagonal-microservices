package com.microservices.user.infrastructure.config;

import com.microservices.user.application.service.DeleteUserService;
import com.microservices.user.application.service.UserService;
import com.microservices.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los servicios de aplicación como beans sin acoplarlos a Spring.
 */
@Configuration
public class ApplicationServiceConfig {

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }

    @Bean
    public DeleteUserService deleteUserService(UserRepository userRepository) {
        return new DeleteUserService(userRepository);
    }
}
