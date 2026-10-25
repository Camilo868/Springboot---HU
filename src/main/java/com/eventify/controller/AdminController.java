package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.exception.InvalidDataException;
import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.CategoryRepository;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {

    private final EventService eventService;
    private final VenueService venueService;
    private final CategoryRepository categoryRepository;

    public AdminController(
            EventService eventService,
            VenueService venueService,
            CategoryRepository categoryRepository
    ) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.categoryRepository = categoryRepository;
    }

    // Redirección de la raíz al panel administrativo
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/admin/events";
    }

    // Panel Centralizado con Motor de Búsqueda y Navegación Inteligente (Slice)
    @GetMapping({"/admin", "/admin/events"})
    public String showAdminDashboard(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<EventSummaryDTO> eventsSlice = eventService.findFilteredSummarySlice(
                ciudad, categoria, null, null, pageable
        );

        model.addAttribute("eventsSlice", eventsSlice);
        model.addAttribute("events", eventsSlice.getContent());
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("categories", categoryRepository.findAll());

        // Parámetros persistentes para la navegación
        model.addAttribute("currentCiudad", ciudad);
        model.addAttribute("currentCategoria", categoria);
        model.addAttribute("currentPage", page);

        return "admin/events";
    }

    @GetMapping("/admin/venues")
    public String venuesRedirect() {
        return "redirect:/admin/events";
    }

    // Formulario para registro de nuevo evento (con Sedes y Categorías)
    @GetMapping("/admin/events/new")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("allCategories", categoryRepository.findAll());
        return "admin/event-form";
    }

    // Procesamiento y guardado de nuevo evento (Post-Redirect-Get)
    @PostMapping("/admin/events")
    public String saveEvent(
            @ModelAttribute("event") Event event,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) List<Long> categoryIds,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (venueId != null) {
                event.setVenue(venueService.findById(venueId));
            }
            if (categoryIds != null && !categoryIds.isEmpty()) {
                Set<Category> categorias = new HashSet<>(categoryRepository.findAllById(categoryIds));
                event.setCategorias(categorias);
            }

            eventService.save(event);
            redirectAttributes.addFlashAttribute("successMessage", "¡Evento registrado exitosamente con sus relaciones!");
            return "redirect:/admin/events";
        } catch (InvalidDataException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/events/new";
        }
    }

    // Formulario para registro de nuevo lugar
    @GetMapping("/admin/venues/new")
    public String showCreateVenueForm(Model model) {
        model.addAttribute("venue", new Venue());
        return "admin/venue-form";
    }

    // Procesamiento y guardado de nuevo lugar (Post-Redirect-Get)
    @PostMapping("/admin/venues")
    public String saveVenue(@ModelAttribute("venue") Venue venue, RedirectAttributes redirectAttributes) {
        try {
            venueService.save(venue);
            redirectAttributes.addFlashAttribute("successMessage", "¡Lugar registrado exitosamente!");
            return "redirect:/admin/events";
        } catch (InvalidDataException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/venues/new";
        }
    }
}
