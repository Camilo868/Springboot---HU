package com.eventify.service;

import com.eventify.exception.*;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        return eventRepository.save(event);
    }

    // Recibe Pageable y retorna Page
    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
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
        return eventRepository.save(event);
    }

    // Eliminar
    public void delete(Long id) {
        Event event = findById(id);
        eventRepository.delete(event);
    }
}