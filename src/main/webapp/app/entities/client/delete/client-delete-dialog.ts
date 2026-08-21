import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FontAwesomeModule, FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faBan, faTimes, faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';
import { TranslatePipe } from '@ngx-translate/core';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IClient } from '../client.model';
import { ClientService } from '../service/client.service';

@Component({
  standalone: true,
  selector: 'jhi-client-delete-dialog',
  templateUrl: './client-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError, TranslateDirective, TranslatePipe],
})
export class ClientDeleteDialog implements OnInit {
  client?: IClient;
  nbAnimaux = 0;

  protected clientService = inject(ClientService);
  protected activeModal = inject(NgbActiveModal);
  private iconLibrary = inject(FaIconLibrary);

  constructor() {
    this.iconLibrary.addIcons(faBan, faTimes, faExclamationTriangle);
  }

  ngOnInit(): void {
    if (this.client?.id) {
      this.clientService.find(this.client.id).subscribe((res: any) => {
        const clientData = res.body ?? res;
        if (clientData) {
          this.client = clientData;
          this.nbAnimaux = Array.isArray(clientData.animals) ? clientData.animals.length : 0;
        }
      });
    }
  }

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clientService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
