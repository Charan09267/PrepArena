CREATE TABLE interview_results(
    id BIGINT NOT NULL AUTO_INCREMENT,
    interview_id BIGINT NOT NULL,
    overall_score DECIMAL(5,2) NOT NULL,
    evaluated_at DATETIME NOT NULL,

    PRIMARY KEY(id),

    CONSTRAINT fk_interview_result_interview
                              FOREIGN KEY (interview_id)
                              REFERENCES interviews(id),

    CONSTRAINT uk_interview_result_interview
                              UNIQUE(interview_id)
);