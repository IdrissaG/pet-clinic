package com.stg.petclinic.repository;

import com.stg.petclinic.domain.RendezVous;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RendezVous entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findByDateBetween(Instant debut, Instant fin);

    boolean existsByMedecinId(Long medecinId);
}
