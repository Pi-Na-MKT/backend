package com.pina.mkt_api.dtos.BoardColumnDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record BoardColumnRequestDTO(

        @NotBlank(message = "Nome da coluna é obrigatório")
        @Size(max = 50)
        @Schema(description = "Nome da coluna", example = "A Fazer")
        String name,

        @NotNull(message = "Posição é obrigatória")
        @Min(0)
        @Schema(description = "Posição da coluna no board", example = "1")
        Integer position
) {}