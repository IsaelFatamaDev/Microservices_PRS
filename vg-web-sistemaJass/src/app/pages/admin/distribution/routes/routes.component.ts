import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Route, Plus, Edit, Trash2, Search, RotateCcw, MapPin, X, ArrowUp, ArrowDown } from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { AuthService, AlertService, DistributionService } from '../../../../core/services';
import { DistributionRoute, CreateRouteRequest, ZoneOrder, ApiResponse, Zone, User } from '../../../../core/models';

@Component({
  selector: 'app-routes',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="p-6 space-y-5">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <lucide-icon [img]="routeIcon" [size]="28"></lucide-icon>
            Rutas de Distribución
          </h1>
          <p class="text-gray-500 text-sm">Gestión de rutas y zonas asignadas</p>
        </div>
        <button (click)="openModal()" class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl flex items-center gap-2 text-sm font-medium shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          Nueva Ruta
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Total Rutas</p>
          <p class="text-3xl font-bold mt-1">{{ routes().length }}</p>
        </div>
        <div class="bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Activas</p>
          <p class="text-3xl font-bold mt-1">{{ activeCount() }}</p>
        </div>
        <div class="bg-gradient-to-br from-cyan-500 to-blue-600 rounded-xl p-5 text-white">
          <p class="text-sm opacity-90">Zonas cubiertas</p>
          <p class="text-3xl font-bold mt-1">{{ totalZones() }}</p>
        </div>
      </div>

      <!-- Search -->
      <div class="bg-white rounded-xl shadow-sm p-4">
        <div class="relative">
          <lucide-icon [img]="searchIcon" [size]="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
          <input [(ngModel)]="searchTerm" (ngModelChange)="applyFilters()" type="text" placeholder="Buscar rutas..."
            class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 text-sm">
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Ruta</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Responsable</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Zonas</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Duración Total</th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Estado</th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              @for (r of filteredRoutes(); track r.id) {
                <tr class="hover:bg-gray-50 transition-colors" [class.bg-red-50/30]="r.recordStatus === 'INACTIVE'">
                  <td class="px-4 py-3">
                    <div class="text-sm font-medium text-gray-800">{{ r.routeName }}</div>
                    <div class="text-xs text-gray-400">{{ r.createdAt | date:'dd/MM/yyyy' }}</div>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ getOperatorName(r.responsibleUserId) }}</td>
                  <td class="px-4 py-3">
                    <div class="flex flex-wrap gap-1">
                      @for (z of r.zones; track z.zoneId) {
                        <span class="inline-flex items-center gap-1 px-1.5 py-0.5 text-[10px] font-semibold rounded bg-indigo-100 text-indigo-700">
                          {{ z.order }}. {{ getZoneName(z.zoneId) }}
                        </span>
                      }
                      @if (!r.zones.length) {
                        <span class="text-xs text-gray-400">Sin zonas</span>
                      }
                    </div>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ r.totalEstimatedDuration }} min</td>
                  <td class="px-4 py-3 text-center">
                    <span class="inline-flex px-2 py-1 text-[11px] font-semibold rounded-full"
                      [class]="r.recordStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-red-50 text-red-600'">
                      {{ r.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="edit(r)" class="p-1.5 hover:bg-blue-50 text-blue-600 rounded-lg" title="Editar">
                        <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                      </button>
                      @if (r.recordStatus === 'ACTIVE') {
                        <button (click)="deactivate(r)" class="p-1.5 hover:bg-red-50 text-red-600 rounded-lg" title="Desactivar">
                          <lucide-icon [img]="trash2Icon" [size]="16"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restore(r)" class="p-1.5 hover:bg-green-50 text-green-600 rounded-lg" title="Restaurar">
                          <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="6" class="text-center py-12 text-gray-400">No hay rutas registradas</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal -->
      @if (showModal()) {
        <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl max-w-2xl w-full p-6 max-h-[90vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <h3 class="text-lg font-bold mb-4">{{ editMode() ? 'Editar' : 'Nueva' }} Ruta</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="md:col-span-2">
                <label class="text-sm text-gray-600 mb-1 block">Nombre de la Ruta *</label>
                <input [(ngModel)]="formData.routeName" placeholder="Ej: Ruta Norte A"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20">
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
                <label class="text-sm text-gray-600 mb-1 block">Duración Total (min) *</label>
                <input [(ngModel)]="formData.totalEstimatedDuration" type="number" min="1" placeholder="Ej: 120"
                  class="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm">
              </div>

              <!-- Zones Management -->
              <div class="md:col-span-2 border-t pt-4 mt-2">
                <label class="text-sm font-medium text-gray-700 mb-3 block">Zonas de la Ruta</label>

                <!-- Add zone row -->
                <div class="flex gap-2 mb-3">
                  <select [(ngModel)]="newZone.zoneId" class="flex-1 px-3 py-2 border border-gray-200 rounded-xl text-sm">
                    <option value="">Seleccione zona...</option>
                    @for (z of availableZones(); track z.id) {
                      <option [value]="z.id">{{ z.zoneName }}</option>
                    }
                  </select>
                  <input [(ngModel)]="newZone.estimatedDuration" type="number" min="0" placeholder="Min"
                    class="w-24 px-3 py-2 border border-gray-200 rounded-xl text-sm">
                  <button (click)="addZone()" [disabled]="!newZone.zoneId"
                    class="px-3 py-2 bg-violet-600 text-white rounded-xl disabled:opacity-50 hover:bg-violet-700">
                    <lucide-icon [img]="plusIcon" [size]="16"></lucide-icon>
                  </button>
                </div>

                <!-- Zone list -->
                @if (formData.zones?.length) {
                  <div class="space-y-2">
                    @for (z of formData.zones; track z.order; let i = $index) {
                      <div class="flex items-center gap-2 p-2 bg-gray-50 rounded-xl">
                        <span class="w-7 h-7 flex items-center justify-center bg-violet-100 text-violet-700 rounded-lg text-xs font-bold">{{ z.order }}</span>
                        <span class="flex-1 text-sm font-medium text-gray-700">{{ getZoneName(z.zoneId) }}</span>
                        <span class="text-xs text-gray-500">{{ z.estimatedDuration }} min</span>
                        <button (click)="moveZoneUp(i)" [disabled]="i === 0" class="p-1 hover:bg-gray-200 rounded disabled:opacity-30">
                          <lucide-icon [img]="arrowUpIcon" [size]="14"></lucide-icon>
                        </button>
                        <button (click)="moveZoneDown(i)" [disabled]="i === formData.zones.length - 1" class="p-1 hover:bg-gray-200 rounded disabled:opacity-30">
                          <lucide-icon [img]="arrowDownIcon" [size]="14"></lucide-icon>
                        </button>
                        <button (click)="removeZone(i)" class="p-1 hover:bg-red-100 text-red-600 rounded">
                          <lucide-icon [img]="xIcon" [size]="14"></lucide-icon>
                        </button>
                      </div>
                    }
                  </div>
                } @else {
                  <p class="text-sm text-gray-400 text-center py-4">No se han agregado zonas aún</p>
                }
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
export class RoutesComponent implements OnInit {
  private auth = inject(AuthService);
  private alert = inject(AlertService);
  private dist = inject(DistributionService);
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  routeIcon = Route; plusIcon = Plus; editIcon = Edit; trash2Icon = Trash2;
  searchIcon = Search; restoreIcon = RotateCcw; mapPinIcon = MapPin;
  xIcon = X; arrowUpIcon = ArrowUp; arrowDownIcon = ArrowDown;

  routes = signal<DistributionRoute[]>([]);
  filteredRoutes = signal<DistributionRoute[]>([]);
  zones = signal<Zone[]>([]);
  availableZones = signal<Zone[]>([]);
  operators = signal<User[]>([]);

  searchTerm = '';
  showModal = signal(false);
  editMode = signal(false);
  selectedId = '';
  activeCount = signal(0);
  totalZones = signal(0);

  formData: any = { zones: [] as ZoneOrder[] };
  newZone: { zoneId: string; estimatedDuration: number } = { zoneId: '', estimatedDuration: 0 };

  ngOnInit() {
    this.loadZones();
    this.loadOperators();
    this.load();
  }

  load() {
    this.dist.getRoutes().subscribe(data => {
      const orgId = this.auth.organizationId();
      const filtered = data.filter(r => r.organizationId === orgId);
      this.routes.set(filtered);
      this.activeCount.set(filtered.filter(r => r.recordStatus === 'ACTIVE').length);
      const uniqueZones = new Set<string>();
      filtered.forEach(r => r.zones?.forEach(z => uniqueZones.add(z.zoneId)));
      this.totalZones.set(uniqueZones.size);
      this.applyFilters();
    });
  }

  loadZones() {
    this.http.get<ApiResponse<Zone[]>>(`${this.apiUrl}/zones`).subscribe(res => {
      const orgId = this.auth.organizationId();
      this.zones.set((res.data || []).filter(z => z.organizationId === orgId));
      this.updateAvailableZones();
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

  updateAvailableZones() {
    const usedIds = new Set((this.formData.zones as ZoneOrder[]).map(z => z.zoneId));
    this.availableZones.set(this.zones().filter(z => !usedIds.has(z.id)));
  }

  applyFilters() {
    let list = [...this.routes()];
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      list = list.filter(r => r.routeName.toLowerCase().includes(term));
    }
    this.filteredRoutes.set(list);
  }

  getZoneName(zoneId: string): string {
    return this.zones().find(z => z.id === zoneId)?.zoneName || zoneId;
  }

  getOperatorName(userId: string): string {
    const op = this.operators().find(u => u.id === userId);
    return op ? `${op.lastName}, ${op.firstName}` : userId || '—';
  }

  openModal() {
    this.editMode.set(false);
    this.formData = { routeName: '', responsibleUserId: '', totalEstimatedDuration: 0, zones: [] as ZoneOrder[] };
    this.newZone = { zoneId: '', estimatedDuration: 0 };
    this.updateAvailableZones();
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  edit(r: DistributionRoute) {
    this.editMode.set(true);
    this.selectedId = r.id;
    this.formData = {
      routeName: r.routeName,
      responsibleUserId: r.responsibleUserId,
      totalEstimatedDuration: r.totalEstimatedDuration,
      zones: (r.zones || []).map(z => ({ ...z }))
    };
    this.newZone = { zoneId: '', estimatedDuration: 0 };
    this.updateAvailableZones();
    this.showModal.set(true);
  }

  addZone() {
    if (!this.newZone.zoneId) return;
    const zones = this.formData.zones as ZoneOrder[];
    zones.push({
      zoneId: this.newZone.zoneId,
      order: zones.length + 1,
      estimatedDuration: this.newZone.estimatedDuration || 0
    });
    this.newZone = { zoneId: '', estimatedDuration: 0 };
    this.recalcDuration();
    this.updateAvailableZones();
  }

  removeZone(index: number) {
    const zones = this.formData.zones as ZoneOrder[];
    zones.splice(index, 1);
    zones.forEach((z, i) => z.order = i + 1);
    this.recalcDuration();
    this.updateAvailableZones();
  }

  moveZoneUp(index: number) {
    if (index === 0) return;
    const zones = this.formData.zones as ZoneOrder[];
    [zones[index - 1], zones[index]] = [zones[index], zones[index - 1]];
    zones.forEach((z, i) => z.order = i + 1);
  }

  moveZoneDown(index: number) {
    const zones = this.formData.zones as ZoneOrder[];
    if (index >= zones.length - 1) return;
    [zones[index], zones[index + 1]] = [zones[index + 1], zones[index]];
    zones.forEach((z, i) => z.order = i + 1);
  }

  recalcDuration() {
    const zones = this.formData.zones as ZoneOrder[];
    this.formData.totalEstimatedDuration = zones.reduce((sum, z) => sum + (z.estimatedDuration || 0), 0);
  }

  isFormValid(): boolean {
    return !!(this.formData.routeName && this.formData.responsibleUserId && this.formData.zones?.length > 0 && this.formData.totalEstimatedDuration > 0);
  }

  save() {
    const orgId = this.auth.organizationId();
    if (!orgId || !this.isFormValid()) return;

    const req: CreateRouteRequest = {
      organizationId: orgId,
      routeName: this.formData.routeName,
      zones: this.formData.zones,
      totalEstimatedDuration: Number(this.formData.totalEstimatedDuration),
      responsibleUserId: this.formData.responsibleUserId
    };

    const op = this.editMode()
      ? this.dist.updateRoute(this.selectedId, req)
      : this.dist.createRoute(req);

    op.subscribe({
      next: () => {
        this.alert.success('Éxito', `Ruta ${this.editMode() ? 'actualizada' : 'creada'}`);
        this.closeModal();
        this.load();
      },
      error: () => this.alert.error('Error', 'No se pudo guardar la ruta')
    });
  }

  deactivate(r: DistributionRoute) {
    if (!confirm(`¿Desactivar ruta "${r.routeName}"?`)) return;
    this.dist.deactivateRoute(r.id).subscribe({
      next: () => { this.alert.success('Éxito', 'Ruta desactivada'); this.load(); },
      error: () => this.alert.error('Error', 'No se pudo desactivar')
    });
  }

  restore(r: DistributionRoute) {
    this.dist.restoreRoute(r.id).subscribe({
      next: () => { this.alert.success('Éxito', 'Ruta restaurada'); this.load(); },
      error: () => this.alert.error('Error', 'No se pudo restaurar')
    });
  }
}
