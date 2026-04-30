package com.pina.mkt_api.dtos.UserDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserRequestDTO(

        @Schema(description = "Nome completo do usuário", example = "Maria Silva")
        String name,

        @Schema(description = "E-mail corporativo", example = "maria.silva@email.com")
        String email,

        @Schema(description = "Senha de acesso", example = "123456")
        String password,

        @Schema(description = "Telefone de contato do funcionário", example = "11 99999-9999")
        String phone,

        @Schema(description = "Cargo de função do funcionário", example = "Analista de Marketing")
        String jobTitle,

        @Schema(description = "Departamento/Equipe do funcionário", example = "Estratégia")
        String department,

        @Schema(description = "Nível de senioridade", example = "junior")
        String seniority,

        @Schema(description = "Nível de acesso no sistema", example = "gestor")
        String role,

        @Schema(description = "ID do perfil (uso administrativo)", example = "1")
        Long roleId,

        @Schema(description = "Responsabilidades principais", example = "Gerenciar campanhas")
        String responsibility,

        @Schema(description = "Biografia curta", example = "Especialista em marketing")
        String bio,

        @Schema(description = "LinkedIn", example = "linkedin.com/in/maria")
        String linkedin,
        Object o) {}