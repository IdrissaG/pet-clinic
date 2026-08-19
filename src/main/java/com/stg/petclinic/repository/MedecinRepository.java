package com.stg.petclinic.repository;

import com.stg.petclinic.domain.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Medecin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    @Query(
        """
        select m from Medecin m
        where (:cliniqueId is null or m.clinique.id = :cliniqueId)
        and (:specialite is null or lower(m.specialite) like lower(concat('%', cast(:specialite as string), '%')))
        """
    )
    Page<Medecin> findByFilters(@Param("cliniqueId") Long cliniqueId, @Param("specialite") String specialite, Pageable pageable);
}
