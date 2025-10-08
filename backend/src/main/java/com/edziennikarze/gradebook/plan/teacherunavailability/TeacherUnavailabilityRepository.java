package com.edziennikarze.gradebook.plan.teacherunavailability;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TeacherUnavailabilityRepository extends ReactiveCrudRepository<TeacherUnavailability, UUID> {

    Flux<TeacherUnavailability> findAllByTeacherId(@NotNull UUID teacherId);

    Mono<Boolean> existsByTeacherIdAndWeekDayAndStartTimeBeforeAndEndTimeAfter(@NotNull UUID teacherId, @NotNull DayOfWeek weekDay, @NotNull LocalTime start, @NotNull LocalTime end);

    Flux<TeacherUnavailability> getAllByTeacherIdAndWeekDayAndStartTimeBeforeAndEndTimeAfter(@NotNull UUID teacherId, @NotNull DayOfWeek weekDay, @NotNull LocalTime start, @NotNull LocalTime end);
}
