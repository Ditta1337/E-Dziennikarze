package com.edziennikarze.gradebook.lesson.assigned.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FillCalendarRequest {

    private LocalDate from;

    private LocalDate to;

    private UUID id;

}
