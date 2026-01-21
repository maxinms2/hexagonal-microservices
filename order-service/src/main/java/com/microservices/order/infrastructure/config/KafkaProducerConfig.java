package com.microservices.order.infrastructure.config;

import com.microservices.order.domain.event.OrderCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaProducerConfig - Configuración del Productor Kafka para Order Service
 * 
 * Configura CÓMO se envían los mensajes a Kafka.
 * 
 * 🍎 Analogía: Es como configurar cómo enviar paquetes:
 *    - Correo: Kafka broker (bootstrap-servers)
 *    - Empaque: Serializer (cómo empacar el evento)
 *    - Garantías: acks = "all" (confirmar que llegó)
 */
@Configuration
public class KafkaProducerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    /**
     * ProducerFactory: Fábrica que crea productores de Kafka
     * 
     * Configura:
     * - DÓNDE enviar (bootstrap-servers)
     * - CÓMO empacar (serializers)
     * - GARANTÍAS de entrega (acks, retries)
     */
    @Bean
    public ProducerFactory<String, OrderCreatedEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Broker de Kafka
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Serializer de la clave (ID de la orden)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        // Serializer del valor (el evento OrderCreatedEvent a JSON)
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Garantía de entrega: "all" = esperar a que se replique en todos los brokers
        // Opciones:
        // - "0" (acks=0): No esperar confirmación (rápido pero risky)
        // - "1" (acks=1): Leader confirma (balanc entre velocidad y seguridad)
        // - "all" (acks=-1): Todos los replicas confirman (seguro pero lento)
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        
        // Reintentos si falla el envío
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        
        // Esperar máximo 30 segundos para confirmar
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        
        // Compresión de mensajes (reduce ancho de banda)
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        
        // Batch size: acumular mensajes antes de enviar (mejora throughput)
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        
        // Linger: esperar hasta X ms para acumular mensajes (mejora throughput)
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    /**
     * KafkaTemplate: Template para enviar mensajes a Kafka
     * 
     * Lo inyectamos en los adaptadores y usamos:
     * kafkaTemplate.send("topic", message)
     */
    @Bean
    public KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
