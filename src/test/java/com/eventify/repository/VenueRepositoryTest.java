package com.eventify.repository;

import com.eventify.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    void shouldSaveVenueSuccessfully() {
        Venue venue = new Venue();
        venue.setNombre("Centro de Eventos");
        venue.setDireccion("Direccion de Eventos");
        venue.setCapacidad(50);
        // venue.setDireccion("Av. Principal"); // Añade los demás setters según tu modelo
        
        Venue savedVenue = venueRepository.save(venue);

        assertNotNull(savedVenue.getId());
        assertEquals("Centro de Eventos", savedVenue.getNombre());
    }

    @Test
    void shouldFindAllVenues() {
        Venue venue1 = new Venue();
        venue1.setNombre("Auditorio A");
        venue1.setDireccion("Direccion de Eventos");
        venue1.setCapacidad(50);
        
        Venue venue2 = new Venue();
        venue2.setCapacidad(50);
        venue2.setNombre("Teatro B");
        venue2.setDireccion("Direccion de Eventos");


        venueRepository.save(venue1);
        venueRepository.save(venue2);

        List<Venue> venues = venueRepository.findAll();

        assertEquals(2, venues.size());
    }
}