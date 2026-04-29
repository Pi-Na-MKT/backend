package com.pina.mkt_api.dtos.CardDTOs;

import com.pina.mkt_api.enums.Priority;
import java.time.LocalDateTime;
import java.util.List;

public record CardResponseDTO(
        Long id,
        String title,
        String description,
        Priority priority,
        Integer position,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime dueDate,
        Long columnId,
        List<Long> assignedUserIds
) {
}