import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, Scissors, Search, ChevronLeft, ChevronRight, X, Plus,
  Zap, RefreshCw, AlertTriangle, CheckCircle, Clock
} from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { CommercialService } from '../../../../core/services/commercial.service';
import { ApiResponse, User, WaterBox, WaterBoxAssignment } from '../../../../core/models';
import { ServiceCut, CreateServiceCutRequest, CutStatus, CutReason } from '../../../../core/models/commercial.model';

@Component({
  selector: 'app-cuts',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Cortes de Servicio</h1>
          <p class="text-sm text-gray-500 mt-1">Gestión de cortes y reconexiones</p>
        </div>
        <button (click)="openCreateModal()" class="inline-flex items-center gap-2 px-4 py-2.5 bg-gradient-to-r from-violet-600 to-violet-700 text-white rounded-xl text-sm font-medium shadow-lg shadow-violet-200 hover:shadow-violet-300 hover:from-violet-700 hover:to-violet-800 transition-all">
          <lucide-icon [img]="plusIcon" [size]="16"></lucide-icon>Programar corte
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Pendientes</span>
            <div class="p-2 rounded-xl bg-amber-50"><lucide-icon [img]="clockIcon" [size]="16" class="text-amber-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-amber-600">{{ pendingCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Ejecutados</span>
            <div class="p-2 rounded-xl bg-red-50"><lucide-icon [img]="zapIcon" [size]="16" class="text-red-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-red-600">{{ executedCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Reconectados</span>
            <div class="p-2 rounded-xl bg-emerald-50"><lucide-icon [img]="checkIcon" [size]="16" class="text-emerald-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-emerald-600">{{ reconnectedCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Total</span>
            <div class="p-2 rounded-xl bg-violet-50"><lucide-icon [img]="scissorsIcon" [size]="16" class="text-violet-600"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-gray-800">{{ allCuts().length }}</p>
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
            <option value="EXECUTED">Ejecutado</option>
            <option value="RECONNECTED">Reconectado</option>
            <option value="CANCELLED">Cancelado</option>
          </select>
        </div>

        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50/80">
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Usuario</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Caja de Agua</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Razón</th>
                <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Deuda</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Programado</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (c of paginatedCuts(); track c.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3">
                    <p class="text-sm font-medium text-gray-800">{{ getUserName(c.userId) }}</p>
                    @if (c.userFullName) { <p class="text-xs text-gray-400">{{ c.userFullName }}</p> }
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ getBoxCode(c.waterBoxId) }}</td>
                  <td class="px-4 py-3">
                    <span [class]="getReasonBadge(c.cutReason)" class="text-xs px-2 py-1 rounded-lg font-medium">{{ getReasonLabel(c.cutReason) }}</span>
                  </td>
                  <td class="px-4 py-3 text-sm font-medium text-gray-800 text-right">S/ {{ c.debtAmount.toFixed(2) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ c.scheduledDate | date:'dd/MM/yyyy' }}</td>
                  <td class="px-4 py-3">
                    <span [class]="getStatusBadge(c.cutStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">{{ getStatusLabel(c.cutStatus) }}</span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      @if (c.cutStatus === 'PENDING') {
                        <button (click)="executeCut(c)" class="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-600 transition-colors" title="Ejecutar corte">
                          <lucide-icon [img]="zapIcon" [size]="15"></lucide-icon>
                        </button>
                      }
                      @if (c.cutStatus === 'EXECUTED') {
                        <button (click)="reconnectCut(c)" class="p-1.5 rounded-lg hover:bg-emerald-50 text-gray-400 hover:text-emerald-600 transition-colors" title="Reconectar">
                          <lucide-icon [img]="refreshIcon" [size]="15"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="7" class="text-center py-12 text-gray-400 text-sm">No se encontraron cortes</td></tr>
              }
            </tbody>
          </table>
        </div>

        <div class="md:hidden divide-y divide-gray-50">
          @for (c of paginatedCuts(); track c.id) {
            <div class="p-4 space-y-2">
              <div class="flex items-center justify-between">
                <p class="text-sm font-medium text-gray-800">{{ getUserName(c.userId) }}</p>
                <span [class]="getStatusBadge(c.cutStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">{{ getStatusLabel(c.cutStatus) }}</span>
              </div>
              <div class="flex items-center justify-between text-xs text-gray-400">
                <span>{{ getBoxCode(c.waterBoxId) }} · {{ getReasonLabel(c.cutReason) }}</span>
                <span class="text-sm font-bold text-gray-800">S/ {{ c.debtAmount.toFixed(2) }}</span>
              </div>
              <p class="text-xs text-gray-400">Programado: {{ c.scheduledDate | date:'dd/MM/yyyy' }}
                @if (c.executedDate) { · Ejecutado: {{ c.executedDate | date:'dd/MM/yyyy' }} }
              </p>
              <div class="flex gap-2 pt-1">
                @if (c.cutStatus === 'PENDING') {
                  <button (click)="executeCut(c)" class="flex-1 py-1.5 text-xs border border-red-200 rounded-lg text-red-600 hover:bg-red-50 transition-colors">Ejecutar corte</button>
                }
                @if (c.cutStatus === 'EXECUTED') {
                  <button (click)="reconnectCut(c)" class="flex-1 py-1.5 text-xs border border-emerald-200 rounded-lg text-emerald-600 hover:bg-emerald-50 transition-colors">Reconectar</button>
                }
              </div>
            </div>
          } @empty {
            <div class="p-8 text-center text-gray-400 text-sm">No se encontraron cortes</div>
          }
        </div>

        @if (filteredCuts().length > pageSize) {
          <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
            <span>{{ (currentPage() - 1) * pageSize + 1 }} - {{ Math.min(currentPage() * pageSize, filteredCuts().length) }} de {{ filteredCuts().length }}</span>
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

      <!-- Modal crear corte -->
      @if (showCreateModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="showCreateModal.set(false)">
          <div class="bg-white rounded-2xl shadow-xl max-w-md w-full" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Programar Corte de Servicio</h3>
              <button (click)="showCreateModal.set(false)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <div class="p-6 space-y-4">
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">Usuario</label>
                <select [(ngModel)]="newCut.userId" (ngModelChange)="onUserSelected()" class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                  <option value="">Seleccionar usuario</option>
                  @for (u of clientUsers(); track u.id) {
                    <option [value]="u.id">{{ u.lastName }}, {{ u.firstName }} - {{ u.documentNumber }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">Caja de Agua</label>
                <select [(ngModel)]="newCut.waterBoxId" class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                  <option value="">Seleccionar caja</option>
                  @for (b of userBoxes(); track b.id) {
                    <option [value]="b.id">{{ b.boxCode }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">Razón</label>
                <select [(ngModel)]="newCut.cutReason" class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                  <option value="">Seleccionar razón</option>
                  <option value="NON_PAYMENT">Falta de pago</option>
                  <option value="VIOLATION">Infracción</option>
                  <option value="MAINTENANCE">Mantenimiento</option>
                  <option value="USER_REQUEST">Solicitud del usuario</option>
                </select>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">Fecha programada</label>
                <input type="date" [(ngModel)]="newCut.scheduledDate" class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">Monto de deuda (S/)</label>
                <input type="number" [(ngModel)]="newCut.debtAmount" step="0.01" min="0" placeholder="0.00"
                       class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 placeholder:text-gray-300">
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-500 mb-1">Notas (opcional)</label>
                <textarea [(ngModel)]="newCut.notes" rows="2" placeholder="Observaciones..."
                          class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 resize-none placeholder:text-gray-300"></textarea>
              </div>
            </div>
            <div class="p-6 border-t border-gray-100 flex gap-3 justify-end">
              <button (click)="showCreateModal.set(false)" class="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-xl transition-colors">Cancelar</button>
              <button (click)="createCut()" [disabled]="!newCut.userId || !newCut.waterBoxId || !newCut.cutReason || !newCut.scheduledDate"
                      class="px-5 py-2 bg-gradient-to-r from-violet-600 to-violet-700 text-white rounded-xl text-sm font-medium shadow-lg shadow-violet-200 hover:shadow-violet-300 transition-all disabled:opacity-50 disabled:cursor-not-allowed">
                Programar corte
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class CutsComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private commercialService = inject(CommercialService);

  scissorsIcon = Scissors; searchIcon = Search; chevronLeftIcon = ChevronLeft;
  chevronRightIcon = ChevronRight; xIcon = X; plusIcon = Plus; zapIcon = Zap;
  refreshIcon = RefreshCw; alertIcon = AlertTriangle; checkIcon = CheckCircle; clockIcon = Clock;

  Math = Math;

  allCuts = signal<ServiceCut[]>([]);
  allUsers = signal<User[]>([]);
  allBoxes = signal<WaterBox[]>([]);
  allAssignments = signal<WaterBoxAssignment[]>([]);
  searchTerm = '';
  statusFilter = '';
  currentPage = signal(1);
  pageSize = 15;

  showCreateModal = signal(false);
  newCut = { userId: '', waterBoxId: '', cutReason: '', scheduledDate: '', debtAmount: 0, notes: '' };

  clientUsers = computed(() => this.allUsers().filter(u => u.role === 'CLIENT'));
  userBoxes = signal<WaterBox[]>([]);

  pendingCount = computed(() => this.allCuts().filter(c => c.cutStatus === 'PENDING').length);
  executedCount = computed(() => this.allCuts().filter(c => c.cutStatus === 'EXECUTED').length);
  reconnectedCount = computed(() => this.allCuts().filter(c => c.cutStatus === 'RECONNECTED').length);

  filteredCuts = computed(() => {
    let list = this.allCuts();
    if (this.statusFilter) list = list.filter(c => c.cutStatus === this.statusFilter);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(c =>
        this.getUserName(c.userId).toLowerCase().includes(q) ||
        this.getBoxCode(c.waterBoxId).toLowerCase().includes(q)
      );
    }
    return list.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredCuts().length / this.pageSize));
  paginatedCuts = computed(() => {
    const s = (this.currentPage() - 1) * this.pageSize;
    return this.filteredCuts().slice(s, s + this.pageSize);
  });

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.http.get<ApiResponse<User[]>>(`${environment.apiUrl}/users`).subscribe({
      next: r => this.allUsers.set((r.data || []).filter(u => u.organizationId === orgId))
    });

    this.http.get<ApiResponse<WaterBox[]>>(`${environment.apiUrl}/water-boxes`).subscribe({
      next: r => this.allBoxes.set((r.data || []).filter(b => b.organizationId === orgId && b.isActive))
    });

    this.http.get<ApiResponse<WaterBoxAssignment[]>>(`${environment.apiUrl}/water-box-assignments`).subscribe({
      next: r => this.allAssignments.set((r.data || []).filter(a => a.organizationId === orgId && a.assignmentStatus === 'ACTIVE'))
    });

    this.commercialService.getServiceCuts().subscribe({
      next: r => this.allCuts.set((r.data || []).filter(c => c.organizationId === orgId && c.recordStatus === 'ACTIVE'))
    });
  }

  onUserSelected(): void {
    if (!this.newCut.userId) { this.userBoxes.set([]); return; }
    const assignedBoxIds = this.allAssignments()
      .filter(a => a.userId === this.newCut.userId)
      .map(a => a.waterBoxId);
    this.userBoxes.set(this.allBoxes().filter(b => assignedBoxIds.includes(b.id)));
    this.newCut.waterBoxId = '';
  }

  openCreateModal(): void {
    this.newCut = { userId: '', waterBoxId: '', cutReason: '', scheduledDate: '', debtAmount: 0, notes: '' };
    this.userBoxes.set([]);
    this.showCreateModal.set(true);
  }

  createCut(): void {
    this.alertService.loading('Programando corte...');
    const req: CreateServiceCutRequest = {
      userId: this.newCut.userId,
      waterBoxId: this.newCut.waterBoxId,
      scheduledDate: this.newCut.scheduledDate + 'T00:00:00',
      cutReason: this.newCut.cutReason,
      debtAmount: this.newCut.debtAmount || undefined,
      notes: this.newCut.notes || undefined
    };
    this.commercialService.createServiceCut(req).subscribe({
      next: () => { this.alertService.close(); this.alertService.success('Corte programado', 'Se registró correctamente'); this.showCreateModal.set(false); this.loadData(); },
      error: (e) => { this.alertService.close(); this.alertService.error('Error', e?.error?.message || 'No se pudo registrar'); }
    });
  }

  executeCut(cut: ServiceCut): void {
    this.alertService.confirm('¿Ejecutar corte?', `Se cortará el servicio a ${this.getUserName(cut.userId)}`).then((r: any) => {
      if (!r.isConfirmed) return;
      this.alertService.loading('Ejecutando corte...');
      this.commercialService.executeServiceCut(cut.id).subscribe({
        next: () => { this.alertService.close(); this.alertService.success('Corte ejecutado', 'Servicio cortado'); this.loadData(); },
        error: (e) => { this.alertService.close(); this.alertService.error('Error', e?.error?.message || 'No se pudo ejecutar'); }
      });
    });
  }

  reconnectCut(cut: ServiceCut): void {
    this.alertService.confirm('¿Reconectar servicio?', `Se reconectará el servicio a ${this.getUserName(cut.userId)}`).then((r: any) => {
      if (!r.isConfirmed) return;
      this.alertService.loading('Reconectando...');
      this.commercialService.reconnectServiceCut(cut.id).subscribe({
        next: () => { this.alertService.close(); this.alertService.success('Servicio reconectado', 'Reconexión exitosa'); this.loadData(); },
        error: (e) => { this.alertService.close(); this.alertService.error('Error', e?.error?.message || 'No se pudo reconectar'); }
      });
    });
  }

  getUserName(userId: string): string {
    const u = this.allUsers().find(x => x.id === userId);
    return u ? `${u.lastName}, ${u.firstName}` : userId;
  }

  getBoxCode(boxId: string): string {
    const b = this.allBoxes().find(x => x.id === boxId);
    return b ? b.boxCode : boxId;
  }

  getStatusLabel(s: string): string {
    const map: Record<string, string> = { PENDING: 'Pendiente', EXECUTED: 'Ejecutado', RECONNECTED: 'Reconectado', CANCELLED: 'Cancelado' };
    return map[s] || s;
  }

  getStatusBadge(s: string): string {
    const map: Record<string, string> = {
      PENDING: 'bg-amber-50 text-amber-700',
      EXECUTED: 'bg-red-50 text-red-600',
      RECONNECTED: 'bg-emerald-50 text-emerald-700',
      CANCELLED: 'bg-gray-100 text-gray-500'
    };
    return map[s] || 'bg-gray-100 text-gray-600';
  }

  getReasonLabel(r: string): string {
    const map: Record<string, string> = { NON_PAYMENT: 'Falta de pago', VIOLATION: 'Infracción', MAINTENANCE: 'Mantenimiento', USER_REQUEST: 'Solicitud' };
    return map[r] || r;
  }

  getReasonBadge(r: string): string {
    const map: Record<string, string> = {
      NON_PAYMENT: 'bg-red-50 text-red-600',
      VIOLATION: 'bg-orange-50 text-orange-600',
      MAINTENANCE: 'bg-blue-50 text-blue-600',
      USER_REQUEST: 'bg-gray-100 text-gray-600'
    };
    return map[r] || 'bg-gray-100 text-gray-600';
  }
}
