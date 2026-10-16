CREATE TABLE interviews (
                            id BIGINT NOT NULL AUTO_INCREMENT,

                            user_id BIGINT NOT NULL,

                            type VARCHAR(20) NOT NULL,
                            status VARCHAR(20) NOT NULL,

                            target_role VARCHAR(100),
                            experience_level VARCHAR(50),

                            duration_minutes INT NOT NULL,
                            question_limit INT NOT NULL,

                            programming_language VARCHAR(50),
                            difficulty VARCHAR(30),
                            topic VARCHAR(100),

                            started_at DATETIME,
                            ended_at DATETIME,
                            created_at DATETIME NOT NULL,

                            PRIMARY KEY (id)
);