package com.stg.petclinic.service;

import com.stg.petclinic.domain.*; // for static metamodels
import com.stg.petclinic.domain.Animal;
import com.stg.petclinic.repository.AnimalRepository;
import com.stg.petclinic.service.criteria.AnimalCriteria;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Animal} entities in the database.
 * The main input is a {@link AnimalCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Animal} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AnimalQueryService extends QueryService<Animal> {

    private static final Logger LOG = LoggerFactory.getLogger(AnimalQueryService.class);

    private final AnimalRepository animalRepository;

    public AnimalQueryService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * Return a {@link Page} of {@link Animal} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Animal> findByCriteria(AnimalCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Animal> specification = createSpecification(criteria);
        return animalRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AnimalCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Animal> specification = createSpecification(criteria);
        return animalRepository.count(specification);
    }

    /**
     * Function to convert {@link AnimalCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Animal> createSpecification(AnimalCriteria criteria) {
        Specification<Animal> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Animal_.client, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Animal_.id),
                    buildStringSpecification(criteria.getNom(), Animal_.nom),
                    buildSpecification(criteria.getEspece(), Animal_.espece),
                    buildRangeSpecification(criteria.getDateNaissance(), Animal_.dateNaissance),
                    buildSpecification(criteria.getSexe(), Animal_.sexe),
                    buildSpecification(criteria.getClientId(), root -> root.join(Animal_.client, JoinType.LEFT).get(Client_.id))
                )
            );
        }
        return specification;
    }
}
