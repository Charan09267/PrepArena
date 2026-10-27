package net.ContestAttempMicroService.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ContestAttempMicroService.dto.AssessmentSubmittedEvent;
import net.ContestAttempMicroService.dto.SubmitRequestDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.CompletableFuture;


@Component
@RequiredArgsConstructor
@Slf4j
public class AssessmentEventProducer {

    private static final String TOPIC = "assessment-submitted";

    private final KafkaTemplate<String, AssessmentSubmittedEvent> kafkaTemplate;

    /**
     * Publishes a submitted assessment event to Kafka.
     *
     * The user ID is used as the message key so events associated with
     * the same user are routed consistently to a Kafka partition.
     *
     * @param event the assessment submission event to publish
     * @return a future containing the Kafka send result
     */
    public CompletableFuture<SendResult<String, AssessmentSubmittedEvent>> publish(
            AssessmentSubmittedEvent event) {

        return kafkaTemplate.send(
                TOPIC,
                event.getUserId().toString(), // Kafka message key
                event                     // Kafka message value
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                        "Assessment submission event published successfully. Topic: {}, User ID: {}",
                        TOPIC,
                        event.getUserId()
                );
            } else {
                log.error(
                        "Failed to publish assessment submission event. Topic: {}, User ID: {}",
                        TOPIC,
                        event.getUserId(),
                        ex
                );
            }
        });
    }
}
