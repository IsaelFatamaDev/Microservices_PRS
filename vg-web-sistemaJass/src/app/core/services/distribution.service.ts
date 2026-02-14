import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
     DistributionProgram,
     CreateProgramRequest,
     UpdateProgramRequest,
     DistributionRoute,
     CreateRouteRequest,
     UpdateRouteRequest,
     DistributionSchedule,
     CreateScheduleRequest,
     UpdateScheduleRequest,
     ApiResponse
} from '../models';

@Injectable({
     providedIn: 'root'
})
export class DistributionService {
     private http = inject(HttpClient);
     private apiUrl = environment.apiUrl;

     // ==================== PROGRAMS ====================
     getPrograms(): Observable<DistributionProgram[]> {
          return this.http.get<ApiResponse<DistributionProgram[]>>(`${this.apiUrl}/programs`)
               .pipe(map(res => res.data || []));
     }

     getActivePrograms(): Observable<DistributionProgram[]> {
          return this.http.get<ApiResponse<DistributionProgram[]>>(`${this.apiUrl}/programs/active`)
               .pipe(map(res => res.data || []));
     }

     getProgramById(id: string): Observable<DistributionProgram> {
          return this.http.get<ApiResponse<DistributionProgram>>(`${this.apiUrl}/programs/${id}`)
               .pipe(map(res => res.data!));
     }

     createProgram(request: CreateProgramRequest): Observable<DistributionProgram> {
          return this.http.post<ApiResponse<DistributionProgram>>(`${this.apiUrl}/programs`, request)
               .pipe(map(res => res.data!));
     }

     updateProgram(id: string, request: UpdateProgramRequest): Observable<DistributionProgram> {
          return this.http.put<ApiResponse<DistributionProgram>>(`${this.apiUrl}/programs/${id}`, request)
               .pipe(map(res => res.data!));
     }

     deactivateProgram(id: string): Observable<DistributionProgram> {
          return this.http.patch<ApiResponse<DistributionProgram>>(`${this.apiUrl}/programs/${id}/deactivate`, {})
               .pipe(map(res => res.data!));
     }

     restoreProgram(id: string): Observable<DistributionProgram> {
          return this.http.patch<ApiResponse<DistributionProgram>>(`${this.apiUrl}/programs/${id}/restore`, {})
               .pipe(map(res => res.data!));
     }

     // ==================== ROUTES ====================
     getRoutes(): Observable<DistributionRoute[]> {
          return this.http.get<ApiResponse<DistributionRoute[]>>(`${this.apiUrl}/routes`)
               .pipe(map(res => res.data || []));
     }

     getActiveRoutes(): Observable<DistributionRoute[]> {
          return this.http.get<ApiResponse<DistributionRoute[]>>(`${this.apiUrl}/routes/active`)
               .pipe(map(res => res.data || []));
     }

     getRouteById(id: string): Observable<DistributionRoute> {
          return this.http.get<ApiResponse<DistributionRoute>>(`${this.apiUrl}/routes/${id}`)
               .pipe(map(res => res.data!));
     }

     createRoute(request: CreateRouteRequest): Observable<DistributionRoute> {
          return this.http.post<ApiResponse<DistributionRoute>>(`${this.apiUrl}/routes`, request)
               .pipe(map(res => res.data!));
     }

     updateRoute(id: string, request: UpdateRouteRequest): Observable<DistributionRoute> {
          return this.http.put<ApiResponse<DistributionRoute>>(`${this.apiUrl}/routes/${id}`, request)
               .pipe(map(res => res.data!));
     }

     deactivateRoute(id: string): Observable<DistributionRoute> {
          return this.http.patch<ApiResponse<DistributionRoute>>(`${this.apiUrl}/routes/${id}/deactivate`, {})
               .pipe(map(res => res.data!));
     }

     restoreRoute(id: string): Observable<DistributionRoute> {
          return this.http.patch<ApiResponse<DistributionRoute>>(`${this.apiUrl}/routes/${id}/restore`, {})
               .pipe(map(res => res.data!));
     }

     // ==================== SCHEDULES ====================
     getSchedules(): Observable<DistributionSchedule[]> {
          return this.http.get<ApiResponse<DistributionSchedule[]>>(`${this.apiUrl}/schedules`)
               .pipe(map(res => res.data || []));
     }

     getActiveSchedules(): Observable<DistributionSchedule[]> {
          return this.http.get<ApiResponse<DistributionSchedule[]>>(`${this.apiUrl}/schedules/active`)
               .pipe(map(res => res.data || []));
     }

     getScheduleById(id: string): Observable<DistributionSchedule> {
          return this.http.get<ApiResponse<DistributionSchedule>>(`${this.apiUrl}/schedules/${id}`)
               .pipe(map(res => res.data!));
     }

     createSchedule(request: CreateScheduleRequest): Observable<DistributionSchedule> {
          return this.http.post<ApiResponse<DistributionSchedule>>(`${this.apiUrl}/schedules`, request)
               .pipe(map(res => res.data!));
     }

     updateSchedule(id: string, request: UpdateScheduleRequest): Observable<DistributionSchedule> {
          return this.http.put<ApiResponse<DistributionSchedule>>(`${this.apiUrl}/schedules/${id}`, request)
               .pipe(map(res => res.data!));
     }

     deactivateSchedule(id: string): Observable<DistributionSchedule> {
          return this.http.patch<ApiResponse<DistributionSchedule>>(`${this.apiUrl}/schedules/${id}/deactivate`, {})
               .pipe(map(res => res.data!));
     }

     restoreSchedule(id: string): Observable<DistributionSchedule> {
          return this.http.patch<ApiResponse<DistributionSchedule>>(`${this.apiUrl}/schedules/${id}/restore`, {})
               .pipe(map(res => res.data!));
     }
}
