export interface CreateChatRequest {
    title: string;
    isChannel: boolean;
    participantIds: number[];
}

export interface ChatMessageRequest{
    messageContent: string;
}

export interface ParticipantsUpdateRequest {
    addedParticipants: number[];
    removedParticipants: number[];
}

export interface AdminRightsRequest{
    isAdmin: boolean;
}

