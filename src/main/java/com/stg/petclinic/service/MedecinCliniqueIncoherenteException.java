package com.stg.petclinic.service;

import com.stg.petclinic.web.rest.errors.ErrorConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

/**
 * Exception levée quand le médecin renseigné sur un RendezVous n'appartient pas
 * à la clinique de ce rendez-vous.
 *
 * Construite sur le même modèle que RendezVousDatePasseeException, en utilisant
 * ProblemDetailWithCause (et non le ProblemDetail standard de Spring), car
 * c'est le seul type reconnu correctement par ExceptionTranslator.
 */
public class MedecinCliniqueIncoherenteException extends ErrorResponseException {

    public MedecinCliniqueIncoherenteException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(), null);
    }

    private static ProblemDetailWithCause asProblemDetail() {
        return ProblemDetailWithCauseBuilder.instance()
            .withStatus(HttpStatus.BAD_REQUEST.value())
            .withType(ErrorConstants.DEFAULT_TYPE)
            .withTitle("Rendez-vous invalide")
            .withDetail("Le médecin sélectionné n'appartient pas à la clinique de ce rendez-vous.")
            .withProperty("message", "error.medecinCliniqueIncoherente")
            .build();
    }
}
