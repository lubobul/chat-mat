export interface CreateChatRequest {
    title: string;
    isChannel: boolean;
    participantIds: number[];
}

export interface ChatMessageRequest{
    messageContent: string;
}
