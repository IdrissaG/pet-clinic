import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IMedecin } from '../medecin.model';
import { MedecinService } from '../service/medecin.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './medecin-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class MedecinDeleteDialog {
  medecin?: IMedecin;

  protected readonly medecinService = inject(MedecinService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.medecinService.delete(id).subscribe({
      next: () => this.activeModal.close(ITEM_DELETED_EVENT),
      // Error is already surfaced globally via the HTTP error interceptor (jhi-alert-error);
      // this empty handler only prevents an unhandled RxJS exception, the modal stays open.
      error: () => {},
    });
  }
}
