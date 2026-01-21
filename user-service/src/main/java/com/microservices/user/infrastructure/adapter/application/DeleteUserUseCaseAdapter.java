package com.microservices.user.infrastructure.adapter.application;

import com.microservices.user.application.service.DeleteUserService;
import com.microservices.user.application.usecase.DeleteUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptador de infraestructura para el caso de uso de eliminación.
 */
@Service
@RequiredArgsConstructor
public class DeleteUserUseCaseAdapter implements DeleteUserUseCase {

    private final DeleteUserService deleteUserService;

    @Override
    @Transactional
    public void execute(String userId) {
        deleteUserService.execute(userId);
    }
}
