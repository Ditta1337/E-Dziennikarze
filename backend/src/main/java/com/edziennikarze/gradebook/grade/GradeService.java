package com.edziennikarze.gradebook.grade;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.grade.dto.Grade;
import com.edziennikarze.gradebook.grade.dto.GradeResponse;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.notification.NotificationService;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.edziennikarze.gradebook.user.Role.*;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final SubjectRepository subjectRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final NotificationService notificationService;
    private final LoggedInUserService loggedInUserService;

    public Mono<GradeResponse> createGrade(Mono<Grade> gradeMono) {
        return gradeMono.flatMap(gradeRepository::save)
                .flatMap(this::enrichGradeWithSubjectName)
                .flatMap(response -> {
                    String message = String.format("Dodano nową ocenę %s o wadze %s z przedmiotu %s",
                            response.getGrade(), response.getWeight(), response.getSubjectName());
                    return notificationService.sendNotification(response.getStudentId(), message)
                            .thenReturn(response);
                });
    }

    public Flux<GradeResponse> getStudentsGradesBySubject(UUID studentId, UUID subjectId) {
        Flux<Grade> gradesFlux = loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .thenMany(gradeRepository.findAllByStudentIdAndSubjectId(studentId, subjectId));
        return enrichGradesWithSubjectNames(gradesFlux);
    }

    public Flux<GradeResponse> getAllStudentsGrades(UUID studentId) {
        Flux<Grade> gradesFlux = loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .thenMany(gradeRepository.findAllByStudentId(studentId));
        return enrichGradesWithSubjectNames(gradesFlux);
    }

    public Flux<GradeResponse> getGroupsGradesBySubject(UUID groupId, UUID subjectId) {
        Flux<Grade> gradesFlux = studentGroupRepository.findAllByGroupId(groupId)
                .map(StudentGroup::getStudentId)
                .collectList()
                .flatMapMany(studentIds -> gradeRepository.findAllByStudentIdInAndSubjectId(studentIds, subjectId));
        return enrichGradesWithSubjectNames(gradesFlux);
    }

    public Mono<Double> getStudentsAverageGrade(UUID studentId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .then(gradeRepository.findAllByStudentId(studentId).collectList())
                .map(this::calculateWeightedAverage);
    }

    public Mono<Double> getStudentsAverageGradeBySubject(UUID studentId, UUID subjectId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .then(gradeRepository.findAllByStudentIdAndSubjectId(studentId, subjectId).collectList())
                .map(this::calculateWeightedAverage);
    }

    public Mono<GradeResponse> updateGrade(Mono<Grade> gradeMono) {
        return gradeMono.flatMap(grade -> gradeRepository.findById(grade.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Grade with id " + grade.getId() + " not found")))
                .flatMap(existingGrade -> {
                    final Double oldGrade = existingGrade.getGrade();
                    final Double oldWeight = existingGrade.getWeight();

                    existingGrade.setGrade(grade.getGrade());
                    existingGrade.setWeight(grade.getWeight());

                    return gradeRepository.save(existingGrade)
                            .flatMap(this::enrichGradeWithSubjectName)
                            .flatMap(response -> {
                                String message = String.format("Zaktualizowano ocenę z %s (waga %s) na %s (waga %s) z przedmiotu %s",
                                        oldGrade, oldWeight, response.getGrade(), response.getWeight(), response.getSubjectName());
                                return notificationService.sendNotification(response.getStudentId(), message)
                                        .thenReturn(response);
                            });
                })
        );
    }

    public Mono<Void> deleteGrade(UUID gradeId) {
        return gradeRepository.findById(gradeId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Grade with id " + gradeId + " not found")))
                .flatMap(gradeToDelete ->
                        enrichGradeWithSubjectName(gradeToDelete)
                                .flatMap(response -> {
                                    String message = String.format("Usunięto ocenę %s o wadze %s z przedmiotu %s",
                                            response.getGrade(), response.getWeight(), response.getSubjectName());
                                    return gradeRepository.delete(gradeToDelete)
                                            .then(notificationService.sendNotification(response.getStudentId(), message));
                                })
                )
                .then();
    }

    private Mono<GradeResponse> enrichGradeWithSubjectName(Grade grade) {
        return subjectRepository.findById(grade.getSubjectId())
                .map(subject -> GradeResponse.from(grade, subject.getName()))
                .switchIfEmpty(Mono.just(GradeResponse.from(grade, "Nieznany przedmiot")));
    }

    private Flux<GradeResponse> enrichGradesWithSubjectNames(Flux<Grade> gradesFlux) {
        return gradesFlux.collectList()
                .flatMapMany(grades -> {
                    List<UUID> subjectIds = grades.stream()
                            .map(Grade::getSubjectId)
                            .distinct()
                            .toList();

                    return subjectRepository.findAllById(subjectIds)
                            .collectMap(Subject::getId, Subject::getName)
                            .flatMapMany(subjectMap -> Flux.fromIterable(grades)
                                    .map(grade -> GradeResponse.from(
                                            grade, subjectMap.getOrDefault(grade.getSubjectId(), "Nieznany przedmiot"))
                                    )
                            );
                });
    }

    private Double calculateWeightedAverage(List<Grade> grades) {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double totalWeights = grades.stream().mapToDouble(Grade::getWeight).sum();
        if (totalWeights == 0) {
            return 0.0;
        }
        double totalWeightedGrades = grades.stream()
                .mapToDouble(grade -> grade.getGrade() * grade.getWeight())
                .sum();
        return totalWeightedGrades / totalWeights;
    }
}