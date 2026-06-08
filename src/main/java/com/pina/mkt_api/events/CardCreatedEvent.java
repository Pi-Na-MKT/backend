package com.pina.mkt_api.events;

import com.pina.mkt_api.entities.Card;
import org.springframework.context.ApplicationEvent;

public class CardCreatedEvent extends ApplicationEvent {

    private final Card card;

    public CardCreatedEvent(Object source, Card card) {
        super(source);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
