package com.pina.mkt_api.dtos.UserDTOs;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Boolean isActive,
        Long roleId,
        String roleName
) {
}