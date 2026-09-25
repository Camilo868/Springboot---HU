package com.eventify.repository;

import com.eventify.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldSaveEventAndGenerateIdAutomatically() {
        // Al instanciar pasamos null en el ID para que la BD lo genere
        Event newEvent = new Event(null, "Testing Summit", LocalDate.now(), "Pruebas de BD");

        Event savedEvent = eventRepository.save(newEvent);

        assertNotNull(savedEvent.getId(), "El ID no debería ser nulo tras guardar en BD");
        assertEquals("Testing Summit", savedEvent.getNombre());
    }

    @Test
    void shouldFindEventById() {
        Event savedEvent = eventRepository.save(new Event(null, "Data JPA Event", LocalDate.now(), "Desc"));

        Event foundEvent = eventRepository.findById(savedEvent.getId()).orElse(null);

        assertNotNull(foundEvent);
        assertEquals(savedEvent.getId(), foundEvent.getId());
        assertEquals("Data JPA Event", foundEvent.getNombre());
    }

    @Test
    void shouldRetrievePaginatedData() {
        eventRepository.save(new Event(null, "Evento 1", LocalDate.now(), "D1"));
        eventRepository.save(new Event(null, "Evento 2", LocalDate.now(), "D2"));
        eventRepository.save(new Event(null, "Evento 3", LocalDate.now(), "D3"));

        // Solicitar la página 0 con un tamaño máximo de 2 elementos
        PageRequest pageRequest = PageRequest.of(0, 2);
        
        Page<Event> eventPage = eventRepository.findAll(pageRequest);

        assertEquals(3, eventPage.getTotalElements(), "Deben existir 3 elementos en total en la BD");
        assertEquals(2, eventPage.getContent().size(), "La página actual debe traer exactamente 2 elementos");
        assertEquals(2, eventPage.getTotalPages(), "Deben existir 2 páginas en total (2 elementos + 1 elemento)");
    }
}