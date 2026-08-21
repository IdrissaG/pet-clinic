import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IClinique } from 'app/entities/clinique/clinique.model';
import { CliniqueService } from 'app/entities/clinique/service/clinique.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IMedecin } from '../medecin.model';
import { MedecinService } from '../service/medecin.service';

import { MedecinFormGroup, MedecinFormService } from './medecin-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-medecin-update',
  templateUrl: './medecin-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MedecinUpdate implements OnInit {
  readonly isSaving = signal(false);
  medecin: IMedecin | null = null;

  cliniquesSharedCollection = signal<IClinique[]>([]);

  protected medecinService = inject(MedecinService);
  protected medecinFormService = inject(MedecinFormService);
  protected cliniqueService = inject(CliniqueService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MedecinFormGroup = this.medecinFormService.createMedecinFormGroup();

  compareClinique = (o1: IClinique | null, o2: IClinique | null): boolean => this.cliniqueService.compareClinique(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ medecin }) => {
      this.medecin = medecin;
      if (medecin) {
        this.updateForm(medecin);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const medecin = this.medecinFormService.getMedecin(this.editForm);
    if (medecin.id === null) {
      this.subscribeToSaveResponse(this.medecinService.create(medecin));
    } else {
      this.subscribeToSaveResponse(this.medecinService.update(medecin));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMedecin | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(medecin: IMedecin): void {
    this.medecin = medecin;
    this.medecinFormService.resetForm(this.editForm, medecin);

    this.cliniquesSharedCollection.update(cliniques =>
      this.cliniqueService.addCliniqueToCollectionIfMissing<IClinique>(cliniques, medecin.clinique),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.cliniqueService
      .query({ size: 9999 })
      .pipe(map((res: HttpResponse<IClinique[]>) => res.body ?? []))
      .pipe(
        map((cliniques: IClinique[]) =>
          this.cliniqueService.addCliniqueToCollectionIfMissing<IClinique>(cliniques, this.medecin?.clinique),
        ),
      )
      .subscribe((cliniques: IClinique[]) => this.cliniquesSharedCollection.set(cliniques));
  }
}
