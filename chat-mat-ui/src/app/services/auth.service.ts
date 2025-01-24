import {Injectable} from '@angular/core';
import {AuthApiService} from '../common/rest/api-services/auth-api.service';
import {JwtResponse, LoginRequest, RegisterRequest, RestMessageResponse} from '../common/rest/types/auth-types';
import {Observable, tap} from 'rxjs';
import {User} from '../common/rest/types/responses/user';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private readonly USER_IDENTITY_KEY = 'USER_IDENTITY';

    constructor(
        private authApiService: AuthApiService,
    ) {
    }

    public register(request: RegisterRequest): Observable<RestMessageResponse> {
        return this.authApiService.register(request);
    }

    public login(request: LoginRequest): Observable<JwtResponse> {
        return this.authApiService.login(request).pipe(
            tap((response) => {
                this.storeUserIdentity(response);
            })
        )
    }

    private storeUserIdentity(userIdentity: JwtResponse): void{
        localStorage.setItem(this.USER_IDENTITY_KEY, JSON.stringify(userIdentity));
    }

    public getUserIdentity(): User{
        return (JSON.parse(localStorage.getItem(this.USER_IDENTITY_KEY) as string) as JwtResponse).user;
    }
}
