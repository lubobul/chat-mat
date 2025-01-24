import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {CreateChatRequest} from '../types/requests/chat-request';
import {ChatResponse} from '../types/responses/chat-response';
import {QueryParams, QueryRequest} from '../types/requests/query-request';
import {PaginatedResponse} from '../types/responses/paginated-response';
import {User} from '../types/responses/user';
import {buildQueryParams} from '../../utils/util-functions';

@Injectable({
    providedIn: 'root',
})
export class ChatApiService {
    private readonly apiUrl = '/api/chats';

    constructor(private http: HttpClient) {}

    createChat(chatRequest: CreateChatRequest): Observable<ChatResponse>{
        return this.http.post<ChatResponse>(this.apiUrl, chatRequest);
    }

    getChats(params: QueryParams | any): Observable<PaginatedResponse<ChatResponse>> {
        return this.http.get<PaginatedResponse<ChatResponse>>(this.apiUrl, { params });
    }
}
