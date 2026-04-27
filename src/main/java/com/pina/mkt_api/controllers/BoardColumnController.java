package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.services.BoardColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/columns")
@CrossOrigin(origins = "*")
// @Tag vai servir pra agrupar e descrever os endpoints relacionados ao "Board Columns"
@Tag(name = "Board Columns", description = "Gerenciamento de colunas do board")
public class BoardColumnController {

    private final BoardColumnService service;

    public BoardColumnController(BoardColumnService service) {
        this.service = service;
    }

    @PostMapping("/board/{boardId}")
    // @Operation descreve o que o endpoint faz
    @Operation(summary = "Criar coluna", description = "Cria uma nova coluna em um board")
    // @ApiResponses documenta os possíveis status de resposta da API
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coluna criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public BoardColumn create(
            // @Parameter adiciona descrição ao parâmetro do endpoint
            @Parameter(description = "ID do board onde a coluna será criada") @PathVariable Long boardId,
            @RequestBody BoardColumn column) {
        return service.create(boardId, column);
    }

    @GetMapping("/board/{boardId}")
// @Operation descreve o que o endpoint faz
    @Operation(summary = "Listar colunas", description = "Retorna todas as colunas de um board")
// @ApiResponses documenta os possíveis status de resposta da API
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de colunas retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Board não encontrado")
    })
    public List<BoardColumn> getByBoard(
            // @Parameter adicion descrição ao parâmetro do endpoint
            @Parameter(description = "ID do board") @PathVariable Long boardId) {
        return service.getByBoard(boardId);
    }

    @DeleteMapping("/{id}")
// @Operation descreve o que o endpoint faz
    @Operation(summary = "Excluir coluna", description = "Remove uma coluna pelo seu ID")
// @ApiResponses documenta os possíveis status de resposta da API
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Coluna removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coluna não encontrada")
    })
    public void delete(
            // @Parameter adiciona descrição ao parâmetro do endpoint
            @Parameter(description = "ID da coluna a ser removida") @PathVariable Long id) {
        service.deleteColumn(id);
    }
}