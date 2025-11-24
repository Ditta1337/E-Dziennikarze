package com.edziennikarze.gradebook.plan.manual.util;

import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLessonResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlannedLessonsVerifier {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public static List<String> verifyAndCreateErrors(List<PlannedLessonResponse> plannedLessons) {
        List<String> errors = new ArrayList<>();
        appendErrorsWithGroupsOverlaps(plannedLessons, errors);
        appendErrorsWithTeacherOverlaps(plannedLessons, errors);
        appendErrorsWithRoomsOverlaps(plannedLessons, errors);
        return errors;
    }

    private static void appendErrorsWithGroupsOverlaps(List<PlannedLessonResponse> plannedLessons, List<String> errors) {
        for(int i = 0; i < plannedLessons.size() - 1; i++) {
            for(int j = i + 1; j < plannedLessons.size(); j++) {
                createErrorMessageIfGroupsOverlap(plannedLessons.get(i), plannedLessons.get(j)).ifPresent(errors::add);
            }
        }
    }

    private static void appendErrorsWithTeacherOverlaps(List<PlannedLessonResponse> plannedLessons, List<String> errors) {
        for(int i = 0; i < plannedLessons.size() - 1; i++) {
            for(int j = i + 1; j < plannedLessons.size(); j++) {
                createErrorMessageIfTeachersOverlap(plannedLessons.get(i), plannedLessons.get(j)).ifPresent(errors::add);
            }
        }
    }

    private static void appendErrorsWithRoomsOverlaps(List<PlannedLessonResponse> plannedLessons, List<String> errors) {
        for(int i = 0; i < plannedLessons.size() - 1; i++) {
            for(int j = i + 1; j < plannedLessons.size(); j++) {
                createErrorMessageIfRoomsOverlap(plannedLessons.get(i), plannedLessons.get(j)).ifPresent(errors::add);
            }
        }
    }

    private static Optional<String> createErrorMessageIfGroupsOverlap(PlannedLessonResponse lessonA, PlannedLessonResponse lessonB) {
        if(!lessonA.getGroupId().equals(lessonB.getGroupId())) {
            return Optional.empty();
        }
        if(!lessonA.getWeekDay().equals(lessonB.getWeekDay())) {
            return Optional.empty();
        }
        if(lessonsOverlap(lessonA, lessonB)) {
            return Optional.of("Lekcje z tą samą grupą na siebie nachodzą: "
                    + "\n" + createLessonSignature(lessonA)
                    + "\n" + createLessonSignature(lessonB));
        }
        return Optional.empty();
    }

    private static Optional<String> createErrorMessageIfTeachersOverlap(PlannedLessonResponse lessonA, PlannedLessonResponse lessonB) {
        if(!lessonA.getTeacherId().equals(lessonB.getTeacherId())) {
            return Optional.empty();
        }
        if(!lessonA.getWeekDay().equals(lessonB.getWeekDay())) {
            return Optional.empty();
        }
        if(lessonsOverlap(lessonA, lessonB)) {
            return Optional.of("Lekcje z tym samym nauczycielem na siebie nachodzą: "
                    + "\n" + createLessonSignature(lessonA)
                    + "\n" + createLessonSignature(lessonB));
        }
        return Optional.empty();
    }

    private static Optional<String> createErrorMessageIfRoomsOverlap(PlannedLessonResponse lessonA, PlannedLessonResponse lessonB) {
        if(!lessonA.getRoomId().equals(lessonB.getRoomId())) {
            return Optional.empty();
        }
        if(!lessonA.getWeekDay().equals(lessonB.getWeekDay())) {
            return Optional.empty();
        }
        if(lessonsOverlap(lessonA, lessonB)) {
            return Optional.of("Lekcje z tym samym pokojem na siebie nachodzą: "
                    + "\n" + createLessonSignature(lessonA)
                    + "\n" + createLessonSignature(lessonB));
        }
        return Optional.empty();
    }

    private static boolean lessonsOverlap(PlannedLessonResponse a, PlannedLessonResponse b) {
        LocalTime startA = a.getStartTime();
        LocalTime endA   = a.getEndTime();
        LocalTime startB = b.getStartTime();
        LocalTime endB   = b.getEndTime();

        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    private static String createLessonSignature(PlannedLessonResponse lesson) {
        return "Przedmiot: " + lesson.getSubject() + " " + lesson.getStartTime().format(formatter)
                + " - " + lesson.getEndTime().format(formatter)
                + ", Grupa: " + lesson.getGroup()
                + ", Nauczyciel: " + lesson.getTeacher()
                + ", Pokój:" + lesson.getRoom();
    }

}
