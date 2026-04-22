package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.Board;
import com.pina.mkt_api.services.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "*")
// @Tag serve para agrupar e descrever os endpoints relacionados a "Boards"
@Tag(name = "Boards", description = "Gerenciamento de boards")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping
    // @Operation descreve o que o endpoint faz (resumo e detalhes)
    @Operation(summary = "Criar board", description = "Cria um novo board")
    // @ApiResponses documenta os possíveis status de resposta da API
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public Board create(
            // @Parameter adiciona descrição ao parâmetro do endpoint
            @Parameter(description = "Objeto Board a ser criado") @RequestBody Board board) {
        return service.create(board);
    }

    @GetMapping
    @Operation(summary = "Listar boards", description = "Retorna todos os boards cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de boards retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum board encontrado")
    })
    public List<Board> getAll() {
        return service.findAll();
    }
}