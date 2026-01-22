# 🧪 Guía Completa de Testing en Hexagonal

## 📋 Estructura de Tests Creados

```
user-service/src/test/java/com/microservices/user/
├── domain/model/
│   └── UserTest.java                    # Tests de dominio (entidades puras)
└── application/service/
    └── UserServiceTest.java             # Tests de servicios (con mocks)

order-service/src/test/java/com/microservices/order/
├── domain/model/
│   └── OrderTest.java                   # Tests de dominio
└── application/service/
    └── OrderServiceTest.java            # Tests de servicios

notification-service/src/test/java/com/microservices/notification/
└── application/service/
    └── NotificationServiceTest.java     # Tests event-driven
```

---

## 🎯 Cómo Cada Test Aprovecha Hexagonal

### 1. **Domain Model Tests** (UserTest, OrderTest)

**Ubicación:** `domain/model/`

**Propósito:** Testear lógica de negocio PURA

```java
// ✅ SIN ninguna dependencia
@Test
void testUserCreation() {
    User user = User.create(Email.of("test@test.com"), "John");
    assertTrue(user.isActive());
}
```

**Ventajas Hexagonal:**
- ✅ Entidad User NO tiene anotaciones JPA (@Entity)
- ✅ No depende de frameworks (Spring, JPA, etc.)
- ✅ Tests ultra-rápidos (milisegundos)
- ✅ 100% testeable sin contexto Spring

**Ejecución:**
```bash
mvn test -Dtest=UserTest
```

---

### 2. **Application Service Tests** (UserServiceTest, OrderServiceTest)

**Ubicación:** `application/service/`

**Propósito:** Testear LÓGICA DE CASOS DE USO con MOCKS

```java
@Mock
private UserRepository userRepository;  // 🔌 PUERTO (interface)

@InjectMocks
private UserService userService;

@Test
void testCreateUser() {
    // Inyectar mock del repositorio
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(testUserId);
            return user;
        });
    
    UserResponse response = userService.execute(request);
    
    verify(userRepository).save(any(User.class));
}
```

**Ventajas Hexagonal:**
- ✅ UserRepository es INTERFACE (puerto) → fácil mockear
- ✅ NO necesita BD real (mock de JPA)
- ✅ Prueba lógica de aplicación aislada
- ✅ Tests rápidos (< 1 segundo)
- ✅ Podemos simular diferentes escenarios

**Patrones Usados:**
- `@Mock`: Crear mock de puerto
- `@InjectMocks`: Inyectar en servicio
- `when(...).thenReturn(...)`: Configurar comportamiento
- `verify(...)`: Verificar que fue llamado

**Ejecución:**
```bash
mvn test -Dtest=UserServiceTest
```

---

### 3. **Event-Driven Service Tests** (NotificationServiceTest)

**Ubicación:** `application/service/`

**Propósito:** Testear procesamiento de EVENTOS sin Kafka

```java
@Mock
private SendEmailPort sendEmailPort;  // 🔌 PUERTO de salida

@Test
void testProcessOrderCreatedEvent() {
    OrderCreatedEvent event = new OrderCreatedEvent(...);
    
    doNothing().when(sendEmailPort)
        .sendNotificationEmail(any(), any(), any());
    
    notificationService.processOrderCreatedEvent(event);
    
    verify(sendEmailPort).sendNotificationEmail(
        "john@example.com",
        any(),
        any()
    );
}
```

**Ventajas Hexagonal en Event-Driven:**
- ✅ SendEmailPort es INTERFACE → mockeable
- ✅ NO necesita Kafka en tests (mock)
- ✅ NO necesita servicio de email real
- ✅ Prueba lógica event-driven aislada
- ✅ Fácil simular fallos de servicios externos

---

## 🧰 Librerías Usadas

### 1. **JUnit 5**
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

- Framework principal de testing
- `@Test`: Marcar test
- `@BeforeEach`: Ejecutar antes de cada test
- `@Nested`: Agrupar tests relacionados
- `@DisplayName`: Descripción legible

### 2. **Mockito**
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

- Crear y verificar mocks
- `@Mock`: Crear mock
- `@InjectMocks`: Inyectar mocks
- `when()`: Configurar comportamiento
- `verify()`: Verificar llamadas
- `ArgumentCaptor`: Capturar argumentos

### 3. **AssertJ** (dentro de Spring Boot Starter Test)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

- Aserciones fluidas
- `assertNotNull()`, `assertEquals()`, `assertTrue()`, etc.

---

## 🚀 Ejecutar Tests

### Ejecutar todos los tests
```bash
mvn test
```

### Ejecutar tests de un microservicio
```bash
cd user-service
mvn test
```

### Ejecutar una clase específica
```bash
mvn test -Dtest=UserTest
mvn test -Dtest=UserServiceTest
```

### Ejecutar método específico
```bash
mvn test -Dtest=UserTest#testDeactivateUser
```

### Con cobertura de código
```bash
mvn test jacoco:report
# Ver reporte en: target/site/jacoco/index.html
```

---

## 📊 Pirámide de Tests Recomendada

```
                    ▲
                   /│\
                  / │ \
                 /  │  \  E2E Tests (2-3%)
                /   │   \ • Toda aplicación
               /    │    \• BD real, externos
              /──────────── 
             /     │      \
            /      │       \ Integration Tests (10-15%)
           /       │        \• Servicios + Adaptadores
          /        │         \• BD en memoria (H2)
         /         │          \
        /──────────────────────
       /          │           \
      /           │            \ Unit Tests (80-85%)
     /            │             \• Dominio + Servicios
    /             │              \• Mocks para todo
   /──────────────────────────────
```

**Implementación en este proyecto:**
- **Unit Tests** ✅: UserTest, UserServiceTest, OrderServiceTest, NotificationServiceTest
- **Integration Tests** ⏳: Por crear (con TestContainers + H2)
- **E2E Tests** ⏳: Por crear (con Spring Boot Test + BD real)

---

## 🧪 Patrones de Testing Usados

### 1. **Arrange-Act-Assert (AAA)**

Todos los tests siguen este patrón:

```java
@Test
void testExample() {
    // ARRANGE: Preparar datos y mocks
    String email = "test@example.com";
    when(repo.findByEmail(Email.of(email)))
        .thenReturn(Optional.of(user));
    
    // ACT: Ejecutar el código a testear
    User result = service.findUser(email);
    
    // ASSERT: Verificar resultados
    assertNotNull(result);
    assertEquals(email, result.getEmail().value());
    
    // VERIFY: Verificar interacciones con mocks
    verify(repo).findByEmail(Email.of(email));
}
```

### 2. **@Nested para Organizar Tests**

```java
@DisplayName("User Service Tests")
class UserServiceTest {
    
    @Nested
    @DisplayName("✅ Create User")
    class CreateUserTests {
        // Tests de creación agrupados
    }
    
    @Nested
    @DisplayName("🔍 Find User")
    class FindUserTests {
        // Tests de búsqueda agrupados
    }
}
```

**Beneficios:**
- Mejor organización
- Más legible
- Fácil de encontrar tests específicos

### 3. **Mock Verification**

```java
// Verificar que fue llamado
verify(repository).save(any(User.class));

// Verificar número de llamadas
verify(repository, times(2)).save(any(User.class));

// Verificar con argumentos específicos
verify(repository).save(argThat(user ->
    user.getEmail().value().equals("test@test.com")
));

// Verificar que NUNCA fue llamado
verify(repository, never()).delete(any(User.class));
```

### 4. **ArgumentCaptor para Inspeccionar Argumentos**

```java
ArgumentCaptor<OrderCreatedEvent> eventCaptor = 
    ArgumentCaptor.forClass(OrderCreatedEvent.class);

doNothing().when(publishEventPort)
    .publishEvent(eventCaptor.capture());

service.createOrder(request);

OrderCreatedEvent capturedEvent = eventCaptor.getValue();
assertEquals("order-123", capturedEvent.orderId());
```

---

## 🎓 Casos de Testing por Capa

### **Domain Layer** (Dominio)

```java
// ✅ Tests PUROS sin mocks
@Test
void shouldCreateUserWithValidEmail() {
    User user = User.create(Email.of("test@test.com"), "John");
    assertNotNull(user.getId());
    assertTrue(user.isActive());
}

// ✅ Tests de validación de negocio
@Test
void shouldRejectInvalidEmail() {
    assertThrows(Exception.class, () -> Email.of("invalid"));
}
```

**Características:**
- No hay mocks
- No hay BD
- No hay frameworks
- Ultra-rápidos

### **Application Layer** (Servicios)

```java
// ✅ Tests CON mocks de puertos
@Mock
private UserRepository mockRepo;

@Test
void shouldFindUserFromRepository() {
    when(mockRepo.findById(userId))
        .thenReturn(Optional.of(user));
    
    User result = service.getUser(userId);
    
    verify(mockRepo).findById(userId);
}
```

**Características:**
- Mocks de puertos (interfaces)
- Lógica de orquestación
- Rápidos

### **Infrastructure Layer** (Adaptadores)

```java
// ⏳ POR CREAR - Tests de adaptadores
@Test
void shouldConvertUserToEntity() {
    User user = new User(...);
    UserEntity entity = adapter.toEntity(user);
    assertEquals(user.getId(), entity.getId());
}
```

**Características:**
- Tests de conversión (mapeos)
- Tests de delegación a infraestructura
- Pueden ser más lentos

---

## 📈 Métricas de Cobertura

### Generar reporte de cobertura
```bash
mvn test jacoco:report
```

### Puntos objetivo
- **Dominio**: 100% (es crítico)
- **Application**: 80%+ (lógica importante)
- **Infrastructure**: 60%+ (menos crítico)
- **General**: 80%+

---

## ✅ Checklist para Tests Efectivos

- [ ] **Naming claro**: El nombre del test describe QUÉ se testea
- [ ] **AAA Pattern**: Arrange-Act-Assert bien separados
- [ ] **Un concepto por test**: No testear múltiples cosas
- [ ] **Independencia**: Los tests no dependen unos de otros
- [ ] **Mocks apropiados**: Mockear dependencias externas
- [ ] **Assertions específicos**: No usar solo `assertTrue`
- [ ] **Documentación**: @DisplayName con lenguaje natural
- [ ] **Cobertura**: 80%+ en código crítico
- [ ] **Performance**: Tests < 1 segundo
- [ ] **Determinísticos**: Mismo resultado siempre

---

## 🐛 Debugging Tests

### Ver output de tests
```bash
mvn test -X
```

### Tests verbose
```bash
mvn test -DargLine="-Xmx1024m"
```

### Ejecutar test solo
```bash
mvn test -Dtest=UserServiceTest#testCreateUser -DfailIfNoTests=false
```

---

## 🔄 Próximos Pasos

### 1. **Integration Tests** (Con BD en memoria)
```java
@SpringBootTest
@Testcontainers
class UserRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test");
    
    @Test
    void testSaveUserToDatabase() {
        // Tests con BD real
    }
}
```

### 2. **E2E Tests** (Flujo completo)
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerE2ETest {
    
    @Test
    void testCompleteUserFlow() {
        // Test desde HTTP hasta BD
    }
}
```

### 3. **Performance Tests**
```java
@Test
void shouldCompleteWithin100Milliseconds() {
    // Verificar performance
}
```

---

## 📚 Recursos Adicionales

- **Documentación Oficial:**
  - JUnit 5: https://junit.org/junit5/
  - Mockito: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html

- **Libros Recomendados:**
  - "Working Effectively with Legacy Code" - Michael Feathers
  - "Growing Object-Oriented Software, Guided by Tests" - Freeman & Pryce

---

## 🎯 Resumen

**Con Arquitectura Hexagonal + Testing:**

| Característica | Beneficio |
|---|---|
| **Puertos = Interfaces** | ✅ Fácil mockear |
| **Capas Claras** | ✅ Tests por capa |
| **Dominio Puro** | ✅ Tests ultra-rápidos |
| **Inyección de Dependencias** | ✅ Control en tests |
| **Adaptadores Aislados** | ✅ Tests de cada adaptador |

**Resultado:**
- 🚀 Suite de tests RÁPIDA y CONFIABLE
- 📊 Cobertura alta (80%+)
- 🔒 Refactoring seguro
- 🛡️ Prevención de bugs

