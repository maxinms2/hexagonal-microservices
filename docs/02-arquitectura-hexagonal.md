# 🏛️ Arquitectura Hexagonal (Explicada con Peras y Manzanas)

## 🤔 ¿Qué es la Arquitectura Hexagonal?

También conocida como **"Ports and Adapters"** (Puertos y Adaptadores), es una forma de organizar tu código para que sea:
- ✅ Fácil de probar
- ✅ Fácil de cambiar
- ✅ Independiente de frameworks
- ✅ Independiente de bases de datos

## 🍕 Analogía: La Cocina de un Restaurante

Imagina la cocina de un restaurante:

### 🎯 El Centro: La Lógica de Negocio (Domain)
El cocinero y sus recetas. Solo se preocupa de hacer buena comida.

```
     ┌─────────────────┐
     │   COCINERO      │
     │  (Domain Core)  │
     │                 │
     │  - Recetas      │
     │  - Técnicas     │
     │  - Sabores      │
     └─────────────────┘
```

### 🔌 Los Puertos: Las Interfaces
Son como las **ventanillas de la cocina**:
- **Puerto de entrada (Input Port)**: Por donde LLEGAN los pedidos
- **Puerto de salida (Output Port)**: Por donde SALEN las peticiones (ingredientes, etc.)

### 🔧 Los Adaptadores: Las Implementaciones
Son las diferentes formas de comunicarse:

**Adaptadores de Entrada** (Drivers):
- 🌐 Pedido por web
- 📱 Pedido por app móvil
- 📞 Pedido por teléfono
- 🤖 Pedido por API REST

**Adaptadores de Salida** (Driven):
- 🗄️ Guardar en PostgreSQL
- 📝 Guardar en MongoDB
- 📧 Enviar email
- 📨 Enviar mensaje

## 📐 La Estructura Hexagonal

```
              EXTERIOR (Infrastructure)
                        │
     ┌──────────────────┼──────────────────┐
     │                  │                  │
┌────▼────┐      ┌──────▼──────┐    ┌─────▼────┐
│  REST   │      │   GraphQL   │    │   CLI    │
│Controller│     │  Controller │    │ Commands │
└────┬────┘      └──────┬──────┘    └─────┬────┘
     │                  │                  │
     └──────────────────┼──────────────────┘
                        │
              ┌─────────▼─────────┐
              │   INPUT PORTS     │  <- Interfaces
              │  (Use Cases)      │
              └─────────┬─────────┘
                        │
              ┌─────────▼─────────┐
              │     DOMAIN        │  <- Corazón
              │  (Business Logic) │
              │                   │
              │  - Entities       │
              │  - Value Objects  │
              │  - Business Rules │
              └─────────┬─────────┘
                        │
              ┌─────────▼─────────┐
              │  OUTPUT PORTS     │  <- Interfaces
              │  (Repositories)   │
              └─────────┬─────────┘
                        │
     ┌──────────────────┼──────────────────┐
     │                  │                  │
┌────▼────┐      ┌──────▼──────┐    ┌─────▼────┐
│PostgreSQL│     │   MongoDB   │    │  Redis   │
│ Adapter  │     │   Adapter   │    │ Adapter  │
└─────────┘      └─────────────┘    └──────────┘
```

## 🎯 Las Capas en Detalle

### 1. **Domain (Dominio)** - El Corazón ❤️

**¿Qué es?** La lógica de negocio pura, sin dependencias externas.

**Contiene:**
- **Entities (Entidades)**: Los objetos principales del negocio
  ```java
  // Usuario.java
  public class User {
      private UserId id;
      private Email email;
      private String name;
      
      // Lógica de negocio
      public void changeEmail(Email newEmail) {
          // Validaciones
          this.email = newEmail;
      }
  }
  ```

- **Value Objects**: Objetos que representan valores
  ```java
  // Email.java
  public record Email(String value) {
      public Email {
          if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
              throw new InvalidEmailException();
          }
      }
  }
  ```

- **Business Rules**: Las reglas del negocio
  ```java
  // Un usuario no puede hacer más de 10 pedidos por día
  if (user.getTodayOrders() >= 10) {
      throw new TooManyOrdersException();
  }
  ```

### 2. **Application (Aplicación)** - Los Casos de Uso 🎬

**¿Qué es?** Define QUÉ puede hacer el sistema.

**Contiene:**
- **Use Cases (Casos de Uso)**: Las acciones que puede realizar
  ```java
  // CreateUserUseCase.java
  public interface CreateUserUseCase {
      UserResponse execute(CreateUserRequest request);
  }
  ```

- **Input Ports**: Interfaces que definen las entradas
- **Output Ports**: Interfaces que definen las salidas

**Ejemplo completo:**
```java
@Service
public class CreateUserService implements CreateUserUseCase {
    
    private final UserRepository userRepository; // Output Port
    private final EmailService emailService;     // Output Port
    
    @Override
    public UserResponse execute(CreateUserRequest request) {
        // 1. Validar
        Email email = new Email(request.email());
        
        // 2. Crear entidad
        User user = new User(email, request.name());
        
        // 3. Guardar (usando puerto de salida)
        userRepository.save(user);
        
        // 4. Enviar email de bienvenida
        emailService.sendWelcome(email);
        
        // 5. Retornar respuesta
        return UserResponse.from(user);
    }
}
```

### 3. **Infrastructure (Infraestructura)** - Los Adaptadores 🔧

**¿Qué es?** Las implementaciones concretas que conectan con el mundo exterior.

**Adaptadores de Entrada (Input Adapters):**
```java
// REST Controller
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;
    
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        UserResponse response = createUserUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}
```

**Adaptadores de Salida (Output Adapters):**
```java
// PostgreSQL Implementation
@Repository
public class PostgresUserRepository implements UserRepository {
    
    private final JpaUserRepository jpaRepository;
    
    @Override
    public void save(User user) {
        UserEntity entity = UserEntity.from(user);
        jpaRepository.save(entity);
    }
}
```

## 📦 Estructura de Carpetas

```
user-service/
├── src/main/java/com/microservices/user/
│   ├── domain/                          # Capa de Dominio
│   │   ├── model/                       # Entidades
│   │   │   ├── User.java
│   │   │   ├── UserId.java
│   │   │   └── Email.java
│   │   ├── exception/                   # Excepciones de dominio
│   │   │   └── UserNotFoundException.java
│   │   └── repository/                  # Puertos de salida
│   │       └── UserRepository.java      # Interface
│   │
│   ├── application/                     # Capa de Aplicación
│   │   ├── usecase/                     # Casos de uso
│   │   │   ├── CreateUserUseCase.java
│   │   │   ├── FindUserUseCase.java
│   │   │   └── UpdateUserUseCase.java
│   │   ├── service/                     # Implementaciones
│   │   │   └── CreateUserService.java
│   │   └── dto/                         # Request/Response
│   │       ├── CreateUserRequest.java
│   │       └── UserResponse.java
│   │
│   └── infrastructure/                  # Capa de Infraestructura
│       ├── adapter/
│       │   ├── input/                   # Adaptadores de entrada
│       │   │   └── rest/
│       │   │       └── UserController.java
│       │   └── output/                  # Adaptadores de salida
│       │       ├── persistence/
│       │       │   ├── PostgresUserRepository.java
│       │       │   └── entity/
│       │       │       └── UserEntity.java
│       │       └── messaging/
│       │           └── RabbitMQPublisher.java
│       └── config/                      # Configuraciones
│           ├── DatabaseConfig.java
│           └── BeanConfig.java
```

## 🎨 Beneficios de esta Arquitectura

### 1. **Testabilidad** 🧪
Puedes probar la lógica de negocio sin bases de datos ni frameworks:
```java
@Test
void shouldCreateUser() {
    // Mock del puerto de salida
    UserRepository mockRepo = mock(UserRepository.class);
    EmailService mockEmail = mock(EmailService.class);
    
    // Crear el caso de uso
    CreateUserService service = new CreateUserService(mockRepo, mockEmail);
    
    // Probar la lógica pura
    UserResponse response = service.execute(
        new CreateUserRequest("test@email.com", "John")
    );
    
    assertNotNull(response);
}
```

### 2. **Flexibilidad** 🔄
Cambiar de PostgreSQL a MongoDB es fácil:
```java
// Solo creas un nuevo adaptador
@Repository
public class MongoUserRepository implements UserRepository {
    // Nueva implementación
    // La lógica de negocio NO cambia
}
```

### 3. **Independencia** 🆓
- El dominio no conoce Spring Boot
- El dominio no conoce la base de datos
- Puedes cambiar frameworks sin tocar la lógica

### 4. **Claridad** 📖
- Cada capa tiene una responsabilidad clara
- Fácil de entender y mantener
- Nuevos desarrolladores se orientan rápido

## 🔄 Flujo de una Petición

Veamos cómo fluye una petición de "Crear Usuario":

```
1. Cliente HTTP
   │
   ▼
2. REST Controller (Input Adapter)
   │ - Recibe el JSON
   │ - Valida formato
   ▼
3. CreateUserRequest (DTO)
   │
   ▼
4. CreateUserUseCase (Input Port)
   │
   ▼
5. CreateUserService (Application)
   │ - Ejecuta lógica de negocio
   │ - Crea entidad User
   │ - Aplica reglas de negocio
   ▼
6. UserRepository (Output Port - Interface)
   │
   ▼
7. PostgresUserRepository (Output Adapter)
   │ - Guarda en la BD
   │
   ▼
8. Base de Datos PostgreSQL
   │
   ▼
9. Respuesta hacia arriba
   UserResponse → Controller → Cliente
```

## 🎯 Reglas de Oro

### ✅ DO (Hacer):
1. **Las dependencias apuntan HACIA el dominio**
2. **El dominio NO depende de nada**
3. **Usa interfaces para los puertos**
4. **Mantén la lógica de negocio en el dominio**

### ❌ DON'T (No hacer):
1. **No pongas anotaciones de Spring en el dominio**
2. **No accedas a la BD desde el dominio**
3. **No mezcles lógica de negocio con infraestructura**
4. **No hagas que el dominio conozca HTTP o JSON**

## 📚 Ejemplo Completo

### Dominio
```java
// User.java (Entidad)
public class User {
    private final UserId id;
    private Email email;
    private String name;
    private LocalDateTime createdAt;
    
    public User(UserId id, Email email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
    
    // Lógica de negocio
    public void updateEmail(Email newEmail) {
        if (newEmail.equals(this.email)) {
            throw new SameEmailException();
        }
        this.email = newEmail;
    }
}
```

### Aplicación
```java
// CreateUserUseCase.java (Input Port)
public interface CreateUserUseCase {
    UserResponse execute(CreateUserRequest request);
}

// CreateUserService.java (Implementación)
@Service
public class CreateUserService implements CreateUserUseCase {
    private final UserRepository userRepository;
    
    @Override
    public UserResponse execute(CreateUserRequest request) {
        User user = new User(
            UserId.generate(),
            new Email(request.email()),
            request.name()
        );
        
        userRepository.save(user);
        
        return UserResponse.from(user);
    }
}

// UserRepository.java (Output Port)
public interface UserRepository {
    void save(User user);
    Optional<User> findById(UserId id);
}
```

### Infraestructura
```java
// UserController.java (Input Adapter)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        UserResponse response = createUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// PostgresUserRepository.java (Output Adapter)
@Repository
public class PostgresUserRepository implements UserRepository {
    private final JpaUserRepository jpaRepository;
    
    @Override
    public void save(User user) {
        UserEntity entity = UserEntity.from(user);
        jpaRepository.save(entity);
    }
    
    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
            .map(UserEntity::toDomain);
    }
}
```

## 💡 Comparación: Sin vs Con Hexagonal

### ❌ Sin Arquitectura Hexagonal
```java
@RestController
public class UserController {
    @Autowired
    private JpaRepository repo;
    
    @PostMapping("/users")
    public User create(@RequestBody User user) {
        // Lógica de negocio mezclada con infraestructura
        if (user.getEmail().contains("@")) {
            return repo.save(user);
        }
        throw new RuntimeException("Invalid");
    }
}
```

**Problemas:**
- Controller conoce la BD
- Difícil de probar
- Lógica de negocio en el controller
- Acoplado a Spring y JPA

### ✅ Con Arquitectura Hexagonal
```java
// Separación clara de responsabilidades
// Controller solo recibe y responde
// Service contiene la lógica
// Repository es una interfaz
// PostgresUserRepository implementa la persistencia
```

## 📚 Siguiente Paso

Ahora que entiendes la arquitectura hexagonal, aprenderás sobre Spring Boot.

➡️ Continúa con: [Spring Boot Basics](03-spring-boot-basics.md)

---

## 💡 Recuerda

> La arquitectura hexagonal es sobre **separar responsabilidades**. El dominio es el rey, todo lo demás es reemplazable.
