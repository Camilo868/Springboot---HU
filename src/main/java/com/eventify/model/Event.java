package com.eventify.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("activo = true")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @Column(length = 450)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    // Relación Many-to-One obligatoria con Venue
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    // Relación Many-to-Many con Category usando tabla intermedia explícita
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "events_categories",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Category> categorias = new HashSet<>();

    // Constructor de conveniencia
    public Event(Long id, String nombre, LocalDate fecha, String descripcion, Venue venue) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.venue = venue;
        this.activo = true;
        this.categorias = new HashSet<>();
    }
}
