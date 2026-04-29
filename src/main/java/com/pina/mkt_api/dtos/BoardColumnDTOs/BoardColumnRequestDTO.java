package com.pina.mkt_api.dtos.BoardColumnDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record BoardColumnRequestDTO(
        @Schema(description = "Nome da coluna", example = "A Fazer")
        String name,

        @Schema(description = "Posição da coluna no board", example = "1")
        Integer position
) {
}