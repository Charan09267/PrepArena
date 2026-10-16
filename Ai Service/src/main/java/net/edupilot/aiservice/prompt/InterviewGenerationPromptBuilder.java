package net.edupilot.aiservice.prompt;

import net.edupilot.aiservice.dto.request.InterviewGenerationRequest;
import net.edupilot.aiservice.dto.request.NextQuestionRequest;
import org.springframework.stereotype.Component;

@Component
public class InterviewGenerationPromptBuilder {

    public String buildPrompt(InterviewGenerationRequest request) {

        String customPrompt = request.getCustomPrompt();

        if (customPrompt == null || customPrompt.isBlank()) {
            customPrompt = "No additional instructions provided.";
        }

        return """
                You are an experienced technical interviewer.

                Generate interview questions for the candidate based on the
                following requirements.

                Role:
                %s

                Topics:
                %s

                Number of Questions:
                %d

                Interview Time Limit:
                %d minutes

                Additional Instructions:
                %s

                Requirements:

                1. Generate exactly %d questions.
                2. Questions should match the candidate's role.
                3. Cover the provided topics.
                4. Start with moderate difficulty.
                5. Gradually increase the difficulty.
                6. Avoid duplicate questions.
                7. Questions should test understanding rather than memorization.
                8. Include practical or scenario-based questions where appropriate.
                9. Keep each question clear and concise.
                10. Number the questions sequentially starting from 1.

                IMPORTANT RESPONSE FORMAT:

                Return ONLY a valid JSON object.

                The JSON object MUST contain a field named "questions".

                The "questions" field MUST be an array of objects.

                Each question object MUST contain exactly these two fields:

                - "questionNumber": the sequential question number.
                - "question": the actual interview question.

                The response MUST follow exactly this structure:

                {
                  "questions": [
                    {
                      "questionNumber": 1,
                      "question": "First interview question"
                    },
                    {
                      "questionNumber": 2,
                      "question": "Second interview question"
                    },
                    {
                      "questionNumber": 3,
                      "question": "Third interview question"
                    }
                  ]
                }

                Rules:

                - Generate exactly %d question objects.
                - questionNumber must start from 1.
                - questionNumber must increase sequentially.
                - Each question must be a string.
                - Do not return questions as plain strings.
                - Do not return a JSON array directly.
                - Do not add any fields other than "questions", "questionNumber", and "question".
                - Do not use markdown.
                - Do not include explanations outside the JSON.
                - Return valid JSON only.
                """.formatted(
                request.getRole(),
                request.getTopic(),
                request.getNumberOfQuestions(),
                request.getTimeLimit(),
                customPrompt,
                request.getNumberOfQuestions(),
                request.getNumberOfQuestions()
        );
    }

    public String buildNextQuestionPrompt(NextQuestionRequest request) {

        StringBuilder history = new StringBuilder();

        request.getConversationHistory().forEach(turn -> {
            history.append("\nQuestion ")
                    .append(turn.getQuestionNumber())
                    .append(": ")
                    .append(turn.getQuestion())
                    .append("\nCandidate's answer: ")
                    .append(turn.getAnswer() == null
                            ? "Not answered yet"
                            : turn.getAnswer())
                    .append("\n");
        });

        return """
            You are an experienced technical interviewer.

            INTERVIEW CONFIGURATION
            Target role: %s
            Experience level: %s
            Interview instructions: %s
            Total question limit: %d
            Questions already asked: %d
            Interview duration: %d minutes

            CONVERSATION HISTORY
            %s

            YOUR TASK
            Generate exactly ONE next interview question.

            RULES
            1. Base the next question on the candidate's previous answers.
            2. Ask a relevant follow-up when the answer reveals a topic
               that deserves deeper exploration.
            3. Follow the interview instructions and target role.
            4. Do not repeat a question already asked.
            5. Adjust the difficulty to the candidate's experience level.
            6. Ask a new relevant topic if the previous answer does not
               provide a useful direction for a follow-up.
            7. Do not answer the question yourself.
            8. Do not provide feedback, explanations, greetings, or numbering.
            9. Return only the next question.
            """.formatted(
                request.getTargetRole(),
                request.getExperienceLevel(),
                request.getInterviewInstructions() == null
                        ? "No additional instructions"
                        : request.getInterviewInstructions(),
                request.getQuestionLimit(),
                request.getQuestionsAsked(),
                request.getDurationMinutes(),
                history
        );
    }

}