package com.pina.mkt_api.controllers;

import com.pina.mkt_api.Card;

import com.pina.mkt_api.Priority;
import com.pina.mkt_api.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardController {

    private List<Card> cards = new ArrayList<>(
            List.of(
                    new Card("Banco de Dados", "Desenvolver o Banco de Dados do Projeto", TaskStatus.TODO, Priority.HIGH, "MTK", null, null),
                    new Card("Backend", "Desenvolver o Backend do Projeto", TaskStatus.IN_PROGRESS, Priority.HIGH, "MTK", null, null),
                    new Card("Frontend", "Desenvolver o Frontend do Projeto", TaskStatus.TODO, Priority.HIGH, "MTK", null, null),
                    new Card("Wireframe", "Desenvolver o Wireframe do Projeto", TaskStatus.IN_PROGRESS, Priority.HIGH, "MTK", null, null)
            )
    );

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        if (card.getTitle() == null || card.getDescription() == null) {
            return ResponseEntity.status(400).build();
        }

        card.setCreatedAt(LocalDateTime.now());
        cards.add(card);

        return ResponseEntity.status(201).body(card);
    }

    @GetMapping
    public ResponseEntity<List<Card>> getAllCards() {
        if (cards.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(cards);
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<List<Card>> getCardsByCompany(@PathVariable String companyName) {
        for (Card card : cards) {
            if (card.getCompany().equals(companyName)) {
                return ResponseEntity.status(200).body(cards.stream().filter(c -> c.getCompany().equalsIgnoreCase(companyName)).collect(Collectors.toList()));
            }
        }
        return ResponseEntity.status(404).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Card> updateCard(@PathVariable Integer id, @RequestBody Card updatedCard) {
        for (Card card : cards) {
            if (card.getId().equals(id)) {
                if (updatedCard.getTitle() != null) {
                    card.setTitle(updatedCard.getTitle());
                }
                if (updatedCard.getDescription() != null) {
                    card.setDescription(updatedCard.getDescription());
                }
                if (updatedCard.getStatus() != null) {
                    card.setStatus(updatedCard.getStatus());
                }
                if (updatedCard.getPriority() != null) {
                    card.setPriority(updatedCard.getPriority());
                }
                if (updatedCard.getDueDate() != null) {
                    card.setDueDate(updatedCard.getDueDate());
                }

                return ResponseEntity.status(200).body(card);
            }
        }
        return ResponseEntity.status(404).build();
    }

//        for (Card card : cards) {
//            if (card.getId().equals(id)) {
//                card.setTitle(updatedCard.getTitle());
//                card.setDescription(updatedCard.getDescription());
//                card.setStatus(updatedCard.getStatus());
//                card.setPriority(updatedCard.getPriority());
//                card.setDueDate(updatedCard.getDueDate());
//
//                return card;
//            }
//        }
//
//        return null;
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCard(@PathVariable Integer id) {
        boolean removed = cards.removeIf(c -> c.getId().equals(id));

        if (removed) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}