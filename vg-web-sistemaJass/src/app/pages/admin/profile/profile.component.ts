import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, User, Mail, Phone, MapPin, Save, Loader2 } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import { AlertService } from '../../../core/services/alert.service';
import { ApiResponse, User as UserModel } from '../../../core';

@Component({
     selector: 'app-admin-profile',
     standalone: true,
     imports: [CommonModule, FormsModule, LucideAngularModule],
     template: `
    <div class="max-w-2xl mx-auto">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Mi Perfil</h1>
        <p class="text-gray-500">Gestiona tu información personal</p>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-6">
        <div class="flex items-center gap-4 mb-6 pb-6 border-b border-gray-200">
          <div class="w-20 h-20 bg-blue-600 text-white rounded-full flex items-center justify-center text-2xl font-bold">
            {{ authService.userInitials() }}
          </div>
          <div>
            <h2 class="text-xl font-semibold text-gray-800">{{ authService.userFullName() }}</h2>
            <p class="text-gray-500">Administrador</p>
            <p class="text-sm text-blue-600">{{ authService.organization()?.name }}</p>
          </div>
        </div>

        <form (ngSubmit)="updateProfile()" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Nombres</label>
              <input
                type="text"
                [(ngModel)]="form.firstName"
                name="firstName"
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Apellidos</label>
              <input
                type="text"
                [(ngModel)]="form.lastName"
                name="lastName"
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Teléfono</label>
            <input
              type="text"
              [(ngModel)]="form.phone"
              name="phone"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Dirección</label>
            <input
              type="text"
              [(ngModel)]="form.address"
              name="address"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
          </div>

          <div class="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
            <lucide-icon [img]="mailIcon" [size]="20" class="text-gray-400"></lucide-icon>
            <div>
              <p class="text-sm text-gray-500">Correo Electrónico</p>
              <p class="font-medium text-gray-800">{{ authService.user()?.email }}</p>
            </div>
          </div>

          <div class="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
            <lucide-icon [img]="userIcon" [size]="20" class="text-gray-400"></lucide-icon>
            <div>
              <p class="text-sm text-gray-500">DNI</p>
              <p class="font-medium text-gray-800">{{ authService.user()?.dni }}</p>
            </div>
          </div>

          <div class="flex justify-end pt-4 border-t border-gray-200">
            <button
              type="submit"
              [disabled]="isSubmitting()"
              class="inline-flex items-center gap-2 px-6 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-blue-400 transition-colors">
              @if (isSubmitting()) {
                <lucide-icon [img]="loaderIcon" [size]="20" class="animate-spin"></lucide-icon>
                Guardando...
              } @else {
                <lucide-icon [img]="saveIcon" [size]="20"></lucide-icon>
                Guardar Cambios
              }
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class AdminProfileComponent {
     private http = inject(HttpClient);
     authService = inject(AuthService);
     private alertService = inject(AlertService);

     isSubmitting = signal(false);

     form = {
          firstName: this.authService.user()?.firstName || '',
          lastName: this.authService.user()?.lastName || '',
          phone: this.authService.user()?.phone || '',
          address: this.authService.user()?.address || ''
     };

     userIcon = User;
     mailIcon = Mail;
     phoneIcon = Phone;
     mapPinIcon = MapPin;
     saveIcon = Save;
     loaderIcon = Loader2;

     updateProfile(): void {
          this.isSubmitting.set(true);
          const userId = this.authService.userId();

          this.http.put<ApiResponse<UserModel>>(`${environment.apiUrl}/users/${userId}`, this.form).subscribe({
               next: () => {
                    this.isSubmitting.set(false);
                    this.alertService.success('Actualizado', 'Perfil actualizado correctamente');
                    this.authService.loadUserProfile().subscribe();
               },
               error: () => this.isSubmitting.set(false)
          });
     }
}
