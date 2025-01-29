import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ChatMessageRequest, CreateChatRequest} from '../types/requests/chat-request';
import {ChatMessageResponse, ChatResponse} from '../types/responses/chat-response';
import {QueryParams, QueryRequest} from '../types/requests/query-request';
import {PaginatedResponse} from '../types/responses/paginated-response';
import {UserResponse} from '../types/responses/userResponse';
import {buildQueryParams} from '../../utils/util-functions';

@Injectable({
    providedIn: 'root',
})
export class ChatApiService {
    private readonly apiUrl = '/api/chats';

    constructor(private http: HttpClient) {
    }

    createChat(chatRequest: CreateChatRequest): Observable<ChatResponse> {
        return this.http.post<ChatResponse>(this.apiUrl, chatRequest);
    }

    getChats(params: QueryParams | any): Observable<PaginatedResponse<ChatResponse>> {
        return this.http.get<PaginatedResponse<ChatResponse>>(this.apiUrl, {params});
    }

    getChat(chatId: number): Observable<ChatResponse> {
        return this.http.get<ChatResponse>(`${this.apiUrl}/${chatId}`);
    }

    sendMessage(message: ChatMessageRequest, chatId: number): Observable<ChatMessageResponse> {
        return this.http.post<ChatMessageResponse>(`${this.apiUrl}/${chatId}/sendMessage`, message);
    }

    getParticipants(chatId: number, params: QueryParams | any): Observable<PaginatedResponse<UserResponse>> {
        return this.http.get<PaginatedResponse<UserResponse>>(`${this.apiUrl}/${chatId}/participants`, {params});
    }
}
