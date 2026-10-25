package com.eventify.service;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.exception.*;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Venue defaultVenue;

    @BeforeEach
    void setUp() {
        defaultVenue = new Venue(1L, "Movistar Arena", "Diag 61c", "Bogotá", 14000);
    }

    // --- TEST: Crear (save) ---
    @Test
    void shouldSaveEventSuccessfully() {
        Event newEvent = new Event(null, "Conferencia Tech", LocalDate.now(), "Evento anual", defaultVenue);
        Event savedEvent = new Event(1L, "Conferencia Tech", LocalDate.now(), "Evento anual", defaultVenue);

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.save(newEvent);

        assertEquals(1L, result.getId());
        assertEquals("Conferencia Tech", result.getNombre());
        assertEquals("Bogotá", result.getVenue().getCiudad());
        verify(eventRepository, times(1)).save(newEvent);
    }

    @Test
    void shouldThrowInvalidDataExceptionWhenNameIsEmpty() {
        Event invalidEvent = new Event(null, "", LocalDate.now(), "Sin nombre", defaultVenue);

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            eventService.save(invalidEvent);
        });

        assertEquals("El nombre del evento no puede estar vacío", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldThrowInvalidDataExceptionWhenVenueIsNull() {
        Event invalidEvent = new Event(null, "Conferencia Tech", LocalDate.now(), "Sin lugar", null);

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            eventService.save(invalidEvent);
        });

        assertEquals("El evento debe tener un lugar asignado obligatoriamente", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    // --- TEST: Leer por ID (findById) ---
    @Test
    void shouldReturnEventWhenIdExists() {
        Event mockEvent = new Event(1L, "Taller Java", LocalDate.now(), "Taller práctico", defaultVenue);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));

        Event result = eventService.findById(1L);

        assertNotNull(result);
        assertEquals("Taller Java", result.getNombre());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            eventService.findById(99L);
        });

        assertEquals("Evento no encontrado con ID: 99", exception.getMessage());
    }

    // --- TEST: Paginación (findAll) ---
    @Test
    void shouldReturnPagedEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        Event mockEvent = new Event(1L, "Hackathon", LocalDate.now(), "Competencia", defaultVenue);
        Page<Event> mockPage = new PageImpl<>(List.of(mockEvent));

        when(eventRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Event> result = eventService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(eventRepository, times(1)).findAll(pageable);
    }

    // --- TEST: Búsqueda con Slice y Record DTO ---
    @Test
    void shouldReturnFilteredSummarySlice() {
        Pageable pageable = PageRequest.of(0, 10);
        EventSummaryDTO dto = new EventSummaryDTO(1L, "Concierto Rock", LocalDate.now(), "Desc", "Movistar Arena", "Bogotá");
        Slice<EventSummaryDTO> slice = new SliceImpl<>(List.of(dto));

        when(eventRepository.findFilteredSummarySlice(eq("Bogotá"), eq("Rock"), any(), any(), eq(pageable)))
                .thenReturn(slice);

        Slice<EventSummaryDTO> result = eventService.findFilteredSummarySlice("Bogotá", "Rock", null, null, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Bogotá", result.getContent().get(0).ciudad());
        verify(eventRepository, times(1)).findFilteredSummarySlice(eq("Bogotá"), eq("Rock"), any(), any(), eq(pageable));
    }

    // --- TEST: Actualizar (update) ---
    @Test
    void shouldUpdateEventSuccessfully() {
        Event existingEvent = new Event(1L, "Viejo Nombre", LocalDate.now(), "Vieja Desc", defaultVenue);
        Event updateData = new Event(null, "Nuevo Nombre", LocalDate.now(), "Nueva Desc", defaultVenue);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);

        Event result = eventService.update(1L, updateData);

        assertEquals("Nuevo Nombre", result.getNombre());
        assertEquals("Nueva Desc", result.getDescripcion());
        verify(eventRepository, times(1)).save(existingEvent);
    }

    // --- TEST: Borrado Lógico (Soft Delete) ---
    @Test
    void shouldSoftDeleteEventSuccessfully() {
        Event existingEvent = new Event(1L, "Concierto", LocalDate.now(), "Música", defaultVenue);
        existingEvent.setActivo(true);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);

        eventService.delete(1L);

        assertFalse(existingEvent.isActivo(), "El evento debe haber cambiado su estado a inactivo (Soft Delete)");
        verify(eventRepository, times(1)).save(existingEvent);
        verify(eventRepository, never()).delete(any());
    }
}
