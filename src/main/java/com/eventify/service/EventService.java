package com.eventify.service;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.exception.*;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Guardar
    public Event save(Event event) {
        if (event.getNombre() == null || event.getNombre().trim().isEmpty()) {
            throw new InvalidDataException("El nombre del evento no puede estar vacío");
        }
        if (event.getVenue() == null) {
            throw new InvalidDataException("El evento debe tener un lugar asignado obligatoriamente");
        }
        return eventRepository.save(event);
    }

    // Listar todos optimizado con EntityGraph ordenado por fecha DESC
    public List<Event> findAll() {
        return eventRepository.findAllWithRelations();
    }

    // Recibe Pageable y retorna Page
    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    // Listado masivo optimizado con Record DTO y Slice (sin COUNT(*))
    public Slice<EventSummaryDTO> findAllSummarySlice(Pageable pageable) {
        return eventRepository.findAllSummarySlice(pageable);
    }

    // Motor de búsqueda avanzado filtrado con proyección DTO y Slice
    public Slice<EventSummaryDTO> findFilteredSummarySlice(
            String ciudad,
            String categoria,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable
    ) {
        String cleanCiudad = (ciudad != null && !ciudad.trim().isEmpty()) ? ciudad.trim() : null;
        String cleanCategoria = (categoria != null && !categoria.trim().isEmpty()) ? categoria.trim() : null;
        return eventRepository.findFilteredSummarySlice(cleanCiudad, cleanCategoria, fechaInicio, fechaFin, pageable);
    }

    // Buscar por ID con manejo de error 404
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));
    }

    // Actualizar
    public Event update(Long id, Event eventDetails) {
        Event event = findById(id);
        event.setNombre(eventDetails.getNombre());
        event.setFecha(eventDetails.getFecha());
        event.setDescripcion(eventDetails.getDescripcion());
        if (eventDetails.getVenue() != null) {
            event.setVenue(eventDetails.getVenue());
        }
        if (eventDetails.getCategorias() != null) {
            event.setCategorias(eventDetails.getCategorias());
        }
        return eventRepository.save(event);
    }

    // Borrado lógico (Soft Delete) - Cumplimiento de regla de negocio
    public void delete(Long id) {
        Event event = findById(id);
        event.setActivo(false);
        eventRepository.save(event);
    }
}
