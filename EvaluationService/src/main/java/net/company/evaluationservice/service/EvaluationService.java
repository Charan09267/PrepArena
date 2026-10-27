package net.company.evaluationservice.service;

import net.company.evaluationservice.dto.AssessmentSubmittedEvent;

/**
 * Defines the operations for evaluating submitted assessments.
 */
public interface EvaluationService {

    /**
     * Evaluates a submitted assessment against its official answer key
     * and updates the corresponding attempt with the evaluation result.
     *
     * @param event assessment submission event containing participant and answer details
     */
    void evaluate(AssessmentSubmittedEvent event);

}
