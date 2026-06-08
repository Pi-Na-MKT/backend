package com.pina.mkt_api.dtos.CardDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para mover um card para outra coluna (arrastar e soltar estilo Trello)")
public record CardMoveDTO(

        @NotNull(message = "ID da coluna de destino é obrigatório")
        @Schema(description = "ID da coluna de destino", example = "3")
        Long columnId,

        @NotNull(message = "Posição é obrigatória")
        @Min(value = 0, message = "Posição deve ser maior ou igual a 0")
        @Schema(description = "Posição do card na coluna de destino", example = "0")
        Integer position
) {
}
