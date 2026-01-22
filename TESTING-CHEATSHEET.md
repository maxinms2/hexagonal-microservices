# 📋 Hoja de Trucos: Testing en Hexagonal

## 🎯 Quick Reference

### Estructura de Carpetas
```
src/test/java/com/microservices/
├── domain/model/
│   └── *Test.java          # Tests de entidades (puros)
└── application/service/
    └── *ServiceTest.java   # Tests de servicios (con mocks)
```

---

## 🧪 Patrones Básicos

### 1. Test Puro (Sin Mocks)
```java
@Test
void testDomainLogic() {
    // Arrange
    User user = User.create(Email.of("test@test.com"), "John");
    
    // Act
    user.deactivate();
    
    // Assert
    assertFalse(user.isActive());
}
```

### 2. Test con Mock (Mockito)
```java
@Mock
private UserRepository repository;

@InjectMocks
private UserService service;

@Test
void testWithMock() {
    // Arrange
    when(repository.save(any()))
        .thenReturn(user);
    
    // Act
    UserResponse response = service.execute(request);
    
    // Assert
    assertNotNull(response);
    verify(repository).save(any());
}
```

### 3. Test de Excepciones
```java
@Test
void testException() {
    assertThrows(UserNotFoundException.class, () -> {
        service.getUser("invalid-id");
    });
}
```

---

## 🛠️ Mockito - Cheat Sheet

### Setup
```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock private Dependency dep;
    @InjectMocks private ClassToTest obj;
}
```

### Configurar Comportamiento
```java
when(repo.save(any())).thenReturn(user);
when(repo.findById("123")).thenReturn(Optional.of(user));
doNothing().when(emailPort).send(any());
doThrow(new Exception()).when(repo).delete(any());
```

### Verificar Llamadas
```java
verify(repo).save(any());              // Fue llamado
verify(repo, times(2)).save(any());    // 2 veces
verify(repo, never()).delete(any());   // Nunca
verify(repo, atLeast(1)).save(any());  // Al menos 1 vez
```

### ArgumentCaptor
```java
ArgumentCaptor<User> captor = 
    ArgumentCaptor.forClass(User.class);
verify(repo).save(captor.capture());
User captured = captor.getValue();
assertEquals("test@test.com", captured.getEmail());
```

### ArgumentMatchers
```java
when(repo.findById(any())).thenReturn(Optional.of(user));
when(repo.findByEmail(eq("test@test.com"))).thenReturn(Optional.of(user));
when(repo.findByName(contains("John"))).thenReturn(users);
when(repo.save(argThat(u -> u.isActive()))).thenReturn(user);
```

---

## 📊 JUnit 5 - Anotaciones

```java
@DisplayName("Descripción clara en lenguaje natural")
@Nested          // Agrupar tests relacionados
@BeforeEach      // Ejecutar antes de cada test
@AfterEach       // Ejecutar después de cada test
@BeforeAll       // Una sola vez antes de todos
@AfterAll        // Una sola vez después de todos
@Test            // Marcar como test
@Disabled        // Deshabilitar test
@Timeout(1000)   // Timeout en ms
@ParameterizedTest  // Tests parametrizados
```

---

## ✅ Aserciones - Comunes

```java
assertEquals(expected, actual);
assertNotNull(obj);
assertNull(obj);
assertTrue(condition);
assertFalse(condition);
assertThrows(Exception.class, () -> method());
assertAll(
    () -> assertEquals(...),
    () -> assertTrue(...)
);
```

---

## 🔥 Patrón AAA (Todo Test Sigue Esto)

```java
@Test
void testExample() {
    // ARRANGE: Preparar datos
    String email = "test@test.com";
    User user = new User(email, "John");
    when(repo.save(any())).thenReturn(user);
    
    // ACT: Ejecutar lo que se testea
    UserResponse response = service.create(email, "John");
    
    // ASSERT: Verificar resultado
    assertEquals(email, response.email());
    
    // VERIFY: Verificar interacciones
    verify(repo).save(any(User.class));
}
```

---

## 🎯 @Nested - Organizar Tests

```java
@DisplayName("User Service")
class UserServiceTest {
    
    @Nested
    @DisplayName("✅ Create User")
    class CreateUserTests {
        @Test
        void shouldCreate() { }
    }
    
    @Nested
    @DisplayName("🔍 Find User")
    class FindUserTests {
        @Test
        void shouldFind() { }
    }
}
```

---

## 🚀 Comandos Maven

### Ejecución
```bash
mvn test                           # Todos
mvn test -Dtest=UserTest          # Clase
mvn test -Dtest=UserTest#method   # Método
mvn clean test                     # Limpiar primero
mvn test -X                        # Debug
```

### Cobertura
```bash
mvn test jacoco:report             # Generar reporte
# Ver: target/site/jacoco/index.html
```

### Performance
```bash
mvn test -DparallelThreads=4       # Paralelo
mvn -DskipTests clean package      # Saltar tests
```

---

## 💡 Puntos Clave de Hexagonal en Tests

| Concepto | Cómo Ayuda |
|----------|-----------|
| **Puertos (Interfaces)** | ✅ Mockear fácil |
| **Inyección de Dependencias** | ✅ Control en tests |
| **Dominio Puro** | ✅ Tests ultra-rápidos |
| **Capas Claras** | ✅ Tests aislados |
| **Adaptadores** | ✅ Cambiar tecnología sin tests |

---

## 🐛 Errores Comunes

### ❌ Mockear entidades de dominio
```java
@Mock private User user;  // ❌ MAL - Es dominio
```

### ✅ Mockear puertos
```java
@Mock private UserRepository repo;  // ✅ BIEN - Es puerto
```

### ❌ Tests acoplados
```java
@Test
void test1() { /* configura repo */ }
@Test
void test2() { /* depende de test1 */ }  // ❌ MAL
```

### ✅ Tests independientes
```java
@BeforeEach
void setUp() { /* configuración */ }

@Test
void test1() { /* independiente */ }
@Test
void test2() { /* independiente */ }  // ✅ BIEN
```

---

## 🔄 Ciclo de Testing

```
1. RED: Escribir test que falla
   └─ mvn test → FAIL

2. GREEN: Escribir código mínimo que pase
   └─ mvn test → PASS

3. REFACTOR: Mejorar código
   └─ mvn test → PASS (sigue pasando)

4. REPEAT: Siguiente feature
```

---

## 📈 Cobertura Objetivo

```
Domain Layer:     100% (es crítico)
Application:       80%+
Infrastructure:    60%+
Overall:          80%+
```

---

## 🎓 Nombrado de Tests

```java
// ✅ BIEN - Describe QUÉ se testea
@Test
@DisplayName("Debe crear usuario con email válido")
void shouldCreateUserWithValidEmail() { }

// ❌ MAL - No es claro
@Test
void test1() { }
```

---

## 🌐 Testing Inter-Microservicios

```java
@Mock private UserValidationPort userValidationPort;

@Test
void shouldValidateUserFromAnotherService() {
    doNothing().when(userValidationPort)
        .validateUserExists("user-123");
    
    orderService.createOrder(request);
    
    verify(userValidationPort)
        .validateUserExists("user-123");
}
```

---

## 🔊 Testing Event-Driven

```java
@Mock private SendEmailPort emailPort;

@Test
void shouldSendEmailWhenEventArrives() {
    OrderCreatedEvent event = new OrderCreatedEvent(...);
    
    notificationService.processOrderCreatedEvent(event);
    
    verify(emailPort).sendEmail(any(), any());
}
```

---

## 📊 Verificación Avanzada

### InOrder (Verificar secuencia)
```java
InOrder inOrder = inOrder(repo, emailPort);
service.execute(request);
inOrder.verify(repo).findById(any());
inOrder.verify(repo).save(any());
inOrder.verify(emailPort).send(any());
```

### ArgumentCaptor (Inspeccionar)
```java
ArgumentCaptor<OrderCreatedEvent> captor = 
    ArgumentCaptor.forClass(OrderCreatedEvent.class);
verify(eventBus).publish(captor.capture());
assertEquals("order-123", captor.getValue().orderId());
```

### Multiple Mocks
```java
doNothing().when(repo).save(any());
doThrow(Exception.class).when(emailPort).send(any());
service.execute(request);
verify(repo).save(any());
verify(emailPort, never()).send(any()); // No llegó
```

---

## 🎯 Testing Exceppciones

### Básico
```java
@Test
void shouldThrowException() {
    assertThrows(UserNotFoundException.class, () -> {
        service.getUser("invalid");
    });
}
```

### Con Message
```java
@Test
void shouldThrowWithCorrectMessage() {
    Exception ex = assertThrows(UserNotFoundException.class, () -> {
        service.getUser("invalid");
    });
    assertEquals("User not found", ex.getMessage());
}
```

### De Mocks
```java
@Test
void shouldHandleRepositoryError() {
    when(repo.save(any())).thenThrow(
        new RuntimeException("DB Error")
    );
    
    assertThrows(RuntimeException.class, () -> {
        service.create(request);
    });
}
```

---

## ⚡ Tips de Performance

### Rápido ✅
```java
@Test void testDomain() { }              // < 1ms
@Test void testWithMock() { }             // < 10ms
@Test void testIntegration() { }          // < 100ms
```

### Lento ❌
```java
@Test void testWithRealDB() { }           // > 1000ms
@Test void testWithKafka() { }            // > 500ms
@Test void testWithExternalAPI() { }      // > 2000ms
```

---

## 📝 Documentación en Tests

```java
/**
 * 🧪 USER SERVICE TESTS
 * 
 * PROPÓSITO: Testear casos de uso con mocks
 * ¿POR QUÉ HEXAGONAL?: Mockear puertos sin BD
 * FRAMEWORKS: JUnit 5, Mockito
 */
@DisplayName("🧪 User Service Tests")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    // ...
}
```

---

## 🎁 Bonus: Test Doubles

```
┌─ Mock: Configurable, verifica llamadas
├─ Stub: Retorna valor fijo
├─ Spy: Wrapper de objeto real
├─ Fake: Implementación alternativa
└─ Dummy: Placeholder sin uso
```

```java
// Mock (Mockito)
UserRepository mock = mock(UserRepository.class);

// Stub (thenReturn)
when(mock.findById(any())).thenReturn(user);

// Spy (wrapping real)
UserRepository spy = spy(new RealRepository());

// Fake (implementación alternativa)
UserRepository fake = new InMemoryUserRepository();

// Dummy (no se usa)
UserRepository dummy = new UserRepository() { };
```

---

## 🏆 Checklist: Tests de Calidad

- [ ] Nombre claro (describe QUÉ)
- [ ] Patrón AAA (Arrange-Act-Assert)
- [ ] Independiente (no depende de otros)
- [ ] @DisplayName en español
- [ ] Cobertura > 80%
- [ ] Tiempo < 1 segundo
- [ ] Determinístico (mismo resultado)
- [ ] Mockea dependencias externas
- [ ] Documentación clara
- [ ] Verifica interacciones (verify)

---

## 📞 Ayuda Rápida

**¿Cómo mockear un puerto?**
```java
@Mock private UserRepository repo;
```

**¿Cómo verificar que fue llamado?**
```java
verify(repo).save(any());
```

**¿Cómo ejecutar tests?**
```bash
mvn test
```

**¿Cómo saber si van bien?**
```bash
mvn test  # BUILD SUCCESS = 100% verde ✅
```

**¿Dónde están los tests?**
```
src/test/java/com/microservices/*/domain/model/
src/test/java/com/microservices/*/application/service/
```

---

## 🎓 Aprende Más

📖 **Documentación Completa:**
- `docs/08-TESTING-Y-HEXAGONAL.md` - Teoría
- `docs/09-GUIA-TESTING-COMPLETA.md` - Guía práctica
- `TESTING-README.md` - Resumen

📺 **En el código:**
- Todos los `*Test.java` tienen comentarios detallados
- Ejemplos reales de cada patrón

---

## ✨ Resumen Ultra-Rápido

**Hexagonal + Testing = ❤️**

- Puertos (interfaces) → Mockear fácil
- Dominio puro → Tests rápidos
- Capas claras → Tests aislados
- Inyección → Control total
- Resultado → 65+ tests en < 200ms

¡Disfruta testeando! 🚀

