package net.edupilot.aiservice.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewEvaluationResponse {

    // Overall interview result
    private double overallScore;

    private String overallFeedback;

    // Review of every question, without individual scores
    private List<QuestionEvaluation> questionEvaluations;

    // Overall performance breakdown
    private AssessmentDetail technicalKnowledge;

    private AssessmentDetail conceptualUnderstanding;

    private AssessmentDetail answerCompleteness;

    private AssessmentDetail communicationClarity;

    private AssessmentDetail vocabularyAndTerminology;

    private AssessmentDetail relevanceAndConciseness;

    // Summary of the candidate's performance
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

        // Score for this performance dimension, out of 10
        private double score;

        private String feedback;
    }
}
