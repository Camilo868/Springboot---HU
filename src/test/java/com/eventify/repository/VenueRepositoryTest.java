package com.eventify.repository;

import com.eventify.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    void shouldSaveVenueSuccessfully() {
        Venue venue = new Venue(null, "Centro de Eventos Único", "Av. Principal 123", "Bogotá", 500);

        Venue savedVenue = venueRepository.save(venue);

        assertNotNull(savedVenue.getId());
        assertEquals("Centro de Eventos Único", savedVenue.getNombre());
        assertEquals("Bogotá", savedVenue.getCiudad());
    }

    @Test
    void shouldFindAllVenues() {
        Venue v1 = venueRepository.save(new Venue(null, "Auditorio A", "Calle 1", "Bogotá", 100));
        Venue v2 = venueRepository.save(new Venue(null, "Auditorio B", "Calle 2", "Medellín", 200));

        List<Venue> venues = venueRepository.findAll();

        assertTrue(venues.size() >= 2);
    }
}
