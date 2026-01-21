# 🚪 API Gateway (Explicado con Peras y Manzanas)

## 🤔 ¿Qué es un API Gateway?

Imagina un hotel grande con muchos departamentos:
- Recepción
- Restaurante
- Gimnasio
- Spa
- Lavandería

### ❌ Sin API Gateway
Cada cliente debe conocer la ubicación exacta de cada servicio:
- Recepción: Piso 1, Puerta 101
- Restaurante: Piso 2, Puerta 205
- Gimnasio: Piso 3, Puerta 312

**Problemas:**
- El cliente debe recordar muchas direcciones
- Si un servicio se mueve, todos los clientes deben actualizar
- Difícil implementar seguridad en cada servicio
- No hay un punto de control central

### ✅ Con API Gateway
Todos van primero a **RECEPCIÓN** (API Gateway):
- Cliente: "Quiero ir al gimnasio"
- Recepción: "Te dirijo al piso 3, puerta 312"

**Ventajas:**
- Un solo punto de entrada
- La recepción conoce todos los servicios
- Puede verificar identificación (autenticación)
- Puede dirigir a la mejor opción (load balancing)
- Si un servicio se mueve, solo la recepción lo sabe

## 📐 Arquitectura

```
                    CLIENTE
                       │
                       ▼
              ┌────────────────┐
              │  API GATEWAY   │ ← Punto de entrada único
              │   (Puerto 80)  │
              └────────┬───────┘
                       │
      ┌────────────────┼────────────────┐
      │                │                │
      ▼                ▼                ▼
┌──────────┐    ┌──────────┐    ┌──────────┐
│   User   │    │  Order   │    │ Product  │
│ Service  │    │ Service  │    │ Service  │
│  :8081   │    │  :8082   │    │  :8083   │
└──────────┘    └──────────┘    └──────────┘
```

## 🎯 Funciones del API Gateway

### 1. **Routing (Enrutamiento)** 🗺️

Dirige las peticiones al servicio correcto:

```
/api/users/*    →  User Service (puerto 8081)
/api/orders/*   →  Order Service (puerto 8082)
/api/products/* →  Product Service (puerto 8083)
```

**Ejemplo:**
```bash
# Cliente hace la petición al Gateway
GET http://api.miapp.com/api/users/123

# Gateway la redirige a
GET http://user-service:8081/api/users/123
```

### 2. **Authentication (Autenticación)** 🔐

Verifica la identidad antes de permitir el acceso:

```
Cliente → Gateway → ¿Token válido? → Sí → User Service
                                   → No → 401 Unauthorized
```

**Sin Gateway:**
- Cada servicio debe verificar autenticación
- Código duplicado
- Difícil de mantener

**Con Gateway:**
- Autenticación centralizada
- Un solo lugar para actualizar
- Los servicios reciben peticiones ya autenticadas

### 3. **Load Balancing (Balanceo de Carga)** ⚖️

Distribuye peticiones entre múltiples instancias:

```
         Gateway
            │
    ┌───────┼───────┐
    ▼       ▼       ▼
 User-1  User-2  User-3
 (25%)   (25%)   (50%)
```

### 4. **Rate Limiting (Límite de Peticiones)** 🚦

Controla cuántas peticiones puede hacer un cliente:

```
Límite: 100 peticiones por minuto

Petición 1-100: ✅ Permitida
Petición 101:   ❌ 429 Too Many Requests
```

### 5. **Request/Response Transformation** 🔄

Modifica peticiones o respuestas:

```
# Cliente envía
{
  "user_name": "John"
}

# Gateway transforma a
{
  "userName": "John"
}

# Y lo envía al servicio
```

### 6. **Caching (Caché)** 💾

Guarda respuestas frecuentes para responder más rápido:

```
Primera petición:  Gateway → User Service → BD (500ms)
Segunda petición:  Gateway → Caché (5ms) ⚡
```

### 7. **Logging & Monitoring (Registro y Monitoreo)** 📊

Registra todas las peticiones en un lugar central:

```
[2026-01-19 10:30:15] GET /api/users/123 → 200 OK (150ms)
[2026-01-19 10:30:16] POST /api/orders → 201 Created (300ms)
[2026-01-19 10:30:17] GET /api/users/999 → 404 Not Found (50ms)
```

### 8. **Circuit Breaker (Interruptor de Circuito)** 🔌

Si un servicio falla, detiene las peticiones para no sobrecargarlo:

```
User Service falla 50% del tiempo
    ↓
Gateway detecta el problema
    ↓
Abre el circuito (detiene peticiones)
    ↓
Devuelve respuesta de fallback
    ↓
Después de un tiempo, intenta de nuevo
```

**Estados:**
1. **CLOSED (Cerrado)**: Todo funciona normal
2. **OPEN (Abierto)**: Hay problemas, no envía peticiones
3. **HALF-OPEN (Semi-abierto)**: Probando si ya se recuperó

## 🛠️ Tecnologías Comunes

### Spring Cloud Gateway
```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        // Ruta a User Service
        .route("user-service", r -> r
            .path("/api/users/**")
            .uri("lb://user-service"))
            
        // Ruta a Order Service
        .route("order-service", r -> r
            .path("/api/orders/**")
            .uri("lb://order-service"))
            
        .build();
}
```

### Kong
Open source, escrito en Lua, muy escalable.

### AWS API Gateway
Servicio de AWS completamente gestionado.

### NGINX
Servidor web que puede funcionar como API Gateway.

## 📝 Ejemplo Completo

### Sin API Gateway

```bash
# Cliente debe conocer todas las URLs
GET http://user-service.com:8081/api/users/123
GET http://order-service.com:8082/api/orders/456
GET http://product-service.com:8083/api/products/789

# Diferentes dominios, diferentes puertos
# Problemas de CORS
# Difícil de gestionar
```

### Con API Gateway

```bash
# Una sola URL
GET http://api.miapp.com/api/users/123
GET http://api.miapp.com/api/orders/456
GET http://api.miapp.com/api/products/789

# Gateway redirige internamente
# Sin problemas de CORS
# Fácil de gestionar
```

## 🔐 Autenticación con Gateway

### Flujo Típico

```
1. Cliente hace login
   POST /auth/login
   { "email": "user@example.com", "password": "***" }
   
2. Gateway autentica y devuelve token
   200 OK
   { "token": "eyJhbGc..." }
   
3. Cliente usa el token en peticiones futuras
   GET /api/users/123
   Authorization: Bearer eyJhbGc...
   
4. Gateway valida el token
   ✅ Token válido → Permite la petición
   ❌ Token inválido → 401 Unauthorized
   
5. Si válido, agrega información al header
   X-User-Id: 123
   X-User-Email: user@example.com
   
6. Servicio recibe petición con información del usuario
```

## ⚡ Patrones Avanzados

### 1. **API Composition**

Combina múltiples servicios en una respuesta:

```
Cliente: Dame el pedido con sus productos

Gateway:
  1. Llama a Order Service → Obtiene pedido
  2. Llama a Product Service → Obtiene detalles de productos
  3. Combina las respuestas
  4. Devuelve al cliente

Respuesta:
{
  "orderId": "123",
  "status": "pending",
  "items": [
    {
      "productId": "456",
      "productName": "Laptop",  ← Del Product Service
      "price": 999.99,
      "quantity": 1
    }
  ]
}
```

### 2. **Backend for Frontend (BFF)**

Gateways específicos para cada tipo de cliente:

```
┌─────────────┐
│ Web Client  │ → Web Gateway    → Servicios
└─────────────┘                      │
                                     │
┌─────────────┐                      │
│Mobile Client│ → Mobile Gateway → ─┘
└─────────────┘
```

**¿Por qué?**
- Web necesita más datos
- Mobile necesita menos datos (ahorro de batería)
- Diferentes formatos de respuesta

### 3. **GraphQL Gateway**

Un solo endpoint con queries flexibles:

```graphql
# Cliente define exactamente qué quiere
query {
  user(id: "123") {
    name
    email
    orders {
      id
      status
      total
    }
  }
}

# Gateway obtiene datos de múltiples servicios
# y devuelve solo lo solicitado
```

## 🚨 Desafíos

### 1. **Punto Único de Fallo**
Si el Gateway cae, TODA la aplicación cae.

**Solución:** 
- Múltiples instancias del Gateway
- Load Balancer delante del Gateway

### 2. **Latencia Adicional**
Cada petición pasa por el Gateway.

**Solución:**
- Gateway optimizado
- Caché
- No procesar en exceso

### 3. **Complejidad**
Un componente más que mantener.

**Solución:**
- Usar soluciones maduras (Spring Cloud Gateway, Kong)
- Documentar bien las configuraciones

## 📊 Comparación

| Aspecto | Sin Gateway | Con Gateway |
|---------|-------------|-------------|
| **URLs** | Muchas diferentes | Una sola |
| **Seguridad** | En cada servicio | Centralizada |
| **Monitoreo** | Disperso | Centralizado |
| **Cambios** | Afectan a clientes | Transparente |
| **Complejidad** | Baja inicial | Alta inicial |
| **Escalabilidad** | Complicada | Más sencilla |

## 🎯 ¿Cuándo usar API Gateway?

### ✅ Úsalo cuando:
- Tienes múltiples microservicios
- Necesitas autenticación centralizada
- Quieres un punto de entrada único
- Necesitas rate limiting
- Quieres ocultar la arquitectura interna

### ❌ No lo uses cuando:
- Tienes un solo servicio (monolito)
- La latencia es crítica
- Quieres simplicidad máxima
- No tienes recursos para mantenerlo

## 💻 Implementación Básica

### Spring Cloud Gateway

```java
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // User Service
            .route("users", r -> r
                .path("/api/users/**")
                .filters(f -> f
                    .addRequestHeader("X-Gateway", "API-Gateway")
                    .circuitBreaker(c -> c.setName("userCircuit")))
                .uri("lb://user-service"))
                
            // Order Service
            .route("orders", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .rewritePath("/api/orders/(?<segment>.*)", "/orders/${segment}")
                    .retry(3))
                .uri("lb://order-service"))
                
            .build();
    }
}
```

```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
            
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=1
```

## 📚 Siguiente Paso

Ahora que entiendes el API Gateway, aprende cómo los servicios se encuentran entre sí.

➡️ Continúa con: [Service Discovery](05-service-discovery.md)

---

## 💡 Recuerda

> El API Gateway es como la recepción de un hotel: un punto de contacto único que dirige a los clientes al servicio correcto, verifica su identidad y monitorea todo lo que sucede.
