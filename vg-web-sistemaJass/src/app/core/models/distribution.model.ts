import { RecordStatus } from './user.model';

export type ProgramStatus = 'PLANNED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
export type ScheduleStatus = 'SCHEDULED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';

export interface DistributionProgram {
     id: string;
     organizationId: string;
     programName: string;
     description?: string;
     startDate: string;
     endDate?: string;
     programStatus: ProgramStatus;
     recordStatus: RecordStatus;
     createdAt: string;
}

export interface DistributionRoute {
     id: string;
     organizationId: string;
     programId: string;
     routeName: string;
     zoneId: string;
     streetIds: string[];
     routeOrder: number;
     estimatedDuration: number;
     recordStatus: RecordStatus;
     createdAt: string;
     zoneName?: string;
     programName?: string;
}

export interface DistributionSchedule {
     id: string;
     organizationId: string;
     routeId: string;
     scheduleDate: string;
     startTime: string;
     endTime: string;
     scheduleStatus: ScheduleStatus;
     executedBy?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     routeName?: string;
     zoneName?: string;
}

export interface CreateScheduleRequest {
     organizationId: string;
     routeId: string;
     scheduleDate: string;
     startTime: string;
     endTime: string;
}
