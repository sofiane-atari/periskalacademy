import {ApplicationConfig,importProvidersFrom,provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';
import { authInterceptor} from './interceptors/auth.interceptor';
import { provideHttpClient, HttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import {provideTranslateService,TranslateLoader} from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideTranslateService({
      defaultLanguage: 'nl',
        loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient],
        },
    }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor])
    )

  ],
};
