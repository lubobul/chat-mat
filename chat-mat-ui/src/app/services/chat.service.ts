import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {CreateChatRequest} from '../common/rest/types/requests/chat-request';
import {ChatResponse} from '../common/rest/types/responses/chat-response';
import {ChatApiService} from '../common/rest/api-services/chat-api.service';

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
}
