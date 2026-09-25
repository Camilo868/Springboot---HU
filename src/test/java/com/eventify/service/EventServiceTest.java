package com.eventify.service;

import com.eventify.exception.*;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    // --- TEST: Crear (save) ---
    @Test
    void shouldSaveEventSuccessfully() {
        Event newEvent = new Event(null, "Conferencia Tech", LocalDate.now(), "Evento anual");
        Event savedEvent = new Event(1L, "Conferencia Tech", LocalDate.now(), "Evento anual");

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.save(newEvent);

        assertEquals(1L, result.getId());
        assertEquals("Conferencia Tech", result.getNombre());
        verify(eventRepository, times(1)).save(newEvent);
    }

    @Test
    void shouldThrowInvalidDataExceptionWhenNameIsEmpty() {
        Event invalidEvent = new Event(null, "", LocalDate.now(), "Sin nombre");

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            eventService.save(invalidEvent);
        });

        assertEquals("El nombre del evento no puede estar vacío", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    // --- TEST: Leer por ID (findById) ---
    @Test
    void shouldReturnEventWhenIdExists() {
        Event mockEvent = new Event(1L, "Taller Java", LocalDate.now(), "Taller práctico");
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
        Event mockEvent = new Event(1L, "Hackathon", LocalDate.now(), "Competencia");
        Page<Event> mockPage = new PageImpl<>(List.of(mockEvent));

        when(eventRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Event> result = eventService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(eventRepository, times(1)).findAll(pageable);
    }

    // --- TEST: Actualizar (update) ---
    @Test
    void shouldUpdateEventSuccessfully() {
        Event existingEvent = new Event(1L, "Viejo Nombre", LocalDate.now(), "Vieja Desc");
        Event updateData = new Event(null, "Nuevo Nombre", LocalDate.now(), "Nueva Desc");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);

        Event result = eventService.update(1L, updateData);

        assertEquals("Nuevo Nombre", result.getNombre());
        assertEquals("Nueva Desc", result.getDescripcion());
        verify(eventRepository, times(1)).save(existingEvent);
    }

    // --- TEST: Eliminar (delete) ---
    @Test
    void shouldDeleteEventSuccessfully() {
        Event existingEvent = new Event(1L, "Concierto", LocalDate.now(), "Música");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        doNothing().when(eventRepository).delete(existingEvent);

        eventService.delete(1L);

        verify(eventRepository, times(1)).delete(existingEvent);
    }
}