package com.microservices.user.application.service;

import com.microservices.user.application.dto.CreateUserRequest;
import com.microservices.user.application.dto.UpdateUserRequest;
import com.microservices.user.application.dto.UserResponse;
import com.microservices.user.domain.exception.EmailAlreadyExistsException;
import com.microservices.user.domain.exception.UserNotFoundException;
import com.microservices.user.domain.model.Email;
import com.microservices.user.domain.model.User;
import com.microservices.user.domain.model.UserId;
import com.microservices.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 🧪 UNIT TESTS PARA USER SERVICE (Application Layer)
 * 
 * PROPÓSITO:
 * - Testear la LÓGICA DE APLICACIÓN (casos de uso)
 * - Usar MOCKS para las dependencias (puertos)
 * - Verificar orquestación de componentes
 */
@DisplayName("🧪 User Service (Application Layer) Tests")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private Email testEmail;
    private String testName;
    private UserId testUserId;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testEmail = new Email("john@example.com");
        testName = "John Doe";
        testUserId = UserId.generate();
        
        testUser = new User(
            testUserId,
            testEmail,
            testName,
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
        );
    }
    
    @Nested
    @DisplayName("✅ execute(CreateUserRequest) - Crear Usuario")
    class CreateUserTests {
        
        @Test
        @DisplayName("Debe crear usuario con email y nombre válidos")
        void shouldCreateUserWithValidEmailAndName() {
            CreateUserRequest request = new CreateUserRequest(
                "newuser@example.com",
                "New User"
            );
            
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(testUserId);
                    return user;
                });
            
            when(userRepository.existsByEmail(new Email("newuser@example.com")))
                .thenReturn(false);
            
            UserResponse response = userService.execute(request);
            
            assertNotNull(response);
            assertEquals("newuser@example.com", response.email());
            assertEquals("New User", response.name());
            assertTrue(response.active());
            
            verify(userRepository).save(any(User.class));
            verify(userRepository).existsByEmail(any(Email.class));
        }
        
        @Test
        @DisplayName("Debe rechazar email que ya existe")
        void shouldRejectDuplicateEmail() {
            CreateUserRequest request = new CreateUserRequest(
                testEmail.value(),
                "Another User"
            );
            
            when(userRepository.existsByEmail(testEmail))
                .thenReturn(true);
            
            assertThrows(EmailAlreadyExistsException.class,
                () -> userService.execute(request));
            
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Debe validar que email no sea null")
        void shouldValidateEmailIsNotNull() {
            CreateUserRequest request = new CreateUserRequest(null, "User");
            
            assertThrows(Exception.class,
                () -> userService.execute(request));
        }
        
        @Test
        @DisplayName("Debe validar que nombre no sea null")
        void shouldValidateNameIsNotNull() {
            CreateUserRequest request = new CreateUserRequest("email@example.com", null);
            
            assertThrows(Exception.class,
                () -> userService.execute(request));
        }
    }
    
    @Nested
    @DisplayName("🔍 execute(String) - Buscar Usuario por ID")
    class FindUserByIdTests {
        
        @Test
        @DisplayName("Debe encontrar usuario existente por ID")
        void shouldFindExistingUserById() {
            String userId = testUserId.value().toString();
            
            when(userRepository.findById(UserId.of(userId)))
                .thenReturn(Optional.of(testUser));
            
            UserResponse response = userService.execute(userId);
            
            assertNotNull(response);
            assertEquals(testEmail.value(), response.email());
            
            verify(userRepository).findById(any(UserId.class));
        }
        
        @Test
        @DisplayName("Debe lanzar excepción si usuario no existe")
        void shouldThrowExceptionIfUserNotFound() {
            String userId = UUID.randomUUID().toString();
            
            when(userRepository.findById(any(UserId.class)))
                .thenReturn(Optional.empty());
            
            assertThrows(UserNotFoundException.class,
                () -> userService.execute(userId));
        }
    }
    
    @Nested
    @DisplayName("📋 findAll() - Obtener Todos los Usuarios")
    class FindAllUsersTests {
        
        @Test
        @DisplayName("Debe retornar lista de usuarios")
        void shouldReturnListOfUsers() {
            List<User> users = new ArrayList<>();
            users.add(testUser);
            users.add(new User(
                UserId.generate(),
                new Email("jane@example.com"),
                "Jane Doe",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
            ));
            
            when(userRepository.findAllActive()).thenReturn(users);
            
            List<UserResponse> response = userService.execute();
            
            assertNotNull(response);
            assertEquals(2, response.size());
            
            verify(userRepository).findAllActive();
        }
        
        @Test
        @DisplayName("Debe retornar lista vacía si no hay usuarios")
        void shouldReturnEmptyListIfNoUsers() {
            when(userRepository.findAllActive()).thenReturn(new ArrayList<>());
            
            List<UserResponse> response = userService.execute();
            
            assertNotNull(response);
            assertTrue(response.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("📝 update(String, UpdateUserRequest) - Actualizar Usuario")
    class UpdateUserTests {
        
        @Test
        @DisplayName("Debe actualizar nombre del usuario")
        void shouldUpdateUserName() {
            String userId = testUserId.value().toString();
            // UpdateUserRequest(email, name) - parámetros en orden correcto
            UpdateUserRequest request = new UpdateUserRequest(null, "Jane Doe");
            
            // Usuario existente
            User existingUser = new User(
                testUserId,
                testEmail,
                "John Doe",  // Nombre actual
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
            );
            
            when(userRepository.findById(any(UserId.class)))
                .thenReturn(Optional.of(existingUser));
            
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
            
            UserResponse response = userService.execute(userId, request);
            
            assertNotNull(response);
            assertEquals("Jane Doe", response.name());
            assertEquals(testEmail.value(), response.email());
            
            verify(userRepository).save(any(User.class));
        }
        
        @Test
        @DisplayName("Debe lanzar excepción si usuario no existe")
        void shouldThrowExceptionIfUserNotFoundOnUpdate() {
            String userId = UUID.randomUUID().toString();
            // UpdateUserRequest(email, name) - orden correcto
            UpdateUserRequest request = new UpdateUserRequest(null, "New Name");
            
            when(userRepository.findById(any(UserId.class)))
                .thenReturn(Optional.empty());
            
            assertThrows(UserNotFoundException.class,
                () -> userService.execute(userId, request));
            
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Debe actualizar email del usuario")
        void shouldUpdateUserEmail() {
            String userId = testUserId.value().toString();
            String newEmail = "jane@example.com";
            // UpdateUserRequest(email, name) - orden correcto
            UpdateUserRequest request = new UpdateUserRequest(newEmail, null);
            
            // Usuario existente
            User existingUser = new User(
                testUserId,
                new Email("john@example.com"),  // Email actual
                "John Doe",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
            );
            
            when(userRepository.findById(any(UserId.class)))
                .thenReturn(Optional.of(existingUser));
            
            when(userRepository.existsByEmail(new Email(newEmail)))
                .thenReturn(false);
            
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
            
            UserResponse response = userService.execute(userId, request);
            
            assertNotNull(response);
            assertEquals(newEmail, response.email());
            assertEquals("John Doe", response.name());
            
            verify(userRepository).save(any(User.class));
        }
    }
    
    @Nested
    @DisplayName("🔗 Integration de Mocks - Verificar Interacciones")
    class MockInteractionTests {
        
        @Test
        @DisplayName("Debe verificar que save fue llamado")
        void shouldVerifySaveWasCalled() {
            CreateUserRequest request = new CreateUserRequest(
                "test@example.com",
                "Test User"
            );
            
            when(userRepository.existsByEmail(any(Email.class)))
                .thenReturn(false);
            
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(testUserId);
                    return user;
                });
            
            userService.execute(request);
            
            verify(userRepository, times(1)).save(any(User.class));
        }
        
        @Test
        @DisplayName("Mock debe ser reseteado entre tests")
        void mockShouldBeResetBetweenTests() {
            verify(userRepository, never()).findAll();
        }
    }
    
    @Nested
    @DisplayName("✓ Comportamiento - Verificar flujos complejos")
    class BehaviorVerificationTests {
        
        @Test
        @DisplayName("Debe no llamar a save si solo estamos buscando")
        void shouldNotCallSaveWhenSearching() {
            when(userRepository.findById(any(UserId.class)))
                .thenReturn(Optional.of(testUser));
            
            userService.execute(testUserId.value().toString());
            
            verify(userRepository, never()).save(any(User.class));
        }
        
        @Test
        @DisplayName("Debe no llamar a métodos innecesarios")
        void shouldNotCallUnnecessaryMethods() {
            List<User> users = List.of(testUser);
            when(userRepository.findAllActive()).thenReturn(users);
            
            userService.execute();
            
            verify(userRepository).findAllActive();
            verify(userRepository, never()).findById(any());
            verify(userRepository, never()).save(any());
        }
    }
}
