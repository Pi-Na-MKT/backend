package com.pina.mkt_api.services;

import com.pina.mkt_api.entities.Card;

public interface CalendarIntegration {
    String createCalendarForCompany(String companyName) throws Exception;
    String createEventForCard(String calendarId, Card card) throws Exception;
}
