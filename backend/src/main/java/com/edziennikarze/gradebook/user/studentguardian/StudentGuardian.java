package com.edziennikarze.gradebook.user.studentguardian;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("student_guardians")
public class StudentGuardian {

    @Id
    private UUID id;

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID guardianId;
}
