package com.edziennikarze.gradebook.subject;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Table("subjects")
@Builder
<<<<<<<< HEAD:backend/src/main/java/com/edziennikarze/gradebook/subject/Subject.java
public class Subject {
========
public class Guardian {

>>>>>>>> origin/backend_new:backend/src/main/java/com/edziennikarze/gradebook/user/guardian/Guardian.java
    @Id
    private UUID id;

    @NotNull
<<<<<<<< HEAD:backend/src/main/java/com/edziennikarze/gradebook/subject/Subject.java
    private String name;
========
    private UUID userId;
>>>>>>>> origin/backend_new:backend/src/main/java/com/edziennikarze/gradebook/user/guardian/Guardian.java
}
