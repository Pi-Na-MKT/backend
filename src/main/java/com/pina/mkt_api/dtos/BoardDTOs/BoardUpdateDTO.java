package com.pina.mkt_api.dtos.BoardDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Dados para atualização parcial de um board (todos os campos são opcionais)")
public record BoardUpdateDTO(

        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        @Schema(description = "Novo nome do board", example = "Projeto Marketing 2026")
        String name,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        @Schema(description = "Nova descrição do board", example = "Quadro principal da equipe de marketing")
        String description,

        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Cor inválida, use formato hexadecimal. Ex: #FFFFFF")
        @Schema(description = "Nova cor de fundo em hexadecimal", example = "#1E90FF")
        String backgroundColor,

        @Schema(description = "Novo status do board", example = "true")
        Boolean isActive,

        @Schema(description = "Lista de IDs dos usuários membros deste board")
        List<Long> userIds
) {
}
