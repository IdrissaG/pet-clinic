package com.stg.petclinic.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

/**
 * Exception levée quand on tente de créer ou modifier un RendezVous
 * dont la date est dans le passé.
 *
 * Construite sur le même modèle que BadRequestAlertException, en utilisant
 * ProblemDetailWithCause (et non le ProblemDetail standard de Spring), car
 * c'est le seul type reconnu correctement par ExceptionTranslator.
 */
public class RendezVousDatePasseeException extends ErrorResponseException {

    public RendezVousDatePasseeException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(), null);
    }

    private static ProblemDetailWithCause asProblemDetail() {
        return ProblemDetailWithCauseBuilder.instance()
            .withStatus(HttpStatus.BAD_REQUEST.value())
            .withType(ErrorConstants.DEFAULT_TYPE)
            .withTitle("Rendez-vous invalide")
            .withDetail("Impossible de créer un rendez-vous dans le passé.")
            .withProperty("message", "error.rendezVousDatePassee")
            .build();
    }
}
