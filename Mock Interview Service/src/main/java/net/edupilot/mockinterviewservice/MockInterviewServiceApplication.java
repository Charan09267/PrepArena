package net.edupilot.mockinterviewservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MockInterviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockInterviewServiceApplication.class, args);
    }

}
