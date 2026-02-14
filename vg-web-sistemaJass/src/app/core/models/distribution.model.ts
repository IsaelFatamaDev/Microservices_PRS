import { RecordStatus } from './user.model';

// ==================== DISTRIBUTION PROGRAMS ====================
export interface DistributionProgram {
     id: string;
     organizationId: string;
     scheduleId: string;
     routeId: string;
     zoneId: string;
     streetId?: string;
     programDate: string;
     plannedStartTime: string;
     plannedEndTime: string;
     actualStartTime?: string;
     actualEndTime?: string;
     responsibleUserId: string;
     observations?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface CreateProgramRequest {
     organizationId: string;
     scheduleId: string;
     routeId: string;
     zoneId: string;
     streetId?: string;
     programDate: string;
     plannedStartTime: string;
     plannedEndTime: string;
     actualStartTime?: string;
     actualEndTime?: string;
     responsibleUserId: string;
     observations?: string;
}

export interface UpdateProgramRequest {
     organizationId?: string;
     scheduleId?: string;
     routeId?: string;
     zoneId?: string;
     streetId?: string;
     programDate?: string;
     plannedStartTime?: string;
     plannedEndTime?: string;
     actualStartTime?: string;
     actualEndTime?: string;
     responsibleUserId?: string;
     observations?: string;
}

// ==================== DISTRIBUTION ROUTES ====================
export interface ZoneOrder {
     zoneId: string;
     order: number;
     estimatedDuration: number;
}

export interface DistributionRoute {
     id: string;
     organizationId: string;
     routeName: string;
     zones: ZoneOrder[];
     totalEstimatedDuration: number;
     responsibleUserId: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface CreateRouteRequest {
     organizationId: string;
     routeName: string;
     zones: ZoneOrder[];
     totalEstimatedDuration: number;
     responsibleUserId: string;
}

export interface UpdateRouteRequest {
     routeName?: string;
     zones?: ZoneOrder[];
     totalEstimatedDuration?: number;
     responsibleUserId?: string;
}

// ==================== DISTRIBUTION SCHEDULES ====================
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export interface DistributionSchedule {
     id: string;
     organizationId: string;
     zoneId: string;
     streetId?: string;
     scheduleName: string;
     daysOfWeek: string[];
     startTime: string;
     endTime: string;
     durationHours?: number;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface CreateScheduleRequest {
     organizationId: string;
     zoneId: string;
     streetId?: string;
     scheduleName: string;
     daysOfWeek?: string[];
     startTime: string;
     endTime: string;
     durationHours?: number;
}

export interface UpdateScheduleRequest {
     zoneId?: string;
     streetId?: string;
     scheduleName?: string;
     daysOfWeek?: string[];
     startTime?: string;
     endTime?: string;
     durationHours?: number;
}
