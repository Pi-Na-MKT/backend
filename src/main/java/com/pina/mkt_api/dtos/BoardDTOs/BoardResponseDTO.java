package com.pina.mkt_api.dtos.BoardDTOs;

import com.pina.mkt_api.dtos.UserDTOs.UserSummaryDTO;
import java.time.LocalDateTime;
import java.util.List;

public record BoardResponseDTO(
        Long id,
        String name,
        String description,
        String backgroundColor,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long companyId,
        List<UserSummaryDTO> members
) {
}
