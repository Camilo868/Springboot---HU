package com.eventify.repository;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Venue testVenue;

    @BeforeEach
    void setUp() {
        testVenue = venueRepository.save(new Venue(null, "Movistar Arena", "Diag 61c", "Bogotá", 14000));
    }

    @Test
    void shouldSaveEventAndGenerateIdAutomatically() {
        Event newEvent = new Event(null, "Testing Summit", LocalDate.now(), "Pruebas de BD", testVenue);

        Event savedEvent = eventRepository.save(newEvent);

        assertNotNull(savedEvent.getId(), "El ID no debería ser nulo tras guardar en BD");
        assertEquals("Testing Summit", savedEvent.getNombre());
        assertTrue(savedEvent.isActivo(), "El evento debe nacer activo");
    }

    @Test
    void shouldFindEventById() {
        Event savedEvent = eventRepository.save(new Event(null, "Data JPA Event", LocalDate.now(), "Desc", testVenue));

        Event foundEvent = eventRepository.findById(savedEvent.getId()).orElse(null);

        assertNotNull(foundEvent);
        assertEquals(savedEvent.getId(), foundEvent.getId());
        assertEquals("Data JPA Event", foundEvent.getNombre());
    }

    @Test
    void shouldRetrieveSummarySliceWithoutCountQuery() {
        eventRepository.save(new Event(null, "Evento 1", LocalDate.now(), "D1", testVenue));

        Slice<EventSummaryDTO> slice = eventRepository.findAllSummarySlice(PageRequest.of(0, 10));

        assertNotNull(slice);
        assertFalse(slice.getContent().isEmpty(), "El catálogo debe contener eventos");
        assertEquals("Bogotá", slice.getContent().get(0).ciudad());
        assertEquals("Movistar Arena", slice.getContent().get(0).nombreLugar());
    }

    @Test
    void shouldFilterEventsByCityCaseInsensitive() {
        eventRepository.save(new Event(null, "Concierto Especial", LocalDate.now(), "Desc", testVenue));

        // Búsqueda parcial en minúsculas 'bog' para encontrar sedes en 'Bogotá' (Escenario 6)
        Slice<EventSummaryDTO> slice = eventRepository.findFilteredSummarySlice("bog", null, null, null, PageRequest.of(0, 10));

        assertNotNull(slice);
        assertFalse(slice.getContent().isEmpty(), "Debe encontrar eventos en Bogotá mediante búsqueda insensible y parcial");
        assertEquals("Bogotá", slice.getContent().get(0).ciudad());
    }
}
