import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IRendezVous, NewRendezVous } from '../rendez-vous.model';

export type PartialUpdateRendezVous = Partial<IRendezVous> & Pick<IRendezVous, 'id'>;

type RestOf<T extends IRendezVous | NewRendezVous> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestRendezVous = RestOf<IRendezVous>;

export type NewRestRendezVous = RestOf<NewRendezVous>;

export type PartialUpdateRestRendezVous = RestOf<PartialUpdateRendezVous>;

@Injectable()
export class RendezVousesService {
  readonly rendezVousesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly rendezVousesResource = httpResource<RestRendezVous[]>(() => {
    const params = this.rendezVousesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of rendezVous that have been fetched. It is updated when the rendezVousesResource emits a new value.
   * In case of error while fetching the rendezVouses, the signal is set to an empty array.
   */
  readonly rendezVouses = computed(() =>
    (this.rendezVousesResource.hasValue() ? this.rendezVousesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/rendez-vous');

  protected convertValueFromServer(restRendezVous: RestRendezVous): IRendezVous {
    return {
      ...restRendezVous,
      date: restRendezVous.date ? dayjs(restRendezVous.date) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class RendezVousService extends RendezVousesService {
  protected readonly http = inject(HttpClient);

  create(rendezVous: NewRendezVous): Observable<IRendezVous> {
    const copy = this.convertValueFromClient(rendezVous);
    return this.http.post<RestRendezVous>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(rendezVous: IRendezVous): Observable<IRendezVous> {
    const copy = this.convertValueFromClient(rendezVous);
    return this.http
      .put<RestRendezVous>(`${this.resourceUrl}/${encodeURIComponent(this.getRendezVousIdentifier(rendezVous))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(rendezVous: PartialUpdateRendezVous): Observable<IRendezVous> {
    const copy = this.convertValueFromClient(rendezVous);
    return this.http
      .patch<RestRendezVous>(`${this.resourceUrl}/${encodeURIComponent(this.getRendezVousIdentifier(rendezVous))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IRendezVous> {
    return this.http
      .get<RestRendezVous>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IRendezVous[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRendezVous[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRendezVousIdentifier(rendezVous: Pick<IRendezVous, 'id'>): number {
    return rendezVous.id;
  }

  compareRendezVous(o1: Pick<IRendezVous, 'id'> | null, o2: Pick<IRendezVous, 'id'> | null): boolean {
    return o1 && o2 ? this.getRendezVousIdentifier(o1) === this.getRendezVousIdentifier(o2) : o1 === o2;
  }

  addRendezVousToCollectionIfMissing<Type extends Pick<IRendezVous, 'id'>>(
    rendezVousCollection: Type[],
    ...rendezVousesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rendezVouses: Type[] = rendezVousesToCheck.filter(isPresent);
    if (rendezVouses.length > 0) {
      const rendezVousCollectionIdentifiers = rendezVousCollection.map(rendezVousItem => this.getRendezVousIdentifier(rendezVousItem));
      const rendezVousesToAdd = rendezVouses.filter(rendezVousItem => {
        const rendezVousIdentifier = this.getRendezVousIdentifier(rendezVousItem);
        if (rendezVousCollectionIdentifiers.includes(rendezVousIdentifier)) {
          return false;
        }
        rendezVousCollectionIdentifiers.push(rendezVousIdentifier);
        return true;
      });
      return [...rendezVousesToAdd, ...rendezVousCollection];
    }
    return rendezVousCollection;
  }

  protected convertValueFromClient<T extends IRendezVous | NewRendezVous | PartialUpdateRendezVous>(rendezVous: T): RestOf<T> {
    return {
      ...rendezVous,
      date: rendezVous.date?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestRendezVous): IRendezVous {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestRendezVous[]): IRendezVous[] {
    return res.map(item => this.convertValueFromServer(item));
  }

  rendezVousToday(): Observable<IRendezVous[]> {
    return this.http.get<IRendezVous[]>(`${this.resourceUrl}/today`);
  }

  getAllRendezVous(): Observable<IRendezVous[]> {
    return this.http.get<any>(this.resourceUrl).pipe(map(res => res.content || res));
  }
}
