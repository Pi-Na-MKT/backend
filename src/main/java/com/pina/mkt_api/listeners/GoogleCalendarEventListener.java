package com.pina.mkt_api.listeners;

import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.events.CardCompletedEvent;
import com.pina.mkt_api.events.CardCreatedEvent;
import com.pina.mkt_api.events.CardMovedEvent;
import com.pina.mkt_api.repositories.CardRepository;
import com.pina.mkt_api.services.CalendarIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class GoogleCalendarEventListener {

    private static final Logger log = LoggerFactory.getLogger(GoogleCalendarEventListener.class);

    @Nullable
    private final CalendarIntegration calendarIntegration;
    private final CardRepository cardRepository;

    public GoogleCalendarEventListener(@Autowired(required = false) CalendarIntegration calendarIntegration,
                                       CardRepository cardRepository) {
        this.calendarIntegration = calendarIntegration;
        this.cardRepository = cardRepository;
    }

    @EventListener
    public void onCardCreated(CardCreatedEvent event) {
        if (calendarIntegration == null) return;

        Card card = event.getCard();

        if (card.getDueDate() == null || card.getGoogleCalendarEventId() != null) return;

        String calendarId = card.getColumn().getBoard().getCompany().getGoogleCalendarId();
        if (calendarId == null) return;

        try {
            String eventId = calendarIntegration.createEventForCard(calendarId, card);
            card.setGoogleCalendarEventId(eventId);
            cardRepository.save(card);
            log.info("Evento do Google Calendar criado automaticamente para o card id={}", card.getId());
        } catch (Exception e) {
            log.warn("Não foi possível criar o evento do Google Calendar para o card id={}: {}", card.getId(), e.getMessage());
        }
    }

    @EventListener
    public void onCardMoved(CardMovedEvent event) {
        Card card = event.getCard();
        log.info("Card id={} movido da coluna id={} para a coluna id={}",
                card.getId(), event.getFromColumnId(), card.getColumn().getId());
    }

    @EventListener
    public void onCardCompleted(CardCompletedEvent event) {
        Card card = event.getCard();
        log.info("Card id={} '{}' marcado como concluído", card.getId(), card.getTitle());
    }
}
