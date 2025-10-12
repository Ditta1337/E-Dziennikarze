package com.edziennikarze.gradebook.attendance;

public enum AttendanceStatus {
    PRESENT, LATE, ABSENT, EXCUSED;

    public String getDisplayName() {
        return switch (this) {
            case PRESENT -> "obecny";
            case LATE -> "spóźniony";
            case ABSENT -> "nieobecny";
            case EXCUSED -> "usprawiedliwiony";
        };
    }
}
