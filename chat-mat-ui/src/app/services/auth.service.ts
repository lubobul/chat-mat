import {Injectable} from '@angular/core';
import {AuthApiService} from '../common/rest/api-services/auth-api.service';
import {
    JwtResponse,
    LoginRequest,
    RegisterRequest,
    RestMessageResponse,
    UpdateProfileRequest
} from '../common/rest/types/auth-types';
import {Observable, tap} from 'rxjs';
import {User} from '../common/rest/types/responses/user';
import {ProfileApiService} from '../common/rest/api-services/profile-api.service';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private readonly USER_IDENTITY_KEY = 'USER_IDENTITY';

    constructor(
        private authApiService: AuthApiService,
        private profileApiService: ProfileApiService,
    ) {
    }

    public register(request: RegisterRequest): Observable<RestMessageResponse> {
        return this.authApiService.register(request);
    }

    public login(request: LoginRequest): Observable<JwtResponse> {
        return this.authApiService.login(request).pipe(
            tap((response) => {
                this.storeUserIdentity(response.user);
            })
        )
    }

    logout(): Observable<void> {
        return this.authApiService.logout().pipe(
            tap((response) => {
                this.clearUserIdentity();
            })
        )
    }

    updateProfile(request: UpdateProfileRequest): Observable<User> {
        return this.profileApiService.updateProfile(request).pipe(
            tap((response) => {
                this.storeUserIdentity(response);
            })
        );
    }

    deleteProfile(): Observable<void> {
        return this.profileApiService.deleteProfile().pipe(
            tap((response) => {
                this.clearUserIdentity();
            })
        );
    }

    private storeUserIdentity(userIdentity: User): void{
        localStorage.setItem(this.USER_IDENTITY_KEY, JSON.stringify(userIdentity));
    }

    private clearUserIdentity(): void{
        localStorage.clear();
    }


    public getUserIdentity(): User{
        return JSON.parse(localStorage.getItem(this.USER_IDENTITY_KEY) as string);
    }

}
