import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { LucideAngularModule, Package, Users, ShoppingCart, TrendingUp, TrendingDown, AlertCircle, DollarSign, Tag } from 'lucide-angular';
import { AuthService, InventoryService } from '../../../../core/services';
import { Material, Supplier, Purchase, InventoryMovement, ProductCategory } from '../../../../core/models';

@Component({
  selector: 'app-inventory-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, LucideAngularModule],
  template: `
    <div class="p-6">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Dashboard de Inventario</h1>
        <p class="text-gray-500">Vista general de inventario y compras</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div class="bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl p-6 text-white">
          <div class="flex items-center gap-3 mb-2">
            <lucide-icon [img]="packageIcon" [size]="32"></lucide-icon>
            <div>
              <p class="text-sm opacity-90">Total Materiales</p>
              <p class="text-3xl font-bold">{{ totalMaterials() }}</p>
            </div>
          </div>
          <a routerLink="/admin/inventory/materials" class="text-xs opacity-90 hover:underline">Ver materiales →</a>
        </div>

        <div class="bg-gradient-to-br from-blue-500 to-cyan-600 rounded-xl p-6 text-white">
          <div class="flex items-center gap-3 mb-2">
            <lucide-icon [img]="usersIcon" [size]="32"></lucide-icon>
            <div>
              <p class="text-sm opacity-90">Proveedores</p>
              <p class="text-3xl font-bold">{{ totalSuppliers() }}</p>
            </div>
          </div>
          <a routerLink="/admin/inventory/suppliers" class="text-xs opacity-90 hover:underline">Ver proveedores →</a>
        </div>

        <div class="bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl p-6 text-white">
          <div class="flex items-center gap-3 mb-2">
            <lucide-icon [img]="cartIcon" [size]="32"></lucide-icon>
            <div>
              <p class="text-sm opacity-90">Total Compras</p>
              <p class="text-3xl font-bold">{{ totalPurchases() }}</p>
            </div>
          </div>
          <a routerLink="/admin/inventory/purchases" class="text-xs opacity-90 hover:underline">Ver compras →</a>
        </div>

        <div class="bg-gradient-to-br from-amber-500 to-orange-600 rounded-xl p-6 text-white">
          <div class="flex items-center gap-3 mb-2">
            <lucide-icon [img]="dollarIcon" [size]="32"></lucide-icon>
            <div>
              <p class="text-sm opacity-90">Valor Inventario</p>
              <p class="text-3xl font-bold">S/ {{ totalInventoryValue().toFixed(2) }}</p>
            </div>
          </div>
          <p class="text-xs opacity-90">Valor total del stock</p>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="font-bold text-gray-800">Materiales con Stock Bajo</h3>
            <lucide-icon [img]="alertIcon" [size]="20" class="text-amber-500"></lucide-icon>
          </div>

          @if (lowStockMaterials().length > 0) {
            <div class="space-y-3">
              @for (mat of lowStockMaterials(); track mat.id) {
                <div class="flex items-center justify-between p-3 bg-amber-50 rounded-lg">
                  <div class="flex-1">
                    <p class="font-medium text-gray-800">{{ mat.materialName }}</p>
                    <p class="text-sm text-gray-600">Categoría: {{ mat.categoryName || 'N/A' }}</p>
                  </div>
                  <div class="text-right">
                    <p class="text-sm font-medium text-amber-700">{{ mat.currentStock }} {{ mat.unit }}</p>
                    <p class="text-xs text-gray-500">Mín: {{ mat.minStock }}</p>
                  </div>
                </div>
              }
            </div>
          } @else {
            <p class="text-gray-400 text-center py-8">No hay materiales con stock bajo</p>
          }
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="font-bold text-gray-800">Resumen de Compras</h3>
            <lucide-icon [img]="cartIcon" [size]="20" class="text-emerald-500"></lucide-icon>
          </div>

          <div class="space-y-3">
            <div class="flex justify-between items-center p-3 bg-amber-50 rounded-lg">
              <span class="text-gray-700">Pendientes</span>
              <span class="font-bold text-amber-700">{{ pendingPurchases() }}</span>
            </div>
            <div class="flex justify-between items-center p-3 bg-emerald-50 rounded-lg">
              <span class="text-gray-700">Recibidas</span>
              <span class="font-bold text-emerald-700">{{ receivedPurchases() }}</span>
            </div>
            <div class="flex justify-between items-center p-3 bg-red-50 rounded-lg">
              <span class="text-gray-700">Canceladas</span>
              <span class="font-bold text-red-700">{{ cancelledPurchases() }}</span>
            </div>
            <div class="flex justify-between items-center p-3 bg-violet-50 rounded-lg">
              <span class="text-gray-700">Monto Total</span>
              <span class="font-bold text-violet-700">S/ {{ totalPurchasesAmount().toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="font-bold text-gray-800">Movimientos Recientes</h3>
            <lucide-icon [img]="trendingUpIcon" [size]="20" class="text-violet-500"></lucide-icon>
          </div>

          @if (recentMovements().length > 0) {
            <div class="space-y-2">
              @for (mov of recentMovements(); track mov.id) {
                <div class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg">
                  <div class="flex items-center gap-3">
                    <lucide-icon
                      [img]="mov.movementType === 'IN' ? trendingUpIcon : trendingDownIcon"
                      [size]="18"
                      [class]="mov.movementType === 'IN' ? 'text-emerald-500' : 'text-red-500'">
                    </lucide-icon>
                    <div>
                      <p class="text-sm font-medium">{{ mov.materialName }}</p>
                      <p class="text-xs text-gray-500">{{ mov.createdAt | date:'dd/MM/yy HH:mm' }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="text-sm font-medium" [class]="mov.movementType === 'IN' ? 'text-emerald-600' : 'text-red-600'">
                      {{ mov.movementType === 'IN' ? '+' : '-' }}{{ mov.quantity }}
                    </p>
                    <p class="text-xs text-gray-500">{{ mov.notes }}</p>
                  </div>
                </div>
              }
            </div>
          } @else {
            <p class="text-gray-400 text-center py-8">No hay movimientos recientes</p>
          }
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="font-bold text-gray-800">Categorías</h3>
            <lucide-icon [img]="tagIcon" [size]="20" class="text-violet-500"></lucide-icon>
          </div>

          @if (categories().length > 0) {
            <div class="space-y-2">
              @for (cat of categories(); track cat.id) {
                <div class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg">
                  <div class="flex items-center gap-3">
                    <lucide-icon [img]="tagIcon" [size]="18" class="text-violet-500"></lucide-icon>
                    <span class="font-medium">{{ cat.categoryName }}</span>
                  </div>
                  <span class="text-sm text-gray-500">{{ getMaterialsCountByCategory(cat.id!) }} items</span>
                </div>
              }
            </div>
          } @else {
            <p class="text-gray-400 text-center py-8">No hay categorías registradas</p>
          }
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent {
  private auth = inject(AuthService);
  private inv = inject(InventoryService);

  packageIcon = Package; usersIcon = Users; cartIcon = ShoppingCart;
  trendingUpIcon = TrendingUp; trendingDownIcon = TrendingDown;
  alertIcon = AlertCircle; dollarIcon = DollarSign; tagIcon = Tag;

  materials = signal<Material[]>([]);
  suppliers = signal<Supplier[]>([]);
  purchases = signal<Purchase[]>([]);
  movements = signal<InventoryMovement[]>([]);
  categories = signal<ProductCategory[]>([]);

  totalMaterials = computed(() => this.materials().length);
  totalSuppliers = computed(() => this.suppliers().length);
  totalPurchases = computed(() => this.purchases().length);

  totalInventoryValue = computed(() =>
    this.materials().reduce((sum, m) => sum + (m.currentStock * m.unitPrice), 0)
  );

  lowStockMaterials = computed(() =>
    this.materials()
      .filter(m => m.currentStock <= m.minStock)
      .sort((a, b) => a.currentStock - b.currentStock)
      .slice(0, 5)
  );

  pendingPurchases = computed(() =>
    this.purchases().filter(p => p.purchaseStatus === 'PENDING').length
  );

  receivedPurchases = computed(() =>
    this.purchases().filter(p => p.purchaseStatus === 'RECEIVED').length
  );

  cancelledPurchases = computed(() =>
    this.purchases().filter(p => p.purchaseStatus === 'CANCELLED').length
  );

  totalPurchasesAmount = computed(() =>
    this.purchases().reduce((sum, p) => sum + p.totalAmount, 0)
  );

  recentMovements = computed(() =>
    this.movements()
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 10)
  );

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    const orgId = this.auth.organizationId();

    this.inv.getMaterials().subscribe(data =>
      this.materials.set(data.filter(m => m.organizationId === orgId))
    );

    this.inv.getSuppliers().subscribe(data =>
      this.suppliers.set(data.filter(s => s.organizationId === orgId))
    );

    this.inv.getPurchases().subscribe(data =>
      this.purchases.set(data.filter(p => p.organizationId === orgId))
    );

    this.inv.getMovements().subscribe(data =>
      this.movements.set(data.filter(m => m.organizationId === orgId))
    );

    this.inv.getCategories().subscribe(data =>
      this.categories.set(data.filter(c => c.organizationId === orgId))
    );
  }

  getMaterialsCountByCategory(categoryId: string): number {
    return this.materials().filter(m => m.categoryId === categoryId).length;
  }
}
