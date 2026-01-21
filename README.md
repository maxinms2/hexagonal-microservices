# 🚀 Proyecto de Microservicios con Spring Boot + Event-Driven con Kafka

## 📚 Proyecto Educativo: De Cero a Héroe en Microservicios

Bienvenido a este proyecto educativo diseñado para aprender microservicios desde cero, aplicando las mejores prácticas de la industria.

> ⚡ **NUEVO**: ¡Ahora con Event-Driven Architecture y Kafka! Comunicación asíncrona entre servicios.

### 🎯 ¿POR DÓNDE EMPEZAR?

**👉 [LEE COMIENZA-AQUI.md PRIMERO](./COMIENZA-AQUI.md)** 

Allí encontrarás:
- 🚀 **Ruta rápida**: 5 minutos para verlo funcionando
- 🧠 **Ruta de aprendizaje**: Entiende los conceptos
- 💻 **Ruta técnica**: Códifica y modifica

## 🎯 ¿Qué vamos a construir?

Un sistema de e-commerce simplificado con:
- **User Service**: Gestión de usuarios
- **Order Service**: Gestión de pedidos
- **Notification Service**: 🆕 Notificaciones vía Kafka
- **API Gateway**: Punto de entrada único
- **Service Discovery**: Para que los servicios se encuentren entre sí
- **Config Server**: Configuración centralizada
- **Kafka**: 🆕 Comunicación asíncrona entre servicios event-driven

## 🏗️ Arquitectura

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │
       ▼
┌─────────────────────────┐
│   API Gateway           │ ← Punto de entrada único
└──────┬──────────────────┘
       │
       ├─────────┬──────────┬──────────────┐
       ▼         ▼          ▼              ▼
  ┌────────┐ ┌────────┐ ┌─────────┐  ┌──────────────┐
  │  User  │ │ Order  │ │Product  │  │Notification │
  │Service │ │Service │ │Service  │  │Service   🆕  │
  └────────┘ └────┬───┘ └─────────┘  └──────▲───────┘
                  │                          │
                  └──────────┬────────────────┘
                             ▼
                      ┌─────────────────┐
                      │  Apache Kafka   │ 🆕
                      │  (Asíncrono)    │
                      │  Event-Driven   │
                      └─────────────────┘
```

### Event-Driven Flow (🆕)

```
1. Usuario crea orden
   └─> Order Service procesa
        └─> Publica evento a Kafka
            └─> Notification Service consume
                └─> Envía notificación
```

## 📖 Documentación

Toda la documentación está en la carpeta [docs](docs/):

### Para empezar rápido:
- **[COMIENZA-AQUI.md](./COMIENZA-AQUI.md)** ← 👈 Empieza aquí
- **[QUICKSTART-KAFKA.md](./QUICKSTART-KAFKA.md)** - 5 pasos para verlo funcionando
- **[RESUMEN-VISUAL.md](./RESUMEN-VISUAL.md)** - Resumen visual del proyecto

1. **[¿Qué son los Microservicios?](docs/01-que-son-microservicios.md)** - Conceptos básicos
2. **[Arquitectura Hexagonal](docs/02-arquitectura-hexagonal.md)** - Patrón de diseño
3. **[Spring Boot Basics](docs/03-spring-boot-basics.md)** - Framework principal
4. **[API Gateway](docs/04-api-gateway.md)** - Puerta de entrada
5. **[Service Discovery](docs/05-service-discovery.md)** - Registro de servicios
6. **[Comunicación Inter-Microservicios](docs/06-comunicacion-inter-microservicios.md)** - REST HTTP
7. **[Event-Driven con Kafka](docs/07-event-driven-kafka.md)** - 🆕 Comunicación asíncrona

### 🚀 Guías Rápidas
- **[QUICKSTART-KAFKA.md](QUICKSTART-KAFKA.md)** - 🆕 Levanta todo en 5 minutos
- **[IMPLEMENTACION-EVENT-DRIVEN.md](IMPLEMENTACION-EVENT-DRIVEN.md)** - 🆕 Resumen de lo implementado

## 🛠️ Tecnologías Utilizadas

- **Java 17+** - Lenguaje de programación
- **Spring Boot 3.x** - Framework principal
- **Spring Cloud** - Herramientas para microservicios
- **Kafka** - 🆕 Broker de eventos para comunicación asíncrona
- **Docker & Docker Compose** - Containerización
- **PostgreSQL/H2** - Bases de datos
- **PostgreSQL** - Base de datos
- **Docker** - Contenedorización
- **Kubernetes** - Orquestación (opcional)
- **Maven** - Gestión de dependencias

## 📂 Estructura del Proyecto

```
hexagonal/
├── docs/                          # Documentación educativa
│   ├── 01-que-son-microservicios.md
│   ├── 02-arquitectura-hexagonal.md
│   ├── 03-spring-boot-basics.md
│   ├── 04-api-gateway.md
│   ├── 05-service-discovery.md
│   ├── 06-comunicacion-inter-microservicios.md
│   ├── 07-event-driven-kafka.md (🆕)
│   └── FLUJO-EVENT-DRIVEN.md (🆕 Diagramas)
│
├── user-service/                  # Microservicio de usuarios
├── order-service/                 # Microservicio de pedidos
├── notification-service/          # 🆕 Microservicio de notificaciones (Kafka)
├── api-gateway/                   # Gateway de entrada
├── config-server/                 # Servidor de configuración
├── eureka-server/                 # Service Discovery
├── common/                        # Código compartido
│
├── docker-compose.yml             # 🆕 Kafka + Zookeeper + UI
├── QUICKSTART-KAFKA.md            # 🆕 Guía rápida (5 minutos)
├── IMPLEMENTACION-EVENT-DRIVEN.md # 🆕 Resumen completo
├── INDICE.md                      # Índice de documentación
└── README.md                      # Este archivo
```

## 🚦 Prerrequisitos

- Java JDK 17 o superior
- Maven 3.8+
- Docker Desktop (opcional pero recomendado)
- IDE (IntelliJ IDEA, VS Code, Eclipse)

## ▶️ Cómo Empezar

### 📖 Para Aprender
1. Lee en orden: [INDICE.md](INDICE.md)
2. Cada documento explica conceptos con analogías
3. El código incluye comentarios detallados
4. Experimenta modificando el código

### 🚀 Para Ejecutar (Event-Driven + Kafka)
1. Lee: [QUICKSTART-KAFKA.md](QUICKSTART-KAFKA.md)
2. Sigue los 5 pasos exactos
3. Ejecuta comandos paso a paso
4. Monitorea con Kafdrop (http://localhost:9000)

### 🔍 Para Entender Kafka
1. Lee: [docs/07-event-driven-kafka.md](docs/07-event-driven-kafka.md)
2. Ve diagramas: [docs/FLUJO-EVENT-DRIVEN.md](docs/FLUJO-EVENT-DRIVEN.md)
3. Consulta: [notification-service/README.md](notification-service/README.md)
3. El código incluye comentarios detallados
4. Practica modificando el código

## 🎓 Filosofía de Aprendizaje

Este proyecto está diseñado para:
- ✅ Aprender haciendo
- ✅ Explicaciones con ejemplos del mundo real
- ✅ Código limpio y comentado
- ✅ Mejores prácticas de la industria
- ✅ Progresión gradual de complejidad

---

**¡Comencemos el viaje! 🎉**
