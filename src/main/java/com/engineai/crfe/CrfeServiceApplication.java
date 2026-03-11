package com.engineai.crfe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CrfeServiceApplication {

    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));

        SpringApplication.run(CrfeServiceApplication.class, args);
    }
}