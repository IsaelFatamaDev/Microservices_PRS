import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Building2, Users, Plus } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { Organization, ApiResponse } from '../../../core';

@Component({
     selector: 'app-super-admin-dashboard',
     standalone: true,
     imports: [CommonModule, RouterLink, LucideAngularModule],
     template: `
    <div>
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Dashboard</h1>
          <p class="text-gray-500">Panel de control del Super Administrador</p>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
              <lucide-icon [img]="buildingIcon" [size]="24" class="text-blue-600"></lucide-icon>
            </div>
            <div>
              <p class="text-sm text-gray-500">Organizaciones</p>
              <p class="text-2xl font-bold text-gray-800">{{ organizationsCount() }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
              <lucide-icon [img]="usersIcon" [size]="24" class="text-green-600"></lucide-icon>
            </div>
            <div>
              <p class="text-sm text-gray-500">Administradores</p>
              <p class="text-2xl font-bold text-gray-800">{{ adminsCount() }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-800">Acciones Rápidas</h2>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <a routerLink="/super-admin/organizations/new"
             class="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-blue-300 transition-colors">
            <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
              <lucide-icon [img]="plusIcon" [size]="20" class="text-blue-600"></lucide-icon>
            </div>
            <div>
              <p class="font-medium text-gray-800">Nueva Organización</p>
              <p class="text-sm text-gray-500">Crear una nueva JASS</p>
            </div>
          </a>
          <a routerLink="/super-admin/organizations"
             class="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-blue-300 transition-colors">
            <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
              <lucide-icon [img]="buildingIcon" [size]="20" class="text-green-600"></lucide-icon>
            </div>
            <div>
              <p class="font-medium text-gray-800">Ver Organizaciones</p>
              <p class="text-sm text-gray-500">Gestionar JASS existentes</p>
            </div>
          </a>
        </div>
      </div>
    </div>
  `
})
export class SuperAdminDashboardComponent implements OnInit {
     private http = inject(HttpClient);

     organizationsCount = signal(0);
     adminsCount = signal(0);

     buildingIcon = Building2;
     usersIcon = Users;
     plusIcon = Plus;

     ngOnInit(): void {
          this.loadStats();
     }

     private loadStats(): void {
          this.http.get<ApiResponse<Organization[]>>(`${environment.apiUrl}/organizations`).subscribe({
               next: res => this.organizationsCount.set(res.data.length)
          });
     }
}
