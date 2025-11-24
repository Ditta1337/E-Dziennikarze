package com.edziennikarze.gradebook.seeder;

import com.edziennikarze.gradebook.exception.UnmarshallException;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.groupsubject.GroupSubjectRepository;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubject;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.subject.subjecttaught.dto.SubjectTaught;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtRepository;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.*;


@Component
@RequiredArgsConstructor
public class Seeder {

    private static final int START_YEAR = 2025;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Random random = new Random();

    private final Faker faker = new Faker();

    private final UserRepository userRepository;

    private final StudentGroupRepository studentGroupRepository;

    private final GroupSubjectRepository groupSubjectRepository;

    private final SubjectTaughtRepository subjectTaughtRepository;

    private final SubjectRepository subjectRepository;

    private final RoomRepository roomRepository;

    private final GroupRepository groupRepository;

    private final ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper;

    private int studentCount = 1;

    private int teacherCount = 1;

    private Map<Integer, Subject> subjectsById = new HashMap<>();

    private final Map<String, List<User>> subjectToTeachersMap = new HashMap<>();

    public void seed(String fileName) {
        SeederInput seederInput = loadSeederInput(fileName);
        generateData(seederInput);
    }

    private SeederInput loadSeederInput(String fileName) {
        try {
            Resource resource = resourceLoader.getResource("classpath:initialization/" + fileName);
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(inputStream, SeederInput.class);
            }
        } catch (IOException e) {
            throw new UnmarshallException("Failed to read or parse seeder JSON:" + fileName);
        }
    }

    private void generateData(SeederInput seederInput) {
        generateAllSubjectsAndTeachers(seederInput);
        generateAllGroupsAndStudents(seederInput);
        generateAllRooms(seederInput);

    }

    private void generateAllRooms(SeederInput seederInput) {
        seederInput.getRooms().forEach(room -> generateRoom(room.getCapacity(), room.getName()));
    }

    private void generateAllGroupsAndStudents(SeederInput seederInput) {
        seederInput.getGroups().stream()
                .collect(java.util.stream.Collectors.groupingBy(SeederInput.Group::getClassLevel))
                .values()
                .forEach(this::generateGroupsByClassLevel);

    }

    private void generateGroupsByClassLevel(List<SeederInput.Group> groupsForClassLevel) {
        List<User> studentsInClassLevel = new ArrayList<>();
        Map<Group, List<User>> languageGroupsToStudentsMap = new HashMap<>();
        groupsForClassLevel.sort(Comparator.comparing(group -> group.getType().getSortOrder()));
        List<User> availableLanguageStudents = null;

        for (SeederInput.Group group : groupsForClassLevel) {
            List<Subject> subjectsForGroup = group.getSubjectIds().stream()
                    .map(subjectsById::get)
                    .toList();
            switch (group.getType()) {
                case CLASS:
                    List<User> students = generateMultipleUsers(Role.STUDENT, group.getNumberOfStudents());
                    studentsInClassLevel.addAll(students);
                    Group savedGroup = generateGroup(group.getName(), group.getClassLevel(), group.getType());
                    students.forEach(student -> assignStudentToGroup(student, savedGroup));
                    assignGroupAllTeachersToSubjects(savedGroup, subjectsForGroup);
                    break;
                case COMBINED:
                    Group combinedGroup = generateGroup(group.getName(), group.getClassLevel(), group.getType());
                    studentsInClassLevel.forEach(student -> assignStudentToGroup(student, combinedGroup));
                    assignGroupAllTeachersToSubjects(combinedGroup, subjectsForGroup);
                    break;
                case LANGUAGE:
                    if (availableLanguageStudents == null) {
                        availableLanguageStudents = new ArrayList<>(studentsInClassLevel);
                        Collections.shuffle(availableLanguageStudents, random);
                    }
                    List<User> studentsForLanguageGroup = getStudentsForLanguageGroup(availableLanguageStudents, group.getNumberOfStudents());
                    Group languageGroup = generateGroup(group.getName(), group.getClassLevel(), group.getType());
                    languageGroupsToStudentsMap.put(languageGroup, studentsForLanguageGroup);
                    studentsForLanguageGroup.forEach(student -> assignStudentToGroup(student, languageGroup));
                    assignGroupAllTeachersToSubjects(languageGroup, subjectsForGroup);
                    break;
            }
        }
    }

    private List<User> getStudentsForLanguageGroup(List<User> availableStudents, int numberOfStudents) {
        if (availableStudents.size() < numberOfStudents) {
            throw new IllegalStateException(
                    "Cannot assign " + numberOfStudents + " students to language group. Only " +
                            availableStudents.size() + " students are left unassigned for this class level."
            );
        }
        List<User> studentsForGroup = new ArrayList<>(availableStudents.subList(0, numberOfStudents));

        availableStudents.subList(0, numberOfStudents).clear();
        return studentsForGroup;
    }

    private void assignGroupAllTeachersToSubjects(Group savedGroup, List<Subject> subjectsForGroup) {
        subjectsForGroup.forEach(subject -> {
            List<User> teachersForSubject = subjectToTeachersMap.get(subject.getName());
            // get by random
            int index = Math.floorMod(savedGroup.getGroupCode().hashCode(), teachersForSubject.size());
            User assignedTeacher = teachersForSubject.get(index);
            assignGroupAndTeacherToSubject(assignedTeacher, savedGroup, subject);

        });
    }

    private void generateAllSubjectsAndTeachers(SeederInput seederInput) {
        seederInput.getSubjects().forEach(subject -> {
            String subjectName = subject.getName();
            Subject savedSubject = generateSubject(subjectName);
            subjectsById.put(subject.getId(), savedSubject);
            List<User> teachersForSubject = subjectToTeachersMap.computeIfAbsent(subjectName, k -> generateMultipleUsers(Role.TEACHER, subject.getNumberOfTeachers()));
            teachersForSubject.forEach(teacher -> assignTeacherToSubject(teacher, savedSubject));
        });
    }

    private void assignTeacherToSubject(User savedTeacher, Subject savedSubject) {
        SubjectTaught subjectTaughtToSave = buildSubjectTaught(savedTeacher.getId(), savedSubject.getId());
        subjectTaughtRepository.save(subjectTaughtToSave).block();
    }

    private void assignGroupAndTeacherToSubject(User savedTeacher, Group savedGroup, Subject savedSubject) {
        GroupSubject groupSubjectToSave = buildGroupSubject(savedTeacher.getId(), savedGroup.getId(), savedSubject.getId(), true);
        groupSubjectRepository.save(groupSubjectToSave).block();
    }

    private void assignStudentToGroup(User savedUser, Group savedGroup) {
        StudentGroup studentGroupToSave = buildStudentGroup(savedUser.getId(), savedGroup.getId());
        studentGroupRepository.save(studentGroupToSave).block();
    }

    private Group generateGroup(String groupName, int classLevel, SeederInput.GroupType type) {
        Group groupToSave = buildGroup(START_YEAR - classLevel, groupName, type == SeederInput.GroupType.CLASS);
        return groupRepository.save(groupToSave).block();
    }

    private Room generateRoom(int capacity, String roomCode) {
        Room roomToSave = buildRoom(capacity, roomCode);
        return roomRepository.save(roomToSave).block();
    }

    private Subject generateSubject(String subjectName) {
        Subject subjectToSave = buildSubject(subjectName);
        return subjectRepository.save(subjectToSave).block();
    }

    private List<User> generateMultipleUsers(Role role, int count) {
        List<User> users = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generateUser(role));
        }
        return users;
    }

    private User generateUser(Role role) {
        String name = faker.name().firstName();
        String surname = faker.name().lastName();
        String password = role == Role.STUDENT ? "gs" + studentCount++ : "gt" + teacherCount++;
        String email = String.format("%s@gmail.com", password);
        User userToSave = User.builder()
                .name(name)
                .surname(surname)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .address("Ulica 32/2;31-230;Krakow;Polska")
                .contact("+48123456789")
                .imageBase64("someImageBase64")
                .active(true)
                .build();

        return userRepository.save(userToSave).block();
    }
}