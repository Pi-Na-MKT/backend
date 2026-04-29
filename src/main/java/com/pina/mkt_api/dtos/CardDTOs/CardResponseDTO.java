package com.pina.mkt_api.dtos.CardDTOs;

import com.pina.mkt_api.enums.Priority;
import java.time.LocalDateTime;

public record CardResponseDTO(
        Long id,
        String title,
        String description,
        Priority priority,
        String company,
        Integer position,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime dueDate,
        Long columnId
) {
}