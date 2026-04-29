package com.pina.mkt_api.dtos.CompanyDTOs;

import java.time.LocalDateTime;

public record CompanyResponseDTO(
        Long id,
        String name,
        String slug,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}