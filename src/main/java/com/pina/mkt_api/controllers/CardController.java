package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
// @Tag serve para agrupar e descrever os endpoints relacionados a "Cards"
@Tag(name = "Cards", description = "Gerenciamento de cards")
public class CardController {


    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    // @Operation descreve o que o endpoint faz (resumo e detalhes)
    @Operation(summary = "Criar card", description = "Cria um novo card")
    // @ApiResponses documenta os possíveis status de resposta da API
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    public ResponseEntity<Card> createCard(
            // @Parameter adiciona descrição ao parâmetro do endpoint
            @Parameter(description = "Objeto Card a ser criado") @RequestBody Card card) {
        Card savedCard = cardService.createCard(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }

    @GetMapping
    @Operation(summary = "Listar cards", description = "Retorna todos os cards cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cards retornada com sucesso")
    })
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardService.findAllCards());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar card por ID", description = "Retorna um card específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card encontrado"),
            @ApiResponse(responseCode = "404", description = "Card não encontrado")
    })
    public ResponseEntity<Card> getCardById(
            @Parameter(description = "ID do card") @PathVariable Long id) {
        return ResponseEntity.ok(cardService.findById(id));
    }

    @GetMapping("/company/{companyName}")
    @Operation(summary = "Buscar cards por empresa", description = "Retorna todos os cards de uma empresa específica")
    public ResponseEntity<List<Card>> getCardsByCompany(
            @Parameter(description = "Nome da empresa") @PathVariable String companyName) {
        return ResponseEntity.ok(cardService.findByCompany(companyName));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar card", description = "Atualiza os dados de um card existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Card não encontrado")
    })
    public ResponseEntity<Card> updateCard(
            @Parameter(description = "ID do card a ser atualizado") @PathVariable Long id,
            @RequestBody Card updatedCard) {
        Card card = cardService.updateCard(id, updatedCard);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir card", description = "Remove um card pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Card não encontrado")
    })
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "ID do card a ser removido") @PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}