import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Plus, Search, Edit, Trash2, RotateCcw, Route, X, Save, Loader2 } from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { Street, Zone, ApiResponse, RecordStatus } from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
     selector: 'app-streets',
     standalone: true,
     imports: [CommonModule, FormsModule, LucideAngularModule],
     template: `
    <div>
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Calles</h1>
          <p class="text-gray-500">Gestión de calles por zona</p>
        </div>
        <button
          (click)="openModal()"
          class="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          <lucide-icon [img]="plusIcon" [size]="20"></lucide-icon>
          Nueva Calle
        </button>
      </div>

      <div class="bg-white rounded-xl shadow-sm">
        <div class="p-4 border-b border-gray-200">
          <div class="flex flex-col sm:flex-row gap-4">
            <div class="relative flex-1">
              <lucide-icon [img]="searchIcon" [size]="20" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
              <input
                type="text"
                [(ngModel)]="searchTerm"
                (input)="onSearch()"
                placeholder="Buscar calle..."
                class="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <select
              [(ngModel)]="zoneFilter"
              (change)="loadStreets()"
              class="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              <option value="">Todas las zonas</option>
              @for (zone of zones(); track zone.id) {
                <option [value]="zone.id">{{ zone.name || zone.zoneName }}</option>
              }
            </select>
            <select
              [(ngModel)]="statusFilter"
              (change)="loadStreets()"
              class="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              <option value="ACTIVE">Activos</option>
              <option value="INACTIVE">Inactivos</option>
              <option value="">Todos</option>
            </select>
          </div>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Zona</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              @if (isLoading()) {
                <tr>
                  <td colspan="4" class="px-6 py-12 text-center text-gray-500">
                    <div class="flex items-center justify-center gap-2">
                      <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                      Cargando...
                    </div>
                  </td>
                </tr>
              } @else if (filteredStreets().length === 0) {
                <tr>
                  <td colspan="4" class="px-6 py-12 text-center">
                    <lucide-icon [img]="routeIcon" [size]="48" class="text-gray-300 mx-auto mb-3"></lucide-icon>
                    <p class="text-gray-500">No se encontraron calles</p>
                  </td>
                </tr>
              } @else {
                @for (street of filteredStreets(); track street.id) {
                  <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                          <lucide-icon [img]="routeIcon" [size]="20" class="text-green-600"></lucide-icon>
                        </div>
                        <span class="font-medium text-gray-800">{{ street.name || street.streetName }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-gray-600">{{ getZoneName(street.zoneId) }}</td>
                    <td class="px-6 py-4">
                      <span
                        class="inline-flex px-2.5 py-1 text-xs font-medium rounded-full"
                        [class.bg-green-100]="street.recordStatus === 'ACTIVE'"
                        [class.text-green-700]="street.recordStatus === 'ACTIVE'"
                        [class.bg-red-100]="street.recordStatus === 'INACTIVE'"
                        [class.text-red-700]="street.recordStatus === 'INACTIVE'">
                        {{ street.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-2">
                        <button
                          (click)="editStreet(street)"
                          class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                          <lucide-icon [img]="editIcon" [size]="18"></lucide-icon>
                        </button>
                        @if (street.recordStatus === 'ACTIVE') {
                          <button
                            (click)="deleteStreet(street)"
                            class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                            <lucide-icon [img]="trashIcon" [size]="18"></lucide-icon>
                          </button>
                        } @else {
                          <button
                            (click)="restoreStreet(street)"
                            class="p-2 text-green-600 hover:bg-green-50 rounded-lg transition-colors">
                            <lucide-icon [img]="restoreIcon" [size]="18"></lucide-icon>
                          </button>
                        }
                      </div>
                    </td>
                  </tr>
                }
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>

    @if (showModal()) {
      <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md">
          <div class="flex items-center justify-between p-4 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-800">{{ editingStreet() ? 'Editar' : 'Nueva' }} Calle</h2>
            <button (click)="closeModal()" class="p-1 hover:bg-gray-100 rounded-lg">
              <lucide-icon [img]="closeIcon" [size]="20" class="text-gray-500"></lucide-icon>
            </button>
          </div>
          <form (ngSubmit)="saveStreet()" class="p-4 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Zona *</label>
              <select
                [(ngModel)]="form.zoneId"
                name="zoneId"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                <option value="">Seleccionar zona</option>
                @for (zone of zones(); track zone.id) {
                  <option [value]="zone.id">{{ zone.name || zone.zoneName }}</option>
                }
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Nombre *</label>
              <input
                type="text"
                [(ngModel)]="form.name"
                name="name"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Jr. Los Pinos">
            </div>
            <div class="flex items-center justify-end gap-3 pt-4">
              <button
                type="button"
                (click)="closeModal()"
                class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors">
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="isSubmitting()"
                class="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-blue-400 transition-colors">
                @if (isSubmitting()) {
                  <lucide-icon [img]="loaderIcon" [size]="18" class="animate-spin"></lucide-icon>
                } @else {
                  <lucide-icon [img]="saveIcon" [size]="18"></lucide-icon>
                }
                Guardar
              </button>
            </div>
          </form>
        </div>
      </div>
    }
  `
})
export class StreetsComponent implements OnInit {
     private http = inject(HttpClient);
     private alertService = inject(AlertService);
     private authService = inject(AuthService);

     streets = signal<Street[]>([]);
     filteredStreets = signal<Street[]>([]);
     zones = signal<Zone[]>([]);
     isLoading = signal(false);
     showModal = signal(false);
     isSubmitting = signal(false);
     editingStreet = signal<Street | null>(null);

     searchTerm = '';
     statusFilter: RecordStatus | '' = 'ACTIVE';
     zoneFilter = '';

     form = { name: '', zoneId: '' };

     plusIcon = Plus;
     searchIcon = Search;
     editIcon = Edit;
     trashIcon = Trash2;
     restoreIcon = RotateCcw;
     routeIcon = Route;
     closeIcon = X;
     saveIcon = Save;
     loaderIcon = Loader2;

     ngOnInit(): void {
          this.loadZones();
          this.loadStreets();
     }

     loadZones(): void {
          const orgId = this.authService.organizationId();
          this.http.get<ApiResponse<Zone[]>>(`${environment.apiUrl}/zones`, {
               params: { organizationId: orgId || '', recordStatus: 'ACTIVE' }
          }).subscribe({
               next: res => this.zones.set(res.data)
          });
     }

     loadStreets(): void {
          this.isLoading.set(true);
          const orgId = this.authService.organizationId();

          const params: any = { organizationId: orgId };
          if (this.statusFilter) params.recordStatus = this.statusFilter;
          if (this.zoneFilter) params.zoneId = this.zoneFilter;

          this.http.get<ApiResponse<Street[]>>(`${environment.apiUrl}/streets`, { params }).subscribe({
               next: res => {
                    this.streets.set(res.data);
                    this.filterStreets();
                    this.isLoading.set(false);
               },
               error: () => this.isLoading.set(false)
          });
     }

     onSearch(): void {
          this.filterStreets();
     }

     private filterStreets(): void {
          let result = this.streets();
          if (this.searchTerm) {
               const term = this.searchTerm.toLowerCase();
               result = result.filter(s => (s.name || s.streetName || '').toLowerCase().includes(term));
          }
          this.filteredStreets.set(result);
     }

     getZoneName(zoneId: string): string {
          const zone = this.zones().find(z => z.id === zoneId);
          return zone?.name || zone?.zoneName || '-';
     }

     openModal(): void {
          this.form = { name: '', zoneId: '' };
          this.editingStreet.set(null);
          this.showModal.set(true);
     }

     closeModal(): void {
          this.showModal.set(false);
          this.editingStreet.set(null);
     }

     editStreet(street: Street): void {
          this.form = { name: street.name || street.streetName || '', zoneId: street.zoneId };
          this.editingStreet.set(street);
          this.showModal.set(true);
     }

     saveStreet(): void {
          if (!this.form.name || !this.form.zoneId) {
               this.alertService.warning('Campos requeridos', 'Complete todos los campos obligatorios');
               return;
          }

          this.isSubmitting.set(true);
          const orgId = this.authService.organizationId();
          const payload = { ...this.form, organizationId: orgId };

          if (this.editingStreet()) {
               this.http.put(`${environment.apiUrl}/streets/${this.editingStreet()!.id}`, payload).subscribe({
                    next: () => {
                         this.isSubmitting.set(false);
                         this.alertService.success('Actualizado', 'Calle actualizada correctamente');
                         this.closeModal();
                         this.loadStreets();
                    },
                    error: () => this.isSubmitting.set(false)
               });
          } else {
               this.http.post(`${environment.apiUrl}/streets`, payload).subscribe({
                    next: () => {
                         this.isSubmitting.set(false);
                         this.alertService.success('Creado', 'Calle creada correctamente');
                         this.closeModal();
                         this.loadStreets();
                    },
                    error: () => this.isSubmitting.set(false)
               });
          }
     }

     async deleteStreet(street: Street): Promise<void> {
          const streetName = street.name || street.streetName || 'esta calle';
          const result = await this.alertService.confirmDelete(streetName);
          if (result.isConfirmed) {
               this.http.delete(`${environment.apiUrl}/streets/${street.id}`).subscribe({
                    next: () => {
                         this.alertService.success('Eliminado', 'Calle eliminada correctamente');
                         this.loadStreets();
                    }
               });
          }
     }

     async restoreStreet(street: Street): Promise<void> {
          const streetName = street.name || street.streetName || 'esta calle';
          const result = await this.alertService.confirmRestore(streetName);
          if (result.isConfirmed) {
               this.http.patch(`${environment.apiUrl}/streets/${street.id}/restore`, {}).subscribe({
                    next: () => {
                         this.alertService.success('Restaurado', 'Calle restaurada correctamente');
                         this.loadStreets();
                    }
               });
          }
     }
}
