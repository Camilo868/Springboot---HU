package com.eventify.service;

import com.eventify.exception.*;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    // EL clásico de guardar
    public Venue save(Venue venue) {
        if (venue.getNombre() == null || venue.getNombre().trim().isEmpty()) {
            throw new InvalidDataException("El nombre del lugar no puede estar vacío");
        }
        return venueRepository.save(venue);
    }

    // Aqui Paginación
    public Page<Venue> findAll(Pageable pageable) {
        return venueRepository.findAll(pageable);
    }

    // Buscar por ID
    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado con ID: " + id));
    }

    // Actualizar
    public Venue update(Long id, Venue venueDetails) {
        Venue venue = findById(id);
        venue.setNombre(venueDetails.getNombre());
        venue.setDireccion(venueDetails.getDireccion());
        venue.setCapacidad(venueDetails.getCapacidad());
        return venueRepository.save(venue);
    }

    // Eliminar
    public void delete(Long id) {
        Venue venue = findById(id);
        venueRepository.delete(venue);
    }
}