import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IClient, NewClient } from '../client.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClient for edit and NewClientFormGroupInput for create.
 */
type ClientFormGroupInput = IClient | PartialWithRequiredKeyOf<NewClient>;

type ClientFormDefaults = Pick<NewClient, 'id'>;

type ClientFormGroupContent = {
  id: FormControl<IClient['id'] | NewClient['id']>;
  nom: FormControl<IClient['nom']>;
  prenom: FormControl<IClient['prenom']>;
  adresse: FormControl<IClient['adresse']>;
  telephone: FormControl<IClient['telephone']>;
  email: FormControl<IClient['email']>;
};

export type ClientFormGroup = FormGroup<ClientFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClientFormService {
  createClientFormGroup(client?: ClientFormGroupInput): ClientFormGroup {
    const clientRawValue = {
      ...this.getFormDefaults(),
      ...(client ?? { id: null }),
    };

    return new FormGroup<ClientFormGroupContent>({
      id: new FormControl(
        { value: clientRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),

      nom: new FormControl(clientRawValue.nom, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(30)],
      }),

      prenom: new FormControl(clientRawValue.prenom, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
      }),

      adresse: new FormControl(clientRawValue.adresse),

      telephone: new FormControl(clientRawValue.telephone, {
        validators: [
          Validators.required,
          Validators.pattern('^(70|75|76|77|78)\\d{7}$'), // NOSONAR
        ],
      }),

      email: new FormControl(clientRawValue.email, {
        validators: [
          Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$'), // NOSONAR
        ],
      }),
    });
  }

  getClient(form: ClientFormGroup): IClient | NewClient {
    return form.getRawValue();
  }

  resetForm(form: ClientFormGroup, client: ClientFormGroupInput): void {
    const clientRawValue = { ...this.getFormDefaults(), ...client };

    form.reset({
      ...clientRawValue,
      id: { value: clientRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ClientFormDefaults {
    return {
      id: null,
    };
  }
}
