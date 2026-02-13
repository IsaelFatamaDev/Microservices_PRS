import { Injectable, signal, computed, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError, of } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { StorageService } from './storage.service';
import { LoginRequest, LoginResponse, TokenPayload, RefreshTokenRequest } from '../models/auth.model';
import { ApiResponse, User, Role, Organization } from '../models';

const TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';
const USER_KEY = 'currentUser';
const ORG_KEY = 'currentOrganization';

@Injectable({ providedIn: 'root' })
export class AuthService {
     private http = inject(HttpClient);
     private storage = inject(StorageService);
     private router = inject(Router);
     private platformId = inject(PLATFORM_ID);

     private _user = signal<User | null>(null);
     private _organization = signal<Organization | null>(null);
     private _isLoading = signal(false);
     private _accessToken = signal<string | null>(null);

     readonly user = this._user.asReadonly();
     readonly organization = this._organization.asReadonly();
     readonly isLoading = this._isLoading.asReadonly();
     readonly accessToken = this._accessToken.asReadonly();

     readonly isAuthenticated = computed(() => !!this._accessToken() && !!this._user());
     readonly userRole = computed(() => this._user()?.role || null);
     readonly userId = computed(() => this._user()?.id || null);
     readonly organizationId = computed(() => this._user()?.organizationId || this._user()?.organization_id || null);
     readonly userFullName = computed(() => {
          const user = this._user();
          return user ? `${user.firstName} ${user.lastName}` : '';
     });
     readonly userInitials = computed(() => {
          const user = this._user();
          return user ? `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase() : '';
     });
     readonly isSuperAdmin = computed(() => this._user()?.role === 'SUPER_ADMIN');
     readonly isAdmin = computed(() => this._user()?.role === 'ADMIN');
     readonly isOperator = computed(() => this._user()?.role === 'OPERATOR');
     readonly isClient = computed(() => this._user()?.role === 'CLIENT');

     constructor() {
          if (isPlatformBrowser(this.platformId)) {
               this.loadFromStorage();
          }
     }

     private loadFromStorage(): void {
          const token = this.storage.get(TOKEN_KEY);
          const user = this.storage.getObject<User>(USER_KEY);
          const org = this.storage.getObject<Organization>(ORG_KEY);

          if (token && user) {
               if (!this.isTokenExpired(token)) {
                    this._accessToken.set(token);
                    this._user.set(user);
                    if (org) this._organization.set(org);
               } else {
                    this.clearStorage();
               }
          }
     }

     private isTokenExpired(token: string): boolean {
          try {
               const payload = this.decodeToken(token);
               return payload.exp * 1000 < Date.now();
          } catch {
               return true;
          }
     }

     private decodeToken(token: string): TokenPayload {
          const parts = token.split('.');
          if (parts.length < 2 || !parts[1]) {
               throw new Error('Invalid JWT token format');
          }
          const base64Url = parts[1];
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          const jsonPayload = decodeURIComponent(
               atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
          );
          return JSON.parse(jsonPayload);
     }

     login(credentials: LoginRequest): Observable<ApiResponse<LoginResponse>> {
          this._isLoading.set(true);
          const payload = {
               username: credentials.email,
               password: credentials.password
          };
          return this.http.post<ApiResponse<LoginResponse>>(`${environment.apiUrl}/auth/login`, payload).pipe(
               tap(response => {
                    this.storage.set(TOKEN_KEY, response.data.access_token);
                    this.storage.set(REFRESH_TOKEN_KEY, response.data.refresh_token);
                    this._accessToken.set(response.data.access_token);
                    this._isLoading.set(false);
               }),
               catchError(error => {
                    this._isLoading.set(false);
                    return throwError(() => error);
               })
          );
     }

     loadUserProfile(): Observable<ApiResponse<User>> {
          const token = this._accessToken() || this.storage.get(TOKEN_KEY);
          if (!token) {
               return throwError(() => new Error('No access token available'));
          }
          try {
               const payload = this.decodeToken(token);
               const userId = payload.userId;

               if (!userId) {
                    return throwError(() => new Error('userId not found in token'));
               }

               return this.http.get<ApiResponse<User>>(`${environment.apiUrl}/users/${userId}`).pipe(
                    tap(response => {
                         this._user.set(response.data);
                         this.storage.setObject(USER_KEY, response.data);
                    })
               );
          } catch (e) {
               console.error('Error decoding token:', e, 'Token value:', token);
               return throwError(() => new Error('Invalid token format'));
          }
     }

     loadOrganization(orgId: string): Observable<ApiResponse<Organization>> {
          return this.http.get<ApiResponse<Organization>>(`${environment.apiUrl}/organizations/${orgId}`).pipe(
               tap(response => {
                    this._organization.set(response.data);
                    this.storage.setObject(ORG_KEY, response.data);
               })
          );
     }

     setOrganization(org: Organization): void {
          this._organization.set(org);
          this.storage.setObject(ORG_KEY, org);
     }

     refreshToken(): Observable<ApiResponse<LoginResponse>> {
          const refreshToken = this.storage.get(REFRESH_TOKEN_KEY);
          if (!refreshToken) {
               this.logout();
               return throwError(() => new Error('No refresh token'));
          }

          const body: RefreshTokenRequest = { refreshToken };
          return this.http.post<ApiResponse<LoginResponse>>(`${environment.apiUrl}/auth/refresh`, body).pipe(
               tap(response => {
                    this.storage.set(TOKEN_KEY, response.data.access_token);
                    this.storage.set(REFRESH_TOKEN_KEY, response.data.refresh_token);
                    this._accessToken.set(response.data.access_token);
               }),
               catchError(error => {
                    this.logout();
                    return throwError(() => error);
               })
          );
     }

     logout(): void {
          this.clearStorage();
          this._user.set(null);
          this._organization.set(null);
          this._accessToken.set(null);
          this.router.navigate(['/auth/login']);
     }

     private clearStorage(): void {
          this.storage.remove(TOKEN_KEY);
          this.storage.remove(REFRESH_TOKEN_KEY);
          this.storage.remove(USER_KEY);
          this.storage.remove(ORG_KEY);
     }

     hasRole(role: Role | Role[]): boolean {
          const userRole = this._user()?.role;
          if (!userRole) return false;
          if (Array.isArray(role)) {
               return role.includes(userRole);
          }
          return userRole === role;
     }
}
