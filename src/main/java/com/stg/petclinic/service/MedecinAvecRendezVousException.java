package com.stg.petclinic.service;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

/**
 * Exception levée quand on tente de supprimer un Medecin
 * ayant des RendezVous rattachés.
 */
public class MedecinAvecRendezVousException extends ErrorResponseException {

    // Duplicated from ErrorConstants.DEFAULT_TYPE (web layer) rather than imported, to keep the
    // service layer from depending on the web layer (see TechnicalStructureTest architecture rule).
    private static final URI DEFAULT_TYPE = URI.create("https://www.jhipster.tech/problem/problem-with-message");

    public MedecinAvecRendezVousException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(), null);
    }

    private static ProblemDetailWithCause asProblemDetail() {
        return ProblemDetailWithCauseBuilder.instance()
            .withStatus(HttpStatus.BAD_REQUEST.value())
            .withType(DEFAULT_TYPE)
            .withTitle("Suppression impossible")
            .withDetail("Impossible de supprimer un médecin ayant des rendez-vous rattachés.")
            .withProperty("message", "error.medecinAvecRendezVous")
            .build();
    }
}
