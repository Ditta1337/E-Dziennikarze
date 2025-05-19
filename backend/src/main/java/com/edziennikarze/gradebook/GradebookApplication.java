package com.edziennikarze.gradebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;

@SpringBootApplication(
        exclude = ManagementWebSecurityAutoConfiguration.class
)
public class GradebookApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradebookApplication.class, args);
    }

}
