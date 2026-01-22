# 🎯 REFERENCIA RÁPIDA - TESTING EN HEXAGONAL

## 🏃 Ejecución Rápida

```bash
# Compilar
mvn clean compile

# Tests unitarios (RÁPIDO - con mocks)
mvn clean test                          # Todos
mvn clean test -f user-service/pom.xml  # Un servicio

# Tests específico
mvn test -Dtest=UserServiceTest#shouldCreateUser

# Con cobertura
mvn clean test jacoco:report
# Ver: target/site/jacoco/index.html
```

---

## 📊 Diagrama: De Código Acoplado a Hexagonal

### ❌ ANTES (Acoplado)
```
┌─────────────────────┐
│   UserService       │
│                     │
│  userRepository =   │
│   new UserRepository│ ← ⚠️ ACOPLADO
│                     │
│  emailService =     │
│   new EmailService  │ ← ⚠️ ACOPLADO
└─────────────────────┘
        ↓
    [BD REAL]  ← ❌ Tests conectan a BD
    [EMAIL]    ← ❌ Tests envían emails
    
Test tarda: 5-10 segundos
Tests frágiles: Fallan si infraestructura falla
```

### ✅ DESPUÉS (Hexagonal)
```
┌─────────────────────────────────┐
│   UserService (POJO)            │
│                                 │
│  private final UserRepository;  │
│  private final EmailPort;       │
│                                 │
│  public UserService(            │
│    UserRepository,   ← Interface│
│    EmailPort)        ← Interface│
└─────────────────────────────────┘
        ↓
    ┌────────────────────────┐
    │   Inyección en Tests   │
    │   (Mocks)              │
    └────────────────────────┘
    
    ┌────────────────────────┐
    │   Inyección en Prod    │
    │   (Implementaciones)   │
    └────────────────────────┘

Test tarda: 10-50 milisegundos
Tests rápidos y confiables: ✅
```

---

## 🧪 Patrón AAA (Arrange-Act-Assert)

### Template Universal

```java
@Test
@DisplayName("Descripción clara de qué testea")
void shouldDoSomethingWhenCondition() {
    // 🟦 ARRANGE - Preparar datos y mocks
    CreateUserRequest request = new CreateUserRequest(
        "test@example.com",
        "Test User"
    );
    
    when(userRepository.save(any(User.class)))
        .thenReturn(testUser);
    
    // 🟪 ACT - Ejecutar la acción
    UserResponse response = userService.execute(request);
    
    // 🟩 ASSERT - Verificar resultados
    assertNotNull(response);
    assertEquals("test@example.com", response.email());
    verify(userRepository).save(any(User.class));
}
```

---

## 📋 Checklist: Mocks Comunes

### ✅ UserRepository (Mock)
```java
@Mock
private UserRepository userRepository;

// Cuando guardamos
when(userRepository.save(any(User.class)))
    .thenAnswer(inv -> {
        User u = inv.getArgument(0);
        u.setId(testId);
        return u;
    });

// Cuando buscamos
when(userRepository.findById(any(UserId.class)))
    .thenReturn(Optional.of(testUser));

// Cuando listamos
when(userRepository.findAllActive())
    .thenReturn(List.of(testUser, testUser2));
```

### ✅ UserValidationPort (Mock)
```java
@Mock
private UserValidationPort userValidationPort;

// Usuario válido
when(userValidationPort.validateUser(testUserId))
    .thenReturn(true);

// Usuario inválido
when(userValidationPort.validateUser(invalidUserId))
    .thenReturn(false);
```

### ✅ PublishOrderEventPort (Mock)
```java
@Mock
private PublishOrderEventPort publishOrderEventPort;

// Evento publicado exitosamente
when(publishOrderEventPort.publishEvent(any()))
    .thenReturn(true);

// Falla en publicación
when(publishOrderEventPort.publishEvent(any()))
    .thenThrow(new RuntimeException("Kafka no disponible"));
```

### ✅ SendNotificationPort (Mock)
```java
@Mock
private SendNotificationPort sendNotificationPort;

// Email enviado
when(sendNotificationPort.sendNotification(any()))
    .thenReturn(true);

// Falla de envío
when(sendNotificationPort.sendNotification(any()))
    .thenThrow(new RuntimeException("SMTP error"));
```

---

## 🎭 Mockito: Los 7 Métodos Clave

### 1️⃣ `when()...thenReturn()`
```java
// Mock simple: siempre retorna lo mismo
when(userRepository.save(any())).thenReturn(testUser);
```

### 2️⃣ `when()...thenThrow()`
```java
// Mock falla: lanza excepción
when(userRepository.save(any())).thenThrow(new RuntimeException());
```

### 3️⃣ `when()...thenAnswer()`
```java
// Mock dinámico: lógica personalizada
when(userRepository.save(any())).thenAnswer(inv -> {
    User u = inv.getArgument(0);
    u.setId(newId);
    return u;
});
```

### 4️⃣ `verify(mock).method()`
```java
// Verificar que se llamó
verify(userRepository).save(any());

// Verificar N veces
verify(userRepository, times(1)).save(any());

// Verificar que NO se llamó
verify(userRepository, never()).delete(any());
```

### 5️⃣ `ArgumentCaptor`
```java
ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
verify(orderRepository).save(captor.capture());

Order capturedOrder = captor.getValue();
assertEquals(expectedAmount, capturedOrder.getAmount());
```

### 6️⃣ `InOrder`
```java
InOrder inOrder = inOrder(repo1, repo2);
inOrder.verify(repo1).method1();  // Primero
inOrder.verify(repo2).method2();  // Después
```

### 7️⃣ `any()` / `eq()` / `argThat()`
```java
// Cualquier valor
when(repo.save(any(User.class))).thenReturn(user);

// Valor exacto
when(repo.findById(eq(userId))).thenReturn(Optional.of(user));

// Condición personalizada
when(repo.findAll(argThat(u -> u.getName().startsWith("J"))))
    .thenReturn(jUsers);
```

---

## 📊 Tabla: Tests vs Puertos

| Servicio | Puerto 1 | Puerto 2 | Puerto 3 | Tests |
|----------|----------|----------|----------|-------|
| **User** | Repository | - | - | 15 |
| **Order** | Repository | UserValidation | EventPublisher | 20 |
| **Notification** | SendNotification | - | - | 12 |

**Total:** 47+ tests, 150-200ms ejecución

---

## 🎯 Decisiones de Diseño (Por qué Hexagonal)

### ¿Por qué UserRepository es interface?
```
✅ Pros:
- Cambiar BD sin tocar servicio
- Mockear en tests fácilmente
- Múltiples implementaciones (SQL, NoSQL)

❌ Contras:
- Una capa más de abstracción
- (Pero vale la pena)
```

### ¿Por qué no usar @Transactional en tests?
```
✅ Lo que hacemos:
- Mocks de repository
- NO usamos @Transactional
- Tests rápidos y aislados

❌ Alternativa (sin Hexagonal):
- @SpringBootTest
- @Transactional en tests
- Tests lentos y acoplados a Spring
```

### ¿Por qué ArgumentCaptor?
```
✅ Casos de uso:
- Verificar contenido exacto de objeto
- Verificar datos complejos (eventos)
- Debugging: ver qué se pasó exactamente

❌ Alternativa:
- Más verificaciones de propiedades
- Código más largo y frágil
```

---

## 🚀 Evolución Sugerida

### Fase 1 (ACTUAL): Unit Tests con Mocks
```
✅ Rápido: 200ms para 47 tests
✅ Aislado: Sin infraestructura
✅ Fácil: Arrange-Act-Assert simple
```

### Fase 2 (FUTURO): Integration Tests
```
- @SpringBootTest con H2 (BD embebida)
- Tests de flujo completo
- Pocas pruebas (5-10)
```

### Fase 3 (FUTURO): E2E Tests
```
- Docker Compose con todos los servicios
- Kafka real
- BD real
- Manual y/o Selenium
```

---

## 💡 Tips Prácticos

### ✅ Nombres de Test Descriptivos
```java
// ✅ BUENO
void shouldThrowEmailAlreadyExistsExceptionWhenCreatingUserWithDuplicateEmail()

// ❌ MALO
void testCreateUser()
```

### ✅ Setup Compartido (@BeforeEach)
```java
@BeforeEach
void setUp() {
    testUser = new User(...);
    testEmail = new Email(...);
    when(repo.save(any())).thenReturn(testUser);
}
```

### ✅ Organización con @Nested
```java
@Nested
@DisplayName("✅ Create User Tests")
class CreateUserTests { }

@Nested
@DisplayName("🔍 Find User Tests")
class FindUserTests { }
```

### ✅ Un Assert Principal
```java
// ✅ BUENO: Un assert por test
assertEquals(expectedEmail, response.email());

// ❌ MALO: Múltiples asserts sin relación
assertEquals(expectedEmail, response.email());
assertEquals(expectedName, response.name());
assertEquals(expectedAge, response.age());
// → Dividir en 3 tests
```

---

## ⚠️ Errores Comunes

### ❌ Error 1: Testear Implementación, no Comportamiento
```java
// ❌ MALO - Testea implementación
@Test
void testUserServiceCallsRepository() {
    userService.execute(request);
    verify(userRepository).save(any());
}

// ✅ BUENO - Testea comportamiento
@Test
void shouldCreateUserWithResponseData() {
    UserResponse response = userService.execute(request);
    assertEquals("john@example.com", response.email());
}
```

### ❌ Error 2: Mocks en exceso
```java
// ❌ MALO - Mockear todo
Email email = mock(Email.class);
when(email.validate()).thenReturn(true);

// ✅ BUENO - Usar objetos reales de dominio
Email email = new Email("valid@example.com");
```

### ❌ Error 3: Tener Interdependencias en Tests
```java
// ❌ MALO - Un test depende de otro
@Test
void test1_createUser() { /* ... */ }

@Test
void test2_findUserCreatedInTest1() { /* Depende de test1 */ }

// ✅ BUENO - Tests independientes
@Test
void shouldCreateUserAndFind() {
    UserResponse created = userService.execute(request);
    UserResponse found = userService.findById(created.id());
}
```

---

## 📚 Referencias Rápidas

### JUnit 5
- `@Test` - Marca como test
- `@BeforeEach` - Ejecuta antes de cada test
- `@Nested` - Agrupa tests relacionados
- `@DisplayName("...")` - Nombre descriptivo

### Mockito
- `@Mock` - Crea mock
- `@InjectMocks` - Inyecta mocks
- `when()...thenReturn()` - Configura mock
- `verify()` - Verifica llamadas

### Assertions
- `assertNotNull()` - No nulo
- `assertEquals()` - Igualdad
- `assertTrue()`/`assertFalse()` - Booleano
- `assertThrows()` - Lanza excepción
- `assertDoesNotThrow()` - No lanza

---

## 🎬 Hoja de Trucos: Copiar y Pegar

### Test Básico
```java
@Test
@DisplayName("Descripción del test")
void shouldBehaviorWhenCondition() {
    // ARRANGE
    CreateUserRequest request = new CreateUserRequest("test@example.com", "Test");
    when(userRepository.save(any())).thenReturn(testUser);
    
    // ACT
    UserResponse response = userService.execute(request);
    
    // ASSERT
    assertNotNull(response);
    verify(userRepository).save(any());
}
```

### Test con Exception
```java
@Test
@DisplayName("Debe lanzar excepción cuando...")
void shouldThrowException() {
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(existingUser));
    
    assertThrows(EmailAlreadyExistsException.class, () -> {
        userService.execute(request);
    });
}
```

### Test con ArgumentCaptor
```java
@Test
@DisplayName("Debe capturar dato exacto")
void shouldCaptureMockArgument() {
    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
    
    orderService.execute(request);
    
    verify(orderRepository).save(captor.capture());
    Order captured = captor.getValue();
    assertEquals(expectedAmount, captured.getAmount());
}
```

---

**Última actualización:** 22 de enero de 2026  
**Estado:** ✅ Todos los tests pasando
