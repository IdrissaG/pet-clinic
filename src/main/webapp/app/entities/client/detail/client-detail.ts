import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule, FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faPaw, faEye, faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslatePipe } from '@ngx-translate/core';

import { TranslateDirective } from 'app/shared/language';
import { IClient } from '../client.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-client-detail',
  templateUrl: './client-detail.html',
  imports: [RouterLink, FontAwesomeModule, TranslateDirective, TranslatePipe],
})
export class ClientDetail {
  readonly client = input<IClient | null>(null);

  private readonly iconLibrary = inject(FaIconLibrary);

  constructor() {
    this.iconLibrary.addIcons(faPaw, faEye, faArrowLeft, faPencilAlt);
  }

  previousState(): void {
    window.history.back();
  }
}
