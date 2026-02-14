import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import {
    TestingPoint, CreateTestingPointRequest, UpdateTestingPointRequest,
    QualityTest, CreateQualityTestRequest, UpdateQualityTestRequest
} from '../models/water-quality.model';

@Injectable({ providedIn: 'root' })
export class WaterQualityService {
    private http = inject(HttpClient);
    private base = environment.apiUrl;

    // ============================================================================
    // TESTING POINTS - PUNTOS DE MUESTREO
    // ============================================================================

    createTestingPoint(req: CreateTestingPointRequest): Observable<ApiResponse<TestingPoint>> {
        return this.http.post<ApiResponse<TestingPoint>>(`${this.base}/testing-points`, req);
    }

    getTestingPoint(id: string): Observable<ApiResponse<TestingPoint>> {
        return this.http.get<ApiResponse<TestingPoint>>(`${this.base}/testing-points/${id}`);
    }

    getTestingPoints(): Observable<ApiResponse<TestingPoint[]>> {
        return this.http.get<ApiResponse<TestingPoint[]>>(`${this.base}/testing-points`);
    }

    getActiveTestingPoints(): Observable<ApiResponse<TestingPoint[]>> {
        return this.http.get<ApiResponse<TestingPoint[]>>(`${this.base}/testing-points/active`);
    }

    getTestingPointsByOrg(orgId: string): Observable<ApiResponse<TestingPoint[]>> {
        return this.http.get<ApiResponse<TestingPoint[]>>(`${this.base}/testing-points/organization/${orgId}`);
    }

    getActiveTestingPointsByOrg(orgId: string): Observable<ApiResponse<TestingPoint[]>> {
        return this.http.get<ApiResponse<TestingPoint[]>>(`${this.base}/testing-points/organization/${orgId}/active`);
    }

    updateTestingPoint(id: string, req: UpdateTestingPointRequest): Observable<ApiResponse<TestingPoint>> {
        return this.http.put<ApiResponse<TestingPoint>>(`${this.base}/testing-points/${id}`, req);
    }

    deleteTestingPoint(id: string): Observable<ApiResponse<TestingPoint>> {
        return this.http.delete<ApiResponse<TestingPoint>>(`${this.base}/testing-points/${id}`);
    }

    restoreTestingPoint(id: string): Observable<ApiResponse<TestingPoint>> {
        return this.http.patch<ApiResponse<TestingPoint>>(`${this.base}/testing-points/${id}/restore`, {});
    }

    // ============================================================================
    // QUALITY TESTS - PRUEBAS DE CALIDAD
    // ============================================================================

    createQualityTest(req: CreateQualityTestRequest): Observable<ApiResponse<QualityTest>> {
        return this.http.post<ApiResponse<QualityTest>>(`${this.base}/quality-tests`, req);
    }

    getQualityTest(id: string): Observable<ApiResponse<QualityTest>> {
        return this.http.get<ApiResponse<QualityTest>>(`${this.base}/quality-tests/${id}`);
    }

    getQualityTests(): Observable<ApiResponse<QualityTest[]>> {
        return this.http.get<ApiResponse<QualityTest[]>>(`${this.base}/quality-tests`);
    }

    getActiveQualityTests(): Observable<ApiResponse<QualityTest[]>> {
        return this.http.get<ApiResponse<QualityTest[]>>(`${this.base}/quality-tests/active`);
    }

    getQualityTestsByOrg(orgId: string): Observable<ApiResponse<QualityTest[]>> {
        return this.http.get<ApiResponse<QualityTest[]>>(`${this.base}/quality-tests/organization/${orgId}`);
    }

    getActiveQualityTestsByOrg(orgId: string): Observable<ApiResponse<QualityTest[]>> {
        return this.http.get<ApiResponse<QualityTest[]>>(`${this.base}/quality-tests/organization/${orgId}/active`);
    }

    updateQualityTest(id: string, req: UpdateQualityTestRequest): Observable<ApiResponse<QualityTest>> {
        return this.http.put<ApiResponse<QualityTest>>(`${this.base}/quality-tests/${id}`, req);
    }

    deleteQualityTest(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.base}/quality-tests/${id}`);
    }

    restoreQualityTest(id: string): Observable<ApiResponse<QualityTest>> {
        return this.http.patch<ApiResponse<QualityTest>>(`${this.base}/quality-tests/${id}/restore`, {});
    }
}
