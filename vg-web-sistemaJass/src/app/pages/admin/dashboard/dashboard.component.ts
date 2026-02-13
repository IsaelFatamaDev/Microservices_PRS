import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Users, DollarSign, Droplets, AlertCircle, TrendingUp, TrendingDown, Calendar } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import { ApiResponse } from '../../../core';

interface DashboardStats {
     totalUsers: number;
     activeUsers: number;
     totalReceipts: number;
     pendingPayments: number;
     totalWaterBoxes: number;
     activeComplaints: number;
     monthlyIncome: number;
     monthlyExpenses: number;
}

@Component({
     selector: 'app-admin-dashboard',
     standalone: true,
     imports: [CommonModule, RouterLink, LucideAngularModule],
     template: `
    <div>
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Dashboard</h1>
          <p class="text-gray-500">Bienvenido, {{ authService.userFullName() }}</p>
        </div>
        <div class="flex items-center gap-2 text-sm text-gray-500">
          <lucide-icon [img]="calendarIcon" [size]="16"></lucide-icon>
          {{ today }}
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="bg-white rounded-xl shadow-sm p-5">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Usuarios Activos</p>
              <p class="text-2xl font-bold text-gray-800 mt-1">{{ stats().activeUsers }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
              <lucide-icon [img]="usersIcon" [size]="24" class="text-blue-600"></lucide-icon>
            </div>
          </div>
          <a routerLink="/admin/users" class="inline-block mt-3 text-sm text-blue-600 hover:underline">Ver usuarios →</a>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-5">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Cajas de Agua</p>
              <p class="text-2xl font-bold text-gray-800 mt-1">{{ stats().totalWaterBoxes }}</p>
            </div>
            <div class="w-12 h-12 bg-cyan-100 rounded-xl flex items-center justify-center">
              <lucide-icon [img]="dropletsIcon" [size]="24" class="text-cyan-600"></lucide-icon>
            </div>
          </div>
          <a routerLink="/admin/infrastructure/water-boxes" class="inline-block mt-3 text-sm text-cyan-600 hover:underline">Ver cajas →</a>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-5">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Pagos Pendientes</p>
              <p class="text-2xl font-bold text-gray-800 mt-1">{{ stats().pendingPayments }}</p>
            </div>
            <div class="w-12 h-12 bg-amber-100 rounded-xl flex items-center justify-center">
              <lucide-icon [img]="dollarIcon" [size]="24" class="text-amber-600"></lucide-icon>
            </div>
          </div>
          <a routerLink="/admin/commercial/debts" class="inline-block mt-3 text-sm text-amber-600 hover:underline">Ver deudas →</a>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-5">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Quejas Activas</p>
              <p class="text-2xl font-bold text-gray-800 mt-1">{{ stats().activeComplaints }}</p>
            </div>
            <div class="w-12 h-12 bg-red-100 rounded-xl flex items-center justify-center">
              <lucide-icon [img]="alertIcon" [size]="24" class="text-red-600"></lucide-icon>
            </div>
          </div>
          <a routerLink="/admin/claims/complaints" class="inline-block mt-3 text-sm text-red-600 hover:underline">Ver quejas →</a>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">Resumen Financiero del Mes</h2>
          <div class="space-y-4">
            <div class="flex items-center justify-between p-4 bg-green-50 rounded-lg">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                  <lucide-icon [img]="trendingUpIcon" [size]="20" class="text-green-600"></lucide-icon>
                </div>
                <div>
                  <p class="text-sm text-gray-500">Ingresos</p>
                  <p class="font-semibold text-gray-800">S/ {{ stats().monthlyIncome.toFixed(2) }}</p>
                </div>
              </div>
            </div>
            <div class="flex items-center justify-between p-4 bg-red-50 rounded-lg">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
                  <lucide-icon [img]="trendingDownIcon" [size]="20" class="text-red-600"></lucide-icon>
                </div>
                <div>
                  <p class="text-sm text-gray-500">Egresos</p>
                  <p class="font-semibold text-gray-800">S/ {{ stats().monthlyExpenses.toFixed(2) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-4">Acciones Rápidas</h2>
          <div class="grid grid-cols-2 gap-3">
            <a routerLink="/admin/users/new" class="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 hover:border-blue-300 transition-colors text-center">
              <lucide-icon [img]="usersIcon" [size]="24" class="text-blue-600 mx-auto mb-2"></lucide-icon>
              <p class="text-sm font-medium text-gray-700">Nuevo Usuario</p>
            </a>
            <a routerLink="/admin/commercial/payments" class="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 hover:border-green-300 transition-colors text-center">
              <lucide-icon [img]="dollarIcon" [size]="24" class="text-green-600 mx-auto mb-2"></lucide-icon>
              <p class="text-sm font-medium text-gray-700">Registrar Pago</p>
            </a>
            <a routerLink="/admin/claims/complaints" class="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 hover:border-amber-300 transition-colors text-center">
              <lucide-icon [img]="alertIcon" [size]="24" class="text-amber-600 mx-auto mb-2"></lucide-icon>
              <p class="text-sm font-medium text-gray-700">Ver Quejas</p>
            </a>
            <a routerLink="/admin/reports" class="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 hover:border-purple-300 transition-colors text-center">
              <lucide-icon [img]="trendingUpIcon" [size]="24" class="text-purple-600 mx-auto mb-2"></lucide-icon>
              <p class="text-sm font-medium text-gray-700">Reportes</p>
            </a>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AdminDashboardComponent implements OnInit {
     private http = inject(HttpClient);
     authService = inject(AuthService);

     stats = signal<DashboardStats>({
          totalUsers: 0,
          activeUsers: 0,
          totalReceipts: 0,
          pendingPayments: 0,
          totalWaterBoxes: 0,
          activeComplaints: 0,
          monthlyIncome: 0,
          monthlyExpenses: 0
     });

     today = new Date().toLocaleDateString('es-PE', {
          weekday: 'long',
          year: 'numeric',
          month: 'long',
          day: 'numeric'
     });

     usersIcon = Users;
     dollarIcon = DollarSign;
     dropletsIcon = Droplets;
     alertIcon = AlertCircle;
     trendingUpIcon = TrendingUp;
     trendingDownIcon = TrendingDown;
     calendarIcon = Calendar;

     ngOnInit(): void {
          this.loadStats();
     }

     private loadStats(): void {
          this.http.get<ApiResponse<DashboardStats>>(`${environment.apiUrl}/dashboard/admin`).subscribe({
               next: res => this.stats.set(res.data),
               error: () => { }
          });
     }
}
