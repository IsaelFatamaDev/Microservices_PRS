import { RecordStatus } from './user.model';

export type BoxType = 'RESIDENTIAL' | 'COMMERCIAL' | 'COMMUNAL' | 'INSTITUTIONAL';
export type AssignmentStatus = 'ACTIVE' | 'INACTIVE' | 'TRANSFERRED';

export interface WaterBox {
     id: string;
     organizationId: string;
     boxCode: string;
     boxType: BoxType;
     zoneId: string;
     streetId: string;
     address: string;
     currentAssignmentId?: string;
     isActive: boolean;
     installationDate: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface CreateWaterBoxRequest {
     organizationId: string;
     boxCode: string;
     boxType: BoxType;
     installationDate?: string;
     zoneId?: string;
     streetId?: string;
     address?: string;
}

export interface UpdateWaterBoxRequest {
     boxCode?: string;
     boxType?: string;
     installationDate?: string;
     zoneId?: string;
     streetId?: string;
     address?: string;
}

export interface AssignWaterBoxRequest {
     waterBoxId: string;
     userId: string;
}

export interface WaterBoxAssignment {
     id: string;
     organizationId: string;
     waterBoxId: string;
     userId: string;
     assignmentDate: string;
     assignmentStatus: AssignmentStatus;
     endDate?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface WaterBoxTransfer {
     id: string;
     organizationId: string;
     waterBoxId: string;
     fromUserId: string;
     toUserId: string;
     transferDate: string;
     transferFee: number;
     notes?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
}

export interface TransferWaterBoxRequest {
     waterBoxId: string;
     fromUserId: string;
     toUserId: string;
     transferFee?: number;
     notes?: string;
}
