import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Building2, Users, Plus, Activity, Server, Database, Shield, Clock } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { Organization, ApiResponse } from '../../../core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-super-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, LucideAngularModule],
  template: `
    <div class="space-y-8">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
        <div>
          <h1 class="text-2xl font-bold text-slate-800 tracking-tight">
            Bienvenido, {{ authService.userFullName() }}
          </h1>
          <p class="text-slate-500 mt-1 flex items-center gap-2">
            <lucide-icon [img]="clockIcon" [size]="16"></lucide-icon>
            {{ currentDate | date:'fullDate' | titlecase }}
          </p>
        </div>
        <div class="flex items-center gap-3">
          <span class="px-3 py-1 bg-blue-50 text-blue-700 text-sm font-medium rounded-full border border-blue-100">
            Super Administrador
          </span>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 hover:shadow-md transition-shadow duration-300">
          <div class="flex items-start justify-between mb-4">
            <div>
              <p class="text-sm font-medium text-slate-500 uppercase tracking-wider">Organizaciones</p>
              <h3 class="text-3xl font-bold text-slate-800 mt-1">{{ organizationsCount() }}</h3>
            </div>
            <div class="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center border border-blue-100">
              <lucide-icon [img]="buildingIcon" [size]="24" class="text-blue-600"></lucide-icon>
            </div>
          </div>
          <div class="flex items-center gap-2 text-sm text-slate-500">
            <span class="text-green-600 flex items-center gap-1 font-medium bg-green-50 px-2 py-0.5 rounded-full">
              <lucide-icon [img]="activityIcon" [size]="14"></lucide-icon>
              Activas
            </span>
            <span>JASS registradas</span>
          </div>
        </div>

        <div class="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 hover:shadow-md transition-shadow duration-300">
          <div class="flex items-start justify-between mb-4">
            <div>
              <p class="text-sm font-medium text-slate-500 uppercase tracking-wider">Administradores</p>
              <h3 class="text-3xl font-bold text-slate-800 mt-1">{{ adminsCount() }}</h3>
            </div>
            <div class="w-12 h-12 bg-indigo-50 rounded-xl flex items-center justify-center border border-indigo-100">
              <lucide-icon [img]="usersIcon" [size]="24" class="text-indigo-600"></lucide-icon>
            </div>
          </div>
          <div class="flex items-center gap-2 text-sm text-slate-500">
            <span class="text-indigo-600 flex items-center gap-1 font-medium bg-indigo-50 px-2 py-0.5 rounded-full">
              <lucide-icon [img]="usersIcon" [size]="14"></lucide-icon>
              Total
            </span>
            <span>Usuarios administrativos</span>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2 space-y-6">
          <h2 class="text-lg font-bold text-slate-800 flex items-center gap-2">
            <lucide-icon [img]="plusIcon" [size]="20" class="text-slate-400"></lucide-icon>
            Acciones Rápidas
          </h2>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <a routerLink="/super-admin/organizations/new"
               class="group p-4 bg-white border border-slate-200 rounded-xl hover:border-blue-400 hover:shadow-md transition-all duration-200 flex items-center gap-4 cursor-pointer">
              <div class="w-12 h-12 bg-blue-50 rounded-full flex items-center justify-center group-hover:bg-blue-600 transition-colors duration-200">
                <lucide-icon [img]="plusIcon" [size]="24" class="text-blue-600 group-hover:text-white transition-colors duration-200"></lucide-icon>
              </div>
              <div>
                <h3 class="font-bold text-slate-800 group-hover:text-blue-700 transition-colors">Nueva Organización</h3>
                <p class="text-sm text-slate-500">Registrar una JASS</p>
              </div>
            </a>

            <a routerLink="/super-admin/organizations"
               class="group p-4 bg-white border border-slate-200 rounded-xl hover:border-slate-400 hover:shadow-md transition-all duration-200 flex items-center gap-4 cursor-pointer">
              <div class="w-12 h-12 bg-slate-50 rounded-full flex items-center justify-center group-hover:bg-slate-800 transition-colors duration-200">
                <lucide-icon [img]="buildingIcon" [size]="24" class="text-slate-600 group-hover:text-white transition-colors duration-200"></lucide-icon>
              </div>
              <div>
                <h3 class="font-bold text-slate-800 group-hover:text-slate-900 transition-colors">Ver Directorio</h3>
                <p class="text-sm text-slate-500">Gestionar existentes</p>
              </div>
            </a>
          </div>
        </div>

        <div class="space-y-6">
          <h2 class="text-lg font-bold text-slate-800 flex items-center gap-2">
            <lucide-icon [img]="serverIcon" [size]="20" class="text-slate-400"></lucide-icon>
            Estado del Sistema
          </h2>

          <div class="bg-white p-5 rounded-2xl shadow-sm border border-slate-200 space-y-4">
            <div class="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
              <div class="flex items-center gap-3">
                <lucide-icon [img]="databaseIcon" [size]="18" class="text-slate-500"></lucide-icon>
                <span class="text-sm font-medium text-slate-700">Base de Datos</span>
              </div>
              <span class="flex items-center gap-1.5 text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-1 rounded-full border border-emerald-100">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                ONLINE
              </span>
            </div>

            <div class="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
              <div class="flex items-center gap-3">
                <lucide-icon [img]="shieldIcon" [size]="18" class="text-slate-500"></lucide-icon>
                <span class="text-sm font-medium text-slate-700">Auth Service</span>
              </div>
              <span class="flex items-center gap-1.5 text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-1 rounded-full border border-emerald-100">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                ONLINE
              </span>
            </div>

            <div class="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
              <div class="flex items-center gap-3">
                <lucide-icon [img]="serverIcon" [size]="18" class="text-slate-500"></lucide-icon>
                <span class="text-sm font-medium text-slate-700">Gateway</span>
              </div>
              <span class="flex items-center gap-1.5 text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-1 rounded-full border border-emerald-100">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                ONLINE
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SuperAdminDashboardComponent implements OnInit {
  private http = inject(HttpClient);
  authService = inject(AuthService);

  organizationsCount = signal(0);
  adminsCount = signal(0);
  currentDate = new Date();

  buildingIcon = Building2;
  usersIcon = Users;
  plusIcon = Plus;
  activityIcon = Activity;
  serverIcon = Server;
  databaseIcon = Database;
  shieldIcon = Shield;
  clockIcon = Clock;

  ngOnInit(): void {
    this.loadStats();
  }

  private loadStats(): void {
    this.http.get<ApiResponse<Organization[]>>(`${environment.apiUrl}/organizations`).subscribe({
      next: res => this.organizationsCount.set(res.data.length)
    });

    // In a real app we might have a separate endpoint for admin count,
    // for now we'll leave it as 0 or implement a fetch if available.
    // this.adminsCount.set(0);
  }
}
