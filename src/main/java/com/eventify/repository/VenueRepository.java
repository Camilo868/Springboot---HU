package com.eventify.repository;

import com.eventify.model.Venue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    Page <Venue> findByNombreContaining(String nombre, Pageable pageable);
}