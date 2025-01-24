export interface CreateChatRequest {
    title: string;
    isChannel: boolean;
    participantIds: number[];
}
