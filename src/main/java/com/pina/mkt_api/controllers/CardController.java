package com.pina.mkt_api.controllers;

import com.pina.mkt_api.dtos.CardDTOs.CardMoveDTO;
import com.pina.mkt_api.dtos.CardDTOs.CardRequestDTO;
import com.pina.mkt_api.dtos.CardDTOs.CardResponseDTO;
import com.pina.mkt_api.dtos.CardDTOs.CardUpdateDTO;
import com.pina.mkt_api.dtos.UserDTOs.UserSummaryDTO;
import com.pina.mkt_api.entities.Card;
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
@Tag(name = "5 - Cards", description = "Gerenciamento de cards e tarefas")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/column/{columnId}")
    @Operation(summary = "Criar card", description = "Cria um card em uma coluna específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Coluna não encontrada")
    })
    public ResponseEntity<CardResponseDTO> createCard(
            @Parameter(description = "ID da coluna") @PathVariable Long columnId,
            @Valid @RequestBody CardRequestDTO requestDTO) {

        Card card = new Card();
        mapRequestToEntity(card, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(cardService.createCard(columnId, card, requestDTO.assignedUserIds())));
    }

    @GetMapping
    @Operation(summary = "Listar cards", description = "Retorna os cards acessíveis ao usuário autenticado")
    public ResponseEntity<List<CardResponseDTO>> getAllCards() {
        return ResponseEntity.ok(cardService.findAllCards().stream().map(this::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/column/{columnId}")
    @Operation(summary = "Listar cards por coluna", description = "Retorna todos os cards de uma coluna específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coluna não encontrada ou sem acesso")
    })
    public ResponseEntity<List<CardResponseDTO>> getByColumn(
            @Parameter(description = "ID da coluna") @PathVariable Long columnId) {
        return ResponseEntity.ok(cardService.findByColumn(columnId).stream().map(this::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar card por ID")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(cardService.findById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar card", description = "Atualiza parcialmente os dados de um card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Card não encontrado")
    })
    public ResponseEntity<CardResponseDTO> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody CardUpdateDTO updateDTO) {

        Card cardDetails = new Card();
        cardDetails.setTitle(updateDTO.title());
        cardDetails.setDescription(updateDTO.description());
        cardDetails.setPriority(updateDTO.priority());
        cardDetails.setPosition(updateDTO.position());
        cardDetails.setDueDate(updateDTO.dueDate());
        cardDetails.setIsActive(updateDTO.isActive());
        cardDetails.setCompleted(updateDTO.completed());

        return ResponseEntity.ok(toDTO(cardService.updateCard(id, cardDetails, updateDTO.assignedUserIds())));
    }

    @PatchMapping("/{id}/move")
    @Operation(summary = "Mover card", description = "Move um card para outra coluna e/ou posição (drag and drop)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card movido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Coluna de destino pertence a outro board"),
            @ApiResponse(responseCode = "404", description = "Card ou coluna não encontrado")
    })
    public ResponseEntity<CardResponseDTO> moveCard(
            @Parameter(description = "ID do card") @PathVariable Long id,
            @Valid @RequestBody CardMoveDTO moveDTO) {
        return ResponseEntity.ok(toDTO(cardService.moveCard(id, moveDTO.columnId(), moveDTO.position())));
    }

    @PostMapping("/{id}/calendar-event")
    @Operation(summary = "Criar evento no Google Calendar", description = "Cria manualmente um evento no Google Calendar para o card.")
    public ResponseEntity<CardResponseDTO> createCalendarEvent(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(cardService.createGoogleCalendarEvent(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir card")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    private CardResponseDTO toDTO(Card card) {
        List<UserSummaryDTO> assignedUsers = card.getAssignedUsers() != null
                ? card.getAssignedUsers().stream()
                        .map(u -> new UserSummaryDTO(u.getId(), u.getName(), u.getAvatarUrl(), u.getJobTitle()))
                        .collect(Collectors.toList())
                : List.of();

        return new CardResponseDTO(
                card.getId(), card.getTitle(), card.getDescription(), card.getPriority(),
                card.getPosition(), card.getIsActive(), card.getCompleted(),
                card.getCreatedAt(), card.getUpdatedAt(),
                card.getDueDate(), card.getColumn() != null ? card.getColumn().getId() : null,
                card.getGoogleCalendarEventId(),
                assignedUsers
        );
    }

    private void mapRequestToEntity(Card card, CardRequestDTO dto) {
        card.setTitle(dto.title());
        card.setDescription(dto.description());
        card.setPriority(dto.priority());
        card.setPosition(dto.position());
        card.setDueDate(dto.dueDate());
        if (dto.isActive()  != null) card.setIsActive(dto.isActive());
        if (dto.completed() != null) card.setCompleted(dto.completed());
    }
}
