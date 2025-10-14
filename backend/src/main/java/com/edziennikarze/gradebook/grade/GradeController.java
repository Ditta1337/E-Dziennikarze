package com.edziennikarze.gradebook.grade;

import com.edziennikarze.gradebook.grade.dto.Grade;
import com.edziennikarze.gradebook.grade.dto.GradeAverageResponse;
import com.edziennikarze.gradebook.grade.dto.GradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    public Mono<GradeResponse> createGrade(@RequestBody Mono<Grade> gradeMono) {
        return gradeService.createGrade(gradeMono);
    }

    @GetMapping("/all/student/{studentId}/subject/{subjectId}")
    public Flux<GradeResponse> getStudentsGradesBySubject(@PathVariable UUID studentId, @PathVariable UUID subjectId) {
        return gradeService.getStudentsGradesBySubject(studentId, subjectId);
    }

    @GetMapping("/all/student/{studentId}")
    public Flux<GradeResponse> getAllStudentsGrades(@PathVariable UUID studentId) {
        return gradeService.getAllStudentsGrades(studentId);
    }

    @GetMapping("/all/group/{groupId}/subject/{subjectId}")
    public Flux<GradeResponse> getGroupsGradesBySubject(@PathVariable UUID groupId, @PathVariable UUID subjectId) {
        return gradeService.getGroupsGradesBySubject(groupId, subjectId);
    }

    @GetMapping("/final/student/{studentId}/subject/{subjectId}")
    public Mono<GradeResponse> getStudentsFinalGradeBySubject(@PathVariable UUID studentId, @PathVariable UUID subjectId) {
        return gradeService.getStudentsFinalGradeBySubject(studentId, subjectId);
    }
    @GetMapping("/final/group/{groupId}/subject/{subjectId}")
    public Flux<GradeResponse> getGroupsFinalGradesBySubject(@PathVariable UUID groupId, @PathVariable UUID subjectId) {
        return gradeService.getGroupsFinalGradesBySubject(groupId, subjectId);
    }

    @GetMapping("/final/student/{studentId}")
    public Flux<GradeResponse> getAllStudentsFinalGrades(@PathVariable UUID studentId) {
        return gradeService.getAllStudentsFinalGrades(studentId);
    }

    @GetMapping("/final/average/student/{studentId}")
    public Mono<Double> getStudentsAverageFinalGrade(@PathVariable UUID studentId) {
        return gradeService.getStudentsAverageFinalGrade(studentId);
    }

    @GetMapping("/average/student/{studentId}")
    public Flux<GradeAverageResponse> getStudentsAverageGrades(@PathVariable UUID studentId) {
        return gradeService.getStudentsAverageGrades(studentId);
    }

    @GetMapping("/average/group/{groupId}/subject/{subjectId}")
    public Flux<GradeAverageResponse> getGroupsAverageGradeBySubject(@PathVariable UUID groupId, @PathVariable UUID subjectId) {
        return gradeService.getGroupsAverageGradeBySubject(groupId, subjectId);
    }

    @PutMapping
    public Mono<GradeResponse> updateGrade(@RequestBody Mono<Grade> gradeMono) {
        return gradeService.updateGrade(gradeMono);
    }

    @DeleteMapping
    public Mono<Void> deleteGrade(@RequestParam UUID gradeId) {
        return gradeService.deleteGrade(gradeId);
    }
}
