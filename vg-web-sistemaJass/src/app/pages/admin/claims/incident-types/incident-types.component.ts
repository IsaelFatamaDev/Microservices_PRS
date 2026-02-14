import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule, AlertTriangle, Search, Plus, Edit, Trash2, X, RotateCcw
} from 'lucide-angular';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { ClaimsService } from '../../../../core/services/claims.service';
import { IncidentType, CreateIncidentTypeRequest, UpdateIncidentTypeRequest } from '../../../../core/models/claims.model';

@Component({
  selector: 'app-incident-types',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Tipos de Incidencias</h1>
          <p class="text-sm text-gray-500 mt-1">Configuración de catálogo de tipos de incidencias</p>
        </div>
        <button (click)="openCreateModal()" class="px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white rounded-xl font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          <span>Nuevo Tipo</span>
        </button>
      </div>

      <!-- Search -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
        <div class="relative">
          <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
          <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar por nombre..."
                 class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-400">
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50/80">
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Nombre</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Descripción</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Prioridad</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Tiempo Est.</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Servicio Ext.</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (type of filteredTypes(); track type.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3 text-sm font-medium text-gray-800">{{ type.typeName }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ type.description }}</td>
                  <td class="px-4 py-3">
                    <span [class]="getPriorityBadge(type.priorityLevel)" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ getPriorityLabel(type.priorityLevel) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ type.estimatedResolutionTime }}h</td>
                  <td class="px-4 py-3">
                    <span [class]="type.requiresExternalService ? 'bg-blue-50 text-blue-700' : 'bg-gray-100 text-gray-600'" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ type.requiresExternalService ? 'Sí' : 'No' }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <span [class]="type.recordStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-gray-100 text-gray-600'" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ type.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="openEditModal(type)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-blue-600 transition-colors" title="Editar">
                        <lucide-icon [img]="editIcon" [size]="15"></lucide-icon>
                      </button>
                      @if (type.recordStatus === 'ACTIVE') {
                        <button (click)="deleteType(type)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-red-600 transition-colors" title="Eliminar">
                          <lucide-icon [img]="trashIcon" [size]="15"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restoreType(type)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-emerald-600 transition-colors" title="Restaurar">
                          <lucide-icon [img]="restoreIcon" [size]="15"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="7" class="text-center py-12 text-gray-400 text-sm">No se encontraron tipos de incidencias</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- Create/Edit Modal -->
      @if (showModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">{{ isEditing() ? 'Editar' : 'Nuevo' }} Tipo de Incidencia</h3>
              <button (click)="closeModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="saveType()" class="p-6 space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Nombre del Tipo *</label>
                <input type="text" [(ngModel)]="formData.typeName" name="typeName" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400" placeholder="Ej: Fuga de agua">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
                <textarea [(ngModel)]="formData.description" name="description" rows="3" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 resize-none" placeholder="Describe el tipo de incidencia"></textarea>
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Nivel de Prioridad *</label>
                  <select [(ngModel)]="formData.priorityLevel" name="priorityLevel" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccionar...</option>
                    <option value="LOW">Baja</option>
                    <option value="MEDIUM">Media</option>
                    <option value="HIGH">Alta</option>
                    <option value="CRITICAL">Crítica</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tiempo Est. de Resolución (horas)</label>
                  <input type="number" [(ngModel)]="formData.estimatedResolutionTime" name="estimatedResolutionTime" min="0" step="0.5" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400" placeholder="8">
                </div>
              </div>
              <div class="flex items-center gap-2">
                <input type="checkbox" [(ngModel)]="formData.requiresExternalService" name="requiresExternalService" id="requiresExternalService" class="w-4 h-4 text-violet-600 rounded">
                <label for="requiresExternalService" class="text-sm text-gray-700 cursor-pointer">Requiere Servicio Externo</label>
              </div>
              <div class="flex gap-3 pt-4">
                <button type="button" (click)="closeModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">
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
    </div>
  `
})
export class IncidentTypesComponent implements OnInit {
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private claimsService = inject(ClaimsService);

  // Icons
  alertIcon = AlertTriangle; searchIcon = Search; plusIcon = Plus; editIcon = Edit;
  trashIcon = Trash2; xIcon = X; restoreIcon = RotateCcw;

  // Data signals
  allTypes = signal<IncidentType[]>([]);

  // Filters
  searchTerm = '';

  // Modals
  showModal = signal(false);
  isEditing = signal(false);
  selectedType = signal<IncidentType | null>(null);
  saving = signal(false);

  // Form data
  formData: any = {};

  // Computed
  filteredTypes = computed(() => {
    let list = this.allTypes();
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(t => t.typeName.toLowerCase().includes(q));
    }
    return list.sort((a, b) => a.typeName.localeCompare(b.typeName));
  });

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.claimsService.getIncidentTypesByOrg(orgId).subscribe({
      next: r => this.allTypes.set(r.data || [])
    });
  }

  openCreateModal(): void {
    this.formData = {
      typeName: '',
      description: '',
      priorityLevel: '',
      estimatedResolutionTime: 0,
      requiresExternalService: false
    };
    this.isEditing.set(false);
    this.selectedType.set(null);
    this.showModal.set(true);
  }

  openEditModal(type: IncidentType): void {
    this.formData = {
      typeName: type.typeName,
      description: type.description,
      priorityLevel: type.priorityLevel,
      estimatedResolutionTime: type.estimatedResolutionTime,
      requiresExternalService: type.requiresExternalService
    };
    this.isEditing.set(true);
    this.selectedType.set(type);
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.isEditing.set(false);
    this.selectedType.set(null);
    this.formData = {};
  }

  saveType(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.saving.set(true);

    if (this.isEditing()) {
      const type = this.selectedType();
      if (!type) return;

      const req: UpdateIncidentTypeRequest = this.formData;
      this.claimsService.updateIncidentType(type.id, req).subscribe({
        next: () => {
          this.alertService.success('Tipo actualizado exitosamente');
          this.closeModal();
          this.loadData();
          this.saving.set(false);
        },
        error: () => {
          this.alertService.error('Error al actualizar el tipo');
          this.saving.set(false);
        }
      });
    } else {
      // Generate typeCode from typeName (e.g., "Fuga de agua" -> "IT-FUGA-DE-AGUA")
      const typeCode = 'IT-' + this.formData.typeName.toUpperCase().replace(/\s+/g, '-');
      const req: CreateIncidentTypeRequest = { ...this.formData, typeCode, organizationId: orgId };
      console.log('🔍 Creating incident type:', req, 'OrgID:', orgId);
      this.claimsService.createIncidentType(req).subscribe({
        next: () => {
          this.alertService.success('Tipo creado exitosamente');
          this.closeModal();
          this.loadData();
          this.saving.set(false);
        },
        error: () => {
          this.alertService.error('Error al crear el tipo');
          this.saving.set(false);
        }
      });
    }
  }

  deleteType(type: IncidentType): void {
    if (!confirm(`¿Estás seguro de que deseas eliminar "${type.typeName}"?`)) return;

    this.claimsService.deleteIncidentType(type.id).subscribe({
      next: () => {
        this.alertService.success('Tipo eliminado exitosamente');
        this.loadData();
      },
      error: () => {
        this.alertService.error('Error al eliminar el tipo');
      }
    });
  }

  restoreType(type: IncidentType): void {
    this.claimsService.restoreIncidentType(type.id).subscribe({
      next: () => {
        this.alertService.success('Tipo restaurado exitosamente');
        this.loadData();
      },
      error: () => {
        this.alertService.error('Error al restaurar el tipo');
      }
    });
  }

  getPriorityLabel(p: string): string {
    const map: Record<string, string> = { LOW: 'Baja', MEDIUM: 'Media', HIGH: 'Alta', CRITICAL: 'Crítica' };
    return map[p] || p;
  }

  getPriorityBadge(p: string): string {
    const map: Record<string, string> = {
      LOW: 'bg-gray-100 text-gray-700',
      MEDIUM: 'bg-amber-50 text-amber-700',
      HIGH: 'bg-orange-50 text-orange-700',
      CRITICAL: 'bg-red-50 text-red-700'
    };
    return map[p] || 'bg-gray-100 text-gray-600';
  }
}
