package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.CategoryRepository;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    // Escenario 1: Visualización Centralizada y Escenario 4: Validación de Navegación
    @Test
    void shouldRenderAdminDashboardWithEventsAndVenues() throws Exception {
        EventSummaryDTO dto = new EventSummaryDTO(1L, "Conferencia Tech 2026", LocalDate.now(), "Desc", "Auditorio", "Bogotá");
        Slice<EventSummaryDTO> slice = new SliceImpl<>(List.of(dto));
        Venue venue = new Venue(1L, "Auditorio Principal", "Calle 123", "Bogotá", 500);

        when(eventService.findFilteredSummarySlice(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(slice);
        when(venueService.findAll()).thenReturn(List.of(venue));
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("eventsSlice"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attribute("events", hasSize(1)));

        verify(eventService, times(1)).findFilteredSummarySlice(any(), any(), any(), any(), any(Pageable.class));
    }

    // Escenario 2: Gestión de Inventario Vacío
    @Test
    void shouldHandleEmptyCatalogState() throws Exception {
        Slice<EventSummaryDTO> emptySlice = new SliceImpl<>(Collections.emptyList());
        when(eventService.findFilteredSummarySlice(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(emptySlice);
        when(venueService.findAll()).thenReturn(Collections.emptyList());
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attribute("events", empty()));
    }

    // Formulario de creación de evento
    @Test
    void shouldDisplayCreateEventForm() throws Exception {
        when(venueService.findAll()).thenReturn(Collections.emptyList());
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/event-form"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attributeExists("allCategories"));
    }

    // Escenario 3: Registro Relacional y Redirección Exitosa (PRG)
    @Test
    void shouldSaveEventAndRedirectToDashboard() throws Exception {
        Venue venue = new Venue(1L, "Sede Central", "Calle 10", "Bogotá", 100);
        Event savedEvent = new Event(1L, "Workshop Spring Boot", LocalDate.now(), "Taller", venue);

        when(venueService.findById(1L)).thenReturn(venue);
        when(eventService.save(any(Event.class))).thenReturn(savedEvent);

        mockMvc.perform(post("/admin/events")
                        .param("nombre", "Workshop Spring Boot")
                        .param("fecha", "2026-11-20")
                        .param("descripcion", "Taller")
                        .param("venueId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "¡Evento registrado exitosamente con sus relaciones!"));

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

    // Registro de lugar con ciudad y redirección exitosa
    @Test
    void shouldSaveVenueAndRedirectToDashboard() throws Exception {
        Venue savedVenue = new Venue(1L, "Centro de Convenciones", "Av. El Sol 123", "Medellín", 400);
        when(venueService.save(any(Venue.class))).thenReturn(savedVenue);

        mockMvc.perform(post("/admin/venues")
                        .param("nombre", "Centro de Convenciones")
                        .param("direccion", "Av. El Sol 123")
                        .param("ciudad", "Medellín")
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
