package com.icebank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WelcomeLogger implements CommandLineRunner {

    @Value("${app.base.url}")
    private String baseUrl;

    private final Environment environment;

    public WelcomeLogger(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {

        boolean isLocal = Arrays.asList(environment.getActiveProfiles())
                .contains("local");

        StringBuilder message = new StringBuilder("""
                
                ----------------------------------------------------------
                  BANKING APP IS LIVE!
                  URL: %s
                  Swagger: %s/swagger-ui/index.html
                """.formatted(baseUrl, baseUrl));

        if (isLocal) {
            message.append("""                  
                  To start Database: docker compose up -d postgres
                  Stop and remove containers: docker compose down
                  To read accounts in DB:
                      docker exec -it icebank-postgres psql -U postgres -d icebank
                      SELECT * FROM account;
                """.formatted(baseUrl, baseUrl));
        }

        message.append("""
                ----------------------------------------------------------
                """);

        System.out.println(message);
    }
}
