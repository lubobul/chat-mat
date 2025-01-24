import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {CreateChatRequest} from '../types/requests/chat-request';
import {ChatResponse} from '../types/responses/chat-response';

@Injectable({
    providedIn: 'root',
})
export class ChatApiService {
    private readonly apiUrl = '/api/chats';

    constructor(private http: HttpClient) {}

    createChat(chatRequest: CreateChatRequest): Observable<ChatResponse>{
        return this.http.post<ChatResponse>(this.apiUrl, chatRequest);
    }
}
