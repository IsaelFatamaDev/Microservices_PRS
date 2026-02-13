import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Plus, Search, Edit, Trash2, RotateCcw, MapPin, X, Save, Loader2 } from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { Zone, ApiResponse, RecordStatus } from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
     selector: 'app-zones',
     standalone: true,
     imports: [CommonModule, FormsModule, LucideAngularModule],
     template: `
    <div>
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Zonas</h1>
          <p class="text-gray-500">Gestión de zonas de la organización</p>
        </div>
        <button
          (click)="openModal()"
          class="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          <lucide-icon [img]="plusIcon" [size]="20"></lucide-icon>
          Nueva Zona
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
                placeholder="Buscar zona..."
                class="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <select
              [(ngModel)]="statusFilter"
              (change)="loadZones()"
              class="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              <option value="A">Activos</option>
              <option value="I">Inactivos</option>
              <option value="">Todos</option>
            </select>
          </div>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Descripción</th>
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
              } @else if (filteredZones().length === 0) {
                <tr>
                  <td colspan="4" class="px-6 py-12 text-center">
                    <lucide-icon [img]="mapPinIcon" [size]="48" class="text-gray-300 mx-auto mb-3"></lucide-icon>
                    <p class="text-gray-500">No se encontraron zonas</p>
                  </td>
                </tr>
              } @else {
                @for (zone of filteredZones(); track zone.id) {
                  <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                          <lucide-icon [img]="mapPinIcon" [size]="20" class="text-blue-600"></lucide-icon>
                        </div>
                        <span class="font-medium text-gray-800">{{ zone.name || zone.zoneName }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-gray-600">{{ zone.description || '-' }}</td>
                    <td class="px-6 py-4">
                      <span
                        class="inline-flex px-2.5 py-1 text-xs font-medium rounded-full"
                        [class.bg-green-100]="zone.recordStatus === 'A'"
                        [class.text-green-700]="zone.recordStatus === 'A'"
                        [class.bg-red-100]="zone.recordStatus === 'I'"
                        [class.text-red-700]="zone.recordStatus === 'I'">
                        {{ zone.recordStatus === 'A' ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-2">
                        <button
                          (click)="editZone(zone)"
                          class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                          <lucide-icon [img]="editIcon" [size]="18"></lucide-icon>
                        </button>
                        @if (zone.recordStatus === 'A') {
                          <button
                            (click)="deleteZone(zone)"
                            class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                            <lucide-icon [img]="trashIcon" [size]="18"></lucide-icon>
                          </button>
                        } @else {
                          <button
                            (click)="restoreZone(zone)"
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
            <h2 class="text-lg font-semibold text-gray-800">{{ editingZone() ? 'Editar' : 'Nueva' }} Zona</h2>
            <button (click)="closeModal()" class="p-1 hover:bg-gray-100 rounded-lg">
              <lucide-icon [img]="closeIcon" [size]="20" class="text-gray-500"></lucide-icon>
            </button>
          </div>
          <form (ngSubmit)="saveZone()" class="p-4 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Nombre *</label>
              <input
                type="text"
                [(ngModel)]="form.name"
                name="name"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Zona Norte">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Descripción</label>
              <textarea
                [(ngModel)]="form.description"
                name="description"
                rows="3"
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                placeholder="Descripción de la zona..."></textarea>
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
export class ZonesComponent implements OnInit {
     private http = inject(HttpClient);
     private alertService = inject(AlertService);
     private authService = inject(AuthService);

     zones = signal<Zone[]>([]);
     filteredZones = signal<Zone[]>([]);
     isLoading = signal(false);
     showModal = signal(false);
     isSubmitting = signal(false);
     editingZone = signal<Zone | null>(null);

     searchTerm = '';
     statusFilter: RecordStatus | '' = 'A';

     form = { name: '', description: '' };

     plusIcon = Plus;
     searchIcon = Search;
     editIcon = Edit;
     trashIcon = Trash2;
     restoreIcon = RotateCcw;
     mapPinIcon = MapPin;
     closeIcon = X;
     saveIcon = Save;
     loaderIcon = Loader2;

     ngOnInit(): void {
          this.loadZones();
     }

     loadZones(): void {
          this.isLoading.set(true);
          const orgId = this.authService.organizationId();

          const params: any = { organizationId: orgId };
          if (this.statusFilter) {
               params.recordStatus = this.statusFilter;
          }

          this.http.get<ApiResponse<Zone[]>>(`${environment.apiUrl}/zones`, { params }).subscribe({
               next: res => {
                    this.zones.set(res.data);
                    this.filterZones();
                    this.isLoading.set(false);
               },
               error: () => this.isLoading.set(false)
          });
     }

     onSearch(): void {
          this.filterZones();
     }

     private filterZones(): void {
          let result = this.zones();
          if (this.searchTerm) {
               const term = this.searchTerm.toLowerCase();
               result = result.filter(z => (z.name || z.zoneName || '').toLowerCase().includes(term));
          }
          this.filteredZones.set(result);
     }

     openModal(): void {
          this.form = { name: '', description: '' };
          this.editingZone.set(null);
          this.showModal.set(true);
     }

     closeModal(): void {
          this.showModal.set(false);
          this.editingZone.set(null);
     }

     editZone(zone: Zone): void {
          this.form = { name: zone.name || zone.zoneName || '', description: zone.description || '' };
          this.editingZone.set(zone);
          this.showModal.set(true);
     }

     saveZone(): void {
          if (!this.form.name) {
               this.alertService.warning('Campo requerido', 'El nombre es obligatorio');
               return;
          }

          this.isSubmitting.set(true);
          const orgId = this.authService.organizationId();
          const payload = { ...this.form, organizationId: orgId };

          if (this.editingZone()) {
               this.http.put(`${environment.apiUrl}/zones/${this.editingZone()!.id}`, payload).subscribe({
                    next: () => {
                         this.isSubmitting.set(false);
                         this.alertService.success('Actualizado', 'Zona actualizada correctamente');
                         this.closeModal();
                         this.loadZones();
                    },
                    error: () => this.isSubmitting.set(false)
               });
          } else {
               this.http.post(`${environment.apiUrl}/zones`, payload).subscribe({
                    next: () => {
                         this.isSubmitting.set(false);
                         this.alertService.success('Creado', 'Zona creada correctamente');
                         this.closeModal();
                         this.loadZones();
                    },
                    error: () => this.isSubmitting.set(false)
               });
          }
     }

     async deleteZone(zone: Zone): Promise<void> {
          const zoneName = zone.name || zone.zoneName || 'esta zona';
          const result = await this.alertService.confirmDelete(zoneName);
          if (result.isConfirmed) {
               this.http.delete(`${environment.apiUrl}/zones/${zone.id}`).subscribe({
                    next: () => {
                         this.alertService.success('Eliminado', 'Zona eliminada correctamente');
                         this.loadZones();
                    }
               });
          }
     }

     async restoreZone(zone: Zone): Promise<void> {
          const zoneName = zone.name || zone.zoneName || 'esta zona';
          const result = await this.alertService.confirmRestore(zoneName);
          if (result.isConfirmed) {
               this.http.patch(`${environment.apiUrl}/zones/${zone.id}/restore`, {}).subscribe({
                    next: () => {
                         this.alertService.success('Restaurado', 'Zona restaurada correctamente');
                         this.loadZones();
                    }
               });
          }
     }
}
