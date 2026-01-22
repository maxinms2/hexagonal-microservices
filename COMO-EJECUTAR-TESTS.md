# 🚀 Guía Rápida: Cómo Ejecutar los Tests

## ⚡ Comando Rápido (Sin Leer Nada)

```bash
# Ejecutar TODOS los tests
mvn test

# Ejecutar tests de un servicio
cd user-service && mvn test

# Ejecutar test específico
mvn test -Dtest=UserTest

# Con salida de colores
mvn test -X
```

---

## 🎯 Comandos por Escenario

### 1. **Quiero ejecutar TODOS los tests**
```bash
cd c:\proyectos\hexagonal
mvn test
```
✅ Ejecutará ~65 tests en todos los microservicios

### 2. **Quiero ejecutar tests de USER SERVICE**
```bash
cd c:\proyectos\hexagonal\user-service
mvn test
```
✅ Ejecutará UserTest + UserServiceTest (~43 tests)

### 3. **Quiero ejecutar tests de ORDER SERVICE**
```bash
cd c:\proyectos\hexagonal\order-service
mvn test
```
✅ Ejecutará OrderServiceTest (~23 tests)

### 4. **Quiero ejecutar tests de NOTIFICATION SERVICE**
```bash
cd c:\proyectos\hexagonal\notification-service
mvn test
```
✅ Ejecutará NotificationServiceTest (~20 tests)

### 5. **Quiero ejecutar UNA CLASE de tests**
```bash
mvn test -Dtest=UserTest
mvn test -Dtest=UserServiceTest
mvn test -Dtest=OrderServiceTest
mvn test -Dtest=NotificationServiceTest
```

### 6. **Quiero ejecutar UN TEST específico**
```bash
mvn test -Dtest=UserTest#testDeactivateUser
mvn test -Dtest=UserServiceTest#shouldCreateUserWithValidEmailAndName
mvn test -Dtest=OrderServiceTest#shouldCreateOrderAndValidateUser
```

### 7. **Quiero ver cobertura de código**
```bash
mvn test jacoco:report
# Luego abre: user-service/target/site/jacoco/index.html
```

### 8. **Quiero ver más detalles (debug)**
```bash
mvn test -X
mvn test -e
```

### 9. **Quiero ejecutar y PARAR en el primer error**
```bash
mvn test -DfailIfNoTests=false
```

### 10. **Quiero saltar los tests**
```bash
mvn install -DskipTests
```

---

## 📊 Salida Esperada

```
[INFO] -------------------------------------------------------
[INFO] T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.microservices.user.domain.model.UserTest
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.045 s - in UserTest
[INFO] Running com.microservices.user.application.service.UserServiceTest
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.032 s - in UserServiceTest
[INFO] Running com.microservices.order.application.service.OrderServiceTest
[INFO] Tests run: 23, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.028 s - in OrderServiceTest
[INFO] Running com.microservices.notification.application.service.NotificationServiceTest
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.025 s - in NotificationServiceTest
[INFO] -------------------------------------------------------
[INFO] Tests run: 65, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.150 s
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
```

---

## ✅ Lo Que Verás

### Verde = Éxito ✅
```
[INFO] Tests run: 65, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Rojo = Algo Falló ❌
```
[INFO] Tests run: 65, Failures: 1, Errors: 0, Skipped: 0
[ERROR] BUILD FAILURE
```

---

## 🐛 Troubleshooting

### "No tests found"
```bash
# ✅ Verificar que los archivos existen
ls user-service/src/test/java/com/microservices/user/
ls order-service/src/test/java/com/microservices/order/
ls notification-service/src/test/java/com/microservices/notification/
```

### "Maven not found"
```bash
# ✅ Instalar Maven
# Windows: descargar de https://maven.apache.org/download.cgi
# Mac/Linux: brew install maven
```

### "Out of memory"
```bash
# ✅ Aumentar memoria
export MAVEN_OPTS="-Xmx1024m"
mvn test
```

### Limpiar antes de ejecutar
```bash
mvn clean test
```

---

## 📈 Diferencias entre Tests

### UserTest - Tests de Dominio (Puros)
```bash
mvn test -Dtest=UserTest
```
- ✅ MUY RÁPIDO (milisegundos)
- ✅ Sin Spring, sin BD, sin mocks
- ✅ Testean entidad `User` directamente

### UserServiceTest - Tests de Servicios
```bash
mvn test -Dtest=UserServiceTest
```
- ✅ RÁPIDO (menos de 1 segundo)
- ✅ Con Mockito, sin BD real
- ✅ Testean lógica de aplicación

### NotificationServiceTest - Tests Event-Driven
```bash
mvn test -Dtest=NotificationServiceTest
```
- ✅ RÁPIDO (milisegundos)
- ✅ Sin Kafka real
- ✅ Mock de servicios externos

---

## 🎯 Visualizar en IDE

### VS Code (Con Maven Extension)
1. Abre el proyecto en VS Code
2. Ctrl+Shift+D → Debug
3. Haz clic en "Run Tests"

### IntelliJ IDEA
1. Abre test → Click derecho → Run 'TestClass'
2. O: Ctrl+Shift+F10

### Eclipse
1. Abre test → Click derecho → Run As → JUnit Test

---

## 📝 Entender la Salida

### Nombres de Tests
```
UserTest#testDeactivateUser
     ↑          ↑
   Clase      Método
   
UserServiceTest#shouldCreateUserWithValidEmailAndName
            ↑                ↑
         Clase            Método (naming claro)
```

### Tiempos de Ejecución
```
Time elapsed: 0.045 s - in UserTest
                       ↑ Menos de 50ms = excelente
```

### Cobertura
```
mvn test jacoco:report
# Abre: target/site/jacoco/index.html
# Muestra % de código testeado por clase
```

---

## 🔄 Workflow Recomendado

### 1. **Desarrollo (Escribo código)**
```bash
# Ejecutar tests rápidamente
mvn test -Dtest=MyTest -DfailIfNoTests=false
```

### 2. **Antes de Commit**
```bash
# Ejecutar TODOS los tests
mvn clean test
```

### 3. **En CI/CD (Pipeline)**
```bash
# Con cobertura
mvn clean test jacoco:report
```

---

## 💡 Tips Profesionales

### 1. Usa filtros de nombres
```bash
# Ejecutar solo tests que contengan "Create"
mvn test -Dtest=*Create*

# Ejecutar solo tests de User
mvn test -Dtest=User*
```

### 2. Parallelizar tests
```bash
# Ejecutar tests en paralelo (más rápido)
mvn test -DparallelThreads=4
```

### 3. Ahorrar tiempo en CI/CD
```bash
# Saltarse tests específicos
mvn clean package -DskipTests

# Ejecutar solo unit tests
mvn test -Dgroups="!slow"
```

### 4. Ver qué está fallando
```bash
# Parar en primer error
mvn test -DfailIfNoTests=false

# Verbose output
mvn test -X
```

---

## 📚 Documentación Relacionada

- **docs/08-TESTING-Y-HEXAGONAL.md** - Teoría
- **docs/09-GUIA-TESTING-COMPLETA.md** - Guía detallada
- **TESTING-README.md** - Resumen completo
- **RESUMEN-TESTS.md** - Ejecutivo

---

## ✨ Lo Que Deberías Ver

✅ 65+ tests pasando
✅ < 200ms total
✅ 100% green (ninguno rojo)
✅ Coverage ~80%+

---

## 🎉 ¡Listo!

Ya tienes todo configurado. Solo ejecuta:

```bash
mvn test
```

Y disfruta de tests rápidos, confiables y bien documentados. 🚀

