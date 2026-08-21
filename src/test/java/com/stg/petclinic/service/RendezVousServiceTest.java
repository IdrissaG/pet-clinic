package com.stg.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.stg.petclinic.domain.RendezVous;
import com.stg.petclinic.repository.RendezVousRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RendezVousServiceTest {

    @Mock
    private RendezVousRepository rendezVousRepository;

    private RendezVousService rendezVousService;

    @BeforeEach
    void setUp() {
        rendezVousService = new RendezVousService(rendezVousRepository);
    }

    @Test
    void save_avecDateDansLeFutur_devraitReussir() {
        // Arrange : un rendez-vous prévu dans 1 heure
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDate(Instant.now().plus(1, ChronoUnit.HOURS));

        when(rendezVousRepository.save(rendezVous)).thenReturn(rendezVous);

        // Act
        RendezVous resultat = rendezVousService.save(rendezVous);

        // Assert : aucune exception, le rendez-vous est bien sauvegardé
        assertThat(resultat).isEqualTo(rendezVous);
    }

    @Test
    void save_avecDateDansLePasse_devraitLeverUneException() {
        // Arrange : un rendez-vous prévu il y a 1 heure
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDate(Instant.now().minus(1, ChronoUnit.HOURS));

        // Act + Assert : la sauvegarde doit être refusée
        assertThatThrownBy(() -> rendezVousService.save(rendezVous)).isInstanceOf(RendezVousDatePasseeException.class);
    }

    @Test
    void save_avecDateTresProcheDeMaintenantDansLeFutur_devraitReussir() {
        // Cas limite : quelques secondes seulement dans le futur.
        // Sert à vérifier qu'on n'est pas trop strict autour de l'instant présent.
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDate(Instant.now().plusSeconds(5));

        when(rendezVousRepository.save(rendezVous)).thenReturn(rendezVous);

        RendezVous resultat = rendezVousService.save(rendezVous);

        assertThat(resultat).isEqualTo(rendezVous);
    }

    @Test
    void save_avecDateTresProcheDeMaintenantDansLePasse_devraitLeverUneException() {
        // Cas limite : quelques secondes seulement dans le passé.
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDate(Instant.now().minusSeconds(5));

        assertThatThrownBy(() -> rendezVousService.save(rendezVous)).isInstanceOf(RendezVousDatePasseeException.class);
    }

    @Test
    void save_avecDateNulle_neDevraitPasLeverDException() {
        // La règle métier ne s'applique que si une date est fournie.
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDate(null);

        when(rendezVousRepository.save(rendezVous)).thenReturn(rendezVous);

        RendezVous resultat = rendezVousService.save(rendezVous);

        assertThat(resultat).isEqualTo(rendezVous);
    }
}
