package com.pina.mkt_api.dtos.UserDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponseDTO(

        @Schema(description = "ID do usuário")
        Long id,

        @Schema(description = "Nome")
        String name,

        @Schema(description = "Email")
        String email,

        @Schema(description = "Telefone")
        String phone,

        @Schema(description = "Cargo")
        String jobTitle,

        @Schema(description = "Senioridade")
        String seniority,

        @Schema(description = "Perfil de acesso")
        String role,

        @Schema(description = "Avatar")
        String avatarUrl,

        @Schema(description = "Bio")
        String bio,

        @Schema(description = "Responsabilidade")
        String responsibility,

        @Schema(description = "LinkedIn")
        String linkedin
) {
}
