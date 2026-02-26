import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, TrendingUp, TrendingDown, RefreshCw, Plus, Search } from 'lucide-angular';
import { AuthService, AlertService, InventoryService } from '../../../../core/services';
import { InventoryMovement, Material } from '../../../../core/models';

@Component({
  selector: 'app-movements',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="p-6">
      <div class="mb-6 flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <lucide-icon [img]="trendingUpIcon" [size]="28"></lucide-icon>
            Movimientos de Inventario
          </h1>
          <p class="text-gray-500">Kardex y registro de movimientos</p>
        </div>
        <button (click)="openModal()" class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl flex items-center gap-2">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          Nuevo Movimiento
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div class="bg-linear-to-br from-emerald-500 to-teal-600 rounded-xl p-6 text-white">
          <div class="flex items-center gap-3">
            <lucide-icon [img]="trendingUpIcon" [size]="32"></lucide-icon>
            <div>
              <p class="text-sm opacity-90">Entradas</p>
              <p class="text-3xl font-bold">{{ entriesCount() }}</p>
            </div>
          </div>
        </div>
        <div class="bg-linear-to-br from-red-500 to-rose-600 rounded-xl p-6 text-white">
          <div class="flex items-center gap-3">
            <lucide-icon [img]="trendingDownIcon" [size]="32"></lucide-icon>
            <div>
              <p class="text-sm opacity-90">Salidas</p>
              <p class="text-3xl font-bold">{{ exitsCount() }}</p>
            </div>
          </div>
        </div>
        <div class="bg-linear-to-br from-violet-500 to-purple-600 rounded-xl p-6 text-white">
          <div class="flex items-center gap-3">
            <lucide-icon [img]="refreshIcon" [size]="32"></lucide-icon>
            <div>
              <p class="text-sm opacity-90">Ajustes</p>
              <p class="text-3xl font-bold">{{ adjustmentsCount() }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-4 mb-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div class="relative">
            <lucide-icon [img]="searchIcon" [size]="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
            <input [(ngModel)]="searchTerm" (ngModelChange)="applyFilters()" type="text" placeholder="Buscar movimientos..." class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 placeholder:text-gray-300">
          </div>
          <select [(ngModel)]="filterType" (ngModelChange)="applyFilters()" class="px-4 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20">
            <option value="">Todos los tipos</option>
            <option value="IN">Entradas</option>
            <option value="OUT">Salidas</option>
            <option value="ADJUSTMENT">Ajustes</option>
          </select>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">#</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Material</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tipo</th>
                <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Cantidad</th>
                <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Stock Anterior</th>
                <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Stock Nuevo</th>
                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Motivo</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
              @for (mov of filteredMovements(); track mov.id; let i = $index) {
                <tr class="hover:bg-gray-50">
                  <td class="px-4 py-3 text-sm text-gray-500">{{ i + 1 }}</td>
                  <td class="px-4 py-3 text-sm">{{ mov.createdAt | date:'dd/MM/yyyy HH:mm' }}</td>
                  <td class="px-4 py-3 text-sm font-medium">{{ getMaterialName(mov.materialId) }}</td>
                  <td class="px-4 py-3">
                    <span [class]="getTypeBadge(mov.movementType)" class="px-2 py-1 text-xs rounded-lg font-medium">
                      {{ getTypeLabel(mov.movementType) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-right text-sm font-medium" [class]="getQuantityColor(mov.movementType)">
                    {{ mov.movementType === 'IN' ? '+' : mov.movementType === 'OUT' ? '-' : '±' }}{{ mov.quantity }}
                  </td>
                  <td class="px-4 py-3 text-right text-sm">{{ mov.previousStock }}</td>
                  <td class="px-4 py-3 text-right text-sm font-medium">{{ mov.newStock }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ mov.notes }}</td>
                </tr>
              } @empty {
                <tr><td colspan="8" class="text-center py-12 text-gray-400">No hay movimientos registrados</td></tr>
              }
            </tbody>
          </table>
        </div>
      </div>

      @if (showModal()) {
        <div class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-lg w-full" (click)="$event.stopPropagation()">
            <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <h3 class="text-lg font-bold text-gray-800">Registrar Movimiento</h3>
            </div>

            <div class="p-6 space-y-3">
              <div>
                <label class="text-sm text-gray-600 mb-1 block">Material *</label>
                <select [(ngModel)]="formData.materialId" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                  <option value="">Seleccione material...</option>
                  @for (m of materials(); track m.id) {
                    <option [value]="m.id">{{ m.materialName }} (Stock: {{ m.currentStock }})</option>
                  }
                </select>
              </div>

              <div>
                <label class="text-sm text-gray-600 mb-1 block">Tipo de Movimiento *</label>
                <select [(ngModel)]="formData.movementType" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                  <option value="">Seleccione tipo...</option>
                  <option value="IN">Entrada</option>
                  <option value="OUT">Salida</option>
                  <option value="ADJUSTMENT">Ajuste</option>
                </select>
              </div>

              <div>
                <label class="text-sm text-gray-600 mb-1 block">Cantidad *</label>
                <input [(ngModel)]="formData.quantity" type="number" min="1" placeholder="0" class="w-full px-3 py-2 border border-gray-200 rounded-xl placeholder:text-gray-300 outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
              </div>

              <div>
                <label class="text-sm text-gray-600 mb-1 block">Motivo *</label>
                <textarea [(ngModel)]="formData.notes" rows="3" placeholder="Describa el motivo del movimiento..." class="w-full px-3 py-2 border border-gray-200 rounded-xl resize-none placeholder:text-gray-300 outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400"></textarea>
              </div>
            </div>

            <div class="flex gap-3 px-6 pb-6">
              <button (click)="closeModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 font-medium rounded-xl hover:bg-gray-50 transition-colors">Cancelar</button>
              <button (click)="save()" [disabled]="!canSave()" class="flex-1 px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white font-medium rounded-xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                Registrar
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class MovementsComponent {
  private auth = inject(AuthService);
  private alert = inject(AlertService);
  private inv = inject(InventoryService);

  trendingUpIcon = TrendingUp; trendingDownIcon = TrendingDown; refreshIcon = RefreshCw;
  plusIcon = Plus; searchIcon = Search;

  movements = signal<InventoryMovement[]>([]);
  filteredMovements = signal<InventoryMovement[]>([]);
  materials = signal<Material[]>([]);

  searchTerm = '';
  filterType = '';
  showModal = signal(false);
  formData: any = {};

  entriesCount = signal(0);
  exitsCount = signal(0);
  adjustmentsCount = signal(0);

  ngOnInit() {
    this.load();
    this.loadMaterials();
  }

  load() {
    this.inv.getMovements().subscribe(data => {
      const orgId = this.auth.organizationId();
      const filtered = data.filter(m => m.organizationId === orgId).sort((a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
      this.movements.set(filtered);
      this.applyFilters();
      this.calculateStats();
    });
  }

  loadMaterials() {
    this.inv.getMaterials().subscribe(data => {
      const orgId = this.auth.organizationId();
      this.materials.set(data.filter(m => m.organizationId === orgId));
    });
  }

  getMaterialName(materialId: string): string {
    return this.materials().find(m => m.id === materialId)?.materialName || '-';
  }

  calculateStats() {
    const entries = this.movements().filter(m => m.movementType === 'IN').length;
    const exits = this.movements().filter(m => m.movementType === 'OUT').length;
    const adj = this.movements().filter(m => m.movementType === 'ADJUSTMENT').length;
    this.entriesCount.set(entries);
    this.exitsCount.set(exits);
    this.adjustmentsCount.set(adj);
  }

  applyFilters() {
    let filtered = [...this.movements()];

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(m =>
        this.getMaterialName(m.materialId).toLowerCase().includes(term) ||
        m.notes.toLowerCase().includes(term)
      );
    }

    if (this.filterType) {
      filtered = filtered.filter(m => m.movementType === this.filterType);
    }

    this.filteredMovements.set(filtered);
  }

  openModal() {
    this.formData = { materialId: '', movementType: '', quantity: 1, notes: '' };
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  canSave() {
    return this.formData.materialId && this.formData.movementType &&
      this.formData.quantity > 0 && this.formData.notes;
  }

  save() {
    const orgId = this.auth.organizationId();
    if (!orgId || !this.canSave()) return;

    const req = {
      organizationId: orgId,
      materialId: this.formData.materialId,
      movementType: this.formData.movementType,
      quantity: Number(this.formData.quantity),
      notes: this.formData.notes
    };

    this.inv.createMovement(req).subscribe({
      next: () => {
        this.alert.success('Éxito', 'Movimiento registrado');
        this.closeModal();
        this.load();
        this.loadMaterials();
      },
      error: () => this.alert.error('Error', 'No se pudo registrar el movimiento')
    });
  }

  getTypeBadge(type: string) {
    const map: Record<string, string> = {
      IN: 'bg-emerald-50 text-emerald-700',
      OUT: 'bg-red-50 text-red-700',
      ADJUSTMENT: 'bg-violet-50 text-violet-700'
    };
    return map[type] || 'bg-gray-100 text-gray-600';
  }

  getTypeLabel(type: string) {
    const map: Record<string, string> = {
      IN: 'Entrada',
      OUT: 'Salida',
      ADJUSTMENT: 'Ajuste'
    };
    return map[type] || type;
  }

  getQuantityColor(type: string) {
    const map: Record<string, string> = {
      IN: 'text-emerald-600',
      OUT: 'text-red-600',
      ADJUSTMENT: 'text-violet-600'
    };
    return map[type] || 'text-gray-600';
  }
}
