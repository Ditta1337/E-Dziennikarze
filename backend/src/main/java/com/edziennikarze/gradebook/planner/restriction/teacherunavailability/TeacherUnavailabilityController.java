package com.edziennikarze.gradebook.planner.restriction.teacherunavailability;

import lombok.AllArgsConstructor;
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
    public Mono<TeacherUnavailability> createTeacherUnavailability(@RequestBody Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityService.createTeacherUnavailability(teacherUnavailabilityMono);
    }

    @GetMapping("/teacher/{teacherId}")
    public Flux<TeacherUnavailability> getTeacherUnavailability(@PathVariable UUID teacherId) {
        return teacherUnavailabilityService.getAllTeachersUnavailabilities(teacherId);
    }

    @PutMapping
    public Mono<TeacherUnavailability> updateTeacherUnavailability(@RequestBody Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityService.updateTeacherUnavailability(teacherUnavailabilityMono);
    }

    @DeleteMapping("/{teacherUnavailabilityId}")
    public Mono<Void> deleteTeacherUnavailability(@PathVariable UUID teacherUnavailabilityId) {
        return teacherUnavailabilityService.deleteTeacherUnavailability(teacherUnavailabilityId);
    }
}