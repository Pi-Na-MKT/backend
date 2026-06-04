package com.pina.mkt_api.dtos.UserDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização do perfil do usuário (todos os campos são opcionais)")
public record UserUpdateDTO(

        @Size(min = 3, message = "O nome deve ter pelo menos 3 caracteres")
        @Schema(description = "Novo nome do usuário", example = "Maria Silva Souza")
        String name,

        @Schema(description = "Novo telefone", example = "11 98888-8888")
        String phone,

        @Schema(description = "Novo cargo", example = "Tech Lead")
        String jobTitle,

        @Schema(description = "Nova senioridade", example = "senior")
        String seniority,

        @Size(max = 500, message = "Biografia deve ter no máximo 500 caracteres")
        @Schema(description = "Nova biografia curta", example = "Especialista em marketing digital")
        String bio,

        @Schema(description = "Novas responsabilidades principais", example = "Gerenciar campanhas e métricas")
        String responsibility,

        @Size(max = 255, message = "LinkedIn deve ter no máximo 255 caracteres")
        @Schema(description = "Novo perfil do LinkedIn", example = "linkedin.com/in/maria")
        String linkedin,

        @Size(max = 500, message = "URL do avatar deve ter no máximo 500 caracteres")
        @Schema(description = "URL da foto de perfil", example = "https://example.com/avatar.jpg")
        String avatarUrl
) {
}
