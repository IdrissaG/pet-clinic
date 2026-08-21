import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../medecin.test-samples';

import { MedecinFormService } from './medecin-form.service';

describe('Medecin Form Service', () => {
  let service: MedecinFormService;

  beforeEach(() => {
    service = TestBed.inject(MedecinFormService);
  });

  describe('Service methods', () => {
    describe('createMedecinFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMedecinFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            specialite: expect.any(Object),
            email: expect.any(Object),
            telephone: expect.any(Object),
            clinique: expect.any(Object),
          }),
        );
      });

      it('passing IMedecin should create a new form with FormGroup', () => {
        const formGroup = service.createMedecinFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            specialite: expect.any(Object),
            email: expect.any(Object),
            telephone: expect.any(Object),
            clinique: expect.any(Object),
          }),
        );
      });
    });

    describe('getMedecin', () => {
      it('should return NewMedecin for default Medecin initial value', () => {
        const formGroup = service.createMedecinFormGroup(sampleWithNewData);

        const medecin = service.getMedecin(formGroup);

        expect(medecin).toMatchObject(sampleWithNewData);
      });

      it('should return NewMedecin for empty Medecin initial value', () => {
        const formGroup = service.createMedecinFormGroup();

        const medecin = service.getMedecin(formGroup);

        expect(medecin).toMatchObject({});
      });

      it('should return IMedecin', () => {
        const formGroup = service.createMedecinFormGroup(sampleWithRequiredData);

        const medecin = service.getMedecin(formGroup);

        expect(medecin).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMedecin should not enable id FormControl', () => {
        const formGroup = service.createMedecinFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMedecin should disable id FormControl', () => {
        const formGroup = service.createMedecinFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });

    describe('email validation', () => {
      it('should be invalid when email is empty', () => {
        const formGroup = service.createMedecinFormGroup();

        formGroup.controls.email.setValue('');

        expect(formGroup.controls.email.valid).toBe(false);
        expect(formGroup.controls.email.errors?.required).toBeTruthy();
      });

      it('should be invalid when email does not match the expected format', () => {
        const formGroup = service.createMedecinFormGroup();

        formGroup.controls.email.setValue('not-an-email');

        expect(formGroup.controls.email.valid).toBe(false);
        expect(formGroup.controls.email.errors?.pattern).toBeTruthy();
      });

      it('should be valid with a correctly formatted email', () => {
        const formGroup = service.createMedecinFormGroup();

        formGroup.controls.email.setValue('medecin@petclinic.fr');

        expect(formGroup.controls.email.valid).toBe(true);
      });
    });
  });
});
