import {ApplicationConfig, EnvironmentProviders, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideHttpClient, withInterceptors, withInterceptorsFromDi} from '@angular/common/http';
import {authInterceptor} from './common/interceptors/auth.interceptor';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ClarityModule} from '@clr/angular';

export const appConfig: { providers: (EnvironmentProviders)[] } = {
    providers:
        [
            provideZoneChangeDetection({eventCoalescing: true}),
            provideRouter(routes),
            provideHttpClient(
                withInterceptors([authInterceptor]),
                withInterceptorsFromDi()
            ),
            importProvidersFrom(
                BrowserModule,
                BrowserAnimationsModule,
                ClarityModule,
            )
        ],

};
