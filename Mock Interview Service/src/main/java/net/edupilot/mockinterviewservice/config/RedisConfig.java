package net.edupilot.mockinterviewservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Configures Redis connectivity and serialization for the application.
 *
 * <p>Uses string serialization for Redis keys and hash keys, and JSON
 * serialization for values and hash values.</p>
 */
@Configuration
public class RedisConfig {

    /**
     * Creates the Redis template used to read and write application data.
     *
     * @param connectionFactory Redis connection factory configured by Spring
     * @return configured Redis template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        // Redis keys remain readable strings.
        StringRedisSerializer stringSerializer =
                new StringRedisSerializer();

        // Store values as JSON so structured objects can be serialized.
        JacksonJsonRedisSerializer<Object> jsonSerializer =
                new JacksonJsonRedisSerializer<>(Object.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);

        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        // Initialize the template after applying its connection and serializers.
        template.afterPropertiesSet();

        return template;
    }
}
