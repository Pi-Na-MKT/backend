package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.BoardColumnRepository;
import com.pina.mkt_api.repositories.BoardRepository;
import com.pina.mkt_api.repositories.CardRepository;
import com.pina.mkt_api.repositories.UserRepository;
import com.pina.mkt_api.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final BoardColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final SecurityUtils securityUtils;

    public CardService(CardRepository cardRepository, BoardColumnRepository columnRepository,
                       UserRepository userRepository, BoardRepository boardRepository,
                       SecurityUtils securityUtils) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.securityUtils = securityUtils;
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

        return cardRepository.save(card);
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

        if (assignedUserIds != null) {
            Long boardId = existingCard.getColumn().getBoard().getId();
            if (!assignedUserIds.isEmpty()) {
                validateBoardMembership(boardId, assignedUserIds);
            }
            existingCard.setAssignedUsers(userRepository.findAllById(assignedUserIds));
        }

        return cardRepository.save(existingCard);
    }

    public Card moveCard(Long id, Long columnId, Integer position) {
        Card card = findById(id);

        BoardColumn targetColumn = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Coluna de destino não encontrada com o ID: " + columnId));

        if (!card.getColumn().getBoard().getId().equals(targetColumn.getBoard().getId())) {
            throw new BusinessRuleException("Não é possível mover um card para uma coluna de outro board.");
        }

        card.setColumn(targetColumn);
        card.setPosition(position);
        return cardRepository.save(card);
    }

    public void deleteCard(Long id) {
        cardRepository.delete(findById(id));
    }

    private void validateBoardMembership(Long boardId, List<Long> userIds) {
        List<Long> memberIds = boardRepository.findMemberIdsByBoardId(boardId);
        List<Long> nonMembers = userIds.stream().filter(uid -> !memberIds.contains(uid)).toList();
        if (!nonMembers.isEmpty()) {
            throw new BusinessRuleException("Os seguintes usuários não são membros deste board: " + nonMembers);
        }
    }
}
