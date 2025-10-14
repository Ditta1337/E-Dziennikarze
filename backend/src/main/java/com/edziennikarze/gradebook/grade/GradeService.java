package com.edziennikarze.gradebook.grade;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.grade.dto.Grade;
import com.edziennikarze.gradebook.grade.dto.GradeAverageResponse;
import com.edziennikarze.gradebook.grade.dto.GradeResponse;
import com.edziennikarze.gradebook.group.groupsubject.GroupSubjectRepository;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubject;
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
    private final GroupSubjectRepository groupSubjectRepository;
    private final NotificationService notificationService;
    private final LoggedInUserService loggedInUserService;

    public Mono<GradeResponse> createGrade(Mono<Grade> gradeMono) {
        return gradeMono.flatMap(gradeRepository::save)
                .flatMap(this::enrichGradeWithSubjectName)
                .flatMap(response -> {
                    boolean isFinal = response.isFinal();
                    String message = String.format("Dodano nową %socenę %s o wadze %s z przedmiotu %s",
                            isFinal ? "końcową " : "", response.getGrade(), response.getWeight(), response.getSubjectName());
                    return notificationService.sendNotification(response.getStudentId(), message)
                            .thenReturn(response);
                });
    }

    public Flux<GradeResponse> getStudentsGradesBySubject(UUID studentId, UUID subjectId) {
        Flux<Grade> gradesFlux = loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .thenMany(gradeRepository.findAllByStudentIdAndSubjectIdAndIsFinal(studentId, subjectId, false));
        return enrichGradesWithSubjectNames(gradesFlux);
    }

    public Flux<GradeResponse> getAllStudentsGrades(UUID studentId) {
        Flux<Grade> gradesFlux = loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .thenMany(gradeRepository.findAllByStudentIdAndIsFinal(studentId, false));
        return enrichGradesWithSubjectNames(gradesFlux);
    }

    public Flux<GradeResponse> getGroupsGradesBySubject(UUID groupId, UUID subjectId) {
        Flux<Grade> gradesFlux = studentGroupRepository.findAllByGroupId(groupId)
                .map(StudentGroup::getStudentId)
                .collectList()
                .flatMapMany(studentIds -> gradeRepository.findAllByStudentIdInAndSubjectIdAndIsFinal(studentIds, subjectId, false));
        return enrichGradesWithSubjectNames(gradesFlux);
    }

    public Mono<GradeResponse> getStudentsFinalGradeBySubject(UUID studentId, UUID subjectId) {
        Mono<Grade> gradeMono = loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .thenMany(gradeRepository.findAllByStudentIdAndSubjectIdAndIsFinal(studentId, subjectId, true))
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Final grade for student with id " + studentId + " and subject id " + subjectId + " not found")));
        return gradeMono.flatMap(this::enrichGradeWithSubjectName);
    }

    public Flux<GradeResponse> getGroupsFinalGradesBySubject(UUID groupId, UUID subjectId) {
        Flux<Grade> gradesFlux = studentGroupRepository.findAllByGroupId(groupId)
                .map(StudentGroup::getStudentId)
                .collectList()
                .flatMapMany(studentIds -> gradeRepository.findAllByStudentIdInAndSubjectIdAndIsFinal(studentIds, subjectId, true));
        return enrichGradesWithSubjectNames(gradesFlux);
    }

    public Flux<GradeResponse> getAllStudentsFinalGrades(UUID studentId) {
        Flux<Grade> gradesFlux = loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .thenMany(gradeRepository.findAllByStudentIdAndIsFinal(studentId, true));
        return enrichGradesWithSubjectNames(gradesFlux);
    }

    public Mono<Double> getStudentsAverageFinalGrade(UUID studentId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .then(gradeRepository.findAllByStudentIdAndIsFinal(studentId, true).collectList())
                .map(this::calculateWeightedAverage);
    }

    public Flux<GradeAverageResponse> getStudentsAverageGrades(UUID studentId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, PRINCIPAL, GUARDIAN)
                .thenMany(getSubjectIdsForStudent(studentId)
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(subjectId -> calculateAverageForSubject(studentId, subjectId))
                );
    }

    public Flux<GradeAverageResponse> getGroupsAverageGradeBySubject(UUID groupId, UUID subjectId) {
        return studentGroupRepository.findAllByGroupId(groupId)
                .map(StudentGroup::getStudentId)
                .collectList()
                .flatMapMany(studentIds -> gradeRepository.findAllByStudentIdInAndSubjectIdAndIsFinal(studentIds, subjectId, false)
                        .groupBy(Grade::getStudentId)
                        .flatMap(groupedFlux -> groupedFlux.collectList()
                                .map(grades -> GradeAverageResponse.builder()
                                        .studentId(grades.getFirst().getStudentId())
                                        .subjectId(subjectId)
                                        .average(calculateWeightedAverage(grades))
                                        .build()
                                )
                        )
                );
    }

    public Mono<GradeResponse> updateGrade(Mono<Grade> gradeMono) {
        return gradeMono.flatMap(grade -> gradeRepository.findById(grade.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Grade with id " + grade.getId() + " not found")))
                .flatMap(existingGrade -> {
                    boolean isFinal = existingGrade.isFinal();
                    Double oldGrade = existingGrade.getGrade();
                    Double oldWeight = existingGrade.getWeight();
                    existingGrade.setGrade(grade.getGrade());
                    existingGrade.setWeight(grade.getWeight());
                    return gradeRepository.save(existingGrade)
                            .flatMap(this::enrichGradeWithSubjectName)
                            .flatMap(response -> {
                                String message = String.format("Zaktualizowano %socenę z %s (waga %s) na %s (waga %s) z przedmiotu %s",
                                        isFinal ? "końcową " : "", oldGrade, oldWeight, response.getGrade(), response.getWeight(), response.getSubjectName());
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
                                    boolean isFinal = gradeToDelete.isFinal();
                                    String message = String.format("Usunięto %socenę %s o wadze %s z przedmiotu %s",
                                            isFinal ? "końcową " : "", response.getGrade(), response.getWeight(), response.getSubjectName());
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
        return gradesFlux
                .groupBy(Grade::getSubjectId)
                .flatMap(groupedFlux -> {
                    UUID subjectId = groupedFlux.key();
                    Mono<String> subjectNameMono = subjectRepository.findById(subjectId)
                            .map(Subject::getName)
                            .switchIfEmpty(Mono.just("Nieznany przedmiot"));
                    return subjectNameMono.flatMapMany(name ->
                            groupedFlux.map(grade -> GradeResponse.from(grade, name))
                    );
                });
    }

    private Double calculateWeightedAverage(List<Grade> grades) {
        if (grades.isEmpty()) return 0.0;
        double totalWeights = grades.stream().mapToDouble(Grade::getWeight).sum();
        if (totalWeights == 0) return 0.0;
        double totalWeightedGrades = grades.stream().mapToDouble(g -> g.getGrade() * g.getWeight()).sum();
        return totalWeightedGrades / totalWeights;
    }

    private Mono<List<UUID>> getSubjectIdsForStudent(UUID studentId) {
        return studentGroupRepository.findAllByStudentId(studentId)
                .map(StudentGroup::getGroupId)
                .collectList()
                .flatMap(groupIds -> groupSubjectRepository.findAllByGroupIdIn(groupIds)
                        .map(GroupSubject::getSubjectId)
                        .collectList()
                );
    }

    private Mono<GradeAverageResponse> calculateAverageForSubject(UUID studentId, UUID subjectId) {
        return gradeRepository.findAllByStudentIdAndSubjectIdAndIsFinal(studentId, subjectId, false)
                .collectList()
                .map(this::calculateWeightedAverage)
                .map(avg -> GradeAverageResponse.builder()
                        .studentId(studentId)
                        .subjectId(subjectId)
                        .average(avg)
                        .build()
                );
    }
}
