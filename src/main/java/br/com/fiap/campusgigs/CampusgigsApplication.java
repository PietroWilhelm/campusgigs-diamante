package br.com.fiap.campusgigs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CampusgigsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusgigsApplication.class, args);
    }

}
