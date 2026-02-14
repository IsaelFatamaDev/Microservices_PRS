import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, AlertCircle, Search, ChevronLeft, ChevronRight, Eye,
  X, DollarSign, Calendar, Filter, Trash2, RefreshCw, Plus
} from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { CommercialService } from '../../../../core/services/commercial.service';
import { ApiResponse, User } from '../../../../core/models';
import { Debt, CreateDebtRequest, DebtStatus } from '../../../../core/models/commercial.model';

@Component({
  selector: 'app-debts',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Deudas</h1>
          <p class="text-sm text-gray-500 mt-1">Control de deudas pendientes por usuarios</p>
        </div>
        <button (click)="showCreateModal.set(true)"
                class="px-4 py-2 bg-violet-600 text-white rounded-xl text-sm font-medium hover:bg-violet-700 transition-all flex items-center gap-2 self-start">
          <lucide-icon [img]="plusIcon" [size]="16"></lucide-icon>
          Generar Deuda
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Pendientes</span>
            <div class="p-2 rounded-xl bg-red-50"><lucide-icon [img]="alertIcon" [size]="16" class="text-red-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-red-600">{{ pendingCount() }}</p>
          <p class="text-xs text-gray-400 mt-1">S/ {{ pendingTotal().toFixed(2) }} total</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Parciales</span>
            <div class="p-2 rounded-xl bg-amber-50"><lucide-icon [img]="dollarIcon" [size]="16" class="text-amber-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-amber-600">{{ partialCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Pagadas (mes)</span>
            <div class="p-2 rounded-xl bg-emerald-50"><lucide-icon [img]="dollarIcon" [size]="16" class="text-emerald-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-emerald-600">{{ paidCount() }}</p>
        </div>
      </div>

      <!-- Tabla -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center gap-3">
          <div class="relative flex-1">
            <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
            <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar por usuario..."
                   class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-300">
          </div>
          <select [(ngModel)]="statusFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todos</option>
            <option value="PENDING">Pendiente</option>
            <option value="PARTIAL">Parcial</option>
            <option value="PAID">Pagada</option>
            <option value="CANCELLED">Cancelada</option>
          </select>
        </div>

        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50/80">
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Usuario</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Período</th>
                <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Monto Original</th>
                <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Mora</th>
                <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Pendiente</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Vencimiento</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (d of paginatedDebts(); track d.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3 text-sm text-gray-800">{{ getUserName(d.userId) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ getMonthLabel(d.periodMonth) }} {{ d.periodYear }}</td>
                  <td class="px-4 py-3 text-sm text-gray-700 text-right">S/ {{ d.originalAmount.toFixed(2) }}</td>
                  <td class="px-4 py-3 text-sm text-right" [class]="d.lateFee > 0 ? 'text-red-600 font-medium' : 'text-gray-400'">
                    S/ {{ d.lateFee.toFixed(2) }}
                  </td>
                  <td class="px-4 py-3 text-sm font-semibold text-right" [class]="d.pendingAmount > 0 ? 'text-red-600' : 'text-emerald-600'">
                    S/ {{ d.pendingAmount.toFixed(2) }}
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ d.dueDate | date:'dd/MM/yyyy' }}</td>
                  <td class="px-4 py-3">
                    <span [class]="getDebtStatusBadge(d.debtStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ getDebtStatusLabel(d.debtStatus) }}
                    </span>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="7" class="text-center py-12 text-gray-400 text-sm">No se encontraron deudas</td></tr>
              }
            </tbody>
          </table>
        </div>

        <div class="md:hidden divide-y divide-gray-50">
          @for (d of paginatedDebts(); track d.id) {
            <div class="p-4 space-y-2">
              <div class="flex items-center justify-between">
                <p class="text-sm font-medium text-gray-800">{{ getUserName(d.userId) }}</p>
                <span [class]="getDebtStatusBadge(d.debtStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">
                  {{ getDebtStatusLabel(d.debtStatus) }}
                </span>
              </div>
              <div class="flex items-center justify-between text-xs text-gray-400">
                <span>{{ getMonthLabel(d.periodMonth) }} {{ d.periodYear }} · Vence: {{ d.dueDate | date:'dd/MM/yyyy' }}</span>
                <span class="text-sm font-bold" [class]="d.pendingAmount > 0 ? 'text-red-600' : 'text-emerald-600'">
                  S/ {{ d.pendingAmount.toFixed(2) }}
                </span>
              </div>
            </div>
          } @empty {
            <div class="p-8 text-center text-gray-400 text-sm">No se encontraron deudas</div>
          }
        </div>

        @if (filteredDebts().length > pageSize) {
          <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
            <span>{{ (currentPage() - 1) * pageSize + 1 }} - {{ Math.min(currentPage() * pageSize, filteredDebts().length) }} de {{ filteredDebts().length }}</span>
            <div class="flex gap-1">
              <button (click)="currentPage.set(currentPage() - 1)" [disabled]="currentPage() <= 1"
                      class="p-1.5 rounded-lg hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors">
                <lucide-icon [img]="chevronLeftIcon" [size]="16"></lucide-icon>
              </button>
              <button (click)="currentPage.set(currentPage() + 1)" [disabled]="currentPage() >= totalPages()"
                      class="p-1.5 rounded-lg hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors">
                <lucide-icon [img]="chevronRightIcon" [size]="16"></lucide-icon>
              </button>
            </div>
          </div>
        }
      </div>

      <!-- Modal Crear Deuda -->
      @if (showCreateModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="showCreateModal.set(false)">
          <div class="bg-white rounded-2xl shadow-xl max-w-md w-full" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Generar Deuda</h3>
              <button (click)="showCreateModal.set(false)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <div class="p-6 space-y-4">
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Usuario</label>
                <select [(ngModel)]="newDebt.userId" class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
                  <option value="">Seleccionar usuario</option>
                  @for (u of allUsers(); track u.id) {
                    <option [value]="u.id">{{ u.lastName }}, {{ u.firstName }} - {{ u.documentNumber }}</option>
                  }
                </select>
              </div>
              <div class="grid grid-cols-2 gap-3">
                <div>
                  <label class="text-xs text-gray-500 mb-1 block">Mes</label>
                  <select [(ngModel)]="newDebt.periodMonth" class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
                    @for (m of monthOptions; track m.value) {
                      <option [value]="m.value">{{ m.label }}</option>
                    }
                  </select>
                </div>
                <div>
                  <label class="text-xs text-gray-500 mb-1 block">Año</label>
                  <input type="number" [(ngModel)]="newDebt.periodYear" [min]="2020" [max]="2030"
                         class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
                </div>
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Monto (S/)</label>
                <input type="number" [(ngModel)]="newDebt.originalAmount" min="0.01" step="0.01"
                       class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none" placeholder="0.00">
              </div>
              <button (click)="createDebt()" [disabled]="!newDebt.userId || newDebt.originalAmount <= 0"
                      class="w-full py-2.5 bg-violet-600 text-white rounded-xl font-medium hover:bg-violet-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed">
                Generar Deuda
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class DebtsComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private commercialService = inject(CommercialService);

  alertIcon = AlertCircle; searchIcon = Search; chevronLeftIcon = ChevronLeft;
  chevronRightIcon = ChevronRight; eyeIcon = Eye; xIcon = X; dollarIcon = DollarSign;
  calendarIcon = Calendar; filterIcon = Filter; trashIcon = Trash2; refreshIcon = RefreshCw; plusIcon = Plus;

  Math = Math;

  allDebts = signal<Debt[]>([]);
  allUsers = signal<User[]>([]);
  searchTerm = '';
  statusFilter = '';
  currentPage = signal(1);
  pageSize = 15;
  showCreateModal = signal(false);

  newDebt = { userId: '', periodMonth: new Date().getMonth() + 1, periodYear: new Date().getFullYear(), originalAmount: 0 };

  monthOptions = [
    { value: 1, label: 'Enero' }, { value: 2, label: 'Febrero' }, { value: 3, label: 'Marzo' },
    { value: 4, label: 'Abril' }, { value: 5, label: 'Mayo' }, { value: 6, label: 'Junio' },
    { value: 7, label: 'Julio' }, { value: 8, label: 'Agosto' }, { value: 9, label: 'Septiembre' },
    { value: 10, label: 'Octubre' }, { value: 11, label: 'Noviembre' }, { value: 12, label: 'Diciembre' }
  ];

  pendingCount = computed(() => this.allDebts().filter(d => d.debtStatus === 'PENDING').length);
  partialCount = computed(() => this.allDebts().filter(d => d.debtStatus === 'PARTIAL').length);
  paidCount = computed(() => this.allDebts().filter(d => d.debtStatus === 'PAID').length);
  pendingTotal = computed(() => this.allDebts().filter(d => d.debtStatus === 'PENDING' || d.debtStatus === 'PARTIAL').reduce((s, d) => s + d.pendingAmount, 0));

  filteredDebts = computed(() => {
    let list = this.allDebts();
    if (this.statusFilter) list = list.filter(d => d.debtStatus === this.statusFilter);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(d => this.getUserName(d.userId).toLowerCase().includes(q));
    }
    return list.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredDebts().length / this.pageSize));
  paginatedDebts = computed(() => {
    const s = (this.currentPage() - 1) * this.pageSize;
    return this.filteredDebts().slice(s, s + this.pageSize);
  });

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.http.get<ApiResponse<User[]>>(`${environment.apiUrl}/users`).subscribe({
      next: r => this.allUsers.set((r.data || []).filter(u => u.organizationId === orgId && u.role === 'CLIENT'))
    });

    this.commercialService.getDebts().subscribe({
      next: r => this.allDebts.set((r.data || []).filter(d => d.organizationId === orgId && d.recordStatus === 'ACTIVE'))
    });
  }

  createDebt(): void {
    if (!this.newDebt.userId || this.newDebt.originalAmount <= 0) return;
    const req: CreateDebtRequest = {
      userId: this.newDebt.userId,
      periodMonth: this.newDebt.periodMonth,
      periodYear: this.newDebt.periodYear,
      originalAmount: this.newDebt.originalAmount
    };
    this.alertService.loading('Generando deuda...');
    this.commercialService.createDebt(req).subscribe({
      next: () => {
        this.alertService.close();
        this.alertService.success('Deuda generada');
        this.showCreateModal.set(false);
        this.loadData();
      },
      error: (err) => {
        this.alertService.close();
        this.alertService.error('Error', err?.error?.message || 'Error al generar deuda');
      }
    });
  }

  getUserName(userId: string): string {
    const u = this.allUsers().find(x => x.id === userId);
    return u ? `${u.lastName}, ${u.firstName}` : userId;
  }

  getMonthLabel(m: number): string {
    const months = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    return months[m - 1] || '';
  }

  getDebtStatusLabel(s: string): string {
    const map: Record<string, string> = { PENDING: 'Pendiente', PARTIAL: 'Parcial', PAID: 'Pagada', CANCELLED: 'Cancelada' };
    return map[s] || s;
  }

  getDebtStatusBadge(s: string): string {
    const map: Record<string, string> = {
      PENDING: 'bg-red-50 text-red-600',
      PARTIAL: 'bg-amber-50 text-amber-700',
      PAID: 'bg-emerald-50 text-emerald-700',
      CANCELLED: 'bg-gray-100 text-gray-500'
    };
    return map[s] || 'bg-gray-100 text-gray-600';
  }
}
