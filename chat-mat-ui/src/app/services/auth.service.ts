import {Injectable} from '@angular/core';
import {AuthApiService} from '../common/rest/api-services/auth-api.service';
import {JwtResponse, LoginRequest, RegisterRequest, RestMessageResponse} from '../common/rest/types/auth-types';
import {Observable, tap} from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    constructor(private authApiService: AuthApiService) {
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
        return localStorage.getItem('jwt');
    }

    private storeToken(jwtResponse: JwtResponse): void {
        localStorage.setItem('jwt', jwtResponse.token);
    }

    private clearToken(): void {
        localStorage.removeItem('jwt');
    }
}
