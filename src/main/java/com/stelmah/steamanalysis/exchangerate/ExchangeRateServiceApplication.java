package com.stelmah.steamanalysis.exchangerate;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.TimeZone;

@SpringBootApplication
@EnableFeignClients
public class ExchangeRateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRateServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Setting App default time zone to UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
