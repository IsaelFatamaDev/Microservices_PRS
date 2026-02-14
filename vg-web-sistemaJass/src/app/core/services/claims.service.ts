import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import {
    Incident, CreateIncidentRequest, UpdateIncidentRequest, AssignIncidentRequest, ResolveIncidentRequest,
    IncidentType, CreateIncidentTypeRequest, UpdateIncidentTypeRequest,
    IncidentResolution,
    Complaint, CreateComplaintRequest, UpdateComplaintRequest,
    ComplaintCategory, CreateComplaintCategoryRequest, UpdateComplaintCategoryRequest,
    ComplaintResponse, AddComplaintResponseRequest
} from '../models/claims.model';

@Injectable({ providedIn: 'root' })
export class ClaimsService {
    private http = inject(HttpClient);
    private base = environment.apiUrl;

    // ============================================================================
    // INCIDENTS - INCIDENCIAS
    // ============================================================================

    createIncident(req: CreateIncidentRequest): Observable<ApiResponse<Incident>> {
        return this.http.post<ApiResponse<Incident>>(`${this.base}/incidents`, req);
    }

    getIncident(id: string): Observable<ApiResponse<Incident>> {
        return this.http.get<ApiResponse<Incident>>(`${this.base}/incidents/${id}`);
    }

    getIncidents(): Observable<ApiResponse<Incident[]>> {
        return this.http.get<ApiResponse<Incident[]>>(`${this.base}/incidents`);
    }

    getIncidentsByOrg(orgId: string): Observable<ApiResponse<Incident[]>> {
        return this.http.get<ApiResponse<Incident[]>>(`${this.base}/incidents/organization/${orgId}`);
    }

    getIncidentsByZone(zoneId: string): Observable<ApiResponse<Incident[]>> {
        return this.http.get<ApiResponse<Incident[]>>(`${this.base}/incidents/zone/${zoneId}`);
    }

    getIncidentsBySeverity(severity: string): Observable<ApiResponse<Incident[]>> {
        return this.http.get<ApiResponse<Incident[]>>(`${this.base}/incidents/severity/${severity}`);
    }

    getIncidentsByStatus(status: string): Observable<ApiResponse<Incident[]>> {
        return this.http.get<ApiResponse<Incident[]>>(`${this.base}/incidents/status/${status}`);
    }

    updateIncident(id: string, req: UpdateIncidentRequest): Observable<ApiResponse<Incident>> {
        return this.http.put<ApiResponse<Incident>>(`${this.base}/incidents/${id}`, req);
    }

    assignIncident(id: string, req: AssignIncidentRequest): Observable<ApiResponse<Incident>> {
        return this.http.post<ApiResponse<Incident>>(`${this.base}/incidents/${id}/assign`, req);
    }

    resolveIncident(id: string, req: ResolveIncidentRequest): Observable<ApiResponse<Incident>> {
        return this.http.post<ApiResponse<Incident>>(`${this.base}/incidents/${id}/resolve`, req);
    }

    closeIncident(id: string): Observable<ApiResponse<Incident>> {
        return this.http.post<ApiResponse<Incident>>(`${this.base}/incidents/${id}/close`, {});
    }

    deleteIncident(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.base}/incidents/${id}`);
    }

    restoreIncident(id: string): Observable<ApiResponse<Incident>> {
        return this.http.patch<ApiResponse<Incident>>(`${this.base}/incidents/${id}/restore`, {});
    }

    // ============================================================================
    // INCIDENT TYPES - TIPOS DE INCIDENCIA
    // ============================================================================

    createIncidentType(req: CreateIncidentTypeRequest): Observable<ApiResponse<IncidentType>> {
        return this.http.post<ApiResponse<IncidentType>>(`${this.base}/incident-types`, req);
    }

    getIncidentType(id: string): Observable<ApiResponse<IncidentType>> {
        return this.http.get<ApiResponse<IncidentType>>(`${this.base}/incident-types/${id}`);
    }

    getIncidentTypes(): Observable<ApiResponse<IncidentType[]>> {
        return this.http.get<ApiResponse<IncidentType[]>>(`${this.base}/incident-types`);
    }

    getIncidentTypesByOrg(orgId: string): Observable<ApiResponse<IncidentType[]>> {
        return this.http.get<ApiResponse<IncidentType[]>>(`${this.base}/incident-types/organization/${orgId}`);
    }

    getActiveIncidentTypesByOrg(orgId: string): Observable<ApiResponse<IncidentType[]>> {
        return this.http.get<ApiResponse<IncidentType[]>>(`${this.base}/incident-types/organization/${orgId}/active`);
    }

    updateIncidentType(id: string, req: UpdateIncidentTypeRequest): Observable<ApiResponse<IncidentType>> {
        return this.http.put<ApiResponse<IncidentType>>(`${this.base}/incident-types/${id}`, req);
    }

    deleteIncidentType(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.base}/incident-types/${id}`);
    }

    restoreIncidentType(id: string): Observable<ApiResponse<IncidentType>> {
        return this.http.patch<ApiResponse<IncidentType>>(`${this.base}/incident-types/${id}/restore`, {});
    }

    // ============================================================================
    // INCIDENT RESOLUTIONS - RESOLUCIONES
    // ============================================================================

    getIncidentResolution(id: string): Observable<ApiResponse<IncidentResolution>> {
        return this.http.get<ApiResponse<IncidentResolution>>(`${this.base}/incident-resolutions/${id}`);
    }

    getResolutionByIncident(incidentId: string): Observable<ApiResponse<IncidentResolution>> {
        return this.http.get<ApiResponse<IncidentResolution>>(`${this.base}/incident-resolutions/incident/${incidentId}`);
    }

    getResolutionsByTechnician(userId: string): Observable<ApiResponse<IncidentResolution[]>> {
        return this.http.get<ApiResponse<IncidentResolution[]>>(`${this.base}/incident-resolutions/technician/${userId}`);
    }

    getResolutionsByType(type: string): Observable<ApiResponse<IncidentResolution[]>> {
        return this.http.get<ApiResponse<IncidentResolution[]>>(`${this.base}/incident-resolutions/type/${type}`);
    }

    // ============================================================================
    // COMPLAINTS - QUEJAS
    // ============================================================================

    createComplaint(req: CreateComplaintRequest): Observable<ApiResponse<Complaint>> {
        return this.http.post<ApiResponse<Complaint>>(`${this.base}/complaints`, req);
    }

    getComplaint(id: string): Observable<ApiResponse<Complaint>> {
        return this.http.get<ApiResponse<Complaint>>(`${this.base}/complaints/${id}`);
    }

    getComplaints(): Observable<ApiResponse<Complaint[]>> {
        return this.http.get<ApiResponse<Complaint[]>>(`${this.base}/complaints`);
    }

    getComplaintsByOrg(orgId: string): Observable<ApiResponse<Complaint[]>> {
        return this.http.get<ApiResponse<Complaint[]>>(`${this.base}/complaints/organization/${orgId}`);
    }

    getComplaintsByUser(userId: string): Observable<ApiResponse<Complaint[]>> {
        return this.http.get<ApiResponse<Complaint[]>>(`${this.base}/complaints/user/${userId}`);
    }

    getComplaintsByStatus(status: string): Observable<ApiResponse<Complaint[]>> {
        return this.http.get<ApiResponse<Complaint[]>>(`${this.base}/complaints/status/${status}`);
    }

    updateComplaint(id: string, req: UpdateComplaintRequest): Observable<ApiResponse<Complaint>> {
        return this.http.put<ApiResponse<Complaint>>(`${this.base}/complaints/${id}`, req);
    }

    addComplaintResponse(id: string, req: AddComplaintResponseRequest): Observable<ApiResponse<ComplaintResponse>> {
        return this.http.post<ApiResponse<ComplaintResponse>>(`${this.base}/complaints/${id}/responses`, req);
    }

    closeComplaint(id: string, satisfactionRating?: number): Observable<ApiResponse<Complaint>> {
        const params = satisfactionRating ? new HttpParams().set('satisfactionRating', satisfactionRating) : undefined;
        return this.http.post<ApiResponse<Complaint>>(`${this.base}/complaints/${id}/close`, {}, { params });
    }

    deleteComplaint(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.base}/complaints/${id}`);
    }

    restoreComplaint(id: string): Observable<ApiResponse<Complaint>> {
        return this.http.patch<ApiResponse<Complaint>>(`${this.base}/complaints/${id}/restore`, {});
    }

    // ============================================================================
    // COMPLAINT CATEGORIES - CATEGORÍAS DE QUEJAS
    // ============================================================================

    createComplaintCategory(req: CreateComplaintCategoryRequest): Observable<ApiResponse<ComplaintCategory>> {
        return this.http.post<ApiResponse<ComplaintCategory>>(`${this.base}/complaint-categories`, req);
    }

    getComplaintCategory(id: string): Observable<ApiResponse<ComplaintCategory>> {
        return this.http.get<ApiResponse<ComplaintCategory>>(`${this.base}/complaint-categories/${id}`);
    }

    getComplaintCategories(): Observable<ApiResponse<ComplaintCategory[]>> {
        return this.http.get<ApiResponse<ComplaintCategory[]>>(`${this.base}/complaint-categories`);
    }

    getComplaintCategoriesByOrg(orgId: string): Observable<ApiResponse<ComplaintCategory[]>> {
        return this.http.get<ApiResponse<ComplaintCategory[]>>(`${this.base}/complaint-categories/organization/${orgId}`);
    }

    getActiveComplaintCategoriesByOrg(orgId: string): Observable<ApiResponse<ComplaintCategory[]>> {
        return this.http.get<ApiResponse<ComplaintCategory[]>>(`${this.base}/complaint-categories/organization/${orgId}/active`);
    }

    updateComplaintCategory(id: string, req: UpdateComplaintCategoryRequest): Observable<ApiResponse<ComplaintCategory>> {
        return this.http.put<ApiResponse<ComplaintCategory>>(`${this.base}/complaint-categories/${id}`, req);
    }

    deleteComplaintCategory(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.base}/complaint-categories/${id}`);
    }

    restoreComplaintCategory(id: string): Observable<ApiResponse<ComplaintCategory>> {
        return this.http.patch<ApiResponse<ComplaintCategory>>(`${this.base}/complaint-categories/${id}/restore`, {});
    }
}
