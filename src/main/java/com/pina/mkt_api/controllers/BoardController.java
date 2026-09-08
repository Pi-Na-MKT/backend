package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.BoardDTOs.BoardRequestDTO;
import com.pina.mkt_api.dtos.BoardDTOs.BoardResponseDTO;
import com.pina.mkt_api.dtos.BoardDTOs.BoardUpdateDTO;
import com.pina.mkt_api.dtos.UserDTOs.UserSummaryDTO;
import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.services.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@Tag(name = "3 - Boards", description = "Gerenciamento de boards e seus membros")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping("/company/{companyId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Criar board", description = "Cria um board em uma empresa. Requer ADMIN ou GESTOR.")
    public ResponseEntity<BoardResponseDTO> create(
            @Parameter(description = "ID da empresa") @PathVariable Long companyId,
            @Valid @RequestBody BoardRequestDTO requestDTO) {

        Board board = new Board();
        board.setName(requestDTO.name());
        board.setDescription(requestDTO.description());
        board.setBackgroundColor(requestDTO.backgroundColor());
        board.setIsActive(requestDTO.isActive() != null ? requestDTO.isActive() : true);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(service.create(companyId, board, requestDTO.userIds())));
    }

    @GetMapping
    @Operation(summary = "Listar boards", description = "Retorna os boards acessíveis ao usuário autenticado")
    public ResponseEntity<List<BoardResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll().stream().map(this::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar board por ID")
    public ResponseEntity<BoardResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(service.findById(id)));
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "Listar membros do board", description = "Retorna os membros de um board específico")
    public ResponseEntity<List<UserSummaryDTO>> getMembers(@PathVariable Long id) {
        Board board = service.findById(id);
        List<UserSummaryDTO> members = board.getUsers() != null
                ? board.getUsers().stream()
                        .map(u -> new UserSummaryDTO(u.getId(), u.getName(), u.getAvatarUrl(), u.getJobTitle()))
                        .collect(Collectors.toList())
                : List.of();
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Atualizar board", description = "Atualiza parcialmente os dados e membros de um board. Requer ADMIN ou GESTOR.")
    public ResponseEntity<BoardResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody BoardUpdateDTO updateDTO) {

        Board boardDetails = new Board();
        boardDetails.setName(updateDTO.name());
        boardDetails.setDescription(updateDTO.description());
        boardDetails.setBackgroundColor(updateDTO.backgroundColor());
        boardDetails.setIsActive(updateDTO.isActive());

        return ResponseEntity.ok(toDTO(service.update(id, boardDetails, updateDTO.userIds())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Excluir board", description = "Remove um board. Requer ADMIN ou GESTOR.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private BoardResponseDTO toDTO(Board board) {
        List<UserSummaryDTO> members = board.getUsers() != null
                ? board.getUsers().stream()
                        .map(u -> new UserSummaryDTO(u.getId(), u.getName(), u.getAvatarUrl(), u.getJobTitle()))
                        .collect(Collectors.toList())
                : List.of();

        return new BoardResponseDTO(
                board.getId(), board.getName(), board.getDescription(), board.getBackgroundColor(),
                board.getIsActive(), board.getCreatedAt(), board.getUpdatedAt(),
                board.getCompany() != null ? board.getCompany().getId() : null,
                members
        );
    }
}
