package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.events.CardCompletedEvent;
import com.pina.mkt_api.events.CardCreatedEvent;
import com.pina.mkt_api.events.CardMovedEvent;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.BoardColumnRepository;
import com.pina.mkt_api.repositories.BoardRepository;
import com.pina.mkt_api.repositories.CardRepository;
import com.pina.mkt_api.repositories.UserRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final BoardColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final SecurityUtils securityUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Nullable
    private final CalendarIntegration calendarIntegration;

    public CardService(CardRepository cardRepository, BoardColumnRepository columnRepository,
                       UserRepository userRepository, BoardRepository boardRepository,
                       SecurityUtils securityUtils, ApplicationEventPublisher eventPublisher,
                       @Autowired(required = false) CalendarIntegration calendarIntegration) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.securityUtils = securityUtils;
        this.eventPublisher = eventPublisher;
        this.calendarIntegration = calendarIntegration;
    }

    public Card createCard(Long columnId, Card card, List<Long> assignedUserIds) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Coluna não encontrada"));
        card.setColumn(column);

        if (assignedUserIds != null && !assignedUserIds.isEmpty()) {
            Long boardId = column.getBoard().getId();
            validateBoardMembership(boardId, assignedUserIds);
            card.setAssignedUsers(userRepository.findAllById(assignedUserIds));
        }

        Card saved = cardRepository.save(card);
        eventPublisher.publishEvent(new CardCreatedEvent(this, saved));
        return saved;
    }

    public List<Card> findAllCards() {
        if (securityUtils.isAdmin()) {
            return cardRepository.findAll();
        }
        return cardRepository.findAccessibleByUserEmail(securityUtils.getAuthenticatedEmail());
    }

    public List<Card> findByColumn(Long columnId) {
        if (!securityUtils.isAdmin()) {
            BoardColumn column = columnRepository.findById(columnId)
                    .orElseThrow(() -> new ResourceNotFoundException("Coluna não encontrada"));
            Long boardId = column.getBoard().getId();
            if (!boardRepository.existsByIdAndUsersEmail(boardId, securityUtils.getAuthenticatedEmail())) {
                throw new ResourceNotFoundException("Coluna não encontrada");
            }
        }
        return cardRepository.findByColumnId(columnId);
    }

    public Card findById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado"));

        if (!securityUtils.isAdmin() &&
                cardRepository.countCardAccessForUser(id, securityUtils.getAuthenticatedEmail()) == 0) {
            throw new ResourceNotFoundException("Card não encontrado");
        }

        return card;
    }

    public Card updateCard(Long id, Card updatedCard, List<Long> assignedUserIds) {
        Card existingCard = findById(id);

        if (updatedCard.getTitle() != null) existingCard.setTitle(updatedCard.getTitle());
        if (updatedCard.getDescription() != null) existingCard.setDescription(updatedCard.getDescription());
        if (updatedCard.getPriority() != null) existingCard.setPriority(updatedCard.getPriority());
        if (updatedCard.getPosition() != null) existingCard.setPosition(updatedCard.getPosition());
        if (updatedCard.getDueDate() != null) existingCard.setDueDate(updatedCard.getDueDate());
        if (updatedCard.getIsActive() != null) existingCard.setIsActive(updatedCard.getIsActive());

        boolean justCompleted = Boolean.TRUE.equals(updatedCard.getCompleted())
                && !Boolean.TRUE.equals(existingCard.getCompleted());
        if (updatedCard.getCompleted() != null) existingCard.setCompleted(updatedCard.getCompleted());

        if (assignedUserIds != null) {
            Long boardId = existingCard.getColumn().getBoard().getId();
            if (!assignedUserIds.isEmpty()) {
                validateBoardMembership(boardId, assignedUserIds);
            }
            existingCard.setAssignedUsers(userRepository.findAllById(assignedUserIds));
        }

        Card saved = cardRepository.save(existingCard);
        if (justCompleted) eventPublisher.publishEvent(new CardCompletedEvent(this, saved));
        return saved;
    }

    public Card moveCard(Long id, Long columnId, Integer position) {
        Card card = findById(id);

        BoardColumn targetColumn = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Coluna de destino não encontrada com o ID: " + columnId));

        if (!card.getColumn().getBoard().getId().equals(targetColumn.getBoard().getId())) {
            throw new BusinessRuleException("Não é possível mover um card para uma coluna de outro board.");
        }

        Long fromColumnId = card.getColumn().getId();
        card.setColumn(targetColumn);
        card.setPosition(position);
        Card saved = cardRepository.save(card);
        eventPublisher.publishEvent(new CardMovedEvent(this, saved, fromColumnId));
        return saved;
    }

    public void deleteCard(Long id) {
        cardRepository.delete(findById(id));
    }

    public Card createGoogleCalendarEvent(Long id) {
        Card card = findById(id);

        if (card.getDueDate() == null) {
            throw new BusinessRuleException("O card precisa ter um prazo definido para criar um evento no calendário.");
        }

        String calendarId = card.getColumn().getBoard().getCompany().getGoogleCalendarId();
        if (calendarId == null) {
            throw new BusinessRuleException("A empresa deste card não possui calendário vinculado. Crie o calendário da empresa primeiro.");
        }

        if (card.getGoogleCalendarEventId() != null) {
            throw new BusinessRuleException("Este card já possui um evento vinculado no Google Calendar.");
        }

        if (calendarIntegration == null) {
            throw new BusinessRuleException("Integração com Google Calendar não está configurada.");
        }

        try {
            String eventId = calendarIntegration.createEventForCard(calendarId, card);
            card.setGoogleCalendarEventId(eventId);
            return cardRepository.save(card);
        } catch (Exception e) {
            throw new BusinessRuleException("Erro ao criar evento no Google Calendar: " + e.getMessage());
        }
    }

    private void validateBoardMembership(Long boardId, List<Long> userIds) {
        List<Long> memberIds = boardRepository.findMemberIdsByBoardId(boardId);
        List<Long> nonMembers = userIds.stream().filter(uid -> !memberIds.contains(uid)).toList();
        if (!nonMembers.isEmpty()) {
            throw new BusinessRuleException("Os seguintes usuários não são membros deste board: " + nonMembers);
        }
    }
}
