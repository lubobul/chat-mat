import {UserResponse} from './user-response';
import {PaginatedResponse} from './paginated-response';

export interface ChatResponse {
    id: number;
    title: string;
    isChannel: boolean;
    createdAt: string;
    owner: UserResponse;
    participantsPage: PaginatedResponse<UserResponse>;
    messagesPage: PaginatedResponse<ChatMessageResponse>;
}

export interface ChatMessageResponse{
    id: number;
    messageContent: string;
    senderUsername: string;
    senderId: number;
    senderAvatar: string;
    createdAt: string;
}
