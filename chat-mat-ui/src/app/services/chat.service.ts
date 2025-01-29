import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ChatMessageRequest, CreateChatRequest} from '../common/rest/types/requests/chat-request';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {ChatApiService} from '../common/rest/api-services/chat-api.service';
import {QueryParams, QueryRequest} from '../common/rest/types/requests/query-request';
import {PaginatedResponse} from '../common/rest/types/responses/paginated-response';
import {buildQueryParams} from '../common/utils/util-functions';
import {UserResponse} from '../common/rest/types/responses/userResponse';

@Injectable({
    providedIn: 'root',
})
export class ChatService {

    constructor(
        private chatApiService: ChatApiService,
    ) {
    }

    createChat(chatRequest: CreateChatRequest): Observable<ChatResponse>{
        return this.chatApiService.createChat(chatRequest);
    }

    getDirectChats(queryRequest: QueryRequest): Observable<PaginatedResponse<ChatResponse>> {
        const params = buildQueryParams(queryRequest) as any;
        return this.chatApiService.getChats(params);
    }

    getChannelChats(queryRequest: QueryRequest): Observable<PaginatedResponse<ChatResponse>> {
        const params = buildQueryParams(queryRequest) as any;
        params.channel = true;
        return this.chatApiService.getChats(params);
    }

    getChat(chatId: number): Observable<ChatResponse> {
        return this.chatApiService.getChat(chatId);
    }

    sendMessage(message: ChatMessageRequest, chatId: number): Observable<ChatMessageRequest> {
        return this.chatApiService.sendMessage(message, chatId);
    }

    getParticipants(chatId: number, queryRequest: QueryRequest): Observable<PaginatedResponse<UserResponse>> {
        const params = buildQueryParams(queryRequest) as any;
        return this.chatApiService.getParticipants(chatId, params);
    }
}
