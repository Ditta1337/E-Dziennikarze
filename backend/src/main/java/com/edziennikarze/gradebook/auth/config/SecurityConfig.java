package com.edziennikarze.gradebook.auth.config;

import com.edziennikarze.gradebook.auth.AuthManager;
import com.edziennikarze.gradebook.auth.AuthRepository;
import com.edziennikarze.gradebook.config.websocket.WebSocketTokenAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.edziennikarze.gradebook.user.Role.*;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

    private final AuthManager authenticationManager;

    private final AuthRepository authRepository;

    private final WebSocketTokenAuthenticationConverter webSocketTokenAuthenticationConverter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter wsAuthFilter = new AuthenticationWebFilter(authenticationManager);
        wsAuthFilter.setServerAuthenticationConverter(webSocketTokenAuthenticationConverter);
        wsAuthFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/ws/**"));

        return http
                .addFilterAt(wsAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(authRepository)
                .cors(cors -> cors.configurationSource(createCorsConfigSource()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() ->
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                        .accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() ->
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                )
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/auth/**").permitAll()

                        // User endpoints
                        .pathMatchers(HttpMethod.GET, "/user/all").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), PRINCIPAL.name())
                        .pathMatchers(HttpMethod.GET, "/user/{userId}").authenticated()
                        .pathMatchers(HttpMethod.POST, "/user").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.PUT, "/user").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/user/{userId}/deactivate", "/user/{userId}/activate").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Student-Guardian endpoints
                        .pathMatchers(HttpMethod.GET, "/student-guardian/guardian/{guardianId}", "/student-guardian/student/{studentId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.GET, "/student-guardian/all").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.POST, "/student-guardian").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.DELETE, "/student-guardian/guardian/{guardianId}/student/{studentId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Subject endpoints
                        .pathMatchers(HttpMethod.GET, "/subject/all").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.POST, "/subject").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.DELETE, "/subject/{subjectId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Subject-Taught endpoints
                        .pathMatchers(HttpMethod.GET, "/subject-taught/all").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.POST, "/subject-taught", "/subject-taught/bulk").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.DELETE, "/subject-taught/teacher/{teacherId}/subject/{subjectId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.GET, "/subject-taught/teacher/{teacherId}", "/subject-taught/subject/{subjectId}").authenticated()

                        // Room endpoints
                        .pathMatchers(HttpMethod.GET, "/room/all", "/room/{roomId}").authenticated()
                        .pathMatchers(HttpMethod.POST, "/room").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.PUT, "/room").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.DELETE, "/room/{roomId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Teacher-Unavailability endpoints
                        .pathMatchers(HttpMethod.GET, "/teacher-unavailability/teacher/{teacherId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name())
                        .pathMatchers(HttpMethod.POST, "/teacher-unavailability").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name())
                        .pathMatchers(HttpMethod.PUT, "/teacher-unavailability").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name())
                        .pathMatchers(HttpMethod.DELETE, "/teacher-unavailability/{teacherUnavailabilityId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name())

                        // Lesson endpoints
                        .pathMatchers(HttpMethod.GET, "/lesson/all/student/{studentId}/from/{dateFrom}/to/{dateTo}").authenticated()
                        .pathMatchers(HttpMethod.GET, "/lesson/all/teacher/{teacherId}/from/{dateFrom}/to/{dateTo}").authenticated()

                        // Planned-Lesson endpoints
                        .pathMatchers(HttpMethod.GET, "/planned-lesson/all").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.POST, "/planned-lesson").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.GET, "/planned-lesson/all/group/{groupId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.GET, "/planned-lesson/all/subject/{subjectId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.GET, "/planned-lesson/all/teacher/{teacherId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name())
                        .pathMatchers(HttpMethod.PUT, "/planned-lesson").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Assigned-Lesson endpoints
                        .pathMatchers(HttpMethod.POST, "/assigned-lesson").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.GET, "/assigned-lesson/all", "/assigned-lesson/all/cancelled").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.PUT, "/assigned-lesson").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Group endpoints
                        .pathMatchers(HttpMethod.GET, "/group/all", "/group/all/classes", "/group/all/{startingYear}").authenticated()
                        .pathMatchers(HttpMethod.POST, "/group").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.PUT, "/group").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.PATCH, "/group/increment").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.DELETE, "/group/{groupId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Group-Subject endpoints
                        .pathMatchers(HttpMethod.GET, "/group-subject/teacher/{teacherId}", "/group_subject/group/{groupId}", "/group_subject/subject/{subjectId}").authenticated()
                        .pathMatchers(HttpMethod.GET, "/group-subject/all").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.POST, "/group-subject").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Student-Group endpoints
                        .pathMatchers(HttpMethod.GET, "/student-group/student/{studentId}", "/student-group/group/{groupId}").authenticated()
                        .pathMatchers(HttpMethod.POST, "/student-group").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        // Attendance endpoints
                        .pathMatchers(HttpMethod.GET, "/attendance/student/{studentId}/subject/{subjectId}", "/attendance/average/student/{studentId}", "/attendance/average/student/{studentId}/subject/{subjectId}").authenticated()
                        .pathMatchers(HttpMethod.GET, "/attendance/lesson/{lessonId}").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name())
                        .pathMatchers(HttpMethod.POST, "/attendance").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name())
                        .pathMatchers(HttpMethod.PUT, "/attendance").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name())

                        // Property endpoints
                        .pathMatchers(HttpMethod.GET, "/property/all").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())
                        .pathMatchers(HttpMethod.GET, "/property/name/{name}").permitAll()
                        .pathMatchers(HttpMethod.PUT, "/property").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), PRINCIPAL.name())

                        // WebSocket endpoints
                        .pathMatchers("/ws/echo").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name(), TEACHER.name(), PRINCIPAL.name())

                        // Plan endpoints
                        .pathMatchers(HttpMethod.POST, "/plan").hasAnyAuthority(ADMIN.name(), OFFICE_WORKER.name())

                        .anyExchange().denyAll()
                )
                .build();
    }

    private CorsConfigurationSource createCorsConfigSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
