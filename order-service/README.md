# 🛒 Order Service

Microservicio de gestión de órdenes con Arquitectura Hexagonal.

## 🏗️ Arquitectura

Este servicio implementa **Arquitectura Hexagonal (Ports & Adapters)** con Spring Boot.

### Estructura de Capas

```
order-service/
├── domain/                    # 💎 CORE - Lógica de Negocio
│   ├── model/                 # Entidades y Value Objects
│   │   ├── Order.java         # Entidad principal
│   │   ├── OrderId.java       # Value Object
│   │   └── OrderStatus.java   # Enum de estados
│   ├── exception/             # Excepciones de dominio
│   └── repository/            # Puertos de salida (interfaces)
│
├── application/               # 🎯 CASOS DE USO
│   ├── dto/                   # Request/Response
│   ├── usecase/               # Puertos de entrada (interfaces)
│   └── service/               # Implementación de casos de uso
│
└── infrastructure/            # 🔧 ADAPTADORES
    ├── adapter/
    │   ├── input/             # Adaptadores de entrada
    │   │   └── rest/          # REST Controllers
    │   ├── output/            # Adaptadores de salida
    │   │   └── persistence/   # Repositorio en memoria
    │   └── application/       # Adaptadores de casos de uso
    └── config/                # Configuraciones
```

## 🚀 Características

- ✅ **Arquitectura Hexagonal**: Dominio independiente de frameworks
- ✅ **Spring Boot 3.2**: Framework moderno
- ✅ **Java 17**: Records, Pattern Matching
- ✅ **H2/PostgreSQL**: Base de datos relacional
- ✅ **API REST**: Endpoints documentados
- ✅ **Validación**: Bean Validation
- ✅ **Manejo de errores**: Global Exception Handler
- ✅ **Logging**: SLF4J + Logback
- ✅ **Value Objects**: Type Safety
- ✅ **Repositorio en memoria**: Para desarrollo rápido

## 📦 Dependencias

- Spring Boot Web
- Spring Data JPA
- Spring Boot Validation
- Spring Boot Actuator
- PostgreSQL Driver
- H2 Database (para desarrollo)
- Spring Cloud Eureka Client

## 📊 Modelo de Dominio

### Order (Entidad)
```java
Order {
  OrderId id;
  UUID userId;
  BigDecimal totalAmount;
  OrderStatus status;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
```

### OrderStatus (Estados)
- **CREATED**: Orden creada (estado inicial)
- **PAID**: Orden pagada
- **CANCELLED**: Orden cancelada

### Reglas de Negocio
- Una orden siempre se crea en estado `CREATED`
- El total debe ser siempre mayor que cero
- Una orden cancelada no puede cambiar de estado
- Solo órdenes en estado `CREATED` pueden ser pagadas

## ⚙️ Configuración

### Perfiles

#### Desarrollo (dev)
```yaml
spring:
  profiles:
    active: dev
```
- Repositorio en memoria (datos volátiles)
- SQL logging habilitado
- H2 Console: http://localhost:8082/h2-console

#### Producción (prod)
```yaml
spring:
  profiles:
    active: prod
```
- PostgreSQL
- SQL logging deshabilitado
- Eureka Client habilitado

### Variables de Entorno (Producción)

```bash
DB_URL=jdbc:postgresql://localhost:5432/orderdb
DB_USERNAME=postgres
DB_PASSWORD=secret
EUREKA_URL=http://localhost:8761/eureka/
```

## 🚀 Ejecutar

### Con Maven
```bash
# Desarrollo
mvn spring-boot:run

# Producción
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Con JAR
```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/order-service-1.0.0.jar
```

## 📡 API Endpoints

### Base URL
```
http://localhost:8082/orders
```

### Crear Orden
```bash
POST /orders
Content-Type: application/json

{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 99.99
}

Response: 201 Created
```

### Obtener Todas las Órdenes
```bash
GET /orders

Response: 200 OK
```

### Obtener Orden por ID
```bash
GET /orders/{orderId}

Response: 200 OK
```

### Actualizar Estado de Orden
```bash
PATCH /orders/{orderId}/status
Content-Type: application/json

{
  "status": "PAID"
}

Response: 200 OK
```

### Eliminar Orden
```bash
DELETE /orders/{orderId}

Response: 204 No Content
```

## 🧪 Probar con cURL

```bash
# Crear orden
curl -X POST http://localhost:8082/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":"550e8400-e29b-41d4-a716-446655440000","totalAmount":150.50}'

# Listar órdenes
curl http://localhost:8082/orders

# Obtener orden
curl http://localhost:8082/orders/{orderId}

# Marcar como pagada
curl -X PATCH http://localhost:8082/orders/{orderId}/status \
  -H "Content-Type: application/json" \
  -d '{"status":"PAID"}'

# Cancelar orden
curl -X PATCH http://localhost:8082/orders/{orderId}/status \
  -H "Content-Type: application/json" \
  -d '{"status":"CANCELLED"}'

# Eliminar orden
curl -X DELETE http://localhost:8082/orders/{orderId}
```

## 📊 Actuator Endpoints

- **Health**: http://localhost:8082/actuator/health
- **Info**: http://localhost:8082/actuator/info
- **Metrics**: http://localhost:8082/actuator/metrics

## 🎯 Flujo de una Petición

```
Cliente HTTP
    ↓
OrderController (Input Adapter)
    ↓
CreateOrderUseCase (Input Port)
    ↓
OrderService (Application)
    ↓
OrderRepository (Output Port)
    ↓
InMemoryOrderRepository (Output Adapter - dev)
    ↓
ConcurrentHashMap
```

## 🧠 Conceptos Clave

### Value Objects
- **OrderId**: Identificador único tipado

### Domain Exceptions
- **OrderNotFoundException**: Orden no encontrada
- **InvalidOrderStateException**: Transición de estado inválida

### Repositorio en Memoria
En desarrollo, las órdenes se almacenan en memoria (ConcurrentHashMap). Los datos se pierden al reiniciar. En producción, se debe implementar un adaptador JPA con PostgreSQL.

## 🔄 Próximos Pasos

1. **Implementar persistencia JPA**: Crear entidades JPA y repositorio PostgreSQL
2. **Agregar tests unitarios**: Tests para casos de uso y lógica de dominio
3. **Agregar tests de integración**: Tests REST con MockMvc
4. ✅ **Integrar con User Service**: Validar que el userId existe (COMPLETADO)
5. **Implementar eventos**: Publicar eventos cuando cambie el estado
6. **Agregar paginación**: Para el endpoint GET /orders

## 🌐 Comunicación Inter-Microservicios

### HTTP Interfaces - Patrón Moderno (Spring 6+)

Este servicio implementa comunicación con **User Service** usando **HTTP Interfaces**, el patrón más moderno en Spring Boot 3.2+.

#### 📍 Flujo de Validación de Órdenes

```
1. Cliente hace POST /orders con userId
   ↓
2. OrderController recibe la petición
   ↓
3. OrderService.create() valida el userId
   ↓
4. UserServiceClient.getUserById(userId) → HTTP GET http://user-service:8081/users/{userId}
   ↓
5. Respuesta:
   ✅ Usuario existe → Se crea la orden
   ❌ Usuario NO existe → UserNotFoundException (HTTP 422)
```

#### 🔧 Configuración (application.yml)

```yaml
# Desarrollo
user-service:
  url: http://localhost:8081

# Producción (Docker/Kubernetes)
user-service:
  url: http://user-service:8081
```

#### 📋 Ejemplo de Uso

```bash
# ✅ Crear orden CON usuario válido
curl -X POST http://localhost:8082/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 99.99
  }'

# Respuesta exitosa:
{
  "id": "660e8400-e29b-41d4-a716-446655440111",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CREATED",
  "totalAmount": 99.99,
  "createdAt": "2024-01-20T17:45:00"
}

# ❌ Crear orden CON usuario INVÁLIDO
curl -X POST http://localhost:8082/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "invalid-user-id",
    "totalAmount": 99.99
  }'

# Respuesta error (HTTP 422):
{
  "timestamp": "2024-01-20T17:46:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Usuario no encontrado: invalid-user-id",
  "path": "/orders"
}
```

#### 🎯 Ventajas de HTTP Interfaces

| Característica | HTTP Interfaces | RestTemplate | Feign |
|---|---|---|---|
| **Boilerplate** | ✅ Mínimo | ❌ Mucho | ⚠️ Moderado |
| **Spring 3.2+** | ✅ Nativo | ⚠️ Legacy | ⚠️ Externo |
| **Type-safe** | ✅ Sí | ❌ Casting | ✅ Sí |
| **Async/Reactive** | ✅ WebClient | ⚠️ BlockingRestTemplate | ✅ WebClient |
| **Testing** | ✅ Fácil mock | ⚠️ Mock server | ✅ Mock |
| **Código** | 10 líneas | 50 líneas | 30 líneas |

#### 📝 Código de HTTP Interfaces

**UserServiceClient.java** (Interface con métodos declarativos)
```java
@HttpExchange(url = "")
public interface UserServiceClient {
    @GetExchange("/users/{id}")
    UserResponse getUserById(@PathVariable String id);
}
```

**HttpClientConfig.java** (Registra el cliente como bean)
```java
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
```

**OrderService.java** (Usa el cliente para validar)
```java
try {
    var user = userServiceClient.getUserById(request.userId());
    log.info("✅ Usuario validado: {} ({})", user.name(), user.email());
} catch (HttpClientErrorException.NotFound ex) {
    log.warn("⚠️ Usuario no encontrado en user-service: {}", request.userId());
    throw new UserNotFoundException(request.userId());
}
```

#### 🔗 Referencias

- [📖 Documentación completa: 06-comunicacion-inter-microservicios.md](../docs/06-comunicacion-inter-microservicios.md)
- [🌐 Spring HTTP Interfaces Docs](https://spring.io/blog/2023/04/13/new-in-spring-framework-6-1-http-interface-client)
- [🚀 WebClient Documentation](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)


- Microservicios
- Arquitectura Hexagonal
- Spring Boot Basics
- API Gateway
- Service Discovery

## 🆚 Diferencias con User Service

| Aspecto | User Service | Order Service |
|---------|--------------|---------------|
| Puerto | 8081 | 8082 |
| Entidad principal | User | Order |
| Value Objects | UserId, Email | OrderId |
| Estados | active/inactive | CREATED/PAID/CANCELLED |
| Persistencia (dev) | H2 con JPA | In-Memory |
| Soft delete | Sí | No (eliminación física) |
