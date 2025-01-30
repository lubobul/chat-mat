import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {
    AdminRightsRequest,
    ChatMessageRequest,
    CreateChatRequest,
    ParticipantsUpdateRequest, UpdateChatRequest
} from '../types/requests/chat-request';
import {ChatMessageResponse, ChatResponse} from '../types/responses/chat-response';
import {QueryParams, QueryRequest} from '../types/requests/query-request';
import {PaginatedResponse} from '../types/responses/paginated-response';
import {UserChatRightsResponse, UserResponse} from '../types/responses/user-response';
import {buildQueryParams} from '../../utils/util-functions';

@Injectable({
    providedIn: 'root',
})
export class ChatMessageApiService {
    private readonly apiUrl = '/api/messages';

    constructor(private http: HttpClient) {
    }

    deleteChatMessage(chatMessageId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${chatMessageId}`);
    }

}
