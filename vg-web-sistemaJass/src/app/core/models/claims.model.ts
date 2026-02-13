import { RecordStatus } from './user.model';

export type ComplaintStatus = 'PENDING' | 'IN_PROGRESS' | 'RESOLVED' | 'REJECTED';
export type IncidentStatus = 'REPORTED' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type IncidentPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface ComplaintCategory {
     id: string;
     organizationId: string;
     categoryName: string;
     description?: string;
     recordStatus: RecordStatus;
     createdAt: string;
}

export interface Complaint {
     id: string;
     organizationId: string;
     userId: string;
     categoryId: string;
     complaintNumber: string;
     subject: string;
     description: string;
     complaintStatus: ComplaintStatus;
     resolution?: string;
     resolvedBy?: string;
     resolvedAt?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     userName?: string;
     categoryName?: string;
}

export interface CreateComplaintRequest {
     organizationId: string;
     userId: string;
     categoryId: string;
     subject: string;
     description: string;
}

export interface IncidentType {
     id: string;
     organizationId: string;
     typeName: string;
     description?: string;
     defaultPriority: IncidentPriority;
     recordStatus: RecordStatus;
     createdAt: string;
}

export interface Incident {
     id: string;
     organizationId: string;
     incidentTypeId: string;
     incidentNumber: string;
     title: string;
     description: string;
     location: string;
     zoneId?: string;
     streetId?: string;
     priority: IncidentPriority;
     incidentStatus: IncidentStatus;
     reportedBy: string;
     assignedTo?: string;
     resolvedAt?: string;
     resolution?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     typeName?: string;
     zoneName?: string;
     streetName?: string;
}

export interface CreateIncidentRequest {
     organizationId: string;
     incidentTypeId: string;
     title: string;
     description: string;
     location: string;
     zoneId?: string;
     streetId?: string;
     priority: IncidentPriority;
}
