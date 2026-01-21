# 🚀 Guía de Inicio Rápido

## ¿Por Dónde Empezar?

Este proyecto está diseñado para aprender de forma gradual. Sigue estos pasos:

## 📖 Paso 1: Lee la Documentación (30 minutos)

Lee los documentos en orden:

1. **[¿Qué son los Microservicios?](docs/01-que-son-microservicios.md)**
   - Entiende los conceptos básicos
   - Aprende cuándo usarlos
   - Conoce las ventajas y desventajas

2. **[Arquitectura Hexagonal](docs/02-arquitectura-hexagonal.md)**
   - Aprende el patrón de diseño
   - Entiende las capas
   - Ve ejemplos prácticos

3. **[Spring Boot Basics](docs/03-spring-boot-basics.md)**
   - Fundamentos de Spring Boot
   - Anotaciones importantes
   - Estructura de proyecto

4. **[API Gateway](docs/04-api-gateway.md)**
   - Punto de entrada único
   - Funciones del Gateway
   - Patrones avanzados

5. **[Service Discovery](docs/05-service-discovery.md)**
   - Registro dinámico de servicios
   - Eureka Server
   - Health checks

## 🛠️ Paso 2: Instala las Herramientas (15 minutos)

### Requisitos

1. **Java JDK 17 o superior**
   ```bash
   # Verificar instalación
   java -version
   
   # Descargar desde:
   # https://adoptium.net/
   ```

2. **Maven 3.8+**
   ```bash
   # Verificar instalación
   mvn -version
   
   # O usa el Maven Wrapper incluido
   ./mvnw -version  (Linux/Mac)
   mvnw.cmd -version  (Windows)
   ```

3. **IDE (Opcional pero recomendado)**
   - IntelliJ IDEA Community (recomendado)
   - VS Code con extensión Java
   - Eclipse

4. **Git**
   ```bash
   git --version
   ```

5. **Docker Desktop (Opcional)**
   - Para PostgreSQL
   - Para producción
   - https://www.docker.com/products/docker-desktop

## ▶️ Paso 3: Ejecuta el User Service (10 minutos)

### Opción A: Con IDE (Recomendado)

1. Abre el proyecto en tu IDE
2. Navega a `user-service/src/main/java/com/microservices/user/UserServiceApplication.java`
3. Click derecho → Run 'UserServiceApplication'

### Opción B: Con Maven

```bash
# Navega al directorio del servicio
cd user-service

# Ejecuta con Maven
mvn spring-boot:run
```

### Opción C: Compilar JAR y Ejecutar

```bash
cd user-service

# Compilar
mvn clean package

# Ejecutar JAR
java -jar target/user-service-1.0.0.jar
```

## ✅ Paso 4: Verifica que Funciona (5 minutos)

### 1. Verifica el Servidor

Deberías ver en la consola:

```
╔════════════════════════════════════════╗
║   USER SERVICE INICIADO CON ÉXITO ✅   ║
╠════════════════════════════════════════╣
║  Puerto: 8081                          ║
║  Consola H2: /h2-console               ║
║  Actuator: /actuator                   ║
╚════════════════════════════════════════╝
```

### 2. Prueba el Health Endpoint

```bash
curl http://localhost:8081/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP"
}
```

### 3. Accede a la Consola H2 (Base de Datos)

1. Abre navegador: http://localhost:8081/h2-console
2. Configuración:
   - JDBC URL: `jdbc:h2:mem:userdb`
   - User Name: `sa`
   - Password: (dejar vacío)
3. Click "Connect"
4. Deberías ver la tabla `USERS`

## 🧪 Paso 5: Prueba la API (15 minutos)

### Usando cURL

#### 1. Crear un usuario

```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@example.com",
    "name": "Juan Pérez"
  }'
```

Respuesta esperada:
```json
{
  "id": "123e4567-e89b-12d3-a456-556642440000",
  "email": "juan@example.com",
  "name": "Juan Pérez",
  "active": true,
  "createdAt": "2026-01-19T10:30:00",
  "updatedAt": "2026-01-19T10:30:00"
}
```

#### 2. Listar todos los usuarios

```bash
curl http://localhost:8081/api/users
```

#### 3. Obtener un usuario específico

```bash
# Usa el ID que obtuviste al crear
curl http://localhost:8081/api/users/123e4567-e89b-12d3-a456-556642440000
```

#### 4. Actualizar un usuario

```bash
curl -X PUT http://localhost:8081/api/users/123e4567-e89b-12d3-a456-556642440000 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Carlos Pérez"
  }'
```

#### 5. Eliminar un usuario (soft delete)

```bash
curl -X DELETE http://localhost:8081/api/users/123e4567-e89b-12d3-a456-556642440000
```

### Usando Postman o Insomnia

1. Importa la colección (próximamente)
2. O crea las peticiones manualmente

### Probar Validaciones

#### Email inválido (debe fallar)

```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "email-invalido",
    "name": "Test"
  }'
```

Respuesta esperada (400 Bad Request):
```json
{
  "timestamp": "2026-01-19T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Error en la validación de datos",
  "errors": {
    "email": "El formato del email es inválido"
  }
}
```

#### Email duplicado (debe fallar)

```bash
# Crear primer usuario
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "name": "Test"}'

# Intentar crear con el mismo email
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "name": "Test 2"}'
```

Respuesta esperada (409 Conflict):
```json
{
  "timestamp": "2026-01-19T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ya existe un usuario con el email: test@example.com"
}
```

## 🔍 Paso 6: Explora el Código (30 minutos)

Ahora que funciona, explora el código siguiendo este orden:

### 1. Domain Layer (Dominio)
```
user-service/src/main/java/com/microservices/user/domain/
├── model/
│   ├── User.java           ← Entidad principal
│   ├── UserId.java         ← Value Object
│   └── Email.java          ← Value Object con validación
├── exception/              ← Excepciones de negocio
└── repository/
    └── UserRepository.java ← Puerto de salida (interface)
```

**¿Qué observar?**
- La entidad `User` tiene lógica de negocio
- `Email` valida automáticamente el formato
- No hay anotaciones de Spring ni JPA

### 2. Application Layer (Aplicación)
```
user-service/src/main/java/com/microservices/user/application/
├── dto/                    ← Request/Response
├── usecase/                ← Puertos de entrada (interfaces)
└── service/
    └── UserService.java    ← Implementación de casos de uso
```

**¿Qué observar?**
- Los casos de uso son interfaces
- `UserService` implementa todas las interfaces
- Orquesta las operaciones
- Usa `@Transactional`

### 3. Infrastructure Layer (Infraestructura)
```
user-service/src/main/java/com/microservices/user/infrastructure/
├── adapter/
│   ├── input/rest/
│   │   ├── UserController.java        ← REST Adapter
│   │   └── GlobalExceptionHandler.java
│   └── output/persistence/
│       ├── PostgresUserRepositoryAdapter.java  ← Persistence Adapter
│       ├── JpaUserRepository.java
│       └── entity/
│           └── UserEntity.java        ← JPA Entity
└── config/
    └── JpaConfig.java
```

**¿Qué observar?**
- `UserController` solo recibe y responde HTTP
- `PostgresUserRepositoryAdapter` implementa `UserRepository`
- Conversión entre `User` (dominio) y `UserEntity` (JPA)

## 📚 Paso 7: Próximos Pasos

1. **Modifica el código**
   - Agrega un campo "phone" al usuario
   - Crea un caso de uso "FindUserByEmail"
   - Agrega validación de edad mínima

2. **Crea tests**
   - Unit tests para el dominio
   - Integration tests para el API

3. **Explora otros conceptos**
   - API Gateway
   - Service Discovery con Eureka
   - Circuit Breaker
   - Distributed Tracing

4. **Crea más microservicios**
   - Order Service
   - Product Service
   - Notification Service

## 🆘 Troubleshooting

### El servicio no inicia

```bash
# Verifica que el puerto 8081 no esté en uso
netstat -ano | findstr :8081  (Windows)
lsof -i :8081                  (Linux/Mac)

# Si está en uso, cambia el puerto en application.yml
server:
  port: 8082
```

### Error al compilar

```bash
# Limpia y recompila
mvn clean install

# Si falla, verifica Java y Maven
java -version
mvn -version
```

### No puedo conectar a H2

1. Verifica que el servicio esté corriendo
2. URL correcta: http://localhost:8081/h2-console
3. JDBC URL: `jdbc:h2:mem:userdb`
4. Usuario: `sa`, Password: (vacío)

### Errores de dependencias

```bash
# Forzar descarga de dependencias
mvn clean install -U
```

## 💬 Preguntas Frecuentes

**P: ¿Puedo usar PostgreSQL en lugar de H2?**  
R: Sí, cambia el perfil a `prod` y configura PostgreSQL.

**P: ¿Cómo agrego más servicios?**  
R: Copia la estructura de `user-service` y adapta para tu dominio.

**P: ¿Puedo usar Gradle en lugar de Maven?**  
R: Sí, pero tendrás que crear el `build.gradle` equivalente.

**P: ¿Necesito Docker?**  
R: No para desarrollo con H2. Sí para producción con PostgreSQL.

## 🎓 Recursos Adicionales

- [Documentación Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación Spring Cloud](https://spring.io/projects/spring-cloud)
- [Baeldung - Spring Tutorials](https://www.baeldung.com/spring-tutorial)
- [Martin Fowler - Microservices](https://martinfowler.com/articles/microservices.html)

---

## 🎉 ¡Felicidades!

Si llegaste hasta aquí, ya tienes:
- ✅ Un microservicio funcional
- ✅ Arquitectura hexagonal implementada
- ✅ API REST completa
- ✅ Comprensión de conceptos clave

**Siguiente objetivo**: Crear el Order Service y comunicarlo con el User Service.

¡Sigue aprendiendo! 🚀
