import {Injectable} from '@angular/core';
import {AuthApiService} from '../common/rest/api-services/auth-api.service';
import {JwtResponse, LoginRequest, RegisterRequest, RestMessageResponse} from '../common/rest/types/auth-types';
import {Observable, tap} from 'rxjs';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private readonly TOKEN_KEY = 'jwt';

    constructor(
        private authApiService: AuthApiService,
        private jwtHelper: JwtHelperService
    ) {
    }

    public register(request: RegisterRequest): Observable<RestMessageResponse> {
        return this.authApiService.register(request);
    }

    public login(request: LoginRequest): Observable<JwtResponse> {
        return this.authApiService.login(request).pipe(
            tap((jwtResponse) => {
                this.storeToken(jwtResponse)
            })
        )
    }

    public getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    public isAuthenticated(): boolean {
        const token = this.getToken();

        // Check if the token exists and is not expired
        return token !== null && !this.jwtHelper.isTokenExpired(token);
    }

    private storeToken(jwtResponse: JwtResponse): void {
        localStorage.setItem(this.TOKEN_KEY, jwtResponse.token);
    }

    public clearToken(): void {
        localStorage.removeItem(this.TOKEN_KEY);
    }

}
