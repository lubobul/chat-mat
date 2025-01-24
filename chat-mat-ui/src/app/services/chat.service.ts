import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {CreateChatRequest} from '../common/rest/types/requests/chat-request';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {ChatApiService} from '../common/rest/api-services/chat-api.service';
import {QueryRequest} from '../common/rest/types/requests/query-request';
import {PaginatedResponse} from '../common/rest/types/responses/paginated-response';
import {buildQueryParams} from '../common/utils/util-functions';

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
}
