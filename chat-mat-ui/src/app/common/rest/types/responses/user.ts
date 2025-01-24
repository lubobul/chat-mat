export interface User {
    id: number;
    username: string;
    email: string;
    createdAt: string;
    avatar?: string;
    friendOfYours?: boolean;
}
