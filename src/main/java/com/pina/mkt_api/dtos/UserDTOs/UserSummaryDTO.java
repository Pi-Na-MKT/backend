package com.pina.mkt_api.dtos.UserDTOs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo compacto do usuário para exibição em membros de card ou board")
public record UserSummaryDTO(

        @Schema(description = "ID do usuário", example = "1")
        Long id,

        @Schema(description = "Nome completo do usuário", example = "Maria Silva")
        String name,

        @Schema(description = "URL do avatar do usuário", example = "https://example.com/avatar.jpg")
        String avatarUrl,

        @Schema(description = "Cargo do usuário", example = "Desenvolvedor")
        String jobTitle
) {
}
