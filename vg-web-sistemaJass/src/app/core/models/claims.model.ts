import { RecordStatus } from './user.model';

// ============================================================================
// ENUMS Y TIPOS
// ============================================================================

export type IncidentStatus = 'REPORTED' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type IncidentSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type ComplaintStatus = 'RECEIVED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type ComplaintPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
export type ResolutionType = 'REPAIR' | 'REPLACEMENT' | 'ADJUSTMENT' | 'OTHER';
export type ResponseType = 'OFFICIAL' | 'INTERNAL';
export type PriorityLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' | 'URGENT';

// ============================================================================
// INCIDENTS - INCIDENCIAS TÉCNICAS
// ============================================================================

export interface Incident {
     id: string;
     organizationId: string;
     incidentCode: string;
     incidentTypeId: string;
     incidentCategory: string;
     zoneId: string;
     incidentDate: string;
     title: string;
     description: string;
     severity: IncidentSeverity;
     status: IncidentStatus;
     affectedBoxesCount: number;
     reportedByUserId: string;
     assignedToUserId?: string;
     resolvedByUserId?: string;
     resolved: boolean;
     resolutionNotes?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     updatedAt?: string;
}

export interface CreateIncidentRequest {
     organizationId: string;
     incidentTypeId: string;
     incidentCategory: string;
     zoneId: string;
     title: string;
     description: string;
     severity: string;
     affectedBoxesCount?: number;
}

export interface UpdateIncidentRequest {
     title?: string;
     description?: string;
     severity?: string;
     status?: string;
     affectedBoxesCount?: number;
     resolutionNotes?: string;
}

export interface AssignIncidentRequest {
     userId: string;
     notes?: string;
}

export interface ResolveIncidentRequest {
     resolutionType: string;
     actionsTaken: string;
     materialsUsed?: MaterialUsed[];
     laborHours: number;
     totalCost: number;
     qualityCheck: boolean;
     followUpRequired: boolean;
     resolutionNotes?: string;
}

export interface MaterialUsed {
     productId: string;
     quantity: number;
     unit: string;
     unitCost: number;
}

// ============================================================================
// INCIDENT TYPES - TIPOS DE INCIDENCIA
// ============================================================================

export interface IncidentType {
     id: string;
     organizationId: string;
     typeCode: string;
     typeName: string;
     description: string;
     priorityLevel: PriorityLevel;
     estimatedResolutionTime: number; // horas
     requiresExternalService: boolean;
     recordStatus: RecordStatus;
     createdAt: string;
     updatedAt?: string;
}

export interface CreateIncidentTypeRequest {
     typeCode: string;
     typeName: string;
     description: string;
     priorityLevel: string;
     estimatedResolutionTime: number;
     requiresExternalService: boolean;
     organizationId: string;
}

export interface UpdateIncidentTypeRequest {
     typeName?: string;
     description?: string;
     priorityLevel?: string;
     estimatedResolutionTime?: number;
     requiresExternalService?: boolean;
}

// ============================================================================
// INCIDENT RESOLUTIONS - RESOLUCIONES DE INCIDENCIAS
// ============================================================================

export interface IncidentResolution {
     id: string;
     incidentId: string;
     resolutionType: ResolutionType;
     actionsTaken: string;
     materialsUsed?: MaterialUsed[];
     laborHours: number;
     totalCost: number;
     qualityCheck: boolean;
     followUpRequired: boolean;
     resolutionNotes?: string;
     resolvedBy: string;
     resolvedAt: string;
     createdAt: string;
}

// ============================================================================
// COMPLAINTS - QUEJAS
// ============================================================================

export interface Complaint {
     id: string;
     organizationId: string;
     complaintCode: string;
     userId: string;
     categoryId: string;
     waterBoxId?: string;
     complaintDate: string;
     subject: string;
     description: string;
     priority: ComplaintPriority;
     status: ComplaintStatus;
     assignedToUserId?: string;
     expectedResolutionDate?: string;
     actualResolutionDate?: string;
     satisfactionRating?: number;
     recordStatus: RecordStatus;
     createdAt: string;
     updatedAt?: string;
}

export interface CreateComplaintRequest {
     subject: string;
     description: string;
     categoryId: string;
     waterBoxId?: string;
     priority: string;
     organizationId: string;
}

export interface UpdateComplaintRequest {
     subject?: string;
     description?: string;
     priority?: string;
     status?: string;
     assignedToUserId?: string;
     expectedResolutionDate?: string;
}

// ============================================================================
// COMPLAINT CATEGORIES - CATEGORÍAS DE QUEJAS
// ============================================================================

export interface ComplaintCategory {
     id: string;
     organizationId: string;
     categoryCode: string;
     categoryName: string;
     description: string;
     priorityLevel: PriorityLevel;
     maxResponseTime: number; // horas
     recordStatus: RecordStatus;
     createdAt: string;
     updatedAt?: string;
}

export interface CreateComplaintCategoryRequest {
     categoryCode: string;
     categoryName: string;
     description: string;
     priorityLevel: string;
     maxResponseTime: number;
     organizationId: string;
}

export interface UpdateComplaintCategoryRequest {
     categoryName?: string;
     description?: string;
     priorityLevel?: string;
     maxResponseTime?: number;
}

// ============================================================================
// COMPLAINT RESPONSES - RESPUESTAS A QUEJAS
// ============================================================================

export interface ComplaintResponse {
     id: string;
     complaintId: string;
     responseType: ResponseType;
     message: string;
     internalNotes?: string;
     respondedBy: string;
     respondedAt: string;
     createdAt: string;
}

export interface AddComplaintResponseRequest {
     responseType: string;
     message: string;
     internalNotes?: string;
}

