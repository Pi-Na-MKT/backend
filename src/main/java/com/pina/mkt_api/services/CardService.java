package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.exceptions.BusinessRuleException;
import com.pina.mkt_api.exceptions.ResourceNotFoundException;
import com.pina.mkt_api.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Card createCard(Card card) {
        if (card.getTitle() == null || card.getTitle().trim().isEmpty()) {
            throw new BusinessRuleException("O título do card é obrigatório.");
        }

        return cardRepository.save(card);
    }

    public List<Card> findAllCards() {
        return cardRepository.findAll();
    }

    public Card findById(Integer id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card com ID " + id + " não foi encontrado no sistema."));
    }

    public List<Card> findByCompany(String companyName) {
        return cardRepository.findByCompanyIgnoreCase(companyName);
    }

    public Card updateCard(Integer id, Card updatedCard) {
        Card existingCard = findById(id);

        if (updatedCard.getTitle() != null) existingCard.setTitle(updatedCard.getTitle());
        if (updatedCard.getDescription() != null) existingCard.setDescription(updatedCard.getDescription());
        if (updatedCard.getStatus() != null) existingCard.setStatus(updatedCard.getStatus());
        if (updatedCard.getPriority() != null) existingCard.setPriority(updatedCard.getPriority());
        if (updatedCard.getDueDate() != null) existingCard.setDueDate(updatedCard.getDueDate());

        return cardRepository.save(existingCard);
    }

    public void deleteCard(Integer id) {
        Card card = findById(id);
        cardRepository.delete(card);
    }
}