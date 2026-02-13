import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, User, Mail, Save, Loader2 } from 'lucide-angular';
import { AuthService } from '../../../core/services/auth.service';
import { AlertService } from '../../../core/services/alert.service';

@Component({
     selector: 'app-super-admin-profile',
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
            <p class="text-gray-500">Super Administrador</p>
          </div>
        </div>

        <div class="space-y-4">
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
              <p class="font-medium text-gray-800">{{ authService.user()?.documentNumber || authService.user()?.dni }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SuperAdminProfileComponent {
     authService = inject(AuthService);

     userIcon = User;
     mailIcon = Mail;
}
