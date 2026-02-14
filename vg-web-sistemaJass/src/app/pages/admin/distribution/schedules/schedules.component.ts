import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Clock, Plus, Edit, Trash2, Search, RotateCcw, MapPin } from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { AuthService, AlertService, DistributionService } from '../../../../core/services';
import { DistributionSchedule, CreateScheduleRequest, DayOfWeek, ApiResponse, Zone, Street } from '../../../../core/models';

const ALL_DAYS: { key: DayOfWeek; label: string }[] = [
  { key: 'MONDAY', label: 'Lunes' },
  { key: 'TUESDAY', label: 'Martes' },
  { key: 'WEDNESDAY', label: 'Miércoles' },
  { key: 'THURSDAY', label: 'Jueves' },
  { key: 'FRIDAY', label: 'Viernes' },
  { key: 'SATURDAY', label: 'Sábado' },
  { key: 'SUNDAY', label: 'Domingo' }
];

@Component({
  selector: 'app-schedules',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="p-6 space-y-5">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <lucide-icon [img]="clockIcon" [size]="28"></lucide-icon>
            Horarios de Distribución
          </h1>
          <p class="text-gray-500 text-sm">Gestión de horarios por zona</p>
        </div>
        <button (click)="openModal()" class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl flex items-center gap-2 text-sm font-medium shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          Nuevo Horario
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Total Horarios</p>
          <p class="text-3xl font-bold mt-1">{{ schedules().length }}</p>
        </div>
        <div class="bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Activos</p>
          <p class="text-3xl font-bold mt-1">{{ activeCount() }}</p>
        </div>
        <div class="bg-gradient-to-br from-amber-500 to-orange-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Inactivos</p>
          <p class="text-3xl font-bold mt-1">{{ schedules().length - activeCount() }}</p>
        </div>
      </div>

      <!-- Search -->
      <div class="bg-white rounded-xl shadow-sm p-4">
        <div class="relative">
          <lucide-icon [img]="searchIcon" [size]="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
          <input [(ngModel)]="searchTerm" (ngModelChange)="applyFilters()" type="text" placeholder="Buscar horarios..."
            class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 text-sm">
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Zona</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Días</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Horario</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Duración</th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Estado</th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              @for (s of filteredSchedules(); track s.id) {
                <tr class="hover:bg-gray-50 transition-colors" [class.bg-red-50/30]="s.recordStatus === 'INACTIVE'">
                  <td class="px-4 py-3">
                    <div class="text-sm font-medium text-gray-800">{{ s.scheduleName }}</div>
                    <div class="text-xs text-gray-400">{{ s.createdAt | date:'dd/MM/yyyy' }}</div>
                  </td>
                  <td class="px-4 py-3">
                    <span class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-lg bg-indigo-50 text-indigo-700 border border-indigo-100">
                      <lucide-icon [img]="mapPinIcon" [size]="12"></lucide-icon>
                      {{ getZoneName(s.zoneId) }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <div class="flex flex-wrap gap-1">
                      @for (d of s.daysOfWeek; track d) {
                        <span class="inline-flex px-1.5 py-0.5 text-[10px] font-semibold rounded bg-violet-100 text-violet-700">{{ getDayShort(d) }}</span>
                      }
                    </div>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ s.startTime }} - {{ s.endTime }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ s.durationHours ? s.durationHours + 'h' : '-' }}</td>
                  <td class="px-4 py-3 text-center">
                    <span class="inline-flex px-2 py-1 text-[11px] font-semibold rounded-full"
                      [class]="s.recordStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-red-50 text-red-600'">
                      {{ s.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="edit(s)" class="p-1.5 hover:bg-blue-50 text-blue-600 rounded-lg" title="Editar">
                        <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                      </button>
                      @if (s.recordStatus === 'ACTIVE') {
                        <button (click)="deactivate(s)" class="p-1.5 hover:bg-red-50 text-red-600 rounded-lg" title="Desactivar">
                          <lucide-icon [img]="trash2Icon" [size]="16"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restore(s)" class="p-1.5 hover:bg-green-50 text-green-600 rounded-lg" title="Restaurar">
                          <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="7" class="text-center py-12 text-gray-400">No hay horarios registrados</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal -->
      @if (showModal()) {
        <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <h3 class="text-lg font-bold mb-4">{{ editMode() ? 'Editar' : 'Nuevo' }} Horario</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="md:col-span-2">
                <label class="text-sm text-gray-600 mb-1 block">Nombre del Horario *</label>
                <input [(ngModel)]="formData.scheduleName" placeholder="Ej: Horario matutino zona norte"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20">
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
                <select [(ngModel)]="formData.streetId"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
                  <option value="">Todas las calles</option>
                  @for (st of filteredStreets(); track st.id) {
                    <option [value]="st.id">{{ st.streetName }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Hora Inicio *</label>
                <input [(ngModel)]="formData.startTime" type="time"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Hora Fin *</label>
                <input [(ngModel)]="formData.endTime" type="time"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Duración (horas)</label>
                <input [(ngModel)]="formData.durationHours" type="number" min="0" step="0.5" placeholder="Ej: 2"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>
              <div class="md:col-span-2">
                <label class="text-sm text-gray-600 mb-2 block">Días de la Semana</label>
                <div class="flex flex-wrap gap-2">
                  @for (day of allDays; track day.key) {
                    <label class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg border cursor-pointer text-sm transition-all"
                      [class]="isDaySelected(day.key) ? 'bg-violet-100 border-violet-300 text-violet-700 font-medium' : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-gray-100'">
                      <input type="checkbox" [checked]="isDaySelected(day.key)" (change)="toggleDay(day.key)" class="hidden">
                      {{ day.label }}
                    </label>
                  }
                </div>
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
export class SchedulesComponent implements OnInit {
  private auth = inject(AuthService);
  private alert = inject(AlertService);
  private dist = inject(DistributionService);
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  clockIcon = Clock; plusIcon = Plus; editIcon = Edit; trash2Icon = Trash2;
  searchIcon = Search; restoreIcon = RotateCcw; mapPinIcon = MapPin;

  allDays = ALL_DAYS;

  schedules = signal<DistributionSchedule[]>([]);
  filteredSchedules = signal<DistributionSchedule[]>([]);
  zones = signal<Zone[]>([]);
  streets = signal<Street[]>([]);
  filteredStreets = signal<Street[]>([]);

  searchTerm = '';
  showModal = signal(false);
  editMode = signal(false);
  selectedId = '';
  activeCount = signal(0);

  formData: any = { daysOfWeek: [] as string[] };

  ngOnInit() {
    this.loadZones();
    this.loadStreets();
    this.load();
  }

  load() {
    this.dist.getSchedules().subscribe(data => {
      const orgId = this.auth.organizationId();
      const filtered = data.filter(s => s.organizationId === orgId);
      this.schedules.set(filtered);
      this.activeCount.set(filtered.filter(s => s.recordStatus === 'ACTIVE').length);
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

  applyFilters() {
    let list = [...this.schedules()];
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      list = list.filter(s =>
        s.scheduleName.toLowerCase().includes(term) ||
        this.getZoneName(s.zoneId).toLowerCase().includes(term)
      );
    }
    this.filteredSchedules.set(list);
  }

  getZoneName(zoneId: string): string {
    return this.zones().find(z => z.id === zoneId)?.zoneName || zoneId;
  }

  getDayShort(day: string): string {
    const map: Record<string, string> = {
      MONDAY: 'Lun', TUESDAY: 'Mar', WEDNESDAY: 'Mié',
      THURSDAY: 'Jue', FRIDAY: 'Vie', SATURDAY: 'Sáb', SUNDAY: 'Dom'
    };
    return map[day] || day;
  }

  isDaySelected(day: DayOfWeek): boolean {
    return this.formData.daysOfWeek?.includes(day) || false;
  }

  toggleDay(day: DayOfWeek) {
    if (!this.formData.daysOfWeek) this.formData.daysOfWeek = [];
    const idx = this.formData.daysOfWeek.indexOf(day);
    if (idx >= 0) this.formData.daysOfWeek.splice(idx, 1);
    else this.formData.daysOfWeek.push(day);
  }

  onZoneChange() {
    const zoneId = this.formData.zoneId;
    this.filteredStreets.set(this.streets().filter(s => s.zoneId === zoneId));
    this.formData.streetId = '';
  }

  openModal() {
    this.editMode.set(false);
    this.formData = { daysOfWeek: [] as string[], zoneId: '', streetId: '', scheduleName: '', startTime: '', endTime: '', durationHours: null };
    this.filteredStreets.set([]);
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  edit(s: DistributionSchedule) {
    this.editMode.set(true);
    this.selectedId = s.id;
    this.formData = {
      scheduleName: s.scheduleName,
      zoneId: s.zoneId,
      streetId: s.streetId || '',
      startTime: s.startTime,
      endTime: s.endTime,
      durationHours: s.durationHours,
      daysOfWeek: [...(s.daysOfWeek || [])]
    };
    this.onZoneChange();
    this.showModal.set(true);
  }

  isFormValid(): boolean {
    return !!(this.formData.scheduleName && this.formData.zoneId && this.formData.startTime && this.formData.endTime);
  }

  save() {
    const orgId = this.auth.organizationId();
    if (!orgId || !this.isFormValid()) return;

    const req: CreateScheduleRequest = {
      organizationId: orgId,
      zoneId: this.formData.zoneId,
      streetId: this.formData.streetId || undefined,
      scheduleName: this.formData.scheduleName,
      daysOfWeek: this.formData.daysOfWeek,
      startTime: this.formData.startTime,
      endTime: this.formData.endTime,
      durationHours: this.formData.durationHours ? Number(this.formData.durationHours) : undefined
    };

    const op = this.editMode()
      ? this.dist.updateSchedule(this.selectedId, req)
      : this.dist.createSchedule(req);

    op.subscribe({
      next: () => {
        this.alert.success('Éxito', `Horario ${this.editMode() ? 'actualizado' : 'creado'}`);
        this.closeModal();
        this.load();
      },
      error: () => this.alert.error('Error', 'No se pudo guardar el horario')
    });
  }

  deactivate(s: DistributionSchedule) {
    if (!confirm(`¿Desactivar horario "${s.scheduleName}"?`)) return;
    this.dist.deactivateSchedule(s.id).subscribe({
      next: () => { this.alert.success('Éxito', 'Horario desactivado'); this.load(); },
      error: () => this.alert.error('Error', 'No se pudo desactivar')
    });
  }

  restore(s: DistributionSchedule) {
    this.dist.restoreSchedule(s.id).subscribe({
      next: () => { this.alert.success('Éxito', 'Horario restaurado'); this.load(); },
      error: () => this.alert.error('Error', 'No se pudo restaurar')
    });
  }
}
