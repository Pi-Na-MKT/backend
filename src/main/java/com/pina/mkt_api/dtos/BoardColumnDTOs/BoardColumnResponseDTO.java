package com.pina.mkt_api.dtos.BoardColumnDTOs;

public record BoardColumnResponseDTO(
        Long id,
        String name,
        Integer position,
        Long boardId
) {
}