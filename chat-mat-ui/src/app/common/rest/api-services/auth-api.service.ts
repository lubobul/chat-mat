import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterRequest, LoginRequest, JwtResponse, RestMessageResponse } from '../types/auth-types';

@Injectable({
    providedIn: 'root',
})
export class AuthApiService {
    private readonly apiUrl = '/api/auth';

    constructor(private http: HttpClient) {}

    register(request: RegisterRequest): Observable<RestMessageResponse> {
        return this.http.post<RestMessageResponse>(`${this.apiUrl}/register`, request);
    }

    login(request: LoginRequest): Observable<JwtResponse> {
        return this.http.post<JwtResponse>(`${this.apiUrl}/login`, request);
    }
}
