import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, interval, takeWhile, switchMap, catchError, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export interface InstanceStatus {
     state: 'open' | 'close' | 'connecting';
     phoneNumber?: string;
}

export interface QrCodeResponse {
     qrcode: string;
     base64: string;
}

@Injectable({ providedIn: 'root' })
export class EvolutionApiService {
     private http = inject(HttpClient);
     private authService = inject(AuthService);

     private _connectionStatus = signal<'connecting' | 'qr_ready' | 'connected' | 'disconnected' | 'error'>('disconnected');
     private _qrCode = signal<string>('');
     private _phoneNumber = signal<string>('');
     private _isChecking = signal(false);

     readonly connectionStatus = this._connectionStatus.asReadonly();
     readonly qrCode = this._qrCode.asReadonly();
     readonly phoneNumber = this._phoneNumber.asReadonly();
     readonly isChecking = this._isChecking.asReadonly();

     private get instanceName(): string {
          return `jass_${this.authService.organizationId()}`;
     }

     private get baseUrl(): string {
          return environment.evolutionApiUrl;
     }

     checkConnection(): Observable<InstanceStatus> {
          this._isChecking.set(true);
          this._connectionStatus.set('connecting');

          return this.http.get<InstanceStatus>(`${this.baseUrl}/instance/connectionState/${this.instanceName}`).pipe(
               tap(status => {
                    this._isChecking.set(false);
                    if (status.state === 'open') {
                         this._connectionStatus.set('connected');
                         this._phoneNumber.set(status.phoneNumber || '');
                    } else {
                         this._connectionStatus.set('disconnected');
                    }
               }),
               catchError(() => {
                    this._isChecking.set(false);
                    this._connectionStatus.set('disconnected');
                    return of({ state: 'close' as const });
               })
          );
     }

     getQrCode(): Observable<QrCodeResponse> {
          this._connectionStatus.set('connecting');

          return this.http.get<QrCodeResponse>(`${this.baseUrl}/instance/connect/${this.instanceName}`).pipe(
               tap(response => {
                    this._qrCode.set(response.base64 || response.qrcode);
                    this._connectionStatus.set('qr_ready');
               }),
               catchError(error => {
                    this._connectionStatus.set('error');
                    throw error;
               })
          );
     }

     startPolling(): Observable<InstanceStatus> {
          return interval(3000).pipe(
               switchMap(() => this.http.get<InstanceStatus>(`${this.baseUrl}/instance/connectionState/${this.instanceName}`)),
               tap(status => {
                    if (status.state === 'open') {
                         this._connectionStatus.set('connected');
                         this._phoneNumber.set(status.phoneNumber || '');
                    }
               }),
               takeWhile(status => status.state !== 'open', true)
          );
     }

     logout(): Observable<void> {
          return this.http.delete<void>(`${this.baseUrl}/instance/logout/${this.instanceName}`).pipe(
               tap(() => {
                    this._connectionStatus.set('disconnected');
                    this._qrCode.set('');
                    this._phoneNumber.set('');
               }),
               catchError(() => of(void 0))
          );
     }

     sendTextMessage(phone: string, message: string): Observable<boolean> {
          const formattedPhone = this.formatPhone(phone);
          return this.http.post<any>(`${this.baseUrl}/message/sendText/${this.instanceName}`, {
               number: formattedPhone,
               textMessage: { text: message }
          }).pipe(
               tap(() => true),
               catchError(() => of(false))
          );
     }

     private formatPhone(phone: string): string {
          const cleaned = phone.replace(/\D/g, '');
          return cleaned.startsWith('51') ? cleaned : `51${cleaned}`;
     }

     reset(): void {
          this._connectionStatus.set('disconnected');
          this._qrCode.set('');
          this._phoneNumber.set('');
          this._isChecking.set(false);
     }
}
