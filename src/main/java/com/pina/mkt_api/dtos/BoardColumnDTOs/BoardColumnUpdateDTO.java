package com.pina.mkt_api.dtos.BoardColumnDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização parcial de uma coluna (todos os campos são opcionais)")
public record BoardColumnUpdateDTO(

        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        @Schema(description = "Novo nome da coluna", example = "Em Revisão")
        String name,

        @Min(value = 0, message = "Posição deve ser maior ou igual a 0")
        @Schema(description = "Nova posição da coluna no board", example = "2")
        Integer position
) {
}
