package net.ContestAttempMicroService.kafka;

import net.ContestAttempMicroService.dto.AssessmentSubmittedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Configures the Kafka producer for assessment submission events.
     *
     * Uses String serialization for message keys and JSON serialization
     * for event payloads.
     *
     * @return the configured producer factory
     */
    @Bean
    public ProducerFactory<String, AssessmentSubmittedEvent> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        // Kafka broker addresses.
        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        // Serialize Kafka message keys as strings.
        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        // Serialize assessment events as JSON.
        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JacksonJsonSerializer.class
        );

        // Map the event type identifier to the event class for JSON type handling.
        config.put(
                "spring.json.type.mapping",
                "assessmentSubmitted:net.ContestAttempMicroService.dto.AssessmentSubmittedEvent"
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Creates a Kafka template for publishing assessment submission events.
     *
     * @param producerFactory the configured Kafka producer factory
     * @return the Kafka template used by the event producer
     */
    @Bean
    public KafkaTemplate<String, AssessmentSubmittedEvent> kafkaTemplate(
            ProducerFactory<String, AssessmentSubmittedEvent> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Declares the Kafka topic used for assessment submission events.
     *
     * Creates the topic with three partitions and one replica per partition.
     *
     * @return the topic configuration
     */
    @Bean
    public NewTopic assessmentSubmittedTopic() {

        return TopicBuilder
                .name("assessment-submitted")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
