import {ChatMessageResponse} from '../rest/types/responses/chat-response';

export interface ChatMessageViewModel extends ChatMessageResponse{
    isMine: boolean;
}
