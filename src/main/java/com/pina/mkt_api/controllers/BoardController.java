package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.BoardDTOs.BoardRequestDTO;
import com.pina.mkt_api.dtos.BoardDTOs.BoardResponseDTO;
import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.services.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "*")
@Tag(name = "3 - Boards", description = "Gerenciamento de boards e seus membros")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping("/company/{companyId}")
    @Operation(summary = "Criar board", description = "Cria um novo board associado a uma empresa e adiciona membros")
    public ResponseEntity<BoardResponseDTO> create(
            @Parameter(description = "ID da Empresa (Company)") @PathVariable Long companyId,
            @Valid @RequestBody BoardRequestDTO requestDTO) {

        Board board = new Board();
        board.setName(requestDTO.name());
        board.setDescription(requestDTO.description());
        board.setBackgroundColor(requestDTO.backgroundColor());
        board.setIsActive(requestDTO.isActive() != null ? requestDTO.isActive() : true);

        Board savedBoard = service.create(companyId, board, requestDTO.userIds());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedBoard));
    }

    @GetMapping
    @Operation(summary = "Listar boards", description = "Retorna todos os boards cadastrados")
    public ResponseEntity<List<BoardResponseDTO>> getAll() {
        List<BoardResponseDTO> response = service.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar board por ID", description = "Retorna os detalhes de um board específico")
    public ResponseEntity<BoardResponseDTO> getById(@PathVariable Long id) {
        Board board = service.findById(id);
        return ResponseEntity.ok(toDTO(board));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar board", description = "Atualiza os dados e os membros de um board")
    public ResponseEntity<BoardResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody BoardRequestDTO requestDTO) {

        Board boardDetails = new Board();
        boardDetails.setName(requestDTO.name());
        boardDetails.setDescription(requestDTO.description());
        boardDetails.setBackgroundColor(requestDTO.backgroundColor());
        boardDetails.setIsActive(requestDTO.isActive());

        Board updatedBoard = service.update(id, boardDetails, requestDTO.userIds());
        return ResponseEntity.ok(toDTO(updatedBoard));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir board", description = "Remove um board pelo seu ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private BoardResponseDTO toDTO(Board board) {
        return new BoardResponseDTO(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getBackgroundColor(),
                board.getIsActive(),
                board.getCreatedAt(),
                board.getUpdatedAt(),
                board.getCompany() != null ? board.getCompany().getId() : null,
                board.getUsers() != null ?
                        board.getUsers().stream().map(User::getId).collect(Collectors.toList()) : null
        );
    }
}