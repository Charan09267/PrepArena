package net.company.evaluationservice.integration;

import net.company.evaluationservice.Client.AttemptFeignClient;
import net.company.evaluationservice.Client.ContestFeignClient;
import net.company.evaluationservice.EvaluationServiceApplication;
import net.company.evaluationservice.dto.AnswerKeyDto;
import net.company.evaluationservice.dto.AssessmentSubmittedEvent;
import net.company.evaluationservice.dto.EvaluationResult;
import net.company.evaluationservice.entity.QuestionType;

import net.company.evaluationservice.kafka.AssessmentSubmittedConsumer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = EvaluationServiceApplication.class)
@EmbeddedKafka(
        partitions = 1,
        topics = "assessment-submitted"
)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*"
})
class AssessmentSubmittedConsumerIntegrationTest {


    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;


    @MockitoBean
    private ContestFeignClient contestFeignClient;

    @MockitoBean
    private AttemptFeignClient attemptFeignClient;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    private KafkaTemplate<String, AssessmentSubmittedEvent> kafkaTemplate;

    @Autowired
    private AssessmentSubmittedConsumer consumer;




    @BeforeEach
    void setup() {
        // Now embeddedKafkaBroker will be safely injected by the time @BeforeEach runs
        Map<String, Object> props =
                KafkaTestUtils.producerProps(embeddedKafkaBroker);

        ProducerFactory<String, AssessmentSubmittedEvent> producerFactory =
                new DefaultKafkaProducerFactory<>(
                        props,
                        new StringSerializer(),
                        new JacksonJsonSerializer<>()
                );

        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    @BeforeEach
    void waitForKafka() {
        // Wait until all containers have their partitions assigned
        for (MessageListenerContainer container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test
    void consumerLoaded() {
        assertNotNull(consumer);
    }

    @Test
    void shouldProcessAssessmentSubmittedEvent() throws Exception {


        // Mock contest service response BEFORE sending Kafka event
        when(contestFeignClient.getAnswerKey(1L))
                .thenReturn(
                        List.of(
                                new AnswerKeyDto(
                                        101L,
                                        List.of(1L),
                                        5,
                                        QuestionType.MCQ
                                )
                        )
                );


        AssessmentSubmittedEvent event =
                AssessmentSubmittedEvent.builder()
                        .contestId(1L)
                        .participantId(10L)
                        .userId(20L)

                        // Replace with your actual submitted answer DTO
                        // Example:
                        // .answers(List.of(
                        //       new SubmittedAnswerDto(101L, List.of(1L))
                        // ))
                        .answers(List.of())

                        .submittedAt(LocalDateTime.now())
                        .build();



        // Send Kafka message and wait until published
        kafkaTemplate.send(
                "assessment-submitted",
                "20",
                event
        ).get();



        // Verify consumer called contest service
        verify(contestFeignClient, timeout(5000))
                .getAnswerKey(1L);



        // Verify evaluation result update
        verify(attemptFeignClient, timeout(5000))
                .updateEvaluation(any(EvaluationResult.class));
    }
}