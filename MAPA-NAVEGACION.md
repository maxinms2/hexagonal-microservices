# 🗺️ MAPA DE NAVEGACIÓN DEL PROYECTO

```
╔═══════════════════════════════════════════════════════════════════════════╗
║                    HEXAGONAL MICROSERVICIOS + KAFKA                      ║
║                           ¿DÓNDE ESTOY?                                  ║
╚═══════════════════════════════════════════════════════════════════════════╝

                    👤 TÚ ESTÁS AQUÍ (README.md)
                              │
                              ▼
                    ┌──────────────────────┐
                    │ COMIENZA-AQUI.md ⭐  │ ← PUNTO DE ENTRADA
                    └──────────┬───────────┘
                               │
            ┌──────────────────┼──────────────────┐
            ▼                  ▼                  ▼
       🚀 RÁPIDO         🧠 APRENDER        💻 TÉCNICO
       (5 min)           (30 min)           (1+ h)
            │                  │                  │
            ▼                  ▼                  ▼
    QUICKSTART-KAFKA.md   RESUMEN-VISUAL.md   ESTADO-DEL-PROYECTO.md
            │                  │                  │
            │                  ├─────┬────────┐   │
            │                  │     ▼        ▼   │
            │              [Docs]  07-event  FLUJO
            │                      driven    visual
            │                      kafka.md
            ▼                                     ▼
        Ejecuta                              Lee código
        5 pasos                              modifica
            │                                     │
            └─────────────────┬───────────────────┘
                              ▼
                    Entiende el flujo
                    Crea nuevos eventos
```

---

## 🎯 MATRIZ DE DECISIÓN

```
┌─────────────────────────────────────────────────────────────┐
│ ¿QUÉ NECESITO?                                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ OPCIÓN 1: SOLO VER QUE FUNCIONA                             │
├─────────────────────────────────────────────────────────────┤
│ ⏱️  Tiempo: 5 minutos                                        │
│ 📚 Lectura: QUICKSTART-KAFKA.md                             │
│ 🔧 Acciones: Copia/pega comandos                            │
│ 📍 Ir a: [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md)      │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ OPCIÓN 2: ENTENDER CÓMO FUNCIONA                            │
├─────────────────────────────────────────────────────────────┤
│ ⏱️  Tiempo: 30 minutos                                       │
│ 📚 Lectura:                                                 │
│    1. [RESUMEN-VISUAL.md](./RESUMEN-VISUAL.md)             │
│    2. [docs/07-event-driven-kafka.md](./docs/07-event-driven-kafka.md) │
│    3. [docs/FLUJO-EVENT-DRIVEN.md](./docs/FLUJO-EVENT-DRIVEN.md) │
│    4. [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md)         │
│ 🔧 Acciones: Leer, observar diagramas, ejecutar            │
│ 📍 Ir a: [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) - Ruta Aprendizaje │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ OPCIÓN 3: MODIFICAR Y CREAR                                 │
├─────────────────────────────────────────────────────────────┤
│ ⏱️  Tiempo: 1+ horas                                         │
│ 📚 Lectura:                                                 │
│    1. [docs/02-arquitectura-hexagonal.md](./docs/02-arquitectura-hexagonal.md) │
│    2. [notification-service/README.md](./notification-service/README.md) │
│    3. [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md) - Próx. Pasos │
│ 🔧 Acciones: Leer código, modificar, crear tests           │
│ 📍 Ir a: [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) - Ruta Técnica │
└─────────────────────────────────────────────────────────────┘
```

---

## 📂 ESTRUCTURA DE ARCHIVOS

```
PROYECTO/
│
├── 🎯 PUNTO DE ENTRADA
│   ├── [README.md](./README.md) ← Estás aquí
│   ├── [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) ⭐ EMPIEZA AQUÍ
│   └── [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md)
│
├── 🚀 INICIO RÁPIDO
│   ├── [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md)
│   ├── [RESUMEN-VISUAL.md](./RESUMEN-VISUAL.md)
│   └── [docker-compose.yml](./docker-compose.yml)
│
├── 📚 DOCUMENTACIÓN
│   ├── docs/
│   │   ├── [01-que-son-microservicios.md](./docs/01-que-son-microservicios.md)
│   │   ├── [02-arquitectura-hexagonal.md](./docs/02-arquitectura-hexagonal.md)
│   │   ├── [03-spring-boot-basics.md](./docs/03-spring-boot-basics.md)
│   │   ├── [04-api-gateway.md](./docs/04-api-gateway.md)
│   │   ├── [05-service-discovery.md](./docs/05-service-discovery.md)
│   │   ├── [06-comunicacion-inter-microservicios.md](./docs/06-comunicacion-inter-microservicios.md)
│   │   ├── [07-event-driven-kafka.md](./docs/07-event-driven-kafka.md) 🆕
│   │   └── [FLUJO-EVENT-DRIVEN.md](./docs/FLUJO-EVENT-DRIVEN.md) 🆕
│   ├── [IMPLEMENTACION-EVENT-DRIVEN.md](./IMPLEMENTACION-EVENT-DRIVEN.md)
│   ├── [RESUMEN-EVENT-DRIVEN.md](./RESUMEN-EVENT-DRIVEN.md)
│   └── [INDICE.md](./INDICE.md)
│
├── 🔧 SERVICIOS
│   ├── notification-service/ 🆕
│   │   ├── [README.md](./notification-service/README.md)
│   │   ├── pom.xml
│   │   └── src/...
│   │
│   ├── order-service/ (mejorado)
│   │   ├── pom.xml (actualizado)
│   │   └── src/...
│   │
│   ├── user-service/
│   ├── payment-service/
│   ├── api-gateway/
│   └── common/
│
└── 🔗 REFERENCIAS
    ├── [COMPLETADO.md](./COMPLETADO.md)
    ├── [ESTADO-MICROSERVICIOS.md](./ESTADO-MICROSERVICIOS.md)
    ├── [PRUEBA-RAPIDA.md](./PRUEBA-RAPIDA.md)
    ├── [GETTING_STARTED.md](./GETTING_STARTED.md)
    ├── [README-NEW.md](./README-NEW.md)
    └── [FLUJO-VISUAL.md](./FLUJO-VISUAL.md)
```

---

## 🎓 RUTAS DE APRENDIZAJE

### Ruta 1: EXPRESS (5 minutos) ⚡

```
START
  │
  ├─→ docker-compose up -d
  │
  ├─→ mvn clean install (en cada servicio)
  │
  ├─→ mvn spring-boot:run (order-service)
  │
  ├─→ mvn spring-boot:run (notification-service) [otra terminal]
  │
  ├─→ curl -X POST http://localhost:8082/api/orders...
  │
  └─→ VER en http://localhost:9000 (Kafdrop UI)

📍 Ver: [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md)
```

### Ruta 2: LEARNING (30 minutos) 🎓

```
START
  │
  ├─→ Lee [RESUMEN-VISUAL.md](./RESUMEN-VISUAL.md)
  │   (¿Qué se hizo?)
  │
  ├─→ Lee [docs/07-event-driven-kafka.md](./docs/07-event-driven-kafka.md)
  │   (¿Cómo funciona Kafka?)
  │
  ├─→ Lee [docs/FLUJO-EVENT-DRIVEN.md](./docs/FLUJO-EVENT-DRIVEN.md)
  │   (¿Visualización?)
  │
  ├─→ Ejecuta [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md)
  │   (¿Funciona?)
  │
  └─→ Experimenta
      - Cambia puerto
      - Añade logging
      - Crea nueva orden

📍 Punto de partida: [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) - Ruta Aprendizaje
```

### Ruta 3: DEVELOPER (1+ horas) 👨‍💻

```
START
  │
  ├─→ Lee [docs/02-arquitectura-hexagonal.md](./docs/02-arquitectura-hexagonal.md)
  │   (¿Patrón de arquitectura?)
  │
  ├─→ Lee [notification-service/README.md](./notification-service/README.md)
  │   (¿Estructura del servicio?)
  │
  ├─→ Revisa código:
  │   - NotificationServiceApplication.java
  │   - ProcessOrderEventUseCase.java
  │   - KafkaConsumerAdapter.java
  │   - Notification.java
  │
  ├─→ Revisa configuración:
  │   - pom.xml (dependencias)
  │   - application.yml (Kafka config)
  │   - KafkaConsumerConfig.java (beans)
  │
  ├─→ Ejecuta [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md)
  │
  ├─→ Experimenta:
  │   - Modifica EmailAdapter
  │   - Cambia topic name
  │   - Añade nuevo listener
  │   - Crea OrderPaidEvent
  │
  └─→ Lee [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md)
      (Próximos pasos)

📍 Punto de partida: [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) - Ruta Técnica
```

---

## 🔍 BUSCAR POR NECESIDAD

### Necesito aprender...

| Tema | Ir a |
|------|------|
| **Microservicios en general** | [docs/01-que-son-microservicios.md](./docs/01-que-son-microservicios.md) |
| **Arquitectura Hexagonal** | [docs/02-arquitectura-hexagonal.md](./docs/02-arquitectura-hexagonal.md) |
| **Spring Boot básico** | [docs/03-spring-boot-basics.md](./docs/03-spring-boot-basics.md) |
| **API Gateway** | [docs/04-api-gateway.md](./docs/04-api-gateway.md) |
| **Service Discovery** | [docs/05-service-discovery.md](./docs/05-service-discovery.md) |
| **HTTP entre servicios** | [docs/06-comunicacion-inter-microservicios.md](./docs/06-comunicacion-inter-microservicios.md) |
| **Event-Driven** | [docs/07-event-driven-kafka.md](./docs/07-event-driven-kafka.md) ⭐ |
| **Kafka específicamente** | [docs/07-event-driven-kafka.md](./docs/07-event-driven-kafka.md) + [docs/FLUJO-EVENT-DRIVEN.md](./docs/FLUJO-EVENT-DRIVEN.md) |

### Necesito hacer...

| Tarea | Ir a |
|------|------|
| **Empezar rápido** | [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md) |
| **Levantar Kafka** | docker-compose + [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md) |
| **Ver evento en vivo** | Kafdrop en http://localhost:9000 |
| **Entender arquitectura** | [notification-service/README.md](./notification-service/README.md) |
| **Modificar notification-service** | Lee [notification-service/README.md](./notification-service/README.md) |
| **Añadir nuevo evento** | [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md) - Próx. Pasos |
| **Implementar real email** | [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md) - Nivel 4 |
| **Crear Saga pattern** | [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md) - Nivel 5 |

---

## 📱 ACCESO RÁPIDO POR DISPOSITIVO

### 🖥️ Desde Windows PowerShell

```powershell
# Ver COMIENZA-AQUI.md
Get-Content COMIENZA-AQUI.md | more

# Ver documentación
explorer docs/

# Ir al proyecto
cd c:\proyectos\hexagonal

# Ver estado
Get-Content ESTADO-DEL-PROYECTO.md | more
```

### 🐧 Desde Linux/Mac

```bash
# Ver COMIENZA-AQUI.md
cat COMIENZA-AQUI.md | less

# Ver documentación
ls -la docs/

# Ir al proyecto
cd ~/proyectos/hexagonal

# Ver estado
less ESTADO-DEL-PROYECTO.md
```

### 🌐 Desde navegador

```
http://localhost:9000  → Kafdrop (monitoreo Kafka)
http://localhost:8082  → Order Service API
http://localhost:8085  → Notification Service API
```

---

## 💡 CONSEJOS DE NAVEGACIÓN

### ✅ HECHO BIEN
```
1. Abre COMIENZA-AQUI.md
2. Elige ruta según tu tiempo
3. Sigue los enlaces
4. Cada archivo tiene contexto
```

### ❌ NO HAGAS ESTO
```
1. ❌ Abierto 20 archivos a la vez
2. ❌ No saber dónde estás
3. ❌ Saltarte conceptos básicos
4. ❌ Ejecutar sin entender
```

---

## 🎯 DECISIÓN RÁPIDA

**¿Tienes 5 minutos?**  
→ Ve a [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md)

**¿Tienes 30 minutos?**  
→ Ve a [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) - Ruta Aprendizaje

**¿Tienes 1+ hora?**  
→ Ve a [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) - Ruta Técnica

**¿Quieres ver estado?**  
→ Ve a [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md)

**¿Estás perdido?**  
→ Ve a [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) (punto de entrada)

---

## 🗺️ MAPA CONCEPTUAL

```
APRENDER                    HACER                    EXTENDER
    │                         │                           │
    ├─ Microservicios        ├─ Docker                   ├─ Más eventos
    ├─ Hexagonal             ├─ Kafka                    ├─ Más servicios
    ├─ Spring Boot           ├─ Tests                    ├─ Saga pattern
    ├─ Event-Driven          ├─ Debugging               └─ Monitoreo
    └─ Kafka                 └─ Performance
         │
         ▼
    COMIENZA-AQUI.md
         │
         ▼
    [Tu ruta elegida]
         │
         ▼
    Éxito! 🎉
```

---

## ⚡ ATAJOS

| Acción | Archivo |
|--------|---------|
| Punto de entrada | [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) |
| Primer paso | [QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md) |
| Entender | [docs/07-event-driven-kafka.md](./docs/07-event-driven-kafka.md) |
| Ver código | [notification-service/](./notification-service/) |
| Estado actual | [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md) |
| Próximos pasos | [ESTADO-DEL-PROYECTO.md](./ESTADO-DEL-PROYECTO.md#-próximos-pasos-recomendados) |
| Todo indexado | [INDICE.md](./INDICE.md) |

---

## 📞 SOPORTE

¿Pregunta? Busca en:
1. [COMIENZA-AQUI.md](./COMIENZA-AQUI.md) - FAQs
2. [docs/07-event-driven-kafka.md](./docs/07-event-driven-kafka.md) - Troubleshooting
3. [notification-service/README.md](./notification-service/README.md) - Guía técnica

---

**¡Listo para empezar?**

👉 **[VE A COMIENZA-AQUI.md](./COMIENZA-AQUI.md)** ⭐

```
start → COMIENZA-AQUI.md → Tu ruta → Success! 🚀
```

