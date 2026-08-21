import { MockInstance, afterEach, beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes } from '@fortawesome/free-solid-svg-icons';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { provideTranslateService } from '@ngx-translate/core';
import { Subject, of } from 'rxjs';

import { sampleWithRequiredData } from '../medecin.test-samples';
import { MedecinService } from '../service/medecin.service';

import { Medecin } from './medecin';

vitest.useFakeTimers();

describe('Medecin Management Component', () => {
  let httpMock: HttpTestingController;
  let comp: Medecin;
  let fixture: ComponentFixture<Medecin>;
  let service: MedecinService;
  let routerNavigateSpy: MockInstance;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            ),
            snapshot: {
              queryParams: {},
              queryParamMap: convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            },
          },
        },
      ],
    });

    fixture = TestBed.createComponent(Medecin);
    comp = fixture.componentInstance;
    service = TestBed.inject(MedecinService);
    routerNavigateSpy = vitest.spyOn(comp.router, 'navigate');

    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
    httpMock.verify();
  });

  it('should call load all on init', async () => {
    // WHEN
    TestBed.tick();
    httpMock.expectOne(r => r.url.includes('cliniques')).flush([]);
    const req = httpMock.expectOne(r => r.url.includes('medecins'));
    req.flush([{ id: 19080 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vitest.runAllTimersAsync();

    // THEN
    expect(comp.isLoading()).toEqual(false);
    expect(comp.medecins()[0]).toEqual(expect.objectContaining({ id: 19080 }));
  });

  it('should cancel previous requests when loading a new page', async () => {
    // WHEN
    TestBed.tick();
    httpMock.expectOne(r => r.url.includes('cliniques')).flush([]);
    const req = httpMock.expectOne(r => r.url.includes('medecins'));
    await vitest.runAllTimersAsync();

    comp.page.set(3);
    comp.load();
    await vitest.runAllTimersAsync();
    const req2 = httpMock.expectOne(r => r.url.includes('medecins'));
    req2.flush([{ id: 19080 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vitest.runAllTimersAsync();

    // THEN
    expect(req.cancelled).toBeTruthy();
    expect(comp.isLoading()).toEqual(false);
    expect(comp.medecins()[0]).toEqual(expect.objectContaining({ id: 19080 }));
  });

  it('should not fail on resource error state', async () => {
    // GIVEN - first load triggers an HTTP error
    TestBed.tick();
    httpMock.expectOne(r => r.url.includes('cliniques')).flush([]);
    const errorReq = httpMock.expectOne(r => r.url.includes('medecins'));
    errorReq.flush('error', { status: 500, statusText: 'Server Error' });
    await vitest.runAllTimersAsync();

    // THEN - loading state was reset and list is empty
    expect(comp.isLoading()).toBe(false);
    expect(comp.medecins()).toEqual([]);

    // WHEN - second load should still work
    comp.load();
    TestBed.tick();
    const successReq = httpMock.expectOne({ method: 'GET' });
    successReq.flush([{ id: 19080 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vitest.runAllTimersAsync();

    // THEN - subscription is still alive and second load succeeds
    expect(comp.medecins()[0]).toEqual(expect.objectContaining({ id: 19080 }));
  });

  describe('trackId', () => {
    it('should forward to medecinService', () => {
      const entity = { id: 19080 };
      vitest.spyOn(service, 'getMedecinIdentifier');
      const id = comp.trackId(entity);
      expect(service.getMedecinIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // WHEN
    comp.navigateToWithComponentValues({ predicate: 'non-existing-column', order: 'asc' });

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['non-existing-column,asc'],
        }),
      }),
    );
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    TestBed.tick();
    httpMock.expectOne(r => r.url.includes('cliniques'));
    httpMock.expectOne(r => r.url.includes('medecins'));

    // THEN
    expect(service.medecinsParams()).toMatchObject(expect.objectContaining({ sort: ['id,desc'] }));
  });

  describe('filters', () => {
    it('should load cliniques for the filter dropdown', async () => {
      // WHEN
      TestBed.tick();
      const cliniquesReq = httpMock.expectOne(r => r.url.includes('cliniques'));
      cliniquesReq.flush([{ id: 1, nom: 'Clinique A' }]);
      httpMock.expectOne(r => r.url.includes('medecins'));
      await vitest.runAllTimersAsync();

      // THEN
      expect(comp.cliniquesForFilter()).toEqual([{ id: 1, nom: 'Clinique A' }]);
    });

    it('should update cliniqueFilter and navigate on clinique filter change', () => {
      // WHEN
      comp.onCliniqueFilterChange('5');

      // THEN
      expect(comp.cliniqueFilter()).toBe(5);
      expect(routerNavigateSpy).toHaveBeenLastCalledWith(
        expect.anything(),
        expect.objectContaining({
          queryParams: expect.objectContaining({ page: 1, cliniqueId: 5 }),
        }),
      );
    });

    it('should clear cliniqueFilter when selecting the empty option', () => {
      // GIVEN
      comp.onCliniqueFilterChange('5');

      // WHEN
      comp.onCliniqueFilterChange('');

      // THEN
      expect(comp.cliniqueFilter()).toBeNull();
      expect(routerNavigateSpy).toHaveBeenLastCalledWith(
        expect.anything(),
        expect.objectContaining({
          queryParams: expect.objectContaining({ cliniqueId: null }),
        }),
      );
    });

    it('should trim and update specialiteFilter and navigate on specialite filter change', () => {
      // WHEN
      comp.onSpecialiteFilterChange('  cardio  ');

      // THEN
      expect(comp.specialiteFilter()).toBe('cardio');
      expect(routerNavigateSpy).toHaveBeenLastCalledWith(
        expect.anything(),
        expect.objectContaining({
          queryParams: expect.objectContaining({ page: 1, specialite: 'cardio' }),
        }),
      );
    });

    it('should include cliniqueId and specialite in the backend query when set', () => {
      // GIVEN
      comp.cliniqueFilter.set(7);
      comp.specialiteFilter.set('dermato');

      // WHEN
      comp.load();

      // THEN
      expect(service.medecinsParams()).toMatchObject(expect.objectContaining({ cliniqueId: 7, specialite: 'dermato' }));
    });

    it('should not include cliniqueId or specialite in the backend query when unset', () => {
      // WHEN
      comp.load();

      // THEN
      expect(service.medecinsParams()).not.toHaveProperty('cliniqueId');
      expect(service.medecinsParams()).not.toHaveProperty('specialite');
    });
  });

  describe('filters restored from route', () => {
    beforeEach(() => {
      TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        providers: [
          provideTranslateService(),
          provideHttpClientTesting(),
          {
            provide: ActivatedRoute,
            useValue: {
              data: of({ defaultSort: 'id,asc' }),
              queryParamMap: of(
                convertToParamMap({
                  page: '1',
                  sort: 'id,desc',
                  cliniqueId: '3',
                  specialite: 'cardio',
                }),
              ),
              snapshot: { queryParams: {}, queryParamMap: convertToParamMap({}) },
            },
          },
        ],
      });
      fixture = TestBed.createComponent(Medecin);
      comp = fixture.componentInstance;
      const library = TestBed.inject(FaIconLibrary);
      library.addIcons(faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should restore cliniqueFilter and specialiteFilter from the URL', () => {
      // WHEN
      TestBed.tick();
      httpMock.expectOne(r => r.url.includes('cliniques')).flush([]);
      httpMock.expectOne(r => r.url.includes('medecins'));

      // THEN
      expect(comp.cliniqueFilter()).toBe(3);
      expect(comp.specialiteFilter()).toBe('cardio');
    });
  });

  describe('delete', () => {
    let ngbModal: NgbModal;
    let deleteModalMock: any;

    beforeEach(() => {
      deleteModalMock = { componentInstance: {}, closed: new Subject() };
      // NgbModal is not a singleton using TestBed.inject.
      // ngbModal = TestBed.inject(NgbModal);
      ngbModal = (comp as any).modalService;
      vitest.spyOn(ngbModal, 'open').mockReturnValue(deleteModalMock);
    });

    it('on confirm should call load', inject([], () => {
      // GIVEN
      vitest.spyOn(comp, 'load');

      // WHEN
      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next('deleted');

      // THEN
      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).toHaveBeenCalled();
    }));

    it('on dismiss should call load', inject([], () => {
      // GIVEN
      vitest.spyOn(comp, 'load');

      // WHEN
      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next();

      // THEN
      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).not.toHaveBeenCalled();
    }));
  });
});
