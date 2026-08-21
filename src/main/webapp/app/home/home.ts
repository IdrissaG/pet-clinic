import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AccountService } from 'app/core/auth/account.service';
import { AnimalService } from 'app/entities/animal/service/animal.service';
import { IRendezVous } from 'app/entities/rendez-vous/rendez-vous.model';
import { RendezVousService } from 'app/entities/rendez-vous/service/rendez-vous.service';
import { TranslateDirective } from 'app/shared/language';

@Component({
  selector: 'jhi-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [CommonModule, FormsModule],
})
export default class Home implements OnInit {
  today: number = Date.now();
  private readonly animalService = inject(AnimalService);
  private readonly rendezVousService = inject(RendezVousService);
  private readonly router = inject(Router);

  totalAnimals = signal<number | null>(null);
  isLoadingAnimals = signal<boolean>(true);
  totalRendezVous = signal<number | null>(null);
  isLoadingRendezVous = signal<boolean>(true);
  listeRendezVous = signal<IRendezVous[]>([]);

  // Signals pour les filtres de la page d'accueil
  readonly searchTerm = signal<string>('');
  readonly cliniqueFilter = signal<string>('');
  readonly dateFilter = signal<string>('');
  readonly dureeMaxFilter = signal<string>('');

  // Signal dérivé : Filtrage réactif de la liste des RDV
  readonly filteredListeRendezVous = computed(() => {
    const search = this.searchTerm().toLowerCase().trim();
    const clinique = this.cliniqueFilter().toLowerCase().trim();
    const date = this.dateFilter();
    const dureeMax = this.dureeMaxFilter() ? Number(this.dureeMaxFilter()) : null;

    return this.listeRendezVous().filter(rv => {
      // 1. Recherche globale (Animal, Médecin, Motif)
      const matchesSearch =
        !search ||
        (rv.animal?.nom?.toLowerCase().includes(search) ?? false) ||
        (rv.medecin?.nom?.toLowerCase().includes(search) ?? false) ||
        (rv.motif?.toLowerCase().includes(search) ?? false);

      // 2. Filtre par Clinique (par nom ou ID)
      const matchesClinique =
        !clinique || (rv.clinique?.nom?.toLowerCase().includes(clinique) ?? false) || rv.clinique?.id?.toString() === clinique;

      // 3. Filtre par Date
      let rdvDateStr = '';
      const rawDate: any = (rv as any).date ?? (rv as any).dateHeure;
      if (rawDate) {
        if (typeof rawDate.format === 'function') {
          rdvDateStr = rawDate.format('YYYY-MM-DD');
        } else if (typeof rawDate === 'string') {
          rdvDateStr = rawDate.slice(0, 10);
        }
      }
      const matchesDate = !date || rdvDateStr === date;

      // 4. Filtre par Durée maximale
      const matchesDuree = !dureeMax || (rv.duree ?? 0) <= dureeMax;

      return matchesSearch && matchesClinique && matchesDate && matchesDuree;
    });
  });

  // Méthodes pour réinitialiser les filtres
  resetFilters(): void {
    this.searchTerm.set('');
    this.cliniqueFilter.set('');
    this.dateFilter.set('');
    this.dureeMaxFilter.set('');
  }
  // Mode d'affichage actif : true = Tous les RDV, false = RDV du jour
  afficherTout = signal<boolean>(false);

  ngOnInit(): void {
    this.loadTotalAnimals();
    this.loadRendezVous();
  }

  loadTotalAnimals() {
    this.animalService.countTotal().subscribe({
      next: res => {
        const totalHeaders = res.headers.get('X-Total-Count');
        const count = totalHeaders ? parseInt(totalHeaders, 10) : (res.body?.length ?? 0);

        this.totalAnimals.set(count);
        this.isLoadingAnimals.set(false);
      },
      error: () => {
        this.totalAnimals.set(0);
        this.isLoadingAnimals.set(false);
      },
    });
  }

  loadRendezVous() {
    this.afficherTout.set(false);
    this.isLoadingRendezVous.set(true);
    this.rendezVousService.rendezVousToday().subscribe({
      next: res => {
        console.log('Données reçues du serveur :', res);
        const data = Array.isArray(res) ? res : ((res as any)?.body ?? []);
        this.totalRendezVous.set(data.length);
        this.listeRendezVous.set(data);
        this.isLoadingRendezVous.set(false);
      },
      error: () => {
        this.totalRendezVous.set(0);
        this.listeRendezVous.set([]);
        this.isLoadingRendezVous.set(false);
      },
    });
  }

  getAllRendezVous() {
    if (this.afficherTout()) {
      this.loadRendezVous();
      return;
    }

    this.isLoadingRendezVous.set(true);
    this.rendezVousService.getAllRendezVous().subscribe({
      next: res => {
        console.log('Données reçues du serveur pour tous les rendez-vous :', res);
        // Gestion si la réponse Spring est un objet Page ({ content: [...] }) ou un Array
        const data = Array.isArray(res) ? res : ((res as any)?.content ?? (res as any)?.body ?? []);
        this.listeRendezVous.set(data);
        this.afficherTout.set(true);
        this.isLoadingRendezVous.set(false);
      },
      error: () => {
        this.listeRendezVous.set([]);
        this.isLoadingRendezVous.set(false);
      },
    });
  }

  viewDetail(id?: number): void {
    if (id) {
      //premiére méthode this.router.navigate(['/rendez-vous', id, 'view']);
      this.router.navigate([`/rendez-vous/${id}/view`]);
    }
  }
}
