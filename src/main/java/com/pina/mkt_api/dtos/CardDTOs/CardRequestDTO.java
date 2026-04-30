package com.pina.mkt_api.dtos.CardDTOs;

import com.pina.mkt_api.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public record CardRequestDTO(

        @NotBlank(message = "Título é obrigatório")
        @Size(max = 255)
        @Schema(description = "Título do card", example = "Campanha de Marketing")
        String title,

        @Size(max = 1000)
        @Schema(description = "Descrição detalhada", example = "Planejar campanha de redes sociais")
        String description,

        @NotNull(message = "Prioridade é obrigatória")
        @Schema(description = "Prioridade do card", example = "HIGH")
        Priority priority,

        @NotNull(message = "Posição é obrigatória")
        @Schema(description = "Posição do card na coluna", example = "1")
        Integer position,

        @Schema(description = "Data de vencimento do card")
        LocalDateTime dueDate,

        @Schema(description = "Status ativo do card", example = "true")
        Boolean isActive,

        @Schema(description = "IDs dos usuários responsáveis por esta tarefa")
        List<Long> assignedUserIds
) {}