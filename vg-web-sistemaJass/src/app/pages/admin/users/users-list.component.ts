import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, Plus, Search, Edit, Trash2, RotateCcw, Users, Filter,
  Download, ChevronLeft, ChevronRight, Eye, X, Droplets, UserCog, UserCheck,
  Phone, Mail, MapPin, FileText, Hash, ChevronsLeft, ChevronsRight, AlertCircle
} from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { User, ApiResponse, Zone, Street } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';
import * as XLSX from 'xlsx';

type UserTab = 'operators' | 'clients';

interface WaterBoxDetail {
  id: string;
  boxCode: string;
  boxType: string;
  address: string;
  isActive: boolean;
  installationDate: string;
  recordStatus: string;
}

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-5">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Gestión de Usuarios</h1>
          <p class="text-sm text-gray-500 mt-0.5">Administra los operadores y clientes de la organización</p>
        </div>
        <div class="flex items-center gap-3">
          <div class="flex items-center gap-2 px-3 py-1.5 bg-purple-50 text-purple-700 rounded-xl border border-purple-100">
            <lucide-icon [img]="userCogIcon" [size]="16"></lucide-icon>
            <span class="text-sm font-semibold">{{ operatorsCount() }}</span>
            <span class="text-xs">Operadores</span>
          </div>
          <div class="flex items-center gap-2 px-3 py-1.5 bg-blue-50 text-blue-700 rounded-xl border border-blue-100">
            <lucide-icon [img]="userCheckIcon" [size]="16"></lucide-icon>
            <span class="text-sm font-semibold">{{ clientsCount() }}</span>
            <span class="text-xs">Clientes</span>
          </div>
        </div>
      </div>

      <!-- Main card -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <!-- Tabs -->
        <div class="flex border-b border-gray-100">
          <button
            (click)="switchTab('operators')"
            class="relative flex-1 sm:flex-none sm:px-8 py-3.5 text-sm font-medium transition-all"
            [class]="activeTab() === 'operators'
              ? 'text-purple-700 bg-purple-50/50'
              : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'">
            <div class="flex items-center justify-center gap-2">
              <lucide-icon [img]="userCogIcon" [size]="18"></lucide-icon>
              <span>Operadores</span>
              <span class="ml-1 px-2 py-0.5 text-xs font-bold rounded-full"
                    [class]="activeTab() === 'operators' ? 'bg-purple-100 text-purple-700' : 'bg-gray-100 text-gray-500'">
                {{ operatorsCount() }}
              </span>
            </div>
            @if (activeTab() === 'operators') {
              <div class="absolute bottom-0 left-0 right-0 h-0.5 bg-purple-600"></div>
            }
          </button>
          <button
            (click)="switchTab('clients')"
            class="relative flex-1 sm:flex-none sm:px-8 py-3.5 text-sm font-medium transition-all"
            [class]="activeTab() === 'clients'
              ? 'text-blue-700 bg-blue-50/50'
              : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'">
            <div class="flex items-center justify-center gap-2">
              <lucide-icon [img]="userCheckIcon" [size]="18"></lucide-icon>
              <span>Clientes</span>
              <span class="ml-1 px-2 py-0.5 text-xs font-bold rounded-full"
                    [class]="activeTab() === 'clients' ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-500'">
                {{ clientsCount() }}
              </span>
            </div>
            @if (activeTab() === 'clients') {
              <div class="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600"></div>
            }
          </button>
        </div>

        <!-- Toolbar -->
        <div class="p-4 border-b border-gray-100 bg-gray-50/30">
          <div class="flex flex-col lg:flex-row gap-3">
            <div class="relative flex-1">
              <div class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                <lucide-icon [img]="searchIcon" [size]="18"></lucide-icon>
              </div>
              <input
                type="text"
                [(ngModel)]="searchTerm"
                (input)="currentPage.set(1)"
                placeholder="Buscar por apellido, nombre o DNI..."
                class="w-full pl-11 pr-4 py-2.5 border border-gray-200 rounded-xl bg-white text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-400 transition-all">
            </div>

            <div class="flex flex-wrap gap-3">
              @if (activeTab() === 'clients') {
                <select
                  [(ngModel)]="zoneFilter"
                  (ngModelChange)="currentPage.set(1)"
                  [style.color]="zoneFilter ? '#111827' : '#9ca3af'"
                  class="px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all min-w-40">
                  <option value="" style="color: #9ca3af">Todas las zonas</option>
                  @for (zone of zones(); track zone.id) {
                    <option [value]="zone.id" style="color: #111827">{{ zone.zoneName || zone.name }}</option>
                  }
                </select>
              }

              <select
                [(ngModel)]="statusFilter"
                (ngModelChange)="currentPage.set(1)"
                class="px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all min-w-35">
                <option value="ACTIVE">Activos</option>
                <option value="INACTIVE">Inactivos</option>
                <option value="">Todos</option>
              </select>

              @if (activeTab() === 'clients') {
                <button
                  (click)="exportToXlsx()"
                  [disabled]="filteredUsers().length === 0"
                  class="inline-flex items-center gap-2 px-4 py-2.5 bg-green-600 text-white rounded-xl hover:bg-green-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium shadow-sm">
                  <lucide-icon [img]="downloadIcon" [size]="18"></lucide-icon>
                  <span class="hidden sm:inline">Exportar</span>
                </button>
              }

              <a [routerLink]="['/admin/users/new']"
                 [queryParams]="{ role: activeTab() === 'operators' ? 'OPERATOR' : 'CLIENT' }"
                 class="inline-flex items-center gap-2 px-4 py-2.5 text-white rounded-xl transition-all text-sm font-medium shadow-sm"
                 [class]="activeTab() === 'operators' ? 'bg-purple-600 hover:bg-purple-700' : 'bg-blue-600 hover:bg-blue-700'">
                <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
                <span class="hidden sm:inline">{{ activeTab() === 'operators' ? 'Nuevo Operador' : 'Nuevo Cliente' }}</span>
              </a>
            </div>
          </div>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          @if (isLoading()) {
            <div class="p-16 text-center">
              <div class="inline-flex items-center gap-2.5 text-gray-500">
                <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                <span class="text-sm">Cargando usuarios...</span>
              </div>
            </div>
          } @else if (filteredUsers().length === 0) {
            <div class="p-16 text-center">
              <div class="w-16 h-16 mx-auto mb-4 bg-gray-100 rounded-2xl flex items-center justify-center">
                <lucide-icon [img]="usersIcon" [size]="32" class="text-gray-400"></lucide-icon>
              </div>
              <p class="text-gray-500 font-medium">No se encontraron {{ activeTab() === 'operators' ? 'operadores' : 'clientes' }}</p>
              <p class="text-gray-400 text-sm mt-1">Intenta cambiar los filtros o crea uno nuevo</p>
            </div>
          } @else {
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50/80">
                  <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider w-12">#</th>
                  <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Apellidos, Nombres</th>
                  <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">DNI</th>
                  <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Teléfono</th>
                  @if (activeTab() === 'clients') {
                    <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider hidden md:table-cell">Dirección</th>
                    <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider hidden lg:table-cell">Zona</th>
                  } @else {
                    <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider hidden md:table-cell">Email</th>
                  }
                  <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Estado</th>
                  <th class="px-4 py-3 text-right text-xs font-semibold text-gray-500 uppercase tracking-wider w-32">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (user of paginatedUsers(); track user.id; let i = $index) {
                  <tr class="hover:bg-blue-50/30 transition-colors group"
                      [class.bg-red-50/20]="user.recordStatus === 'INACTIVE'">
                    <td class="px-4 py-3.5 text-sm text-gray-400 font-mono">
                      {{ (currentPage() - 1) * pageSize + i + 1 }}
                    </td>
                    <td class="px-4 py-3.5">
                      <div class="flex items-center gap-3">
                        <div class="w-9 h-9 rounded-full flex items-center justify-center text-xs font-bold shrink-0"
                             [class]="activeTab() === 'operators' ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'">
                          {{ (user.lastName || '').charAt(0) }}{{ (user.firstName || '').charAt(0) }}
                        </div>
                        <div class="min-w-0">
                          <p class="font-semibold text-gray-800 text-sm truncate">{{ user.lastName }}, {{ user.firstName }}</p>
                          @if (activeTab() === 'clients' && user.email) {
                            <p class="text-xs text-gray-400 truncate">{{ user.email }}</p>
                          }
                        </div>
                      </div>
                    </td>
                    <td class="px-4 py-3.5 text-sm text-gray-600 font-mono">{{ user.documentNumber || user.dni || '-' }}</td>
                    <td class="px-4 py-3.5 text-sm text-gray-600">{{ user.phone || '-' }}</td>
                    @if (activeTab() === 'clients') {
                      <td class="px-4 py-3.5 text-sm text-gray-500 hidden md:table-cell max-w-45 truncate">{{ user.address || '-' }}</td>
                      <td class="px-4 py-3.5 hidden lg:table-cell">
                        @if (getZoneName(user.zoneId)) {
                          <span class="inline-flex px-2.5 py-1 text-xs font-medium rounded-lg bg-indigo-50 text-indigo-700 border border-indigo-100">
                            {{ getZoneName(user.zoneId) }}
                          </span>
                        } @else {
                          <span class="text-sm text-gray-400">-</span>
                        }
                      </td>
                    } @else {
                      <td class="px-4 py-3.5 text-sm text-gray-500 hidden md:table-cell truncate max-w-50">{{ user.email || '-' }}</td>
                    }
                    <td class="px-4 py-3.5">
                      <span
                        class="inline-flex px-2.5 py-1 text-xs font-semibold rounded-lg border"
                        [class.bg-emerald-50]="user.recordStatus === 'ACTIVE'"
                        [class.text-emerald-700]="user.recordStatus === 'ACTIVE'"
                        [class.border-emerald-100]="user.recordStatus === 'ACTIVE'"
                        [class.bg-red-50]="user.recordStatus === 'INACTIVE'"
                        [class.text-red-600]="user.recordStatus === 'INACTIVE'"
                        [class.border-red-100]="user.recordStatus === 'INACTIVE'">
                        {{ user.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5 text-right">
                      <div class="flex items-center justify-end gap-1 opacity-60 group-hover:opacity-100 transition-opacity">
                        @if (activeTab() === 'clients') {
                          <button
                            (click)="toggleDetail(user)"
                            class="p-2 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all"
                            title="Ver detalle">
                            <lucide-icon [img]="eyeIcon" [size]="16"></lucide-icon>
                          </button>
                        }
                        <a [routerLink]="['/admin/users', user.id]"
                           class="p-2 text-gray-500 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition-all"
                           title="Editar">
                          <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                        </a>
                        @if (user.recordStatus === 'ACTIVE') {
                          <button
                            (click)="deleteUser(user)"
                            class="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                            title="Desactivar">
                            <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                          </button>
                        } @else {
                          <button
                            (click)="restoreUser(user)"
                            class="p-2 text-gray-500 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-all"
                            title="Restaurar">
                            <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                          </button>
                        }
                      </div>
                    </td>
                  </tr>

                  @if (expandedUserId() === user.id) {
                    <tr>
                      <td [attr.colspan]="activeTab() === 'clients' ? 8 : 7" class="px-6 py-4 bg-blue-50/30 border-b border-blue-100">
                        @if (loadingBoxes()) {
                          <div class="flex items-center gap-2 text-sm text-gray-500">
                            <div class="w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                            Cargando cajas de agua...
                          </div>
                        } @else if (userWaterBoxes().length === 0) {
                          <div class="flex items-center gap-2 text-sm text-gray-400">
                            <lucide-icon [img]="alertIcon" [size]="16"></lucide-icon>
                            Este usuario no tiene cajas de agua asignadas
                          </div>
                        } @else {
                          <div class="space-y-2">
                            <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">
                              <lucide-icon [img]="dropletsIcon" [size]="14" class="inline mr-1"></lucide-icon>
                              Cajas de Agua Asignadas ({{ userWaterBoxes().length }})
                            </p>
                            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
                              @for (box of userWaterBoxes(); track box.id) {
                                <div class="bg-white rounded-xl p-3 border border-gray-200 text-sm">
                                  <div class="flex items-center justify-between mb-2">
                                    <span class="font-bold text-gray-800 font-mono">{{ box.boxCode }}</span>
                                    <span class="px-2 py-0.5 text-xs font-semibold rounded-lg"
                                          [class]="box.isActive ? 'bg-emerald-50 text-emerald-700' : 'bg-red-50 text-red-600'">
                                      {{ box.isActive ? 'Activa' : 'Suspendida' }}
                                    </span>
                                  </div>
                                  <div class="space-y-1 text-xs text-gray-500">
                                    <p><span class="font-medium text-gray-600">Tipo:</span> {{ getBoxTypeLabel(box.boxType) }}</p>
                                    <p><span class="font-medium text-gray-600">Dirección:</span> {{ box.address || '-' }}</p>
                                    <p><span class="font-medium text-gray-600">Instalación:</span> {{ box.installationDate | date:'dd/MM/yyyy' }}</p>
                                  </div>
                                </div>
                              }
                            </div>
                          </div>
                        }
                      </td>
                    </tr>
                  }
                }
              </tbody>
            </table>
          }
        </div>

        <!-- Pagination -->
        @if (totalPages() > 1) {
          <div class="p-4 border-t border-gray-100 flex flex-col sm:flex-row items-center justify-between gap-3">
            <p class="text-sm text-gray-500">
              Mostrando <span class="font-semibold text-gray-700">{{ startIndex() + 1 }}</span> -
              <span class="font-semibold text-gray-700">{{ endIndex() }}</span> de
              <span class="font-semibold text-gray-700">{{ filteredUsers().length }}</span>
            </p>
            <div class="flex items-center gap-1">
              <button
                (click)="currentPage.set(1)"
                [disabled]="currentPage() === 1"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="chevronsLeftIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
              <button
                (click)="currentPage.set(currentPage() - 1)"
                [disabled]="currentPage() === 1"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="prevIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>

              @for (page of visiblePages(); track page) {
                <button
                  (click)="currentPage.set(page)"
                  class="min-w-9 h-9 px-2 border rounded-lg text-sm font-medium transition-colors"
                  [class]="page === currentPage()
                    ? (activeTab() === 'operators' ? 'bg-purple-600 text-white border-purple-600' : 'bg-blue-600 text-white border-blue-600')
                    : 'border-gray-200 text-gray-600 hover:bg-gray-50'">
                  {{ page }}
                </button>
              }

              <button
                (click)="currentPage.set(currentPage() + 1)"
                [disabled]="currentPage() >= totalPages()"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="nextIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
              <button
                (click)="currentPage.set(totalPages())"
                [disabled]="currentPage() >= totalPages()"
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
export class UsersListComponent implements OnInit {
  private http = inject(HttpClient);
  private alertService = inject(AlertService);
  private authService = inject(AuthService);

  allUsers = signal<User[]>([]);
  zones = signal<Zone[]>([]);
  isLoading = signal(false);
  activeTab = signal<UserTab>('clients');
  currentPage = signal(1);
  expandedUserId = signal<string | null>(null);
  userWaterBoxes = signal<WaterBoxDetail[]>([]);
  loadingBoxes = signal(false);

  searchTerm = '';
  zoneFilter = '';
  statusFilter = 'ACTIVE';
  readonly pageSize = 10;

  plusIcon = Plus; searchIcon = Search; editIcon = Edit; trashIcon = Trash2;
  restoreIcon = RotateCcw; usersIcon = Users; filterIcon = Filter;
  downloadIcon = Download; prevIcon = ChevronLeft; nextIcon = ChevronRight;
  chevronsLeftIcon = ChevronsLeft; chevronsRightIcon = ChevronsRight;
  eyeIcon = Eye; closeIcon = X; dropletsIcon = Droplets;
  userCogIcon = UserCog; userCheckIcon = UserCheck;
  phoneIcon = Phone; mailIcon = Mail; mapPinIcon = MapPin;
  fileIcon = FileText; hashIcon = Hash; alertIcon = AlertCircle;

  private zoneMap = new Map<string, string>();

  private get headers() {
    return { 'X-User-Id': this.authService.userId() || '', 'Content-Type': 'application/json' };
  }

  operatorsCount = computed(() =>
    this.allUsers().filter(u => u.role === 'OPERATOR' && u.recordStatus === 'ACTIVE').length
  );

  clientsCount = computed(() =>
    this.allUsers().filter(u => u.role === 'CLIENT' && u.recordStatus === 'ACTIVE').length
  );

  filteredUsers = computed(() => {
    const role = this.activeTab() === 'operators' ? 'OPERATOR' : 'CLIENT';
    let users = this.allUsers().filter(u => u.role === role);

    if (this.statusFilter) {
      users = users.filter(u => u.recordStatus === this.statusFilter);
    }
    if (this.zoneFilter && this.activeTab() === 'clients') {
      users = users.filter(u => u.zoneId === this.zoneFilter);
    }
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.trim().toLowerCase();
      users = users.filter(u =>
        (u.lastName || '').toLowerCase().includes(term) ||
        (u.firstName || '').toLowerCase().includes(term) ||
        (u.documentNumber || u.dni || '').toLowerCase().includes(term) ||
        (u.phone || '').includes(term)
      );
    }

    users.sort((a, b) => {
      const cmp = (a.lastName || '').localeCompare(b.lastName || '');
      if (cmp !== 0) return cmp;
      return (a.firstName || '').localeCompare(b.firstName || '');
    });

    return users;
  });

  totalPages = computed(() => Math.ceil(this.filteredUsers().length / this.pageSize) || 1);
  startIndex = computed(() => (this.currentPage() - 1) * this.pageSize);
  endIndex = computed(() => Math.min(this.currentPage() * this.pageSize, this.filteredUsers().length));
  paginatedUsers = computed(() => this.filteredUsers().slice(this.startIndex(), this.startIndex() + this.pageSize));

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
    this.loadUsers();
    this.loadZones();
  }

  switchTab(tab: UserTab): void {
    this.activeTab.set(tab);
    this.currentPage.set(1);
    this.searchTerm = '';
    this.zoneFilter = '';
    this.expandedUserId.set(null);
  }

  loadUsers(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;
    this.isLoading.set(true);

    this.http.get<ApiResponse<User[]>>(
      `${environment.apiUrl}/users/organization/${orgId}`,
      { params: { includeInactive: 'true' }, headers: this.headers }
    ).subscribe({
      next: res => {
        this.allUsers.set(res.data || []);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.alertService.error('Error', 'No se pudieron cargar los usuarios');
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

  getZoneName(zoneId?: string): string {
    if (!zoneId) return '';
    return this.zoneMap.get(zoneId) || '';
  }

  getBoxTypeLabel(type: string): string {
    const map: Record<string, string> = {
      RESIDENTIAL: 'Residencial', COMMERCIAL: 'Comercial',
      COMMUNAL: 'Comunal', INSTITUTIONAL: 'Institucional'
    };
    return map[type] || type;
  }

  toggleDetail(user: User): void {
    if (this.expandedUserId() === user.id) {
      this.expandedUserId.set(null);
      return;
    }
    this.expandedUserId.set(user.id);
    this.loadWaterBoxes(user.id);
  }

  loadWaterBoxes(userId: string): void {
    this.loadingBoxes.set(true);
    this.userWaterBoxes.set([]);

    this.http.get<ApiResponse<any[]>>(
      `${environment.apiUrl}/water-box-assignments/user/${userId}`,
      { headers: this.headers }
    ).subscribe({
      next: res => {
        const assignments = res.data || [];
        if (assignments.length === 0) {
          this.loadingBoxes.set(false);
          return;
        }
        const boxIds = [...new Set(assignments.map((a: any) => a.waterBoxId))];
        let loaded = 0;
        const boxes: WaterBoxDetail[] = [];

        boxIds.forEach(boxId => {
          this.http.get<ApiResponse<any>>(
            `${environment.apiUrl}/water-boxes/${boxId}`,
            { headers: this.headers }
          ).subscribe({
            next: boxRes => {
              const b = boxRes.data;
              if (b) {
                boxes.push({
                  id: b.id,
                  boxCode: b.boxCode || b.supplyNumber || b.code || '-',
                  boxType: b.boxType || 'RESIDENTIAL',
                  address: b.address || '',
                  isActive: b.isActive ?? b.is_active ?? true,
                  installationDate: b.installationDate || b.createdAt || '',
                  recordStatus: b.recordStatus || 'ACTIVE'
                });
              }
              loaded++;
              if (loaded === boxIds.length) {
                this.userWaterBoxes.set(boxes);
                this.loadingBoxes.set(false);
              }
            },
            error: () => {
              loaded++;
              if (loaded === boxIds.length) {
                this.userWaterBoxes.set(boxes);
                this.loadingBoxes.set(false);
              }
            }
          });
        });
      },
      error: () => this.loadingBoxes.set(false)
    });
  }

  exportToXlsx(): void {
    const users = this.filteredUsers();
    if (users.length === 0) return;

    const data = users.map((u, i) => ({
      'N°': i + 1,
      'Apellidos y Nombres': `${u.lastName || ''}, ${u.firstName || ''}`,
      'N° DNI': u.documentNumber || u.dni || '',
      'Teléfono': u.phone || '',
      'Dirección': u.address || '',
      'Zona': this.getZoneName(u.zoneId) || 'Sin zona'
    }));

    const ws = XLSX.utils.json_to_sheet(data);
    ws['!cols'] = [{ wch: 5 }, { wch: 35 }, { wch: 12 }, { wch: 12 }, { wch: 30 }, { wch: 20 }];

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Clientes');

    const orgName = this.authService.organization()?.organizationName || 'JASS';
    const date = new Date().toISOString().split('T')[0];
    XLSX.writeFile(wb, `${orgName}_Clientes_${date}.xlsx`);
  }

  async deleteUser(user: User): Promise<void> {
    const result = await this.alertService.confirmDelete(`${user.lastName}, ${user.firstName}`);
    if (result.isConfirmed) {
      this.http.delete<ApiResponse<any>>(
        `${environment.apiUrl}/users/${user.id}`,
        { headers: this.headers, params: { reason: 'Desactivado por administrador' } }
      ).subscribe({
        next: () => {
          this.alertService.success('Desactivado', 'Usuario desactivado correctamente');
          this.loadUsers();
        },
        error: () => this.alertService.error('Error', 'No se pudo desactivar el usuario')
      });
    }
  }

  async restoreUser(user: User): Promise<void> {
    const result = await this.alertService.confirmRestore(`${user.lastName}, ${user.firstName}`);
    if (result.isConfirmed) {
      this.http.patch<ApiResponse<any>>(
        `${environment.apiUrl}/users/${user.id}/restore`,
        {},
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Restaurado', 'Usuario restaurado correctamente');
          this.loadUsers();
        },
        error: () => this.alertService.error('Error', 'No se pudo restaurar el usuario')
      });
    }
  }
}
