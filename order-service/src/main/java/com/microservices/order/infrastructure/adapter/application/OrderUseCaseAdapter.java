// package com.microservices.order.infrastructure.adapter.application;

// import com.microservices.order.application.dto.CreateOrderRequest;
// import com.microservices.order.application.dto.OrderResponse;
// import com.microservices.order.application.dto.UpdateOrderStatusRequest;
// import com.microservices.order.application.service.OrderService;
// import com.microservices.order.application.usecase.CreateOrderUseCase;
// import com.microservices.order.application.usecase.FindAllOrdersUseCase;
// import com.microservices.order.application.usecase.FindOrderByIdUseCase;
// import com.microservices.order.application.usecase.UpdateOrderStatusUseCase;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;

// /**
//  * 🔌 ORDER USE CASE ADAPTER
//  * 
//  * Adaptador de Infraestructura que expone los casos de uso del Application Layer
//  * a través de interfaces agnósticas de framework.
//  * 
//  * Responsabilidades:
//  * ├─ Implementar interfaces de USE CASES (no contaminadas con Spring)
//  * ├─ Aplicar reglas transaccionales de Spring (@Transactional)
//  * ├─ Delegación transparente al OrderService (sin lógica)
//  * └─ Ser un @Service de Spring (coordinación de infraestructura)
//  * 
//  * Nota: Los métodos son simple pass-through. La lógica de negocio
//  * está en OrderService (agnóstica de framework). Las transacciones
//  * se coordinan aquí, en la capa de infraestructura.
//  * 
//  * Diagrama de flujo:
//  *   Controller → UseCase Interface (agnóstica)
//  *            ↓
//  *       OrderUseCaseAdapter (@Service, @Transactional)
//  *            ↓
//  *       OrderService (lógica de negocio pura)
//  */
// @Service
// @Transactional(readOnly = true)
// public class OrderUseCaseAdapter implements
//         CreateOrderUseCase,
//         FindOrderByIdUseCase,
//         FindAllOrdersUseCase,
//         UpdateOrderStatusUseCase {

//     private final OrderService orderService;

//     public OrderUseCaseAdapter(OrderService orderService) {
//         this.orderService = orderService;
//     }

//     @Override
//     @Transactional  // Escritura: requiere transacción
//     public OrderResponse execute(CreateOrderRequest request) {
//         return orderService.create(request);
//     }

//     @Override
//     // Lectura: usa readOnly=true del nivel de clase
//     public OrderResponse execute(String orderId) {
//         return orderService.findById(orderId);
//     }

//     @Override
//     // Lectura: usa readOnly=true del nivel de clase
//     public List<OrderResponse> execute() {
//         return orderService.findAll();
//     }

//     @Override
//     @Transactional  // Escritura: requiere transacción
//     public OrderResponse execute(String orderId, UpdateOrderStatusRequest request) {
//         return orderService.updateStatus(orderId, request);
//     }
// }
