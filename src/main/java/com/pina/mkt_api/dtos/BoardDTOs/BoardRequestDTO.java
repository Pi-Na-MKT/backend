package com.pina.mkt_api.dtos.BoardDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;

public record BoardRequestDTO(

        @NotBlank(message = "Nome do board é obrigatório")
        @Size(max = 100)
        @Schema(description = "Nome do board", example = "Projeto Marketing")
        String name,

        @Size(max = 500)
        @Schema(description = "Descrição do quadro", example = "Quadro principal da equipe")
        String description,

        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Cor inválida")
        @Size(max = 7)
        @Schema(description = "Cor de fundo (Hexadecimal)", example = "#FFFFFF")
        String backgroundColor,

        @Schema(description = "Status do quadro", example = "true")
        Boolean isActive,

        @Schema(description = "Lista de IDs dos usuários membros deste quadro")
        List<Long> userIds
) {}