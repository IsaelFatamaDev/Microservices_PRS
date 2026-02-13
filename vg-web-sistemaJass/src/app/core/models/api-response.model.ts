export interface ApiResponse<T> {
     success: boolean;
     message: string;
     data: T;
     timestamp: string;
}

export interface PageResponse<T> {
     content: T[];
     totalElements: number;
     totalPages: number;
     size: number;
     number: number;
     first: boolean;
     last: boolean;
}

export interface ErrorResponse {
     success: boolean;
     message: string;
     errors?: string[];
     timestamp: string;
     path?: string;
}
