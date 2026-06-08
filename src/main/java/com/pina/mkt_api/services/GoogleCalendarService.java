package com.pina.mkt_api.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.pina.mkt_api.entities.Card;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class GoogleCalendarService implements CalendarIntegration {

    private static final String APPLICATION_NAME = "Pi.Na MKT";
    private static final String TIMEZONE = "America/Sao_Paulo";
    private static final ZoneId ZONE_ID = ZoneId.of(TIMEZONE);

    private Calendar getCalendarService() throws Exception {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(),
                new InputStreamReader(GoogleCalendarService.class.getResourceAsStream("/credentials.json"))
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                clientSecrets,
                Collections.singletonList(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String createCalendarForCompany(String companyName) throws Exception {
        Calendar service = getCalendarService();

        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("Pi.Na - " + companyName);
        calendar.setTimeZone(TIMEZONE);

        com.google.api.services.calendar.model.Calendar created = service.calendars().insert(calendar).execute();
        return created.getId();
    }

    public String createEventForCard(String calendarId, Card card) throws Exception {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(card.getTitle())
                .setDescription(card.getDescription() != null ? card.getDescription() : "Tarefa do sistema Pi.Na MKT.");

        ZonedDateTime start = card.getDueDate().atZone(ZONE_ID);
        ZonedDateTime end = start.plusMinutes(30);

        event.setStart(new EventDateTime()
                .setDateTime(new DateTime(start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                .setTimeZone(TIMEZONE));
        event.setEnd(new EventDateTime()
                .setDateTime(new DateTime(end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)))
                .setTimeZone(TIMEZONE));

        event = service.events().insert(calendarId, event).execute();
        return event.getId();
    }
}
