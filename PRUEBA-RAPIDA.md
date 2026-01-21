# 🧪 Guía Rápida de Prueba

## 🚀 Paso 1: Iniciar User Service

```bash
cd user-service
mvn spring-boot:run
```

**Esperado:**
```
... Started UserServiceApplication in 2.5 seconds
... Tomcat started on port(s): 8081
```

---

## 🚀 Paso 2: Iniciar Order Service

En **otra terminal**:

```bash
cd order-service
mvn spring-boot:run
```

**Esperado:**
```
... Started UserServiceApplication in 2.5 seconds
... Tomcat started on port(s): 8082
```

---

## 📝 Paso 3: Crear un Usuario

Primero necesitamos un usuario que exista en User Service:

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "name": "John Doe",
    "password": "password123"
  }'
```

**Respuesta esperada (HTTP 201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john@example.com",
  "name": "John Doe",
  "active": true,
  "createdAt": "2024-01-20T17:45:00"
}
```

**Guarda este `id`** (lo necesitarás en el siguiente paso)

---

## ✅ Paso 4: Crear Orden CON Usuario Válido

```bash
# Reemplaza el UUID con el id que obtuviste en el paso anterior
curl -X POST http://localhost:8082/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 99.99
  }'
```

**Respuesta esperada (HTTP 201):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440111",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 99.99,
  "status": "CREATED",
  "createdAt": "2024-01-20T17:45:00"
}
```

**Logs esperados en Order Service:**
```
✅ Usuario validado: John Doe (john@example.com)
ORDER CREATED: 660e8400-e29b-41d4-a716-446655440111
```

---

## ❌ Paso 5: Crear Orden CON Usuario Inválido

```bash
curl -X POST http://localhost:8082/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "invalid-user-id-that-does-not-exist",
    "totalAmount": 99.99
  }'
```

**Respuesta esperada (HTTP 422 Unprocessable Entity):**
```json
{
  "timestamp": "2024-01-20T17:46:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Usuario no encontrado: invalid-user-id-that-does-not-exist",
  "path": "/orders"
}
```

**Logs esperados en Order Service:**
```
⚠️ Usuario no encontrado en user-service: invalid-user-id-that-does-not-exist
```

---

## 🔍 Paso 6: Verificar Orden Creada

```bash
# Reemplaza con el ID de la orden del Paso 4
curl http://localhost:8082/orders/660e8400-e29b-41d4-a716-446655440111
```

**Respuesta esperada (HTTP 200):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440111",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 99.99,
  "status": "CREATED",
  "createdAt": "2024-01-20T17:45:00"
}
```

---

## 📊 Paso 7: Listar Todas las Órdenes

```bash
curl http://localhost:8082/orders
```

**Respuesta esperada (HTTP 200):**
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440111",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 99.99,
    "status": "CREATED",
    "createdAt": "2024-01-20T17:45:00"
  }
]
```

---

## 🔄 Paso 8: Cambiar Estado de Orden

### Marcar como PAGADA

```bash
curl -X PATCH http://localhost:8082/orders/660e8400-e29b-41d4-a716-446655440111/status \
  -H "Content-Type: application/json" \
  -d '{"status": "PAID"}'
```

**Respuesta esperada (HTTP 200):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440111",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 99.99,
  "status": "PAID",
  "createdAt": "2024-01-20T17:45:00"
}
```

### Cancelar Orden

```bash
curl -X PATCH http://localhost:8082/orders/660e8400-e29b-41d4-a716-446655440111/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CANCELLED"}'
```

**Respuesta esperada (HTTP 200):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440111",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 99.99,
  "status": "CANCELLED",
  "createdAt": "2024-01-20T17:45:00"
}
```

---

## 🗑️ Paso 9: Eliminar Orden

```bash
curl -X DELETE http://localhost:8082/orders/660e8400-e29b-41d4-a716-446655440111
```

**Respuesta esperada (HTTP 204 No Content):**
```
(sin body)
```

---

## ✅ Checklist de Verificación

- [ ] User Service inició correctamente (puerto 8081)
- [ ] Order Service inició correctamente (puerto 8082)
- [ ] Creaste un usuario en User Service
- [ ] Creaste una orden CON usuario válido (HTTP 201)
- [ ] Intentaste crear orden CON usuario inválido (HTTP 422)
- [ ] Recuperaste la orden creada (HTTP 200)
- [ ] Listaste todas las órdenes
- [ ] Cambiaste el estado de la orden a PAID
- [ ] Cancelaste la orden
- [ ] Eliminaste la orden (HTTP 204)

---

## 🐛 Troubleshooting

### Error: "Connection refused"

**Problema:** Order Service no puede conectar con User Service

**Solución:**
1. Verifica que User Service está corriendo en puerto 8081
2. Revisa que `user-service.url` en `order-service/src/main/resources/application.yml` sea `http://localhost:8081`

### Error: "Usuario no encontrado"

**Problema:** El UUID del usuario es diferente

**Solución:**
1. Asegúrate de usar el UUID exacto que obtuviste en Paso 3
2. Copia-pega en lugar de escribir manualmente

### Error: "400 Bad Request"

**Problema:** El JSON es inválido

**Solución:**
1. Verifica que los UUIDs entre comillas (strings)
2. Verifica que `totalAmount` sea número decimal
3. Usa un validador JSON (jsonlint.com)

### Error: "500 Internal Server Error"

**Problema:** Hay un error en el servidor

**Solución:**
1. Revisa los logs de Order Service
2. Busca "ERROR" en los logs
3. Verifica que el usuario existe en User Service

---

## 📞 Soporte

Si encuentras problemas:

1. **Revisa los logs** de ambos servicios
2. **Verifica la conectividad** entre servicios
3. **Confirma los UUIDs** exactos
4. **Revisa el contenido de requests** (JSON válido)

---

## 🎯 Resumen

Si todos los pasos pasaron ✅, significa que:

- ✅ Los microservicios se comunican correctamente
- ✅ La validación de usuario funciona
- ✅ HTTP Interfaces está correctamente configurado
- ✅ WebClient está conectando exitosamente
- ✅ Manejo de excepciones funciona como se espera

¡**Felicidades! Tu arquitectura de microservicios está funcionando correctamente.**

