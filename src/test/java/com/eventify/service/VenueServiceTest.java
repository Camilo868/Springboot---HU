package com.eventify.service;

import com.eventify.exception.InvalidDataException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    @Test
    void shouldCreateVenueSuccessfully() {
        Venue newVenue = new Venue(null, "Auditorio Principal", "Calle 123", 500);
        Venue savedVenue = new Venue(1L, "Auditorio Principal", "Calle 123", 500);

        when(venueRepository.save(any(Venue.class))).thenReturn(savedVenue);

        Venue result = venueService.save(newVenue);

        assertNotNull(result.getId());
        assertEquals("Auditorio Principal", result.getNombre());
        assertEquals(500, result.getCapacidad());
        verify(venueRepository, times(1)).save(newVenue);
    }

    @Test
    void shouldThrowExceptionWhenVenueNameIsNull() {
        Venue invalidVenue = new Venue(null, null, "Calle 123", 500);

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            venueService.save(invalidVenue);
        });

        assertEquals("El nombre del lugar no puede estar vacío", exception.getMessage());
        verify(venueRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenVenueNameIsEmpty() {
        Venue invalidVenue = new Venue(null, "   ", "Calle 123", 500);

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            venueService.save(invalidVenue);
        });

        assertEquals("El nombre del lugar no puede estar vacío", exception.getMessage());
        verify(venueRepository, never()).save(any());
    }
}