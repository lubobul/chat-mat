import {User} from './responses/user';

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface JwtResponse {
    user: User;
    token: string;
}

export interface RestMessageResponse {
    message: string;
}
