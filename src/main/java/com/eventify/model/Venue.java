package com.eventify.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Table(name="venues")
@NoArgsConstructor
@AllArgsConstructor
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String nombre;

    @Column(length = 100, nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Integer capacidad;
}