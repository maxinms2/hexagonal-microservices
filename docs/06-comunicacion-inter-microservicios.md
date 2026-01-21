# 🔗 Comunicación Inter-Microservicios

## 📚 Contenido

1. [¿Qué es la comunicación inter-microservicios?](#qué-es)
2. [Opciones disponibles en Spring Boot 3.2](#opciones)
3. [HTTP Interfaces: La opción elegida](#opción-elegida)
4. [Arquitectura de nuestro sistema](#arquitectura)
5. [Flujo de validación de usuario](#flujo)
6. [Manejo de errores](#errores)
7. [Testing y mocking](#testing)
8. [Escalabilidad y tolerancia a fallos](#escalabilidad)

---

## 🎯 ¿Qué es la comunicación inter-microservicios?

En una arquitectura de microservicios, cada servicio es independiente pero a menudo necesita comunicarse con otros servicios. En nuestro caso:

- **Order Service** necesita validar que el usuario existe antes de crear una orden
- Para hacerlo, llama a **User Service** a través de HTTP/REST
- La comunicación es **síncrona**: espera la respuesta antes de continuar

```
Order Service                User Service
     │                            │
     ├─ POST /orders ────────────→ GET /users/{id}
     │                            │
     │ ← Validación exitosa ──────┤
     │
     └─ Crear orden
```

---

## 🔄 Opciones disponibles en Spring Boot 3.2

### 1. RestTemplate ❌ DEPRECATED

```java
RestTemplate restTemplate = new RestTemplate();
UserResponse user = restTemplate.getForObject(
    "http://user-service:8081/users/{id}",
    UserResponse.class,
    userId
);
```

**Desventajas:**
- Imperativo (requiere mucho código)
- Síncrono y bloqueante
- Marcado como **deprecated** en Spring Boot 3
- Bajo rendimiento en aplicaciones de alto tráfico
- Difícil de testear

---

### 2. OpenFeign ⚠️ TRADICIONAL

```java
@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable String id);
}
```

**Ventajas:**
- Declarativo (menos código que RestTemplate)
- Fácil de usar
- Ampliamente adoptado

**Desventajas:**
- Requiere dependencia extra: `spring-cloud-starter-openfeign`
- Acoplamiento a Spring Cloud
- WebClient no se usa internamente (menos moderno)
- Overhead adicional

---

### 3. WebClient ✅ REACTIVO

```java
WebClient webClient = WebClient.create("http://user-service:8081");

userService.get()
    .uri("/users/{id}", userId)
    .retrieve()
    .bodyToMono(UserResponse.class)
    .block(); // ⚠️ Bloquear para operación síncrona
```

**Ventajas:**
- Reactivo (async, non-blocking)
- Excelente rendimiento
- Muy flexible

**Desventajas:**
- Curva de aprendizaje pronunciada
- Código más complejo
- Requiere entender Reactive Streams

---

### 4. HTTP Interfaces ✅✅✅ RECOMENDADO (Elegido)

```java
public interface UserServiceClient {
    @GetExchange("/users/{userId}")
    UserResponse getUserById(@PathVariable String userId);
}
```

**Ventajas:**
- ✅ Lo más moderno (Spring 6 / Spring Boot 3.1+)
- ✅ Declarativo (sin boilerplate)
- ✅ Usa WebClient internamente (reactivo, excelente rendimiento)
- ✅ Sin dependencias extra
- ✅ Código limpio y expresivo
- ✅ Spring la recomienda oficialmente
- ✅ Fácil de testear (es solo una interface)

**Por qué elegimos HTTP Interfaces:**
Es la dirección oficial de Spring Framework para nuevas aplicaciones. Combina la simplicidad de Feign con la potencia del WebClient.

---

## 🏗️ Arquitectura de nuestro sistema

### Diagrama de comunicación

```
┌─────────────────────────────────────────────────────────────┐
│                    Order Service (8082)                     │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  OrderController (REST Adapter)                     │   │
│  │  POST /orders                                       │   │
│  └─────────────────┬──────────────────────────────────┘   │
│                    │                                        │
│  ┌─────────────────▼──────────────────────────────────┐   │
│  │  OrderService (Application Layer)                  │   │
│  │  - Valida usuario via UserServiceClient            │   │
│  │  - Crea orden si usuario existe                    │   │
│  └─────────────────┬──────────────────────────────────┘   │
│                    │                                        │
│  ┌─────────────────▼──────────────────────────────────┐   │
│  │  UserServiceClient (HTTP Interface)                │   │
│  │  @GetExchange("/users/{userId}")                   │   │
│  │  getUserById(String userId)                        │   │
│  └─────────────────┬──────────────────────────────────┘   │
│                    │                                        │
│  ┌─────────────────▼──────────────────────────────────┐   │
│  │  WebClient (Spring)                                │   │
│  │  GET http://user-service:8081/users/{userId}      │   │
│  └─────────────────┬──────────────────────────────────┘   │
└────────────────────┼─────────────────────────────────────────┘
                     │ HTTP Request
                     │ ────────────→
                     │
┌────────────────────▼─────────────────────────────────────────┐
│                   User Service (8081)                        │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  UserController                                    │   │
│  │  GET /users/{id}                                   │   │
│  └─────────────────┬──────────────────────────────────┘   │
│                    │                                        │
│  ┌─────────────────▼──────────────────────────────────┐   │
│  │  UserService                                       │   │
│  │  Obtiene usuario de BD                             │   │
│  └─────────────────┬──────────────────────────────────┘   │
│                    │                                        │
│  ┌─────────────────▼──────────────────────────────────┐   │
│  │  Response: { id, email, name }                     │   │
│  └─────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────┘
                     │ HTTP Response
                     │ ←────────────
                     │
         ┌───────────▼──────────────┐
         │  Validación exitosa      │
         │  Proceder con creación   │
         │  de la orden             │
         └──────────────────────────┘
```

---

## 🔄 Flujo de validación de usuario

### Caso de éxito (Usuario existe)

```
1. Cliente: POST /orders
   {
     "userId": "550e8400-e29b-41d4-a716-446655440000",
     "totalAmount": 99.99
   }

2. OrderController → OrderService.create()

3. OrderService llama a UserServiceClient.getUserById(userId)
   HTTP GET http://user-service:8081/users/550e8400-...

4. User Service responde:
   {
     "id": "550e8400-e29b-41d4-a716-446655440000",
     "email": "john@example.com",
     "name": "John Doe"
   }

5. OrderService valida respuesta:
   ✅ Usuario existe: "John Doe (john@example.com)"

6. OrderService crea la orden
   Orden guardada en repositorio

7. Respuesta al cliente:
   201 Created
   {
     "id": "f47ac10b-...",
     "userId": "550e8400-...",
     "totalAmount": 99.99,
     "status": "CREATED",
     "createdAt": "2026-01-20T...",
     "updatedAt": "2026-01-20T..."
   }
```

### Caso de error (Usuario no existe)

```
1. Cliente: POST /orders
   {
     "userId": "invalid-user-id",
     "totalAmount": 99.99
   }

2. OrderService llama a UserServiceClient.getUserById(userId)
   HTTP GET http://user-service:8081/users/invalid-user-id

3. User Service responde:
   404 Not Found
   {
     "timestamp": "2026-01-20T...",
     "status": 404,
     "error": "Not Found",
     "message": "Usuario no encontrado: invalid-user-id"
   }

4. OrderService captura HttpClientErrorException.NotFound

5. OrderService lanza UserNotFoundException
   "Usuario no encontrado en el sistema: invalid-user-id"

6. GlobalExceptionHandler captura la excepción

7. Respuesta al cliente:
   422 Unprocessable Entity
   {
     "timestamp": "2026-01-20T...",
     "status": 422,
     "error": "User Not Found",
     "message": "Usuario no encontrado en el sistema: invalid-user-id"
   }
```

### Caso de error (User Service no disponible)

```
1. OrderService intenta llamar a UserServiceClient

2. User Service no responde (timeout, error de conexión, etc.)

3. OrderService captura Exception genérica

4. OrderService lanza RuntimeException
   "Error comunicándose con user-service. Intenta más tarde."

5. GlobalExceptionHandler captura la excepción

6. Respuesta al cliente:
   500 Internal Server Error
   {
     "timestamp": "2026-01-20T...",
     "status": 500,
     "error": "Internal Server Error",
     "message": "Error comunicándose con user-service. Intenta más tarde."
   }
```

---

## ⚠️ Manejo de errores

### Niveles de error

1. **User Service retorna 404**
   - Significa: El usuario no existe
   - Respuesta: **422 Unprocessable Entity**
   - Motivo: La entidad (usuario) no existe, no se puede procesar

2. **User Service retorna 5xx**
   - Significa: Error interno en user-service
   - Respuesta: **500 Internal Server Error**
   - Motivo: No es culpa del cliente, es un error del servidor

3. **Timeout/Connection refused**
   - Significa: User Service no está disponible
   - Respuesta: **500 Internal Server Error**
   - Motivo: Problema temporal, recomendar reintentar

### Código de OrderService

```java
try {
    var user = userServiceClient.getUserById(request.userId());
    log.info("✅ Usuario validado: {} ({})", user.name(), user.email());
} catch (HttpClientErrorException.NotFound ex) {
    log.warn("⚠️ Usuario no encontrado en user-service: {}", request.userId());
    throw new UserNotFoundException(request.userId());
} catch (Exception ex) {
    log.error("❌ Error al validar usuario en user-service", ex);
    throw new RuntimeException(
            "Error comunicándose con user-service. Intenta más tarde.",
            ex
    );
}
```

---

## 🧪 Testing y mocking

### Mockear UserServiceClient en tests

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private UserServiceClient userServiceClient;
    
    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    void testCreateOrderWithValidUser() {
        // Arrange
        String userId = "550e8400-...";
        CreateOrderRequest request = new CreateOrderRequest(userId, BigDecimal.valueOf(99.99));
        
        UserResponse userResponse = new UserResponse(userId, "john@example.com", "John Doe");
        when(userServiceClient.getUserById(userId)).thenReturn(userResponse);
        
        Order savedOrder = Order.create(UUID.fromString(userId), BigDecimal.valueOf(99.99));
        when(orderRepository.save(any())).thenReturn(savedOrder);
        
        // Act
        OrderResponse response = orderService.create(request);
        
        // Assert
        assertThat(response).isNotNull();
        verify(userServiceClient).getUserById(userId);
        verify(orderRepository).save(any());
    }
    
    @Test
    void testCreateOrderWithInvalidUser() {
        // Arrange
        String userId = "invalid-id";
        CreateOrderRequest request = new CreateOrderRequest(userId, BigDecimal.valueOf(99.99));
        
        when(userServiceClient.getUserById(userId))
            .thenThrow(new HttpClientErrorException.NotFound("Not Found"));
        
        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            orderService.create(request);
        });
    }
}
```

---

## 📈 Escalabilidad y tolerancia a fallos

### Configuración actual

En `application.yml`:
```yaml
user-service:
  url: http://localhost:8081  # Dev
  url: http://user-service:8081  # Prod (Docker/Kubernetes)
```

### Mejoras futuras

1. **Circuit Breaker (Resilience4j)**
   ```java
   @CircuitBreaker(name = "userServiceClient", fallbackMethod = "fallback")
   public UserResponse getUserById(String userId) { ... }
   ```

2. **Retry automático**
   ```java
   @Retry(name = "userServiceClient", maxAttempts = 3)
   public UserResponse getUserById(String userId) { ... }
   ```

3. **Timeout**
   ```java
   @Timeout(duration = "3s")
   public UserResponse getUserById(String userId) { ... }
   ```

4. **Load Balancing con Eureka**
   Ya configurado en producción con `eureka.client.enabled: true`

5. **Logging distribuido (ELK stack)**
   - Rastrear requests entre servicios
   - Correlation ID en logs

6. **Service Mesh (Istio, Linkerd)**
   - Manejo automático de resiliencia
   - Observabilidad completa

---

## 📝 Configuración por perfil

### Desarrollo (dev)

```yaml
user-service:
  url: http://localhost:8081
```

Ambos servicios corren en localhost, puertos diferentes.

### Producción (prod)

```yaml
user-service:
  url: http://user-service:8081
```

En Docker/Kubernetes, usa DNS del servicio (nombre de servicio en lugar de IP).

### Variable de entorno

```bash
export USER_SERVICE_URL=http://user-service:8081
java -jar order-service.jar
```

---

## 🎓 Conclusión

HTTP Interfaces es la opción moderna y recomendada para comunicación inter-microservicios en Spring Boot 3.2+. Ofrece:

✅ Código limpio y declarativo  
✅ Rendimiento excelente (usa WebClient)  
✅ Sin dependencias extra  
✅ Fácil testing  
✅ La dirección oficial de Spring  

Es la arquitectura perfecta para microservicios escalables y resilientes.
