package com.microservices.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Aplicación principal del Notification Service
 * 
 * Este microservicio es un ejemplo de patrón Event-Driven Architecture.
 * Escucha eventos de Kafka y envía notificaciones
 * 
 * @EnableKafka: Activa el soporte para listeners de Kafka en Spring
 * @EnableDiscoveryClient: Se registra automáticamente en Eureka
 */
@SpringBootApplication
@EnableKafka
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
