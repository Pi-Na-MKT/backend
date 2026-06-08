package com.pina.mkt_api.dtos.CardDTOs;

import com.pina.mkt_api.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Dados para atualização parcial de um card (todos os campos são opcionais)")
public record CardUpdateDTO(

        @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
        @Schema(description = "Novo título do card", example = "Campanha de Marketing - Revisão")
        String title,

        @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
        @Schema(description = "Nova descrição detalhada", example = "Revisar artes e textos da campanha")
        String description,

        @Schema(description = "Nova prioridade do card", example = "MEDIUM")
        Priority priority,

        @Schema(description = "Nova posição do card na coluna", example = "2")
        Integer position,

        @Schema(description = "Nova data de vencimento do card")
        LocalDateTime dueDate,

        @Schema(description = "Novo status do card", example = "true")
        Boolean isActive,

        @Schema(description = "Card marcado como concluído", example = "false")
        Boolean completed,

        @Schema(description = "IDs dos usuários responsáveis por esta tarefa")
        List<Long> assignedUserIds
) {
}
