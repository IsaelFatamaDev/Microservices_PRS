import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import {
  LucideAngularModule, Plus, Trash2, RotateCcw,
  Settings, X, Save, Loader2
} from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { Parameter, ApiResponse } from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-parameters',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Parámetros</h1>
        <p class="text-sm text-gray-500 mt-0.5">Configuración de parámetros del sistema</p>
      </div>

      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 bg-gray-50/50 border-b border-gray-100">
          <div class="flex flex-col sm:flex-row gap-3">
            <select
              [(ngModel)]="typeFilter"
              class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent bg-white">
              <option value="ALL">Todos los tipos</option>
              @for (pt of parameterTypeOptions; track pt.value) {
                <option [value]="pt.value">{{ pt.label }}</option>
              }
            </select>
            <select
              [(ngModel)]="statusFilter"
              class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent bg-white">
              <option value="ALL">Todos los estados</option>
              <option value="ACTIVE">Activos</option>
              <option value="INACTIVE">Inactivos</option>
            </select>
            <div class="flex-1"></div>
            <button
              (click)="openModal()"
              class="inline-flex items-center justify-center gap-2 px-4 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 transition-colors text-sm font-medium shadow-sm">
              <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
              Nuevo Parámetro
            </button>
          </div>
        </div>

        @if (isLoading()) {
          <div class="p-16 text-center">
            <div class="inline-flex items-center gap-2.5 text-gray-500">
              <div class="w-5 h-5 border-2 border-amber-600 border-t-transparent rounded-full animate-spin"></div>
              <span class="text-sm">Cargando parámetros...</span>
            </div>
          </div>
        } @else if (filteredParameters.length === 0) {
          <div class="p-16 text-center">
            <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <lucide-icon [img]="settingsIcon" [size]="32" class="text-gray-400"></lucide-icon>
            </div>
            <p class="text-gray-600 font-medium mb-1">No se encontraron parámetros</p>
            <p class="text-sm text-gray-400">Crea un nuevo parámetro para comenzar</p>
          </div>
        } @else {
          <div class="hidden lg:block overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50/80 border-b border-gray-100">
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-14">#</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Tipo de Parámetro</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Valor</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Descripción</th>
                  <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-24">Estado</th>
                  <th class="px-4 py-3 text-right text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-28">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (param of filteredParameters; track param.id; let i = $index) {
                  <tr class="hover:bg-amber-50/30 transition-colors">
                    <td class="px-4 py-3.5">
                      <span class="inline-flex items-center justify-center w-7 h-7 bg-amber-50 text-amber-600 rounded-lg text-xs font-bold">{{ i + 1 }}</span>
                    </td>
                    <td class="px-4 py-3.5 font-semibold text-gray-800">{{ getParameterTypeLabel(param.parameterType) }}</td>
                    <td class="px-4 py-3.5">
                      <span class="inline-flex px-2.5 py-1 bg-slate-100 text-slate-700 rounded-lg text-sm font-mono font-medium">{{ param.parameterValue }}</span>
                    </td>
                    <td class="px-4 py-3.5 text-sm text-gray-500 max-w-xs truncate">{{ param.description || '—' }}</td>
                    <td class="px-4 py-3.5 text-center">
                      <span class="inline-flex px-2.5 py-1 text-[11px] font-semibold rounded-full"
                            [class.bg-emerald-50]="isActive(param.recordStatus)"
                            [class.text-emerald-700]="isActive(param.recordStatus)"
                            [class.bg-red-50]="!isActive(param.recordStatus)"
                            [class.text-red-600]="!isActive(param.recordStatus)">
                        {{ isActive(param.recordStatus) ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5 text-right">
                      <div class="flex items-center justify-end gap-1">
                        @if (isActive(param.recordStatus)) {
                          <button (click)="deleteParam(param)" class="p-2 text-red-500 hover:bg-red-100 rounded-lg transition-colors" title="Eliminar">
                            <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                          </button>
                        } @else {
                          <button (click)="restoreParam(param)" class="p-2 text-emerald-600 hover:bg-emerald-100 rounded-lg transition-colors" title="Restaurar">
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
            @for (param of filteredParameters; track param.id; let i = $index) {
              <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden hover:shadow-md transition-shadow">
                <div class="flex items-stretch">
                  <div class="w-11 bg-linear-to-b from-amber-500 to-amber-600 flex items-center justify-center text-white font-bold text-sm shrink-0">
                    {{ i + 1 }}
                  </div>
                  <div class="flex-1 p-3.5 min-w-0">
                    <div class="flex items-start justify-between gap-2 mb-1.5">
                      <h4 class="font-semibold text-gray-800 text-sm truncate">{{ getParameterTypeLabel(param.parameterType) }}</h4>
                      <span class="inline-flex px-2 py-0.5 text-[10px] font-bold rounded-full shrink-0"
                            [class.bg-emerald-50]="isActive(param.recordStatus)"
                            [class.text-emerald-700]="isActive(param.recordStatus)"
                            [class.bg-red-50]="!isActive(param.recordStatus)"
                            [class.text-red-600]="!isActive(param.recordStatus)">
                        {{ isActive(param.recordStatus) ? 'Activo' : 'Inactivo' }}
                      </span>
                    </div>
                    <div class="mb-1.5">
                      <span class="inline-flex px-2 py-1 bg-slate-100 text-slate-700 rounded-lg text-sm font-mono font-medium">{{ param.parameterValue }}</span>
                    </div>
                    @if (param.description) {
                      <p class="text-xs text-gray-500 mb-2 line-clamp-2">{{ param.description }}</p>
                    }
                    <div class="flex items-center justify-end gap-1 pt-1 border-t border-gray-50">
                      @if (isActive(param.recordStatus)) {
                        <button (click)="deleteParam(param)" class="p-1.5 text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                          <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restoreParam(param)" class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors">
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
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden" (click)="$event.stopPropagation()">
          <div class="border-b border-gray-100 bg-linear-to-r from-gray-50 to-white">
            <div class="flex items-center gap-3 px-5 py-4">
              <div class="w-10 h-10 bg-amber-100 rounded-xl flex items-center justify-center">
                <lucide-icon [img]="settingsIcon" [size]="20" class="text-amber-600"></lucide-icon>
              </div>
              <div class="flex-1 min-w-0">
                <h2 class="text-lg font-bold text-gray-900">Nuevo Parámetro</h2>
                <p class="text-xs text-gray-400">Configuración del sistema</p>
              </div>
              <button (click)="closeModal()" class="p-2 hover:bg-gray-100 rounded-xl transition-colors">
                <lucide-icon [img]="closeIcon" [size]="18" class="text-gray-400"></lucide-icon>
              </button>
            </div>
          </div>

          <form (ngSubmit)="saveParam()" class="p-5 space-y-4">
            <div>
              <label class="block text-sm font-semibold text-gray-800 mb-1.5">Tipo de parámetro <span class="text-red-500">*</span></label>
              <select
                [(ngModel)]="paramForm.parameterType"
                name="parameterType"
                required
                [style.color]="paramForm.parameterType ? '#111827' : '#9ca3af'"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                <option value="" disabled>Seleccionar tipo de parámetro</option>
                @for (pt of parameterTypeOptions; track pt.value) {
                  <option [value]="pt.value" style="color: #111827">{{ pt.label }}</option>
                }
              </select>
              @if (getHint(paramForm.parameterType); as hint) {
                <p class="mt-1.5 text-xs text-gray-400 italic">{{ hint }}</p>
              }
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-800 mb-1.5">Valor <span class="text-red-500">*</span></label>
              <input
                type="text"
                [(ngModel)]="paramForm.parameterValue"
                name="parameterValue"
                required
                class="w-full px-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm font-mono placeholder:text-gray-400 transition-all"
                [placeholder]="getPlaceholder(paramForm.parameterType)">
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-800 mb-1.5">Descripción</label>
              <textarea
                [(ngModel)]="paramForm.description"
                name="description"
                rows="2"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm resize-none placeholder:text-gray-400 transition-all"
                placeholder="Descripción opcional del parámetro"></textarea>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-100">
              <button
                type="button"
                (click)="closeModal()"
                class="px-4 py-2.5 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-xl transition-all text-sm font-medium">
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="isSubmitting()"
                class="inline-flex items-center gap-2 px-5 py-2.5 bg-amber-600 text-white rounded-xl hover:bg-amber-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium shadow-sm">
                @if (isSubmitting()) {
                  <lucide-icon [img]="loaderIcon" [size]="18" class="animate-spin"></lucide-icon>
                  <span>Guardando...</span>
                } @else {
                  <lucide-icon [img]="saveIcon" [size]="18"></lucide-icon>
                  <span>Crear</span>
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    }
  `
})
export class ParametersComponent implements OnInit {
  private http = inject(HttpClient);
  private alertService = inject(AlertService);
  private authService = inject(AuthService);

  parameters = signal<Parameter[]>([]);
  isLoading = signal(false);
  showModal = signal(false);
  editingParam = signal<Parameter | null>(null);
  isSubmitting = signal(false);

  statusFilter = 'ALL';
  typeFilter = 'ALL';
  paramForm = { parameterType: '', parameterValue: '', description: '' };

  plusIcon = Plus;
  trashIcon = Trash2;
  restoreIcon = RotateCcw;
  settingsIcon = Settings;
  closeIcon = X;
  saveIcon = Save;
  loaderIcon = Loader2;

  parameterTypeOptions = [
    { value: 'MESES_MAX_DEUDA', label: 'Meses Máx. Deuda' },
    { value: 'DIA_PAGO', label: 'Día de Pago' },
    { value: 'INSTANCIA_WHATSAPP', label: 'Instancia de WhatsApp' },
    { value: 'RUC_ORGANIZACION', label: 'RUC de la Organización' }
  ];

  private hints: Record<string, string> = {
    MESES_MAX_DEUDA: 'Número máximo de meses de deuda antes de generar alerta o corte',
    DIA_PAGO: 'Día del mes establecido para el pago (1–28)',
    INSTANCIA_WHATSAPP: 'Nombre de la instancia en Evolution API, ej: jass-whatsapp',
    RUC_ORGANIZACION: 'RUC de 11 dígitos de la organización'
  };

  private placeholders: Record<string, string> = {
    MESES_MAX_DEUDA: 'Ej: 3',
    DIA_PAGO: 'Ej: 15',
    INSTANCIA_WHATSAPP: 'Ej: jass-whatsapp',
    RUC_ORGANIZACION: 'Ej: 20123456789'
  };

  private get headers() {
    return { 'X-User-Id': this.authService.userId() || '', 'Content-Type': 'application/json' };
  }

  get filteredParameters(): Parameter[] {
    let items = this.parameters();
    // Filter by type
    if (this.typeFilter !== 'ALL') items = items.filter(p => p.parameterType === this.typeFilter);
    // Filter by status
    if (this.statusFilter === 'ACTIVE') items = items.filter(p => this.isActive(p.recordStatus));
    if (this.statusFilter === 'INACTIVE') items = items.filter(p => !this.isActive(p.recordStatus));
    // Sort newest first
    return items.sort((a, b) => (b.createdAt || '').localeCompare(a.createdAt || ''));
  }

  isActive(status: string): boolean {
    return status === 'ACTIVE';
  }

  getParameterTypeLabel(type: string): string {
    return this.parameterTypeOptions.find(p => p.value === type)?.label || type;
  }

  getHint(type: string): string {
    return this.hints[type] || '';
  }

  getPlaceholder(type: string): string {
    return this.placeholders[type] || 'Ingrese el valor';
  }

  ngOnInit(): void {
    this.loadParameters();
  }

  loadParameters(): void {
    this.isLoading.set(true);
    const orgId = this.authService.organizationId();
    this.http.get<ApiResponse<Parameter[]>>(`${environment.apiUrl}/parameters/organization/${orgId}`).subscribe({
      next: res => {
        this.parameters.set(res.data || []);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  openModal(): void {
    this.editingParam.set(null);
    this.paramForm = { parameterType: '', parameterValue: '', description: '' };
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.editingParam.set(null);
  }

  async saveParam(): Promise<void> {
    if (!this.paramForm.parameterType || !this.paramForm.parameterValue.trim()) {
      this.alertService.warning('Campos requeridos', 'Seleccione un tipo y un valor para el parámetro');
      return;
    }

    this.isSubmitting.set(true);
    const orgId = this.authService.organizationId();

    try {
      // Deactivate existing active params of same type, then create new
      const existingActive = this.parameters()
        .filter(p => p.parameterType === this.paramForm.parameterType && this.isActive(p.recordStatus));

      for (const old of existingActive) {
        await firstValueFrom(
          this.http.delete<ApiResponse<Parameter>>(`${environment.apiUrl}/parameters/${old.id}`, {
            headers: this.headers,
            body: { reason: 'Reemplazado por nuevo parámetro' }
          })
        );
      }

      // Create new parameter
      await firstValueFrom(
        this.http.post<ApiResponse<Parameter>>(`${environment.apiUrl}/parameters`, {
          organizationId: orgId,
          parameterType: this.paramForm.parameterType,
          parameterValue: this.paramForm.parameterValue.trim(),
          description: this.paramForm.description.trim() || undefined
        }, { headers: this.headers })
      );
      this.alertService.success('Creado', 'Parámetro creado correctamente');

      this.closeModal();
      this.loadParameters();
    } catch {
      this.alertService.error('Error', 'No se pudo guardar el parámetro');
    } finally {
      this.isSubmitting.set(false);
    }
  }

  async deleteParam(param: Parameter): Promise<void> {
    const name = this.getParameterTypeLabel(param.parameterType);
    const result = await this.alertService.confirmDelete(name);
    if (result.isConfirmed) {
      this.http.delete<ApiResponse<Parameter>>(`${environment.apiUrl}/parameters/${param.id}`, {
        headers: this.headers,
        body: { reason: 'Eliminado por el administrador' }
      }).subscribe({
        next: () => {
          this.alertService.success('Eliminado', 'Parámetro eliminado correctamente');
          this.loadParameters();
        },
        error: () => this.alertService.error('Error', 'No se pudo eliminar el parámetro')
      });
    }
  }

  async restoreParam(param: Parameter): Promise<void> {
    const name = this.getParameterTypeLabel(param.parameterType);
    const result = await this.alertService.confirmRestore(name);
    if (result.isConfirmed) {
      this.http.patch<ApiResponse<Parameter>>(
        `${environment.apiUrl}/parameters/${param.id}/restore`,
        {},
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Restaurado', 'Parámetro restaurado correctamente');
          this.loadParameters();
        },
        error: () => this.alertService.error('Error', 'No se pudo restaurar el parámetro')
      });
    }
  }
}
