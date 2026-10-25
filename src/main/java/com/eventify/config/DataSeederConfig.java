package com.eventify.config;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataSeederConfig {

    @Bean
    public CommandLineRunner seedInitialData(EventService eventService, VenueService venueService) {
        return args -> {
            if (venueService.findAll().isEmpty()) {
                venueService.save(new Venue(null, "Centro de Convenciones Principal", "Av. El Sol 123", 500));
                venueService.save(new Venue(null, "Auditorio Tecnológico", "Calle Innovación 456", 150));
            }

            if (eventService.findAll().isEmpty()) {
                eventService.save(new Event(null, "Conferencia Tech 2026", LocalDate.parse("2026-10-15"), "Encuentro anual de desarrollo"));
                eventService.save(new Event(null, "Workshop Spring Boot", LocalDate.parse("2026-11-20"), "Taller práctico de backend"));
            }
        };
    }
}
