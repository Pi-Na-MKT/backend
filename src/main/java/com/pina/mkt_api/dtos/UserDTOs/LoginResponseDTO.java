package com.pina.mkt_api.dtos.UserDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com token JWT e dados do usuário")
public record LoginResponseDTO(

        @Schema(description = "Token JWT para autenticação nas próximas requisições", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "ID do usuário autenticado", example = "1")
        Long userId,

        @Schema(description = "Nome completo do usuário", example = "Maria Silva")
        String name,

        @Schema(description = "Cargo/papel do usuário no sistema", example = "GESTOR")
        String role
) {
}
