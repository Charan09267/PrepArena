package net.company.evaluationservice.Client;

import net.company.evaluationservice.dto.EvaluationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "${attempt.service.name}")
public interface AttemptFeignClient {

    /**
     * Updates the evaluation result associated with a participant's attempt.
     *
     * @param participationId identifier used by the Attempt Service endpoint
     * @param result          calculated evaluation result
     */
    @PutMapping("/attempts/{participationId}/evaluation")
    void updateEvaluation(
            @PathVariable Long participationId,
            @RequestBody EvaluationResult result
    );
}
