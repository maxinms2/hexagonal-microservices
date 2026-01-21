package com.microservices.notification.infrastructure.config;

import com.microservices.notification.domain.event.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaConsumerConfig - Configuración de Kafka para el Notification Service
 * 
 * Esta clase configura CÓMO los mensajes de Kafka serán deserializados
 * y procesados por Spring Kafka.
 * 
 * 🍎 Analogía: Es como configurar cómo recibir paquetes:
 *    - Dirección: bootstrap-servers (dónde vive el cartero)
 *    - Forma de abrir paquetes: Deserializer (cómo abrir el paquete)
 *    - A qué grupo perteneces: group-id (aceptas paquetes con nombre X)
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    
    /**
     * Inyectamos valores de application.yml
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    
    /**
     * ConsumerFactory: Fábrica que crea instancias de KafkaConsumer
     * 
     * Configura:
     * - DÓNDE conectarse (bootstrap-servers)
     * - CÓMO entender los mensajes (deserializers)
     * - COMPORTAMIENTO (auto-offset-reset, group-id)
     */
    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        
        // Broker de Kafka
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Grupo de consumidores
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        // ¿Qué hacer si no tenemos offset guardado?
        // "earliest" = leer desde el principio (útil para nuevos servicios)
        // "latest" = leer solo nuevos mensajes
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Key Deserializer: Cómo deserializar la CLAVE del mensaje
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // Value Deserializer: Cómo deserializar el VALOR (nuestro evento JSON)
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // Permitir deserialización de clases desconocidas (para versioning)
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        // Tipo de valor a deserializar
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreatedEvent.class.getName());
        
        // Permitir fallos de tipo y seguir (para evolucionar eventos)
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        // Máximo de registros por poll
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        
        // Session timeout
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    /**
     * ConcurrentKafkaListenerContainerFactory: Contenedor para procesar mensajes
     * 
     * Este bean:
     * - Escucha múltiples particiones en paralelo
     * - Manage retries automáticamente
     * - Maneja confirmación de mensajes (acks)
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> 
        kafkaListenerContainerFactory() {
        
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(consumerFactory());
        
        // Número de threads para procesar mensajes en paralelo
        factory.setConcurrency(3);
        
        // AckMode: Cuándo confirmar que el mensaje fue procesado
        // RECORD: Confirmar después de procesar cada registro
        // Es la más segura pero más lenta
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        
        // Habilitar batch processing (procesar múltiples mensajes a la vez)
        // factory.setBatchListener(true);
        
        return factory;
    }
}
