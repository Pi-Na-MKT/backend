package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.BoardColumnDTOs.BoardColumnRequestDTO;
import com.pina.mkt_api.dtos.BoardColumnDTOs.BoardColumnResponseDTO;
import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.services.BoardColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/columns")
@CrossOrigin(origins = "*")
@Tag(name = "Board Columns", description = "Gerenciamento de colunas do board")
public class BoardColumnController {

    private final BoardColumnService service;

    public BoardColumnController(BoardColumnService service) {
        this.service = service;
    }

    @PostMapping("/board/{boardId}")
    @Operation(summary = "Criar coluna", description = "Cria uma nova coluna em um board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coluna criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<BoardColumnResponseDTO> create(
            @Parameter(description = "ID do board onde a coluna será criada") @PathVariable Long boardId,
            @Parameter(description = "Dados da coluna") @Valid @RequestBody BoardColumnRequestDTO requestDTO) {

        BoardColumn column = new BoardColumn();
        column.setName(requestDTO.name());
        column.setPosition(requestDTO.position());

        BoardColumn savedColumn = service.create(boardId, column);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedColumn));
    }

    @GetMapping("/board/{boardId}")
    @Operation(summary = "Listar colunas", description = "Retorna todas as colunas de um board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de colunas retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Board não encontrado")
    })
    public ResponseEntity<List<BoardColumnResponseDTO>> getByBoard(
            @Parameter(description = "ID do board") @PathVariable Long boardId) {

        List<BoardColumnResponseDTO> response = service.getByBoard(boardId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir coluna", description = "Remove uma coluna pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Coluna removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coluna não encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da coluna a ser removida") @PathVariable Long id) {
        service.deleteColumn(id);

        return ResponseEntity.noContent().build();
    }

    private BoardColumnResponseDTO toDTO(BoardColumn column) {
        return new BoardColumnResponseDTO(
                column.getId(),
                column.getName(),
                column.getPosition(),
                column.getBoard() != null ? column.getBoard().getId() : null
        );
    }
}