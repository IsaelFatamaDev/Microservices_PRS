import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule, AlertTriangle, Search, Plus, Eye, X,
  ChevronLeft, ChevronRight, UserPlus, CheckCircle, XCircle
} from 'lucide-angular';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { ClaimsService } from '../../../../core/services/claims.service';
import { Incident, IncidentType, CreateIncidentRequest, AssignIncidentRequest } from '../../../../core/models/claims.model';
import { User, ApiResponse } from '../../../../core/models';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

interface Zone {
  id: string;
  zoneName: string;
}

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Incidencias</h1>
          <p class="text-sm text-gray-500 mt-1">Gestión de incidencias técnicas de infraestructura</p>
        </div>
        <button (click)="openCreateModal()" class="px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white rounded-xl font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          <span>Nueva Incidencia</span>
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-5 gap-4">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Total</span>
            <div class="p-2 rounded-xl bg-violet-50"><lucide-icon [img]="alertIcon" [size]="16" class="text-violet-600"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-gray-800">{{ allIncidents().length }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Reportadas</span>
            <div class="p-2 rounded-xl bg-amber-50"><lucide-icon [img]="alertIcon" [size]="16" class="text-amber-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-amber-600">{{ reportedCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">En Progreso</span>
            <div class="p-2 rounded-xl bg-blue-50"><lucide-icon [img]="alertIcon" [size]="16" class="text-blue-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-blue-600">{{ inProgressCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Resueltas</span>
            <div class="p-2 rounded-xl bg-emerald-50"><lucide-icon [img]="checkIcon" [size]="16" class="text-emerald-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-emerald-600">{{ resolvedCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Críticas</span>
            <div class="p-2 rounded-xl bg-red-50"><lucide-icon [img]="alertIcon" [size]="16" class="text-red-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-red-600">{{ criticalCount() }}</p>
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center gap-3">
          <div class="relative flex-1">
            <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
            <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar por código o título..."
                   class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-400">
          </div>
          <select [(ngModel)]="statusFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todos los Estados</option>
            <option value="REPORTED">Reportado</option>
            <option value="ASSIGNED">Asignado</option>
            <option value="IN_PROGRESS">En Progreso</option>
            <option value="RESOLVED">Resuelto</option>
            <option value="CLOSED">Cerrado</option>
          </select>
          <select [(ngModel)]="severityFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todas las Severidades</option>
            <option value="LOW">Baja</option>
            <option value="MEDIUM">Media</option>
            <option value="HIGH">Alta</option>
            <option value="CRITICAL">Crítica</option>
          </select>
        </div>

        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50/80">
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Código</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Título</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Zona</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Severidad</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Fecha</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (inc of paginatedIncidents(); track inc.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3 text-sm font-medium text-violet-700">{{ inc.incidentCode }}</td>
                  <td class="px-4 py-3 text-sm text-gray-800">{{ inc.title }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ getZoneName(inc.zoneId) }}</td>
                  <td class="px-4 py-3">
                    <span [class]="getSeverityBadge(inc.severity)" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ getSeverityLabel(inc.severity) }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <span [class]="getStatusBadge(inc.status)" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ getStatusLabel(inc.status) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ inc.incidentDate | date:'dd/MM/yyyy' }}</td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="viewDetail(inc)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-violet-600 transition-colors" title="Ver detalle">
                        <lucide-icon [img]="eyeIcon" [size]="15"></lucide-icon>
                      </button>
                      @if (inc.status === 'REPORTED') {
                        <button (click)="openAssignModal(inc)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-blue-600 transition-colors" title="Asignar">
                          <lucide-icon [img]="userPlusIcon" [size]="15"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="7" class="text-center py-12 text-gray-400 text-sm">No se encontraron incidencias</td></tr>
              }
            </tbody>
          </table>
        </div>

        <!-- Mobile cards -->
        <div class="md:hidden divide-y divide-gray-50">
          @for (inc of paginatedIncidents(); track inc.id) {
            <div class="p-4 space-y-2">
              <div class="flex items-center justify-between">
                <span class="text-sm font-medium text-violet-700">{{ inc.incidentCode }}</span>
                <span [class]="getStatusBadge(inc.status)" class="text-xs px-2 py-1 rounded-lg font-medium">
                  {{ getStatusLabel(inc.status) }}
                </span>
              </div>
              <p class="text-sm text-gray-800 font-medium">{{ inc.title }}</p>
              <div class="flex items-center gap-2">
                <span [class]="getSeverityBadge(inc.severity)" class="text-xs px-2 py-1 rounded-lg font-medium">
                  {{ getSeverityLabel(inc.severity) }}
                </span>
                <span class="text-xs text-gray-400">{{ inc.incidentDate | date:'dd/MM/yyyy' }}</span>
              </div>
              <div class="flex gap-2 pt-1">
                <button (click)="viewDetail(inc)" class="flex-1 py-1.5 text-xs border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 transition-colors">Ver detalle</button>
                @if (inc.status === 'REPORTED') {
                  <button (click)="openAssignModal(inc)" class="flex-1 py-1.5 text-xs border border-blue-200 rounded-lg text-blue-600 hover:bg-blue-50 transition-colors">Asignar</button>
                }
              </div>
            </div>
          } @empty {
            <div class="p-8 text-center text-gray-400 text-sm">No se encontraron incidencias</div>
          }
        </div>

        @if (filteredIncidents().length > pageSize) {
          <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
            <span>{{ (currentPage() - 1) * pageSize + 1 }} - {{ Math.min(currentPage() * pageSize, filteredIncidents().length) }} de {{ filteredIncidents().length }}</span>
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

      <!-- Create Modal -->
      @if (showCreateModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeCreateModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Nueva Incidencia</h3>
              <button (click)="closeCreateModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="saveIncident()" class="p-6 space-y-4">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tipo de Incidencia *</label>
                  <select [(ngModel)]="formData.incidentTypeId" name="incidentTypeId" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccionar...</option>
                    @for (type of allIncidentTypes(); track type.id) {
                      <option [value]="type.id">{{ type.typeName }}</option>
                    }
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Zona *</label>
                  <select [(ngModel)]="formData.zoneId" name="zoneId" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccionar...</option>
                    @for (zone of allZones(); track zone.id) {
                      <option [value]="zone.id">{{ zone.zoneName }}</option>
                    }
                  </select>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Título *</label>
                <input type="text" [(ngModel)]="formData.title" name="title" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400" placeholder="Ej: Fuga en tubería principal">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Descripción *</label>
                <textarea [(ngModel)]="formData.description" name="description" required rows="3" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 resize-none" placeholder="Describe la incidencia"></textarea>
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Severidad *</label>
                  <select [(ngModel)]="formData.severity" name="severity" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccionar...</option>
                    <option value="LOW">Baja</option>
                    <option value="MEDIUM">Media</option>
                    <option value="HIGH">Alta</option>
                    <option value="CRITICAL">Crítica</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Cajas Afectadas</label>
                  <input type="number" [(ngModel)]="formData.affectedBoxesCount" name="affectedBoxesCount" min="0" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400" placeholder="0">
                </div>
              </div>
              <div class="flex gap-3 pt-4">
                <button type="button" (click)="closeCreateModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">
                  Cancelar
                </button>
                <button type="submit" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                  {{ saving() ? 'Guardando...' : 'Guardar' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Detail Modal -->
      @if (showDetailModal() && selectedIncident()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeDetailModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Detalle de Incidencia</h3>
              <button (click)="closeDetailModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <div class="p-6 space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div><p class="text-xs text-gray-400">Código</p><p class="text-sm font-medium text-violet-700">{{ selectedIncident()!.incidentCode }}</p></div>
                <div><p class="text-xs text-gray-400">Fecha</p><p class="text-sm text-gray-700">{{ selectedIncident()!.incidentDate | date:'dd/MM/yyyy HH:mm' }}</p></div>
                <div><p class="text-xs text-gray-400">Zona</p><p class="text-sm text-gray-700">{{ getZoneName(selectedIncident()!.zoneId) }}</p></div>
                <div><p class="text-xs text-gray-400">Severidad</p>
                  <span [class]="getSeverityBadge(selectedIncident()!.severity)" class="text-xs px-2 py-1 rounded-lg font-medium inline-block">{{ getSeverityLabel(selectedIncident()!.severity) }}</span>
                </div>
                <div><p class="text-xs text-gray-400">Estado</p>
                  <span [class]="getStatusBadge(selectedIncident()!.status)" class="text-xs px-2 py-1 rounded-lg font-medium inline-block">{{ getStatusLabel(selectedIncident()!.status) }}</span>
                </div>
                <div><p class="text-xs text-gray-400">Cajas Afectadas</p><p class="text-sm text-gray-700">{{ selectedIncident()!.affectedBoxesCount || 0 }}</p></div>
              </div>
              <div><p class="text-xs text-gray-400 mb-1">Título</p><p class="text-sm font-medium text-gray-800">{{ selectedIncident()!.title }}</p></div>
              <div><p class="text-xs text-gray-400 mb-1">Descripción</p><p class="text-sm text-gray-700">{{ selectedIncident()!.description }}</p></div>
              @if (selectedIncident()!.assignedToUserId) {
                <div><p class="text-xs text-gray-400 mb-1">Asignado a</p><p class="text-sm text-gray-700">{{ getUserName(selectedIncident()!.assignedToUserId!) }}</p></div>
              }
              @if (selectedIncident()!.resolutionNotes) {
                <div class="bg-emerald-50 rounded-xl p-4"><p class="text-xs text-emerald-600 font-medium mb-1">Notas de Resolución</p><p class="text-sm text-emerald-800">{{ selectedIncident()!.resolutionNotes }}</p></div>
              }
            </div>
          </div>
        </div>
      }

      <!-- Assign Modal -->
      @if (showAssignModal() && selectedIncident()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeAssignModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-md w-full" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Asignar Técnico</h3>
              <button (click)="closeAssignModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="assignIncident()" class="p-6 space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Técnico *</label>
                <select [(ngModel)]="assignData.userId" name="userId" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                  <option value="">Seleccionar técnico...</option>
                  @for (user of allUsers(); track user.id) {
                    <option [value]="user.id">{{ user.lastName }}, {{ user.firstName }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Notas</label>
                <textarea [(ngModel)]="assignData.notes" name="notes" rows="3" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 resize-none" placeholder="Notas adicionales"></textarea>
              </div>
              <div class="flex gap-3">
                <button type="button" (click)="closeAssignModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">Cancelar</button>
                <button type="submit" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50">
                  {{ saving() ? 'Asignando...' : 'Asignar' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `
})
export class IncidentsComponent implements OnInit {
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private claimsService = inject(ClaimsService);
  private http = inject(HttpClient);

  // Icons
  alertIcon = AlertTriangle; searchIcon = Search; plusIcon = Plus; eyeIcon = Eye;
  xIcon = X; chevronLeftIcon = ChevronLeft; chevronRightIcon = ChevronRight;
  userPlusIcon = UserPlus; checkIcon = CheckCircle; closeIcon = XCircle;

  Math = Math;

  // Data signals
  allIncidents = signal<Incident[]>([]);
  allIncidentTypes = signal<IncidentType[]>([]);
  allZones = signal<Zone[]>([]);
  allUsers = signal<User[]>([]);

  // Filters
  searchTerm = '';
  statusFilter = '';
  severityFilter = '';

  // Pagination
  currentPage = signal(1);
  pageSize = 15;

  // Modals
  showCreateModal = signal(false);
  showDetailModal = signal(false);
  showAssignModal = signal(false);
  selectedIncident = signal<Incident | null>(null);
  saving = signal(false);

  // Form data
  formData: any = {};
  assignData: AssignIncidentRequest = { userId: '', notes: '' };

  // Computed
  reportedCount = computed(() => this.allIncidents().filter(i => i.status === 'REPORTED').length);
  inProgressCount = computed(() => this.allIncidents().filter(i => i.status === 'IN_PROGRESS' || i.status === 'ASSIGNED').length);
  resolvedCount = computed(() => this.allIncidents().filter(i => i.status === 'RESOLVED').length);
  criticalCount = computed(() => this.allIncidents().filter(i => i.severity === 'CRITICAL').length);

  filteredIncidents = computed(() => {
    let list = this.allIncidents();
    if (this.statusFilter) list = list.filter(i => i.status === this.statusFilter);
    if (this.severityFilter) list = list.filter(i => i.severity === this.severityFilter);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(i =>
        i.incidentCode.toLowerCase().includes(q) ||
        i.title.toLowerCase().includes(q)
      );
    }
    return list.sort((a, b) => new Date(b.incidentDate).getTime() - new Date(a.incidentDate).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredIncidents().length / this.pageSize));
  paginatedIncidents = computed(() => {
    const s = (this.currentPage() - 1) * this.pageSize;
    return this.filteredIncidents().slice(s, s + this.pageSize);
  });

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.claimsService.getIncidentsByOrg(orgId).subscribe({
      next: r => this.allIncidents.set((r.data || []).filter(i => i.recordStatus === 'ACTIVE'))
    });

    this.claimsService.getActiveIncidentTypesByOrg(orgId).subscribe({
      next: r => this.allIncidentTypes.set(r.data || [])
    });

    this.http.get<ApiResponse<Zone[]>>(`${environment.apiUrl}/zones`).subscribe({
      next: r => this.allZones.set(r.data || [])
    });

    this.http.get<ApiResponse<User[]>>(`${environment.apiUrl}/users`).subscribe({
      next: r => this.allUsers.set((r.data || []).filter(u => u.organizationId === orgId))
    });
  }

  openCreateModal(): void {
    this.formData = {
      incidentTypeId: '',
      zoneId: '',
      title: '',
      description: '',
      severity: '',
      affectedBoxesCount: 0,
      incidentCategory: ''
    };
    this.showCreateModal.set(true);
  }

  closeCreateModal(): void {
    this.showCreateModal.set(false);
    this.formData = {};
  }

  saveIncident(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    const req: CreateIncidentRequest = { ...this.formData, organizationId: orgId };

    this.saving.set(true);
    this.claimsService.createIncident(req).subscribe({
      next: () => {
        this.alertService.success('Incidencia creada exitosamente');
        this.closeCreateModal();
        this.loadData();
        this.saving.set(false);
      },
      error: () => {
        this.alertService.error('Error al crear la incidencia');
        this.saving.set(false);
      }
    });
  }

  viewDetail(incident: Incident): void {
    this.selectedIncident.set(incident);
    this.showDetailModal.set(true);
  }

  closeDetailModal(): void {
    this.showDetailModal.set(false);
    this.selectedIncident.set(null);
  }

  openAssignModal(incident: Incident): void {
    this.selectedIncident.set(incident);
    this.assignData = { userId: '', notes: '' };
    this.showAssignModal.set(true);
  }

  closeAssignModal(): void {
    this.showAssignModal.set(false);
    this.selectedIncident.set(null);
    this.assignData = { userId: '', notes: '' };
  }

  assignIncident(): void {
    const inc = this.selectedIncident();
    if (!inc) return;

    this.saving.set(true);
    this.claimsService.assignIncident(inc.id, this.assignData).subscribe({
      next: () => {
        this.alertService.success('Incidencia asignada exitosamente');
        this.closeAssignModal();
        this.loadData();
        this.saving.set(false);
      },
      error: () => {
        this.alertService.error('Error al asignar la incidencia');
        this.saving.set(false);
      }
    });
  }

  getZoneName(zoneId: string): string {
    return this.allZones().find(z => z.id === zoneId)?.zoneName || zoneId;
  }

  getUserName(userId: string): string {
    const u = this.allUsers().find(x => x.id === userId);
    return u ? `${u.lastName}, ${u.firstName}` : userId;
  }

  getSeverityLabel(s: string): string {
    const map: Record<string, string> = { LOW: 'Baja', MEDIUM: 'Media', HIGH: 'Alta', CRITICAL: 'Crítica' };
    return map[s] || s;
  }

  getSeverityBadge(s: string): string {
    const map: Record<string, string> = {
      LOW: 'bg-gray-100 text-gray-700',
      MEDIUM: 'bg-amber-50 text-amber-700',
      HIGH: 'bg-orange-50 text-orange-700',
      CRITICAL: 'bg-red-50 text-red-700'
    };
    return map[s] || 'bg-gray-100 text-gray-600';
  }

  getStatusLabel(s: string): string {
    const map: Record<string, string> = {
      REPORTED: 'Reportado',
      ASSIGNED: 'Asignado',
      IN_PROGRESS: 'En Progreso',
      RESOLVED: 'Resuelto',
      CLOSED: 'Cerrado'
    };
    return map[s] || s;
  }

  getStatusBadge(s: string): string {
    const map: Record<string, string> = {
      REPORTED: 'bg-amber-50 text-amber-700',
      ASSIGNED: 'bg-blue-50 text-blue-700',
      IN_PROGRESS: 'bg-indigo-50 text-indigo-700',
      RESOLVED: 'bg-emerald-50 text-emerald-700',
      CLOSED: 'bg-gray-100 text-gray-600'
    };
    return map[s] || 'bg-gray-100 text-gray-600';
  }
}
