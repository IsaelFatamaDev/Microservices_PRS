import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Injectable({ providedIn: 'root' })
export class AlertService {
     private platformId = inject(PLATFORM_ID);

     private get isBrowser(): boolean {
          return isPlatformBrowser(this.platformId);
     }

     success(title: string, message?: string): Promise<SweetAlertResult> {
          if (!this.isBrowser) return Promise.resolve({} as SweetAlertResult);
          return Swal.fire({
               icon: 'success',
               title,
               text: message,
               confirmButtonColor: '#3B82F6',
               timer: 3000,
               timerProgressBar: true
          });
     }

     error(title: string, message?: string): Promise<SweetAlertResult> {
          if (!this.isBrowser) return Promise.resolve({} as SweetAlertResult);
          return Swal.fire({
               icon: 'error',
               title,
               text: message,
               confirmButtonColor: '#3B82F6'
          });
     }

     warning(title: string, message?: string): Promise<SweetAlertResult> {
          if (!this.isBrowser) return Promise.resolve({} as SweetAlertResult);
          return Swal.fire({
               icon: 'warning',
               title,
               text: message,
               confirmButtonColor: '#3B82F6'
          });
     }

     info(title: string, message?: string): Promise<SweetAlertResult> {
          if (!this.isBrowser) return Promise.resolve({} as SweetAlertResult);
          return Swal.fire({
               icon: 'info',
               title,
               text: message,
               confirmButtonColor: '#3B82F6'
          });
     }

     confirm(title: string, message?: string, confirmText = 'Confirmar', cancelText = 'Cancelar'): Promise<SweetAlertResult> {
          if (!this.isBrowser) return Promise.resolve({} as SweetAlertResult);
          return Swal.fire({
               icon: 'question',
               title,
               text: message,
               showCancelButton: true,
               confirmButtonColor: '#3B82F6',
               cancelButtonColor: '#EF4444',
               confirmButtonText: confirmText,
               cancelButtonText: cancelText
          });
     }

     confirmDelete(itemName: string): Promise<SweetAlertResult> {
          if (!this.isBrowser) return Promise.resolve({} as SweetAlertResult);
          return Swal.fire({
               icon: 'warning',
               title: '¿Eliminar?',
               text: `¿Está seguro de eliminar ${itemName}? Esta acción no se puede deshacer.`,
               showCancelButton: true,
               confirmButtonColor: '#EF4444',
               cancelButtonColor: '#6B7280',
               confirmButtonText: 'Sí, eliminar',
               cancelButtonText: 'Cancelar'
          });
     }

     confirmRestore(itemName: string): Promise<SweetAlertResult> {
          if (!this.isBrowser) return Promise.resolve({} as SweetAlertResult);
          return Swal.fire({
               icon: 'question',
               title: '¿Restaurar?',
               text: `¿Está seguro de restaurar ${itemName}?`,
               showCancelButton: true,
               confirmButtonColor: '#10B981',
               cancelButtonColor: '#6B7280',
               confirmButtonText: 'Sí, restaurar',
               cancelButtonText: 'Cancelar'
          });
     }

     loading(title = 'Procesando...', message?: string): void {
          if (!this.isBrowser) return;
          Swal.fire({
               title,
               text: message,
               allowOutsideClick: false,
               allowEscapeKey: false,
               didOpen: () => {
                    Swal.showLoading();
               }
          });
     }

     close(): void {
          if (!this.isBrowser) return;
          Swal.close();
     }
}
