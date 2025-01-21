import {ApplicationConfig, EnvironmentProviders, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';

export const appConfig: { providers: (EnvironmentProviders)[] } = {
    providers:
        [
            provideZoneChangeDetection({eventCoalescing: true}),
            provideRouter(routes),
            provideHttpClient(withInterceptorsFromDi()),
        ]
};
