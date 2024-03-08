package com.fts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FTSMainApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext ac = SpringApplication.run(FTSMainApp.class, args);
    }
}
