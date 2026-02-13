export interface LoginRequest {
     email: string;
     password: string;
}

export interface LoginResponse {
     access_token: string;
     refresh_token: string;
     expires_in: number;
     token_type: string;
     refresh_expires_in: number;
     user_id: string;
     organization_id: string;
     email: string;
     full_name: string;
     role: string;
     scope: string;
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
     userId: string;
}

export interface RefreshTokenRequest {
     refreshToken: string;
}
