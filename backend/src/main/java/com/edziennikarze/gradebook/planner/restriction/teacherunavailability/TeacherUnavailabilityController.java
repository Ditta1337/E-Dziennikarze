package com.edziennikarze.gradebook.planner.restriction.teacherunavailability;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/teacher-unavailability")
@AllArgsConstructor
public class TeacherUnavailabilityController {

    private final TeacherUnavailabilityService teacherUnavailabilityService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'TEACHER')")
    public Mono<TeacherUnavailability> createTeacherUnavailability(@RequestBody Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityService.createTeacherUnavailability(teacherUnavailabilityMono);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'TEACHER')")
    public Flux<TeacherUnavailability> getTeacherUnavailability(@PathVariable UUID teacherId) {
        return teacherUnavailabilityService.getAllTeachersUnavailabilities(teacherId);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'TEACHER')")
    public Mono<TeacherUnavailability> updateTeacherUnavailability(@RequestBody Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityService.updateTeacherUnavailability(teacherUnavailabilityMono);
    }

    @DeleteMapping("/{teacherUnavailabilityId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'TEACHER')")
    public Mono<Void> deleteTeacherUnavailability(@PathVariable UUID teacherUnavailabilityId) {
        return teacherUnavailabilityService.deleteTeacherUnavailability(teacherUnavailabilityId);
    }
}