package com.pina.mkt_api.dtos.CompanyDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompanyUpdateDTO(

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        @Schema(description = "Novo nome da empresa", example = "Tech Solutions Brasil")
        String name,

        @Size(max = 100, message = "O slug deve ter no máximo 100 caracteres")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug inválido. Use apenas letras minúsculas, números e hifens.")
        @Schema(description = "Novo slug/URL amigável", example = "tech-solutions-br")
        String slug,

        @Schema(description = "Novo status da empresa", example = "false")
        Boolean active
) {
}