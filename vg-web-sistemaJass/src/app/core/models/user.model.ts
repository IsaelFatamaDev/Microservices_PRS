export type Role = 'SUPER_ADMIN' | 'ADMIN' | 'OPERATOR' | 'CLIENT';
export type RecordStatus = 'ACTIVE' | 'INACTIVE';
export type DocumentType = 'DNI' | 'CE' | 'PASSPORT';

export interface User {
     id: string;
     organizationId: string;
     organization_id?: string;
     firstName: string;
     lastName: string;
     documentType: DocumentType;
     documentNumber: string;
     dni?: string;
     email?: string;
     phone?: string;
     address?: string;
     zoneId?: string;
     streetId?: string;
     role: Role;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface CreateUserRequest {
     organizationId?: string;
     firstName: string;
     lastName: string;
     documentType?: DocumentType;
     documentNumber?: string;
     dni?: string;
     email?: string;
     phone?: string;
     address?: string;
     zoneId?: string;
     streetId?: string;
     role?: Role;
}

export interface UpdateUserRequest {
     firstName?: string;
     lastName?: string;
     email?: string;
     phone?: string;
     address?: string;
     zoneId?: string;
     streetId?: string;
}
