package com.microservices.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 🚀 USER SERVICE - Punto de Entrada
 * 
 * Esta es la clase principal que inicia el microservicio de usuarios.
 * 
 * @SpringBootApplication hace 3 cosas:
 * 1. @Configuration - Permite definir beans
 * 2. @EnableAutoConfiguration - Configuración automática
 * 3. @ComponentScan - Busca componentes en el paquete
 * 
 * ¿Qué hace este servicio?
 * - Gestiona usuarios (crear, leer, actualizar, eliminar)
 * - Aplica Arquitectura Hexagonal
 * - Se comunica con otros microservicios
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        
        System.out.println("""
            
            ╔════════════════════════════════════════╗
            ║   USER SERVICE INICIADO CON ÉXITO ✅   ║
            ╠════════════════════════════════════════╣
            ║  Puerto: 8081                          ║
            ║  Consola H2: /h2-console               ║
            ║  Actuator: /actuator                   ║
            ╚════════════════════════════════════════╝
            
            """);
    }
}
