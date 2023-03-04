package com.isikato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class IsikatoApplication {

    public static void main(String[] args) {
        SpringApplication.run(IsikatoApplication.class, args);
    }

}
