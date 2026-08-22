import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IClinique, NewClinique } from '../clinique.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClinique for edit and NewCliniqueFormGroupInput for create.
 */
type CliniqueFormGroupInput = IClinique | PartialWithRequiredKeyOf<NewClinique>;

type CliniqueFormDefaults = Pick<NewClinique, 'id'>;

type CliniqueFormGroupContent = {
  id: FormControl<IClinique['id'] | NewClinique['id']>;
  nom: FormControl<IClinique['nom']>;
  adresse: FormControl<IClinique['adresse']>;
  telephone: FormControl<IClinique['telephone']>;
};

export type CliniqueFormGroup = FormGroup<CliniqueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CliniqueFormService {
  createCliniqueFormGroup(clinique?: CliniqueFormGroupInput): CliniqueFormGroup {
    const cliniqueRawValue = {
      ...this.getFormDefaults(),
      ...(clinique ?? { id: null }),
    };

    return new FormGroup<CliniqueFormGroupContent>({
      id: new FormControl(
        { value: cliniqueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(cliniqueRawValue.nom, {
        validators: [Validators.required],
      }),
      adresse: new FormControl(cliniqueRawValue.adresse, {
        validators: [Validators.required],
      }),
      telephone: new FormControl(cliniqueRawValue.telephone, {
        validators: [Validators.required, Validators.pattern(/^\+221[0-9]{9}$/)],
      }),
    });
  }

  getClinique(form: CliniqueFormGroup): IClinique | NewClinique {
    return form.getRawValue();
  }

  resetForm(form: CliniqueFormGroup, clinique: CliniqueFormGroupInput): void {
    const cliniqueRawValue = { ...this.getFormDefaults(), ...clinique };
    form.reset({
      ...cliniqueRawValue,
      id: { value: cliniqueRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CliniqueFormDefaults {
    return {
      id: null,
    };
  }
}
