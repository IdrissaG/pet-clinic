import { HttpHeaders } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { TranslatePipe } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { RendezVousDeleteDialog } from '../delete/rendez-vous-delete-dialog';
import { IRendezVous } from '../rendez-vous.model';
import { RendezVousService } from '../service/rendez-vous.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-rendez-vous',
  templateUrl: './rendez-vous.html',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    TranslatePipe,
    FormatMediumDatetimePipe,
    NgbPagination,
    ItemCount,
  ],
})
export class RendezVous implements OnInit {
  subscription: Subscription | null = null;
  readonly rendezVouses = signal<IRendezVous[]>([]);

  sortState = sortStateSignal({});

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);

  // Signals pour les filtres en direct
  readonly dateFilter = signal<string>('');
  readonly animalFilter = signal<string>('');
  readonly medecinFilter = signal<string>('');
  readonly cliniqueFilter = signal<string>('');

  // Signal dérivé : Filtrage réactif local
  readonly filteredRendezVouses = computed(() => {
    const date = this.dateFilter();
    const animal = this.animalFilter().toLowerCase().trim();
    const medecin = this.medecinFilter().toLowerCase().trim();
    const clinique = this.cliniqueFilter().toLowerCase().trim();

    return this.rendezVouses().filter(rdv => {
      // Extraction date (Dayjs ou String)
      let rdvDateStr = '';
      const rawDate: any = (rdv as any).date ?? (rdv as any).dateHeure;
      if (rawDate) {
        if (typeof rawDate.format === 'function') {
          rdvDateStr = rawDate.format('YYYY-MM-DD');
        } else if (typeof rawDate === 'string') {
          rdvDateStr = rawDate.slice(0, 10);
        }
      }

      const matchesDate = !date || rdvDateStr === date;
      const matchesAnimal = !animal || ((rdv as any).animal?.nom?.toLowerCase().includes(animal) ?? false);
      const matchesMedecin =
        !medecin ||
        ((rdv as any).medecin?.nom?.toLowerCase().includes(medecin) ?? false) ||
        ((rdv as any).medecin?.prenom?.toLowerCase().includes(medecin) ?? false);
      const matchesClinique = !clinique || ((rdv as any).clinique?.nom?.toLowerCase().includes(clinique) ?? false);

      return matchesDate && matchesAnimal && matchesMedecin && matchesClinique;
    });
  });

  readonly router = inject(Router);
  protected readonly rendezVousService = inject(RendezVousService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.rendezVousService.rendezVousesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      const headers = this.rendezVousService.rendezVousesResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.rendezVouses.set(this.fillComponentAttributesFromResponseBody([...this.rendezVousService.rendezVouses()]));
    });
  }

  trackId = (item: IRendezVous): number => this.rendezVousService.getRendezVousIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(rendezVous: IRendezVous): void {
    const modalRef = this.modalService.open(RendezVousDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.rendezVous = rendezVous;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  onDateChange(value: string): void {
    this.dateFilter.set(value);
  }

  onAnimalChange(value: string): void {
    this.animalFilter.set(value);
  }

  onMedecinChange(value: string): void {
    this.medecinFilter.set(value);
  }

  onCliniqueChange(value: string): void {
    this.cliniqueFilter.set(value);
  }

  resetFilters(): void {
    this.dateFilter.set('');
    this.animalFilter.set('');
    this.medecinFilter.set('');
    this.cliniqueFilter.set('');
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState());
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected fillComponentAttributesFromResponseBody(data: IRendezVous[]): IRendezVous[] {
    return data;
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems.set(Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
  }

  protected queryBackend(): void {
    const pageToLoad: number = this.page();
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.rendezVousService.rendezVousesParams.set(queryObject);
  }

  protected handleNavigation(page: number, sortState: SortState): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
