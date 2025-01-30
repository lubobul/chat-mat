import {Injectable} from '@angular/core';
import {catchError, Observable, tap, throwError} from 'rxjs';
import {
    AdminRightsRequest,
    ChatMessageRequest,
    CreateChatRequest,
    ParticipantsUpdateRequest, UpdateChatRequest
} from '../common/rest/types/requests/chat-request';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {ChatApiService} from '../common/rest/api-services/chat-api.service';
import {QueryParams, QueryRequest} from '../common/rest/types/requests/query-request';
import {PaginatedResponse} from '../common/rest/types/responses/paginated-response';
import {buildQueryParams, resolveErrorMessage} from '../common/utils/util-functions';
import {UserChatRightsResponse, UserResponse} from '../common/rest/types/responses/user-response';
import {ChatMessageViewModel} from '../common/view-models/chat-message-view-models';
import {ChatMessageApiService} from '../common/rest/api-services/chat-message-api.service';

@Injectable({
    providedIn: 'root',
})
export class ChatService {

    constructor(
        private chatApiService: ChatApiService,
        private chatMessageApiService: ChatMessageApiService,
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

    getFriendsNotPartOfChat(chatId: number, queryRequest: QueryRequest): Observable<PaginatedResponse<UserResponse>> {
        const params = buildQueryParams(queryRequest) as any;
        return this.chatApiService.getFriendsNotPartOfChat(chatId, params);
    }

    updateParticipants(chatId: number, payload: ParticipantsUpdateRequest): Observable<ChatResponse> {
        return this.chatApiService.updateParticipants(chatId, payload);
    }

    getParticipantRights(chatId: number, userId: number): Observable<UserChatRightsResponse> {
        return this.chatApiService.getParticipantRights(chatId, userId);
    }

    updateAdminRights(chatId: number, userId: number, payload: AdminRightsRequest): Observable<void> {
        return this.chatApiService.updateAdminRights(chatId, userId, payload);
    }

    updateChat(chatId: number, payload: UpdateChatRequest): Observable<void> {
        return this.chatApiService.updateChat(chatId, payload);
    }

    deleteChat(chatId: number): Observable<void> {
        return this.chatApiService.deleteChat(chatId);
    }

    leaveChat(chatId: number): Observable<void> {
        return this.chatApiService.leaveChat(chatId);
    }

    deleteChatMessage(chatMessageId: number): Observable<void> {
        return this.chatMessageApiService.deleteChatMessage(chatMessageId);
    }
}
