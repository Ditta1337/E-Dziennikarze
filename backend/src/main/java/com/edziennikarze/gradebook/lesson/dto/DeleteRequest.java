package com.edziennikarze.gradebook.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteRequest {

    List<UUID> ids;

}
