# 🏗️ ARQUITECTURA HEXAGONAL Y TESTABILIDAD

## ¿Por qué Hexagonal hace más fácil el testing?

### 1. **Separación de Capas = Separación de Responsabilidades**

```
┌─────────────────────────────────────────────────┐
│           🌐 ADAPTERS (ENTRADA)                 │
│    Controllers, ConsumidoresKafka, etc.         │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│        📦 APPLICATION LAYER (USE CASES)         │
│   UserService, OrderService, etc.               │
│   ⚡ SIN dependencias de Spring                 │
│   ⚡ SIN dependencias de BD                     │
│   ⚡ Lógica pura de negocio                     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│        🎯 DOMAIN LAYER (Lógica Pura)            │
│   Entidades, Value Objects, Excepciones         │
│   ⚡ SIN dependencias externas                  │
│   ⚡ Reglas de negocio puras                    │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  🔌 ADAPTERS (SALIDA) - PORTS IMPLEMENTADOS    │
│    BD, Kafka, Email, Servicios externos         │
└─────────────────────────────────────────────────┘
```

### 2. **Inversión de Dependencias = Fácil de Mockear**

#### ❌ SIN Hexagonal (Código acoplado):
```java
@Service
public class UserService {
    private UserRepository userRepository = new UserRepositoryImpl(); // Acoplado
    private EmailService emailService = new EmailServiceImpl();        // Acoplado
    
    public void createUser(String name) {
        // Si EmailService envía emails reales, nuestro test fallará
        // No podemos cambiar el comportamiento en tests
    }
}
```

**Problema:** 
- ❌ Tests envían emails reales
- ❌ Tests conectan a BD real
- ❌ Imposible cambiar comportamiento en pruebas

#### ✅ CON Hexagonal (Código desacoplado):
```java
public class UserService {
    private final UserRepository userRepository;          // Inyectado
    private final EmailNotificationPort emailPort;        // Puerto (interface)
    
    public UserService(UserRepository repo, EmailNotificationPort port) {
        this.userRepository = repo;
        this.emailPort = port;
    }
    
    public void createUser(String name) {
        // En pruebas, inyectamos un MOCK
        // En producción, inyectamos implementación real
    }
}
```

**Ventaja:**
- ✅ Tests usan mocks
- ✅ Código puro sin side effects
- ✅ Fácil cambiar comportamiento por test

---

## 3. **TRES NIVELES DE TESTING CLAROS**

### 🧪 Level 1: Domain Tests (Más rápidos)
```java
@Test
void shouldValidateEmail() {
    // ✨ Sin mocks, sin BD, solo lógica pura
    Email email = new Email("invalid-email");  // Lanza excepción
}
```
**Velocidad:** ⚡ Milisegundos

---

### 🧪 Level 2: Application Tests (Con mocks)
```java
@Test
void shouldCreateUserWithValidEmail() {
    // 📦 Testeamos UserService con UserRepository mockeado
    
    when(userRepository.save(any())).thenReturn(testUser);
    UserResponse response = userService.execute(request);
    
    // ✨ SIN conectar a BD real
}
```
**Velocidad:** ⚡ Milisegundos (mocks instantáneos)

---

### 🧪 Level 3: Integration Tests (Con dependencias reales)
```java
@SpringBootTest
void shouldIntegrateDatabaseCorrectly() {
    // 🔗 Aquí sí usamos BD real (en tests)
    userRepository.save(testUser);
    User found = userRepository.findById(userId);
    
    assertEquals(testUser.getName(), found.getName());
}
```
**Velocidad:** 🐢 Segundos (pero pocas pruebas)

---

## 4. **TABLA COMPARATIVA: CON vs SIN Hexagonal**

| Aspecto | SIN Hexagonal | CON Hexagonal |
|---------|---------------|---------------|
| **Acoplamiento** | 🔴 Alto | 🟢 Bajo |
| **Tests veloces** | 🔴 No | 🟢 Sí |
| **Mocks fáciles** | 🔴 No | 🟢 Sí |
| **Cambiar BD** | 🔴 Difícil | 🟢 Trivial |
| **Cambiar Service** | 🔴 Difícil | 🟢 Trivial |
| **Tests unitarios** | 🔴 Imposibles** | 🟢 Naturales |
| **Cobertura** | 🔴 Baja | 🟢 Alta |

\*Sin Hexagonal, los "unit tests" terminan siendo "integration tests"

---

## 5. **PATRÓN: TRES TIPOS DE OBJETOS EN TESTS**

### 🧩 Real Objects (Objetos reales)
```java
// Objetos de dominio - NO tienen dependencias externas
Email email = new Email("test@example.com");
UserId userId = UserId.generate();
User user = new User(userId, email, "John");  // ✅ Sin mocks
```

### 🎭 Mock Objects (Simulados)
```java
// Dependencias externas - Comportamiento controlado
UserRepository userRepository = mock(UserRepository.class);
when(userRepository.save(any())).thenReturn(testUser);
```

### 🤝 Stub Objects (Respuestas fijas)
```java
// Cuando solo necesitamos una respuesta, sin verificaciones
when(userRepository.findAllActive()).thenReturn(Collections.emptyList());
```

---

## 6. **FLUJO DE TESTING EN NUESTROS MICROSERVICIOS**

### 📍 UserService
```
Test
  ↓
UserService.execute(CreateUserRequest)
  ├─→ Email (REAL - no tiene dependencias)
  ├─→ UserId (REAL - Value Object puro)
  ├─→ User (REAL - entidad del dominio)
  └─→ userRepository.save() ← MOCK (no queremos guardar en BD)
  
Resultado: Test puro, rápido, aislado
```

### 📍 OrderService
```
Test
  ↓
OrderService.execute(CreateOrderRequest)
  ├─→ Order (REAL)
  ├─→ userValidationPort.validateUser() ← MOCK
  └─→ publishOrderEventPort.publishEvent() ← MOCK
  
Resultado: Test puro, sin enviar eventos reales a Kafka
```

### 📍 NotificationService
```
Test
  ↓
NotificationService.processOrderCreatedEvent(event)
  ├─→ Notification (REAL)
  └─→ sendNotificationPort.send() ← MOCK
  
Resultado: Test puro, sin enviar emails reales
```

---

## 7. **BENEFICIOS CONCRETOS EN NUESTRO PROYECTO**

| Beneficio | Ejemplo |
|-----------|---------|
| **Velocidad** | Tests de UserService: 50ms vs 5s sin Hexagonal |
| **Independencia** | Tests funcionan sin Kafka, BD, Email |
| **Paralelización** | Puedes correr 100 tests simultáneamente |
| **CI/CD** | Muy rápido (segundos, no minutos) |
| **Mantenimiento** | Cambiar BD: solo cambiar adapter |
| **Confianza** | 85%+ cobertura es fácil de alcanzar |

---

## 8. **CHECKLIST PARA BUENOS TESTS HEXAGONALES**

✅ **El test debería poder pasar SIN:**
- [ ] Conectar a BD
- [ ] Enviar emails reales
- [ ] Conectar a Kafka
- [ ] Llamar servicios externos
- [ ] Iniciar servidor Spring

✅ **El test debería tener:**
- [ ] Nombres descriptivos
- [ ] Setup claro con `@BeforeEach`
- [ ] Mocks explícitos con `when()...thenReturn()`
- [ ] Verificaciones con `verify()`
- [ ] Assertions precisas

✅ **El test debería evitar:**
- [ ] Tests que se interfieran entre sí
- [ ] Datos compartidos entre tests
- [ ] Lógica compleja de setup
- [ ] Assertions genéricas

---

## 9. **ESTRUCTURA DE CARPETAS PARA TESTS**

```
user-service/
├── src/main/java/com/microservices/user/
│   ├── application/
│   │   ├── service/
│   │   │   └── UserService.java
│   │   └── dto/
│   ├── domain/
│   │   ├── model/
│   │   ├── exception/
│   │   └── repository/
│   └── infrastructure/
│
└── src/test/java/com/microservices/user/
    ├── domain/           ← Tests de dominio (SIN mocks)
    │   └── model/
    ├── application/      ← Tests de application (CON mocks)
    │   └── service/
    └── infrastructure/   ← Tests de integración (CON BD)
        └── adapter/
```

---

## 10. **EJEMPLO COMPLETO: USER CREATION**

### Domain Test (Puro, sin mocks)
```java
@Test
void shouldValidateEmailFormat() {
    assertThrows(IllegalArgumentException.class,
        () -> new Email("invalid-email"));
}
```

### Application Test (Con mocks)
```java
@Test
void shouldCreateUserWithRepository() {
    when(userRepository.save(any())).thenReturn(testUser);
    
    UserResponse response = userService.execute(request);
    
    assertNotNull(response);
    verify(userRepository).save(any());
}
```

### Integration Test (Completo)
```java
@SpringBootTest
void shouldCreateUserEnd2End() {
    userRepository.save(testUser);
    User found = userRepository.findById(testUser.getId());
    
    assertEquals(testUser.getEmail(), found.getEmail());
}
```

---

## 📚 Conclusión

**Hexagonal Architecture no es solo sobre organización de código, es sobre hacer testing fácil, rápido y confiable.**

Con Hexagonal:
- ✅ Escribes tests en lugar de temer tests
- ✅ Cambias código sin miedo a romper todo
- ✅ Tu suite de tests corre en segundos
- ✅ Tu cobertura es alta naturalmente
- ✅ Onboarding de nuevos devs es más fácil
