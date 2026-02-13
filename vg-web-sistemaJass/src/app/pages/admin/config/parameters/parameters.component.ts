import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Settings, Save, Loader2 } from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { Parameter, ApiResponse } from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
     selector: 'app-parameters',
     standalone: true,
     imports: [CommonModule, FormsModule, LucideAngularModule],
     template: `
    <div>
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Parámetros</h1>
          <p class="text-gray-500">Configuración general del sistema</p>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-6">
        @if (isLoading()) {
          <div class="text-center py-12 text-gray-500">
            <div class="flex items-center justify-center gap-2">
              <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
              Cargando...
            </div>
          </div>
        } @else {
          <form (ngSubmit)="saveParameters()" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              @for (param of parameters(); track param.id) {
                <div class="p-4 border border-gray-200 rounded-lg">
                  <div class="flex items-center gap-3 mb-3">
                    <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                      <lucide-icon [img]="settingsIcon" [size]="20" class="text-blue-600"></lucide-icon>
                    </div>
                    <div>
                      <p class="font-medium text-gray-800">{{ param.name || param.parameterKey }}</p>
                      <p class="text-xs text-gray-500">{{ param.key || param.parameterKey }}</p>
                    </div>
                  </div>
                  <input
                    type="text"
                    [(ngModel)]="param.parameterValue"
                    [name]="param.parameterKey"
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    [placeholder]="param.description || ''">
                  @if (param.description) {
                    <p class="text-xs text-gray-400 mt-1">{{ param.description }}</p>
                  }
                </div>
              }
            </div>

            @if (parameters().length === 0) {
              <div class="text-center py-12">
                <lucide-icon [img]="settingsIcon" [size]="48" class="text-gray-300 mx-auto mb-3"></lucide-icon>
                <p class="text-gray-500">No hay parámetros configurados</p>
              </div>
            } @else {
              <div class="flex justify-end pt-4 border-t border-gray-200">
                <button
                  type="submit"
                  [disabled]="isSubmitting()"
                  class="inline-flex items-center gap-2 px-6 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-blue-400 transition-colors">
                  @if (isSubmitting()) {
                    <lucide-icon [img]="loaderIcon" [size]="20" class="animate-spin"></lucide-icon>
                    Guardando...
                  } @else {
                    <lucide-icon [img]="saveIcon" [size]="20"></lucide-icon>
                    Guardar Cambios
                  }
                </button>
              </div>
            }
          </form>
        }
      </div>
    </div>
  `
})
export class ParametersComponent implements OnInit {
     private http = inject(HttpClient);
     private alertService = inject(AlertService);
     private authService = inject(AuthService);

     parameters = signal<Parameter[]>([]);
     isLoading = signal(false);
     isSubmitting = signal(false);

     settingsIcon = Settings;
     saveIcon = Save;
     loaderIcon = Loader2;

     ngOnInit(): void {
          this.loadParameters();
     }

     loadParameters(): void {
          this.isLoading.set(true);
          const orgId = this.authService.organizationId();

          this.http.get<ApiResponse<Parameter[]>>(`${environment.apiUrl}/parameters`, {
               params: { organizationId: orgId || '' }
          }).subscribe({
               next: res => {
                    this.parameters.set(res.data);
                    this.isLoading.set(false);
               },
               error: () => this.isLoading.set(false)
          });
     }

     saveParameters(): void {
          this.isSubmitting.set(true);
          const orgId = this.authService.organizationId();

          const updates = this.parameters().map(p => ({
               id: p.id,
               value: p.parameterValue || p.value
          }));

          this.http.put(`${environment.apiUrl}/parameters/batch`, {
               organizationId: orgId,
               parameters: updates
          }).subscribe({
               next: () => {
                    this.isSubmitting.set(false);
                    this.alertService.success('Guardado', 'Parámetros actualizados correctamente');
               },
               error: () => this.isSubmitting.set(false)
          });
     }
}
