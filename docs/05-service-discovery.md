# 🔍 Service Discovery (Explicado con Peras y Manzanas)

## 🤔 ¿Qué es Service Discovery?

Imagina una ciudad donde las personas cambian de casa constantemente:
- Juan vivía en la Calle 1, ahora vive en la Calle 5
- María vivía en la Calle 3, ahora hay 3 Marías en diferentes calles
- Pedro se mudó a otra ciudad

### ❌ Sin Service Discovery

Tienes que actualizar tu agenda manualmente cada vez que alguien se muda:

```
Mi Agenda:
Juan: Calle 1, Casa 23    ← Desactualizada!
María: Calle 3, Casa 45   ← Desactualizada!
Pedro: Calle 7, Casa 89   ← Ya no existe!
```

**Problemas:**
- Información obsoleta
- Tienes que preguntar a cada persona su nueva dirección
- Si alguien desaparece, no lo sabes
- No sabes si hay nuevas personas

### ✅ Con Service Discovery

Existe un **DIRECTORIO TELEFÓNICO DINÁMICO** (Service Registry) que se actualiza automáticamente:

```
Directorio Automático:
✅ Juan: Calle 5, Casa 12 (Actualizado hace 2 segundos)
✅ María-1: Calle 2, Casa 34 (3 instancias disponibles)
✅ María-2: Calle 4, Casa 56
✅ María-3: Calle 6, Casa 78
❌ Pedro: No disponible
```

**Ventajas:**
- Siempre actualizado
- Las personas se registran automáticamente
- Se detecta cuando alguien no está disponible
- Puedes encontrar a todos con el mismo nombre

## 📐 Arquitectura

```
              ┌─────────────────────┐
              │  EUREKA SERVER      │
              │ (Service Registry)  │
              │   Puerto 8761       │
              └──────────┬──────────┘
                         │
      ┌──────────────────┼──────────────────┐
      │                  │                  │
      │ REGISTRO         │                  │ REGISTRO
      ▼                  ▼                  ▼
┌──────────┐       ┌──────────┐       ┌──────────┐
│   User   │       │  Order   │       │ Product  │
│ Service  │       │ Service  │       │ Service  │
│  :8081   │       │  :8082   │       │  :8083   │
└─────┬────┘       └─────┬────┘       └─────┬────┘
      │                  │                  │
      └──────────────────┼──────────────────┘
              CONSULTA   │
                         ▼
              "¿Dónde está Order Service?"
```

## 🎯 Componentes Principales

### 1. **Service Registry (Registro de Servicios)** 📖

Es el "directorio telefónico". Almacena la información de todos los servicios.

**Tecnologías:**
- **Eureka Server** (Netflix, Spring Cloud)
- **Consul** (HashiCorp)
- **Zookeeper** (Apache)
- **Kubernetes Service** (en entornos K8s)

### 2. **Service Registration (Registro)** 📝

Cada servicio se registra automáticamente al iniciar:

```
User Service inicia → "Hola Eureka, soy User Service"
                       "Mi IP es 192.168.1.10"
                       "Mi puerto es 8081"
                       "Estoy disponible"
```

### 3. **Service Discovery (Descubrimiento)** 🔍

Los servicios consultan el registro para encontrar otros servicios:

```
Order Service: "Necesito llamar a User Service"
               "¿Dónde está User Service?"

Eureka:        "User Service tiene 3 instancias:"
               "- 192.168.1.10:8081 ✅"
               "- 192.168.1.11:8081 ✅"
               "- 192.168.1.12:8081 ✅"

Order Service: "Gracias, llamaré a la primera"
```

### 4. **Health Checks (Verificación de Salud)** 💓

El registro verifica constantemente que los servicios estén vivos:

```
Cada 30 segundos:
Eureka: "User Service, ¿estás ahí?"
User:   "Sí, estoy bien" → Renueva registro

Si después de 3 intentos no responde:
Eureka: "User Service no responde"
        "Lo marco como no disponible"
        "No lo recomendaré a nadie"
```

## 🔄 Flujo Completo

### 1. Inicio del Servicio

```
┌─────────────────────────────────────────┐
│ 1. User Service inicia                  │
│ 2. Busca Eureka Server (configuración)  │
│ 3. Se conecta a Eureka                  │
│ 4. Se registra con su información       │
│ 5. Envía heartbeat cada 30 segundos     │
└─────────────────────────────────────────┘
```

### 2. Descubrimiento de Servicio

```
┌─────────────────────────────────────────┐
│ 1. Order Service necesita User Service  │
│ 2. Consulta a Eureka                    │
│ 3. Eureka devuelve lista de instancias  │
│ 4. Order elige una (load balancing)     │
│ 5. Order llama directamente al User     │
└─────────────────────────────────────────┘
```

### 3. Manejo de Fallos

```
┌─────────────────────────────────────────┐
│ 1. User Service 1 falla                 │
│ 2. No envía heartbeat                   │
│ 3. Eureka espera 90 segundos            │
│ 4. Eureka lo marca como no disponible   │
│ 5. Ya no lo recomienda a nadie          │
│ 6. Order Service usa User Service 2     │
└─────────────────────────────────────────┘
```

## 🛠️ Implementación con Eureka

### Eureka Server

```java
@SpringBootApplication
@EnableEurekaServer  // Activa el servidor Eureka
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
# application.yml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false  # No se registra a sí mismo
    fetch-registry: false         # No obtiene el registro
```

### Eureka Client (Servicios)

```java
@SpringBootApplication
@EnableDiscoveryClient  // Activa el cliente Eureka
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

```yaml
# application.yml
spring:
  application:
    name: user-service  # Nombre con el que se registra

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true  # Registra IP en lugar de hostname
```

## 📞 Client-Side Load Balancing

Cuando hay múltiples instancias, el cliente elige a cuál llamar:

```java
@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;  // Con @LoadBalanced
    
    public User getUser(String userId) {
        // No especificas IP ni puerto
        // Solo el nombre del servicio
        String url = "http://user-service/api/users/" + userId;
        
        // RestTemplate consulta Eureka
        // Elige una instancia (round-robin por defecto)
        // Hace la petición
        return restTemplate.getForObject(url, User.class);
    }
}
```

```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    @LoadBalanced  // Habilita load balancing con Eureka
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

## 🔍 Estrategias de Descubrimiento

### 1. **Client-Side Discovery**

El cliente consulta el registro y hace la llamada directamente:

```
Order Service → Eureka: "¿Dónde está User Service?"
Eureka → Order: "En 192.168.1.10:8081"
Order → User: Hace la petición directamente
```

**Ventajas:**
- Cliente tiene control total
- Sin intermediarios
- Más rápido

**Desventajas:**
- Cliente más complejo
- Lógica de balanceo en el cliente

### 2. **Server-Side Discovery**

El cliente llama a un load balancer que consulta el registro:

```
Order → Load Balancer: "Llama a User Service"
Load Balancer → Eureka: "¿Dónde está User Service?"
Load Balancer → User: Hace la petición
Load Balancer → Order: Devuelve respuesta
```

**Ventajas:**
- Cliente simple
- Lógica centralizada

**Desventajas:**
- Un componente más (load balancer)
- Potencial cuello de botella

## 🌐 Patrones Avanzados

### 1. **Self Registration**

El servicio se registra a sí mismo:

```java
// Spring Boot lo hace automáticamente con @EnableDiscoveryClient
// Al iniciar:
POST http://eureka:8761/eureka/apps/USER-SERVICE
{
  "instance": {
    "hostName": "user-service-1",
    "app": "USER-SERVICE",
    "ipAddr": "192.168.1.10",
    "port": 8081,
    "status": "UP"
  }
}
```

### 2. **Third-Party Registration**

Un registrador externo registra los servicios:

```
Service Registrar observa contenedores Docker
→ Detecta nuevo contenedor user-service
→ Lo registra en Eureka
→ Envía heartbeats por el servicio
```

### 3. **Health Endpoint**

Eureka verifica la salud llamando a un endpoint:

```
GET http://user-service:8081/actuator/health

Response:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

## 📊 Eureka Dashboard

Eureka incluye una UI web para ver todos los servicios:

```
http://localhost:8761/

┌─────────────────────────────────────────┐
│          EUREKA DASHBOARD                │
├─────────────────────────────────────────┤
│ Instances currently registered:         │
│                                          │
│ USER-SERVICE                             │
│   ✅ user-service-1  (192.168.1.10:8081)│
│   ✅ user-service-2  (192.168.1.11:8081)│
│                                          │
│ ORDER-SERVICE                            │
│   ✅ order-service-1 (192.168.1.20:8082)│
│                                          │
│ API-GATEWAY                              │
│   ✅ api-gateway-1   (192.168.1.30:8080)│
└─────────────────────────────────────────┘
```

## ⚙️ Configuración Avanzada

### Timeouts y Reintentos

```yaml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 30    # Heartbeat cada 30s
    lease-expiration-duration-in-seconds: 90 # Expira después de 90s
  client:
    registry-fetch-interval-seconds: 30      # Actualiza registro cada 30s
    
ribbon:
  ConnectTimeout: 3000    # 3 segundos para conectar
  ReadTimeout: 10000      # 10 segundos para leer
  MaxAutoRetries: 1       # Reintentar 1 vez
  MaxAutoRetriesNextServer: 1  # Probar siguiente servidor
```

### Zonas de Disponibilidad

Para alta disponibilidad en múltiples regiones:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka-us-east:8761/eureka/,
                   http://eureka-us-west:8762/eureka/
  instance:
    metadata-map:
      zone: us-east-1a
```

## 🚨 Problemas Comunes

### 1. **Servicio no se registra**

```
Verificar:
✓ @EnableDiscoveryClient en la clase principal
✓ Eureka Server está corriendo
✓ URL de Eureka es correcta
✓ No hay firewall bloqueando
```

### 2. **Servicio aparece como DOWN**

```
Verificar:
✓ Actuator está habilitado
✓ Health endpoint responde
✓ Heartbeat se está enviando
✓ Red es estable
```

### 3. **Load Balancing no funciona**

```
Verificar:
✓ @LoadBalanced en RestTemplate
✓ Nombre del servicio es correcto
✓ Hay múltiples instancias disponibles
```

## 🔒 Seguridad

### Autenticación en Eureka

```yaml
# Eureka Server
spring:
  security:
    user:
      name: admin
      password: secret

# Eureka Client
eureka:
  client:
    service-url:
      defaultZone: http://admin:secret@localhost:8761/eureka/
```

## 🆚 Comparación de Tecnologías

| Característica | Eureka | Consul | Zookeeper |
|----------------|--------|--------|-----------|
| **Lenguaje** | Java | Go | Java |
| **Protocolo** | HTTP | HTTP/DNS | Custom |
| **Health Checks** | ✅ | ✅ | ✅ |
| **Key-Value Store** | ❌ | ✅ | ✅ |
| **DNS** | ❌ | ✅ | ❌ |
| **UI** | ✅ | ✅ | ❌ |
| **Complejidad** | Baja | Media | Alta |

## 🎯 ¿Cuándo usar Service Discovery?

### ✅ Úsalo cuando:
- Tienes microservicios
- Las IPs cambian dinámicamente (Docker, K8s)
- Necesitas auto-scaling
- Quieres alta disponibilidad
- Usas cloud (AWS, Azure, GCP)

### ❌ No lo uses cuando:
- Tienes pocas instancias estáticas
- Las IPs no cambian
- Aplicación monolítica
- Infraestructura simple

## 💻 Ejemplo Completo

### Proyecto Maven Completo

```xml
<!-- Eureka Server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

<!-- Eureka Client (en cada microservicio) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Código Completo User Service

```java
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

```yaml
spring:
  application:
    name: user-service

server:
  port: 8081

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${random.value}
```

## 📚 Siguiente Paso

Ahora que entiendes Service Discovery, puedes implementar un sistema completo.

➡️ Continúa aprendiendo sobre patrones de resiliencia como Circuit Breaker

---

## 💡 Recuerda

> Service Discovery es como un directorio telefónico que se actualiza automáticamente. Los servicios se registran al iniciar y se consultan cuando necesitan comunicarse con otros.

## 🎓 Resumen

1. **Service Registry**: Directorio central (Eureka Server)
2. **Registration**: Servicios se registran automáticamente
3. **Discovery**: Servicios consultan el registro para encontrar otros
4. **Health Checks**: Verificación constante de disponibilidad
5. **Load Balancing**: Distribución automática de carga

Con Service Discovery, tu arquitectura de microservicios se vuelve verdaderamente dinámica y resiliente. 🚀
