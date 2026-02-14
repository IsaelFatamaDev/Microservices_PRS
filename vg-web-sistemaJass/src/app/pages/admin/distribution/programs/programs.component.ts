import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Calendar, Plus, Edit, Trash2, Search, RotateCcw, MapPin, Clock, Route } from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { AuthService, AlertService, DistributionService } from '../../../../core/services';
import {
  DistributionProgram, CreateProgramRequest, ApiResponse,
  Zone, Street, DistributionSchedule, DistributionRoute, User
} from '../../../../core/models';

@Component({
  selector: 'app-programs',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="p-6 space-y-5">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <lucide-icon [img]="calendarIcon" [size]="28"></lucide-icon>
            Programas de Distribución
          </h1>
          <p class="text-gray-500 text-sm">Planificación de distribución de agua</p>
        </div>
        <button (click)="openModal()" class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl flex items-center gap-2 text-sm font-medium shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          Nuevo Programa
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Total Programas</p>
          <p class="text-3xl font-bold mt-1">{{ programs().length }}</p>
        </div>
        <div class="bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Activos</p>
          <p class="text-3xl font-bold mt-1">{{ activeCount() }}</p>
        </div>
        <div class="bg-gradient-to-br from-amber-500 to-orange-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Inactivos</p>
          <p class="text-3xl font-bold mt-1">{{ programs().length - activeCount() }}</p>
        </div>
      </div>

      <!-- Search -->
      <div class="bg-white rounded-xl shadow-sm p-4">
        <div class="relative">
          <lucide-icon [img]="searchIcon" [size]="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
          <input [(ngModel)]="searchTerm" (ngModelChange)="applyFilters()" type="text" placeholder="Buscar programas..."
            class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 text-sm">
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Horario</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Ruta</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Zona</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora Plan.</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora Real</th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Estado</th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              @for (p of filteredPrograms(); track p.id) {
                <tr class="hover:bg-gray-50 transition-colors" [class.bg-red-50/30]="p.recordStatus === 'INACTIVE'">
                  <td class="px-4 py-3">
                    <div class="text-sm font-medium text-gray-800">{{ p.programDate }}</div>
                    <div class="text-xs text-gray-400">{{ p.createdAt | date:'dd/MM/yyyy' }}</div>
                  </td>
                  <td class="px-4 py-3">
                    <span class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-lg bg-purple-50 text-purple-700 border border-purple-100">
                      <lucide-icon [img]="clockIcon" [size]="12"></lucide-icon>
                      {{ getScheduleName(p.scheduleId) }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <span class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-lg bg-cyan-50 text-cyan-700 border border-cyan-100">
                      <lucide-icon [img]="routeIcon" [size]="12"></lucide-icon>
                      {{ getRouteName(p.routeId) }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <span class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-lg bg-indigo-50 text-indigo-700 border border-indigo-100">
                      <lucide-icon [img]="mapPinIcon" [size]="12"></lucide-icon>
                      {{ getZoneName(p.zoneId) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">
                    {{ p.plannedStartTime }} - {{ p.plannedEndTime }}
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">
                    @if (p.actualStartTime) {
                      {{ p.actualStartTime }} - {{ p.actualEndTime || '...' }}
                    } @else {
                      <span class="text-gray-400">-</span>
                    }
                  </td>
                  <td class="px-4 py-3 text-center">
                    <span class="inline-flex px-2 py-1 text-[11px] font-semibold rounded-full"
                      [class]="p.recordStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-red-50 text-red-600'">
                      {{ p.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="edit(p)" class="p-1.5 hover:bg-blue-50 text-blue-600 rounded-lg" title="Editar">
                        <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                      </button>
                      @if (p.recordStatus === 'ACTIVE') {
                        <button (click)="deactivate(p)" class="p-1.5 hover:bg-red-50 text-red-600 rounded-lg" title="Desactivar">
                          <lucide-icon [img]="trash2Icon" [size]="16"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restore(p)" class="p-1.5 hover:bg-green-50 text-green-600 rounded-lg" title="Restaurar">
                          <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="8" class="text-center py-12 text-gray-400">No hay programas registrados</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal -->
      @if (showModal()) {
        <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <h3 class="text-lg font-bold mb-4">{{ editMode() ? 'Editar' : 'Nuevo' }} Programa</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Horario *</label>
                <select [(ngModel)]="formData.scheduleId" class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
                  <option value="">Seleccione horario...</option>
                  @for (s of schedulesList(); track s.id) {
                    <option [value]="s.id">{{ s.scheduleName }} ({{ s.startTime }}-{{ s.endTime }})</option>
                  }
                </select>
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Ruta *</label>
                <select [(ngModel)]="formData.routeId" class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
                  <option value="">Seleccione ruta...</option>
                  @for (r of routesList(); track r.id) {
                    <option [value]="r.id">{{ r.routeName }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Zona *</label>
                <select [(ngModel)]="formData.zoneId" (ngModelChange)="onZoneChange()"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
                  <option value="">Seleccione zona...</option>
                  @for (z of zones(); track z.id) {
                    <option [value]="z.id">{{ z.zoneName }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Calle (opcional)</label>
                <select [(ngModel)]="formData.streetId" class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
                  <option value="">Todas las calles</option>
                  @for (st of filteredStreets(); track st.id) {
                    <option [value]="st.id">{{ st.streetName }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Fecha del Programa *</label>
                <input [(ngModel)]="formData.programDate" type="date"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Responsable *</label>
                <select [(ngModel)]="formData.responsibleUserId"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20">
                  <option value="">Seleccione operario...</option>
                  @for (op of operators(); track op.id) {
                    <option [value]="op.id">{{ op.lastName }}, {{ op.firstName }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Hora Inicio Planif. *</label>
                <input [(ngModel)]="formData.plannedStartTime" type="time"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Hora Fin Planif. *</label>
                <input [(ngModel)]="formData.plannedEndTime" type="time"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Hora Inicio Real</label>
                <input [(ngModel)]="formData.actualStartTime" type="time"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Hora Fin Real</label>
                <input [(ngModel)]="formData.actualEndTime" type="time"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>
              <div class="md:col-span-2">
                <label class="text-sm text-gray-600 mb-1 block">Observaciones</label>
                <textarea [(ngModel)]="formData.observations" rows="2" placeholder="Comentarios adicionales..."
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm resize-none"></textarea>
              </div>
            </div>
            <div class="flex gap-2 mt-6">
              <button (click)="closeModal()" class="flex-1 px-4 py-2 border rounded-xl hover:bg-gray-50 text-sm">Cancelar</button>
              <button (click)="save()" [disabled]="!isFormValid()"
                class="flex-1 px-4 py-2 bg-violet-600 text-white rounded-xl disabled:opacity-50 hover:bg-violet-700 text-sm font-medium">
                Guardar
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class ProgramsComponent implements OnInit {
  private auth = inject(AuthService);
  private alert = inject(AlertService);
  private dist = inject(DistributionService);
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  calendarIcon = Calendar; plusIcon = Plus; editIcon = Edit; trash2Icon = Trash2;
  searchIcon = Search; restoreIcon = RotateCcw; mapPinIcon = MapPin;
  clockIcon = Clock; routeIcon = Route;

  programs = signal<DistributionProgram[]>([]);
  filteredPrograms = signal<DistributionProgram[]>([]);
  zones = signal<Zone[]>([]);
  streets = signal<Street[]>([]);
  filteredStreets = signal<Street[]>([]);
  schedulesList = signal<DistributionSchedule[]>([]);
  routesList = signal<DistributionRoute[]>([]);
  operators = signal<User[]>([]);

  searchTerm = '';
  showModal = signal(false);
  editMode = signal(false);
  selectedId = '';
  activeCount = signal(0);

  formData: any = {};

  ngOnInit() {
    this.loadZones();
    this.loadStreets();
    this.loadSchedules();
    this.loadRoutes();
    this.loadOperators();
    this.load();
  }

  load() {
    this.dist.getPrograms().subscribe(data => {
      const orgId = this.auth.organizationId();
      const filtered = data.filter(p => p.organizationId === orgId);
      this.programs.set(filtered);
      this.activeCount.set(filtered.filter(p => p.recordStatus === 'ACTIVE').length);
      this.applyFilters();
    });
  }

  loadZones() {
    this.http.get<ApiResponse<Zone[]>>(`${this.apiUrl}/zones`).subscribe(res => {
      const orgId = this.auth.organizationId();
      this.zones.set((res.data || []).filter(z => z.organizationId === orgId));
    });
  }

  loadStreets() {
    this.http.get<ApiResponse<Street[]>>(`${this.apiUrl}/streets`).subscribe(res => {
      const orgId = this.auth.organizationId();
      this.streets.set((res.data || []).filter(s => s.organizationId === orgId));
    });
  }

  loadSchedules() {
    this.dist.getActiveSchedules().subscribe(data => {
      const orgId = this.auth.organizationId();
      this.schedulesList.set(data.filter(s => s.organizationId === orgId));
    });
  }

  loadRoutes() {
    this.dist.getActiveRoutes().subscribe(data => {
      const orgId = this.auth.organizationId();
      this.routesList.set(data.filter(r => r.organizationId === orgId));
    });
  }

  loadOperators() {
    const orgId = this.auth.organizationId();
    if (!orgId) return;
    this.http.get<ApiResponse<User[]>>(`${this.apiUrl}/users`).subscribe(res => {
      this.operators.set(
        (res.data || []).filter(u => u.organizationId === orgId && u.role === 'OPERATOR' && u.recordStatus === 'ACTIVE')
      );
    });
  }

  applyFilters() {
    let list = [...this.programs()];
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      list = list.filter(p =>
        p.programDate?.toLowerCase().includes(term) ||
        this.getZoneName(p.zoneId).toLowerCase().includes(term) ||
        this.getRouteName(p.routeId).toLowerCase().includes(term) ||
        p.observations?.toLowerCase().includes(term)
      );
    }
    this.filteredPrograms.set(list);
  }

  getZoneName(id: string): string {
    return this.zones().find(z => z.id === id)?.zoneName || id || '-';
  }

  getScheduleName(id: string): string {
    return this.schedulesList().find(s => s.id === id)?.scheduleName || id || '-';
  }

  getRouteName(id: string): string {
    return this.routesList().find(r => r.id === id)?.routeName || id || '-';
  }

  onZoneChange() {
    const zoneId = this.formData.zoneId;
    this.filteredStreets.set(this.streets().filter(s => s.zoneId === zoneId));
    this.formData.streetId = '';
  }

  openModal() {
    this.editMode.set(false);
    this.formData = {
      scheduleId: '', routeId: '', zoneId: '', streetId: '',
      programDate: '', plannedStartTime: '', plannedEndTime: '',
      actualStartTime: '', actualEndTime: '', responsibleUserId: '', observations: ''
    };
    this.filteredStreets.set([]);
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  edit(p: DistributionProgram) {
    this.editMode.set(true);
    this.selectedId = p.id;
    this.formData = {
      scheduleId: p.scheduleId,
      routeId: p.routeId,
      zoneId: p.zoneId,
      streetId: p.streetId || '',
      programDate: p.programDate,
      plannedStartTime: p.plannedStartTime,
      plannedEndTime: p.plannedEndTime,
      actualStartTime: p.actualStartTime || '',
      actualEndTime: p.actualEndTime || '',
      responsibleUserId: p.responsibleUserId,
      observations: p.observations || ''
    };
    this.onZoneChange();
    this.showModal.set(true);
  }

  isFormValid(): boolean {
    return !!(this.formData.scheduleId && this.formData.routeId && this.formData.zoneId &&
      this.formData.programDate && this.formData.plannedStartTime && this.formData.plannedEndTime &&
      this.formData.responsibleUserId);
  }

  save() {
    const orgId = this.auth.organizationId();
    if (!orgId || !this.isFormValid()) return;

    const req: CreateProgramRequest = {
      organizationId: orgId,
      scheduleId: this.formData.scheduleId,
      routeId: this.formData.routeId,
      zoneId: this.formData.zoneId,
      streetId: this.formData.streetId || undefined,
      programDate: this.formData.programDate,
      plannedStartTime: this.formData.plannedStartTime,
      plannedEndTime: this.formData.plannedEndTime,
      actualStartTime: this.formData.actualStartTime || undefined,
      actualEndTime: this.formData.actualEndTime || undefined,
      responsibleUserId: this.formData.responsibleUserId,
      observations: this.formData.observations || undefined
    };

    const op = this.editMode()
      ? this.dist.updateProgram(this.selectedId, req)
      : this.dist.createProgram(req);

    op.subscribe({
      next: () => {
        this.alert.success('Éxito', `Programa ${this.editMode() ? 'actualizado' : 'creado'}`);
        this.closeModal();
        this.load();
      },
      error: () => this.alert.error('Error', 'No se pudo guardar el programa')
    });
  }

  deactivate(p: DistributionProgram) {
    if (!confirm('¿Desactivar este programa?')) return;
    this.dist.deactivateProgram(p.id).subscribe({
      next: () => { this.alert.success('Éxito', 'Programa desactivado'); this.load(); },
      error: () => this.alert.error('Error', 'No se pudo desactivar')
    });
  }

  restore(p: DistributionProgram) {
    this.dist.restoreProgram(p.id).subscribe({
      next: () => { this.alert.success('Éxito', 'Programa restaurado'); this.load(); },
      error: () => this.alert.error('Error', 'No se pudo restaurar')
    });
  }
}
