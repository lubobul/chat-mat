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
    token: string;
}

export interface RestMessageResponse {
    message: string;
}
