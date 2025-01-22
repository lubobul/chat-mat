import {ApplicationConfig, EnvironmentProviders, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideHttpClient, withInterceptors, withInterceptorsFromDi} from '@angular/common/http';
import {authInterceptor} from './common/interceptors/auth.interceptor';

export const appConfig: { providers: (EnvironmentProviders)[] } = {
    providers:
        [
            provideZoneChangeDetection({eventCoalescing: true}),
            provideRouter(routes),
            provideHttpClient(
                withInterceptors([authInterceptor]),
                withInterceptorsFromDi()
            ),
        ]
};
