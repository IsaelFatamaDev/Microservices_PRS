export interface LoginRequest {
     email: string;
     password: string;
}

export interface LoginResponse {
     accessToken: string;
     refreshToken: string;
     expiresIn: number;
     tokenType: string;
}

export interface TokenPayload {
     sub: string;
     organizationId: string;
     roles: string[];
     email: string;
     firstName: string;
     lastName: string;
     exp: number;
     iat: number;
}

export interface RefreshTokenRequest {
     refreshToken: string;
}
