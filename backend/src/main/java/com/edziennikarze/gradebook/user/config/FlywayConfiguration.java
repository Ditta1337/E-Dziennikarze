package com.edziennikarze.gradebook.user.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class FlywayConfiguration {

    @Value("${spring.flyway.url}")
    private String flywayUrl;

    @Value("${spring.flyway.user}")
    private String flywayUser;

    @Value("${spring.flyway.password}")
    private String flywayPassword;

    @Bean
    @FlywayDataSource
    @ConfigurationProperties(prefix = "spring.flyway")
    public DataSource flywayDataSource() {

        return DataSourceBuilder.create()
                .url(flywayUrl)
                .username(flywayUser)
                .password(flywayPassword)
                .driverClassName("org.postgresql.Driver")
                .type(HikariDataSource.class)
                .build();
    }
}
