import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAnimal, NewAnimal } from '../animal.model';
import { Animal } from '../list/animal';

export type EntityResponseType = HttpResponse<IAnimal>;
export type EntityArrayResponseType = HttpResponse<IAnimal[]>;

export type PartialUpdateAnimal = Partial<IAnimal> & Pick<IAnimal, 'id'>;

type RestOf<T extends IAnimal | NewAnimal> = Omit<T, 'dateNaissance'> & {
  dateNaissance?: string | null;
};

export type RestAnimal = RestOf<IAnimal>;

export type NewRestAnimal = RestOf<NewAnimal>;

export type PartialUpdateRestAnimal = RestOf<PartialUpdateAnimal>;

@Injectable()
export class AnimalsService {
  readonly animalsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly animalsResource = httpResource<RestAnimal[]>(() => {
    const params = this.animalsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });

  readonly animals = computed(() =>
    (this.animalsResource.hasValue() ? this.animalsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/animals');

  protected convertValueFromServer(restAnimal: RestAnimal): IAnimal {
    return {
      ...restAnimal,
      dateNaissance: restAnimal.dateNaissance ? dayjs(restAnimal.dateNaissance) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class AnimalService extends AnimalsService {
  protected readonly http = inject(HttpClient);

  create(animal: NewAnimal): Observable<IAnimal> {
    const copy = this.convertValueFromClient(animal);
    return this.http.post<RestAnimal>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(animal: IAnimal): Observable<IAnimal> {
    const copy = this.convertValueFromClient(animal);
    return this.http
      .put<RestAnimal>(`${this.resourceUrl}/${encodeURIComponent(this.getAnimalIdentifier(animal))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(animal: PartialUpdateAnimal): Observable<IAnimal> {
    const copy = this.convertValueFromClient(animal);
    return this.http
      .patch<RestAnimal>(`${this.resourceUrl}/${encodeURIComponent(this.getAnimalIdentifier(animal))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAnimal> {
    return this.http.get<RestAnimal>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  findByClient(clientId: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<RestAnimal[]>(`${this.resourceUrl}/client/${clientId}`, { observe: 'response' })
      .pipe(map(res => res.clone({ body: res.body ? this.convertResponseArrayFromServer(res.body) : [] })));
  }

  query(req?: any): Observable<HttpResponse<IAnimal[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAnimal[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAnimalIdentifier(animal: Pick<IAnimal, 'id'>): number {
    return animal.id;
  }

  compareAnimal(o1: Pick<IAnimal, 'id'> | null, o2: Pick<IAnimal, 'id'> | null): boolean {
    return o1 && o2 ? this.getAnimalIdentifier(o1) === this.getAnimalIdentifier(o2) : o1 === o2;
  }

  addAnimalToCollectionIfMissing<Type extends Pick<IAnimal, 'id'>>(
    animalCollection: Type[],
    ...animalsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const animals: Type[] = animalsToCheck.filter(isPresent);
    if (animals.length > 0) {
      const animalCollectionIdentifiers = animalCollection.map(animalItem => this.getAnimalIdentifier(animalItem));
      const animalsToAdd = animals.filter(animalItem => {
        const animalIdentifier = this.getAnimalIdentifier(animalItem);
        if (animalCollectionIdentifiers.includes(animalIdentifier)) {
          return false;
        }
        animalCollectionIdentifiers.push(animalIdentifier);
        return true;
      });
      return [...animalsToAdd, ...animalCollection];
    }
    return animalCollection;
  }

  protected convertValueFromClient<T extends IAnimal | NewAnimal | PartialUpdateAnimal>(animal: T): RestOf<T> {
    return {
      ...animal,
      dateNaissance: animal.dateNaissance?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAnimal): IAnimal {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAnimal[]): IAnimal[] {
    return res.map(item => this.convertValueFromServer(item));
  }

  private readonly baseUrl = 'api/animals';

  countTotal(): Observable<HttpResponse<IAnimal[]>> {
    return this.http.get<IAnimal[]>(this.baseUrl, {
      params: { page: '0', size: '1' },
      observe: 'response',
    });
  }
}
