package com.pina.mkt_api.dtos.UserDTOs;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UserRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        @Schema(description = "Nome completo do usuário", example = "Maria Silva")
        String name,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 150)
        @Schema(description = "E-mail corporativo", example = "maria.silva@email.com")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 255)
        @Schema(description = "Senha de acesso", example = "123456")
        String password,

        @Size(max = 20)
        @Schema(description = "Telefone de contato do funcionário", example = "11 99999-9999")
        String phone,

        @Size(max = 45)
        @Schema(description = "Cargo de função do funcionário", example = "Analista de Marketing")
        String jobTitle,

        @Size(max = 45)
        @Schema(description = "Departamento/Equipe do funcionário", example = "Estratégia")
        String department,

        @Size(max = 45)
        @Schema(description = "Nível de senioridade", example = "junior")
        String seniority,

        @Schema(description = "Nível de acesso no sistema", example = "gestor")
        String role,

        @Schema(description = "ID do perfil (uso administrativo)", example = "1")
        Long roleId,

        @Schema(description = "Responsabilidades principais", example = "Gerenciar campanhas")
        String responsibility,

        @Size(max = 500)
        @Schema(description = "Biografia curta", example = "Especialista em marketing")
        String bio,

        @Size(max = 255)
        @Schema(description = "LinkedIn", example = "linkedin.com/in/maria")
        String linkedin,

        Object o
) {}