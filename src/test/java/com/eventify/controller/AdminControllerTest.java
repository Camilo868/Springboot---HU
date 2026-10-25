package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @Mock
    private VenueService venueService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    // Escenario 1: Visualización Centralizada (Camino Feliz)
    // Escenario 4: Validación de Navegación (Testing)
    @Test
    void shouldRenderAdminDashboardWithEventsAndVenues() throws Exception {
        Venue mockVenue = new Venue(1L, "Auditorio Principal", "Calle 123", 500);
        Event mockEvent = new Event(1L, "Conferencia Tech 2026", LocalDate.now(), "Encuentro de tecnología");

        when(eventService.findAll()).thenReturn(List.of(mockEvent));
        when(venueService.findAll()).thenReturn(List.of(mockVenue));

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attribute("events", hasSize(1)))
                .andExpect(model().attribute("venues", hasSize(1)));

        verify(eventService, times(1)).findAll();
        verify(venueService, times(1)).findAll();
    }

    // Escenario 2: Gestión de Inventario Vacío (Caso de Borde)
    @Test
    void shouldHandleEmptyCatalogState() throws Exception {
        when(eventService.findAll()).thenReturn(Collections.emptyList());
        when(venueService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attribute("events", empty()))
                .andExpect(model().attribute("venues", empty()));
    }

    // Formulario de creación de evento
    @Test
    void shouldDisplayCreateEventForm() throws Exception {
        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/event-form"))
                .andExpect(model().attributeExists("event"));
    }

    // Escenario 3: Registro y Redirección Exitosa (Patrón Post-Redirect-Get)
    @Test
    void shouldSaveEventAndRedirectToDashboard() throws Exception {
        Event savedEvent = new Event(1L, "Workshop Spring Boot", LocalDate.now(), "Taller práctico");
        when(eventService.save(any(Event.class))).thenReturn(savedEvent);

        mockMvc.perform(post("/admin/events")
                        .param("nombre", "Workshop Spring Boot")
                        .param("fecha", "2026-11-20")
                        .param("descripcion", "Taller práctico"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "¡Evento registrado exitosamente!"));

        verify(eventService, times(1)).save(any(Event.class));
    }

    // Formulario de creación de lugar
    @Test
    void shouldDisplayCreateVenueForm() throws Exception {
        mockMvc.perform(get("/admin/venues/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/venue-form"))
                .andExpect(model().attributeExists("venue"));
    }

    // Registro de lugar y redirección exitosa (Patrón Post-Redirect-Get)
    @Test
    void shouldSaveVenueAndRedirectToDashboard() throws Exception {
        Venue savedVenue = new Venue(1L, "Centro de Convenciones", "Av. El Sol 123", 400);
        when(venueService.save(any(Venue.class))).thenReturn(savedVenue);

        mockMvc.perform(post("/admin/venues")
                        .param("nombre", "Centro de Convenciones")
                        .param("direccion", "Av. El Sol 123")
                        .param("capacidad", "400"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "¡Lugar registrado exitosamente!"));

        verify(venueService, times(1)).save(any(Venue.class));
    }

    // Redirección de la raíz a /admin/events
    @Test
    void shouldRedirectRootToAdminEvents() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"));
    }
}
