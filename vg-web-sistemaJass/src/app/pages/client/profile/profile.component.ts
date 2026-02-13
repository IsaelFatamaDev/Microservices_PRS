import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import { AlertService } from '../../../core/services/alert.service';

@Component({
     selector: 'app-client-profile',
     standalone: true,
     imports: [CommonModule, FormsModule],
     template: `
          <div class="animate-fade-in">
               <div class="mb-6">
                    <h1 class="text-2xl font-bold text-gray-800">Mi Perfil</h1>
                    <p class="text-gray-600 mt-1">Consulta y actualiza tu información personal</p>
               </div>

               @if (loading()) {
                    <div class="flex justify-center items-center h-64">
                         <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                    </div>
               } @else {
                    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                         <div class="lg:col-span-1">
                              <div class="bg-white rounded-xl shadow-lg p-6 text-center">
                                   <div class="w-24 h-24 mx-auto bg-gradient-to-br from-blue-500 to-blue-700 rounded-full flex items-center justify-center mb-4">
                                        <span class="text-3xl font-bold text-white">
                                             {{ getInitials() }}
                                        </span>
                                   </div>
                                   <h2 class="text-xl font-semibold text-gray-800">{{ user()?.firstName }} {{ user()?.lastName }}</h2>
                                   <p class="text-gray-500 mt-1">{{ user()?.email }}</p>
                                   <div class="mt-4 inline-flex items-center gap-2 px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm">
                                        <span class="w-2 h-2 bg-green-500 rounded-full"></span>
                                        Cliente Activo
                                   </div>

                                   <div class="mt-6 pt-6 border-t">
                                        <div class="text-left space-y-3">
                                             <div>
                                                  <span class="text-xs text-gray-500 uppercase">DNI</span>
                                                  <p class="text-gray-800 font-medium">{{ user()?.documentNumber }}</p>
                                             </div>
                                             <div>
                                                  <span class="text-xs text-gray-500 uppercase">Teléfono</span>
                                                  <p class="text-gray-800 font-medium">{{ user()?.phone || 'No registrado' }}</p>
                                             </div>
                                             <div>
                                                  <span class="text-xs text-gray-500 uppercase">Dirección</span>
                                                  <p class="text-gray-800 font-medium">{{ user()?.address || 'No registrada' }}</p>
                                             </div>
                                        </div>
                                   </div>
                              </div>
                         </div>

                         <div class="lg:col-span-2 space-y-6">
                              <div class="bg-white rounded-xl shadow-lg p-6">
                                   <h3 class="text-lg font-semibold text-gray-800 mb-4">Información Personal</h3>

                                   <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <div>
                                             <label class="block text-sm font-medium text-gray-700 mb-1">Nombres</label>
                                             <input
                                                  type="text"
                                                  [(ngModel)]="formData.firstName"
                                                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                                  placeholder="Ingresa tus nombres">
                                        </div>
                                        <div>
                                             <label class="block text-sm font-medium text-gray-700 mb-1">Apellidos</label>
                                             <input
                                                  type="text"
                                                  [(ngModel)]="formData.lastName"
                                                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                                  placeholder="Ingresa tus apellidos">
                                        </div>
                                        <div>
                                             <label class="block text-sm font-medium text-gray-700 mb-1">DNI</label>
                                             <input
                                                  type="text"
                                                  [(ngModel)]="formData.documentNumber"
                                                  disabled
                                                  class="w-full px-4 py-2 border border-gray-200 rounded-lg bg-gray-50 text-gray-500 cursor-not-allowed">
                                        </div>
                                        <div>
                                             <label class="block text-sm font-medium text-gray-700 mb-1">Correo Electrónico</label>
                                             <input
                                                  type="email"
                                                  [(ngModel)]="formData.email"
                                                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                                  placeholder="correo@ejemplo.com">
                                        </div>
                                        <div>
                                             <label class="block text-sm font-medium text-gray-700 mb-1">Teléfono</label>
                                             <input
                                                  type="tel"
                                                  [(ngModel)]="formData.phone"
                                                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                                  placeholder="999 999 999">
                                        </div>
                                        <div class="md:col-span-2">
                                             <label class="block text-sm font-medium text-gray-700 mb-1">Dirección</label>
                                             <input
                                                  type="text"
                                                  [(ngModel)]="formData.address"
                                                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                                  placeholder="Ingresa tu dirección">
                                        </div>
                                   </div>

                                   <div class="mt-6 flex justify-end">
                                        <button
                                             (click)="updateProfile()"
                                             [disabled]="saving()"
                                             class="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed">
                                             @if (saving()) {
                                                  <div class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                                             }
                                             Guardar Cambios
                                        </button>
                                   </div>
                              </div>

                              <div class="bg-white rounded-xl shadow-lg p-6">
                                   <h3 class="text-lg font-semibold text-gray-800 mb-4">Cambiar Contraseña</h3>

                                   <div class="space-y-4">
                                        <div>
                                             <label class="block text-sm font-medium text-gray-700 mb-1">Contraseña Actual</label>
                                             <div class="relative">
                                                  <input
                                                       [type]="showCurrentPassword ? 'text' : 'password'"
                                                       [(ngModel)]="passwordData.currentPassword"
                                                       class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 pr-10"
                                                       placeholder="Ingresa tu contraseña actual">
                                                  <button
                                                       type="button"
                                                       (click)="showCurrentPassword = !showCurrentPassword"
                                                       class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700">
                                                       @if (showCurrentPassword) {
                                                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
                                                            </svg>
                                                       } @else {
                                                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                                                            </svg>
                                                       }
                                                  </button>
                                             </div>
                                        </div>
                                        <div>
                                             <label class="block text-sm font-medium text-gray-700 mb-1">Nueva Contraseña</label>
                                             <div class="relative">
                                                  <input
                                                       [type]="showNewPassword ? 'text' : 'password'"
                                                       [(ngModel)]="passwordData.newPassword"
                                                       class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 pr-10"
                                                       placeholder="Ingresa tu nueva contraseña">
                                                  <button
                                                       type="button"
                                                       (click)="showNewPassword = !showNewPassword"
                                                       class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700">
                                                       @if (showNewPassword) {
                                                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
                                                            </svg>
                                                       } @else {
                                                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                                                            </svg>
                                                       }
                                                  </button>
                                             </div>
                                        </div>
                                        <div>
                                             <label class="block text-sm font-medium text-gray-700 mb-1">Confirmar Nueva Contraseña</label>
                                             <div class="relative">
                                                  <input
                                                       [type]="showConfirmPassword ? 'text' : 'password'"
                                                       [(ngModel)]="passwordData.confirmPassword"
                                                       class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 pr-10"
                                                       placeholder="Confirma tu nueva contraseña">
                                                  <button
                                                       type="button"
                                                       (click)="showConfirmPassword = !showConfirmPassword"
                                                       class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700">
                                                       @if (showConfirmPassword) {
                                                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
                                                            </svg>
                                                       } @else {
                                                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                                                            </svg>
                                                       }
                                                  </button>
                                             </div>
                                        </div>
                                   </div>

                                   <div class="mt-6 flex justify-end">
                                        <button
                                             (click)="changePassword()"
                                             [disabled]="changingPassword()"
                                             class="flex items-center gap-2 px-6 py-2 bg-gray-800 text-white rounded-lg hover:bg-gray-900 transition disabled:opacity-50 disabled:cursor-not-allowed">
                                             @if (changingPassword()) {
                                                  <div class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                                             }
                                             Cambiar Contraseña
                                        </button>
                                   </div>
                              </div>
                         </div>
                    </div>
               }
          </div>
     `,
     styles: [`
          .animate-fade-in {
               animation: fadeIn 0.5s ease-out;
          }
          @keyframes fadeIn {
               from { opacity: 0; transform: translateY(20px); }
               to { opacity: 1; transform: translateY(0); }
          }
     `]
})
export class ClientProfileComponent implements OnInit {
     private http = inject(HttpClient);
     private authService = inject(AuthService);
     private alertService = inject(AlertService);

     loading = signal(true);
     saving = signal(false);
     changingPassword = signal(false);
     user = signal<any>(null);

     formData = {
          firstName: '',
          lastName: '',
          documentNumber: '',
          email: '',
          phone: '',
          address: ''
     };

     passwordData = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
     };

     showCurrentPassword = false;
     showNewPassword = false;
     showConfirmPassword = false;

     ngOnInit(): void {
          this.loadProfile();
     }

     private loadProfile(): void {
          const currentUser = this.authService.user();
          if (currentUser?.id) {
               this.http.get<any>(`${environment.apiUrl}/users/${currentUser.id}`)
                    .subscribe({
                         next: (response) => {
                              const userData = response.data || response;
                              this.user.set(userData);
                              this.formData = {
                                   firstName: userData.firstName || '',
                                   lastName: userData.lastName || '',
                                   documentNumber: userData.documentNumber || '',
                                   email: userData.email || '',
                                   phone: userData.phone || '',
                                   address: userData.address || ''
                              };
                              this.loading.set(false);
                         },
                         error: () => {
                              this.alertService.error('Error', 'No se pudo cargar tu información');
                              this.loading.set(false);
                         }
                    });
          }
     }

     getInitials(): string {
          const user = this.user();
          if (!user) return '';
          const first = user.firstName?.charAt(0) || '';
          const last = user.lastName?.charAt(0) || '';
          return (first + last).toUpperCase();
     }

     async updateProfile(): Promise<void> {
          if (!this.formData.firstName || !this.formData.lastName) {
               this.alertService.warning('Campos requeridos', 'Nombres y apellidos son obligatorios');
               return;
          }

          const confirmed = await this.alertService.confirm(
               '¿Actualizar perfil?',
               '¿Estás seguro de guardar los cambios?'
          );

          if (!confirmed) return;

          this.saving.set(true);
          const currentUser = this.authService.user();

          this.http.put<any>(`${environment.apiUrl}/users/${currentUser?.id}`, {
               firstName: this.formData.firstName,
               lastName: this.formData.lastName,
               email: this.formData.email,
               phone: this.formData.phone,
               address: this.formData.address
          }).subscribe({
               next: () => {
                    this.alertService.success('Actualizado', 'Tu perfil ha sido actualizado correctamente');
                    this.loadProfile();
                    this.saving.set(false);
               },
               error: () => {
                    this.alertService.error('Error', 'No se pudo actualizar tu perfil');
                    this.saving.set(false);
               }
          });
     }

     async changePassword(): Promise<void> {
          if (!this.passwordData.currentPassword || !this.passwordData.newPassword || !this.passwordData.confirmPassword) {
               this.alertService.warning('Campos requeridos', 'Todos los campos de contraseña son obligatorios');
               return;
          }

          if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
               this.alertService.error('Error', 'Las contraseñas no coinciden');
               return;
          }

          if (this.passwordData.newPassword.length < 6) {
               this.alertService.warning('Contraseña débil', 'La contraseña debe tener al menos 6 caracteres');
               return;
          }

          const confirmed = await this.alertService.confirm(
               '¿Cambiar contraseña?',
               '¿Estás seguro de cambiar tu contraseña?'
          );

          if (!confirmed) return;

          this.changingPassword.set(true);
          const currentUser = this.authService.user();

          this.http.post<any>(`${environment.apiUrl}/users/${currentUser?.id}/change-password`, {
               currentPassword: this.passwordData.currentPassword,
               newPassword: this.passwordData.newPassword
          }).subscribe({
               next: () => {
                    this.alertService.success('Actualizado', 'Tu contraseña ha sido cambiada correctamente');
                    this.passwordData = { currentPassword: '', newPassword: '', confirmPassword: '' };
                    this.changingPassword.set(false);
               },
               error: () => {
                    this.alertService.error('Error', 'No se pudo cambiar la contraseña. Verifica tu contraseña actual.');
                    this.changingPassword.set(false);
               }
          });
     }
}
