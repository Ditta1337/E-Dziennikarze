package com.edziennikarze.gradebook.user.officeworker;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("office_workers")
@Builder
public class OfficeWorker {
    @Id
    private UUID id;
    private UUID userId;
    private boolean principalPriviledge;
}
