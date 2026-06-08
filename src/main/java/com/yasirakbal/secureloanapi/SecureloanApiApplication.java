package com.yasirakbal.secureloanapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SecureloanApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureloanApiApplication.class, args);
    }

}
