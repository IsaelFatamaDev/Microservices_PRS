import { RecordStatus } from './user.model';

export type BoxType = 'RESIDENTIAL' | 'COMMERCIAL' | 'INDUSTRIAL' | 'INSTITUTIONAL';
export type BoxStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'TRANSFERRED';

export interface WaterBox {
     id: string;
     organizationId: string;
     userId: string;
     supplyNumber: string;
     code?: string;
     boxType: BoxType;
     boxStatus: BoxStatus;
     zoneId: string;
     streetId: string;
     address: string;
     description?: string;
     installationDate: string;
     installedBy: string;
     recordStatus: RecordStatus;
     createdAt: string;
     userName?: string;
     zoneName?: string;
     streetName?: string;
}

export interface CreateWaterBoxRequest {
     organizationId: string;
     userId: string;
     boxType: BoxType;
     zoneId: string;
     streetId: string;
     address: string;
     installedBy: string;
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
