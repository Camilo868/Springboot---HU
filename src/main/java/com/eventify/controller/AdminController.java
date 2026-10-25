package com.eventify.controller;

import com.eventify.exception.InvalidDataException;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final EventService eventService;
    private final VenueService venueService;

    public AdminController(EventService eventService, VenueService venueService) {
        this.eventService = eventService;
        this.venueService = venueService;
    }

    // Redirección de la raíz al panel administrativo
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/admin/events";
    }

    // Panel Centralizado: Visualización de eventos y lugares
    @GetMapping({"/admin", "/admin/events"})
    public String showAdminDashboard(Model model) {
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("venues", venueService.findAll());
        return "admin/events";
    }

    // Redirección conveniente para /admin/venues hacia el panel
    @GetMapping("/admin/venues")
    public String venuesRedirect() {
        return "redirect:/admin/events";
    }

    // Formulario para registro de nuevo evento
    @GetMapping("/admin/events/new")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }

    // Procesamiento y guardado de nuevo evento (Post-Redirect-Get)
    @PostMapping("/admin/events")
    public String saveEvent(@ModelAttribute("event") Event event, RedirectAttributes redirectAttributes) {
        try {
            eventService.save(event);
            redirectAttributes.addFlashAttribute("successMessage", "¡Evento registrado exitosamente!");
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
