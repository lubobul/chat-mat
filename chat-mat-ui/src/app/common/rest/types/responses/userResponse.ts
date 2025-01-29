export interface UserResponse {
    id: number;
    username: string;
    email: string;
    createdAt: string;
    avatar?: string;
    isFriendOfYours?: boolean;
}
