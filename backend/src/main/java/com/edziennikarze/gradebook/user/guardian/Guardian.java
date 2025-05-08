package com.edziennikarze.gradebook.user.guardian;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("guardians")
@Builder
public class Guardian {
    @Id
    private UUID id;
    private UUID userId;
}
