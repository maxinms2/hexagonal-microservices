# 🚀 GUÍA RÁPIDA: Event-Driven con Kafka

## 📋 Prerrequisitos

- ✅ Java 17+
- ✅ Maven 3.6+
- ✅ Docker y Docker Compose
- ✅ Todos los servicios anteriores compilados

## 🎯 Objetivo

Demostrar cómo:
1. **order-service** produce eventos cuando se crea una orden
2. **notification-service** consume esos eventos
3. Kafka es el "periódico" que comunica ambos servicios

## 🚀 Paso a Paso

### **PASO 1: Levantar Kafka (Terminal 1)**

```bash
cd c:\proyectos\hexagonal

# Iniciar Kafka, Zookeeper y Kafdrop
docker-compose up -d

# Verificar que todo esté corriendo
docker-compose ps

# Debería ver:
# CONTAINER ID   NAMES              STATUS
# xxx            kafka-broker       Up 2 minutes
# xxx            kafka-zookeeper    Up 2 minutes
# xxx            kafka-ui           Up 2 minutes
```

✅ Kafka está listo cuando veas "Up" en todos

---

### **PASO 2: Compilar Servicios (Terminal 2)**

```bash
# Compilar order-service
cd c:\proyectos\hexagonal\order-service
mvn clean install

# Compilar notification-service
cd c:\proyectos\hexagonal\notification-service
mvn clean install

# Compilar user-service (si lo necesitas)
cd c:\proyectos\hexagonal\user-service
mvn clean install
```

✅ Verifica que todos terminen con "BUILD SUCCESS"

---

### **PASO 3: Iniciar user-service (Terminal 2 - nueva)**

```bash
cd c:\proyectos\hexagonal\user-service
mvn spring-boot:run

# Logs esperados:
# Started UserServiceApplication in 3.5 seconds
# Tomcat started on port(s): 8081
```

✅ Ver en http://localhost:8081/api/actuator/health → {"status":"UP"}

---

### **PASO 4: Iniciar order-service (Terminal 3 - nueva)**

```bash
cd c:\proyectos\hexagonal\order-service
mvn spring-boot:run

# Logs esperados:
# Started OrderServiceApplication in 4.2 seconds
# Tomcat started on port(s): 8082
# Kafka broker[ID: 1] is ready
```

✅ Ver en http://localhost:8082/api/actuator/health

---

### **PASO 5: Iniciar notification-service (Terminal 4 - nueva)**

```bash
cd c:\proyectos\hexagonal\notification-service
mvn spring-boot:run

# Logs esperados:
# Started NotificationServiceApplication in 3.8 seconds
# Tomcat started on port(s): 8085
# Successfully subscribed to topic(s): order-events
```

✅ Debe decir "subscribed to topic(s): order-events"

---

## 🧪 PRUEBA EL FLUJO COMPLETO

### **Test 1: Crear una Orden (en otra Terminal)**

```bash
# Primero obtén un UUID válido de usuario
# (o usa este de prueba)

curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 99.99
  }'
```

**Respuesta esperada:**
```json
{
  "id": "order-uuid-123",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING",
  "totalAmount": 99.99,
  "createdAt": "2024-01-20T10:30:00"
}
```

---

### **Observa los Logs**

#### En **Terminal 3 (order-service)**:
```
📤 Publicando evento OrderCreated a Kafka - Orden: order-uuid-123
✅ Evento publicado exitosamente - Orden: order-uuid-123
```

#### En **Terminal 4 (notification-service)**:
```
🎧 Mensaje recibido de Kafka - Partición: 0, Offset: 0
📨 Evento de orden recibido: order-uuid-123
📩 Procesando evento de orden creada: order-uuid-123

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📬 EMAIL ENVIADO (SIMULADO)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Para: user@email.com
Asunto: 📦 Tu orden ha sido creada!
Mensaje:
Hola,

Tu orden #order-uuid-123 ha sido procesada exitosamente.
Monto: $99.99
Items: Productos varios

Gracias por tu compra!
✅ Notificación enviada exitosamente para orden: order-uuid-123
```

---

## 🔍 VISUALIZAR EN KAFDROP

Abre en el navegador: **http://localhost:9000**

### Ver Topics
- Navega a "Topics"
- Haz clic en "order-events"
- Verás los mensajes producidos

### Ver Consumer Groups
- Navega a "Consumer Groups"
- Busca "notification-service-group"
- Verás el LAG (mensajes pendientes)

### Ver Mensajes
- En "Topics" → "order-events"
- Ver el JSON deserializado de cada evento

---

## 🔧 COMANDOS ÚTILES

### Ver si Kafka está corriendo
```bash
docker-compose ps
```

### Ver logs de Kafka
```bash
docker-compose logs kafka
```

### Ver logs de Zookeeper
```bash
docker-compose logs zookeeper
```

### Ver los topics
```bash
docker exec kafka-broker kafka-topics --list \
  --bootstrap-server localhost:9092
```

### Ver consumer groups
```bash
docker exec kafka-broker kafka-consumer-groups --list \
  --bootstrap-server localhost:9092
```

### Ver el LAG (mensajes no procesados)
```bash
docker exec kafka-broker kafka-consumer-groups \
  --describe \
  --group notification-service-group \
  --bootstrap-server localhost:9092
```

Debería mostrar LAG = 0 (todo procesado)

### Crear un mensaje de prueba manualmente
```bash
docker exec -it kafka-broker kafka-console-producer \
  --topic order-events \
  --bootstrap-server localhost:9092

# Luego pega esto y presiona Enter:
{"orderId":"order-manual-test","customerId":"cust-123","customerEmail":"test@example.com","totalAmount":50.00,"description":"Test manual","createdAt":"2024-01-20T10:30:00","eventType":"OrderCreated"}

# Ver en los logs de notification-service que lo procesa
```

---

## 📊 FLUJO VISUAL

```
USUARIO → POST /orders
     │
     ▼
┌──────────────────┐
│ order-service    │
│ (8082)           │
│                  │
│ 1. Crea orden    │
│ 2. Publica evento│
└────────┬─────────┘
         │
         │ OrderCreatedEvent (JSON)
         │ {
         │   orderId: "order-123",
         │   customerId: "cust-456",
         │   customerEmail: "test@email.com"
         │ }
         │
         ▼
┌──────────────────────────────┐
│      KAFKA (Broker)          │
│                              │
│  Topic: order-events         │
│  ├─ Partition 0: [msg]       │
│  ├─ Partition 1: [msg]       │
│  └─ Partition 2: [msg]       │
└────────┬─────────────────────┘
         │
         │ Consumed by
         │
         ▼
┌──────────────────────┐
│notification-service │
│(8085)               │
│                     │
│1. Recibe evento     │
│2. Procesa evento    │
│3. Envía notificación│
│                     │
│📬 EMAIL SIMULADO    │
└──────────────────────┘
```

---

## ⚠️ TROUBLESHOOTING

### ❌ "Connection to node -1 could not be established"
**Causa**: Kafka no está corriendo
```bash
docker-compose up -d
docker-compose ps
```

### ❌ "Topic 'order-events' does not exist"
**Causa**: No se creó el topic
**Solución**: Spring lo crea automáticamente o crear manualmente:
```bash
docker exec kafka-broker kafka-topics --create \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

### ❌ notification-service no recibe eventos
**Verificar**:
1. ¿Está notification-service corriendo? (logs dicen "subscribed to topic(s)")
2. ¿Kafka está corriendo? (docker-compose ps)
3. ¿order-service publica? (ver logs "Publicando evento")

### ❌ El email no se envía
**Nota**: Estamos usando un adaptador simulado. Los logs muestran el email.
En producción, usarías SendGrid, AWS SES, etc.

---

## 📈 NEXT STEPS

1. **Implementar verdadero envío de email**
   - Usar SendGrid o AWS SES
   - Cambiar EmailAdapter

2. **Añadir más eventos**
   - OrderPaidEvent
   - OrderShippedEvent
   - OrderCancelledEvent

3. **Múltiples Consumer Servicios**
   - AnalyticsService (contar órdenes)
   - ReportingService (generar reportes)
   - BillingService (procesar pagos)

4. **Implementar Dead Letter Topic**
   - Para mensajes que fallan

5. **Monitoreo y Alertas**
   - Prometheus + Grafana
   - ELK Stack

---

## ✅ CHECKLIST

- [ ] Docker corriendo
- [ ] Kafka levantado (docker-compose up -d)
- [ ] Todos los servicios compilados
- [ ] user-service iniciado
- [ ] order-service iniciado
- [ ] notification-service iniciado
- [ ] Crear una orden (POST /api/orders)
- [ ] Ver evento en logs de order-service
- [ ] Ver notificación en logs de notification-service
- [ ] Acceder a Kafdrop (http://localhost:9000)
- [ ] Ver topic "order-events" en Kafdrop
- [ ] Ver consumer group "notification-service-group"
- [ ] ✅ ¡ÉXITO! Event-Driven con Kafka funcionando

---

## 🎓 ¿Qué Aprendiste?

✅ Patrón Event-Driven Architecture
✅ Kafka: Topics, Partitions, Offsets, Consumer Groups
✅ Desacoplamiento entre servicios
✅ Escalabilidad horizontal
✅ Configuración de Producers y Consumers en Spring
✅ Serialización/Deserialización JSON
✅ Arquitectura Hexagonal + Event-Driven

¡Felicidades! 🎉

