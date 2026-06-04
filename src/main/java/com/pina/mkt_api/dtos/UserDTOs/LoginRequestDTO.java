package com.pina.mkt_api.dtos.UserDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        @Schema(description = "E-mail corporativo", example = "maria.silva@email.com")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Schema(description = "Senha de acesso", example = "123456")
        String password
) {
}
