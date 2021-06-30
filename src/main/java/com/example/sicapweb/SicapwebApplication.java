package com.example.sicapweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"br.gov.to.tce.*"})
public class SicapwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SicapwebApplication.class, args);
    }
}
