import {ApplicationConfig, EnvironmentProviders, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideHttpClient, withInterceptors, withInterceptorsFromDi} from '@angular/common/http';
import {authInterceptor, loginRedirectInterceptor} from './common/interceptors/auth.interceptor';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ClarityModule} from '@clr/angular';
import {JWT_OPTIONS, JwtHelperService} from '@auth0/angular-jwt';

export const appConfig: { providers: any[] } = {
    providers:
        [
            provideZoneChangeDetection({eventCoalescing: true}),
            provideRouter(routes),
            provideHttpClient(
                withInterceptors([
                    authInterceptor,
                    loginRedirectInterceptor,
                ]),
                withInterceptorsFromDi()
            ),
            importProvidersFrom(
                BrowserModule,
                BrowserAnimationsModule,
                ClarityModule,
            ),
            {provide: JWT_OPTIONS, useValue: JWT_OPTIONS},
            JwtHelperService,
        ],
};
