import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
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

  previousState(): void {
    window.history.back();
  }
}
