import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, ShoppingCart, Plus, Eye, Search, X, Check, Ban } from 'lucide-angular';
import { AuthService, AlertService, InventoryService } from '../../../../core/services';
import { Purchase, CreatePurchaseRequest, Supplier, Material, CreatePurchaseDetailRequest } from '../../../../core/models';

@Component({
  selector: 'app-purchases',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="p-6">
      <div class="mb-6 flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <lucide-icon [img]="cartIcon" [size]="28"></lucide-icon>
            Compras
          </h1>
          <p class="text-gray-500">Registro de compras y pedidos</p>
        </div>
        <button (click)="openModal()" class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl flex items-center gap-2">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          Nueva Compra
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="bg-linear-to-br from-violet-500 to-purple-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Total Compras</p>
          <p class="text-3xl font-bold mt-1">{{ purchases().length }}</p>
        </div>
        <div class="bg-linear-to-br from-blue-500 to-cyan-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Pendientes</p>
          <p class="text-3xl font-bold mt-1">{{ pendingCount() }}</p>
        </div>
        <div class="bg-linear-to-br from-emerald-500 to-teal-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Recibidas</p>
          <p class="text-3xl font-bold mt-1">{{ receivedCount() }}</p>
        </div>
        <div class="bg-linear-to-br from-amber-500 to-orange-600 rounded-xl p-6 text-white">
          <p class="text-sm opacity-90">Monto Total</p>
          <p class="text-2xl font-bold mt-1">S/ {{ totalAmount().toFixed(2) }}</p>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-4 mb-4">
        <input [(ngModel)]="searchTerm" (ngModelChange)="applyFilters()" type="text" placeholder="Buscar compras..." class="w-full px-4 py-2 border border-gray-200 rounded-xl">
      </div>

      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">#</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Proveedor</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Monto</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
              <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            @for (p of filteredPurchases(); track p.id; let i = $index) {
              <tr class="hover:bg-gray-50">
                <td class="px-4 py-3 text-sm font-medium">{{ i + 1 }}</td>
                <td class="px-4 py-3 text-sm">{{ getSupplierName(p.supplierId) }}</td>
                <td class="px-4 py-3 text-sm">{{ p.purchaseDate | date:'dd/MM/yyyy' }}</td>
                <td class="px-4 py-3 text-sm font-medium">S/ {{ p.totalAmount.toFixed(2) }}</td>
                <td class="px-4 py-3">
                  <span [class]="getStatusBadge(p.purchaseStatus)" class="px-2 py-1 text-xs rounded-lg font-medium">
                    {{ getStatusLabel(p.purchaseStatus) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-center">
                  <button (click)="viewDetail(p)" class="p-1 hover:bg-violet-50 text-violet-600 rounded">
                    <lucide-icon [img]="eyeIcon" [size]="16"></lucide-icon>
                  </button>
                </td>
              </tr>
            } @empty {
              <tr><td colspan="6" class="text-center py-12 text-gray-400">No hay compras registradas</td></tr>
            }
          </tbody>
        </table>
      </div>

      @if (showModal()) {
        <div class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto" (click)="$event.stopPropagation()">

            <!-- Header -->
            <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <div>
                <h3 class="text-lg font-bold text-gray-800">Nueva Compra</h3>
                <p class="text-xs text-gray-400 mt-0.5">Registra una nueva orden de compra</p>
              </div>
              <button (click)="closeModal()" class="p-2 hover:bg-gray-100 rounded-xl transition-colors text-gray-400 hover:text-gray-600">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>

            <div class="p-6 space-y-5">

              <!-- Proveedor -->
              <div class="bg-gray-50 rounded-xl p-4">
                <label class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2 block">Proveedor *</label>
                <select [(ngModel)]="formData.supplierId" class="w-full px-3 py-2.5 bg-white border border-gray-200 rounded-xl text-sm text-gray-800 outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 transition-colors">
                  <option value="">Seleccione proveedor...</option>
                  @for (s of suppliers(); track s.id) {
                    <option [value]="s.id">{{ s.supplierName }}</option>
                  }
                </select>
              </div>

              <!-- Materiales -->
              <div>
                <div class="flex items-center justify-between mb-3">
                  <div>
                    <h4 class="text-sm font-semibold text-gray-700">Materiales</h4>
                    <p class="text-xs text-gray-400">Agrega los materiales a comprar</p>
                  </div>
                  <button (click)="addDetail()" type="button"
                          class="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium bg-violet-50 text-violet-600 hover:bg-violet-100 rounded-lg transition-colors">
                    <lucide-icon [img]="plusIcon" [size]="13"></lucide-icon>
                    Agregar Material
                  </button>
                </div>

                @if (formData.details.length > 0) {
                  <!-- Header columnas -->
                  <div class="grid grid-cols-12 gap-2 px-3 mb-1.5">
                    <div class="col-span-6"><span class="text-[10px] font-semibold text-gray-400 uppercase tracking-wider">Material</span></div>
                    <div class="col-span-2"><span class="text-[10px] font-semibold text-gray-400 uppercase tracking-wider">Cantidad</span></div>
                    <div class="col-span-3"><span class="text-[10px] font-semibold text-gray-400 uppercase tracking-wider">Costo Unit.</span></div>
                    <div class="col-span-1"></div>
                  </div>

                  <div class="space-y-2">
                    @for (detail of formData.details; track $index; let i = $index) {
                      <div class="bg-gray-50 rounded-xl p-3 border border-gray-100">
                        <div class="grid grid-cols-12 gap-2 items-start">
                          <div class="col-span-6">
                            <select [(ngModel)]="detail.materialId"
                                    class="w-full px-2.5 py-2 bg-white border border-gray-200 rounded-lg text-sm text-gray-800 outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 transition-colors">
                              <option value="">Seleccione...</option>
                              @for (m of availableMaterials(i); track m.id) {
                                <option [value]="m.id">{{ m.materialName }}</option>
                              }
                            </select>
                          </div>
                          <div class="col-span-2">
                            <input [(ngModel)]="detail.quantity" type="number" min="1" placeholder="0"
                                   class="w-full px-2.5 py-2 bg-white border rounded-lg text-sm text-gray-800 outline-none focus:ring-2 focus:ring-violet-500/20 transition-colors"
                                   [class.border-red-300]="detail.quantity <= 0"
                                   [class.border-gray-200]="detail.quantity > 0">
                            @if (detail.quantity <= 0) {
                              <span class="text-[10px] text-red-500 mt-0.5 block">Mín. 1</span>
                            }
                          </div>
                          <div class="col-span-3">
                            <input [(ngModel)]="detail.unitPrice" type="number" min="0.01" step="0.01" placeholder="0.00"
                                   class="w-full px-2.5 py-2 bg-white border rounded-lg text-sm text-gray-800 outline-none focus:ring-2 focus:ring-violet-500/20 transition-colors"
                                   [class.border-red-300]="detail.unitPrice <= 0"
                                   [class.border-gray-200]="detail.unitPrice > 0">
                            @if (detail.unitPrice <= 0) {
                              <span class="text-[10px] text-red-500 mt-0.5 block">Requerido</span>
                            }
                          </div>
                          <div class="col-span-1 flex items-center justify-center pt-1">
                            <button (click)="removeDetail(i)" type="button"
                                    class="p-1.5 hover:bg-red-50 text-red-400 hover:text-red-600 rounded-lg transition-colors">
                              <lucide-icon [img]="xIcon" [size]="14"></lucide-icon>
                            </button>
                          </div>
                        </div>
                      </div>
                    }
                  </div>
                } @else {
                  <div class="text-center py-8 border-2 border-dashed border-gray-200 rounded-xl text-gray-400 text-sm">
                    Sin materiales — haz clic en "Agregar Material"
                  </div>
                }
              </div>

              <!-- Total -->
              <div class="flex items-center justify-between bg-violet-50 border border-violet-100 rounded-xl px-5 py-4">
                <div>
                  <p class="text-xs text-violet-500 font-medium uppercase tracking-wider">Total de la Compra</p>
                </div>
                <span class="text-2xl font-bold text-violet-600">S/ {{ calculateTotal().toFixed(2) }}</span>
              </div>

            </div>

            <!-- Footer -->
            <div class="flex gap-3 px-6 pb-6">
              <button (click)="closeModal()"
                      class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 font-medium rounded-xl hover:bg-gray-50 transition-colors">
                Cancelar
              </button>
              <button (click)="save()" [disabled]="!canSave()"
                      class="flex-1 px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white font-medium rounded-xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                Registrar Compra
              </button>
            </div>

          </div>
        </div>
      }

      @if (showDetailModal() && selectedPurchase()) {
        <div class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" (click)="closeDetailModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto" (click)="$event.stopPropagation()">

            <!-- Header -->
            <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <div>
                <h3 class="text-lg font-bold text-gray-800">Detalle de Compra</h3>
                <p class="text-xs text-gray-400 mt-0.5">{{ selectedPurchase()!.purchaseCode }}</p>
              </div>
              <button (click)="closeDetailModal()" class="p-2 hover:bg-gray-100 rounded-xl transition-colors text-gray-400 hover:text-gray-600">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>

            <div class="p-6 space-y-5">
              <!-- Info general -->
              <div class="grid grid-cols-2 gap-4 bg-gray-50 rounded-xl p-4">
                <div>
                  <label class="text-[10px] font-semibold text-gray-400 uppercase tracking-wider">Proveedor</label>
                  <p class="text-sm font-medium text-gray-800 mt-0.5">{{ getSupplierName(selectedPurchase()!.supplierId) }}</p>
                </div>
                <div>
                  <label class="text-[10px] font-semibold text-gray-400 uppercase tracking-wider">Estado</label>
                  <p class="mt-0.5"><span [class]="getStatusBadge(selectedPurchase()!.purchaseStatus)" class="px-2.5 py-1 text-xs rounded-lg font-medium">{{ getStatusLabel(selectedPurchase()!.purchaseStatus) }}</span></p>
                </div>
                <div>
                  <label class="text-[10px] font-semibold text-gray-400 uppercase tracking-wider">Fecha</label>
                  <p class="text-sm text-gray-700 mt-0.5">{{ selectedPurchase()!.purchaseDate | date:'dd/MM/yyyy' }}</p>
                </div>
                <div>
                  <label class="text-[10px] font-semibold text-gray-400 uppercase tracking-wider">Factura</label>
                  <p class="text-sm text-gray-700 mt-0.5">{{ selectedPurchase()!.invoiceNumber || '-' }}</p>
                </div>
              </div>

              <!-- Materiales -->
              @if (selectedPurchase()!.details && selectedPurchase()!.details!.length > 0) {
                <div>
                  <h4 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Materiales</h4>
                  <div class="border border-gray-100 rounded-xl overflow-hidden">
                    <table class="w-full text-sm">
                      <thead class="bg-gray-50">
                        <tr>
                          <th class="px-4 py-2.5 text-left text-[10px] font-semibold text-gray-400 uppercase">Material</th>
                          <th class="px-4 py-2.5 text-right text-[10px] font-semibold text-gray-400 uppercase">Cantidad</th>
                          <th class="px-4 py-2.5 text-right text-[10px] font-semibold text-gray-400 uppercase">Costo Unit.</th>
                          <th class="px-4 py-2.5 text-right text-[10px] font-semibold text-gray-400 uppercase">Total</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-gray-100">
                        @for (d of selectedPurchase()!.details; track d.id) {
                          <tr class="hover:bg-gray-50">
                            <td class="px-4 py-2.5 text-gray-800">{{ getMaterialName(d.materialId) }}</td>
                            <td class="px-4 py-2.5 text-right">{{ d.quantity }}</td>
                            <td class="px-4 py-2.5 text-right">S/ {{ d.unitPrice.toFixed(2) }}</td>
                            <td class="px-4 py-2.5 text-right font-medium text-gray-800">S/ {{ d.subtotal.toFixed(2) }}</td>
                          </tr>
                        }
                      </tbody>
                    </table>
                  </div>
                </div>
              }

              <!-- Total -->
              <div class="flex items-center justify-between bg-violet-50 border border-violet-100 rounded-xl px-5 py-4">
                <p class="text-xs text-violet-500 font-medium uppercase tracking-wider">Total de Compra</p>
                <span class="text-2xl font-bold text-violet-600">S/ {{ selectedPurchase()!.totalAmount.toFixed(2) }}</span>
              </div>
            </div>

            <!-- Footer con acciones -->
            <div class="flex gap-3 px-6 pb-6">
              <button (click)="closeDetailModal()"
                      class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 font-medium rounded-xl hover:bg-gray-50 transition-colors">
                Cerrar
              </button>
              @if (selectedPurchase()!.purchaseStatus === 'PENDING') {
                <button (click)="cancelSelectedPurchase()"
                        class="px-4 py-2.5 border border-red-200 text-red-600 font-medium rounded-xl hover:bg-red-50 transition-colors flex items-center gap-2">
                  <lucide-icon [img]="banIcon" [size]="16"></lucide-icon>
                  Cancelar Compra
                </button>
                <button (click)="receiveSelectedPurchase()"
                        class="flex-1 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-medium rounded-xl transition-colors flex items-center justify-center gap-2">
                  <lucide-icon [img]="checkIcon" [size]="16"></lucide-icon>
                  Marcar como Recibida
                </button>
              }
            </div>

          </div>
        </div>
      }
    </div>
  `
})
export class PurchasesComponent {
  private auth = inject(AuthService);
  private alert = inject(AlertService);
  private inv = inject(InventoryService);

  cartIcon = ShoppingCart; plusIcon = Plus; eyeIcon = Eye; searchIcon = Search; xIcon = X; checkIcon = Check; banIcon = Ban;

  purchases = signal<Purchase[]>([]);
  filteredPurchases = signal<Purchase[]>([]);
  suppliers = signal<Supplier[]>([]);
  materials = signal<Material[]>([]);
  searchTerm = '';

  showModal = signal(false);
  showDetailModal = signal(false);
  selectedPurchase = signal<Purchase | null>(null);

  formData: any = { details: [] };

  pendingCount = signal(0);
  receivedCount = signal(0);
  totalAmount = signal(0);

  ngOnInit() {
    this.load();
    this.loadSuppliers();
    this.loadMaterials();
  }

  load() {
    this.inv.getPurchases().subscribe(data => {
      const orgId = this.auth.organizationId();
      const filtered = data.filter(p => p.organizationId === orgId);
      this.purchases.set(filtered);
      this.applyFilters();
      this.calculateStats();
    });
  }

  loadSuppliers() {
    this.inv.getActiveSuppliers().subscribe(data => {
      const orgId = this.auth.organizationId();
      this.suppliers.set(data.filter(s => s.organizationId === orgId));
    });
  }

  loadMaterials() {
    this.inv.getActiveMaterials().subscribe(data => {
      const orgId = this.auth.organizationId();
      this.materials.set(data.filter(m => m.organizationId === orgId));
    });
  }

  calculateStats() {
    const pend = this.purchases().filter(p => p.purchaseStatus === 'PENDING').length;
    const rec = this.purchases().filter(p => p.purchaseStatus === 'RECEIVED').length;
    const total = this.purchases().reduce((sum, p) => sum + p.totalAmount, 0);
    this.pendingCount.set(pend);
    this.receivedCount.set(rec);
    this.totalAmount.set(total);
  }

  applyFilters() {
    let filtered = [...this.purchases()];
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(p =>
        p.purchaseCode.toLowerCase().includes(term) ||
        p.supplierName?.toLowerCase().includes(term)
      );
    }
    this.filteredPurchases.set(filtered);
  }

  openModal() {
    this.formData = { supplierId: '', details: [] };
    this.addDetail();
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  viewDetail(p: Purchase) {
    // Load full purchase with details
    this.inv.getPurchaseById(p.id).subscribe(full => {
      this.selectedPurchase.set(full);
      this.showDetailModal.set(true);
    });
  }

  closeDetailModal() {
    this.showDetailModal.set(false);
    this.selectedPurchase.set(null);
  }

  addDetail() {
    this.formData.details.push({ materialId: '', quantity: 1, unitPrice: 0 });
  }

  removeDetail(index: number) {
    this.formData.details.splice(index, 1);
  }

  calculateTotal() {
    return this.formData.details.reduce((sum: number, d: any) => sum + (d.quantity * d.unitPrice), 0);
  }

  availableMaterials(currentIndex: number): Material[] {
    const selectedIds = this.formData.details
      .filter((_: any, i: number) => i !== currentIndex)
      .map((d: any) => d.materialId)
      .filter((id: string) => id);
    return this.materials().filter(m => !selectedIds.includes(m.id));
  }

  canSave() {
    return this.formData.supplierId && this.formData.details.length > 0 &&
      this.formData.details.every((d: any) => d.materialId && d.quantity > 0 && d.unitPrice > 0);
  }

  save() {
    const orgId = this.auth.organizationId();
    if (!orgId || !this.canSave()) return;

    const details: CreatePurchaseDetailRequest[] = this.formData.details.map((d: any) => ({
      materialId: d.materialId,
      quantity: Number(d.quantity),
      unitPrice: Number(d.unitPrice)
    }));

    const req: CreatePurchaseRequest = {
      organizationId: orgId,
      supplierId: this.formData.supplierId,
      details
    };

    this.inv.createPurchase(req).subscribe({
      next: () => {
        this.alert.success('Éxito', 'Compra registrada');
        this.closeModal();
        this.load();
      },
      error: () => this.alert.error('Error', 'No se pudo registrar la compra')
    });
  }

  getStatusBadge(status: string) {
    const map: Record<string, string> = {
      PENDING: 'bg-amber-50 text-amber-700',
      RECEIVED: 'bg-emerald-50 text-emerald-700',
      CANCELLED: 'bg-red-50 text-red-700'
    };
    return map[status] || 'bg-gray-100 text-gray-600';
  }

  getStatusLabel(status: string) {
    const map: Record<string, string> = {
      PENDING: 'Pendiente',
      RECEIVED: 'Recibida',
      CANCELLED: 'Cancelada'
    };
    return map[status] || status;
  }

  getSupplierName(supplierId: string): string {
    return this.suppliers().find(s => s.id === supplierId)?.supplierName || '-';
  }

  getMaterialName(materialId: string): string {
    return this.materials().find(m => m.id === materialId)?.materialName || materialId;
  }

  receiveSelectedPurchase(): void {
    const purchase = this.selectedPurchase();
    if (!purchase) return;

    this.alert.confirm(
      '¿Marcar como recibida?',
      'Esto actualizará el stock de los materiales automáticamente. Esta acción no se puede deshacer.',
      'Sí, recibida'
    ).then(result => {
      if (!result.isConfirmed) return;
      this.inv.receivePurchase(purchase.id).subscribe({
        next: () => {
          this.alert.success('Recibida', 'Compra marcada como recibida. El stock fue actualizado.');
          this.closeDetailModal();
          this.load();
          this.loadMaterials();
        },
        error: (err) => {
          const msg = err.error?.message || 'No se pudo recibir la compra';
          this.alert.error('Error', msg);
        }
      });
    });
  }

  cancelSelectedPurchase(): void {
    const purchase = this.selectedPurchase();
    if (!purchase) return;

    this.alert.confirm(
      '¿Cancelar compra?',
      'Esta acción no se puede deshacer.',
      'Sí, cancelar'
    ).then(result => {
      if (!result.isConfirmed) return;
      this.inv.cancelPurchase(purchase.id).subscribe({
        next: () => {
          this.alert.success('Cancelada', 'Compra cancelada correctamente');
          this.closeDetailModal();
          this.load();
        },
        error: (err) => {
          const msg = err.error?.message || 'No se pudo cancelar la compra';
          this.alert.error('Error', msg);
        }
      });
    });
  }
}
