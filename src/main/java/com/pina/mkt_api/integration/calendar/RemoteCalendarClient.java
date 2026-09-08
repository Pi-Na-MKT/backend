package com.pina.mkt_api.integration.calendar;

import com.pina.mkt_api.entities.Card;
import com.pina.mkt_api.services.CalendarIntegration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

/**
 * Adapter de saída do monólito: implementa a porta {@link CalendarIntegration} delegando
 * ao microsserviço calendar-service via HTTP. Substitui a antiga integração local com o Google,
 * sem que CardService/CompanyService/listener precisem mudar.
 */
@Component
public class RemoteCalendarClient implements CalendarIntegration {

    private final RestClient restClient;

    public RemoteCalendarClient(@Value("${calendar.service.base-url:http://localhost:8081}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public String createCalendarForCompany(String companyName) throws Exception {
        CalendarResponse response = restClient.post()
                .uri("/api/calendars")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CalendarRequest(companyName))
                .retrieve()
                .body(CalendarResponse.class);
        return response != null ? response.calendarId() : null;
    }

    @Override
    public String createEventForCard(String calendarId, Card card) throws Exception {
        EventResponse response = restClient.post()
                .uri("/api/calendars/{calendarId}/events", calendarId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new EventRequest(card.getTitle(), card.getDescription(), card.getDueDate()))
                .retrieve()
                .body(EventResponse.class);
        return response != null ? response.eventId() : null;
    }

    public record CalendarRequest(String companyName) {}
    public record CalendarResponse(String calendarId) {}
    public record EventRequest(String title, String description, LocalDateTime dueDate) {}
    public record EventResponse(String eventId) {}
}
