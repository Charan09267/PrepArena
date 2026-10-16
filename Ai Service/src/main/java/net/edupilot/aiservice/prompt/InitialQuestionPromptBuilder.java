package net.edupilot.aiservice.prompt;

import net.edupilot.aiservice.dto.request.InitialQuestionRequest;
import org.springframework.stereotype.Component;

@Component
public class InitialQuestionPromptBuilder {

    public String build(InitialQuestionRequest request) {

        return """
                You are conducting a technical interview.

                Target Role: %s
                Experience Level: %s
                Interview Instructions: %s
                Maximum Questions: %d
                Interview Duration: %d minutes

                Generate the FIRST interview question.

                Requirements:
                - Ask only one question.
                - The question must be relevant to the target role.
                - Consider the candidate's experience level.
                - Follow the interview instructions.
                - Do not provide the answer.
                - Do not provide explanations.
                - Return only the interview question.
                """.formatted(
                request.getTargetRole(),
                request.getExperienceLevel(),
                request.getInterviewInstructions(),
                request.getQuestionLimit(),
                request.getDurationMinutes()
        );
    }



}