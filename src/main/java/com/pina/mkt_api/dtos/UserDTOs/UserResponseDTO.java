package com.pina.mkt_api.dtos.UserDTOs;

import com.pina.mkt_api.enums.Role;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Role role,
        Boolean active
) {
}