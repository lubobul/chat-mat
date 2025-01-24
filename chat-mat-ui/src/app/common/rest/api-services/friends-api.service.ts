import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterRequest, LoginRequest, JwtResponse, RestMessageResponse } from '../types/auth-types';
import {PaginatedResponse} from '../types/responses/paginated-response';
import {User} from '../types/responses/user';
import {QueryRequest} from '../types/requests/query-request';
import {buildQueryParams} from '../../utils/util-functions';

@Injectable({
    providedIn: 'root',
})
export class FriendsApiService {
    private readonly apiUrl = '/api/friends';

    constructor(private http: HttpClient) {}

    getFriends(queryRequest: QueryRequest): Observable<PaginatedResponse<User>> {
        const params = buildQueryParams(queryRequest) as any;
        return this.http.get<PaginatedResponse<User>>(this.apiUrl, { params });
    }

    addFriend(user: User): Observable<void>{
        return this.http.post<void>(`${this.apiUrl}/${user.id}`, null);
    }

    removeFriend(user: User): Observable<void>{
        return this.http.delete<void>(`${this.apiUrl}/${user.id}`);
    }
}
