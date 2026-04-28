package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.BoardDTOs.BoardRequestDTO;
import com.pina.mkt_api.dtos.BoardDTOs.BoardResponseDTO;
import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.services.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "*")
@Tag(name = "Boards", description = "Gerenciamento de boards")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping("/company/{companyId}")
    @Operation(summary = "Criar board", description = "Cria um novo board associado a uma empresa")
    public ResponseEntity<BoardResponseDTO> create(
            @Parameter(description = "ID da Empresa (Company)") @PathVariable Long companyId,
            @RequestBody BoardRequestDTO requestDTO) {

        Board board = new Board();
        board.setName(requestDTO.name());
        board.setDescription(requestDTO.description());
        board.setBackgroundColor(requestDTO.backgroundColor());
        board.setActive(requestDTO.active() != null ? requestDTO.active() : true);

        Board savedBoard = service.create(companyId, board);

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

    private BoardResponseDTO toDTO(Board board) {
        return new BoardResponseDTO(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getBackgroundColor(),
                board.getActive(),
                board.getCreatedAt(),
                board.getUpdatedAt(),
                board.getCompany() != null ? board.getCompany().getId() : null // Devolve o ID da empresa de forma segura!
        );
    }
}