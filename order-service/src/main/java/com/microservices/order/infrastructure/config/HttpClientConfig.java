package com.microservices.order.infrastructure.config;

import com.microservices.order.infrastructure.adapter.output.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * 🔧 HTTP CLIENT CONFIG - Configuración de Clientes HTTP
 * 
 * Registra los clientes HTTP (HTTP Interfaces) como beans de Spring.
 * 
 * ARQUITECTURA DE COMUNICACIÓN ENTRE MICROSERVICIOS:
 * 
 * ┌─────────────────────────────────────────────────────┐
 * │                   Order Service                      │
 * │                                                      │
 * │  OrderService → UserServiceClient (HTTP Interface)  │
 * │                        ↓                            │
 * │                    WebClient                        │
 * │                        ↓                            │
 * │            HTTP Request to User Service             │
 * │                        ↓                            │
 * │              http://user-service:8081/users/{id}    │
 * │                        ↓                            │
 * │                 User Service Response               │
 * │                (UserResponse DTO)                   │
 * └─────────────────────────────────────────────────────┘
 * 
 * VENTAJAS DE ESTA ARQUITECTURA:
 * 1. Desacoplamiento: Order Service no conoce detalles de User Service
 * 2. Cambios transparentes: Cambios en User Service no afectan Order Service
 * 3. Escalabilidad: Cada servicio puede escalar independientemente
 * 4. Tolerancia a fallos: Se puede implementar retry/circuit-breaker
 * 5. Testing: Fácil hacer mock del cliente para tests
 * 
 * CONFIGURACIÓN:
 * - WebClient: Cliente reactivo (async, no-blocking)
 * - HttpServiceProxyFactory: Crea proxy de la interface
 * - user.service.url: URL base del user-service (configurable)
 */
@Configuration
public class HttpClientConfig {

    /**
     * URL base del user-service
     * Se configura en application.yml y puede variar por perfil
     * 
     * Desarrollo: http://localhost:8081
     * Producción: http://user-service (si está en Kubernetes/Docker)
     */
    @Value("${user-service.url:http://localhost:8081}")
    private String userServiceUrl;

    /**
     * Registra el cliente HTTP para acceder a User Service
     * 
     * Flujo:
     * 1. Crea un WebClient apuntando a userServiceUrl
     * 2. Crea HttpServiceProxyFactory usando el WebClient
     * 3. Genera un proxy automáticamente basado en UserServiceClient
     * 4. El proxy implementa la interface y hace las llamadas HTTP reales
     * 
     * @return Bean de UserServiceClient listo para inyectar
     */
    @Bean
    public UserServiceClient userServiceClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(UserServiceClient.class);
    }
}
