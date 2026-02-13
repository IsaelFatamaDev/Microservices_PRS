import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Plus, Edit, Trash2, RotateCcw, DollarSign, X, Save, Loader2 } from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { Fare, ApiResponse, RecordStatus } from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
     selector: 'app-fares',
     standalone: true,
     imports: [CommonModule, FormsModule, LucideAngularModule],
     template: `
    <div>
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Tarifas</h1>
          <p class="text-gray-500">Configuración de tarifas por consumo</p>
        </div>
        <button
          (click)="openModal()"
          class="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          <lucide-icon [img]="plusIcon" [size]="20"></lucide-icon>
          Nueva Tarifa
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        @if (isLoading()) {
          <div class="col-span-full text-center py-12 text-gray-500">
            <div class="flex items-center justify-center gap-2">
              <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
              Cargando...
            </div>
          </div>
        } @else if (fares().length === 0) {
          <div class="col-span-full text-center py-12">
            <lucide-icon [img]="dollarIcon" [size]="48" class="text-gray-300 mx-auto mb-3"></lucide-icon>
            <p class="text-gray-500">No hay tarifas configuradas</p>
          </div>
        } @else {
          @for (fare of fares(); track fare.id) {
            <div class="bg-white rounded-xl shadow-sm p-5 hover:shadow-md transition-shadow"
                 [class.opacity-60]="fare.recordStatus === 'I'">
              <div class="flex items-start justify-between mb-4">
                <div class="w-12 h-12 rounded-xl flex items-center justify-center"
                     [class.bg-green-100]="fare.recordStatus === 'A'"
                     [class.bg-gray-100]="fare.recordStatus === 'I'">
                  <lucide-icon [img]="dollarIcon" [size]="24"
                               [class.text-green-600]="fare.recordStatus === 'A'"
                               [class.text-gray-400]="fare.recordStatus === 'I'"></lucide-icon>
                </div>
                <div class="flex gap-1">
                  <button
                    (click)="editFare(fare)"
                    class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                    <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                  </button>
                  @if (fare.recordStatus === 'A') {
                    <button
                      (click)="deleteFare(fare)"
                      class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                      <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                    </button>
                  } @else {
                    <button
                      (click)="restoreFare(fare)"
                      class="p-2 text-green-600 hover:bg-green-50 rounded-lg transition-colors">
                      <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                    </button>
                  }
                </div>
              </div>
              <h3 class="font-semibold text-gray-800 mb-2">{{ fare.name || fare.fareName }}</h3>
              <div class="space-y-1 text-sm">
                <p class="text-gray-500">Rango: <span class="font-medium text-gray-700">{{ fare.minCubicMeters || 0 }} - {{ fare.maxCubicMeters || 0 }} m³</span></p>
                <p class="text-gray-500">Precio: <span class="font-medium text-green-600">S/ {{ (fare.pricePerCubicMeter || fare.amount || 0).toFixed(2) }} / m³</span></p>
                @if (fare.basePrice) {
                  <p class="text-gray-500">Base: <span class="font-medium text-gray-700">S/ {{ fare.basePrice.toFixed(2) }}</span></p>
                }
              </div>
            </div>
          }
        }
      </div>
    </div>

    @if (showModal()) {
      <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md">
          <div class="flex items-center justify-between p-4 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-800">{{ editingFare() ? 'Editar' : 'Nueva' }} Tarifa</h2>
            <button (click)="closeModal()" class="p-1 hover:bg-gray-100 rounded-lg">
              <lucide-icon [img]="closeIcon" [size]="20" class="text-gray-500"></lucide-icon>
            </button>
          </div>
          <form (ngSubmit)="saveFare()" class="p-4 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Nombre *</label>
              <input
                type="text"
                [(ngModel)]="form.name"
                name="name"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Tarifa Básica">
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Mínimo (m³) *</label>
                <input
                  type="number"
                  [(ngModel)]="form.minCubicMeters"
                  name="minCubicMeters"
                  required
                  min="0"
                  class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="0">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Máximo (m³) *</label>
                <input
                  type="number"
                  [(ngModel)]="form.maxCubicMeters"
                  name="maxCubicMeters"
                  required
                  min="0"
                  class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="20">
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Precio / m³ *</label>
                <input
                  type="number"
                  [(ngModel)]="form.pricePerCubicMeter"
                  name="pricePerCubicMeter"
                  required
                  min="0"
                  step="0.01"
                  class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="1.50">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Cargo Fijo</label>
                <input
                  type="number"
                  [(ngModel)]="form.basePrice"
                  name="basePrice"
                  min="0"
                  step="0.01"
                  class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="5.00">
              </div>
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
export class FaresComponent implements OnInit {
     private http = inject(HttpClient);
     private alertService = inject(AlertService);
     private authService = inject(AuthService);

     fares = signal<Fare[]>([]);
     isLoading = signal(false);
     showModal = signal(false);
     isSubmitting = signal(false);
     editingFare = signal<Fare | null>(null);

     form = { name: '', minCubicMeters: 0, maxCubicMeters: 0, pricePerCubicMeter: 0, basePrice: 0 };

     plusIcon = Plus;
     editIcon = Edit;
     trashIcon = Trash2;
     restoreIcon = RotateCcw;
     dollarIcon = DollarSign;
     closeIcon = X;
     saveIcon = Save;
     loaderIcon = Loader2;

     ngOnInit(): void {
          this.loadFares();
     }

     loadFares(): void {
          this.isLoading.set(true);
          const orgId = this.authService.organizationId();

          this.http.get<ApiResponse<Fare[]>>(`${environment.apiUrl}/fares`, {
               params: { organizationId: orgId || '' }
          }).subscribe({
               next: res => {
                    this.fares.set(res.data);
                    this.isLoading.set(false);
               },
               error: () => this.isLoading.set(false)
          });
     }

     openModal(): void {
          this.form = { name: '', minCubicMeters: 0, maxCubicMeters: 0, pricePerCubicMeter: 0, basePrice: 0 };
          this.editingFare.set(null);
          this.showModal.set(true);
     }

     closeModal(): void {
          this.showModal.set(false);
          this.editingFare.set(null);
     }

     editFare(fare: Fare): void {
          this.form = {
               name: fare.name || fare.fareName || '',
               minCubicMeters: fare.minCubicMeters || 0,
               maxCubicMeters: fare.maxCubicMeters || 0,
               pricePerCubicMeter: fare.pricePerCubicMeter || fare.amount || 0,
               basePrice: fare.basePrice || 0
          };
          this.editingFare.set(fare);
          this.showModal.set(true);
     }

     saveFare(): void {
          if (!this.form.name || this.form.pricePerCubicMeter <= 0) {
               this.alertService.warning('Campos requeridos', 'Complete todos los campos obligatorios');
               return;
          }

          this.isSubmitting.set(true);
          const orgId = this.authService.organizationId();
          const payload = { ...this.form, organizationId: orgId };

          if (this.editingFare()) {
               this.http.put(`${environment.apiUrl}/fares/${this.editingFare()!.id}`, payload).subscribe({
                    next: () => {
                         this.isSubmitting.set(false);
                         this.alertService.success('Actualizado', 'Tarifa actualizada correctamente');
                         this.closeModal();
                         this.loadFares();
                    },
                    error: () => this.isSubmitting.set(false)
               });
          } else {
               this.http.post(`${environment.apiUrl}/fares`, payload).subscribe({
                    next: () => {
                         this.isSubmitting.set(false);
                         this.alertService.success('Creado', 'Tarifa creada correctamente');
                         this.closeModal();
                         this.loadFares();
                    },
                    error: () => this.isSubmitting.set(false)
               });
          }
     }

     async deleteFare(fare: Fare): Promise<void> {
          const fareName = fare.name || fare.fareName || 'esta tarifa';
          const result = await this.alertService.confirmDelete(fareName);
          if (result.isConfirmed) {
               this.http.delete(`${environment.apiUrl}/fares/${fare.id}`).subscribe({
                    next: () => {
                         this.alertService.success('Eliminado', 'Tarifa eliminada correctamente');
                         this.loadFares();
                    }
               });
          }
     }

     async restoreFare(fare: Fare): Promise<void> {
          const fareName = fare.name || fare.fareName || 'esta tarifa';
          const result = await this.alertService.confirmRestore(fareName);
          if (result.isConfirmed) {
               this.http.patch(`${environment.apiUrl}/fares/${fare.id}/restore`, {}).subscribe({
                    next: () => {
                         this.alertService.success('Restaurado', 'Tarifa restaurada correctamente');
                         this.loadFares();
                    }
               });
          }
     }
}
