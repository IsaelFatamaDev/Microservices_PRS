import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Users, Plus, Edit, Trash2, Search } from 'lucide-angular';
import { AuthService, AlertService, InventoryService } from '../../../../core/services';
import { Supplier, CreateSupplierRequest } from '../../../../core/models';

@Component({
  selector: 'app-suppliers',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="p-6">
      <div class="mb-6 flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <lucide-icon [img]="usersIcon" [size]="28"></lucide-icon>
            Proveedores
          </h1>
          <p class="text-gray-500">Gestión de proveedores</p>
        </div>
        <button (click)="openModal()" class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl flex items-center gap-2">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          Nuevo
        </button>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-4 mb-4">
        <div class="relative">
          <lucide-icon [img]="searchIcon" [size]="18" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
          <input [(ngModel)]="searchTerm" (ngModelChange)="applyFilters()" type="text" placeholder="Buscar..." class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20">
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nombre</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">RUC</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Teléfono</th>
              <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            @for (s of filteredSuppliers(); track s.id) {
              <tr class="hover:bg-gray-50">
                <td class="px-4 py-3 text-sm">{{ s.supplierName }}</td>
                <td class="px-4 py-3 text-sm">{{ s.ruc || '-' }}</td>
                <td class="px-4 py-3 text-sm">{{ s.email || '-' }}</td>
                <td class="px-4 py-3 text-sm">{{ s.phone || '-' }}</td>
                <td class="px-4 py-3 text-center">
                  <button (click)="edit(s)" class="p-1 hover:bg-blue-50 text-blue-600 rounded">
                    <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                  </button>
                  <button (click)="delete(s)" class="p-1 hover:bg-red-50 text-red-600 rounded ml-1">
                    <lucide-icon [img]="trash2Icon" [size]="16"></lucide-icon>
                  </button>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>

      @if (showModal()) {
        <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl max-w-lg w-full p-6" (click)="$event.stopPropagation()">
            <h3 class="text-lg font-bold mb-4">{{ editMode() ? 'Editar' : 'Nuevo' }} Proveedor</h3>
            <div class="space-y-3">
              <input [(ngModel)]="formData.supplierName" placeholder="Nombre *" class="w-full px-3 py-2 border border-gray-100 rounded-xl placeholder:text-gray-400">
              <div>
                <input [(ngModel)]="formData.ruc" placeholder="RUC (20 dígitos)" maxlength="20" (input)="sanitizeRuc()" class="w-full px-3 py-2 border rounded-xl placeholder:text-gray-400" [class.border-red-400]="formData.ruc && formData.ruc.length > 0 && formData.ruc.length !== 20" [class.border-gray-100]="!formData.ruc || formData.ruc.length === 0 || formData.ruc.length === 20">
                @if (formData.ruc && formData.ruc.length > 0 && formData.ruc.length !== 20) {
                  <p class="text-xs text-red-500 mt-1">El RUC debe tener exactamente 20 dígitos ({{ formData.ruc.length }}/20)</p>
                }
              </div>
              <input [(ngModel)]="formData.email" type="email" placeholder="Email" class="w-full px-3 py-2 border border-gray-100 rounded-xl placeholder:text-gray-400">
              <div>
                <input [(ngModel)]="formData.phone" placeholder="Teléfono (9 dígitos)" maxlength="9" (input)="sanitizePhone()" class="w-full px-3 py-2 border rounded-xl placeholder:text-gray-400" [class.border-red-400]="formData.phone && formData.phone.length > 0 && formData.phone.length !== 9" [class.border-gray-100]="!formData.phone || formData.phone.length === 0 || formData.phone.length === 9">
                @if (formData.phone && formData.phone.length > 0 && formData.phone.length !== 9) {
                  <p class="text-xs text-red-500 mt-1">El teléfono debe tener exactamente 9 dígitos ({{ formData.phone.length }}/9)</p>
                }
              </div>
              <textarea [(ngModel)]="formData.address" placeholder="Dirección" rows="2" class="w-full px-3 py-2 border border-gray-100 rounded-xl placeholder:text-gray-400"></textarea>
            </div>
            <div class="flex gap-2 mt-4">
              <button (click)="closeModal()" class="flex-1 px-4 py-2 border rounded-xl">Cancelar</button>
              <button (click)="save()" [disabled]="!isFormValid()" class="flex-1 px-4 py-2 bg-violet-600 text-white rounded-xl disabled:opacity-50 disabled:cursor-not-allowed">Guardar</button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class SuppliersComponent {
  private auth = inject(AuthService);
  private alert = inject(AlertService);
  private inv = inject(InventoryService);

  usersIcon = Users; plusIcon = Plus; editIcon = Edit; trash2Icon = Trash2; searchIcon = Search;

  suppliers = signal<Supplier[]>([]);
  filteredSuppliers = signal<Supplier[]>([]);
  searchTerm = '';
  showModal = signal(false);
  editMode = signal(false);
  selectedId = '';
  formData: any = {};

  ngOnInit() {
    this.load();
  }

  load() {
    this.inv.getSuppliers().subscribe(data => {
      const orgId = this.auth.organizationId();
      this.suppliers.set(data.filter(s => s.organizationId === orgId));
      this.applyFilters();
    });
  }

  applyFilters() {
    let filtered = [...this.suppliers()];
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(s => s.supplierName.toLowerCase().includes(term) || s.ruc?.toLowerCase().includes(term));
    }
    this.filteredSuppliers.set(filtered);
  }

  openModal() {
    this.editMode.set(false);
    this.formData = {};
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  edit(s: Supplier) {
    this.editMode.set(true);
    this.selectedId = s.id;
    this.formData = { supplierName: s.supplierName, ruc: s.ruc, email: s.email, phone: s.phone, address: s.address };
    this.showModal.set(true);
  }

  isFormValid(): boolean {
    if (!this.formData.supplierName) return false;
    if (this.formData.ruc && this.formData.ruc.length !== 20) return false;
    if (this.formData.phone && this.formData.phone.length !== 9) return false;
    return true;
  }

  sanitizeRuc(): void {
    if (this.formData.ruc) this.formData.ruc = this.formData.ruc.replace(/[^0-9]/g, '');
  }

  sanitizePhone(): void {
    if (this.formData.phone) this.formData.phone = this.formData.phone.replace(/[^0-9]/g, '');
  }

  save() {
    const orgId = this.auth.organizationId();
    if (!orgId || !this.isFormValid()) return;

    const req: CreateSupplierRequest = { organizationId: orgId, ...this.formData };
    const op = this.editMode() ? this.inv.updateSupplier(this.selectedId, req) : this.inv.createSupplier(req);

    op.subscribe({
      next: () => {
        this.alert.success('Éxito', 'Proveedor guardado');
        this.closeModal();
        this.load();
      },
      error: () => this.alert.error('Error', 'No se pudo guardar')
    });
  }

  delete(s: Supplier) {
    if (!confirm(`¿Eliminar "${s.supplierName}"?`)) return;
    this.inv.deleteSupplier(s.id).subscribe({
      next: () => {
        this.alert.success('Éxito', 'Proveedor eliminado');
        this.load();
      },
      error: () => this.alert.error('Error', 'No se pudo eliminar')
    });
  }
}
