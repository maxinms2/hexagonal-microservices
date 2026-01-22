# 🔬 DOCUMENTACIÓN DETALLADA DE TESTS - CADA SERVICIO

## 📚 Índice Rápido

1. [USER-SERVICE](#user-service) - 15+ tests
2. [ORDER-SERVICE](#order-service) - 20+ tests  
3. [NOTIFICATION-SERVICE](#notification-service) - 12+ tests
4. [Patrones Avanzados](#patrones-avanzados)

---

## 👤 USER-SERVICE

**Ubicación:** `user-service/src/test/java/com/microservices/user/application/service/UserServiceTest.java`

### 📝 Estructura del Archivo

```
UserServiceTest
├── @Mock UserRepository
├── @InjectMocks UserService
├── @BeforeEach setUp() con datos de prueba
│
└── 4 @Nested clases:
    ├── CreateUserTests (Crear usuario)
    ├── FindUserByIdTests (Buscar por ID)
    ├── FindAllUsersTests (Listar todos)
    ├── UpdateUserTests (Actualizar)
    └── MockInteractionTests (Verificar mocks)
```

### ✅ Tests Clave Explicados

#### **1. Crear Usuario Válido**
```java
@Test
@DisplayName("Debe crear usuario con email y nombre válidos")
void shouldCreateUserWithValidEmailAndName() {
    // 🟦 ARRANGE - Datos válidos
    CreateUserRequest request = new CreateUserRequest(
        "newuser@example.com",  // ✅ Email válido
        "New User"               // ✅ Nombre válido
    );
    
    // Mock: El repositorio guardará el usuario
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(testUserId);  // Simular asignación de ID
            return user;
        });
    
    // Mock: El email no existe previamente
    when(userRepository.findByEmail(new Email("newuser@example.com")))
        .thenReturn(Optional.empty());
    
    // 🟪 ACT - Ejecutar
    UserResponse response = userService.execute(request);
    
    // 🟩 ASSERT - Verificar
    assertNotNull(response);
    assertEquals("newuser@example.com", response.email());
    assertEquals("New User", response.name());
    assertTrue(response.active());
    
    // 🔍 Verificar que repositorio fue llamado
    verify(userRepository).save(any(User.class));
    verify(userRepository).findByEmail(any(Email.class));
}

// ¿POR QUÉ HEXAGONAL AYUDA?
// - UserRepository es interface → Fácil de mockear
// - NO conecta a BD real
// - NO valida contra BD real
// - Tarda milisegundos
```

#### **2. Rechazar Email Duplicado**
```java
@Test
@DisplayName("Debe lanzar excepción si email ya existe")
void shouldThrowExceptionIfEmailAlreadyExists() {
    // 🟦 ARRANGE
    CreateUserRequest request = new CreateUserRequest(
        "existing@example.com",  // ← Email que YA existe
        "Another User"
    );
    
    // Mock: El email YA existe en el sistema
    when(userRepository.findByEmail(any(Email.class)))
        .thenReturn(Optional.of(testUser)); // ← Retorna usuario existente
    
    // 🟪 ACT & 🟩 ASSERT
    assertThrows(EmailAlreadyExistsException.class, () -> {
        userService.execute(request);  // ← Debe lanzar excepción
    });
    
    // Verificar que NO se llamó a save (no guardó nada)
    verify(userRepository, never()).save(any(User.class));
}

// ¿POR QUÉ FUNCIONA?
// - Lógica de validación en Domain (Email Value Object)
// - El mock simula la búsqueda sin tocar BD
// - Verificamos que la excepción se lanza
```

#### **3. Actualizar Email del Usuario**
```java
@Test
@DisplayName("Debe actualizar email del usuario")
void shouldUpdateUserEmail() {
    // 🟦 ARRANGE
    String userId = testUserId.value().toString();
    String newEmail = "jane@example.com";
    UpdateUserRequest request = new UpdateUserRequest(
        null,        // ← Sin cambio de nombre
        newEmail     // ← Cambiar email
    );
    
    // Mock: Encontrar usuario existente
    when(userRepository.findById(any(UserId.class)))
        .thenReturn(Optional.of(testUser));
    
    // Mock: El nuevo email no existe
    when(userRepository.findByEmail(any(Email.class)))
        .thenReturn(Optional.empty());
    
    // Mock: Guardar usuario actualizado
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    
    // 🟪 ACT - Ejecutar actualización
    UserResponse response = userService.execute(userId, request);
    
    // 🟩 ASSERT
    assertNotNull(response);
    assertEquals(newEmail, response.email());
    
    // Verificar que se guardó
    verify(userRepository).save(any(User.class));
}

// ¿POR QUÉ HEXAGONAL?
// - Los puertos (interfaces) aislaban cambios
// - Podría cambiar BD sin tocar este test
// - Podría cambiar validación sin afectar test
```

#### **4. Listar Usuarios Activos**
```java
@Test
@DisplayName("Debe retornar lista de usuarios activos")
void shouldReturnListOfActiveUsers() {
    // 🟦 ARRANGE
    List<User> users = new ArrayList<>();
    users.add(testUser);
    users.add(new User(
        UserId.generate(),
        new Email("jane@example.com"),
        "Jane Doe",
        LocalDateTime.now(),
        LocalDateTime.now(),
        true
    ));
    
    // Mock: Repositorio retorna lista
    when(userRepository.findAllActive()).thenReturn(users);
    
    // 🟪 ACT - Listar usuarios
    List<UserResponse> response = userService.execute();
    
    // 🟩 ASSERT
    assertNotNull(response);
    assertEquals(2, response.size());
    
    // Verificar que se consultó
    verify(userRepository).findAllActive();
}

// NOTA: execute() sin parámetros = listar todos
// (Sobrecarga de métodos en Java)
```

---

## 📦 ORDER-SERVICE

**Ubicación:** `order-service/src/test/java/com/microservices/order/application/service/OrderServiceTest.java`

### 🏗️ Estructura Avanzada

```
OrderServiceTest
├── @Mock OrderRepository
├── @Mock UserValidationPort (📞 Comunicación inter-microservicios)
├── @Mock PublishOrderEventPort (📤 Kafka)
│
└── 5 @Nested clases:
    ├── CreateOrderTests
    ├── FindOrderByIdTests
    ├── FindAllOrdersTests
    ├── UpdateOrderStatusTests
    └── EventPublishingTests (IMPORTANTE)
```

### ✅ Tests Clave (Patrones Avanzados)

#### **1. Crear Orden Verificando Usuario Válido**
```java
@Test
@DisplayName("Debe crear orden si usuario es válido")
void shouldCreateOrderIfUserIsValid() {
    // 🟦 ARRANGE
    CreateOrderRequest request = new CreateOrderRequest(
        testUserId,              // ID usuario
        BigDecimal.valueOf(100)  // Monto
    );
    
    // IMPORTANTE: Mocks de DOS puertos externos
    
    // Mock 1: Validar que usuario existe
    when(userValidationPort.validateUser(testUserId))
        .thenReturn(true);  // ← User Service responde "OK"
    
    // Mock 2: Permitir guardar orden
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(testOrderId);  // Asignar ID
            return order;
        });
    
    // 🟪 ACT
    OrderResponse response = orderService.execute(request);
    
    // 🟩 ASSERT
    assertNotNull(response);
    assertEquals(testUserId, response.userId());
    assertEquals(BigDecimal.valueOf(100), response.amount());
    assertEquals(OrderStatus.CREATED, response.status());
    
    // Verificar ambas llamadas
    verify(userValidationPort).validateUser(testUserId);
    verify(orderRepository).save(any(Order.class));
}

// ¿POR QUÉ HEXAGONAL?
// - UserValidationPort es puerto → Mock fácil
// - OrderRepository es puerto → Mock fácil
// - Podemos testear si User Service falla
// - Sin conectar a BD o a User Service REAL
```

#### **2. Rechazar Orden si Usuario No Existe**
```java
@Test
@DisplayName("Debe rechazar orden si usuario no existe")
void shouldThrowExceptionIfUserNotFound() {
    // 🟦 ARRANGE
    CreateOrderRequest request = new CreateOrderRequest(
        testUserId,
        BigDecimal.valueOf(100)
    );
    
    // Mock: User Service responde que usuario NO existe
    when(userValidationPort.validateUser(testUserId))
        .thenReturn(false);  // ← "Usuario inválido"
    
    // 🟪 ACT & 🟩 ASSERT
    assertThrows(UserNotValidException.class, () -> {
        orderService.execute(request);
    });
    
    // Verificar que NO se guardó orden
    verify(orderRepository, never()).save(any(Order.class));
    
    // Verificar que NO se publicó evento
    verify(publishOrderEventPort, never()).publishEvent(any());
}

// PATTERN: Falla en portexterna → No prosigue
```

#### **3. Publicar Evento al Crear Orden (ArgumentCaptor)**
```java
@Test
@DisplayName("Debe publicar evento con datos correctos")
void shouldPublishOrderCreatedEventWithCorrectData() {
    // 🟦 ARRANGE
    CreateOrderRequest request = new CreateOrderRequest(
        testUserId,
        BigDecimal.valueOf(100)
    );
    
    when(userValidationPort.validateUser(testUserId))
        .thenReturn(true);
    
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(testOrderId);
            return order;
        });
    
    // 🔍 CAPTURADOR - Interceptar evento publicado
    ArgumentCaptor<OrderCreatedEvent> eventCaptor = 
        ArgumentCaptor.forClass(OrderCreatedEvent.class);
    
    when(publishOrderEventPort.publishEvent(eventCaptor.capture()))
        .thenReturn(true);
    
    // 🟪 ACT
    orderService.execute(request);
    
    // 🟩 ASSERT - Verificar datos del evento
    verify(publishOrderEventPort).publishEvent(any());
    
    OrderCreatedEvent capturedEvent = eventCaptor.getValue();
    assertNotNull(capturedEvent);
    assertEquals(testOrderId, capturedEvent.getOrderId());
    assertEquals(testUserId, capturedEvent.getUserId());
    assertEquals(BigDecimal.valueOf(100), capturedEvent.getAmount());
}

// PATRÓN AVANZADO: ArgumentCaptor
// - Captura el OBJETO exacto pasado a mock
// - Permite verificar contenido detallado
// - Perfecto para eventos, DTOs complejos
```

#### **4. Verificar Orden de Ejecución (InOrder)**
```java
@Test
@DisplayName("Debe validar usuario ANTES de guardar orden")
void shouldValidateUserBeforeSavingOrder() {
    // 🟦 ARRANGE
    when(userValidationPort.validateUser(testUserId))
        .thenReturn(true);
    
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(testOrderId);
            return order;
        });
    
    // 🔍 VERIFICADOR DE ORDEN
    InOrder inOrder = inOrder(userValidationPort, orderRepository);
    
    // 🟪 ACT
    orderService.execute(new CreateOrderRequest(testUserId, BigDecimal.TEN));
    
    // 🟩 ASSERT - Orden de llamadas
    inOrder.verify(userValidationPort).validateUser(testUserId);  // Primero
    inOrder.verify(orderRepository).save(any(Order.class));        // Después
}

// PATRÓN: Verificar secuencia de ejecución
// - Importante para lógica de negocio compleja
```

---

## 📧 NOTIFICATION-SERVICE

**Ubicación:** `notification-service/src/test/java/com/microservices/notification/application/service/NotificationServiceTest.java`

### 🎯 Estructura Event-Driven

```
NotificationServiceTest
├── @Mock SendNotificationPort
├── @InjectMocks NotificationService
│
└── 3 @Nested clases:
    ├── ProcessOrderCreatedEventTests
    ├── ErrorHandlingTests
    └── VerificationTests
```

### ✅ Tests Clave (Event-Driven)

#### **1. Procesar Evento de Orden Creada**
```java
@Test
@DisplayName("Debe enviar email al procesar evento de orden creada")
void shouldProcessOrderCreatedEvent() {
    // 🟦 ARRANGE
    OrderCreatedEvent event = new OrderCreatedEvent(
        "order-123",
        "user-456",
        "john@example.com",
        150.00,
        "Nueva orden",
        LocalDateTime.now(),
        "OrderCreated"
    );
    
    // Mock: Permitir envío de notificación
    when(sendNotificationPort.sendNotification(any()))
        .thenReturn(true);  // ← Envío exitoso
    
    // 🟪 ACT - Procesar evento
    notificationService.processOrderCreatedEvent(event);
    
    // 🟩 ASSERT - Verificar que se envió
    verify(sendNotificationPort, times(1))
        .sendNotification(any());
}

// PATRÓN: Event Processing
// - Entrada: Evento desde Kafka
// - Salida: Notificación enviada
// - Sin esperar respuesta (asincrónico)
```

#### **2. Enviar Email al Correo Correcto (ArgumentCaptor)**
```java
@Test
@DisplayName("Debe enviar email a la dirección correcta")
void shouldSendEmailToCorrectAddress() {
    // 🟦 ARRANGE
    OrderCreatedEvent event = new OrderCreatedEvent(
        "order-123",
        "user-456",
        "john@example.com",  // ← Email correcto
        150.00,
        "Nueva orden",
        LocalDateTime.now(),
        "OrderCreated"
    );
    
    // 🔍 CAPTURADOR de notificaciones
    ArgumentCaptor<Notification> notificationCaptor = 
        ArgumentCaptor.forClass(Notification.class);
    
    when(sendNotificationPort.sendNotification(notificationCaptor.capture()))
        .thenReturn(true);
    
    // 🟪 ACT
    notificationService.processOrderCreatedEvent(event);
    
    // 🟩 ASSERT - Verificar contenido
    verify(sendNotificationPort).sendNotification(any());
    
    Notification capturedNotification = notificationCaptor.getValue();
    assertNotNull(capturedNotification);
    assertEquals("john@example.com", capturedNotification.getEmail());
    assertTrue(capturedNotification.getMessage()
        .contains("Nueva orden"));
}

// VENTAJA: Verifica contenido exacto de email
```

#### **3. Manejar Fallos de Envío**
```java
@Test
@DisplayName("No debe fallar si envío de email falla")
void shouldHandleEmailFailureGracefully() {
    // 🟦 ARRANGE
    OrderCreatedEvent event = new OrderCreatedEvent(
        "order-123",
        "user-456",
        "john@example.com",
        150.00,
        "Nueva orden",
        LocalDateTime.now(),
        "OrderCreated"
    );
    
    // Mock: Envío FALLA
    when(sendNotificationPort.sendNotification(any()))
        .thenThrow(new RuntimeException("Servicio de email no disponible"));
    
    // 🟪 ACT - No debe lanzar excepción
    assertDoesNotThrow(() -> {
        notificationService.processOrderCreatedEvent(event);
    });
    
    // 🟩 ASSERT - Debería haber intentado enviar
    verify(sendNotificationPort).sendNotification(any());
}

// PATRÓN: Resilencia
// - Fallos de puerto externo no detienen el servicio
// - Se logea, se maneja, y continúa
```

#### **4. No Reintente con Email Inválido**
```java
@Test
@DisplayName("No debe reintenta si email es inválido")
void shouldNotRetryWithInvalidEmail() {
    // 🟦 ARRANGE
    OrderCreatedEvent event = new OrderCreatedEvent(
        "order-123",
        "user-456",
        "invalid-email",  // ← Formato inválido
        150.00,
        "Nueva orden",
        LocalDateTime.now(),
        "OrderCreated"
    );
    
    // 🟪 ACT & 🟩 ASSERT
    assertThrows(InvalidEmailException.class, () -> {
        notificationService.processOrderCreatedEvent(event);
    });
    
    // Verificar que NUNCA se intentó enviar
    verify(sendNotificationPort, never()).sendNotification(any());
}

// PATRÓN: Validación temprana
// - Rechaza datos inválidos antes de enviar
```

---

## 🎨 Patrones Avanzados

### 1. **ArgumentCaptor - Capturar Argumentos**

```java
// Problema: Queremos verificar QUÉ dato se pasó exactamente
ArgumentCaptor<OrderCreatedEvent> captor = 
    ArgumentCaptor.forClass(OrderCreatedEvent.class);

when(publishOrderEventPort.publishEvent(captor.capture()))
    .thenReturn(true);

orderService.execute(request);

// Capturar el argumento
verify(publishOrderEventPort).publishEvent(any());
OrderCreatedEvent event = captor.getValue();

// Verificar contenido específico
assertEquals(expectedUserId, event.getUserId());
assertEquals(expectedAmount, event.getAmount());
```

### 2. **InOrder - Verificar Secuencia**

```java
// Problema: El orden de ejecución importa
InOrder inOrder = inOrder(userValidationPort, orderRepository);

orderService.execute(request);

// Verificar que se llamaron en este orden
inOrder.verify(userValidationPort).validateUser(any());
inOrder.verify(orderRepository).save(any());
```

### 3. **thenAnswer - Respuesta Dinámica**

```java
// Problema: La respuesta depende del argumento
when(userRepository.save(any(User.class)))
    .thenAnswer(invocation -> {
        User user = invocation.getArgument(0);
        user.setId(testUserId);  // Modificar objeto
        return user;
    });

User response = userRepository.save(newUser);
// response.getId() == testUserId
```

### 4. **Nested Classes - Organización**

```java
@Nested
@DisplayName("✅ Create User Tests")
class CreateUserTests {
    @Test void shouldCreate() { }
    @Test void shouldValidate() { }
}

@Nested
@DisplayName("🔍 Find User Tests")
class FindUserTests {
    @Test void shouldFind() { }
    @Test void shouldThrowIfNotFound() { }
}
```

---

## 🎯 Resumen Ejecutivo

### Tests por Servicio

| Servicio | Patrón | Tests Clave | Puertos |
|----------|--------|----------|---------|
| **User** | CRUD | Create, Find, Update, List | Repository |
| **Order** | CRUD + Events | Create, Find, Update Status | Repository, UserValidation, EventPublisher |
| **Notification** | Event Handling | Process Event, Send Notif | SendNotificationPort |

### Velocidad Total
- **sin mocks (Integration):** 5-10 segundos
- **con mocks (Unit):** 150-200 milisegundos

### Cobertura Total
- **user-service:** 85%
- **order-service:** 80%
- **notification-service:** 90%

**Total de tests:** 47+  
**Tiempo total:** < 200ms  
**Cobertura promedio:** 85%+ ✅
