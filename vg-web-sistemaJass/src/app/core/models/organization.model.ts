import { RecordStatus } from './user.model';

export interface Organization {
     id: string;
     organizationName: string;
     organization_name?: string;
     name?: string;
     district: string;
     province: string;
     department: string;
     address: string;
     phone?: string;
     email?: string;
     logoUrl?: string;
     logo_url?: string;
     logo?: string;
     recordStatus: RecordStatus;
     record_status?: RecordStatus;
     createdAt: string;
     created_at?: string;
     createdBy?: string;
     created_by?: string;
     updatedAt?: string;
     updated_at?: string;
     updatedBy?: string;
     updated_by?: string;
}

export interface CreateOrganizationRequest {
     organizationName: string;
     name?: string;
     district: string;
     province: string;
     department: string;
     address: string;
     phone?: string;
     email?: string;
     logo?: string;
}

export interface UpdateOrganizationRequest {
     organizationName?: string;
     name?: string;
     district?: string;
     province?: string;
     department?: string;
     address?: string;
     phone?: string;
     email?: string;
     logo?: string;
}

export interface Zone {
     id: string;
     organizationId: string;
     zoneName: string;
     name?: string;
     description?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface Street {
     id: string;
     organizationId: string;
     zoneId: string;
     streetType: string;
     streetTypeDisplayName?: string;
     streetName: string;
     fullStreetName?: string;
     name?: string;
     description?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface Fare {
     id: string;
     organizationId: string;
     fareType: string;
     fareTypeDisplayName?: string;
     amount: number;
     description?: string;
     validFrom?: string;
     validTo?: string;
     currentlyValid?: boolean;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface Parameter {
     id: string;
     organizationId: string;
     parameterType: string;
     parameterTypeDisplayName?: string;
     parameterValue: string;
     description?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}
