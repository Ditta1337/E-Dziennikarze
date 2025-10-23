package com.edziennikarze.gradebook.util;

import com.edziennikarze.gradebook.attendance.Attendance;
import com.edziennikarze.gradebook.attendance.AttendanceRepository;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubject;
import com.edziennikarze.gradebook.group.groupsubject.GroupSubjectRepository;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;
import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailability;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailabilityRepository;
import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.seeder.Seeder;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaught;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtRepository;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.studentguardian.StudentGuardian;
import com.edziennikarze.gradebook.user.studentguardian.StudentGuardianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.*;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    //<editor-fold desc="Repositories">
    private final UserRepository userRepository;
    private final StudentGuardianRepository studentGuardianRepository;
    private final GroupRepository groupRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final GroupSubjectRepository groupSubjectRepository;
    private final TeacherUnavailabilityRepository teacherUnavailabilityRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectTaughtRepository subjectTaughtRepository;
    private final RoomRepository roomRepository;
    private final PlannedLessonRepository plannedLessonRepository;
    private final AssignedLessonRepository assignedLessonRepository;
    private final AttendanceRepository attendanceRepository;
    private final Seeder seeder;
    //</editor-fold>

    //<editor-fold desc="In-memory lists for relationships">
    private List<User> students;
    private List<User> teachers;
    private List<User> guardians;
    private List<User> officeWorkers;
    private List<User> principals;
    private List<User> admins;
    private List<StudentGuardian> studentGuardians;
    private List<Group> groups;
    private List<StudentGroup> studentGroups;
    private List<TeacherUnavailability> teacherUnavailabilities;
    private List<Subject> subjects;
    private List<SubjectTaught> subjectTaught;
    private List<GroupSubject> groupSubjects;
    private List<Room> rooms;
    private List<PlannedLesson> plannedLessons;
    private List<AssignedLesson> assignedLessons;
    private List<Attendance> attendances;
    //</editor-fold>

    public void onApplicationEvent(ApplicationReadyEvent event) {
        initializeData();
    }

    private void initializeData() {
        if (userRepository.count().blockOptional().orElse(0L) > 0) {
            log.info("Users table is not empty. Skipping data initialization.");
            return;
        }

        try {
            log.info("Users table is empty. Initializing all database tables with sample data.");
            initializeUsers();
            initializeStudentGuardians();
            initializeGroups();
            initializeGroups();
            initializeSubjects();
            initializeStudentGroups();
            initializeTeacherUnavailabilities();
            initializeSubjectTaught();
            initializeGroupSubjects();
            initializeRooms();
            initializePlannedLessons();
            initializeAssignedLessons();
            initializeAttendances();
            seeder.seed("classes_four_to_eight.json");
            log.info("Data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Data initialization failed.", e);
        }
    }

    private void initializeUsers() {
        log.info("Initializing Users...");
        List<User> usersToSave = Stream.of(
                        List.of(buildUser("admin@gmail.com", Role.ADMIN, true, false)),
                        List.of(
                                buildUser("s1@gmail.com", Role.STUDENT, true, false),
                                buildUser("s2@gmail.com", Role.STUDENT, true, false),
                                buildUser("s3@gmail.com", Role.STUDENT, true, false),
                                buildUser("s4@gmail.com", Role.STUDENT, true, false),
                                buildUser("s5@gmail.com", Role.STUDENT, true, false),
                                buildUser("s6@gmail.com", Role.STUDENT, true, false)),
                        List.of(
                                buildUser("s1g1@gmail.com", Role.GUARDIAN, true, false),
                                buildUser("s1g2@gmail.com", Role.GUARDIAN, true, false),
                                buildUser("s2g1@gmail.com", Role.GUARDIAN, true, false)),
                        List.of(buildUser("ow1@gmail.com", Role.OFFICE_WORKER, true, false)),
                        List.of(buildUser("p1@gmail.com", Role.PRINCIPAL, true, false)),
                        List.of(
                                buildUser("t1@gmail.com", Role.TEACHER, true, true),
                                buildUser("t2@gmail.com", Role.TEACHER, true, true),
                                buildUser("t3@gmail.com", Role.TEACHER, true, true),
                                buildUser("t4@gmail.com", Role.TEACHER, true, true),
                                buildUser("t5@gmail.com", Role.TEACHER, true, true)))
                .flatMap(List::stream)
                .toList();

        List<User> savedUsers = userRepository.saveAll(usersToSave).collectList().block();

        Map<Role, List<User>> groupedUsers = savedUsers.stream().collect(Collectors.groupingBy(User::getRole));
        this.students = groupedUsers.getOrDefault(Role.STUDENT, List.of());
        this.teachers = groupedUsers.getOrDefault(Role.TEACHER, List.of());
        this.guardians = groupedUsers.getOrDefault(Role.GUARDIAN, List.of());
        this.officeWorkers = groupedUsers.getOrDefault(Role.OFFICE_WORKER, List.of());
        this.principals = groupedUsers.getOrDefault(Role.PRINCIPAL, List.of());
        this.admins = groupedUsers.getOrDefault(Role.ADMIN, List.of());
    }

    private void initializeStudentGuardians() {
        log.info("Initializing Student-Guardian relationships...");
        studentGuardianRepository.deleteAll().block();
        List<StudentGuardian> studentGuardiansToSave = List.of(
                buildStudentGuardian(students.get(0).getId(), guardians.get(0).getId()), // s1 -> s1g1
                buildStudentGuardian(students.get(0).getId(), guardians.get(1).getId()), // s1 -> s1g2
                buildStudentGuardian(students.get(1).getId(), guardians.get(2).getId())  // s2 -> s2g1
        );
        this.studentGuardians = studentGuardianRepository.saveAll(studentGuardiansToSave).collectList().block();
    }

    private void initializeGroups() {
        log.info("Initializing Groups...");
        groupRepository.deleteAll().block();
        List<Group> groupsToSave = List.of(
                buildGroup(1, "1_A", true),
                buildGroup(1, "1_A_ANG", false),
                buildGroup(2, "2_A", true),
                buildGroup(2, "2_B", false));
        this.groups = groupRepository.saveAll(groupsToSave).collectList().block();
    }

    private void initializeSubjects() {
        log.info("Initializing Subjects...");
        subjectRepository.deleteAll().block();
        List<Subject> subjectsToSave = List.of(
                buildSubject("Matematyka"),
                buildSubject("Biologia"),
                buildSubject("Niemiecki"),
                buildSubject("Angielski"),
                buildSubject("Informatyka"));
        this.subjects = subjectRepository.saveAll(subjectsToSave).collectList().block();
    }

    private void initializeStudentGroups() {
        log.info("Initializing Student-Group relationships...");
        studentGroupRepository.deleteAll().block();
        List<StudentGroup> studentGroupsToSave = List.of(
                buildStudentGroup(students.get(0).getId(), groups.get(0).getId()), // s1 -> 1A
                buildStudentGroup(students.get(1).getId(), groups.get(0).getId()), // s2 -> 1A
                buildStudentGroup(students.get(0).getId(), groups.get(1).getId()), // s1 -> 1A_ANG
                buildStudentGroup(students.get(2).getId(), groups.get(2).getId()), // s3 -> 2A
                buildStudentGroup(students.get(3).getId(), groups.get(2).getId()), // s4 -> 2A
                buildStudentGroup(students.get(4).getId(), groups.get(2).getId()), // s5 -> 2A
                buildStudentGroup(students.get(5).getId(), groups.get(3).getId())  // s6 -> 2B
        );
        this.studentGroups = studentGroupRepository.saveAll(studentGroupsToSave).collectList().block();
    }

    private void initializeTeacherUnavailabilities() {
        log.info("Initializing Teacher Unavailabilities...");
        teacherUnavailabilityRepository.deleteAll().block();
        List<TeacherUnavailability> unavailabilitiesToSave = List.of(
                buildTeacherUnavailability(teachers.get(0).getId(), LocalTime.of(7, 30), LocalTime.of(8, 15), DayOfWeek.MONDAY),
                buildTeacherUnavailability(teachers.get(0).getId(), LocalTime.of(15, 0), LocalTime.of(16, 30), DayOfWeek.MONDAY)
        );
        this.teacherUnavailabilities = teacherUnavailabilityRepository.saveAll(unavailabilitiesToSave).collectList().block();
    }

    private void initializeSubjectTaught() {
        log.info("Initializing Subject-Taught relationships...");
        subjectTaughtRepository.deleteAll().block();
        List<SubjectTaught> subjectTaughtToSave = List.of(
                buildSubjectTaught(teachers.get(0).getId(), subjects.get(0).getId()), // t1 -> Matematyka
                buildSubjectTaught(teachers.get(1).getId(), subjects.get(1).getId()), // t2 -> Biologia
                buildSubjectTaught(teachers.get(2).getId(), subjects.get(2).getId()), // t3 -> Niemiecki
                buildSubjectTaught(teachers.get(3).getId(), subjects.get(3).getId()), // t4 -> Angielski
                buildSubjectTaught(teachers.get(4).getId(), subjects.get(4).getId())  // t5 -> Informatyka
        );
        this.subjectTaught = subjectTaughtRepository.saveAll(subjectTaughtToSave).collectList().block();
    }

    private void initializeGroupSubjects() {
        log.info("Initializing Group-Subjects relationships...");
        groupSubjectRepository.deleteAll().block();
        List<GroupSubject> groupsToSaveSubject = List.of(
                buildGroupSubject(teachers.get(0).getId(), groups.get(0).getId(), subjects.get(0).getId(), true), // t1 -> 1A Matematyka
                buildGroupSubject(teachers.get(0).getId(), groups.get(2).getId(), subjects.get(0).getId(), true), // t1 -> 2A Matematyka
                buildGroupSubject(teachers.get(1).getId(), groups.get(0).getId(), subjects.get(1).getId(), true), // t2 -> 1A Biologia
                buildGroupSubject(teachers.get(2).getId(), groups.get(2).getId(), subjects.get(2).getId(), true), // t3 -> 2A Niemiecki
                buildGroupSubject(teachers.get(3).getId(), groups.get(1).getId(), subjects.get(3).getId(), true), // t4 -> 1A_ANG Angielski
                buildGroupSubject(teachers.get(4).getId(), groups.get(3).getId(), subjects.get(4).getId(), true)  // t5 -> 2B Informatyka
        );
        this.groupSubjects = groupSubjectRepository.saveAll(groupsToSaveSubject).collectList().block();
    }

    private void initializeRooms() {
        log.info("Initializing Rooms...");
        roomRepository.deleteAll().block();
        List<Room> roomsToSave = List.of(
                buildRoom(30, "1"),
                buildRoom(25, "2"),
                buildRoom(20, "3"),
                buildRoom(15, "4"));
        this.rooms = roomRepository.saveAll(roomsToSave).collectList().block();
    }

    private void initializePlannedLessons() {
        log.info("Initializing Planned Lessons...");
        plannedLessonRepository.deleteAll().block();
        List<PlannedLesson> plannedLessonsToSave = List.of(
                buildPlannedLesson(rooms.get(0).getId(), groups.get(0).getId(), teachers.get(0).getId(), subjects.get(0).getId(), DayOfWeek.MONDAY, LocalTime.of(8, 15), LocalTime.of(9, 0), true),
                buildPlannedLesson(rooms.get(1).getId(), groups.get(0).getId(), teachers.get(1).getId(), subjects.get(1).getId(), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(9, 45), true),
                buildPlannedLesson(rooms.get(2).getId(), groups.get(1).getId(), teachers.get(3).getId(), subjects.get(3).getId(), DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(10, 45), true),
                buildPlannedLesson(rooms.get(0).getId(), groups.get(2).getId(), teachers.get(0).getId(), subjects.get(0).getId(), DayOfWeek.TUESDAY, LocalTime.of(8, 15), LocalTime.of(9, 0), true),
                buildPlannedLesson(rooms.get(1).getId(), groups.get(2).getId(), teachers.get(2).getId(), subjects.get(2).getId(), DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(9, 45), true),
                buildPlannedLesson(rooms.get(3).getId(), groups.get(3).getId(), teachers.get(4).getId(), subjects.get(4).getId(), DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(11, 45), true)
        );
        this.plannedLessons = plannedLessonRepository.saveAll(plannedLessonsToSave).collectList().block();
    }

    private void initializeAssignedLessons() {
        log.info("Initializing Assigned Lessons...");
        List<AssignedLesson> lessonsToAssign = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        for (PlannedLesson plannedLesson : this.plannedLessons) {
            for (int i = -5; i <= 5; i++) {
                DayOfWeek weekDay = plannedLesson.getWeekDay();
                int daysDifference = weekDay.getValue() - dayOfWeek.getValue() + (i * 7);
                LocalDate currentDate = today.plusDays(daysDifference);
                lessonsToAssign.add(buildAssignedLesson(plannedLesson.getId(), currentDate, false, false));
            }
        }

        this.assignedLessons = assignedLessonRepository.saveAll(lessonsToAssign).collectList().block();
    }

    private void initializeAttendances() {
        log.info("Initializing Attendances...");
        attendanceRepository.deleteAll().block();
        this.attendances = attendanceRepository.saveAll(List.of()).collectList().block();
    }
}

