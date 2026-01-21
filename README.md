# 🚀 Proyecto de Microservicios con Spring Boot

## 📚 Proyecto Educativo: De Cero a Héroe en Microservicios

Bienvenido a este proyecto educativo diseñado para aprender microservicios desde cero, aplicando las mejores prácticas de la industria.

## 🎯 ¿Qué vamos a construir?

Un sistema de e-commerce simplificado con:
- **User Service**: Gestión de usuarios
- **Order Service**: Gestión de pedidos
- **API Gateway**: Punto de entrada único
- **Service Discovery**: Para que los servicios se encuentren entre sí
- **Config Server**: Configuración centralizada

## 🏗️ Arquitectura

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ API Gateway │ ← Punto de entrada único
└──────┬──────┘
       │
       ├─────────┬──────────┐
       ▼         ▼          ▼
  ┌────────┐ ┌────────┐ ┌────────┐
  │  User  │ │ Order  │ │ Product│
  │Service │ │Service │ │Service │
  └────────┘ └────────┘ └────────┘
```

## 📖 Documentación

Toda la documentación está en la carpeta [docs](docs/):

1. **[¿Qué son los Microservicios?](docs/01-que-son-microservicios.md)** - Conceptos básicos
2. **[Arquitectura Hexagonal](docs/02-arquitectura-hexagonal.md)** - Patrón de diseño
3. **[Spring Boot Basics](docs/03-spring-boot-basics.md)** - Framework principal
4. **[API Gateway](docs/04-api-gateway.md)** - Puerta de entrada
5. **[Service Discovery](docs/05-service-discovery.md)** - Registro de servicios

## 🛠️ Tecnologías Utilizadas

- **Java 17+** - Lenguaje de programación
- **Spring Boot 3.x** - Framework principal
- **Spring Cloud** - Herramientas para microservicios
- **PostgreSQL** - Base de datos
- **Docker** - Contenedorización
- **Kubernetes** - Orquestación (opcional)
- **Maven** - Gestión de dependencias

## 📂 Estructura del Proyecto

```
hexagonal/
├── docs/                    # Documentación educativa
├── user-service/           # Microservicio de usuarios
├── order-service/          # Microservicio de pedidos
├── api-gateway/            # Gateway de entrada
├── config-server/          # Servidor de configuración
├── eureka-server/          # Service Discovery
└── common/                 # Código compartido
```

## 🚦 Prerrequisitos

- Java JDK 17 o superior
- Maven 3.8+
- Docker Desktop (opcional pero recomendado)
- IDE (IntelliJ IDEA, VS Code, Eclipse)

## ▶️ Cómo Empezar

1. Lee la documentación en orden
2. Cada documento explica conceptos de forma simple
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
