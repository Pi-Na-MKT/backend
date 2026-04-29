package com.pina.mkt_api.dtos.BoardDTOs;

import java.time.LocalDateTime;

public record BoardResponseDTO(
        Long id,
        String name,
        String description,
        String backgroundColor,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long companyId
) {
}