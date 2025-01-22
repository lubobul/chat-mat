import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import { inject } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {catchError, throwError} from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const token = authService.getToken();

    if (token) {
        const clonedRequest = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`,
            },
        });
        return next(clonedRequest);
    }

    return next(req);
};

export const loginRedirectInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                // Clear JWT and redirect to login on authentication failure
                authService.clearToken();
                router.navigate(['/login']);
            } else if (error.status === 403) {
                // Optional: Show a forbidden message or handle gracefully
                console.error('Access denied. You do not have permission to perform this action.');
            }
            return throwError(() => error);
        })
    );
};
