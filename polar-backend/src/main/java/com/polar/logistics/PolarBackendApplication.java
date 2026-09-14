package com.polar.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PolarBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolarBackendApplication.class, args);
    }
}
