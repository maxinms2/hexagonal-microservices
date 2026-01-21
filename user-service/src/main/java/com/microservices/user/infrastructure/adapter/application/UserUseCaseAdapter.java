package com.microservices.user.infrastructure.adapter.application;

import com.microservices.user.application.dto.CreateUserRequest;
import com.microservices.user.application.dto.UpdateUserRequest;
import com.microservices.user.application.dto.UserResponse;
import com.microservices.user.application.service.UserService;
import com.microservices.user.application.usecase.CreateUserUseCase;
import com.microservices.user.application.usecase.FindAllUsersUseCase;
import com.microservices.user.application.usecase.FindUserByIdUseCase;
import com.microservices.user.application.usecase.UpdateUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Adaptador de infraestructura que expone los casos de uso de usuario
 * y aplica las reglas transaccionales de Spring.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserUseCaseAdapter implements
        CreateUserUseCase,
        FindUserByIdUseCase,
        FindAllUsersUseCase,
        UpdateUserUseCase {

    private final UserService userService;

    @Override
    @Transactional
    public UserResponse execute(CreateUserRequest request) {
        return userService.execute(request);
    }

    @Override
    public UserResponse execute(String userId) {
        return userService.execute(userId);
    }

    @Override
    public List<UserResponse> execute() {
        return userService.execute();
    }

    @Override
    @Transactional
    public UserResponse execute(String userId, UpdateUserRequest request) {
        return userService.execute(userId, request);
    }
}
