import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule, FolderTree, Search, Plus, Edit, Trash2, X, RotateCcw
} from 'lucide-angular';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { ClaimsService } from '../../../../core/services/claims.service';
import { ComplaintCategory, CreateComplaintCategoryRequest, UpdateComplaintCategoryRequest } from '../../../../core/models/claims.model';

@Component({
  selector: 'app-complaint-categories',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Categorías de Quejas</h1>
          <p class="text-sm text-gray-500 mt-1">Configuración de catálogo de categorías para quejas</p>
        </div>
        <button (click)="openCreateModal()" class="px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white rounded-xl font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          <span>Nueva Categoría</span>
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
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Tiempo Máx.</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (cat of filteredCategories(); track cat.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3 text-sm font-medium text-gray-800">{{ cat.categoryName }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ cat.description }}</td>
                  <td class="px-4 py-3">
                    <span [class]="getPriorityBadge(cat.priorityLevel)" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ getPriorityLabel(cat.priorityLevel) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ cat.maxResponseTime }}h</td>
                  <td class="px-4 py-3">
                    <span [class]="cat.recordStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-gray-100 text-gray-600'" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ cat.recordStatus === 'ACTIVE' ? 'Activa' : 'Inactiva' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="openEditModal(cat)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-blue-600 transition-colors" title="Editar">
                        <lucide-icon [img]="editIcon" [size]="15"></lucide-icon>
                      </button>
                      @if (cat.recordStatus === 'ACTIVE') {
                        <button (click)="deleteCategory(cat)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-red-600 transition-colors" title="Eliminar">
                          <lucide-icon [img]="trashIcon" [size]="15"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restoreCategory(cat)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-emerald-600 transition-colors" title="Restaurar">
                          <lucide-icon [img]="restoreIcon" [size]="15"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="6" class="text-center py-12 text-gray-400 text-sm">No se encontraron categorías</td></tr>
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
              <h3 class="text-lg font-semibold text-gray-800">{{ isEditing() ? 'Editar' : 'Nueva' }} Categoría de Queja</h3>
              <button (click)="closeModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="saveCategory()" class="p-6 space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Nombre de la Categoría *</label>
                <input type="text" [(ngModel)]="formData.categoryName" name="categoryName" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400" placeholder="Ej: Calidad del agua">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
                <textarea [(ngModel)]="formData.description" name="description" rows="3" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 resize-none" placeholder="Describe la categoría"></textarea>
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Nivel de Prioridad *</label>
                  <select [(ngModel)]="formData.priorityLevel" name="priorityLevel" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccionar...</option>
                    <option value="LOW">Baja</option>
                    <option value="MEDIUM">Media</option>
                    <option value="HIGH">Alta</option>
                    <option value="URGENT">Urgente</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tiempo Máx. de Respuesta (horas)</label>
                  <input type="number" [(ngModel)]="formData.maxResponseTime" name="maxResponseTime" min="0" step="1" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400" placeholder="24">
                </div>
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
export class ComplaintCategoriesComponent implements OnInit {
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private claimsService = inject(ClaimsService);

  // Icons
  categoryIcon = FolderTree; searchIcon = Search; plusIcon = Plus; editIcon = Edit;
  trashIcon = Trash2; xIcon = X; restoreIcon = RotateCcw;

  // Data signals
  allCategories = signal<ComplaintCategory[]>([]);

  // Filters
  searchTerm = '';

  // Modals
  showModal = signal(false);
  isEditing = signal(false);
  selectedCategory = signal<ComplaintCategory | null>(null);
  saving = signal(false);

  // Form data
  formData: any = {};

  // Computed
  filteredCategories = computed(() => {
    let list = this.allCategories();
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(c => c.categoryName.toLowerCase().includes(q));
    }
    return list.sort((a, b) => a.categoryName.localeCompare(b.categoryName));
  });

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.claimsService.getComplaintCategoriesByOrg(orgId).subscribe({
      next: r => this.allCategories.set(r.data || [])
    });
  }

  openCreateModal(): void {
    this.formData = {
      categoryName: '',
      description: '',
      priorityLevel: '',
      maxResponseTime: 0
    };
    this.isEditing.set(false);
    this.selectedCategory.set(null);
    this.showModal.set(true);
  }

  openEditModal(cat: ComplaintCategory): void {
    this.formData = {
      categoryName: cat.categoryName,
      description: cat.description,
      priorityLevel: cat.priorityLevel,
      maxResponseTime: cat.maxResponseTime
    };
    this.isEditing.set(true);
    this.selectedCategory.set(cat);
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.isEditing.set(false);
    this.selectedCategory.set(null);
    this.formData = {};
  }

  saveCategory(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.saving.set(true);

    if (this.isEditing()) {
      const cat = this.selectedCategory();
      if (!cat) return;

      const req: UpdateComplaintCategoryRequest = this.formData;
      this.claimsService.updateComplaintCategory(cat.id, req).subscribe({
        next: () => {
          this.alertService.success('Categoría actualizada exitosamente');
          this.closeModal();
          this.loadData();
          this.saving.set(false);
        },
        error: () => {
          this.alertService.error('Error al actualizar la categoría');
          this.saving.set(false);
        }
      });
    } else {
      // Generate categoryCode from categoryName (e.g., "Calidad del agua" -> "CC-CALIDAD-DEL-AGUA")
      const categoryCode = 'CC-' + this.formData.categoryName.toUpperCase().replace(/\s+/g, '-');
      const req: CreateComplaintCategoryRequest = { ...this.formData, categoryCode, organizationId: orgId };
      console.log('🔍 Creating category with request:', req);
      console.log('🔍 Organization ID:', orgId);
      this.claimsService.createComplaintCategory(req).subscribe({
        next: () => {
          this.alertService.success('Categoría creada exitosamente');
          this.closeModal();
          this.loadData();
          this.saving.set(false);
        },
        error: () => {
          this.alertService.error('Error al crear la categoría');
          this.saving.set(false);
        }
      });
    }
  }

  deleteCategory(cat: ComplaintCategory): void {
    if (!confirm(`¿Estás seguro de que deseas eliminar "${cat.categoryName}"?`)) return;

    this.claimsService.deleteComplaintCategory(cat.id).subscribe({
      next: () => {
        this.alertService.success('Categoría eliminada exitosamente');
        this.loadData();
      },
      error: () => {
        this.alertService.error('Error al eliminar la categoría');
      }
    });
  }

  restoreCategory(cat: ComplaintCategory): void {
    this.claimsService.restoreComplaintCategory(cat.id).subscribe({
      next: () => {
        this.alertService.success('Categoría restaurada exitosamente');
        this.loadData();
      },
      error: () => {
        this.alertService.error('Error al restaurar la categoría');
      }
    });
  }

  getPriorityLabel(p: string): string {
    const map: Record<string, string> = { LOW: 'Baja', MEDIUM: 'Media', HIGH: 'Alta', URGENT: 'Urgente' };
    return map[p] || p;
  }

  getPriorityBadge(p: string): string {
    const map: Record<string, string> = {
      LOW: 'bg-gray-100 text-gray-700',
      MEDIUM: 'bg-amber-50 text-amber-700',
      HIGH: 'bg-orange-50 text-orange-700',
      URGENT: 'bg-red-50 text-red-700'
    };
    return map[p] || 'bg-gray-100 text-gray-600';
  }
}
