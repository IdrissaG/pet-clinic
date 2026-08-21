import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { IClinique } from 'app/entities/clinique/clinique.model';

import { TranslateDirective } from 'app/shared/language';

@Component({
  selector: 'jhi-footer',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './footer.html',
  imports: [TranslateDirective, RouterLink],
})
export default class Footer {
  private readonly router = inject(Router);

  getCliniques(): void {
    this.router.navigate(['/clinique']);
  }

  getMedecins(): void {
    this.router.navigate(['/medecin']);
  }

  getRendezVous(): void {
    this.router.navigate(['/rendez-vous']);
  }

  getClients(): void {
    this.router.navigate(['/client']);
  }

  getAnimaux(): void {
    this.router.navigate(['/animal']);
  }
}
