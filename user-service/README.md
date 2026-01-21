# 👤 User Service

Microservicio de gestión de usuarios con Arquitectura Hexagonal.

## 🏗️ Arquitectura

Este servicio implementa **Arquitectura Hexagonal (Ports & Adapters)** con Spring Boot.

### Estructura de Capas

```
user-service/
├── domain/                    # 💎 CORE - Lógica de Negocio
│   ├── model/                 # Entidades y Value Objects
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
    │   └── output/            # Adaptadores de salida
    │       └── persistence/   # JPA Repository
    └── config/                # Configuraciones
```

## 🚀 Características

- ✅ **Arquitectura Hexagonal**: Dominio independiente de frameworks
- ✅ **Spring Boot 3.2**: Framework moderno
- ✅ **Java 17**: Records, Pattern Matching
- ✅ **PostgreSQL/H2**: Base de datos relacional
- ✅ **API REST**: Endpoints documentados
- ✅ **Validación**: Bean Validation
- ✅ **Manejo de errores**: Global Exception Handler
- ✅ **Logging**: SLF4J + Logback
- ✅ **Value Objects**: Type Safety

## 📦 Dependencias

- Spring Boot Web
- Spring Data JPA
- Spring Boot Validation
- Spring Boot Actuator
- PostgreSQL Driver
- H2 Database (para desarrollo)
- Lombok
- Spring Cloud Eureka Client

## ⚙️ Configuración

### Perfiles

#### Desarrollo (dev)
```yaml
spring:
  profiles:
    active: dev
```
- Base de datos H2 en memoria
- SQL logging habilitado
- H2 Console: http://localhost:8081/h2-console

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
DB_URL=jdbc:postgresql://localhost:5432/userdb
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
java -jar target/user-service-1.0.0.jar
```

### Con Docker
```bash
# Construir imagen
docker build -t user-service:1.0.0 .

# Ejecutar
docker run -p 8081:8081 user-service:1.0.0
```

## 📡 API Endpoints

### Base URL
```
http://localhost:8081/api/users
```

### Crear Usuario
```bash
POST /api/users
Content-Type: application/json

{
  "email": "john@example.com",
  "name": "John Doe"
}

Response: 201 Created
```

### Obtener Todos los Usuarios
```bash
GET /api/users

Response: 200 OK
```

### Obtener Usuario por ID
```bash
GET /api/users/{id}

Response: 200 OK
```

### Actualizar Usuario
```bash
PUT /api/users/{id}
Content-Type: application/json

{
  "email": "newemail@example.com",
  "name": "New Name"
}

Response: 200 OK
```

### Eliminar Usuario
```bash
DELETE /api/users/{id}

Response: 204 No Content
```

## 🧪 Probar con cURL

```bash
# Crear usuario
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","name":"Test User"}'

# Listar usuarios
curl http://localhost:8081/api/users

# Obtener usuario
curl http://localhost:8081/api/users/{id}

# Actualizar usuario
curl -X PUT http://localhost:8081/api/users/{id} \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Name"}'

# Eliminar usuario
curl -X DELETE http://localhost:8081/api/users/{id}
```

## 📊 Actuator Endpoints

- **Health**: http://localhost:8081/actuator/health
- **Info**: http://localhost:8081/actuator/info
- **Metrics**: http://localhost:8081/actuator/metrics

## 🎯 Flujo de una Petición

```
Cliente HTTP
    ↓
UserController (Input Adapter)
    ↓
CreateUserUseCase (Input Port)
    ↓
UserService (Application)
    ↓
UserRepository (Output Port)
    ↓
PostgresUserRepositoryAdapter (Output Adapter)
    ↓
JpaUserRepository (JPA)
    ↓
Base de Datos
```

## 🧠 Conceptos Clave

### Value Objects
- **UserId**: Identificador único tipado
- **Email**: Email validado y normalizado

### Domain Exceptions
- **UserNotFoundException**: Usuario no encontrado
- **EmailAlreadyExistsException**: Email duplicado

### Soft Delete
Los usuarios no se eliminan físicamente, solo se desactivan (`active = false`).

## 📚 Documentación Adicional

Ver carpeta [/docs](../docs/) para documentación completa sobre:
- Microservicios
- Arquitectura Hexagonal
- Spring Boot Basics
- API Gateway
- Service Discovery
