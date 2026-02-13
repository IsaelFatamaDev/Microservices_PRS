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
     readonly organizationId = computed(() => this._user()?.organizationId || null);
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
          const base64Url = token.split('.')[1];
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
                    this.storage.set(TOKEN_KEY, response.data.accessToken);
                    this.storage.set(REFRESH_TOKEN_KEY, response.data.refreshToken);
                    this._accessToken.set(response.data.accessToken);
                    this._isLoading.set(false);
               }),
               catchError(error => {
                    this._isLoading.set(false);
                    return throwError(() => error);
               })
          );
     }

     loadUserProfile(): Observable<ApiResponse<User>> {
          return this.http.get<ApiResponse<User>>(`${environment.apiUrl}/users/me`).pipe(
               tap(response => {
                    this._user.set(response.data);
                    this.storage.setObject(USER_KEY, response.data);
               })
          );
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
                    this.storage.set(TOKEN_KEY, response.data.accessToken);
                    this.storage.set(REFRESH_TOKEN_KEY, response.data.refreshToken);
                    this._accessToken.set(response.data.accessToken);
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
