package com.pina.mkt_api.dtos.CardDTOs;

import com.pina.mkt_api.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record CardRequestDTO(
        @Schema(description = "Título do card", example = "Campanha de Marketing")
        String title,

        @Schema(description = "Descrição detalhada", example = "Planejar campanha de redes sociais")
        String description,

        @Schema(description = "Prioridade do card", example = "HIGH")
        Priority priority,

        @Schema(description = "Empresa associada ao card", example = "Microsoft")
        String company,

        @Schema(description = "Posição do card na coluna", example = "1")
        Integer position,

        @Schema(description = "Data de vencimento do card")
        LocalDateTime dueDate
) {
}