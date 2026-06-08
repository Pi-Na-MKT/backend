package com.pina.mkt_api.dtos.AttachmentDTOs;

import java.time.LocalDateTime;

public record AttachmentResponseDTO(
        Long id,
        String fileName,
        Long fileSize,
        String contentType,
        Long companyId,
        String uploadedByName,
        LocalDateTime createdAt
) {}
