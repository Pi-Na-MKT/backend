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

    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card com ID " + id + " não foi encontrado no sistema."));
    }

    public List<Card> findByCompany(String companyName) {
        return cardRepository.findByCompanyIgnoreCase(companyName);
    }

    public void deleteCard(Long id) {
        Card card = findById(id);
        cardRepository.delete(card);
    }

    public Card updateCard(Long id, Card updatedCard) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card não encontrado"));

        if(updatedCard.getTitle() != null) existingCard.setTitle(updatedCard.getTitle());
        if(updatedCard.getDescription() != null) existingCard.setDescription(updatedCard.getDescription());

        if(updatedCard.getPosition() != null) existingCard.setPosition(updatedCard.getPosition());
        if(updatedCard.getColumn() != null) existingCard.setColumn(updatedCard.getColumn());

        return cardRepository.save(existingCard);
    }
}