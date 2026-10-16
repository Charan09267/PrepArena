package net.edupilot.mockinterviewservice.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {

    private double overallScore;

    private String overallFeedback;

    private List<QuestionEvaluation> questionEvaluations;

    private AssessmentDetail technicalKnowledge;

    private AssessmentDetail conceptualUnderstanding;

    private AssessmentDetail answerCompleteness;

    private AssessmentDetail communicationClarity;

    private AssessmentDetail vocabularyAndTerminology;

    private AssessmentDetail relevanceAndConciseness;

    private List<String> strengths;

    private List<String> weaknesses;

    private List<String> suggestions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionEvaluation {

        private int questionNumber;

        private String question;

        private String candidateAnswer;

        private String feedback;

        private String betterAnswer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentDetail {

        private double score;

        private String feedback;
    }
}
