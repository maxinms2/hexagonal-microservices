package com.microservices.order.infrastructure.adapter.output.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

/**
 * 🔗 USER SERVICE CLIENT - HTTP Interface
 * 
 * Cliente declarativo para comunicarse con el user-service.
 * 
 * ¿POR QUÉ HTTP INTERFACES?
 * 
 * Comparativa de opciones de comunicación en Spring Boot 3.2:
 * 
 * 1. RestTemplate (DEPRECATED) ❌
 *    - Imperativo
 *    - Síncrono
 *    - Anticuado (marcado como deprecated)
 *    - No recomendado para nuevos proyectos
 * 
 * 2. Feign (OpenFeign) ⚠️
 *    - Declarativo
 *    - Fácil de usar
 *    - Requiere dependencia adicional (spring-cloud-starter-openfeign)
 *    - Aún se usa pero menos moderno
 *    - Acoplamiento a Spring Cloud
 * 
 * 3. WebClient (Reactivo) ✅✅
 *    - Reactivo y async
 *    - Mejor rendimiento
 *    - Habilitado por defecto en Spring Boot
 *    - Ideal para aplicaciones de alto rendimiento
 *    - Curva de aprendizaje más pronunciada
 * 
 * 4. HTTP Interfaces (RECOMENDADO) ✅✅✅
 *    - Nuevo en Spring 6 / Spring Boot 3.1+
 *    - Lo más moderno y recomendado por Spring
 *    - Declarativo como Feign, pero sin dependencias extra
 *    - Usa WebClient internamente (reactivo)
 *    - Zero-boilerplate
 *    - No requiere implementación (Spring genera proxy automático)
 *    - Mejor rendimiento que RestTemplate/Feign
 * 
 * ELECCIÓN: HTTP Interfaces
 * Razones:
 * - Es la dirección oficial de Spring Framework
 * - Menor overhead que Feign
 * - Reactivo sin complejidad (usa WebClient internamente)
 * - Mejor para microservicios modernos
 * - No requiere dependencias de Spring Cloud
 * 
 * ¿CÓMO FUNCIONA?
 * 1. Definimos una interface con anotaciones @GetExchange, @PostExchange, etc.
 * 2. Spring crea automáticamente un proxy que implementa la interfaz
 * 3. Las llamadas HTTP se hacen automáticamente
 * 4. Manejo de errores y reintentos configurables
 */
public interface UserServiceClient {

    /**
     * Obtiene un usuario del user-service por su ID
     * 
     * @param userId ID del usuario a obtener
     * @return Información del usuario (id, email, name)
     * @throws org.springframework.web.client.HttpClientErrorException.NotFound si no existe
     */
    @GetExchange("/api/users/{userId}")
    UserResponse getUserById(@PathVariable String userId);
}
