# 📬 Notification Service

Microservicio de notificaciones que consume eventos de Kafka y envía notificaciones cuando se crean órdenes.

## 🎯 Propósito

Este servicio demuestra el patrón **Event-Driven Architecture**:
- 📨 Escucha eventos de orden creada desde Kafka
- 🚀 Procesa el evento sin bloquear order-service
- ✉️ Envía una notificación al cliente

## 🏛️ Arquitectura Hexagonal

```
┌─────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE                    │
│                                                     │
│  ┌─────────────────────────────────────────────┐  │
│  │  In: KafkaConsumerAdapter                   │  │
│  │  └─> Escucha topic "order-events"           │  │
│  └─────────────────────────────────────────────┘  │
│                     │                              │
│                     ▼                              │
│  ┌─────────────────────────────────────────────┐  │
│  │   APPLICATION                                │  │
│  │   ProcessOrderEventUseCase (Puerto)         │  │
│  │   NotificationService (Implementación)      │  │
│  └─────────────────────────────────────────────┘  │
│                     │                              │
│                     ▼                              │
│  ┌─────────────────────────────────────────────┐  │
│  │  Out: EmailAdapter                          │  │
│  │  └─> SendNotificationPort (Simulado)        │  │
│  └─────────────────────────────────────────────┘  │
│                                                     │
└─────────────────────────────────────────────────────┘
         │                            │
         ▼                            ▼
    DOMAIN CORE              EXTERNAL SYSTEMS
    ├─ Notification         └─ Email Service
    ├─ OrderCreatedEvent        (simulado)
    └─ Value Objects
```

## 📋 Estructura de Directorios

```
notification-service/
├── src/
│   ├── main/
│   │   ├── java/com/microservices/notification/
│   │   │   ├── NotificationServiceApplication.java
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   └── Notification.java
│   │   │   │   └── event/
│   │   │   │       └── OrderCreatedEvent.java
│   │   │   ├── application/
│   │   │   │   ├── port/
│   │   │   │   │   ├── in/
│   │   │   │   │   │   └── ProcessOrderEventUseCase.java
│   │   │   │   │   └── out/
│   │   │   │   │       └── SendNotificationPort.java
│   │   │   │   └── service/
│   │   │   │       └── NotificationService.java
│   │   │   └── infrastructure/
│   │   │       ├── adapter/
│   │   │       │   ├── in/kafka/
│   │   │       │   │   └── KafkaConsumerAdapter.java
│   │   │       │   └── out/
│   │   │       │       └── EmailAdapter.java
│   │   │       └── config/
│   │   │           └── KafkaConsumerConfig.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/microservices/notification/
└── pom.xml
```

## 🚀 Inicio Rápido

### 1. Levantar Kafka con Docker

```bash
# En la raíz del proyecto
docker-compose up -d

# Verificar que está corriendo
docker-compose ps

# Acceder a Kafdrop (UI)
# http://localhost:9000
```

### 2. Compilar el Servicio

```bash
cd notification-service
mvn clean install
```

### 3. Ejecutar el Servicio

```bash
mvn spring-boot:run
```

O desde el IDE ejecutando: `NotificationServiceApplication.main()`

### 4. Verificar que está corriendo

```bash
# Debe responder en el health check
curl http://localhost:8085/api/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP",
  "components": {
    "kafkaListener": {
      "status": "UP",
      "details": {
        "brokerId": 1
      }
    }
  }
}
```

## 🔄 Flujo de Funcionamiento

### Paso 1: Order Service Crea una Orden

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 99.99
  }'
```

### Paso 2: Order Service Publica Evento

En los logs de order-service verás:
```
📤 Publicando evento OrderCreated a Kafka - Orden: order-123
✅ Evento publicado exitosamente - Orden: order-123
```

### Paso 3: Notification Service Consume Evento

En los logs de notification-service verás:
```
🎧 Mensaje recibido de Kafka - Partición: 0, Offset: 0
📨 Evento de orden recibido: order-123
📩 Procesando evento de orden creada: order-123
```

### Paso 4: Se Envía la Notificación

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📬 EMAIL ENVIADO (SIMULADO)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Para: user@email.com
Asunto: 📦 Tu orden ha sido creada!
Mensaje:
Hola,

Tu orden #order-123 ha sido procesada exitosamente.
Monto: $99.99
Items: Productos varios

Gracias por tu compra!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

## 📊 Monitoreo con Kafdrop

Accede a http://localhost:9000 para ver:

1. **Topics**
   - Ver el topic "order-events"
   - Ver particiones
   - Ver mensajes

2. **Consumer Groups**
   - `notification-service-group`
   - Ver lag (mensajes no procesados)

3. **Mensajes**
   - Visualizar los eventos en tiempo real
   - Ver el JSON deserializado

## ⚙️ Configuración (application.yml)

### Kafka Consumer
```yaml
spring.kafka.bootstrap-servers: localhost:9092
# Donde está el broker de Kafka

spring.kafka.consumer.group-id: notification-service-group
# Grupo de consumidores (agrupa múltiples instancias)

spring.kafka.consumer.auto-offset-reset: earliest
# Qué hacer si no hay offset guardado:
# - earliest: Leer desde el principio
# - latest: Leer solo nuevos mensajes
```

### Topics
- `order-events`: Topic donde se publican órdenes creadas

### Logging
```yaml
logging.level.com.microservices: DEBUG
logging.level.org.springframework.kafka: DEBUG
```

## 🧪 Testing

### Producir Mensajes de Test Manualmente

```bash
# Conectar a Kafka
docker exec -it kafka-broker kafka-console-producer \
  --topic order-events \
  --bootstrap-server localhost:9092

# Escribe este JSON y presiona Enter:
{"orderId":"order-test-123","customerId":"cust-789","customerEmail":"test@email.com","totalAmount":49.99,"description":"Test Order","createdAt":"2024-01-20T10:30:00","eventType":"OrderCreated"}
```

Ver en logs de notification-service:
```
📨 Evento de orden recibido: order-test-123
✅ Notificación enviada exitosamente para orden: order-test-123
```

### Ver Consumer Group Status

```bash
docker exec kafka-broker kafka-consumer-groups \
  --describe \
  --group notification-service-group \
  --bootstrap-server localhost:9092
```

Esperado:
```
GROUP                       TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG
notification-service-group order-events    0          1               1               0
notification-service-group order-events    1          0               0               0
notification-service-group order-events    2          0               0               0
```

## 🔧 Troubleshooting

### Problema: "Connection to node -1 could not be established"

**Causa**: Kafka no está corriendo

**Solución**:
```bash
docker-compose ps
docker-compose up -d
```

### Problema: "Topic 'order-events' does not exist"

**Causa**: El topic no ha sido creado

**Solución**: Spring creará automáticamente si está habilitado, o crear manualmente:
```bash
docker exec kafka-broker kafka-topics --create \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

### Problema: "org.apache.kafka.common.errors.SerializationException"

**Causa**: Error al deserializar el mensaje

**Solución**: Verificar que el JSON cumple con el formato de `OrderCreatedEvent`:
- Todos los campos deben estar presentes
- Los tipos deben coincidir
- Usar `@JsonProperty` en los DTOs

### Problema: "Group notification-service-group has no active members"

**Causa**: El servicio se apagó pero el grupo sigue registrado

**Solución**: Esperar 30 segundos o reiniciar el servicio

## 📈 Escalabilidad

### ¿Qué pasa si hay 2 instancias de notification-service?

```yaml
# Instancia 1: Puerto 8085
# Instancia 2: Puerto 8086

# Ambas en el mismo grupo: "notification-service-group"
```

Kafka automáticamente:
- Asigna Partition 0 a Instancia 1
- Asigna Partition 1 a Instancia 2
- Asigna Partition 2 a Instancia 1

Resultado: **Cada evento se procesa UNA VEZ** aunque haya múltiples instancias

## 🔐 Seguridad

En producción (NO en este ejemplo):
- Usar SSL/TLS para Kafka
- Autenticación SASL
- Validar tokens JWT
- Rate limiting

## 📚 Referencias

- [Documentación Kafka](https://kafka.apache.org/documentation/)
- [Spring Kafka](https://spring.io/projects/spring-kafka)
- [Arquitectura Hexagonal](../docs/02-arquitectura-hexagonal.md)
- [Event-Driven Architecture](../docs/07-event-driven-kafka.md)

## 📞 Puertos

| Servicio | Puerto | URL |
|----------|--------|-----|
| Notification Service | 8085 | http://localhost:8085 |
| Kafka Broker | 9092 | localhost:9092 (interno) |
| Kafka External | 29092 | localhost:29092 (host) |
| Zookeeper | 2181 | localhost:2181 |
| Kafdrop UI | 9000 | http://localhost:9000 |

## 🛑 Detener Servicios

```bash
# Detener notification-service
# (Presionar Ctrl+C en la terminal)

# Detener Kafka y servicios
docker-compose down

# Limpiar volúmenes (borrar datos)
docker-compose down -v
```

