package com.pina.mkt_api.dtos.BoardDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record BoardRequestDTO(
        @Schema(description = "Nome do board", example = "Projeto Marketing")
        String name,

        @Schema(description = "Descrição do quadro", example = "Quadro principal da equipe")
        String description,

        @Schema(description = "Cor de fundo (Hexadecimal)", example = "#FFFFFF")
        String backgroundColor,

        @Schema(description = "Status do quadro", example = "true")
        Boolean isActive,

        @Schema(description = "Lista de IDs dos usuários membros deste quadro")
        List<Long> userIds
) {
}