export type Role = 'SUPER_ADMIN' | 'ADMIN' | 'OPERATOR' | 'CLIENT';
export type RecordStatus = 'ACTIVE' | 'INACTIVE';
export type DocumentType = 'DNI' | 'CNE';

export interface User {
     id: string;
     organizationId: string;
     firstName: string;
     lastName: string;
     fullName?: string;
     documentType: DocumentType;
     documentNumber: string;
     dni?: string;
     email?: string;
     phone?: string;
     address?: string;
     zoneId?: string;
     streetId?: string;
     role: Role;
     roleDisplayName?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface CreateUserRequest {
     organizationId: string;
     firstName: string;
     lastName: string;
     documentType: DocumentType;
     documentNumber: string;
     email?: string;
     phone?: string;
     address?: string;
     zoneId?: string;
     streetId?: string;
     role: Role;
}

export interface UpdateUserRequest {
     firstName?: string;
     lastName?: string;
     email?: string;
     phone?: string;
     address?: string;
}
