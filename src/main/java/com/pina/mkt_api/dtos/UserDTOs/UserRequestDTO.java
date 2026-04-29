package com.pina.mkt_api.dtos.UserDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserRequestDTO(
        @Schema(description = "Nome completo do usuário", example = "Maria Silva")
        String name,

        @Schema(description = "E-mail corporativo", example = "maria.silva@email.com")
        String email,

        @Schema(description = "Senha de acesso", example = "123456")
        String password,

        @Schema(description = "ID do Perfil/Cargo (Apenas para criação via Admin)", example = "1")
        Long roleId
) {
}