# 🚀 Event-Driven Architecture con Kafka (Explicado con Peras y Manzanas)

## 🤔 ¿Qué es Event-Driven Architecture?

### 📞 La Forma Antigua: Llamadas Directas (Síncrono)

Imagina que llamas a tu amigo por teléfono cada vez que algo importante sucede:

```
┌──────────────┐                    ┌──────────────┐
│ Order Service│─────HTTP CALL─────>│Notification  │
│              │  "¡Hey, orden #123"│ Service      │
│              │<─────RESPUESTA─────│              │
└──────────────┘                    └──────────────┘
        │
        └─> PROBLEMA: Si Notification está caído, order-service falla
```

**Desventajas**:
- ❌ Orden y Notificación están fuertemente acopladas
- ❌ Si notification-service cae, order-service también falla
- ❌ Bajo escalabilidad
- ❌ Difícil de testear

### 📰 La Forma Moderna: Event-Driven (Asíncrono con Kafka)

Ahora imagina que publicas un anuncio en el periódico ("He creado una orden"):

```
┌──────────────┐      PUBLICA      ┌───────────────┐
│ Order Service│─────EVENTO────────>│   KAFKA       │
│              │   "OrderCreated"   │  (Periódico)  │
└──────────────┘                    └───────────────┘
                                            │
                    ┌───────────────────────┼───────────────────────┐
                    │                       │                       │
            ┌───────▼──────┐        ┌───────▼──────┐        ┌───────▼──────┐
            │Notification  │        │ Analytics    │        │ Email        │
            │ Service      │        │ Service      │        │ Service      │
            │ "¡Notificación│       │ "Registrar   │        │ "Enviar      │
            │  enviada!"    │       │  evento"     │        │  confirmación"│
            └──────────────┘        └──────────────┘        └──────────────┘
```

**Ventajas**:
- ✅ Desacoplamiento: order-service NO depende de los demás
- ✅ Resiliencia: Si notification-service falla, el evento sigue en Kafka
- ✅ Escalabilidad: Múltiples servicios pueden reaccionar al mismo evento
- ✅ Flexible: Puedes añadir nuevos servicios sin tocar order-service

---

## 🍎 ¿Qué es Kafka?

### Definición Simple

**Kafka = Un periódico distribuido que guarda historias**

Características:
- 📰 **Topics**: Categorías de noticias (ej: "order-events", "user-registered")
- 📖 **Messages**: Cada noticia es un mensaje
- 👥 **Producers**: Quien escribe la noticia (order-service)
- 👁️ **Consumers**: Quien lee la noticia (notification-service)
- 🏗️ **Broker**: El periódico en sí
- 📚 **Partitions**: Múltiples copias para escalabilidad

### Arquitectura de Kafka

```
┌─────────────────────────────────────────────────────────────────┐
│                    KAFKA CLUSTER                               │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Topic: "order-events"                                    │  │
│  │                                                          │  │
│  │  Partition 0: [msg1] [msg2] [msg3] [msg4] [msg5]       │  │
│  │  Partition 1: [msg1] [msg2] [msg3] [msg4]              │  │
│  │  Partition 2: [msg1] [msg2] [msg3] [msg4] [msg5] [msg6]│  │
│  │                                                          │  │
│  │  ↑ Cada partición es como un "registro de eventos"      │  │
│  │  ↑ Se distribuyen entre brokers para escalabilidad      │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  Replicación: Cada mensaje se copia en múltiples brokers       │
│  para garantizar que no se pierde si uno falla               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📊 Flujo Completo de Nuestro Proyecto

### Escenario: Crear una Orden

```
1. USUARIO → POST /api/orders
                    │
                    ▼
2. OrderController recibe solicitud
                    │
                    ▼
3. OrderService.create()
   ├─ Valida usuario (user-service)
   ├─ Crea orden en BD
   │
   └─ 🎯 NUEVO: Publica evento a Kafka
       │
       │ OrderCreatedEvent:
       │ {
       │   "orderId": "order-123",
       │   "customerId": "cust-456",
       │   "customerEmail": "user@email.com",
       │   "totalAmount": 99.99,
       │   "createdAt": "2024-01-20T10:30:00"
       │ }
       │
       ▼
4. KafkaProducerAdapter envía a Kafka
   (topic: "order-events")
                    │
       ┌────────────┴────────────┐
       │                         │
       ▼                         ▼
5. NotificationService        (Otros servicios
   consume evento              podrían consumir aquí)
       │
       ├─ Recibe: OrderCreatedEvent
       ├─ Procesa en: NotificationService
       └─ Envía: EmailAdapter
           │
           ▼
6. Simula envío de email
   ✅ "Notificación enviada"
```

---

## 🔧 Conceptos Clave de Kafka

### 1. **Topic** (Tema)

Un topic es como un canal de comunicación. En nuestro proyecto tenemos:

```yaml
Topic: order-events
├─ Contiene: Eventos de órdenes creadas
├─ Retencion: 7 días (configurable)
├─ Particiones: 3
│   └─ Para distribuir carga entre consumers
└─ Replicacion: 1
    └─ Copias del mensaje (en producción: 3+)
```

**Crear un topic manualmente:**
```bash
docker exec kafka-broker kafka-topics --create \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

### 2. **Producer** (Productor)

Quien ENVÍA mensajes a Kafka.

En nuestro proyecto:
- **Classe**: `KafkaProducerAdapter`
- **Topic**: "order-events"
- **Cuando**: Cuando se crea una orden
- **Qué envía**: `OrderCreatedEvent`

```java
// Ejemplo de producir un mensaje
kafkaTemplate.send("order-events", orderCreatedEvent);
```

### 3. **Consumer** (Consumidor)

Quien LEE mensajes de Kafka.

En nuestro proyecto:
- **Clase**: `KafkaConsumerAdapter`
- **Topic**: "order-events"
- **Group**: "notification-service-group"
- **Qué hace**: Escucha eventos y envía notificaciones

```java
@KafkaListener(topics = "order-events", groupId = "notification-service-group")
public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
    // Procesar el evento
}
```

### 4. **Consumer Group** (Grupo de Consumidores)

Agrupa múltiples instancias del mismo servicio.

```
Escenario: Tenemos 2 instancias de notification-service

Topic: order-events
├─ Partition 0: MSG1, MSG2, MSG3
├─ Partition 1: MSG4, MSG5, MSG6
├─ Partition 2: MSG7, MSG8, MSG9

Group: notification-service-group
├─ Instancia 1 procesa: Partition 0, Partition 1
└─ Instancia 2 procesa: Partition 2

Resultado: Cada mensaje se procesa UNA SOLA VEZ (aunque haya 2 instancias)
```

### 5. **Offset** (Desplazamiento)

Es como el "punto de lectura" en el periódico.

```
Topic: order-events
│
├─ Offset 0: OrderCreatedEvent (order-1)  ← ¿Dónde está ahora el consumer?
├─ Offset 1: OrderCreatedEvent (order-2)
├─ Offset 2: OrderCreatedEvent (order-3)
├─ Offset 3: OrderCreatedEvent (order-4)  ← Nuevo mensaje
│
Consumer se acuerda: "Ya procesé hasta offset 2"
Próxima lectura: Comienza en offset 3
```

**Auto Offset Reset** (en application.yml):
```yaml
spring.kafka.consumer.auto-offset-reset: earliest
```
- `earliest`: Si es la primera vez, leer desde el principio
- `latest`: Si es la primera vez, leer solo nuevos mensajes

### 6. **Partitions** (Particiones)

Dividen los mensajes de un topic para paralelismo.

```
Topic sin particione (1 partition):
┌─────────────────────────────────┐
│ Consumer 1: Lee TODO            │
│ (es el cuello de botella)       │
└─────────────────────────────────┘

Topic con 3 particiones:
┌──────────┐  ┌──────────┐  ┌──────────┐
│Partition │  │Partition │  │Partition │
│    0     │  │    1     │  │    2     │
│ Consumer │  │ Consumer │  │ Consumer │
│    1     │  │    2     │  │    3     │
└──────────┘  └──────────┘  └──────────┘

Resultado: Procesamiento paralelo, mejor throughput
```

---

## 🛠️ Configuración en Nuestro Proyecto

### En `order-service` (Productor)

**application.yml:**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all  # Esperar confirmación de todos
      retries: 3
```

**KafkaProducerConfig.java:**
- Define cómo serializar (convertir objeto Java a JSON)
- Configura garantías de entrega (acks)
- Configura reintentos

**KafkaProducerAdapter.java:**
- Implementa `PublishOrderEventPort`
- Usa `KafkaTemplate` para enviar
- Convierte la orden en evento

### En `notification-service` (Consumidor)

**application.yml:**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-service-group
      auto-offset-reset: earliest
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```

**KafkaConsumerConfig.java:**
- Define cómo deserializar (convertir JSON a objeto Java)
- Configura group-id
- Configura auto-offset-reset

**KafkaConsumerAdapter.java:**
- Escucha el topic "order-events"
- Deserializa automáticamente a `OrderCreatedEvent`
- Llama al caso de uso `ProcessOrderEventUseCase`

---

## 🚀 Garantías de Entrega en Kafka

### 1. **At-Most-Once** (Acks = 0)
```
Velocidad:   ⚡⚡⚡ Muy rápido
Confiabilidad: ❌ Puede perderse
```

### 2. **At-Least-Once** (Acks = 1)
```
Velocidad:   ⚡⚡ Medio
Confiabilidad: ⚠️ Puede duplicarse
```

### 3. **Exactly-Once** (Acks = all + Consumer idempotente)
```
Velocidad:   ⚡ Lento
Confiabilidad: ✅ Perfecto
```

**En nuestro proyecto usamos: Acks = all**
```java
configProps.put(ProducerConfig.ACKS_CONFIG, "all");
```

---

## 📋 Manejo de Topics

### Ver todos los topics
```bash
docker exec kafka-broker kafka-topics --list \
  --bootstrap-server localhost:9092
```

### Describir un topic
```bash
docker exec kafka-broker kafka-topics --describe \
  --topic order-events \
  --bootstrap-server localhost:9092
```

Salida:
```
Topic: order-events     TopicId: XYZ     PartitionCount: 3       ReplicationFactor: 1
Topic: order-events     Partition: 0    Leader: 1       Replicas: 1     Isr: 1
Topic: order-events     Partition: 1    Leader: 1       Replicas: 1     Isr: 1
Topic: order-events     Partition: 2    Leader: 1       Replicas: 1     Isr: 1
```

### Consumir mensajes desde un topic
```bash
docker exec kafka-broker kafka-console-consumer \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

### Producir mensajes de test
```bash
docker exec -it kafka-broker kafka-console-producer \
  --topic order-events \
  --bootstrap-server localhost:9092
```

Luego escribe:
```json
{"orderId":"order-123","customerId":"cust-456","customerEmail":"user@email.com","totalAmount":99.99,"createdAt":"2024-01-20T10:30:00","eventType":"OrderCreated"}
```

### Ver consumer groups
```bash
docker exec kafka-broker kafka-consumer-groups \
  --list \
  --bootstrap-server localhost:9092
```

### Ver offset de un consumer group
```bash
docker exec kafka-broker kafka-consumer-groups \
  --describe \
  --group notification-service-group \
  --bootstrap-server localhost:9092
```

Salida:
```
GROUP                       TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG
notification-service-group order-events    0          5               5               0
notification-service-group order-events    1          3               3               0
notification-service-group order-events    2          4               4               0
```

---

## 🔄 Flujo Típico en Event-Driven

```
1. EVENTO OCURRE
   └─> "Se creó orden #123"

2. PRODUCTOR PUBLICA
   └─> kafkaTemplate.send("order-events", event)

3. KAFKA ALMACENA
   └─> Topic "order-events" con múltiples particiones

4. CONSUMIDOR CONSUME
   └─> @KafkaListener escucha y procesa

5. PROCESAMIENTO
   └─> NotificationService envía email

6. CONFIRMACIÓN
   └─> Consumer confirma que procesó el mensaje
   └─> Kafka actualiza el offset
```

---

## 🛡️ Manejo de Errores

### Reintentos Automáticos
```yaml
spring:
  kafka:
    producer:
      retries: 3  # Reintentar 3 veces si falla
```

### Dead Letter Topic (DLT)
Para mensajes que fallan después de reintentos:

```java
@KafkaListener(
  topics = "order-events",
  groupId = "notification-service-group",
  containerFactory = "kafkaListenerContainerFactory"
)
@DltHandler  // Si falla, va a "order-events.DLT"
public void handleFailure(OrderCreatedEvent event, Exception e) {
    log.error("Mensaje fallido: {}", event, e);
}
```

---

## 📊 Métricas Útiles

### Ver lag del consumer
```
LAG = LOG-END-OFFSET - CURRENT-OFFSET

Si LAG > 0:
  └─> El consumer está atrasado
  └─> Hay mensajes no procesados

Si LAG = 0:
  └─> El consumer está al día
  └─> Todo procesado
```

---

## 🔗 Relación con Arquitectura Hexagonal

```
┌─────────────────────────────────────────┐
│         HEXAGONAL ARCHITECTURE          │
│                                         │
│  Domain Layer:                          │
│  ├─ OrderCreatedEvent (Evento dominio) │
│  ├─ Notification (Entidad)             │
│  └─ NotificationStatus (Value Object)  │
│                                         │
│  Application Layer:                     │
│  ├─ ProcessOrderEventUseCase (Puerto)  │
│  ├─ SendNotificationPort (Puerto)      │
│  └─ NotificationService (Case Use)     │
│                                         │
│  Infrastructure Layer:                  │
│  ├─ KafkaConsumerAdapter (In)          │
│  ├─ EmailAdapter (Out)                 │
│  ├─ KafkaConsumerConfig                │
│  └─ KafkaProducerConfig                │
└─────────────────────────────────────────┘
```

---

## ✅ Checklist de Implementación

- [x] Crear notification-service con estructura hexagonal
- [x] Crear OrderCreatedEvent en ambos servicios
- [x] Crear puertos para Kafka (In/Out)
- [x] Crear adaptadores Kafka (Consumer/Producer)
- [x] Configurar Kafka en application.yml
- [x] Docker compose con Kafka + Zookeeper
- [x] Crear NotificationService (caso de uso)
- [x] Crear EmailAdapter (simulado)
- [x] Documentación completa

---

## 🚀 Próximos Pasos

1. **Levantar Kafka**: `docker-compose up -d`
2. **Compilar servicios**: `mvn clean install`
3. **Iniciar notification-service**: `mvn spring-boot:run`
4. **Iniciar order-service**: `mvn spring-boot:run`
5. **Crear orden**: `POST http://localhost:8082/api/orders`
6. **Ver evento en Kafka**: http://localhost:9000 (Kafdrop)

