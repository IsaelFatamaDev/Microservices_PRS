import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, Bell, Send, AlertCircle, Clock, RefreshCw,
  Search, CheckCircle2, XCircle, Loader2, MessageSquare, Phone,
  RotateCcw, ChevronLeft, ChevronRight, Filter
} from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import { AlertService } from '../../../core/services/alert.service';

interface Notification {
  id: string;
  userId: string;
  phoneNumber: string;
  recipientName: string;
  type: string;
  channel: string;
  status: 'SENT' | 'FAILED' | 'PENDING' | 'RETRYING';
  message: string;
  imageUrl?: string;
  eventSource: string;
  retryCount: number;
  failureReason?: string;
  createdAt: string;
  sentAt?: string;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Notificaciones</h1>
          <p class="text-sm text-gray-500 mt-0.5">Historial de notificaciones enviadas vía WhatsApp</p>
        </div>
        <div class="flex gap-2">
          <button (click)="loadNotifications()"
                  [disabled]="isLoading()"
                  class="inline-flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-xl hover:bg-gray-50 transition-all text-sm font-medium shadow-sm disabled:opacity-50">
            <lucide-icon [img]="refreshIcon" [size]="16" [class]="isLoading() ? 'animate-spin' : ''"></lucide-icon>
            Actualizar
          </button>
          @if (failedCount() > 0) {
            <button (click)="retryAll()"
                    [disabled]="isRetryingAll()"
                    class="inline-flex items-center gap-2 px-4 py-2 bg-amber-600 text-white rounded-xl hover:bg-amber-700 transition-all text-sm font-medium shadow-sm disabled:opacity-50 disabled:cursor-not-allowed">
              @if (isRetryingAll()) {
                <lucide-icon [img]="loaderIcon" [size]="16" class="animate-spin"></lucide-icon>
              } @else {
                <lucide-icon [img]="retryIcon" [size]="16"></lucide-icon>
              }
              Re-enviar Fallidas ({{ failedCount() }})
            </button>
          }
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Total</span>
            <div class="p-1.5 rounded-lg bg-violet-50"><lucide-icon [img]="bellIcon" [size]="14" class="text-violet-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-gray-800">{{ allNotifications().length }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Enviadas</span>
            <div class="p-1.5 rounded-lg bg-emerald-50"><lucide-icon [img]="checkIcon" [size]="14" class="text-emerald-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-emerald-600">{{ sentCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Fallidas</span>
            <div class="p-1.5 rounded-lg bg-red-50"><lucide-icon [img]="failedIcon" [size]="14" class="text-red-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-red-600">{{ failedCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Pendientes</span>
            <div class="p-1.5 rounded-lg bg-amber-50"><lucide-icon [img]="pendingIcon" [size]="14" class="text-amber-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-amber-600">{{ pendingCount() }}</p>
        </div>
      </div>

      <!-- Tabla -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <!-- Filtros -->
        <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row gap-3">
          <div class="relative flex-1">
            <lucide-icon [img]="searchIcon" [size]="15" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
            <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar por destinatario, teléfono..."
                   class="w-full pl-9 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-300">
          </div>
          <div class="flex gap-1.5 flex-wrap">
            @for (tab of statusTabs; track tab.value) {
              <button (click)="activeStatus = tab.value"
                      [class]="activeStatus === tab.value ? tab.activeClass : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
                      class="px-3 py-1.5 rounded-lg text-xs font-semibold transition-all">
                {{ tab.label }}
              </button>
            }
          </div>
        </div>

        @if (isLoading()) {
          <div class="p-16 text-center">
            <div class="inline-flex items-center gap-2.5 text-gray-400">
              <lucide-icon [img]="loaderIcon" [size]="20" class="animate-spin"></lucide-icon>
              <span class="text-sm">Cargando notificaciones...</span>
            </div>
          </div>
        } @else if (filteredNotifications().length === 0) {
          <div class="p-16 text-center">
            <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-3">
              <lucide-icon [img]="bellIcon" [size]="28" class="text-gray-350"></lucide-icon>
            </div>
            <p class="text-gray-500 font-medium">No hay notificaciones</p>
            <p class="text-sm text-gray-400 mt-1">{{ allNotifications().length === 0 ? 'Aún no se han enviado notificaciones' : 'No coincide con el filtro aplicado' }}</p>
          </div>
        } @else {
          <!-- Desktop table -->
          <div class="hidden md:block overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50/80 border-b border-gray-100">
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">#</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Destinatario</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Tipo</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Origen</th>
                  <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Estado</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Fecha</th>
                  <th class="px-4 py-3 text-right text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Acc.</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (n of paginatedNotifications(); track n.id; let i = $index) {
                  <tr class="hover:bg-gray-50/40 transition-colors">
                    <td class="px-4 py-3">
                      <span class="inline-flex items-center justify-center w-6 h-6 bg-violet-50 text-violet-600 rounded-md text-xs font-bold">
                        {{ (currentPage() - 1) * pageSize + i + 1 }}
                      </span>
                    </td>
                    <td class="px-4 py-3">
                      <p class="text-sm font-medium text-gray-800">{{ n.recipientName || 'Sin nombre' }}</p>
                      <p class="text-xs text-gray-400 flex items-center gap-1 mt-0.5">
                        <lucide-icon [img]="phoneIcon" [size]="11"></lucide-icon>
                        {{ n.phoneNumber || '—' }}
                      </p>
                    </td>
                    <td class="px-4 py-3">
                      <span class="inline-flex px-2 py-0.5 bg-slate-100 text-slate-700 rounded text-xs font-medium">
                        {{ getTypeLabel(n.type) }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-xs text-gray-500">{{ n.eventSource || '—' }}</td>
                    <td class="px-4 py-3 text-center">
                      <span [class]="getStatusBadge(n.status)" class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-semibold">
                        {{ getStatusLabel(n.status) }}
                      </span>
                      @if (n.retryCount > 0) {
                        <p class="text-[10px] text-gray-400 mt-0.5">{{ n.retryCount }} reintento(s)</p>
                      }
                    </td>
                    <td class="px-4 py-3 text-xs text-gray-500">
                      {{ (n.sentAt || n.createdAt) | date:'dd/MM/yy HH:mm' }}
                    </td>
                    <td class="px-4 py-3 text-right">
                      @if (n.status === 'FAILED') {
                        <button (click)="retryOne(n)"
                                [disabled]="retryingId() === n.id"
                                class="p-1.5 text-amber-600 hover:bg-amber-50 rounded-lg transition-colors disabled:opacity-50" title="Re-enviar">
                          @if (retryingId() === n.id) {
                            <lucide-icon [img]="loaderIcon" [size]="15" class="animate-spin"></lucide-icon>
                          } @else {
                            <lucide-icon [img]="retryIcon" [size]="15"></lucide-icon>
                          }
                        </button>
                      }
                      @if (n.failureReason) {
                        <button class="p-1.5 text-red-400 hover:bg-red-50 rounded-lg transition-colors ml-0.5"
                                [title]="n.failureReason">
                          <lucide-icon [img]="failedIcon" [size]="15"></lucide-icon>
                        </button>
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <!-- Mobile cards -->
          <div class="md:hidden divide-y divide-gray-50">
            @for (n of paginatedNotifications(); track n.id; let i = $index) {
              <div class="p-4 space-y-2">
                <div class="flex items-start justify-between gap-2">
                  <div class="flex items-center gap-2">
                    <span class="inline-flex items-center justify-center w-5 h-5 bg-violet-50 text-violet-600 rounded text-[10px] font-bold">
                      {{ (currentPage() - 1) * pageSize + i + 1 }}
                    </span>
                    <p class="text-sm font-medium text-gray-800">{{ n.recipientName || 'Sin nombre' }}</p>
                  </div>
                  <span [class]="getStatusBadge(n.status)" class="inline-flex px-2 py-0.5 rounded-full text-xs font-semibold shrink-0">
                    {{ getStatusLabel(n.status) }}
                  </span>
                </div>
                <div class="flex items-center gap-3 text-xs text-gray-400">
                  <span>{{ n.phoneNumber || '—' }}</span>
                  <span>·</span>
                  <span class="px-1.5 py-0.5 bg-slate-100 text-slate-600 rounded font-medium">{{ getTypeLabel(n.type) }}</span>
                  <span>·</span>
                  <span>{{ (n.sentAt || n.createdAt) | date:'dd/MM/yy' }}</span>
                </div>
                @if (n.failureReason) {
                  <p class="text-xs text-red-500 bg-red-50 rounded-lg px-2 py-1">{{ n.failureReason }}</p>
                }
                @if (n.status === 'FAILED') {
                  <button (click)="retryOne(n)" [disabled]="retryingId() === n.id"
                          class="text-xs text-amber-600 font-medium flex items-center gap-1 disabled:opacity-50">
                    <lucide-icon [img]="retryIcon" [size]="12"></lucide-icon> Re-enviar
                  </button>
                }
              </div>
            }
          </div>

          <!-- Paginación -->
          @if (filteredNotifications().length > pageSize) {
            <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
              <span>{{ (currentPage() - 1) * pageSize + 1 }}–{{ Math.min(currentPage() * pageSize, filteredNotifications().length) }} de {{ filteredNotifications().length }}</span>
              <div class="flex gap-1">
                <button (click)="prevPage()" [disabled]="currentPage() <= 1"
                        class="p-1.5 rounded-lg hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors">
                  <lucide-icon [img]="chevronLeftIcon" [size]="16"></lucide-icon>
                </button>
                <button (click)="nextPage()" [disabled]="currentPage() >= totalPages()"
                        class="p-1.5 rounded-lg hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors">
                  <lucide-icon [img]="chevronRightIcon" [size]="16"></lucide-icon>
                </button>
              </div>
            </div>
          }
        }
      </div>
    </div>
  `
})
export class NotificationsComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private alertService = inject(AlertService);

  Math = Math;

  allNotifications = signal<Notification[]>([]);
  isLoading = signal(false);
  isRetryingAll = signal(false);
  retryingId = signal<string | null>(null);

  searchTerm = '';
  activeStatus = 'ALL';
  currentPage = signal(1);
  pageSize = 15;

  bellIcon = Bell;
  sendIcon = Send;
  checkIcon = CheckCircle2;
  failedIcon = XCircle;
  pendingIcon = Clock;
  refreshIcon = RefreshCw;
  loaderIcon = Loader2;
  retryIcon = RotateCcw;
  searchIcon = Search;
  msgIcon = MessageSquare;
  phoneIcon = Phone;
  chevronLeftIcon = ChevronLeft;
  chevronRightIcon = ChevronRight;
  filterIcon = Filter;
  alertIcon = AlertCircle;

  statusTabs = [
    { value: 'ALL', label: 'Todas', activeClass: 'bg-violet-600 text-white' },
    { value: 'SENT', label: 'Enviadas', activeClass: 'bg-emerald-600 text-white' },
    { value: 'FAILED', label: 'Fallidas', activeClass: 'bg-red-600 text-white' },
    { value: 'PENDING', label: 'Pendientes', activeClass: 'bg-amber-500 text-white' },
    { value: 'RETRYING', label: 'Reintentando', activeClass: 'bg-blue-600 text-white' },
  ];

  sentCount = computed(() => this.allNotifications().filter(n => n.status === 'SENT').length);
  failedCount = computed(() => this.allNotifications().filter(n => n.status === 'FAILED').length);
  pendingCount = computed(() => this.allNotifications().filter(n => n.status === 'PENDING' || n.status === 'RETRYING').length);

  filteredNotifications = computed(() => {
    let list = this.allNotifications();
    if (this.activeStatus !== 'ALL') list = list.filter(n => n.status === this.activeStatus);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(n =>
        (n.recipientName || '').toLowerCase().includes(q) ||
        (n.phoneNumber || '').includes(q) ||
        (n.type || '').toLowerCase().includes(q)
      );
    }
    return list.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredNotifications().length / this.pageSize));
  paginatedNotifications = computed(() => {
    const s = (this.currentPage() - 1) * this.pageSize;
    return this.filteredNotifications().slice(s, s + this.pageSize);
  });

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading.set(true);
    this.http.get<ApiResponse<Notification[]>>(`${environment.apiUrl}/notifications`).subscribe({
      next: res => {
        this.allNotifications.set(res.data || []);
        this.isLoading.set(false);
        this.currentPage.set(1);
      },
      error: () => {
        this.isLoading.set(false);
        this.alertService.error('Error', 'No se pudieron cargar las notificaciones');
      }
    });
  }

  retryOne(n: Notification): void {
    this.retryingId.set(n.id);
    this.http.post<ApiResponse<Notification>>(`${environment.apiUrl}/notifications/${n.id}/retry`, {}).subscribe({
      next: res => {
        this.retryingId.set(null);
        this.alertService.success('Re-enviado', 'Notificación re-enviada correctamente');
        if (res.data) {
          this.allNotifications.update(list =>
            list.map(item => item.id === n.id ? res.data : item)
          );
        }
      },
      error: () => {
        this.retryingId.set(null);
        this.alertService.error('Error', 'No se pudo re-enviar la notificación');
      }
    });
  }

  async retryAll(): Promise<void> {
    const result = await this.alertService.confirm(
      '¿Re-enviar todas las fallidas?',
      `Se intentará re-enviar ${this.failedCount()} notificación(es) fallida(s).`,
      'Re-enviar todas',
      'Cancelar'
    );
    if (!result.isConfirmed) return;

    this.isRetryingAll.set(true);
    this.http.post<ApiResponse<Notification[]>>(`${environment.apiUrl}/notifications/retry-all`, {}).subscribe({
      next: res => {
        this.isRetryingAll.set(false);
        const count = res.data?.length || 0;
        this.alertService.success('Completado', `Se procesaron ${count} notificaciones`);
        this.loadNotifications();
      },
      error: () => {
        this.isRetryingAll.set(false);
        this.alertService.error('Error', 'No se pudo ejecutar el reenvío masivo');
      }
    });
  }

  prevPage(): void { this.currentPage.update(p => p - 1); }
  nextPage(): void { this.currentPage.update(p => p + 1); }

  getTypeLabel(type: string): string {
    const map: Record<string, string> = {
      WELCOME: 'Bienvenida',
      PROFILE_UPDATED: 'Perfil Act.',
      ACCOUNT_DEACTIVATED: 'Cuenta Baja',
      ACCOUNT_RESTORED: 'Cuenta Rest.',
      PAYMENT_RECEIVED: 'Pago',
      PAYMENT_OVERDUE: 'Mora',
      SERVICE_CUT: 'Corte',
      SERVICE_CUT_WARNING: 'Aviso Corte',
      SERVICE_RESTORED: 'Rest. Servicio',
      RECEIPT_GENERATED: 'Recibo',
      CUSTOM: 'Personalizado',
    };
    return map[type] || type;
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      SENT: 'Enviada', FAILED: 'Fallida', PENDING: 'Pendiente', RETRYING: 'Reintentando'
    };
    return map[status] || status;
  }

  getStatusBadge(status: string): string {
    const map: Record<string, string> = {
      SENT: 'bg-emerald-50 text-emerald-700',
      FAILED: 'bg-red-50 text-red-600',
      PENDING: 'bg-amber-50 text-amber-700',
      RETRYING: 'bg-blue-50 text-blue-700',
    };
    return map[status] || 'bg-gray-100 text-gray-600';
  }
}
