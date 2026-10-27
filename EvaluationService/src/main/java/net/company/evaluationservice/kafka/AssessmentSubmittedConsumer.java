package net.company.evaluationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.company.evaluationservice.dto.AssessmentSubmittedEvent;
import net.company.evaluationservice.service.EvaluationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentSubmittedConsumer {

    private final EvaluationService evaluationService;

    /**
     * Consumes assessment submission events from Kafka and initiates evaluation.
     *
     * @param event submitted assessment details
     * @param key   Kafka message key
     */
    @KafkaListener(
            topics = "assessment-submitted",
            groupId = "assessment-service-group"
    )
    public void consume(
            AssessmentSubmittedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        log.info("Received assessment submission event: {}", event);

        evaluationService.evaluate(event);
    }
}
