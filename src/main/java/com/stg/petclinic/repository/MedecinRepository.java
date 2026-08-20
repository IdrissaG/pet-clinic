package com.stg.petclinic.repository;

import com.stg.petclinic.domain.Medecin;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Medecin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    boolean existsByCliniqueId(Long cliniqueId);
}
