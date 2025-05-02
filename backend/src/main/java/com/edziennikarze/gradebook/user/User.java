package com.edziennikarze.gradebook.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@Table("users")
public class User {
    @Id
    private UUID id;
    private String name;
    private String surname;
    private LocalDate createdAt;
    private String address;
    private String email;
    private String password;
    private String contact;
    private String imageBase64;
    private String role;
    private Boolean isActive;
}
