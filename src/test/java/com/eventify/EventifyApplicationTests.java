package com.eventify;

import com.eventify.model.Event;
import com.eventify.repository.CategoryRepository;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventifyApplicationTests {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Escenario 5: Validación de Evolución de Esquema (Flyway)
    // Dado el arranque de la aplicación con Flyway (V1, V2 y V3)
    // Entonces la base de datos debe quedar poblada con 200 registros y la estructura relacional completa
    @Test
    @Transactional
    void shouldVerifyFlywayMigrationAndMassiveSeed() {
        List<Event> events = eventRepository.findAllWithRelations();
        assertEquals(200, events.size(), "Flyway V3 debe haber poblado exactamente 200 eventos");

        assertTrue(venueRepository.count() >= 10, "Deben existir las sedes del catálogo migrado");
        assertTrue(categoryRepository.count() >= 8, "Deben existir las categorías creadas por Flyway");

        // Validar que el primer evento tenga sede y categorías asignadas
        Event firstEvent = events.get(0);
        assertNotNull(firstEvent.getVenue(), "El evento debe tener un Venue vinculado");
        assertNotNull(firstEvent.getVenue().getCiudad(), "El Venue debe tener su ciudad");
        assertFalse(firstEvent.getCategorias().isEmpty(), "El evento debe tener al menos una categoría asignada");
    }
}
