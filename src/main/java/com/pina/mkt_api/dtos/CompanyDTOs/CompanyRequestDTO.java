package com.pina.mkt_api.dtos.CompanyDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CompanyRequestDTO(

        @NotBlank(message = "Nome da empresa é obrigatório")
        @Size(max = 100)
        @Schema(description = "Nome da empresa", example = "Tech Solutions")
        String name,

        @NotBlank(message = "Slug é obrigatório")
        @Size(max = 100)
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug inválido")
        @Schema(description = "Slug/URL amigável", example = "tech-solutions")
        String slug,

        @Schema(description = "Status da empresa", example = "true")
        Boolean active
) {}