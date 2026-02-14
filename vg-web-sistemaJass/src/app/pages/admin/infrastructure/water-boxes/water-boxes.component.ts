import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, Droplets, Search, Filter, Plus, Edit, Trash2, RotateCcw,
  ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, AlertCircle,
  MapPin, Hash, Power, PowerOff, Zap, ZapOff, UserCheck, Phone, Download
} from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { ApiResponse, WaterBox, WaterBoxAssignment, Zone, User } from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';
import * as XLSX from 'xlsx';

interface WaterBoxRow extends WaterBox {
  userName?: string;
  userPhone?: string;
  userDocumentNumber?: string;
  zoneName?: string;
}

@Component({
  selector: 'app-water-boxes',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-5">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Cajas de Agua</h1>
          <p class="text-sm text-gray-500 mt-0.5">Gestión de cajas de agua y asignaciones</p>
        </div>
        <div class="flex items-center gap-3">
          <div class="flex items-center gap-2 px-3 py-1.5 bg-cyan-50 text-cyan-700 rounded-xl border border-cyan-100">
            <lucide-icon [img]="dropletsIcon" [size]="16"></lucide-icon>
            <span class="text-sm font-semibold">{{ activeCount() }}</span>
            <span class="text-xs">Activas</span>
          </div>
          <div class="flex items-center gap-2 px-3 py-1.5 bg-amber-50 text-amber-700 rounded-xl border border-amber-100">
            <lucide-icon [img]="zapOffIcon" [size]="16"></lucide-icon>
            <span class="text-sm font-semibold">{{ suspendedCount() }}</span>
            <span class="text-xs">Suspendidas</span>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 border-b border-gray-100 bg-gray-50/30">
          <div class="flex flex-col lg:flex-row gap-3">
            <div class="relative flex-1">
              <div class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                <lucide-icon [img]="searchIcon" [size]="18"></lucide-icon>
              </div>
              <input
                type="text"
                [ngModel]="searchTerm()"
                (ngModelChange)="searchTerm.set($event); currentPage.set(1)"
                placeholder="Buscar por código, responsable o dirección..."
                class="w-full pl-11 pr-4 py-2.5 border border-gray-200 rounded-xl bg-white text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all">
            </div>
            <div class="flex flex-wrap gap-3">
              <select
                [ngModel]="zoneFilter()"
                (ngModelChange)="zoneFilter.set($event); currentPage.set(1)"
                class="px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all min-w-40">
                <option value="">Todas las zonas</option>
                @for (zone of zones(); track zone.id) {
                  <option [value]="zone.id">{{ zone.zoneName || zone.name }}</option>
                }
              </select>
              <select
                [ngModel]="serviceFilter()"
                (ngModelChange)="serviceFilter.set($event); currentPage.set(1)"
                class="px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all min-w-35">
                <option value="">Todos</option>
                <option value="active">Servicio activo</option>
                <option value="suspended">Suspendido</option>
              </select>
              <select
                [ngModel]="statusFilter()"
                (ngModelChange)="statusFilter.set($event); currentPage.set(1)"
                class="px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all min-w-35">
                <option value="">Todos</option>
                <option value="ACTIVE">Activos</option>
                <option value="INACTIVE">Inactivos</option>
              </select>
              <button
                (click)="exportToXlsx()"
                [disabled]="filteredBoxes().length === 0"
                class="inline-flex items-center gap-2 px-4 py-2.5 bg-green-600 text-white rounded-xl hover:bg-green-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium shadow-sm">
                <lucide-icon [img]="downloadIcon" [size]="18"></lucide-icon>
                <span class="hidden sm:inline">Exportar</span>
              </button>
            </div>
          </div>
        </div>

        @if (isLoading()) {
          <div class="p-16 text-center">
            <div class="inline-flex items-center gap-2.5 text-gray-500">
              <div class="w-5 h-5 border-2 border-cyan-600 border-t-transparent rounded-full animate-spin"></div>
              <span class="text-sm">Cargando cajas de agua...</span>
            </div>
          </div>
        } @else if (filteredBoxes().length === 0) {
          <div class="p-16 text-center">
            <div class="w-16 h-16 mx-auto mb-4 bg-gray-100 rounded-2xl flex items-center justify-center">
              <lucide-icon [img]="dropletsIcon" [size]="32" class="text-gray-400"></lucide-icon>
            </div>
            <p class="text-gray-500 font-medium">No se encontraron cajas de agua</p>
            <p class="text-gray-400 text-sm mt-1">Intenta cambiar los filtros</p>
          </div>
        } @else {

          <div class="hidden lg:block overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50/80 border-b border-gray-100">
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-14">#</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Código</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Tipo</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Responsable</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Dirección</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Zona</th>
                  <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-24">Servicio</th>
                  <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-24">Estado</th>
                  <th class="px-4 py-3 text-right text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-28">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (box of paginatedBoxes(); track box.id; let i = $index) {
                  <tr class="hover:bg-cyan-50/30 transition-colors group"
                      [class.bg-red-50/20]="box.recordStatus === 'INACTIVE'">
                    <td class="px-4 py-3.5">
                      <span class="inline-flex items-center justify-center w-7 h-7 bg-cyan-50 text-cyan-600 rounded-lg text-xs font-bold">
                        {{ (currentPage() - 1) * pageSize + i + 1 }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5">
                      <span class="font-bold text-gray-800 font-mono text-sm">{{ box.boxCode }}</span>
                    </td>
                    <td class="px-4 py-3.5">
                      <span class="inline-flex px-2.5 py-1 text-xs font-medium rounded-lg border"
                            [class]="getBoxTypeBadgeClass(box.boxType)">
                        {{ getBoxTypeLabel(box.boxType) }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5">
                      @if (box.userName) {
                        <div class="min-w-0">
                          <p class="font-semibold text-gray-800 text-sm truncate">{{ box.userName }}</p>
                          @if (box.userDocumentNumber) {
                            <p class="text-xs text-gray-400">DNI: {{ box.userDocumentNumber }}</p>
                          }
                        </div>
                      } @else {
                        <span class="text-sm text-gray-400 italic">Sin asignar</span>
                      }
                    </td>
                    <td class="px-4 py-3.5 text-sm text-gray-500 max-w-45 truncate">{{ box.address || '-' }}</td>
                    <td class="px-4 py-3.5">
                      @if (box.zoneName) {
                        <span class="inline-flex px-2.5 py-1 text-xs font-medium rounded-lg bg-indigo-50 text-indigo-700 border border-indigo-100">
                          {{ box.zoneName }}
                        </span>
                      } @else {
                        <span class="text-sm text-gray-400">-</span>
                      }
                    </td>
                    <td class="px-4 py-3.5 text-center">
                      <span class="inline-flex items-center gap-1 px-2.5 py-1 text-[11px] font-semibold rounded-full"
                            [class]="box.isActive ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-700'">
                        <lucide-icon [img]="box.isActive ? zapIcon : zapOffIcon" [size]="12"></lucide-icon>
                        {{ box.isActive ? 'Activo' : 'Suspendido' }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5 text-center">
                      <span class="inline-flex px-2.5 py-1 text-[11px] font-semibold rounded-full"
                            [class.bg-emerald-50]="box.recordStatus === 'ACTIVE'"
                            [class.text-emerald-700]="box.recordStatus === 'ACTIVE'"
                            [class.bg-red-50]="box.recordStatus === 'INACTIVE'"
                            [class.text-red-600]="box.recordStatus === 'INACTIVE'">
                        {{ box.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5 text-right">
                      <div class="flex items-center justify-end gap-1 opacity-60 group-hover:opacity-100 transition-opacity">
                        @if (box.isActive && box.recordStatus === 'ACTIVE') {
                          <button (click)="suspendBox(box)"
                            class="p-2 text-gray-500 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition-all"
                            title="Suspender servicio">
                            <lucide-icon [img]="powerOffIcon" [size]="16"></lucide-icon>
                          </button>
                        }
                        @if (!box.isActive && box.recordStatus === 'ACTIVE') {
                          <button (click)="reconnectBox(box)"
                            class="p-2 text-gray-500 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-all"
                            title="Reconectar servicio">
                            <lucide-icon [img]="powerIcon" [size]="16"></lucide-icon>
                          </button>
                        }
                        @if (box.recordStatus === 'ACTIVE') {
                          <button (click)="deleteBox(box)"
                            class="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                            title="Desactivar">
                            <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                          </button>
                        } @else {
                          <button (click)="restoreBox(box)"
                            class="p-2 text-gray-500 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-all"
                            title="Restaurar">
                            <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                          </button>
                        }
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="lg:hidden p-3 sm:p-4 space-y-3">
            @for (box of paginatedBoxes(); track box.id; let i = $index) {
              <div class="bg-white rounded-xl border shadow-sm overflow-hidden hover:shadow-md transition-shadow"
                   [class]="box.recordStatus === 'INACTIVE' ? 'border-red-200' : 'border-gray-200'">
                <div class="flex items-stretch">
                  <div class="w-11 bg-linear-to-b from-cyan-500 to-cyan-600 flex items-center justify-center text-white font-bold text-sm shrink-0">
                    {{ (currentPage() - 1) * pageSize + i + 1 }}
                  </div>
                  <div class="flex-1 p-3.5 min-w-0">
                    <div class="flex items-start justify-between gap-2 mb-1.5">
                      <div class="min-w-0">
                        <h4 class="font-bold text-gray-800 text-sm font-mono">{{ box.boxCode }}</h4>
                        <span class="inline-flex px-2 py-0.5 text-[10px] font-medium rounded-lg border mt-0.5"
                              [class]="getBoxTypeBadgeClass(box.boxType)">
                          {{ getBoxTypeLabel(box.boxType) }}
                        </span>
                      </div>
                      <div class="flex flex-col items-end gap-1 shrink-0">
                        <span class="inline-flex items-center gap-1 px-2 py-0.5 text-[10px] font-bold rounded-full"
                              [class]="box.isActive ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-700'">
                          {{ box.isActive ? 'Activo' : 'Suspendido' }}
                        </span>
                        <span class="inline-flex px-2 py-0.5 text-[10px] font-bold rounded-full"
                              [class.bg-emerald-50]="box.recordStatus === 'ACTIVE'"
                              [class.text-emerald-700]="box.recordStatus === 'ACTIVE'"
                              [class.bg-red-50]="box.recordStatus === 'INACTIVE'"
                              [class.text-red-600]="box.recordStatus === 'INACTIVE'">
                          {{ box.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                        </span>
                      </div>
                    </div>
                    <div class="space-y-1 mb-2">
                      @if (box.userName) {
                        <div class="flex items-center gap-1.5 text-xs text-gray-600 font-medium">
                          <lucide-icon [img]="userCheckIcon" [size]="12" class="text-gray-400"></lucide-icon>
                          {{ box.userName }}
                          @if (box.userDocumentNumber) {
                            <span class="text-gray-400">· DNI: {{ box.userDocumentNumber }}</span>
                          }
                        </div>
                      }
                      @if (box.address) {
                        <div class="flex items-center gap-1.5 text-xs text-gray-500 truncate">
                          <lucide-icon [img]="mapPinIcon" [size]="12" class="text-gray-400"></lucide-icon>
                          {{ box.address }}
                        </div>
                      }
                      @if (box.zoneName) {
                        <span class="inline-flex px-2 py-0.5 text-[10px] font-medium rounded-lg bg-indigo-50 text-indigo-700 border border-indigo-100">
                          {{ box.zoneName }}
                        </span>
                      }
                    </div>
                    <div class="flex items-center justify-end gap-1 pt-1 border-t border-gray-100">
                      @if (box.isActive && box.recordStatus === 'ACTIVE') {
                        <button (click)="suspendBox(box)"
                                class="p-1.5 text-amber-600 hover:bg-amber-50 rounded-lg transition-colors"
                                title="Suspender">
                          <lucide-icon [img]="powerOffIcon" [size]="16"></lucide-icon>
                        </button>
                      }
                      @if (!box.isActive && box.recordStatus === 'ACTIVE') {
                        <button (click)="reconnectBox(box)"
                                class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors"
                                title="Reconectar">
                          <lucide-icon [img]="powerIcon" [size]="16"></lucide-icon>
                        </button>
                      }
                      @if (box.recordStatus === 'ACTIVE') {
                        <button (click)="deleteBox(box)"
                                class="p-1.5 text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                          <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restoreBox(box)"
                                class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors">
                          <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                        </button>
                      }
                    </div>
                  </div>
                </div>
              </div>
            }
          </div>
        }

        @if (totalPages() > 1) {
          <div class="p-4 border-t border-gray-100 flex flex-col sm:flex-row items-center justify-between gap-3">
            <p class="text-sm text-gray-500">
              Mostrando <span class="font-semibold text-gray-700">{{ startIndex() + 1 }}</span> -
              <span class="font-semibold text-gray-700">{{ endIndex() }}</span> de
              <span class="font-semibold text-gray-700">{{ filteredBoxes().length }}</span>
            </p>
            <div class="flex items-center gap-1">
              <button (click)="currentPage.set(1)" [disabled]="currentPage() === 1"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="chevronsLeftIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
              <button (click)="currentPage.set(currentPage() - 1)" [disabled]="currentPage() === 1"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="prevIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
              @for (page of visiblePages(); track page) {
                <button (click)="currentPage.set(page)"
                  class="min-w-9 h-9 px-2 border rounded-lg text-sm font-medium transition-colors"
                  [class]="page === currentPage() ? 'bg-cyan-600 text-white border-cyan-600' : 'border-gray-200 text-gray-600 hover:bg-gray-50'">
                  {{ page }}
                </button>
              }
              <button (click)="currentPage.set(currentPage() + 1)" [disabled]="currentPage() >= totalPages()"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="nextIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
              <button (click)="currentPage.set(totalPages())" [disabled]="currentPage() >= totalPages()"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="chevronsRightIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
            </div>
          </div>
        }
      </div>
    </div>
  `
})
export class WaterBoxesComponent implements OnInit {
  private http = inject(HttpClient);
  private alertService = inject(AlertService);
  private authService = inject(AuthService);

  allBoxes = signal<WaterBoxRow[]>([]);
  zones = signal<Zone[]>([]);
  isLoading = signal(false);
  currentPage = signal(1);

  searchTerm = signal('');
  zoneFilter = signal('');
  serviceFilter = signal('');
  statusFilter = signal('');
  readonly pageSize = 10;

  dropletsIcon = Droplets; searchIcon = Search; filterIcon = Filter;
  plusIcon = Plus; editIcon = Edit; trashIcon = Trash2; restoreIcon = RotateCcw;
  prevIcon = ChevronLeft; nextIcon = ChevronRight;
  chevronsLeftIcon = ChevronsLeft; chevronsRightIcon = ChevronsRight;
  alertIcon = AlertCircle; mapPinIcon = MapPin; hashIcon = Hash;
  powerIcon = Power; powerOffIcon = PowerOff; zapIcon = Zap; zapOffIcon = ZapOff;
  userCheckIcon = UserCheck; phoneIcon = Phone; downloadIcon = Download;

  private zoneMap = new Map<string, string>();
  private userCache = new Map<string, User>();

  private get headers() {
    return { 'X-User-Id': this.authService.userId() || '', 'Content-Type': 'application/json' };
  }

  activeCount = computed(() => this.allBoxes().filter(b => b.isActive && b.recordStatus === 'ACTIVE').length);
  suspendedCount = computed(() => this.allBoxes().filter(b => !b.isActive && b.recordStatus === 'ACTIVE').length);

  filteredBoxes = computed(() => {
    let boxes = this.allBoxes();

    const status = this.statusFilter();
    if (status) boxes = boxes.filter(b => b.recordStatus === status);

    const zone = this.zoneFilter();
    if (zone) boxes = boxes.filter(b => b.zoneId === zone);

    const svc = this.serviceFilter();
    if (svc === 'active') boxes = boxes.filter(b => b.isActive);
    else if (svc === 'suspended') boxes = boxes.filter(b => !b.isActive);

    const term = this.searchTerm().trim().toLowerCase();
    if (term) {
      boxes = boxes.filter(b =>
        (b.boxCode || '').toLowerCase().includes(term) ||
        (b.userName || '').toLowerCase().includes(term) ||
        (b.userDocumentNumber || '').toLowerCase().includes(term) ||
        (b.address || '').toLowerCase().includes(term)
      );
    }

    return boxes.sort((a, b) => (a.boxCode || '').localeCompare(b.boxCode || ''));
  });

  totalPages = computed(() => Math.ceil(this.filteredBoxes().length / this.pageSize) || 1);
  startIndex = computed(() => (this.currentPage() - 1) * this.pageSize);
  endIndex = computed(() => Math.min(this.currentPage() * this.pageSize, this.filteredBoxes().length));
  paginatedBoxes = computed(() => this.filteredBoxes().slice(this.startIndex(), this.startIndex() + this.pageSize));

  visiblePages = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    const pages: number[] = [];
    let start = Math.max(1, current - 2);
    const end = Math.min(total, start + 4);
    if (end - start < 4) start = Math.max(1, end - 4);
    for (let i = start; i <= end; i++) pages.push(i);
    return pages;
  });

  ngOnInit(): void {
    this.loadZones();
    this.loadWaterBoxes();
  }

  loadWaterBoxes(): void {
    this.isLoading.set(true);
    this.http.get<ApiResponse<WaterBox[]>>(
      `${environment.apiUrl}/water-boxes`,
      { headers: this.headers }
    ).subscribe({
      next: res => {
        const boxes = (res.data || []).filter(b => b.organizationId === this.authService.organizationId());
        this.loadAssignmentsForBoxes(boxes);
      },
      error: () => {
        this.isLoading.set(false);
        this.alertService.error('Error', 'No se pudieron cargar las cajas de agua');
      }
    });
  }

  private loadAssignmentsForBoxes(boxes: WaterBox[]): void {
    if (boxes.length === 0) {
      this.allBoxes.set([]);
      this.isLoading.set(false);
      return;
    }

    const orgId = this.authService.organizationId();
    this.http.get<ApiResponse<User[]>>(
      `${environment.apiUrl}/users/organization/${orgId}`,
      { params: { includeInactive: 'true' }, headers: this.headers }
    ).subscribe({
      next: usersRes => {
        const users = usersRes.data || [];
        this.userCache.clear();
        users.forEach(u => this.userCache.set(u.id, u));

        this.http.get<ApiResponse<WaterBoxAssignment[]>>(
          `${environment.apiUrl}/water-box-assignments`,
          { headers: this.headers }
        ).subscribe({
          next: assignRes => {
            const assignments = assignRes.data || [];
            const activeAssignmentMap = new Map<string, WaterBoxAssignment>();
            assignments
              .filter(a => a.assignmentStatus === 'ACTIVE')
              .forEach(a => activeAssignmentMap.set(a.waterBoxId, a));

            const enriched: WaterBoxRow[] = boxes.map(box => {
              const assignment = activeAssignmentMap.get(box.id);
              const user = assignment ? this.userCache.get(assignment.userId) : undefined;
              return {
                ...box,
                userName: user ? `${user.lastName}, ${user.firstName}` : undefined,
                userPhone: user?.phone,
                userDocumentNumber: user?.documentNumber,
                zoneName: this.zoneMap.get(box.zoneId) || undefined
              };
            });

            this.allBoxes.set(enriched);
            this.isLoading.set(false);
          },
          error: () => {
            const enriched: WaterBoxRow[] = boxes.map(box => ({
              ...box,
              zoneName: this.zoneMap.get(box.zoneId) || undefined
            }));
            this.allBoxes.set(enriched);
            this.isLoading.set(false);
          }
        });
      },
      error: () => {
        const enriched: WaterBoxRow[] = boxes.map(box => ({
          ...box,
          zoneName: this.zoneMap.get(box.zoneId) || undefined
        }));
        this.allBoxes.set(enriched);
        this.isLoading.set(false);
      }
    });
  }

  loadZones(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;
    this.http.get<ApiResponse<Zone[]>>(
      `${environment.apiUrl}/zones/organization/${orgId}`,
      { headers: this.headers }
    ).subscribe({
      next: res => {
        const z = (res.data || []).filter(zo => zo.recordStatus === 'ACTIVE');
        this.zones.set(z);
        this.zoneMap.clear();
        z.forEach(zo => this.zoneMap.set(zo.id, zo.zoneName || zo.name || ''));
      }
    });
  }

  getBoxTypeLabel(type: string): string {
    const map: Record<string, string> = {
      RESIDENTIAL: 'Residencial', COMMERCIAL: 'Comercial',
      COMMUNAL: 'Comunal', INSTITUTIONAL: 'Institucional'
    };
    return map[type] || type;
  }

  getBoxTypeBadgeClass(type: string): string {
    const map: Record<string, string> = {
      RESIDENTIAL: 'bg-blue-50 text-blue-700 border-blue-100',
      COMMERCIAL: 'bg-purple-50 text-purple-700 border-purple-100',
      COMMUNAL: 'bg-teal-50 text-teal-700 border-teal-100',
      INSTITUTIONAL: 'bg-orange-50 text-orange-700 border-orange-100'
    };
    return map[type] || 'bg-gray-50 text-gray-700 border-gray-200';
  }

  async suspendBox(box: WaterBoxRow): Promise<void> {
    const result = await this.alertService.confirm(
      'Suspender servicio',
      `¿Está seguro de suspender el servicio de la caja ${box.boxCode}?`,
      'Suspender', 'Cancelar'
    );
    if (result.isConfirmed) {
      this.http.patch<ApiResponse<any>>(
        `${environment.apiUrl}/water-boxes/${box.id}/suspend`,
        {},
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Suspendido', 'Servicio suspendido correctamente');
          this.loadWaterBoxes();
        },
        error: () => this.alertService.error('Error', 'No se pudo suspender el servicio')
      });
    }
  }

  async reconnectBox(box: WaterBoxRow): Promise<void> {
    const result = await this.alertService.confirm(
      'Reconectar servicio',
      `¿Está seguro de reconectar el servicio de la caja ${box.boxCode}?`,
      'Reconectar', 'Cancelar'
    );
    if (result.isConfirmed) {
      this.http.patch<ApiResponse<any>>(
        `${environment.apiUrl}/water-boxes/${box.id}/reconnect`,
        {},
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Reconectado', 'Servicio reconectado correctamente');
          this.loadWaterBoxes();
        },
        error: () => this.alertService.error('Error', 'No se pudo reconectar el servicio')
      });
    }
  }

  async deleteBox(box: WaterBoxRow): Promise<void> {
    const result = await this.alertService.confirmDelete(`caja ${box.boxCode}`);
    if (result.isConfirmed) {
      this.http.delete<ApiResponse<any>>(
        `${environment.apiUrl}/water-boxes/${box.id}`,
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Desactivado', 'Caja desactivada correctamente');
          this.loadWaterBoxes();
        },
        error: () => this.alertService.error('Error', 'No se pudo desactivar la caja')
      });
    }
  }

  async restoreBox(box: WaterBoxRow): Promise<void> {
    const result = await this.alertService.confirmRestore(`caja ${box.boxCode}`);
    if (result.isConfirmed) {
      this.http.patch<ApiResponse<any>>(
        `${environment.apiUrl}/water-boxes/${box.id}/restore`,
        {},
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Restaurado', 'Caja restaurada correctamente');
          this.loadWaterBoxes();
        },
        error: () => this.alertService.error('Error', 'No se pudo restaurar la caja')
      });
    }
  }

  exportToXlsx(): void {
    const boxes = this.filteredBoxes();
    if (boxes.length === 0) return;

    const data = boxes.map((b, i) => ({
      'N°': i + 1,
      'Código': b.boxCode,
      'Tipo': this.getBoxTypeLabel(b.boxType),
      'Responsable': b.userName || 'Sin asignar',
      'DNI': b.userDocumentNumber || '',
      'Dirección': b.address || '',
      'Zona': b.zoneName || 'Sin zona',
      'Servicio': b.isActive ? 'Activo' : 'Suspendido',
      'Estado': b.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo'
    }));

    const ws = XLSX.utils.json_to_sheet(data);
    ws['!cols'] = [
      { wch: 5 }, { wch: 15 }, { wch: 14 }, { wch: 35 },
      { wch: 12 }, { wch: 30 }, { wch: 20 }, { wch: 12 }, { wch: 10 }
    ];

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Cajas de Agua');

    const orgName = this.authService.organization()?.organizationName || 'JASS';
    const date = new Date().toISOString().split('T')[0];
    XLSX.writeFile(wb, `${orgName}_CajasDeAgua_${date}.xlsx`);
  }
}
