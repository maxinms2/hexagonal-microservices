# ☕ Spring Boot Basics (Explicado con Peras y Manzanas)

## 🤔 ¿Qué es Spring Boot?

Imagina que quieres construir una casa. Tienes dos opciones:

### 🏗️ Opción 1: Desde Cero (Spring Framework tradicional)
- Compras todos los materiales por separado
- Configuras cada tubería, cada cable
- Escribes archivos XML interminables
- Tardas semanas en tener algo funcionando

### 🚀 Opción 2: Casa Prefabricada (Spring Boot)
- Viene con todo pre-configurado
- Enchufas y funciona
- En minutos tienes algo corriendo
- Puedes personalizar después

**Spring Boot = Spring Framework + Configuración Automática + Servidor Integrado**

## 🎯 ¿Por qué Spring Boot?

### ✅ Ventajas

1. **Autoconfiguración Mágica** ✨
   ```java
   // Solo con agregar una dependencia
   // Spring Boot configura TODO automáticamente
   // Base de datos, servidor web, logging, etc.
   ```

2. **Servidor Embebido** 🖥️
   ```java
   // No necesitas instalar Tomcat o Jetty
   // Ya viene incluido
   // Solo ejecutas: java -jar app.jar
   ```

3. **Sin XML** 🚫
   ```java
   // Todo con anotaciones y Java
   // Adiós a los archivos XML
   @SpringBootApplication
   public class Application {}
   ```

4. **Spring Boot Starter** 📦
   ```xml
   <!-- Una dependencia incluye TODO lo necesario -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ```

## 📚 Conceptos Fundamentales

### 1. **@SpringBootApplication** - El Punto de Entrada

Es como el interruptor principal de tu casa. Activa todo.

```java
@SpringBootApplication  // Esta anotación hace 3 cosas:
                       // 1. @Configuration - Permite definir beans
                       // 2. @EnableAutoConfiguration - Configuración automática
                       // 3. @ComponentScan - Busca componentes
public class UserServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

**Analogía**: Es como encender la llave general de tu casa. Activa la electricidad, el agua, el gas, todo de golpe.

### 2. **Inyección de Dependencias (DI)** - El Mayordomo

Imagina que tienes un mayordomo que te trae lo que necesitas:

#### ❌ Sin DI (Tú haces todo):
```java
public class UserController {
    private UserService userService;
    
    public UserController() {
        // Tú creas todo manualmente
        UserRepository repo = new PostgresUserRepository();
        EmailService email = new SmtpEmailService();
        this.userService = new UserService(repo, email);
        // ¡Mucho trabajo!
    }
}
```

#### ✅ Con DI (Spring lo hace por ti):
```java
@RestController
public class UserController {
    
    private final UserService userService;
    
    // Spring automáticamente te da lo que necesitas
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
```

**Analogía**: En un restaurante, el mesero (Spring) te trae los platos (dependencias) sin que tú vayas a la cocina.

### 3. **Anotaciones Comunes** - Las Etiquetas

Las anotaciones son como etiquetas que le dicen a Spring qué es cada cosa:

#### 📦 Estereotipos (Stereotypes)

```java
// 1. @Component - Componente genérico
@Component
public class EmailValidator {
    // Spring lo gestiona
}

// 2. @Service - Lógica de negocio
@Service
public class UserService {
    // Contiene casos de uso
}

// 3. @Repository - Acceso a datos
@Repository
public class UserRepository {
    // Accede a la base de datos
}

// 4. @Controller - Controlador web (devuelve vistas)
@Controller
public class WebController {
    // Maneja peticiones web
}

// 5. @RestController - Controlador REST (devuelve JSON)
@RestController
public class ApiController {
    // Maneja peticiones API REST
}
```

**Analogía**: Es como poner letreros en las puertas:
- "Oficina" (@Service)
- "Almacén" (@Repository)
- "Recepción" (@Controller)

#### 🔌 Inyección

```java
// Opción 1: Constructor (RECOMENDADA)
@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

// Opción 2: @Autowired en el campo (NO recomendada)
@Service
public class UserService {
    @Autowired
    private UserRepository repository;
}

// Opción 3: Setter
@Service
public class UserService {
    private UserRepository repository;
    
    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }
}
```

### 4. **REST Controllers** - Los Puntos de Entrada

```java
@RestController                    // Marca como controlador REST
@RequestMapping("/api/users")     // Ruta base
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;
    
    // GET /api/users
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.findAll();
    }
    
    // GET /api/users/123
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable String id) {
        return userService.findById(id);
    }
    
    // POST /api/users
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
        @RequestBody CreateUserRequest request
    ) {
        UserResponse response = createUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // PUT /api/users/123
    @PutMapping("/{id}")
    public UserResponse updateUser(
        @PathVariable String id,
        @RequestBody UpdateUserRequest request
    ) {
        return userService.update(id, request);
    }
    
    // DELETE /api/users/123
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

**Explicación de anotaciones:**
- `@GetMapping` → HTTP GET (leer)
- `@PostMapping` → HTTP POST (crear)
- `@PutMapping` → HTTP PUT (actualizar)
- `@DeleteMapping` → HTTP DELETE (eliminar)
- `@PathVariable` → Variable en la URL (`/users/{id}`)
- `@RequestBody` → Cuerpo de la petición (JSON)
- `@RequestParam` → Parámetros de consulta (`?page=1&size=10`)

### 5. **Configuration** - Las Configuraciones

```java
@Configuration  // Marca como clase de configuración
public class BeanConfig {
    
    // Define un bean manualmente
    @Bean
    public UserRepository userRepository(JpaUserRepository jpaRepo) {
        return new PostgresUserRepository(jpaRepo);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 6. **application.properties / application.yml** - El Archivo de Configuración

Es como el panel de control de tu aplicación:

```yaml
# application.yml
server:
  port: 8080                          # Puerto del servidor
  
spring:
  application:
    name: user-service                # Nombre del servicio
    
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: postgres
    password: secret
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: update                # Crear/actualizar tablas automáticamente
    show-sql: true                    # Mostrar queries SQL en consola
    properties:
      hibernate:
        format_sql: true              # Formatear SQL bonito
        
logging:
  level:
    root: INFO
    com.microservices: DEBUG          # Log detallado de nuestro código
```

## 🏗️ Estructura de un Proyecto Spring Boot

```
user-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/microservices/user/
│   │   │       ├── UserServiceApplication.java    # Main
│   │   │       ├── domain/                        # Dominio
│   │   │       ├── application/                   # Aplicación
│   │   │       └── infrastructure/                # Infraestructura
│   │   │
│   │   └── resources/
│   │       ├── application.yml                    # Configuración
│   │       ├── application-dev.yml                # Config desarrollo
│   │       ├── application-prod.yml               # Config producción
│   │       └── static/                            # Archivos estáticos
│   │
│   └── test/
│       └── java/                                  # Tests
│
├── pom.xml                                        # Dependencias Maven
└── README.md
```

## 📦 Spring Boot Starters - Los Paquetes Todo-en-Uno

Son como kits de LEGO temáticos. Incluyen todo lo necesario para una funcionalidad:

```xml
<!-- Para aplicaciones web con REST APIs -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Para acceso a base de datos con JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Para seguridad -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Para validación -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Para tests -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 🎯 Ejemplo Completo: API REST Simple

### 1. Clase Principal
```java
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

### 2. Modelo (Entity)
```java
@Entity
@Table(name = "users")
public class UserEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // Getters, Setters, Constructors
}
```

### 3. Repository
```java
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### 4. Service
```java
@Service
public class UserService {
    
    private final JpaUserRepository repository;
    
    public UserService(JpaUserRepository repository) {
        this.repository = repository;
    }
    
    public UserEntity createUser(String email, String name) {
        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setName(name);
        
        return repository.save(user);
    }
    
    public List<UserEntity> findAll() {
        return repository.findAll();
    }
}
```

### 5. Controller
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<UserEntity> create(@RequestBody CreateUserRequest request) {
        UserEntity user = userService.createUser(request.email(), request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @GetMapping
    public List<UserEntity> getAll() {
        return userService.findAll();
    }
}
```

### 6. DTO (Data Transfer Object)
```java
public record CreateUserRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    String name
) {}
```

### 7. Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
            "EMAIL_EXISTS",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));
            
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            message,
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
}
```

## 🚀 Ejecutar la Aplicación

### Opción 1: Desde el IDE
```java
// Click derecho en UserServiceApplication.java
// Run 'UserServiceApplication'
```

### Opción 2: Maven
```bash
# Compilar
mvn clean package

# Ejecutar
mvn spring-boot:run
```

### Opción 3: JAR
```bash
# Compilar JAR
mvn clean package

# Ejecutar JAR
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

## 🧪 Probarlo con cURL

```bash
# Crear usuario
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","name":"John Doe"}'

# Obtener todos los usuarios
curl http://localhost:8080/api/users

# Obtener usuario por ID
curl http://localhost:8080/api/users/123e4567-e89b-12d3-a456-556642440000
```

## 📊 Perfiles de Configuración

Diferentes configuraciones para diferentes ambientes:

```yaml
# application.yml (Común para todos)
spring:
  application:
    name: user-service

---
# application-dev.yml (Desarrollo)
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
  jpa:
    show-sql: true

---
# application-prod.yml (Producción)
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://prod-db:5432/userdb
  jpa:
    show-sql: false
```

**Ejecutar con perfil:**
```bash
# Desarrollo
java -jar app.jar --spring.profiles.active=dev

# Producción
java -jar app.jar --spring.profiles.active=prod
```

## 🔍 Actuator - Monitoreo y Métricas

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

**Endpoints útiles:**
- `http://localhost:8080/actuator/health` - Estado de salud
- `http://localhost:8080/actuator/metrics` - Métricas
- `http://localhost:8080/actuator/info` - Información de la app

## 💡 Mejores Prácticas

### ✅ DO (Hacer):
1. **Usa inyección por constructor**
2. **Externaliza la configuración**
3. **Usa perfiles para diferentes ambientes**
4. **Implementa manejo de excepciones global**
5. **Valida los datos de entrada**
6. **Usa DTOs para requests/responses**
7. **Implementa logging adecuado**

### ❌ DON'T (No hacer):
1. **No pongas lógica de negocio en los controllers**
2. **No uses @Autowired en campos**
3. **No hardcodees valores de configuración**
4. **No expongas entidades JPA directamente**
5. **No ignores las excepciones**

## 📚 Siguiente Paso

Ahora que entiendes Spring Boot, aprenderás sobre API Gateway.

➡️ Continúa con: [API Gateway](04-api-gateway.md)

---

## 💡 Recuerda

> Spring Boot es como tener un asistente que prepara todo por ti. Solo te enfocas en la lógica de negocio, él se encarga del resto.
