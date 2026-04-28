package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.BoardDTOs.BoardRequestDTO;
import com.pina.mkt_api.dtos.BoardDTOs.BoardResponseDTO;
import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.services.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping
    @Operation(summary = "Criar board", description = "Cria um novo board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Board criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<BoardResponseDTO> create(
            @Parameter(description = "Dados do Board") @RequestBody BoardRequestDTO requestDTO) {

        Board board = new Board();
        board.setName(requestDTO.name());

        Board savedBoard = service.create(board);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedBoard));
    }

    @GetMapping
    @Operation(summary = "Listar boards", description = "Retorna todos os boards cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de boards retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum board encontrado")
    })
    public ResponseEntity<List<BoardResponseDTO>> getAll() {
        List<BoardResponseDTO> response = service.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private BoardResponseDTO toDTO(Board board) {
        return new BoardResponseDTO(
                board.getId(),
                board.getName()
        );
    }
}