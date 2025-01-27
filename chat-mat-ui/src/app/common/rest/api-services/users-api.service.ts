import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterRequest, LoginRequest, JwtResponse, RestMessageResponse } from '../types/auth-types';
import {PaginatedResponse} from '../types/responses/paginated-response';
import {User} from '../types/responses/user';
import {QueryParams, QueryRequest} from '../types/requests/query-request';
import {buildQueryParams} from '../../utils/util-functions';

@Injectable({
    providedIn: 'root',
})
export class UsersApiService {
    private readonly apiUrl = '/api/users';

    constructor(private http: HttpClient) {}

    getUsers(params: QueryParams | any): Observable<PaginatedResponse<User>> {
        return this.http.get<PaginatedResponse<User>>(this.apiUrl, { params });
    }

    getUser(userId: number): Observable<User> {
        return this.http.get<User>(`${this.apiUrl}/${userId}`);
    }
}
