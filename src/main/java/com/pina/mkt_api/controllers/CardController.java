package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.CardDTOs.CardRequestDTO;
import com.pina.mkt_api.dtos.CardDTOs.CardResponseDTO;
import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.services.CardService;
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
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
@Tag(name = "Cards", description = "Gerenciamento de cards e tarefas")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/column/{columnId}")
    @Operation(summary = "Criar card", description = "Cria um novo card em uma coluna específica e pode atribuir usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Coluna não encontrada")
    })
    public ResponseEntity<CardResponseDTO> createCard(
            @Parameter(description = "ID da coluna onde o card vai nascer") @PathVariable Long columnId,
            @Valid @RequestBody CardRequestDTO requestDTO) {

        Card card = new Card();
        updateEntityFromDTO(card, requestDTO);

        Card savedCard = cardService.createCard(columnId, card, requestDTO.assignedUserIds());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedCard));
    }

    @GetMapping
    @Operation(summary = "Listar cards", description = "Retorna todos os cards cadastrados")
    public ResponseEntity<List<CardResponseDTO>> getAllCards() {
        List<CardResponseDTO> response = cardService.findAllCards().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar card por ID", description = "Retorna um card específico pelo seu ID")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long id) {
        Card card = cardService.findById(id);
        return ResponseEntity.ok(toDTO(card));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar card", description = "Atualiza os dados de um card existente")
    public ResponseEntity<CardResponseDTO> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody CardRequestDTO requestDTO) {
        Card cardDetails = new Card();
        updateEntityFromDTO(cardDetails, requestDTO);

        Card updatedCard = cardService.updateCard(id, cardDetails, requestDTO.assignedUserIds());
        return ResponseEntity.ok(toDTO(updatedCard));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir card", description = "Remove um card pelo seu ID")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    private CardResponseDTO toDTO(Card card) {
        return new CardResponseDTO(
                card.getId(),
                card.getTitle(),
                card.getDescription(),
                card.getPriority(),
                card.getPosition(),
                card.getIsActive(),
                card.getCreatedAt(),
                card.getUpdatedAt(),
                card.getDueDate(),
                card.getColumn() != null ? card.getColumn().getId() : null,

                card.getAssignedUsers() != null ?
                        card.getAssignedUsers().stream().map(User::getId).collect(Collectors.toList()) : null
        );
    }

    private void updateEntityFromDTO(Card card, CardRequestDTO dto) {
        card.setTitle(dto.title());
        card.setDescription(dto.description());
        card.setPriority(dto.priority());
        card.setPosition(dto.position());
        card.setDueDate(dto.dueDate());
        if (dto.isActive() != null) card.setIsActive(dto.isActive());
    }
}