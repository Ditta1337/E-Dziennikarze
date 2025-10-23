package com.edziennikarze.gradebook.seeder;

import lombok.Data;

import java.util.List;

@Data
public class SeederInput {

    private List<Subject> subjects;

    private List<Group> groups;

    private List<Room> rooms;

    @Data
    public static class Subject {
        private int id;
        private String name;
        private List<Integer> classes;
        private int numberOfTeachers;
    }

    @Data
    public static class Group {
        private String name;
        private int classLevel;
        private List<Integer> subjectIds;
        private int numberOfStudents;
        private GroupType type;
    }

    @Data
    public static class Room {
        private String name;
        private int capacity;
    }

    public enum GroupType {
        CLASS(1),
        LANGUAGE(2),
        COMBINED(3);

        private final int sortOrder;

        GroupType(int sortOrder) {
            this.sortOrder = sortOrder;
        }

        public int getSortOrder() {
            return sortOrder;
        }
    }
}