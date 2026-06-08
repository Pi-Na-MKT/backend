package com.pina.mkt_api.events;

import com.pina.mkt_api.entities.Card;
import org.springframework.context.ApplicationEvent;

public class CardMovedEvent extends ApplicationEvent {

    private final Card card;
    private final Long fromColumnId;

    public CardMovedEvent(Object source, Card card, Long fromColumnId) {
        super(source);
        this.card = card;
        this.fromColumnId = fromColumnId;
    }

    public Card getCard() {
        return card;
    }

    public Long getFromColumnId() {
        return fromColumnId;
    }
}
