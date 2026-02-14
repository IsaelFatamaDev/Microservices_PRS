import { RecordStatus } from './user.model';

// ============================================================================
// TESTING POINTS - PUNTOS DE MUESTREO
// ============================================================================

export interface TestingPoint {
    id: string;
    organizationId: string;
    zoneId: string;
    pointName: string;
    pointType: string;
    location: string;
    latitude: string;
    longitude: string;
    recordStatus: RecordStatus;
    createdAt: string;
    updatedAt?: string;
    createdBy: string;
    updatedBy?: string;
}

export interface CreateTestingPointRequest {
    organizationId: string;
    zoneId?: string;
    pointName: string;
    pointType: string;
    location?: string;
    latitude?: string;
    longitude?: string;
}

export interface UpdateTestingPointRequest {
    organizationId?: string;
    zoneId?: string;
    pointName?: string;
    pointType?: string;
    location?: string;
    latitude?: string;
    longitude?: string;
}

// ============================================================================
// QUALITY TESTS - PRUEBAS DE CALIDAD
// ============================================================================

export interface QualityTest {
    id: string;
    organizationId: string;
    recordStatus: RecordStatus;
    testingPointId: string;
    testDate: string;
    testType: string;
    chlorineLevel: number;
    phLevel: number;
    turbidityLevel: number;
    testResult: string;
    testedByUserId: string;
    observations: string;
    treatmentApplied: boolean;
    treatmentDescription: string;
    createdAt: string;
    createdBy: string;
    updatedAt?: string;
    updatedBy?: string;
}

export interface CreateQualityTestRequest {
    organizationId: string;
    testingPointId: string;
    testType: string;
    testDate?: string;
    chlorineLevel?: number;
    phLevel?: number;
    turbidityLevel?: number;
    testResult: string;
    testedByUserId?: string;
    observations?: string;
    treatmentApplied?: boolean;
    treatmentDescription?: string;
}

export interface UpdateQualityTestRequest {
    organizationId?: string;
    testingPointId?: string;
    testType?: string;
    testDate?: string;
    chlorineLevel?: number;
    phLevel?: number;
    turbidityLevel?: number;
    testResult?: string;
    testedByUserId?: string;
    observations?: string;
    treatmentApplied?: boolean;
    treatmentDescription?: string;
}
