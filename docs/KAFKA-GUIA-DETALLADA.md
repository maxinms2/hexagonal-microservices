# 📚 GUÍA DETALLADA: KAFKA Y DOCKER-COMPOSE

> **Objetivo**: Explicar de manera clara y elemental cómo funciona Kafka en nuestro proyecto

---

## 📖 TABLA DE CONTENIDOS

1. [¿Qué es Kafka?](#qué-es-kafka)
2. [Conceptos Elementales](#conceptos-elementales)
3. [Arquitectura de Kafka](#arquitectura-de-kafka)
4. [Componentes en el docker-compose](#componentes-en-el-docker-compose)
5. [Cómo funcionan juntos](#cómo-funcionan-juntos)
6. [Puertos y Conexiones](#puertos-y-conexiones)
7. [Ejemplos Prácticos](#ejemplos-prácticos)

---

## ¿QUÉ ES KAFKA?

### La Idea Principal

Imagina que tienes varias personas en una sala:
- **Juan** (order-service) quiere comunicar "Acabo de crear un pedido"
- **María** (notification-service) quiere saber "¿Se creó algún pedido?"

**Sin Kafka**: Juan tendría que llamar directamente a María. Si María no está, el mensaje se pierde.

**Con Kafka**: Juan deja un mensaje en un tablón de anuncios (Kafka). María puede ver el mensaje cuando quiera.

### Definición Técnica

**Kafka es un sistema de mensajería distribuido** que actúa como un intermediario entre servicios. Permite que:
- Un servicio **PRODUZCA** (envíe) eventos/mensajes
- Otros servicios **CONSUMAN** (reciban) esos mensajes
- Los mensajes se guarden temporalmente para procesamiento posterior

---

## CONCEPTOS ELEMENTALES

### 1. **EVENTOS (Mensajes)**

Un evento es una notificación de que algo ocurrió.

```json
{
  "eventType": "OrderCreatedEvent",
  "orderId": "12345",
  "userId": "user-001",
  "totalAmount": 99.99,
  "timestamp": "2026-01-20T10:30:00Z"
}
```

**En nuestro proyecto**: Cuando order-service crea un pedido, genera un evento OrderCreatedEvent.

---

### 2. **TOPICS (Temas)**

Un topic es como un **canal de radio** o un **grupo de WhatsApp**. Es donde se publican los eventos.

**Ejemplo de topics en nuestro proyecto**:
- `order-events` → eventos de pedidos
- `user-events` → eventos de usuarios
- `notification-events` → eventos de notificaciones

**Características**:
- Un topic puede tener múltiples productores (muchos servicios enviando eventos)
- Un topic puede tener múltiples consumidores (muchos servicios recibiendo eventos)
- Los mensajes en un topic se guardan durante un tiempo (retención)

```
Topic: order-events
┌─────────────────────────────────────┐
│ Evento 1: Pedido creado             │
│ Evento 2: Pedido pagado             │
│ Evento 3: Pedido enviado            │
│ Evento 4: Pedido entregado          │
└─────────────────────────────────────┘
```

---

### 3. **PRODUCTORES (Producers)**

Un productor es un servicio que **ENVÍA** eventos a un topic.

**En nuestro proyecto**:
- `order-service` es productor → envía eventos cuando crea/modifica pedidos
- `user-service` es productor → envía eventos cuando crea/modifica usuarios

```
order-service
     │
     ├─→ Crea un pedido
     │
     └─→ PRODUCE evento: OrderCreatedEvent
              │
              v
         [order-events topic en Kafka]
```

---

### 4. **CONSUMIDORES (Consumers)**

Un consumidor es un servicio que **RECIBE** eventos de un topic.

**En nuestro proyecto**:
- `notification-service` es consumidor → recibe eventos de pedidos
- Puede haber múltiples instancias del mismo consumidor

```
[order-events topic en Kafka]
              │
              ├─→ notification-service recibe: OrderCreatedEvent
              │        (envía email de confirmación)
              │
              ├─→ analytics-service recibe: OrderCreatedEvent
              │        (analiza estadísticas)
              │
              └─→ inventory-service recibe: OrderCreatedEvent
                       (actualiza inventario)
```

---

### 5. **PARTICIONES (Partitions)**

Las particiones son **divisiones** de un topic para aumentar rendimiento.

```
Topic: order-events
├─ Partición 0: [Evento 1, Evento 4, Evento 7, ...]
├─ Partición 1: [Evento 2, Evento 5, Evento 8, ...]
└─ Partición 2: [Evento 3, Evento 6, Evento 9, ...]
```

**¿Por qué?**
- **Paralelismo**: Múltiples consumidores pueden leer simultáneamente
- **Velocidad**: Los datos se distribuyen para procesamiento rápido
- **Escalabilidad**: Cuando crece el volumen, es fácil agregar particiones

**En nuestro docker-compose**: 
```yaml
--partitions 3  # Dividimos el topic en 3 particiones
```

---

### 6. **REPLICACIÓN (Replication)**

La replicación es hacer **copias** de los datos para seguridad.

```
Broker 1: [Copia del topic]
Broker 2: [Copia del topic]
Broker 3: [Copia del topic]
```

**Si un broker se cae**, los datos aún existen en otro broker.

**En nuestro docker-compose**:
```yaml
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
--replication-factor 1  # Solo 1 copia (OK para desarrollo, NO para producción)
```

---

### 7. **CONSUMER GROUPS (Grupos de Consumo)**

Un consumer group es un **grupo de consumidores** que trabajan juntos.

```
Consumer Group: notification-service-group
├─ notification-service (instancia 1)
├─ notification-service (instancia 2)
└─ notification-service (instancia 3)

Todos comparten el trabajo de consumir el topic order-events
```

**Beneficio**: Si tenemos 3 instancias de notification-service, cada una consume de particiones diferentes en paralelo.

---

### 8. **OFFSETS**

Un offset es una **posición** en el topic. Es como una coordenada GPS.

```
Topic: order-events
Posición (offset) 0: OrderCreatedEvent (Usuario123, Pedido789)
Posición (offset) 1: OrderCreatedEvent (Usuario456, Pedido790)
Posición (offset) 2: OrderCreatedEvent (Usuario789, Pedido791)
```

**Kafka recuerda**: "El servicio notification-service ya consumió hasta el offset 5". Así, si se reinicia, no procesa eventos viejos.

---

## ARQUITECTURA DE KAFKA

### Flujo Completo

```
┌────────────────────────────────────────────────────────────────────┐
│                          KAFKA CLUSTER                              │
├────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────┐                                                │
│  │   ZOOKEEPER     │ ← Coordina todo el cluster                     │
│  │  (Maestro)      │                                                │
│  └─────────────────┘                                                │
│         │                                                            │
│         │                                                            │
│  ┌──────┴──────────────────────────────────────────────────────┐   │
│  │                                                               │   │
│  │        Topic: order-events                                   │   │
│  │  ┌──────────────┬──────────────┬──────────────┐              │   │
│  │  │ Partición 0  │ Partición 1  │ Partición 2  │              │   │
│  │  │ [Eventos]    │ [Eventos]    │ [Eventos]    │              │   │
│  │  └──────────────┴──────────────┴──────────────┘              │   │
│  │                                                               │   │
│  └───────────────────────────────────────────────────────────────┘   │
│                                                                      │
└────────────────────────────────────────────────────────────────────┘
         ▲                                          ▲
         │                                          │
    PRODUCE                                    CONSUME
         │                                          │
         │                                          │
    ┌────────────┐                            ┌──────────────────┐
    │ order-      │                            │ notification-    │
    │ service     │                            │ service          │
    │ (Producer)  │                            │ (Consumer)       │
    └────────────┘                            └──────────────────┘
```

---

## COMPONENTES EN EL DOCKER-COMPOSE

Ahora veamos cada servicio en el `docker-compose.yml`:

### 1. **ZOOKEEPER** 🐘

```yaml
zookeeper:
  image: confluentinc/cp-zookeeper:7.5.0
  container_name: kafka-zookeeper
  environment:
    ZOOKEEPER_CLIENT_PORT: 2181
    ZOOKEEPER_SERVER_ID: 1
    ZOOKEEPER_TICK_TIME: 2000
  ports:
    - "2181:2181"
```

#### ¿Qué es Zookeeper?

Zookeeper es el **"maestro director"** de Kafka. Coordina:

- **¿Cuáles brokers están vivos?** (Mantiene registro)
- **¿Quién es el líder de cada partición?** (Elige líderes)
- **¿Cuál es la configuración del cluster?** (Almacena configuración)
- **¿Quién es el coordinador de consumer groups?** (Coordina consumidores)

#### Variables de Entorno Explicadas

| Variable | Valor | Significado |
|----------|-------|-------------|
| `ZOOKEEPER_CLIENT_PORT` | `2181` | Puerto donde escucha Zookeeper para conexiones |
| `ZOOKEEPER_SERVER_ID` | `1` | ID único del servidor (en un cluster sería 1, 2, 3...) |
| `ZOOKEEPER_TICK_TIME` | `2000` | Latido del corazón en milisegundos (cada 2 segundos) |

#### Puertos

- **2181**: Puerto donde otros servicios (Kafka, aplicaciones) se conectan

#### Health Check

```yaml
healthcheck:
  test: [ "CMD", "echo", "ruok", "|", "nc", "127.0.0.1", "2181" ]
```

Verifica que Zookeeper esté **"ruok"** (are you ok?). Si no responde, el contenedor se marca como no saludable.

---

### 2. **KAFKA** 🚀

```yaml
kafka:
  image: confluentinc/cp-kafka:7.5.0
  container_name: kafka-broker
  depends_on:
    zookeeper:
      condition: service_healthy
  environment:
    KAFKA_BROKER_ID: 1
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
    # ... más configuraciones
```

#### ¿Qué es un Kafka Broker?

Un broker es el **servidor central** que:
- Recibe mensajes de productores
- Almacena mensajes
- Envía mensajes a consumidores
- Se coordina con Zookeeper

#### Variables de Entorno Explicadas

| Variable | Valor | Significado |
|----------|-------|-------------|
| `KAFKA_BROKER_ID` | `1` | ID único del broker (en cluster sería 1, 2, 3...) |
| `KAFKA_ZOOKEEPER_CONNECT` | `zookeeper:2181` | Dónde encontrar Zookeeper |
| `KAFKA_ADVERTISED_LISTENERS` | `PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092` | Dónde pueden conectarse los clientes |
| `KAFKA_AUTO_CREATE_TOPICS_ENABLE` | `true` | Auto-crear topics cuando se usan (útil en desarrollo) |
| `KAFKA_LOG_RETENTION_HOURS` | `168` | Guardar mensajes durante 7 días |

#### LISTENERS (Muy Importante ⚠️)

Los listeners son **direcciones** donde se puede conectar a Kafka:

```
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
```

- **`PLAINTEXT://kafka:9092`**: 
  - Dirección **INTERNA** (dentro de Docker)
  - Usado por: otros contenedores (como notification-service)
  - Nombre: `kafka` (DNS interno de Docker)

- **`PLAINTEXT_HOST://localhost:29092`**:
  - Dirección **EXTERNA** (desde tu máquina Windows)
  - Usado por: aplicaciones en tu máquina
  - Nombre: `localhost` (tu computadora)

```
┌─────────────────────────────────────────────┐
│          DOCKER NETWORK (kafka-network)     │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │   Kafka Broker                       │   │
│  │                                      │   │
│  │  Listener 1: kafka:9092 (interno)   │   │
│  │  Listener 2: localhost:29092 (ext)  │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │   notification-service               │   │
│  │   Se conecta a: kafka:9092           │   │
│  └──────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
         │
         │ (puente)
         │
┌────────┴──────────────────────────────────┐
│   Tu Computadora Windows                  │
│                                           │
│  Puedes conectarte a: localhost:29092    │
│  (Para herramientas de prueba, etc)      │
└──────────────────────────────────────────┘
```

#### Puertos

| Puerto | Tipo | Uso |
|--------|------|-----|
| `9092` | Interno | Contenedores se conectan aquí (kafka:9092) |
| `29092` | Externo | Tu máquina se conecta aquí (localhost:29092) |

#### Health Check

```yaml
healthcheck:
  test: [ "CMD", "kafka-broker-api-versions", "--bootstrap-server", "kafka:9092" ]
```

Verifica que Kafka responda correctamente a solicitudes de API.

---

### 3. **KAFDROP** 🎨

```yaml
kafdrop:
  image: obsidiandynamics/kafdrop:latest
  container_name: kafka-ui
  depends_on:
    kafka:
      condition: service_healthy
  environment:
    KAFKA_BROKERCONNECT: kafka:9092
    ZK_HOSTS: zookeeper:2181
  ports:
    - "9000:9000"
```

#### ¿Qué es Kafdrop?

Kafdrop es una **herramienta web** para visualizar Kafka.

**Puedes ver**:
- ✅ Topics disponibles
- ✅ Mensajes en cada topic
- ✅ Consumer groups
- ✅ Offsets
- ✅ Particiones
- ✅ Metadatos

#### Cómo Acceder

```
URL: http://localhost:9000
```

Abre esta URL en tu navegador cuando Kafka esté corriendo.

#### Variables de Entorno

| Variable | Valor | Significado |
|----------|-------|-------------|
| `KAFKA_BROKERCONNECT` | `kafka:9092` | Dónde está el broker de Kafka |
| `ZK_HOSTS` | `zookeeper:2181` | Dónde está Zookeeper |

---

### 4. **NETWORK** 🌐

```yaml
networks:
  kafka-network:
    driver: bridge
```

La red `kafka-network` es la **carretera** donde se comunican los contenedores.

**Tipo**: `bridge` = Red privada donde solo estos contenedores pueden verse.

---

## CÓMO FUNCIONAN JUNTOS

### Secuencia Paso a Paso

```
1. INICIO
   ├─ Zookeeper se inicia en puerto 2181
   ├─ Kafka se conecta a Zookeeper
   └─ Kafdrop se conecta a Kafka y Zookeeper

2. ORDER-SERVICE CREA UN PEDIDO
   ├─ order-service.java → genera OrderCreatedEvent
   ├─ Envía evento a Kafka (al topic "order-events")
   └─ Kafka almacena el evento

3. NOTIFICATION-SERVICE RECIBE EL EVENTO
   ├─ notification-service escucha el topic "order-events"
   ├─ Ve que hay un evento nuevo
   ├─ Lo procesa (por ejemplo, envía un email)
   └─ Actualiza el offset (ya procesé hasta aquí)

4. VISUALIZACIÓN EN KAFDROP
   ├─ Abres http://localhost:9000
   ├─ Ves el topic "order-events"
   ├─ Ves los eventos que han pasado
   └─ Ves que notification-service ya consumió algunos
```

---

## PUERTOS Y CONEXIONES

### Resumen de Puertos

| Servicio | Puerto Interno | Puerto Externo | Acceso |
|----------|---|---|---|
| **Zookeeper** | 2181 | 2181 | Otros contenedores |
| **Kafka** | 9092 | 29092 | Contenedores usan 9092; tu PC usa 29092 |
| **Kafdrop** | 9000 | 9000 | http://localhost:9000 |

### Cómo se Conectan

```
┌──────────────────────────────────────────────────────────┐
│                    DOCKER COMPOSE                        │
│                                                          │
│  Tu PC (Windows)                                         │
│  ├─ Kafdrop → http://localhost:9000                      │
│  │                                                       │
│  └─ Apps Java → localhost:29092 (para conexiones)        │
│                      │                                   │
│                      └──→ [DOCKER NETWORK]               │
│                                                          │
│          Dentro de Docker:                               │
│          ├─ notification-service                         │
│          │  └─ Se conecta a: kafka:9092                  │
│          │                                               │
│          ├─ order-service                                │
│          │  └─ Se conecta a: kafka:9092                  │
│          │                                               │
│          ├─ Kafka Broker                                 │
│          │  ├─ Escucha en: 9092                          │
│          │  └─ También escucha en: 29092 (mapeo)         │
│          │                                               │
│          ├─ Zookeeper                                    │
│          │  └─ Escucha en: 2181                          │
│          │                                               │
│          └─ Kafdrop (UI)                                 │
│             ├─ Escucha en: 9000                          │
│             └─ Se conecta a: kafka:9092 y zookeeper:2181 │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## EJEMPLOS PRÁCTICOS

### Ejemplo 1: Ver Topics

```bash
# Conectarse al contenedor de Kafka
docker exec kafka-broker kafka-topics \
  --list \
  --bootstrap-server localhost:9092

# Resultado esperado:
# order-events
# __consumer_offsets
```

---

### Ejemplo 2: Crear un Topic Manualmente

```bash
docker exec kafka-broker kafka-topics \
  --create \
  --topic user-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

**Explicación**:
- `--topic user-events`: Nombre del topic
- `--partitions 3`: Dividir en 3 particiones
- `--replication-factor 1`: Solo 1 copia

---

### Ejemplo 3: Producir Mensajes de Test

```bash
docker exec -it kafka-broker kafka-console-producer \
  --topic order-events \
  --bootstrap-server localhost:9092
```

Luego escribe:
```json
{"orderId": "123", "userId": "user-001", "amount": 99.99}
{"orderId": "124", "userId": "user-002", "amount": 149.99}
```

Presiona `Ctrl+D` para salir.

---

### Ejemplo 4: Consumir Mensajes

```bash
docker exec kafka-broker kafka-console-consumer \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

Verás los eventos que produjiste.

---

### Ejemplo 5: Ver Grupos de Consumo

```bash
docker exec kafka-broker kafka-consumer-groups \
  --list \
  --bootstrap-server localhost:9092

# Resultado:
# notification-service-group
# order-service-group
```

---

## CONFIGURACIÓN EN APLICACIONES JAVA

### En `application.yml` de notification-service

```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092  # Usar puerto 9092 (interno)
    consumer:
      group-id: notification-service-group
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
```

**Importante**: Dentro de Docker, usa `kafka:9092` (no localhost:29092)

### En `application.yml` de order-service

```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092  # Usar puerto 9092 (interno)
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

---

## FLUJO COMPLETO EN NUESTRO PROYECTO

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                  │
│  1. USUARIO CREA PEDIDO (POST /api/orders)                      │
│     │                                                            │
│     └──→ order-service recibe la solicitud                      │
│          │                                                       │
│          ├──→ Guarda el pedido en BD                            │
│          │                                                       │
│          └──→ PRODUCE evento OrderCreatedEvent                  │
│               │                                                 │
│               └──→ Kafka almacena en topic "order-events"       │
│                    │                                            │
│                    ├──→ Partición 0: [Evento OrderCreated]     │
│                    ├──→ Partición 1: []                         │
│                    └──→ Partición 2: []                         │
│                         │                                       │
│  2. NOTIFICATION-SERVICE ESCUCHA                                │
│     │                                                            │
│     └──→ notification-service consume de "order-events"        │
│          │                                                       │
│          ├──→ Ve el evento OrderCreatedEvent                    │
│          │                                                       │
│          ├──→ Genera email de confirmación                      │
│          │                                                       │
│          └──→ Envia email al usuario                            │
│               Offset actualizado: 1                             │
│                                                                  │
│  3. USUARIO VERIFICA EMAIL                                      │
│     │                                                            │
│     └──→ ¡Email recibido!                                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## RESUMEN VISUAL

```
┌──────────────┐
│ order-service│ ← Usuario crea pedido
└──────┬───────┘
       │ PRODUCE
       │ OrderCreatedEvent
       │
       ▼
┌─────────────────────────────┐
│  Kafka Topic: order-events  │
│ ┌─────────┬─────────┬─────────┐
│ │Part. 0  │Part. 1  │Part. 2  │
│ │[evento] │[]       │[]       │
│ └─────────┴─────────┴─────────┘
│   ▲                    │
│   │                    │
│   └────────────────────│
│  (Kafdrop muestra      │ CONSUME
│   esto en web)         │
│                        │
└────────────────────────┼────────────────────────
                         ▼
                ┌──────────────────────────┐
                │notification-service     │
                │- Procesa evento         │
                │- Envía email            │
                │- Marca como consumido   │
                └──────────────────────────┘
```

---

## VENTAJAS DE ESTA ARQUITECTURA

✅ **Desacoplamiento**: order-service NO necesita conocer notification-service

✅ **Escalabilidad**: Podemos agregar más instancias de notification-service

✅ **Durabilidad**: Si notification-service se cae, Kafka guarda los eventos

✅ **Asincronia**: order-service no espera a que se envíe el email

✅ **Debugging**: Kafdrop nos permite ver exactamente qué eventos pasaron

---

## COMANDOS RÁPIDOS

```bash
# Iniciar Kafka
docker-compose up -d

# Ver logs de Kafka
docker logs kafka-broker

# Ver logs de Zookeeper
docker logs kafka-zookeeper

# Ver logs de Kafdrop
docker logs kafka-ui

# Detener todo
docker-compose down

# Detener todo y limpiar volúmenes
docker-compose down -v

# Ver estado de contenedores
docker-compose ps

# Entrar a bash del broker de Kafka
docker exec -it kafka-broker bash
```

---

## ¿PREGUNTAS FRECUENTES?

**P: ¿Qué diferencia hay entre los dos listeners?**
R: `kafka:9092` es para dentro de Docker, `localhost:29092` es para fuera (tu PC).

**P: ¿Por qué se llama Zookeeper?**
R: Es un nombre divertido, pero básicamente coordina el "rebaño" de Kafka brokers.

**P: ¿Qué pasa si Kafka se cae?**
R: Los eventos quedan en disco. Cuando vuelve, todo sigue ahí.

**P: ¿Puedo eliminar todos los eventos?**
R: Sí, con `docker-compose down -v` se eliminan los volúmenes.

**P: ¿Cómo agrego más particiones?**
R: Usa el comando `kafka-topics --alter --topic name --partitions 5`

---

## REFERENCIAS

- [Documentación oficial Kafka](https://kafka.apache.org/documentation/)
- [Conflux Docker Images](https://hub.docker.com/r/confluentinc/cp-kafka)
- [Kafdrop GitHub](https://github.com/obsidiandynamics/kafdrop)

---

**Última actualización**: 20 de enero de 2026
