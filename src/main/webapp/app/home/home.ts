import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
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
  imports: [CommonModule],
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
