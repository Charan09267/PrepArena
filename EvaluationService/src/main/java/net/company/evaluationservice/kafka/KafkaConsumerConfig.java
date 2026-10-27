package net.company.evaluationservice.kafka;

import org.springframework.beans.factory.annotation.Value;
import net.company.evaluationservice.dto.AssessmentSubmittedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates the Kafka consumer factory used to deserialize assessment events.
     *
     * @return configured consumer factory
     */
    @Bean
    public ConsumerFactory<String, AssessmentSubmittedEvent> consumerFactory() {

        JacksonJsonDeserializer<AssessmentSubmittedEvent> deserializer =
                new JacksonJsonDeserializer<>(AssessmentSubmittedEvent.class);

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "assessment-service-group"
        );
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JacksonJsonDeserializer.class
        );
        props.put(
                "spring.json.type.mapping",
                "assessmentSubmitted:net.company.evaluationservice.dto.AssessmentSubmittedEvent"
        );

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    /**
     * Configures the listener container factory for assessment event consumers.
     *
     * @return configured Kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AssessmentSubmittedEvent>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AssessmentSubmittedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}