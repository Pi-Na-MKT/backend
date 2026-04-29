package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.BoardColumn;
import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.entities.User;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.BoardColumnRepository;
import com.pina.mkt_api.repositories.CardRepository;
import com.pina.mkt_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final BoardColumnRepository columnRepository;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, BoardColumnRepository columnRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
    }

    public Card createCard(Long columnId, Card card, List<Long> assignedUserIds) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Coluna não encontrada"));
        card.setColumn(column);

        if (assignedUserIds != null && !assignedUserIds.isEmpty()) {
            List<User> users = userRepository.findAllById(assignedUserIds);
            card.setAssignedUsers(users);
        }

        return cardRepository.save(card);
    }

    public List<Card> findAllCards() {
        return cardRepository.findAll();
    }

    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card com ID " + id + " não foi encontrado no sistema."));
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
            List<User> users = userRepository.findAllById(assignedUserIds);
            existingCard.setAssignedUsers(users);
        }

        return cardRepository.save(existingCard);
    }

    public void deleteCard(Long id) {
        Card card = findById(id);
        cardRepository.delete(card);
    }
}