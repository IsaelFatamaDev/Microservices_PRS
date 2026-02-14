import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Tag, Plus, Edit, Trash2, Search, RotateCcw } from 'lucide-angular';
import { AuthService, AlertService, InventoryService } from '../../../../core/services';
import { ProductCategory } from '../../../../core/models';

@Component({
     selector: 'app-categories',
     standalone: true,
     imports: [CommonModule, FormsModule, LucideAngularModule],
     template: `
    <div class="p-6">
      <div class="mb-6 flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <lucide-icon [img]="tagIcon" [size]="28"></lucide-icon>
            Categorías de Productos
          </h1>
          <p class="text-gray-500">Gestión de categorías para materiales</p>
        </div>
        <button (click)="openModal()" class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl flex items-center gap-2">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          Nueva Categoría
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <div class="bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90 mb-1">Total Categorías</p>
          <p class="text-3xl font-bold">{{ categories().length }}</p>
        </div>
        <div class="bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90 mb-1">Categorías Activas</p>
          <p class="text-3xl font-bold">{{ activeCount() }}</p>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-4 mb-4">
        <div class="relative">
          <lucide-icon [img]="searchIcon" [size]="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
          <input [(ngModel)]="searchTerm" (ngModelChange)="applyFilters()" type="text" placeholder="Buscar categorías..." class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl">
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Descripción</th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Estado</th>
                <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              @for (cat of filteredCategories(); track cat.id) {
                <tr class="hover:bg-gray-50">
                  <td class="px-4 py-3">
                    <div class="flex items-center gap-2">
                      <lucide-icon [img]="tagIcon" [size]="18" class="text-violet-500"></lucide-icon>
                      <span class="font-medium">{{ cat.categoryName }}</span>
                    </div>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ cat.description || '-' }}</td>
                  <td class="px-4 py-3 text-center">
                    <span [class]="cat.recordStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-gray-100 text-gray-600'" class="px-2 py-1 text-xs rounded-lg font-medium">
                      {{ cat.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <div class="flex justify-end gap-2">
                      <button (click)="edit(cat)" class="p-2 hover:bg-violet-50 rounded-lg text-violet-600">
                        <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                      </button>
                      @if (cat.recordStatus === 'ACTIVE') {
                        <button (click)="delete(cat.id!)" class="p-2 hover:bg-red-50 rounded-lg text-red-600">
                          <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restore(cat.id!)" class="p-2 hover:bg-emerald-50 rounded-lg text-emerald-600">
                          <lucide-icon [img]="rotateIcon" [size]="16"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="4" class="text-center py-12 text-gray-400">No hay categorías registradas</td></tr>
              }
            </tbody>
          </table>
        </div>

        <div class="md:hidden divide-y divide-gray-100">
          @for (cat of filteredCategories(); track cat.id) {
            <div class="p-4">
              <div class="flex items-start justify-between mb-2">
                <div class="flex items-center gap-2">
                  <lucide-icon [img]="tagIcon" [size]="18" class="text-violet-500"></lucide-icon>
                  <span class="font-medium">{{ cat.categoryName }}</span>
                </div>
                <span [class]="cat.recordStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700' : 'bg-gray-100 text-gray-600'" class="px-2 py-1 text-xs rounded-lg">
                  {{ cat.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                </span>
              </div>
              <p class="text-sm text-gray-600 mb-3">{{ cat.description || 'Sin descripción' }}</p>
              <div class="flex gap-2">
                <button (click)="edit(cat)" class="flex-1 px-3 py-2 bg-violet-50 text-violet-600 rounded-lg text-sm">Editar</button>
                @if (cat.recordStatus === 'ACTIVE') {
                  <button (click)="delete(cat.id!)" class="px-3 py-2 bg-red-50 text-red-600 rounded-lg text-sm">Eliminar</button>
                } @else {
                  <button (click)="restore(cat.id!)" class="px-3 py-2 bg-emerald-50 text-emerald-600 rounded-lg text-sm">Restaurar</button>
                }
              </div>
            </div>
          }
        </div>
      </div>

      @if (showModal()) {
        <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl max-w-lg w-full p-6" (click)="$event.stopPropagation()">
            <h3 class="text-lg font-bold mb-4">{{ editMode() ? 'Editar' : 'Nueva' }} Categoría</h3>

            <div class="space-y-3">
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Nombre de la Categoría *</label>
                <input [(ngModel)]="formData.categoryName" type="text" placeholder="Ej: Materiales de Construcción" class="w-full px-3 py-2 border border-gray-100 rounded-xl placeholder:text-gray-400">
              </div>

              <div>
                <label class="text-sm text-gray-600 mb-1 block">Descripción</label>
                <textarea [(ngModel)]="formData.description" rows="3" placeholder="Descripción de la categoría..." class="w-full px-3 py-2 border border-gray-100 rounded-xl resize-none placeholder:text-gray-400"></textarea>
              </div>
            </div>

            <div class="flex gap-2 mt-6">
              <button (click)="closeModal()" class="flex-1 px-4 py-2 border rounded-xl">Cancelar</button>
              <button (click)="save()" [disabled]="!isFormValid()" class="flex-1 px-4 py-2 bg-violet-600 text-white rounded-xl disabled:opacity-50">
                {{ editMode() ? 'Actualizar' : 'Crear' }}
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class CategoriesComponent {
     private auth = inject(AuthService);
     private alert = inject(AlertService);
     private inv = inject(InventoryService);

     tagIcon = Tag; plusIcon = Plus; editIcon = Edit; trashIcon = Trash2;
     searchIcon = Search; rotateIcon = RotateCcw;

     categories = signal<ProductCategory[]>([]);
     filteredCategories = signal<ProductCategory[]>([]);

     searchTerm = '';
     showModal = signal(false);
     editMode = signal(false);
     selectedId = signal<string>('');
     formData: any = {};

     activeCount = computed(() =>
          this.categories().filter(c => c.recordStatus === 'ACTIVE').length
     );

     ngOnInit() {
          this.load();
     }

     load() {
          this.inv.getCategories().subscribe(data => {
               const orgId = this.auth.organizationId();
               const filtered = data.filter(c => c.organizationId === orgId);
               this.categories.set(filtered);
               this.applyFilters();
          });
     }

     applyFilters() {
          let filtered = [...this.categories()];

          if (this.searchTerm) {
               const term = this.searchTerm.toLowerCase();
               filtered = filtered.filter(c =>
                    c.categoryName.toLowerCase().includes(term) ||
                    c.description?.toLowerCase().includes(term)
               );
          }

          this.filteredCategories.set(filtered);
     }

     openModal() {
          this.formData = { categoryName: '', description: '' };
          this.editMode.set(false);
          this.showModal.set(true);
     }

     closeModal() {
          this.showModal.set(false);
     }

     edit(cat: ProductCategory) {
          this.formData = { categoryName: cat.categoryName, description: cat.description };
          this.selectedId.set(cat.id || '');
          this.editMode.set(true);
          this.showModal.set(true);
     }

     isFormValid() {
          return this.formData.categoryName?.trim();
     }

     save() {
          const orgId = this.auth.organizationId();
          if (!orgId || !this.isFormValid()) return;

          const req = {
               organizationId: orgId,
               categoryName: this.formData.categoryName.trim(),
               description: this.formData.description?.trim() || ''
          };

          const obs = this.editMode()
               ? this.inv.updateCategory(this.selectedId(), req)
               : this.inv.createCategory(req);

          obs.subscribe({
               next: () => {
                    this.alert.success('Éxito', `Categoría ${this.editMode() ? 'actualizada' : 'creada'}`);
                    this.closeModal();
                    this.load();
               },
               error: () => this.alert.error('Error', 'No se pudo guardar la categoría')
          });
     }

     delete(id: string) {
          if (!confirm('¿Eliminar esta categoría?')) return;

          this.inv.deleteCategory(id).subscribe({
               next: () => {
                    this.alert.success('Éxito', 'Categoría eliminada');
                    this.load();
               },
               error: () => this.alert.error('Error', 'No se pudo eliminar')
          });
     }

     restore(id: string) {
          this.inv.restoreCategory(id).subscribe({
               next: () => {
                    this.alert.success('Éxito', 'Categoría restaurada');
                    this.load();
               },
               error: () => this.alert.error('Error', 'No se pudo restaurar')
          });
     }
}
