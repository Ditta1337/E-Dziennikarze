package com.edziennikarze.gradebook.planner.restriction.teacherunavailability;

import static com.edziennikarze.gradebook.user.Role.*;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.HasAnyRole;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/teacher-unavailability")
@AllArgsConstructor
public class TeacherUnavailabilityController {

    private final TeacherUnavailabilityService teacherUnavailabilityService;


    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER, TEACHER})
    public Mono<TeacherUnavailability> createTeacherUnavailability(@RequestBody Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityService.createTeacherUnavailability(teacherUnavailabilityMono);
    }

    @GetMapping("/teacher/{teacherId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, TEACHER})
    public Flux<TeacherUnavailability> getTeacherUnavailability(@PathVariable UUID teacherId) {
        return teacherUnavailabilityService.getAllTeachersUnavailabilities(teacherId);
    }

    @PutMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER, TEACHER})
    public Mono<TeacherUnavailability> updateTeacherUnavailability(@RequestBody Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityService.updateTeacherUnavailability(teacherUnavailabilityMono);
    }

    @DeleteMapping("/{teacherUnavailabilityId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, TEACHER})
    public Mono<Void> deleteTeacherUnavailability(@PathVariable UUID teacherUnavailabilityId) {
        return teacherUnavailabilityService.deleteTeacherUnavailability(teacherUnavailabilityId);
    }


}
