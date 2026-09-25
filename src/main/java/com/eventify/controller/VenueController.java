package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")

public class VenueController {
    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo lugar")
    public ResponseEntity<Venue> create(@RequestBody Venue venue) {
        return new ResponseEntity<>(venueService.save(venue), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Consultar catálogo de lugares paginado")
    public ResponseEntity<Page<Venue>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(venueService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un lugar por ID")
    public ResponseEntity<Venue> getById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un lugar existente")
    public ResponseEntity<Venue> update(@PathVariable Long id, @RequestBody Venue venue) {
        return ResponseEntity.ok(venueService.update(id, venue));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un lugar")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}