package com.pina.mkt_api.dtos.BoardDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record BoardRequestDTO(
        @Schema(description = "Nome do board", example = "Projeto Marketing")
        String name
) {
}