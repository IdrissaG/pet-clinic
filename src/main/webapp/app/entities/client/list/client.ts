import { HttpHeaders } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule, FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faPhone, faTrash, faEye, faPencilAlt, faPlus, faSort, faSync, faSearch, faTimes } from '@fortawesome/free-solid-svg-icons';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { TranslatePipe } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { IClient } from '../client.model';
import { ClientDeleteDialog } from '../delete/client-delete-dialog';
import { ClientService } from '../service/client.service';

import { UpperCasePipe, TitleCasePipe } from '@angular/common';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-client',
  templateUrl: './client.html',
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
    NgbPagination,
    ItemCount,
    UpperCasePipe,
    TitleCasePipe,
  ],
})
export class Client implements OnInit {
  subscription: Subscription | null = null;
  readonly clients = signal<IClient[]>([]);

  sortState = sortStateSignal({});

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);
  currentSearch = '';

  readonly router = inject(Router);
  protected readonly clientService = inject(ClientService);
  readonly isLoading = this.clientService.clientsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  private readonly iconLibrary = inject(FaIconLibrary);

  constructor() {
    // Enregistrement explicite de l'icône faPhone et des icônes courantes de la liste
    this.iconLibrary.addIcons(faPhone, faTrash, faEye, faPencilAlt, faPlus, faSort, faSync, faSearch, faTimes);

    effect(() => {
      const headers = this.clientService.clientsResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.clients.set(this.fillComponentAttributesFromResponseBody([...this.clientService.clients()]));
    });
  }

  trackId = (item: IClient): number => this.clientService.getClientIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(client: IClient): void {
    const modalRef = this.modalService.open(ClientDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.client = client;
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

  search(query: string): void {
    this.currentSearch = query;
    this.handleNavigation(1, this.sortState(), this.currentSearch);
  }

  clearSearch(): void {
    this.currentSearch = '';
    this.handleNavigation(1, this.sortState(), '');
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event, this.currentSearch);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.currentSearch);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.currentSearch = params.get('query') ?? '';
  }

  protected fillComponentAttributesFromResponseBody(data: IClient[]): IClient[] {
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

    if (this.currentSearch && this.currentSearch.trim() !== '') {
      queryObject.query = this.currentSearch.trim();
    }

    this.clientService.clientsParams.set(queryObject);
  }

  protected handleNavigation(page: number, sortState: SortState, currentSearch?: string): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };

    if (currentSearch && currentSearch.trim() !== '') {
      queryParamsObj.query = currentSearch.trim();
    }

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
