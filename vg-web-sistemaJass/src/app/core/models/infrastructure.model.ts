import { RecordStatus } from './user.model';

export type BoxType = 'RESIDENTIAL' | 'COMMERCIAL' | 'COMMUNAL' | 'INSTITUTIONAL';
export type BoxStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'TRANSFERRED';

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
     updatedAt?: string;
     // display helpers
     userName?: string;
     zoneName?: string;
     streetName?: string;
}

export interface CreateWaterBoxRequest {
     organizationId: string;
     boxCode: string;
     boxType: BoxType;
     installationDate: string;
     zoneId: string;
     streetId: string;
     address: string;
}

export interface AssignWaterBoxRequest {
     waterBoxId: string;
     userId: string;
}

export interface WaterBoxAssignment {
     id: string;
     waterBoxId: string;
     userId: string;
     assignmentDate: string;
     assignedBy: string;
     recordStatus: RecordStatus;
}

export interface WaterBoxTransfer {
     id: string;
     waterBoxId: string;
     fromUserId: string;
     toUserId: string;
     transferDate: string;
     transferReason: string;
     transferFee: number;
     processedBy: string;
     recordStatus: RecordStatus;
     createdAt: string;
}

export interface CreateTransferRequest {
     waterBoxId: string;
     fromUserId: string;
     toUserId: string;
     transferReason: string;
}
