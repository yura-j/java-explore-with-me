package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum.ewm")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}