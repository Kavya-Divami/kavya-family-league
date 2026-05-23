package com.familyleague;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class FamilyLeagueApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyLeagueApplication.class, args);
    }
}
