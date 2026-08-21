import { ChangeDetectionStrategy, Component, input, inject, effect, computed } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IAnimal } from '../animal.model';
import { PeserAnimalService } from '../../peser-animal/service/peser-animal.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-animal-detail',
  templateUrl: './animal-detail.html',

  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslatePipe, DecimalPipe, RouterLink, FormatMediumDatePipe],
})
export class AnimalDetail {
  readonly animal = input<IAnimal | null>(null);

  readonly dernierPoids = computed(() => {
    const list = this.peserAnimalService.peserAnimalsResource.value() ?? [];
    return list.length > 0 ? list[0].poids : null;
  });

  protected readonly peserAnimalService = inject(PeserAnimalService);

  constructor() {
    effect(() => {
      const currentAnimal = this.animal();
      if (currentAnimal?.id) {
        this.peserAnimalService.peserAnimalsParams.set({
          'animalId.equals': currentAnimal.id,
          sort: 'id,desc',
          size: 1,
        });
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }
}
