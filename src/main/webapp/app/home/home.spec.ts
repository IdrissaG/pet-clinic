import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';

import Home from './home';

describe('Home Component', () => {
  let comp: Home;
  let fixture: ComponentFixture<Home>;
  let mockAccountService: AccountService;
  let mockRouter: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        {
          provide: AccountService,
          useValue: {
            isAuthenticated: vitest.fn(),
          },
        },
      ],
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Home);
    comp = fixture.componentInstance;
    mockAccountService = TestBed.inject(AccountService);
    mockAccountService.identity = vitest.fn(() => of(null));

    mockRouter = TestBed.inject(Router);
    vitest.spyOn(mockRouter, 'navigate');
  });

  // TODO(G5) : test obsolète — le composant Home a été remplacé par un dashboard,
  // login() n'existe plus sur cette classe. À réactiver si login() revient un jour.
  // describe('login', (): void => {
  //   it('should navigate to /login on login', (): void => {
  //     // WHEN
  //     comp.login();
  //
  //     // THEN
  //     expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  //   });
  // });
});
