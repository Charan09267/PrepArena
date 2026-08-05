package net.company.evaluationservice.Client;

import net.company.evaluationservice.dto.AnswerKeyDto;
import net.company.evaluationservice.dto.EvaluationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "${contest.service.name}")
public interface ContestFeignClient {
    @GetMapping("/contests/{contestId}/answer-key")
    List<AnswerKeyDto> getAnswerKey(
            @PathVariable Long contestId
    );


}
