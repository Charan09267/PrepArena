package net.UserService.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration

@EnableMongoAuditing
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void createIndexes() {
        mongoTemplate.indexOps("user_profiles").createIndex(
                new Index()
                        .on("userId", Sort.Direction.ASC)
                        .unique()
                        .named("uk_user_profiles_user_id")
        );
    }
}
