package com.pina.mkt_api.dtos.CompanyDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record CompanyRequestDTO(
        @Schema(description = "Nome da empresa", example = "Tech Solutions")
        String name,

        @Schema(description = "Slug/URL amigável", example = "tech-solutions")
        String slug,

        @Schema(description = "Status da empresa", example = "true")
        Boolean active
) {
}