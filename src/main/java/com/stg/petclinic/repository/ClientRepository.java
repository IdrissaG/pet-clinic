package com.stg.petclinic.repository;

import com.stg.petclinic.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Client entity.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    @Query(
        "select client from Client client where lower(client.nom) like lower(concat('%', :query, '%')) or lower(client.prenom) like lower(concat('%', :query, '%'))"
    )
    Page<Client> searchByName(@Param("query") String query, Pageable pageable);
}
