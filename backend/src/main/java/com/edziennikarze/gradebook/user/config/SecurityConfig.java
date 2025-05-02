package com.edziennikarze.gradebook.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        // you can choose to lock down /actuator/** too:
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        //.pathMatchers("/actuator/**").hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .httpBasic(Customizer.withDefaults())   // or formLogin(), oauth2Login(), whatever
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        // define at least one user so that .authenticated() has something to verify:
        var user = User.withUsername("admin")
                .password("{noop}secret")
                .roles("ADMIN")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
