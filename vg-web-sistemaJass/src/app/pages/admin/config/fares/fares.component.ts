import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import {
  LucideAngularModule, Plus, Trash2, RotateCcw,
  DollarSign, X, Save, Loader2, MapPin, Map, Filter
} from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { Fare, Zone, Street, ApiResponse } from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-fares',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Tarifas</h1>
        <p class="text-sm text-gray-500 mt-0.5">Gestión de tarifas por zona y calle</p>
      </div>

      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 bg-gray-50/50 border-b border-gray-100">
          <div class="flex flex-col sm:flex-row gap-3">
            <select
              [(ngModel)]="typeFilter"
              class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:border-transparent bg-white">
              <option value="ALL">Todos los tipos</option>
              @for (ft of fareTypeOptions; track ft.value) {
                @if (ft.value !== 'OTRO') {
                  <option [value]="ft.value">{{ ft.label }}</option>
                }
              }
              @for (ct of customFareTypes; track ct) {
                <option [value]="ct">{{ getFareTypeLabel(ct) }}</option>
              }
            </select>
            <select
              [(ngModel)]="statusFilter"
              class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-emerald-500 focus:border-transparent bg-white">
              <option value="ALL">Todos los estados</option>
              <option value="ACTIVE">Activos</option>
              <option value="INACTIVE">Inactivos</option>
            </select>
            <div class="flex-1"></div>
            <button
              (click)="openModal()"
              class="inline-flex items-center justify-center gap-2 px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-sm font-medium shadow-sm">
              <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
              Nueva Tarifa
            </button>
          </div>
        </div>

        @if (isLoading()) {
          <div class="p-16 text-center">
            <div class="inline-flex items-center gap-2.5 text-gray-500">
              <div class="w-5 h-5 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin"></div>
              <span class="text-sm">Cargando tarifas...</span>
            </div>
          </div>
        } @else if (filteredFares.length === 0) {
          <div class="p-16 text-center">
            <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <lucide-icon [img]="dollarIcon" [size]="32" class="text-gray-400"></lucide-icon>
            </div>
            <p class="text-gray-600 font-medium mb-1">No se encontraron tarifas</p>
            <p class="text-sm text-gray-400">Crea una nueva tarifa para comenzar</p>
          </div>
        } @else {
          <div class="hidden lg:block overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50/80 border-b border-gray-100">
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-14">#</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Tipo de Tarifa</th>
                  <th class="px-4 py-3 text-right text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-28">Monto (S/)</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Descripción</th>
                  <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-32">Vigencia</th>
                  <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-24">Estado</th>
                  <th class="px-4 py-3 text-right text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-28">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (fare of filteredFares; track fare.id; let i = $index) {
                  <tr class="hover:bg-emerald-50/30 transition-colors">
                    <td class="px-4 py-3.5">
                      <span class="inline-flex items-center justify-center w-7 h-7 bg-emerald-50 text-emerald-600 rounded-lg text-xs font-bold">{{ i + 1 }}</span>
                    </td>
                    <td class="px-4 py-3.5 font-semibold text-gray-800">{{ getFareTypeLabel(fare.fareType) }}</td>
                    <td class="px-4 py-3.5 text-right font-bold text-emerald-700 tabular-nums">S/. {{ fare.amount | number:'1.2-2' }}</td>
                    <td class="px-4 py-3.5 text-sm text-gray-500 max-w-xs truncate">{{ fare.description || '—' }}</td>
                    <td class="px-4 py-3.5 text-center text-xs text-gray-500">
                      @if (fare.validFrom || fare.validTo) {
                        <span>{{ fare.validFrom ? (fare.validFrom | date:'dd/MM/yy') : '—' }} → {{ fare.validTo ? (fare.validTo | date:'dd/MM/yy') : '∞' }}</span>
                      } @else {
                        <span class="text-gray-400">Permanente</span>
                      }
                    </td>
                    <td class="px-4 py-3.5 text-center">
                      <span class="inline-flex px-2.5 py-1 text-[11px] font-semibold rounded-full"
                            [class.bg-emerald-50]="isActive(fare.recordStatus)"
                            [class.text-emerald-700]="isActive(fare.recordStatus)"
                            [class.bg-red-50]="!isActive(fare.recordStatus)"
                            [class.text-red-600]="!isActive(fare.recordStatus)">
                        {{ isActive(fare.recordStatus) ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5 text-right">
                      <div class="flex items-center justify-end gap-1">
                        @if (isActive(fare.recordStatus)) {
                          <button (click)="deleteFare(fare)" class="p-2 text-red-500 hover:bg-red-100 rounded-lg transition-colors" title="Eliminar">
                            <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                          </button>
                        } @else {
                          <button (click)="restoreFare(fare)" class="p-2 text-emerald-600 hover:bg-emerald-100 rounded-lg transition-colors" title="Restaurar">
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
            @for (fare of filteredFares; track fare.id; let i = $index) {
              <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden hover:shadow-md transition-shadow">
                <div class="flex items-stretch">
                  <div class="w-11 bg-linear-to-b from-emerald-500 to-emerald-600 flex items-center justify-center text-white font-bold text-sm shrink-0">
                    {{ i + 1 }}
                  </div>
                  <div class="flex-1 p-3.5 min-w-0">
                    <div class="flex items-start justify-between gap-2 mb-1.5">
                      <h4 class="font-semibold text-gray-800 text-sm truncate">{{ getFareTypeLabel(fare.fareType) }}</h4>
                      <span class="inline-flex px-2 py-0.5 text-[10px] font-bold rounded-full shrink-0"
                            [class.bg-emerald-50]="isActive(fare.recordStatus)"
                            [class.text-emerald-700]="isActive(fare.recordStatus)"
                            [class.bg-red-50]="!isActive(fare.recordStatus)"
                            [class.text-red-600]="!isActive(fare.recordStatus)">
                        {{ isActive(fare.recordStatus) ? 'Activo' : 'Inactivo' }}
                      </span>
                    </div>
                    <div class="flex items-baseline gap-1 mb-1.5">
                      <span class="text-lg font-bold text-emerald-700">S/ {{ fare.amount | number:'1.2-2' }}</span>
                    </div>
                    @if (fare.description) {
                      <p class="text-xs text-gray-500 mb-1.5 line-clamp-2">{{ fare.description }}</p>
                    }
                    @if (fare.validFrom || fare.validTo) {
                      <p class="text-[11px] text-gray-400 mb-2">{{ fare.validFrom ? (fare.validFrom | date:'dd/MM/yy') : '—' }} → {{ fare.validTo ? (fare.validTo | date:'dd/MM/yy') : '∞' }}</p>
                    }
                    <div class="flex items-center justify-end gap-1 pt-1 border-t border-gray-50">
                      @if (isActive(fare.recordStatus)) {
                        <button (click)="deleteFare(fare)" class="p-1.5 text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                          <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restoreFare(fare)" class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors">
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
      </div>
    </div>

    @if (showModal()) {
      <div class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4" (click)="closeModal()">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden max-h-[90vh] flex flex-col" (click)="$event.stopPropagation()">
          <div class="border-b border-gray-100 bg-linear-to-r from-gray-50 to-white shrink-0">
            <div class="flex items-center gap-3 px-5 py-4">
              <div class="w-10 h-10 bg-emerald-100 rounded-xl flex items-center justify-center">
                <lucide-icon [img]="dollarIcon" [size]="20" class="text-emerald-600"></lucide-icon>
              </div>
              <div class="flex-1 min-w-0">
                <h2 class="text-lg font-bold text-gray-900">Nueva Tarifa</h2>
                <p class="text-xs text-gray-400">Configuración de tarifa por zona</p>
              </div>
              <button (click)="closeModal()" class="p-2 hover:bg-gray-100 rounded-xl transition-colors">
                <lucide-icon [img]="closeIcon" [size]="18" class="text-gray-400"></lucide-icon>
              </button>
            </div>
          </div>

          <form (ngSubmit)="saveFare()" class="flex-1 overflow-y-auto">
            <div class="p-5 space-y-4">
              <div>
                <label class="block text-sm font-semibold text-gray-800 mb-1.5">Tipo de tarifa <span class="text-red-500">*</span></label>
                <select
                  [(ngModel)]="fareForm.selectedType"
                  name="selectedType"
                  required
                  (ngModelChange)="onTypeChange()"
                  [style.color]="fareForm.selectedType ? '#111827' : '#9ca3af'"
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                  <option value="" disabled>Seleccionar tipo de tarifa</option>
                  @for (ft of fareTypeOptions; track ft.value) {
                    <option [value]="ft.value" style="color: #111827">{{ ft.label }}</option>
                  }
                </select>
              </div>

              @if (fareForm.selectedType === 'OTRO') {
                <div>
                  <label class="block text-sm font-semibold text-gray-800 mb-1.5">Nombre de la tarifa <span class="text-red-500">*</span></label>
                  <input
                    type="text"
                    [(ngModel)]="fareForm.customType"
                    name="customType"
                    required
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm uppercase placeholder:text-gray-400 placeholder:normal-case transition-all"
                    placeholder="Ej: LIMPIEZA_CANAL, CUOTA_EXTRA">
                  <p class="mt-1.5 text-xs text-gray-400 italic">Solo letras mayúsculas, números y guión bajo (A-Z, 0-9, _)</p>
                </div>
              }

              <div class="bg-gray-50 rounded-xl p-4 space-y-3 border border-gray-100">
                <div class="flex items-center gap-2 mb-1">
                  <lucide-icon [img]="mapIcon" [size]="16" class="text-gray-500"></lucide-icon>
                  <span class="text-sm font-semibold text-gray-700">Asociar a zona / calle</span>
                  <span class="text-xs text-gray-400">(opcional)</span>
                </div>
                <div>
                  <select
                    [(ngModel)]="fareForm.zoneId"
                    name="zoneId"
                    (ngModelChange)="onZoneChange()"
                    [style.color]="fareForm.zoneId ? '#111827' : '#9ca3af'"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                    <option value="">Sin zona específica</option>
                    @for (zone of zones(); track zone.id) {
                      <option [value]="zone.id" style="color: #111827">{{ zone.zoneName }}</option>
                    }
                  </select>
                </div>
                @if (fareForm.zoneId) {
                  <div>
                    <select
                      [(ngModel)]="fareForm.streetId"
                      name="streetId"
                      (ngModelChange)="onStreetChange()"
                      [style.color]="fareForm.streetId ? '#111827' : '#9ca3af'"
                      class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                      <option value="">Toda la zona</option>
                      @if (loadingStreets()) {
                        <option value="" disabled>Cargando calles...</option>
                      } @else {
                        @for (street of zoneStreets(); track street.id) {
                          <option [value]="street.id" style="color: #111827">{{ street.fullStreetName || (street.streetType + ' ' + street.streetName) }}</option>
                        }
                      }
                    </select>
                  </div>
                }
              </div>

              <div>
                <label class="block text-sm font-semibold text-gray-800 mb-1.5">Monto (S/) <span class="text-red-500">*</span></label>
                <input
                  type="number"
                  [(ngModel)]="fareForm.amount"
                  name="amount"
                  required
                  step="0.01"
                  min="0"
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm tabular-nums placeholder:text-gray-400 transition-all"
                  placeholder="0.00">
              </div>

              <div>
                <label class="block text-sm font-semibold text-gray-800 mb-1.5">Descripción</label>
                <textarea
                  [(ngModel)]="fareForm.description"
                  name="description"
                  rows="2"
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm resize-none placeholder:text-gray-400 transition-all"
                  placeholder="Descripción opcional de la tarifa"></textarea>
              </div>


            </div>

            <div class="flex items-center justify-end gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/30 shrink-0">
              <button
                type="button"
                (click)="closeModal()"
                class="px-4 py-2.5 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-xl transition-all text-sm font-medium">
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="isSubmitting()"
                class="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium shadow-sm">
                @if (isSubmitting()) {
                  <lucide-icon [img]="loaderIcon" [size]="18" class="animate-spin"></lucide-icon>
                  <span>Guardando...</span>
                } @else {
                  <lucide-icon [img]="saveIcon" [size]="18"></lucide-icon>
                  <span>Crear Tarifa</span>
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    }
  `
})
export class FaresComponent implements OnInit {
  private http = inject(HttpClient);
  private alertService = inject(AlertService);
  private authService = inject(AuthService);

  fares = signal<Fare[]>([]);
  zones = signal<Zone[]>([]);
  zoneStreets = signal<Street[]>([]);
  isLoading = signal(false);
  loadingStreets = signal(false);
  showModal = signal(false);
  editingFare = signal<Fare | null>(null);
  isSubmitting = signal(false);

  statusFilter = 'ALL';
  typeFilter = 'ALL';
  fareForm = {
    selectedType: '',
    customType: '',
    zoneId: '',
    streetId: '',
    amount: 0,
    description: ''
  };

  plusIcon = Plus;
  trashIcon = Trash2;
  restoreIcon = RotateCcw;
  dollarIcon = DollarSign;
  closeIcon = X;
  saveIcon = Save;
  loaderIcon = Loader2;
  mapPinIcon = MapPin;
  mapIcon = Map;
  filterIcon = Filter;

  fareTypeOptions = [
    { value: 'CUOTA_MENSUAL', label: 'Cuota Mensual' },
    { value: 'RECONEXION', label: 'Reconexión' },
    { value: 'INSTALACION', label: 'Instalación' },
    { value: 'REPARACION', label: 'Reparación' },
    { value: 'OTRO', label: 'Otro (personalizado)' }
  ];

  private get headers() {
    return { 'X-User-Id': this.authService.userId() || '', 'Content-Type': 'application/json' };
  }

  /** Fare types that are not predefined (user-created custom types) */
  get customFareTypes(): string[] {
    const predefined = new Set(this.fareTypeOptions.map(ft => ft.value));
    const custom = new Set<string>();
    for (const f of this.fares()) {
      if (!predefined.has(f.fareType)) custom.add(f.fareType);
    }
    return [...custom];
  }

  get filteredFares(): Fare[] {
    let items = this.fares();
    // Filter by type
    if (this.typeFilter !== 'ALL') items = items.filter(f => f.fareType === this.typeFilter);
    // Filter by status
    if (this.statusFilter === 'ACTIVE') items = items.filter(f => this.isActive(f.recordStatus));
    if (this.statusFilter === 'INACTIVE') items = items.filter(f => !this.isActive(f.recordStatus));
    // Sort newest first
    return items.sort((a, b) => (b.createdAt || '').localeCompare(a.createdAt || ''));
  }

  isActive(status: string): boolean {
    return status === 'ACTIVE';
  }

  getFareTypeLabel(type: string): string {
    const found = this.fareTypeOptions.find(f => f.value === type);
    if (found && found.value !== 'OTRO') return found.label;
    return type.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
  }

  ngOnInit(): void {
    this.loadFares();
    this.loadZones();
  }

  loadFares(): void {
    this.isLoading.set(true);
    const orgId = this.authService.organizationId();
    this.http.get<ApiResponse<Fare[]>>(`${environment.apiUrl}/fares/organization/${orgId}`).subscribe({
      next: res => {
        this.fares.set(res.data || []);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  loadZones(): void {
    const orgId = this.authService.organizationId();
    this.http.get<ApiResponse<Zone[]>>(`${environment.apiUrl}/zones/organization/${orgId}`).subscribe({
      next: res => this.zones.set((res.data || []).filter(z => z.recordStatus === 'ACTIVE')),
      error: () => { }
    });
  }

  onZoneChange(): void {
    this.fareForm.streetId = '';
    this.zoneStreets.set([]);
    if (this.fareForm.zoneId) {
      this.loadingStreets.set(true);
      this.http.get<ApiResponse<Street[]>>(`${environment.apiUrl}/streets/zone/${this.fareForm.zoneId}`).subscribe({
        next: res => {
          this.zoneStreets.set((res.data || []).filter(s => s.recordStatus === 'ACTIVE'));
          this.loadingStreets.set(false);
        },
        error: () => this.loadingStreets.set(false)
      });
    }
    this.autoDescription();
  }

  onStreetChange(): void {
    this.autoDescription();
  }

  onTypeChange(): void {
    if (this.fareForm.selectedType !== 'OTRO') {
      this.fareForm.customType = '';
    }
  }

  autoDescription(): void {
    const parts: string[] = [];
    if (this.fareForm.zoneId) {
      const zone = this.zones().find(z => z.id === this.fareForm.zoneId);
      if (zone) parts.push(zone.zoneName);
    }
    if (this.fareForm.streetId) {
      const street = this.zoneStreets().find(s => s.id === this.fareForm.streetId);
      if (street) parts.push(street.fullStreetName || `${street.streetType} ${street.streetName}`);
    }
    if (parts.length > 0) {
      this.fareForm.description = parts.join(' — ');
    }
  }

  openModal(): void {
    this.editingFare.set(null);
    this.fareForm = {
      selectedType: '',
      customType: '',
      zoneId: '',
      streetId: '',
      amount: 0,
      description: ''
    };
    this.zoneStreets.set([]);
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.editingFare.set(null);
  }

  private resolveFareType(): string {
    if (this.fareForm.selectedType === 'OTRO') {
      return this.fareForm.customType.trim().toUpperCase().replace(/\s+/g, '_').replace(/[^A-Z0-9_]/g, '');
    }
    return this.fareForm.selectedType;
  }

  async saveFare(): Promise<void> {
    const fareType = this.resolveFareType();

    if (!fareType || this.fareForm.amount <= 0) {
      this.alertService.warning('Campos requeridos', 'Seleccione un tipo de tarifa y un monto válido');
      return;
    }

    if (fareType.length < 2 || fareType.length > 50 || !/^[A-Z0-9_]+$/.test(fareType)) {
      this.alertService.warning('Tipo inválido', 'El tipo de tarifa debe tener entre 2 y 50 caracteres (A-Z, 0-9, _)');
      return;
    }

    this.isSubmitting.set(true);
    const orgId = this.authService.organizationId();
    const now = new Date().toISOString().substring(0, 19);

    try {
      const description = this.fareForm.description.trim() || undefined;

      // Deactivate existing active fares of same type AND same description (zone/street)
      const existingActive = this.fares()
        .filter(f => f.fareType === fareType
          && this.isActive(f.recordStatus)
          && (f.description || '') === (description || ''));

      for (const old of existingActive) {
        // Set validTo on the old fare
        await firstValueFrom(
          this.http.put<ApiResponse<Fare>>(
            `${environment.apiUrl}/fares/${old.id}`,
            { fareType: old.fareType, amount: old.amount, description: old.description, validTo: now },
            { headers: this.headers }
          )
        );
        // Soft-delete (INACTIVE)
        await firstValueFrom(
          this.http.delete<ApiResponse<Fare>>(`${environment.apiUrl}/fares/${old.id}`, {
            headers: this.headers,
            body: { reason: 'Reemplazada por nueva tarifa' }
          })
        );
      }

      // Create new fare with validFrom = now
      const body: any = {
        organizationId: orgId,
        fareType,
        amount: this.fareForm.amount,
        description,
        validFrom: now
      };

      await firstValueFrom(
        this.http.post<ApiResponse<Fare>>(`${environment.apiUrl}/fares`, body, { headers: this.headers })
      );
      this.alertService.success('Creado', 'Tarifa creada correctamente');

      this.closeModal();
      this.loadFares();
    } catch {
      this.alertService.error('Error', 'No se pudo guardar la tarifa');
    } finally {
      this.isSubmitting.set(false);
    }
  }

  async deleteFare(fare: Fare): Promise<void> {
    const name = this.getFareTypeLabel(fare.fareType);
    const result = await this.alertService.confirmDelete(name);
    if (result.isConfirmed) {
      this.http.delete<ApiResponse<Fare>>(`${environment.apiUrl}/fares/${fare.id}`, {
        headers: this.headers,
        body: { reason: 'Eliminado por el administrador' }
      }).subscribe({
        next: () => {
          this.alertService.success('Eliminado', 'Tarifa eliminada correctamente');
          this.loadFares();
        },
        error: () => this.alertService.error('Error', 'No se pudo eliminar la tarifa')
      });
    }
  }

  async restoreFare(fare: Fare): Promise<void> {
    const name = this.getFareTypeLabel(fare.fareType);
    const result = await this.alertService.confirmRestore(name);
    if (result.isConfirmed) {
      this.http.patch<ApiResponse<Fare>>(
        `${environment.apiUrl}/fares/${fare.id}/restore`,
        {},
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Restaurado', 'Tarifa restaurada correctamente');
          this.loadFares();
        },
        error: () => this.alertService.error('Error', 'No se pudo restaurar la tarifa')
      });
    }
  }
}
