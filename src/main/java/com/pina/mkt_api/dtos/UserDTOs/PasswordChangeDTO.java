package com.pina.mkt_api.dtos.UserDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para troca de senha do próprio usuário")
public record PasswordChangeDTO(

        @NotBlank(message = "Senha atual é obrigatória")
        @Schema(description = "Senha atual", example = "Senha@123")
        String currentPassword,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 6, max = 255, message = "A nova senha deve ter entre 6 e 255 caracteres")
        @Schema(description = "Nova senha", example = "NovaSenha@456")
        String newPassword
) {
}
