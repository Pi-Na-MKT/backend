package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.AttachmentDTOs.AttachmentResponseDTO;
import com.pina.mkt_api.entities.Attachment;
import com.pina.mkt_api.services.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attachments")
@CrossOrigin(origins = "*")
@Tag(name = "6 - Attachments", description = "Gerenciamento de anexos por empresa")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping(value = "/company/{companyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar arquivo", description = "Faz upload de um ou mais arquivos para uma empresa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Arquivo enviado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<List<AttachmentResponseDTO>> upload(
            @Parameter(description = "ID da empresa") @PathVariable Long companyId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {

        List<AttachmentResponseDTO> result = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            result.add(toDTO(attachmentService.upload(companyId, file)));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Listar anexos", description = "Retorna todos os anexos de uma empresa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Anexos retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<List<AttachmentResponseDTO>> listByCompany(
            @Parameter(description = "ID da empresa") @PathVariable Long companyId) {
        List<AttachmentResponseDTO> dtos = attachmentService.findByCompany(companyId)
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Baixar arquivo", description = "Retorna o conteúdo binário do anexo para download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo retornado"),
            @ApiResponse(responseCode = "404", description = "Anexo não encontrado")
    })
    public ResponseEntity<byte[]> download(
            @Parameter(description = "ID do anexo") @PathVariable Long id) {
        Attachment attachment = attachmentService.findById(id);

        String contentType = attachment.getContentType() != null
                ? attachment.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(attachment.getFileName()).build()
        );

        return ResponseEntity.ok().headers(headers).body(attachment.getFileData());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir anexo")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do anexo") @PathVariable Long id) {
        attachmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AttachmentResponseDTO toDTO(Attachment a) {
        return new AttachmentResponseDTO(
                a.getId(),
                a.getFileName(),
                a.getFileSize(),
                a.getContentType(),
                a.getCompany().getId(),
                a.getUploadedBy() != null ? a.getUploadedBy().getName() : null,
                a.getCreatedAt()
        );
    }
}
