import { RecordStatus } from './user.model';

export interface Organization {
     id: string;
     organizationName: string;
     name?: string;
     ruc?: string;
     district: string;
     province: string;
     department: string;
     address: string;
     phone?: string;
     email?: string;
     logoUrl?: string;
     logo?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface CreateOrganizationRequest {
     organizationName: string;
     name?: string;
     ruc?: string;
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
     ruc?: string;
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
}

export interface Street {
     id: string;
     organizationId: string;
     zoneId: string;
     streetName: string;
     name?: string;
     description?: string;
     recordStatus: RecordStatus;
     createdAt: string;
}

export interface Fare {
     id: string;
     organizationId: string;
     zoneId?: string;
     fareName: string;
     name?: string;
     amount: number;
     minCubicMeters?: number;
     maxCubicMeters?: number;
     pricePerCubicMeter?: number;
     basePrice?: number;
     description?: string;
     recordStatus: RecordStatus;
     createdAt: string;
}

export interface Parameter {
     id: string;
     organizationId: string;
     parameterKey: string;
     key?: string;
     name?: string;
     parameterValue: string;
     value?: string;
     description?: string;
     recordStatus: RecordStatus;
     createdAt: string;
}
