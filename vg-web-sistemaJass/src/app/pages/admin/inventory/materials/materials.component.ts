import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Package, Plus, Edit, Trash2, Search, AlertCircle } from 'lucide-angular';
import { AuthService, AlertService, InventoryService } from '../../../../core/services';
import { Material, CreateMaterialRequest, ProductCategory } from '../../../../core/models';

@Component({
  selector: 'app-materials',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="p-6">
      <div class="mb-6 flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <lucide-icon [img]="packageIcon" [size]="28"></lucide-icon>
            Materiales e Insumos
          </h1>
          <p class="text-gray-500">Gestión del inventario</p>
        </div>
        <button (click)="openModal()" class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl flex items-center gap-2">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          Nuevo Material
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div class="bg-linear-to-br from-violet-500 to-purple-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Total Materiales</p>
          <p class="text-3xl font-bold mt-1">{{ materials().length }}</p>
        </div>
        <div class="bg-linear-to-br from-amber-500 to-orange-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Stock Bajo</p>
          <p class="text-3xl font-bold mt-1">{{ lowStockCount() }}</p>
        </div>
        <div class="bg-linear-to-br from-emerald-500 to-teal-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Valor Total</p>
          <p class="text-3xl font-bold mt-1">S/ {{ totalValue().toFixed(2) }}</p>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-4 mb-4">
        <div class="relative">
          <lucide-icon [img]="searchIcon" [size]="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
          <input [(ngModel)]="searchTerm" (ngModelChange)="applyFilters()" type="text" placeholder="Buscar materiales..." class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 placeholder:text-gray-300">
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">#</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Material</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Categoría</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Unidad</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Costo Unit.</th>
                <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              @for (m of filteredMaterials(); track m.id; let i = $index) {
                <tr class="hover:bg-gray-50">
                  <td class="px-4 py-3 text-sm text-gray-500">{{ i + 1 }}</td>
                  <td class="px-4 py-3">
                    <div class="text-sm font-medium text-gray-800">{{ m.materialName }}</div>
                  </td>
                  <td class="px-4 py-3 text-sm">
                    @if (getCategoryName(m.categoryId)) {
                      <span class="inline-flex items-center px-2 py-0.5 rounded-lg bg-violet-50 text-violet-700 text-xs font-medium">
                        {{ getCategoryName(m.categoryId) }}
                      </span>
                    } @else {
                      <span class="text-gray-400">-</span>
                    }
                  </td>
                  <td class="px-4 py-3">
                    <div class="flex items-center gap-2">
                      <span class="text-sm font-medium">{{ m.currentStock }}</span>
                      @if (m.currentStock <= m.minStock) {
                        <lucide-icon [img]="alertIcon" [size]="14" class="text-red-600" title="Stock bajo"></lucide-icon>
                      }
                    </div>
                    <div class="text-xs text-gray-500">Mín: {{ m.minStock }}</div>
                  </td>
                  <td class="px-4 py-3 text-sm">{{ m.unit }}</td>
                  <td class="px-4 py-3 text-sm font-medium">S/ {{ m.unitPrice.toFixed(2) }}</td>
                  <td class="px-4 py-3 text-center">
                    <button (click)="edit(m)" class="p-1 hover:bg-blue-50 text-blue-600 rounded">
                      <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                    </button>
                    <button (click)="delete(m)" class="p-1 hover:bg-red-50 text-red-600 rounded ml-1">
                      <lucide-icon [img]="trash2Icon" [size]="16"></lucide-icon>
                    </button>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="7" class="text-center py-12 text-gray-400">No hay materiales</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>

      @if (showModal()) {
        <div class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <h3 class="text-lg font-bold text-gray-800">{{ editMode() ? 'Editar' : 'Nuevo' }} Material</h3>
            </div>
            <div class="p-6">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div class="md:col-span-2">
                  <label class="text-sm text-gray-600 mb-1 block">Nombre *</label>
                  <input [(ngModel)]="formData.materialName" placeholder="Nombre del material" class="w-full px-3 py-2 border border-gray-200 rounded-xl placeholder:text-gray-300 outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                  @if (duplicateNameError) {
                    <p class="text-xs text-red-500 mt-1">Ya existe un material con ese nombre</p>
                  }
                </div>

                <div>
                  <label class="text-sm text-gray-600 mb-1 block">Categoría *</label>
                  <select [(ngModel)]="formData.categoryId" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccione...</option>
                    @for (c of categories(); track c.id) {
                      <option [value]="c.id">{{ c.categoryName }}</option>
                    }
                  </select>
                </div>
                <div>
                  <label class="text-sm text-gray-600 mb-1 block">Unidad *</label>
                  <select [(ngModel)]="formData.unit" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccione...</option>
                    <option value="UNIDAD">Unidad</option>
                    <option value="KILOGRAMO">Kilogramo</option>
                    <option value="GRAMO">Gramo</option>
                    <option value="LITRO">Litro</option>
                    <option value="METRO">Metro</option>
                    <option value="CAJA">Caja</option>
                    <option value="PAQUETE">Paquete</option>
                  </select>
                </div>
                <div>
                  <label class="text-sm text-gray-600 mb-1 block">Stock Mínimo *</label>
                  <input [(ngModel)]="formData.minStock" type="number" min="0" placeholder="0" class="w-full px-3 py-2 border border-gray-200 rounded-xl placeholder:text-gray-300 outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                </div>
                <div>
                  <label class="text-sm text-gray-600 mb-1 block">Precio Unitario (S/) *</label>
                  <input [(ngModel)]="formData.unitPrice" type="number" min="0" step="0.01" placeholder="0.00" class="w-full px-3 py-2 border border-gray-200 rounded-xl placeholder:text-gray-300 outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                </div>
              </div>
            </div>
            <div class="flex gap-3 px-6 pb-6">
              <button (click)="closeModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 font-medium rounded-xl hover:bg-gray-50 transition-colors">Cancelar</button>
              <button (click)="save()" [disabled]="!isFormValid() || duplicateNameError" class="flex-1 px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white font-medium rounded-xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed">Guardar</button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class MaterialsComponent {
  private auth = inject(AuthService);
  private alert = inject(AlertService);
  private inv = inject(InventoryService);

  packageIcon = Package; plusIcon = Plus; editIcon = Edit; trash2Icon = Trash2;
  searchIcon = Search; alertIcon = AlertCircle;

  materials = signal<Material[]>([]);
  filteredMaterials = signal<Material[]>([]);
  categories = signal<ProductCategory[]>([]);
  searchTerm = '';
  showModal = signal(false);
  editMode = signal(false);
  selectedId = '';
  formData: any = {};
  duplicateNameError = false;

  lowStockCount = signal(0);
  totalValue = signal(0);

  getCategoryName(categoryId: string): string {
    return this.categories().find(c => c.id === categoryId)?.categoryName || '';
  }

  ngOnInit() {
    this.load();
    this.loadCategories();
  }

  load() {
    this.inv.getMaterials().subscribe(data => {
      const orgId = this.auth.organizationId();
      const filtered = data.filter(m => m.organizationId === orgId);
      this.materials.set(filtered);
      this.applyFilters();
      this.calculateStats();
    });
  }

  loadCategories() {
    this.inv.getActiveCategories().subscribe(data => {
      const orgId = this.auth.organizationId();
      this.categories.set(data.filter(c => c.organizationId === orgId));
    });
  }

  calculateStats() {
    const lowStock = this.materials().filter(m => m.currentStock <= m.minStock).length;
    const total = this.materials().reduce((sum, m) => sum + (m.currentStock * m.unitPrice), 0);
    this.lowStockCount.set(lowStock);
    this.totalValue.set(total);
  }

  applyFilters() {
    let filtered = [...this.materials()];
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(m =>
        m.materialName.toLowerCase().includes(term)
      );
    }
    this.filteredMaterials.set(filtered);
  }

  openModal() {
    this.editMode.set(false);
    this.formData = { minStock: 10, unitPrice: 0 };
    this.duplicateNameError = false;
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  edit(m: Material) {
    this.editMode.set(true);
    this.selectedId = m.id;
    this.formData = {
      materialName: m.materialName,
      categoryId: m.categoryId,
      unit: m.unit,
      minStock: m.minStock,
      unitPrice: m.unitPrice
    };
    this.duplicateNameError = false;
    this.showModal.set(true);
  }

  checkDuplicateName(): boolean {
    const name = this.formData.materialName?.trim().toLowerCase();
    if (!name) return false;
    const duplicate = this.materials().some(m =>
      m.materialName.trim().toLowerCase() === name && (!this.editMode() || m.id !== this.selectedId)
    );
    this.duplicateNameError = duplicate;
    return duplicate;
  }

  isFormValid() {
    return this.formData.materialName && this.formData.categoryId &&
      this.formData.unit && this.formData.minStock >= 0 && this.formData.unitPrice >= 0;
  }

  save() {
    const orgId = this.auth.organizationId();
    if (!orgId || !this.isFormValid()) return;

    if (this.checkDuplicateName()) {
      this.alert.warning('Nombre duplicado', 'Ya existe un material con ese nombre');
      return;
    }

    const req: CreateMaterialRequest = {
      organizationId: orgId,
      categoryId: this.formData.categoryId,
      materialName: this.formData.materialName,
      unit: this.formData.unit,
      minStock: Number(this.formData.minStock),
      unitPrice: Number(this.formData.unitPrice)
    };

    const op = this.editMode() ? this.inv.updateMaterial(this.selectedId, req) : this.inv.createMaterial(req);

    op.subscribe({
      next: () => {
        this.alert.success('Éxito', 'Material guardado');
        this.closeModal();
        this.load();
      },
      error: () => this.alert.error('Error', 'No se pudo guardar')
    });
  }

  delete(m: Material) {
    this.alert.confirmDelete(m.materialName).then(result => {
      if (!result.isConfirmed) return;
      this.inv.deleteMaterial(m.id).subscribe({
        next: () => {
          this.alert.success('Éxito', 'Material eliminado');
          this.load();
        },
        error: () => this.alert.error('Error', 'No se pudo eliminar')
      });
    });
  }
}
