package com.core.notificationservice.configuration;

import com.core.notificationservice.dto.OrderPlacedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public NewTopic orderPlacedTopic() {
        return TopicBuilder.name("order-placed-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Đăng ký JavaTimeModule để hỗ trợ LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        log.info("ObjectMapper configured with JavaTimeModule - Registered modules: {}", mapper.getRegisteredModuleIds());
        return mapper;
    }

    @Bean
    public JsonDeserializer<OrderPlacedEvent> jsonDeserializer(ObjectMapper objectMapper) {
        // Tạo JsonDeserializer với ObjectMapper đã cấu hình JavaTimeModule
        JsonDeserializer<OrderPlacedEvent> deserializer = new JsonDeserializer<>(OrderPlacedEvent.class, objectMapper);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        log.info("JsonDeserializer created with ObjectMapper - Registered modules: {}", objectMapper.getRegisteredModuleIds());
        return deserializer;
    }

    @Bean
    public ConsumerFactory<String, OrderPlacedEvent> consumerFactory(JsonDeserializer<OrderPlacedEvent> jsonDeserializer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Sử dụng ErrorHandlingDeserializer để xử lý lỗi deserialization
        ErrorHandlingDeserializer<String> keyDeserializer = new ErrorHandlingDeserializer<>(new StringDeserializer());
        ErrorHandlingDeserializer<OrderPlacedEvent> valueDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer.getClass());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getClass());
        
        return new DefaultKafkaConsumerFactory<String, OrderPlacedEvent>(props, keyDeserializer, valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> kafkaListenerContainerFactory(ConsumerFactory<String, OrderPlacedEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        // Set ConsumerFactory
        factory.setConsumerFactory(consumerFactory);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            new FixedBackOff(1000L, 3L) // Retry 3 lần, mỗi lần cách nhau 1 giây
        );
        factory.setCommonErrorHandler(errorHandler);
        
        return factory;
    }
}
