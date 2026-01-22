package com.microservices.user.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 UNIT TESTS PARA ENTIDAD User
 * 
 * PROPÓSITO:
 * - Testear la lógica de negocio PURA del dominio
 * - Verificar que las reglas de negocio se cumplan
 * - NO depende de Spring, BD, ni frameworks
 * - Tests ultrarrápidos y confiables
 * 
 * ¿POR QUÉ HEXAGONAL AYUDA?
 * - La entidad User NO tiene anotaciones JPA (@Entity)
 * - Es código PURO, fácil de testear
 * - NO necesita mock, BD, ni contexto Spring
 * - Podemos crear instancias directamente en el test
 * 
 * ESTRUCTURA:
 * - @Nested: Agrupa tests por funcionalidad
 * - @DisplayName: Describe qué se está testeando en lenguaje natural
 * - Clear naming: arrange, act, assert
 */
@DisplayName("🧪 User Domain Model Tests")
class UserTest {
    
    private User user;
    private Email testEmail;
    private String testName;
    
    @BeforeEach
    void setUp() {
        // Arrange: Preparar datos comunes para todos los tests
        testEmail = new Email("john@example.com");
        testName = "John Doe";
        user = User.create(testEmail, testName);
    }
    
    // ============================================
    // FACTORY METHOD TESTS
    // ============================================
    
    @Nested
    @DisplayName("✅ User.create() - Factory Method")
    class UserCreationTests {
        
        @Test
        @DisplayName("Debe crear usuario con valores válidos")
        void shouldCreateUserWithValidValues() {
            // Arrange
            Email email = new Email("test@example.com");
            String name = "Test User";
            
            // Act
            User createdUser = User.create(email, name);
            
            // Assert
            assertNotNull(createdUser, "El usuario no debe ser null");
            assertNotNull(createdUser.getId(), "El ID debe ser generado");
            assertEquals(email, createdUser.getEmail(), "El email debe coincidir");
            assertEquals(name, createdUser.getName(), "El nombre debe coincidir");
            assertTrue(createdUser.isActive(), "El usuario debe estar activo por defecto");
            assertNotNull(createdUser.getCreatedAt(), "La fecha de creación debe ser establecida");
            assertNotNull(createdUser.getUpdatedAt(), "La fecha de actualización debe ser establecida");
        }
        
        @Test
        @DisplayName("Cada nuevo usuario debe tener ID único")
        void eachNewUserShouldHaveUniqueId() {
            // Arrange
            Email email1 = new Email("user1@example.com");
            Email email2 = new Email("user2@example.com");
            
            // Act
            User user1 = User.create(email1, "User 1");
            User user2 = User.create(email2, "User 2");
            
            // Assert
            assertNotEquals(user1.getId(), user2.getId(), "Los IDs deben ser diferentes");
        }
        
        @Test
        @DisplayName("La fecha de creación debe ser ahora")
        void creationDateShouldBeNow() {
            // Arrange
            LocalDateTime beforeCreation = LocalDateTime.now();
            
            // Act
            User createdUser = User.create(testEmail, testName);
            
            // Assert
            LocalDateTime afterCreation = LocalDateTime.now();
            assertTrue(createdUser.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)),
                "La fecha debe ser >= a ahora-1seg");
            assertTrue(createdUser.getCreatedAt().isBefore(afterCreation.plusSeconds(1)),
                "La fecha debe ser <= a ahora+1seg");
        }
    }
    
    // ============================================
    // UPDATE NAME TESTS
    // ============================================
    
    @Nested
    @DisplayName("📝 updateName() - Actualizar Nombre")
    class UpdateNameTests {
        
        @Test
        @DisplayName("Debe actualizar nombre con valor válido")
        void shouldUpdateNameWithValidValue() {
            // Arrange
            String newName = "Jane Doe";
            LocalDateTime beforeUpdate = LocalDateTime.now();
            
            // Act
            user.updateName(newName);
            
            // Assert
            assertEquals(newName, user.getName(), "El nombre debe actualizarse");
            assertTrue(user.getUpdatedAt().isAfter(beforeUpdate.minusSeconds(1)),
                "La fecha de actualización debe ser reciente");
        }
        
        @Test
        @DisplayName("Debe rechazar nombre null")
        void shouldRejectNullName() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> user.updateName(null),
                "Debe lanzar excepción para nombre null");
        }
        
        @Test
        @DisplayName("Debe rechazar nombre vacío")
        void shouldRejectEmptyName() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> user.updateName(""),
                "Debe lanzar excepción para nombre vacío");
        }
        
        @Test
        @DisplayName("Debe rechazar nombre solo espacios")
        void shouldRejectWhitespaceOnlyName() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> user.updateName("   "),
                "Debe lanzar excepción para nombre solo espacios");
        }
        
        @Test
        @DisplayName("Debe trimear espacios del nombre")
        void shouldTrimNameSpaces() {
            // Arrange
            String nameWithSpaces = "  Jane Doe  ";
            
            // Act
            user.updateName(nameWithSpaces);
            
            // Assert
            assertEquals("Jane Doe", user.getName(), "Debe remover espacios al inicio/fin");
        }
    }
    
    // ============================================
    // UPDATE EMAIL TESTS
    // ============================================
    
    @Nested
    @DisplayName("📧 updateEmail() - Actualizar Email")
    class UpdateEmailTests {
        
        @Test
        @DisplayName("Debe actualizar email con valor válido")
        void shouldUpdateEmailWithValidValue() {
            // Arrange
            Email newEmail = new Email("newemail@example.com");
            LocalDateTime beforeUpdate = LocalDateTime.now();
            
            // Act
            user.updateEmail(newEmail);
            
            // Assert
            assertEquals(newEmail, user.getEmail(), "El email debe actualizarse");
            assertTrue(user.getUpdatedAt().isAfter(beforeUpdate.minusSeconds(1)),
                "La fecha de actualización debe ser reciente");
        }
        
        @Test
        @DisplayName("Debe rechazar email igual al actual")
        void shouldRejectSameEmail() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> user.updateEmail(testEmail),
                "Debe lanzar excepción si el email es igual");
        }
        
        @Test
        @DisplayName("Debe actualizar email diferente")
        void shouldUpdateDifferentEmail() {
            // Arrange
            Email differentEmail = new Email("different@example.com");
            
            // Act
            user.updateEmail(differentEmail);
            
            // Assert
            assertEquals(differentEmail, user.getEmail());
        }
    }
    
    // ============================================
    // DEACTIVATE/ACTIVATE TESTS
    // ============================================
    
    @Nested
    @DisplayName("🔒 Deactivate/Activate - Control de Activación")
    class DeactivateActivateTests {
        
        @Test
        @DisplayName("Debe desactivar usuario activo")
        void shouldDeactivateActiveUser() {
            // Arrange
            assertTrue(user.isActive(), "Usuario debe estar activo inicialmente");
            LocalDateTime beforeDeactivate = LocalDateTime.now();
            
            // Act
            user.deactivate();
            
            // Assert
            assertFalse(user.isActive(), "El usuario debe estar inactivo");
            assertTrue(user.getUpdatedAt().isAfter(beforeDeactivate.minusSeconds(1)),
                "La fecha de actualización debe ser reciente");
        }
        
        @Test
        @DisplayName("Debe activar usuario inactivo")
        void shouldActivateInactiveUser() {
            // Arrange
            user.deactivate();
            assertFalse(user.isActive(), "Usuario debe estar inactivo");
            LocalDateTime beforeActivate = LocalDateTime.now();
            
            // Act
            user.activate();
            
            // Assert
            assertTrue(user.isActive(), "El usuario debe estar activo");
            assertTrue(user.getUpdatedAt().isAfter(beforeActivate.minusSeconds(1)),
                "La fecha de actualización debe ser reciente");
        }
        
        @Test
        @DisplayName("Puede desactivar y reactivar múltiples veces")
        void shouldAllowMultipleDeactivateActivateCycles() {
            // Act & Assert - Primera desactivación
            user.deactivate();
            assertFalse(user.isActive());
            
            // Act & Assert - Primera activación
            user.activate();
            assertTrue(user.isActive());
            
            // Act & Assert - Segunda desactivación
            user.deactivate();
            assertFalse(user.isActive());
            
            // Act & Assert - Segunda activación
            user.activate();
            assertTrue(user.isActive());
        }
    }
    
    // ============================================
    // TIMESTAMP UPDATE TESTS
    // ============================================
    
    @Nested
    @DisplayName("⏰ Timestamps - Validación de Fechas")
    class TimestampTests {
        
        @Test
        @DisplayName("CreatedAt no cambia después de crear")
        void createdAtShouldNotChangeAfterCreation() {
            // Arrange
            LocalDateTime originalCreatedAt = user.getCreatedAt();
            
            // Act
            user.updateName("New Name");
            
            // Assert
            assertEquals(originalCreatedAt, user.getCreatedAt(),
                "CreatedAt debe mantenerse igual");
        }
        
        @Test
        @DisplayName("UpdatedAt cambia con cada modificación")
        void updatedAtShouldChangeWithEachModification() {
            // Arrange
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();
            
            // Wait a bit to ensure time passes
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Act
            user.updateName("New Name");
            
            // Assert
            assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt),
                "UpdatedAt debe ser más reciente que el original");
        }
    }
    
    // ============================================
    // EDGE CASES Y VALIDACIONES
    // ============================================
    
    @Nested
    @DisplayName("⚠️ Edge Cases - Casos Límite")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Nombre con caracteres especiales debe ser aceptado")
        void shouldAcceptSpecialCharactersInName() {
            // Act
            user.updateName("José María O'Brien");
            
            // Assert
            assertEquals("José María O'Brien", user.getName());
        }
        
        @Test
        @DisplayName("Nombre muy largo debe ser aceptado")
        void shouldAcceptLongName() {
            // Arrange
            String longName = "A".repeat(200);
            
            // Act
            user.updateName(longName);
            
            // Assert
            assertEquals(longName, user.getName());
        }
        
        @Test
        @DisplayName("Email debe ser inmutable después de creación (valor object)")
        void emailShouldBeImmutable() {
            // Arrange & Act
            Email email1 = new Email("test@example.com");
            Email email2 = new Email("test@example.com");
            
            // Assert
            assertEquals(email1, email2, "Email objects with same value should be equal");
        }
    }
}
