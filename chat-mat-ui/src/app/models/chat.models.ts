export interface UserInput{
    question: string;
}

export interface ChatResponse{
    message: string;
    items?: string[];
}

export interface ChatHistoryMessage{
    messageType: ChatHistoryMessageType;
    message: string;
    //this is omitted if messageType === UserMessage
    items?: string[];
    id: number;
    isWarning: boolean;
}

export enum ChatHistoryMessageType{
    UserMessage = "UserMessage",
    BotMessage = "BotMessage",
}
