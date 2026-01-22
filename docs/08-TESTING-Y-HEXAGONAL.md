# 🧪 Testing en Arquitectura Hexagonal

## ¿Por Qué Hexagonal Mejora la Testibilidad?

### 1. **Separación de Responsabilidades = Tests Aislados**

La arquitectura hexagonal divide la aplicación en capas claras:

```
┌─────────────────────────────────────────────────┐
│  ADAPTADORES DE ENTRADA (Controllers, APIs)     │
│         ↓                                        │
│  PUERTOS DE ENTRADA (Interfaces de Casos Uso)   │
│         ↓                                        │
│  APPLICATION LAYER (Servicios, Lógica)          │
│         ↓                                        │
│  PUERTOS DE SALIDA (Interfaces de Repositorios) │
│         ↓                                        │
│  ADAPTADORES DE SALIDA (BD, APIs Externas)      │
└─────────────────────────────────────────────────┘
```

**Beneficio para testing:**
- Cada capa se puede testear de forma **completamente aislada**
- Usamos **mocks** en las capas inferiores
- No necesitamos una base de datos real
- Los tests son **rápidos y confiables**

### 2. **Puertos = Inyección de Dependencias = Fácil Mockear**

#### ❌ SIN Hexagonal (Acoplamiento):
```java
public class UserService {
    private final PostgresUserRepository repo = new PostgresUserRepository();
    
    public User getUser(String id) {
        return repo.findById(id);  // 🔗 Acoplado a PostgreSQL
    }
}

// Testing: ¡IMPOSIBLE sin base de datos!
@Test
public void testGetUser() {
    UserService service = new UserService(); // Crea BD automáticamente
    User user = service.getUser("123");      // ¡Falla si BD no está!
}
```

#### ✅ CON Hexagonal (Desacoplamiento):
```java
public interface UserRepository {  // 📦 PUERTO (interface)
    Optional<User> findById(UserId id);
}

public class UserService {
    private final UserRepository repo;  // Depende de INTERFACE
    
    public UserService(UserRepository repo) {
        this.repo = repo;
    }
    
    public User getUser(String id) {
        return repo.findById(UserId.of(id))
                   .orElseThrow();
    }
}

// Testing: ¡FÁCIL! Inyectamos mock
@Test
public void testGetUser() {
    // 🎭 Crear mock del repositorio
    UserRepository mockRepo = mock(UserRepository.class);
    
    // 📋 Configurar comportamiento esperado
    User expectedUser = new User(...);
    when(mockRepo.findById(UserId.of("123")))
        .thenReturn(Optional.of(expectedUser));
    
    // ✅ Inyectar mock en servicio
    UserService service = new UserService(mockRepo);
    User result = service.getUser("123");
    
    // 🔍 Verificar resultado
    assertEquals(expectedUser, result);
    verify(mockRepo).findById(UserId.of("123"));  // Verificar que fue llamado
}
```

### 3. **Dominio Independiente = Tests Puros**

El **dominio** NO tiene ninguna dependencia:

```java
public class User {
    private Email email;
    private String name;
    private boolean active;
    
    // ✅ Lógica de negocio PURA (sin frameworks)
    public void deactivate() {
        this.active = false;
    }
    
    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre inválido");
        }
        this.name = newName.trim();
    }
}

// Testing: ¡Súper simple, rápido y confiable!
@Test
public void testDeactivateUser() {
    User user = new User(UserId.of("123"), Email.of("test@test.com"), "John", true);
    
    user.deactivate();
    
    assertFalse(user.isActive());
}

@Test
public void testUpdateNameWithInvalidValue() {
    User user = new User(...);
    
    assertThrows(IllegalArgumentException.class, () -> user.updateName(""));
    assertThrows(IllegalArgumentException.class, () -> user.updateName(null));
}
```

### 4. **Pirámide de Tests Bien Definida**

```
                    ▲
                   /│\
                  / │ \
                 /  │  \  E2E Tests (1-2 tests)
                /   │   \ • Flujo completo real
               /    │    \• BD real, externos
              /──────────── 
             /     │      \
            /      │       \ Integration Tests (10-20 tests)
           /       │        \• Servicios + Adaptadores
          /        │         \• BD en memoria (H2)
         /         │          \
        /──────────────────────
       /          │           \
      /           │            \ Unit Tests (50+ tests)
     /            │             \• Dominio + Servicios
    /             │              \• Mocks para todo
   /──────────────────────────────
```

**Con Hexagonal:**
- **Unit Tests**: Testean **lógica de negocio pura** (dominio)
- **Integration Tests**: Testean **servicios con mocks** de infraestructura
- **E2E Tests**: Testean **toda la aplicación** con BD real

### 5. **Cada Capa Tiene un Propósito Testeable**

#### Domain Layer (Tests Rápidos, Aislados)
```java
// ✅ NO tiene dependencias
@Test
public void testUserCreation() {
    User user = User.create(
        Email.of("john@test.com"),
        "John Doe"
    );
    
    assertTrue(user.isActive());
    assertNotNull(user.getId());
}
```

#### Application Layer (Tests de Orquestación)
```java
// ✅ Depende de INTERFACES (fácil mockear)
@Test
public void testCreateUserUseCase() {
    // Mocks
    UserRepository mockRepo = mock(UserRepository.class);
    EmailService mockEmailService = mock(EmailService.class);
    
    // Setup
    when(mockRepo.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    
    UserService service = new UserService(mockRepo, mockEmailService);
    
    // Execute
    UserResponse response = service.execute(
        new CreateUserRequest("john@test.com", "John")
    );
    
    // Verify
    assertNotNull(response.id());
    verify(mockRepo).save(any(User.class));
    verify(mockEmailService).sendWelcomeEmail("john@test.com");
}
```

#### Infrastructure Layer (Tests de Adaptadores)
```java
// ✅ Testea SOLO conversión y delegación
@Test
public void testPostgresRepositoryAdapter() {
    // Mocks
    JpaUserRepository mockJpa = mock(JpaUserRepository.class);
    
    // Setup
    UserEntity entity = new UserEntity(...);
    when(mockJpa.save(any(UserEntity.class))).thenReturn(entity);
    
    PostgresUserRepositoryAdapter adapter = 
        new PostgresUserRepositoryAdapter(mockJpa);
    
    // Execute
    User result = adapter.save(user);
    
    // Verify
    assertNotNull(result);
    verify(mockJpa).save(any(UserEntity.class));
}
```

---

## 🎯 Estrategia de Testing por Capa

### Unit Tests (Rápidos)
- **¿Qué?** Lógica de dominio aislada
- **¿Con qué?** JUnit + Mocks
- **¿Velocidad?** Milisegundos
- **¿Ejemplo?** Validaciones, reglas de negocio

### Integration Tests (Medianos)
- **¿Qué?** Servicios + Adaptadores (sin infraestructura real)
- **¿Con qué?** JUnit + TestContainers + H2
- **¿Velocidad?** Segundos
- **¿Ejemplo?** Persistencia en BD en memoria

### E2E Tests (Lentos)
- **¿Qué?** Toda la aplicación con infraestructura real
- **¿Con qué?** Spring Test + BD real + APIs reales
- **¿Velocidad?** Decenas de segundos
- **¿Ejemplo?** Flujo completo de usuario

---

## 📊 Comparación: Hexagonal vs Monolítico

| Aspecto | Monolítico Acoplado | Hexagonal |
|---------|-------------------|-----------|
| **Testabilidad del Dominio** | ❌ Requiere framework | ✅ Puro e independiente |
| **Velocidad de Tests** | ❌ Lentos (BD real) | ✅ Rápidos (mocks) |
| **Aislamiento** | ❌ Todos los tests acoplados | ✅ Cada test aislado |
| **Cambiar Tecnología** | ❌ Reimplementar tests | ✅ Solo cambiar adaptador |
| **Cantidad de Mocks** | ❌ Complicado mockear | ✅ Fácil (puertos claros) |
| **Cobertura** | ❌ Difícil alcanzar 80% | ✅ Fácil alcanzar 90%+ |

---

## 🛠️ Herramientas Recomendadas

```xml
<!-- Mockito: Crear mocks -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ: Aserciones fluidas -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5: Framework de testing -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- TestContainers: Bases de datos en contenedores -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 📌 Resumen: Por Qué Hexagonal es Perfecto para Testing

| Principio Hexagonal | Beneficio para Testing |
|-------------------|------------------------|
| **Puertos (Interfaces)** | ✅ Fácil inyectar mocks |
| **Adaptadores Aislados** | ✅ Testear cada uno por separado |
| **Dominio Puro** | ✅ Tests ultra-rápidos |
| **Inyección de Dependencias** | ✅ Control total en tests |
| **Capas Claras** | ✅ Pirámide de tests bien definida |

**La arquitectura hexagonal NO SOLO mejora el diseño, TAMBIÉN mejora la testabilidad de forma drástica.**

