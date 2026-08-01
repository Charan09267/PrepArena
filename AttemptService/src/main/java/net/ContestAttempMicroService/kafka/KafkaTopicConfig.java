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

    @Bean
    public ProducerFactory<String, AssessmentSubmittedEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();


        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        config.put(
                "spring.json.type.mapping",
                "assessmentSubmitted:net.ContestAttempMicroService.dto.AssessmentSubmittedEvent"
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, AssessmentSubmittedEvent> kafkaTemplate(
            ProducerFactory<String, AssessmentSubmittedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
    @Bean
    public NewTopic assessmentSubmittedTopic() {
        return TopicBuilder
                .name("assessment-submitted")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
