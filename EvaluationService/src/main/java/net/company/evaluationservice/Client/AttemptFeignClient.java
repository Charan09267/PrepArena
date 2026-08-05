package net.company.evaluationservice.Client;

import net.company.evaluationservice.dto.EvaluationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${attempt.service.name}")
public interface AttemptFeignClient {

    @PutMapping("/attempts/{participationId}/evaluation")
    void updateEvaluation(
            @PathVariable Long participationId,
            @RequestBody EvaluationResult result
    );

}
