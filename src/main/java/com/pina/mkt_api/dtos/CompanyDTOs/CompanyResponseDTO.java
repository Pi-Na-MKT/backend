package com.pina.mkt_api.dtos.CompanyDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record CompanyResponseDTO(
        @Schema(description = "ID da empresa", example = "1")
        Long id,

        @Schema(description = "Nome fantasia ou razão social", example = "PiNa Tech")
        String name,

        @Schema(description = "Slug de URL da empresa", example = "pina-tech")
        String slug,

        @Schema(description = "Status de atividade da empresa", example = "true")
        Boolean active,

        @Schema(description = "ID do calendário no Google Calendar (null se não vinculado)")
        String googleCalendarId,

        @Schema(description = "Data de registro no sistema")
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização")
        LocalDateTime updatedAt
) {
}