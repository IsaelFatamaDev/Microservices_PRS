import { Component, inject, signal, computed, OnInit, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule, Search, Plus, Eye, X, Edit, Trash2, RotateCcw,
  ChevronLeft, ChevronRight, MapPin, Droplets
} from 'lucide-angular';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { WaterQualityService } from '../../../../core/services/water-quality.service';
import { TestingPoint, CreateTestingPointRequest, UpdateTestingPointRequest } from '../../../../core/models/water-quality.model';
import { ApiResponse } from '../../../../core/models/api-response.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

interface Zone {
  id: string;
  zoneName: string;
}

@Component({
  selector: 'app-testing-points',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Puntos de Muestreo</h1>
          <p class="text-sm text-gray-500 mt-1">Gestión de puntos de muestreo para análisis de calidad de agua</p>
        </div>
        <button (click)="openCreateModal()" class="px-4 py-2.5 bg-cyan-600 hover:bg-cyan-700 text-white rounded-xl font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          <span>Nuevo Punto</span>
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Total</span>
            <div class="p-2 rounded-xl bg-cyan-50"><lucide-icon [img]="mapPinIcon" [size]="16" class="text-cyan-600"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-gray-800">{{ allPoints().length }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Activos</span>
            <div class="p-2 rounded-xl bg-emerald-50"><lucide-icon [img]="dropletIcon" [size]="16" class="text-emerald-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-emerald-600">{{ activeCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Inactivos</span>
            <div class="p-2 rounded-xl bg-red-50"><lucide-icon [img]="dropletIcon" [size]="16" class="text-red-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-red-600">{{ inactiveCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Tipos</span>
            <div class="p-2 rounded-xl bg-violet-50"><lucide-icon [img]="mapPinIcon" [size]="16" class="text-violet-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-violet-600">{{ uniqueTypes() }}</p>
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center gap-3">
          <div class="relative flex-1">
            <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
            <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar por nombre o ubicación..."
                   class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-cyan-500/20 focus:border-cyan-400 outline-none transition-all placeholder:text-gray-400">
          </div>
          <select [(ngModel)]="typeFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todos los Tipos</option>
            <option value="RED">Red</option>
            <option value="FUENTE">Fuente</option>
            <option value="RESERVORIO">Reservorio</option>
            <option value="DOMICILIO">Domicilio</option>
            <option value="OTRO">Otro</option>
          </select>
          <select [(ngModel)]="statusFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todos</option>
            <option value="ACTIVE">Activos</option>
            <option value="INACTIVE">Inactivos</option>
          </select>
        </div>

        <!-- Desktop table -->
        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50/80">
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Nombre</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Tipo</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Zona</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Ubicación</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (point of paginatedPoints(); track point.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3 text-sm font-medium text-cyan-700">{{ point.pointName }}</td>
                  <td class="px-4 py-3">
                    <span class="text-xs px-2 py-1 rounded-lg font-medium" [class]="getTypeBadge(point.pointType)">
                      {{ getTypeLabel(point.pointType) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ getZoneName(point.zoneId) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ point.location || '—' }}</td>
                  <td class="px-4 py-3">
                    <span class="text-xs px-2 py-1 rounded-lg font-medium" [class]="getStatusBadge(point.recordStatus)">
                      {{ point.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="viewDetail(point)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-cyan-600 transition-colors" title="Ver detalle">
                        <lucide-icon [img]="eyeIcon" [size]="15"></lucide-icon>
                      </button>
                      @if (point.recordStatus === 'ACTIVE') {
                        <button (click)="openEditModal(point)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-blue-600 transition-colors" title="Editar">
                          <lucide-icon [img]="editIcon" [size]="15"></lucide-icon>
                        </button>
                        <button (click)="confirmDelete(point)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-red-600 transition-colors" title="Eliminar">
                          <lucide-icon [img]="trashIcon" [size]="15"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restorePoint(point)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-emerald-600 transition-colors" title="Restaurar">
                          <lucide-icon [img]="restoreIcon" [size]="15"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="6" class="text-center py-12 text-gray-400 text-sm">No se encontraron puntos de muestreo</td></tr>
              }
            </tbody>
          </table>
        </div>

        <!-- Mobile cards -->
        <div class="md:hidden divide-y divide-gray-50">
          @for (point of paginatedPoints(); track point.id) {
            <div class="p-4 space-y-2">
              <div class="flex items-center justify-between">
                <span class="text-sm font-medium text-cyan-700">{{ point.pointName }}</span>
                <span class="text-xs px-2 py-1 rounded-lg font-medium" [class]="getStatusBadge(point.recordStatus)">
                  {{ point.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                </span>
              </div>
              <div class="flex items-center gap-2">
                <span class="text-xs px-2 py-1 rounded-lg font-medium" [class]="getTypeBadge(point.pointType)">{{ getTypeLabel(point.pointType) }}</span>
                <span class="text-xs text-gray-400">{{ getZoneName(point.zoneId) }}</span>
              </div>
              @if (point.location) {
                <p class="text-xs text-gray-500">{{ point.location }}</p>
              }
              <div class="flex gap-2 pt-1">
                <button (click)="viewDetail(point)" class="flex-1 py-1.5 text-xs border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 transition-colors">Ver detalle</button>
                @if (point.recordStatus === 'ACTIVE') {
                  <button (click)="openEditModal(point)" class="flex-1 py-1.5 text-xs border border-blue-200 rounded-lg text-blue-600 hover:bg-blue-50 transition-colors">Editar</button>
                  <button (click)="confirmDelete(point)" class="flex-1 py-1.5 text-xs border border-red-200 rounded-lg text-red-600 hover:bg-red-50 transition-colors">Eliminar</button>
                } @else {
                  <button (click)="restorePoint(point)" class="flex-1 py-1.5 text-xs border border-emerald-200 rounded-lg text-emerald-600 hover:bg-emerald-50 transition-colors">Restaurar</button>
                }
              </div>
            </div>
          } @empty {
            <div class="p-8 text-center text-gray-400 text-sm">No se encontraron puntos de muestreo</div>
          }
        </div>

        <!-- Pagination -->
        @if (filteredPoints().length > pageSize) {
          <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
            <span>{{ (currentPage() - 1) * pageSize + 1 }} - {{ Math.min(currentPage() * pageSize, filteredPoints().length) }} de {{ filteredPoints().length }}</span>
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

      <!-- Create / Edit Modal -->
      @if (showFormModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeFormModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">{{ editingPoint() ? 'Editar Punto de Muestreo' : 'Nuevo Punto de Muestreo' }}</h3>
              <button (click)="closeFormModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="savePoint()" class="p-6 space-y-4">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Nombre del Punto *</label>
                  <input type="text" [(ngModel)]="formData.pointName" name="pointName" required
                         class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-cyan-500/20 focus:border-cyan-400"
                         placeholder="Ej: Punto Red Principal">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tipo de Punto *</label>
                  <select [(ngModel)]="formData.pointType" name="pointType" required
                          class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-cyan-500/20 focus:border-cyan-400">
                    <option value="">Seleccionar...</option>
                    <option value="RED">Red</option>
                    <option value="FUENTE">Fuente</option>
                    <option value="RESERVORIO">Reservorio</option>
                    <option value="DOMICILIO">Domicilio</option>
                    <option value="OTRO">Otro</option>
                  </select>
                </div>
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Zona</label>
                  <select [(ngModel)]="formData.zoneId" name="zoneId"
                          class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-cyan-500/20 focus:border-cyan-400">
                    <option value="">Sin zona</option>
                    @for (zone of allZones(); track zone.id) {
                      <option [value]="zone.id">{{ zone.zoneName }}</option>
                    }
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Ubicación</label>
                  <input type="text" [(ngModel)]="formData.location" name="location"
                         class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-cyan-500/20 focus:border-cyan-400"
                         placeholder="Ej: Av. Principal 123">
                </div>
              </div>
              <!-- Mapa para seleccionar ubicación -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Seleccionar Ubicación en el Mapa</label>
                <p class="text-xs text-gray-400 mb-2">Haz clic en el mapa para marcar la ubicación del punto de muestreo</p>
                <div id="locationMap" class="w-full h-64 rounded-xl border border-gray-200 z-0"></div>
                <div class="grid grid-cols-2 gap-4 mt-2">
                  <div class="flex items-center gap-2">
                    <span class="text-xs font-medium text-gray-500">Lat:</span>
                    <span class="text-sm text-gray-700">{{ formData.latitude || '—' }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <span class="text-xs font-medium text-gray-500">Lng:</span>
                    <span class="text-sm text-gray-700">{{ formData.longitude || '—' }}</span>
                  </div>
                </div>
              </div>
              <div class="flex gap-3 pt-4">
                <button type="button" (click)="closeFormModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">
                  Cancelar
                </button>
                <button type="submit" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-cyan-600 hover:bg-cyan-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                  {{ saving() ? 'Guardando...' : (editingPoint() ? 'Actualizar' : 'Guardar') }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Detail Modal -->
      @if (showDetailModal() && selectedPoint()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeDetailModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-3xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Detalle del Punto de Muestreo</h3>
              <button (click)="closeDetailModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <div class="p-6">
              <div class="flex gap-6" [class.flex-col]="!selectedPoint()!.latitude || !selectedPoint()!.longitude">
                <!-- Info -->
                <div class="flex-1 space-y-3">
                  <div class="grid grid-cols-2 gap-3">
                    <div><p class="text-xs text-gray-400">Nombre</p><p class="text-sm font-medium text-cyan-700">{{ selectedPoint()!.pointName }}</p></div>
                    <div><p class="text-xs text-gray-400">Tipo</p>
                      <span class="text-xs px-2 py-1 rounded-lg font-medium inline-block" [class]="getTypeBadge(selectedPoint()!.pointType)">{{ getTypeLabel(selectedPoint()!.pointType) }}</span>
                    </div>
                    <div><p class="text-xs text-gray-400">Zona</p><p class="text-sm text-gray-700">{{ getZoneName(selectedPoint()!.zoneId) }}</p></div>
                    <div><p class="text-xs text-gray-400">Estado</p>
                      <span class="text-xs px-2 py-1 rounded-lg font-medium inline-block" [class]="getStatusBadge(selectedPoint()!.recordStatus)">{{ selectedPoint()!.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}</span>
                    </div>
                    <div><p class="text-xs text-gray-400">Ubicación</p><p class="text-sm text-gray-700">{{ selectedPoint()!.location || '—' }}</p></div>
                    <div><p class="text-xs text-gray-400">Coordenadas</p><p class="text-sm text-gray-700">{{ selectedPoint()!.latitude && selectedPoint()!.longitude ? selectedPoint()!.latitude + ', ' + selectedPoint()!.longitude : '—' }}</p></div>
                    <div><p class="text-xs text-gray-400">Creado</p><p class="text-sm text-gray-700">{{ selectedPoint()!.createdAt | date:'dd/MM/yyyy HH:mm' }}</p></div>
                    <div><p class="text-xs text-gray-400">Actualizado</p><p class="text-sm text-gray-700">{{ selectedPoint()!.updatedAt ? (selectedPoint()!.updatedAt | date:'dd/MM/yyyy HH:mm') : '—' }}</p></div>
                  </div>
                </div>
                <!-- Mapa -->
                @if (selectedPoint()!.latitude && selectedPoint()!.longitude) {
                  <div class="w-full sm:w-72 shrink-0">
                    <p class="text-xs font-medium text-gray-500 mb-1.5">Ubicación en el mapa</p>
                    <div id="detailMapView" class="w-full h-56 rounded-xl border border-gray-200 z-0"></div>
                  </div>
                }
              </div>
            </div>
          </div>
        </div>
      }

      <!-- Delete Confirmation Modal -->
      @if (showDeleteModal() && selectedPoint()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeDeleteModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-md w-full" (click)="$event.stopPropagation()">
            <div class="p-6 text-center space-y-4">
              <div class="mx-auto w-12 h-12 rounded-full bg-red-50 flex items-center justify-center">
                <lucide-icon [img]="trashIcon" [size]="24" class="text-red-500"></lucide-icon>
              </div>
              <div>
                <h3 class="text-lg font-semibold text-gray-800">Eliminar Punto de Muestreo</h3>
                <p class="text-sm text-gray-500 mt-1">¿Está seguro de eliminar <strong>{{ selectedPoint()!.pointName }}</strong>? Esta acción se puede revertir.</p>
              </div>
              <div class="flex gap-3">
                <button (click)="closeDeleteModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">Cancelar</button>
                <button (click)="deletePoint()" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-red-600 hover:bg-red-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50">
                  {{ saving() ? 'Eliminando...' : 'Eliminar' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class TestingPointsComponent implements OnInit, AfterViewChecked {
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private wqService = inject(WaterQualityService);
  private http = inject(HttpClient);

  private map: any = null;
  private marker: any = null;
  private mapInitPending = false;
  private detailMap: any = null;
  private detailMapInitPending = false;

  // Icons
  searchIcon = Search; plusIcon = Plus; eyeIcon = Eye; xIcon = X;
  editIcon = Edit; trashIcon = Trash2; restoreIcon = RotateCcw;
  chevronLeftIcon = ChevronLeft; chevronRightIcon = ChevronRight;
  mapPinIcon = MapPin; dropletIcon = Droplets;

  Math = Math;

  // Data
  allPoints = signal<TestingPoint[]>([]);
  allZones = signal<Zone[]>([]);

  // Filters
  searchTerm = '';
  typeFilter = '';
  statusFilter = '';

  // Pagination
  currentPage = signal(1);
  pageSize = 15;

  // Modals
  showFormModal = signal(false);
  showDetailModal = signal(false);
  showDeleteModal = signal(false);
  selectedPoint = signal<TestingPoint | null>(null);
  editingPoint = signal<TestingPoint | null>(null);
  saving = signal(false);

  // Form
  formData: any = {};

  // Computed
  activeCount = computed(() => this.allPoints().filter(p => p.recordStatus === 'ACTIVE').length);
  inactiveCount = computed(() => this.allPoints().filter(p => p.recordStatus === 'INACTIVE').length);
  uniqueTypes = computed(() => new Set(this.allPoints().map(p => p.pointType)).size);

  filteredPoints = computed(() => {
    let list = this.allPoints();
    if (this.statusFilter) list = list.filter(p => p.recordStatus === this.statusFilter);
    if (this.typeFilter) list = list.filter(p => p.pointType === this.typeFilter);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(p =>
        p.pointName.toLowerCase().includes(q) ||
        (p.location && p.location.toLowerCase().includes(q))
      );
    }
    return list.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredPoints().length / this.pageSize));
  paginatedPoints = computed(() => {
    const s = (this.currentPage() - 1) * this.pageSize;
    return this.filteredPoints().slice(s, s + this.pageSize);
  });

  ngOnInit(): void {
    this.loadData();
  }

  ngAfterViewChecked(): void {
    if (this.mapInitPending) {
      const container = document.getElementById('locationMap');
      if (container && !this.map) {
        this.mapInitPending = false;
        this.initMap();
      }
    }
    if (this.detailMapInitPending) {
      const container = document.getElementById('detailMapView');
      if (container && !this.detailMap) {
        this.detailMapInitPending = false;
        this.initDetailMap();
      }
    }
  }

  private async initMap(): Promise<void> {
    const L = await import('leaflet');

    const lat = this.formData.latitude ? parseFloat(this.formData.latitude) : -6.77;
    const lng = this.formData.longitude ? parseFloat(this.formData.longitude) : -76.12;
    const zoom = this.formData.latitude ? 15 : 8;

    this.map = L.map('locationMap').setView([lat, lng], zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    const defaultIcon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41]
    });

    if (this.formData.latitude && this.formData.longitude) {
      this.marker = L.marker([lat, lng], { icon: defaultIcon }).addTo(this.map);
    }

    this.map.on('click', (e: any) => {
      const { lat: clat, lng: clng } = e.latlng;
      this.formData.latitude = clat.toFixed(6);
      this.formData.longitude = clng.toFixed(6);
      if (this.marker) {
        this.marker.setLatLng([clat, clng]);
      } else {
        this.marker = L.marker([clat, clng], { icon: defaultIcon }).addTo(this.map);
      }
    });

    setTimeout(() => this.map?.invalidateSize(), 200);
  }

  private destroyMap(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
      this.marker = null;
    }
  }

  private async initDetailMap(): Promise<void> {
    const point = this.selectedPoint();
    if (!point?.latitude || !point?.longitude) return;

    const L = await import('leaflet');
    const lat = parseFloat(point.latitude);
    const lng = parseFloat(point.longitude);

    this.detailMap = L.map('detailMapView', {
      zoomControl: true,
      dragging: true,
      scrollWheelZoom: true
    }).setView([lat, lng], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.detailMap);

    const icon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41]
    });

    L.marker([lat, lng], { icon }).addTo(this.detailMap);
    setTimeout(() => this.detailMap?.invalidateSize(), 200);
  }

  private destroyDetailMap(): void {
    if (this.detailMap) {
      this.detailMap.remove();
      this.detailMap = null;
    }
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.wqService.getTestingPointsByOrg(orgId).subscribe({
      next: r => this.allPoints.set(r.data || [])
    });

    this.http.get<ApiResponse<Zone[]>>(`${environment.apiUrl}/zones`).subscribe({
      next: r => this.allZones.set(r.data || [])
    });
  }

  // ============================================================================
  // MODALS
  // ============================================================================

  openCreateModal(): void {
    this.editingPoint.set(null);
    this.formData = { pointName: '', pointType: '', zoneId: '', location: '', latitude: '', longitude: '' };
    this.destroyMap();
    this.showFormModal.set(true);
    this.mapInitPending = true;
  }

  openEditModal(point: TestingPoint): void {
    this.editingPoint.set(point);
    this.formData = {
      pointName: point.pointName,
      pointType: point.pointType,
      zoneId: point.zoneId || '',
      location: point.location || '',
      latitude: point.latitude || '',
      longitude: point.longitude || ''
    };
    this.destroyMap();
    this.showFormModal.set(true);
    this.mapInitPending = true;
  }

  closeFormModal(): void {
    this.destroyMap();
    this.showFormModal.set(false);
    this.editingPoint.set(null);
    this.formData = {};
  }

  viewDetail(point: TestingPoint): void {
    this.destroyDetailMap();
    this.selectedPoint.set(point);
    this.showDetailModal.set(true);
    if (point.latitude && point.longitude) {
      this.detailMapInitPending = true;
    }
  }

  closeDetailModal(): void {
    this.destroyDetailMap();
    this.showDetailModal.set(false);
    this.selectedPoint.set(null);
  }

  confirmDelete(point: TestingPoint): void {
    this.selectedPoint.set(point);
    this.showDeleteModal.set(true);
  }

  closeDeleteModal(): void {
    this.showDeleteModal.set(false);
    this.selectedPoint.set(null);
  }

  // ============================================================================
  // CRUD
  // ============================================================================

  savePoint(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.saving.set(true);
    const editing = this.editingPoint();

    if (editing) {
      const req: UpdateTestingPointRequest = { ...this.formData, organizationId: orgId };
      this.wqService.updateTestingPoint(editing.id, req).subscribe({
        next: () => {
          this.alertService.success('Punto de muestreo actualizado exitosamente');
          this.closeFormModal();
          this.loadData();
          this.saving.set(false);
        },
        error: () => {
          this.alertService.error('Error al actualizar el punto de muestreo');
          this.saving.set(false);
        }
      });
    } else {
      const req: CreateTestingPointRequest = { ...this.formData, organizationId: orgId };
      this.wqService.createTestingPoint(req).subscribe({
        next: () => {
          this.alertService.success('Punto de muestreo creado exitosamente');
          this.closeFormModal();
          this.loadData();
          this.saving.set(false);
        },
        error: () => {
          this.alertService.error('Error al crear el punto de muestreo');
          this.saving.set(false);
        }
      });
    }
  }

  deletePoint(): void {
    const point = this.selectedPoint();
    if (!point) return;

    this.saving.set(true);
    this.wqService.deleteTestingPoint(point.id).subscribe({
      next: () => {
        this.alertService.success('Punto de muestreo eliminado exitosamente');
        this.closeDeleteModal();
        this.loadData();
        this.saving.set(false);
      },
      error: () => {
        this.alertService.error('Error al eliminar el punto de muestreo');
        this.saving.set(false);
      }
    });
  }

  restorePoint(point: TestingPoint): void {
    this.wqService.restoreTestingPoint(point.id).subscribe({
      next: () => {
        this.alertService.success('Punto de muestreo restaurado exitosamente');
        this.loadData();
      },
      error: () => {
        this.alertService.error('Error al restaurar el punto de muestreo');
      }
    });
  }

  // ============================================================================
  // HELPERS
  // ============================================================================

  getZoneName(zoneId: string): string {
    if (!zoneId) return '—';
    return this.allZones().find(z => z.id === zoneId)?.zoneName || zoneId;
  }

  getTypeLabel(type: string): string {
    const map: Record<string, string> = { RED: 'Red', FUENTE: 'Fuente', RESERVORIO: 'Reservorio', DOMICILIO: 'Domicilio', OTRO: 'Otro' };
    return map[type] || type;
  }

  getTypeBadge(type: string): string {
    const map: Record<string, string> = {
      RED: 'bg-blue-50 text-blue-700',
      FUENTE: 'bg-cyan-50 text-cyan-700',
      RESERVORIO: 'bg-violet-50 text-violet-700',
      DOMICILIO: 'bg-amber-50 text-amber-700',
      OTRO: 'bg-gray-100 text-gray-600'
    };
    return map[type] || 'bg-gray-100 text-gray-600';
  }

  getStatusBadge(status: string): string {
    return status === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-red-50 text-red-700';
  }
}
