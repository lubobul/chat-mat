import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterRequest, LoginRequest, JwtResponse, RestMessageResponse } from '../types/auth-types';
import {PaginatedResponse} from '../types/paginated-response';
import {User} from '../types/user';
import {QueryParams, QueryRequest} from '../types/query-request';
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
}
