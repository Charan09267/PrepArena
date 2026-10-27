package net.edupilot.mockinterviewservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


/**
 * Configures the Spring REST client used for HTTP communication
 * with external services.
 */
@Configuration
public class RestClientConfig {

    /**
     * Provides a builder for creating REST client instances.
     *
     * @return REST client builder
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Creates the REST client used by application components.
     *
     * @param builder configured REST client builder
     * @return REST client instance
     */
    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}
