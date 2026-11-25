import {
  ApplicationConfig,
  inject,
  LOCALE_ID,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, withXhr } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';
import localeCs from '@angular/common/locales/cs';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { routes } from './app.routes';
import { authInterceptor, OidcSecurityService, provideAuth } from 'angular-auth-oidc-client';
import { tap } from 'rxjs';

registerLocaleData(localeCs, 'cs-CZ');

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withXhr(), withInterceptors([authInterceptor()])),
    provideAuth({
      config: {
        authority: 'http://localhost:8180/realms/feicargo',
        redirectUrl: window.location.origin,
        postLogoutRedirectUri: window.location.origin,
        clientId: 'feicargo-web',
        scope: 'openid profile email',
        responseType: 'code',
        silentRenew: true,
        useRefreshToken: true,
        secureRoutes: ['/api'],
      },
    }),
    provideAppInitializer(() => {
      const oidc = inject(OidcSecurityService);
      return oidc.checkAuth().pipe(
        tap(({ isAuthenticated }) => {
          if (!isAuthenticated) {
            oidc.authorize();
          }
        }),
      );
    }),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
        prefix: './i18n/',
        suffix: '.json',
      }),
    }),
    { provide: LOCALE_ID, useValue: 'cs-CZ' },
  ],
};
