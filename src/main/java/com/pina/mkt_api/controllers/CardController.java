package com.pina.mkt_api.controllers;

import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        Card savedCard = cardService.createCard(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }

    @GetMapping
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardService.findAllCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Integer id) {
        return ResponseEntity.ok(cardService.findById(id));
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<List<Card>> getCardsByCompany(@PathVariable String companyName) {
        return ResponseEntity.ok(cardService.findByCompany(companyName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Card> updateCard(@PathVariable Integer id, @RequestBody Card updatedCard) {
        Card card = cardService.updateCard(id, updatedCard);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Integer id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}