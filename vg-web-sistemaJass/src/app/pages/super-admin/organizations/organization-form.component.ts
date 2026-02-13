import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, ArrowLeft, Save, Loader2 } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { Organization, ApiResponse, CreateOrganizationRequest, UpdateOrganizationRequest } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';

@Component({
     selector: 'app-organization-form',
     standalone: true,
     imports: [CommonModule, FormsModule, RouterLink, LucideAngularModule],
     template: `
    <div>
      <div class="flex items-center gap-4 mb-6">
        <a routerLink="/super-admin/organizations" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
          <lucide-icon [img]="backIcon" [size]="24" class="text-gray-600"></lucide-icon>
        </a>
        <div>
          <h1 class="text-2xl font-bold text-gray-800">{{ isEditMode() ? 'Editar' : 'Nueva' }} Organización</h1>
          <p class="text-gray-500">{{ isEditMode() ? 'Modifica los datos de la organización' : 'Registra una nueva JASS' }}</p>
        </div>
      </div>

      <form (ngSubmit)="onSubmit()" class="bg-white rounded-xl shadow-sm p-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Nombre *</label>
            <input
              type="text"
              [(ngModel)]="form.name"
              name="name"
              required
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="JASS San Pedro">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">RUC *</label>
            <input
              type="text"
              [(ngModel)]="form.ruc"
              name="ruc"
              required
              maxlength="11"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="20123456789">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Email *</label>
            <input
              type="email"
              [(ngModel)]="form.email"
              name="email"
              required
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="jass@example.com">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Teléfono</label>
            <input
              type="text"
              [(ngModel)]="form.phone"
              name="phone"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="987654321">
          </div>

          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Dirección</label>
            <input
              type="text"
              [(ngModel)]="form.address"
              name="address"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Av. Principal 123">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Departamento</label>
            <input
              type="text"
              [(ngModel)]="form.department"
              name="department"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Lima">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Provincia</label>
            <input
              type="text"
              [(ngModel)]="form.province"
              name="province"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Lima">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Distrito</label>
            <input
              type="text"
              [(ngModel)]="form.district"
              name="district"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="San Juan">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Logo URL</label>
            <input
              type="text"
              [(ngModel)]="form.logo"
              name="logo"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="https://...">
          </div>

          @if (!isEditMode()) {
            <div class="md:col-span-2 border-t border-gray-200 pt-6 mt-2">
              <h3 class="text-lg font-semibold text-gray-800 mb-4">Administrador</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Nombres *</label>
                  <input
                    type="text"
                    [(ngModel)]="adminForm.firstName"
                    name="adminFirstName"
                    required
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Juan">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Apellidos *</label>
                  <input
                    type="text"
                    [(ngModel)]="adminForm.lastName"
                    name="adminLastName"
                    required
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Pérez">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Email *</label>
                  <input
                    type="email"
                    [(ngModel)]="adminForm.email"
                    name="adminEmail"
                    required
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="admin@jass.com">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">DNI *</label>
                  <input
                    type="text"
                    [(ngModel)]="adminForm.dni"
                    name="adminDni"
                    required
                    maxlength="8"
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="12345678">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Teléfono *</label>
                  <input
                    type="text"
                    [(ngModel)]="adminForm.phone"
                    name="adminPhone"
                    required
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="987654321">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Contraseña *</label>
                  <input
                    type="password"
                    [(ngModel)]="adminForm.password"
                    name="adminPassword"
                    required
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="••••••••">
                </div>
              </div>
            </div>
          }
        </div>

        <div class="flex items-center justify-end gap-4 mt-8 pt-6 border-t border-gray-200">
          <a routerLink="/super-admin/organizations" class="px-6 py-2.5 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors">
            Cancelar
          </a>
          <button
            type="submit"
            [disabled]="isSubmitting()"
            class="inline-flex items-center gap-2 px-6 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-blue-400 transition-colors">
            @if (isSubmitting()) {
              <lucide-icon [img]="loaderIcon" [size]="20" class="animate-spin"></lucide-icon>
              Guardando...
            } @else {
              <lucide-icon [img]="saveIcon" [size]="20"></lucide-icon>
              {{ isEditMode() ? 'Actualizar' : 'Crear' }}
            }
          </button>
        </div>
      </form>
    </div>
  `
})
export class OrganizationFormComponent implements OnInit {
     private http = inject(HttpClient);
     private router = inject(Router);
     private route = inject(ActivatedRoute);
     private alertService = inject(AlertService);

     isEditMode = signal(false);
     isSubmitting = signal(false);
     organizationId = signal<string | null>(null);

     form: any = {
          name: '',
          organizationName: '',
          ruc: '',
          email: '',
          phone: '',
          address: '',
          department: '',
          province: '',
          district: '',
          logo: ''
     };

     adminForm = {
          firstName: '',
          lastName: '',
          email: '',
          dni: '',
          phone: '',
          password: ''
     };

     backIcon = ArrowLeft;
     saveIcon = Save;
     loaderIcon = Loader2;

     ngOnInit(): void {
          const id = this.route.snapshot.paramMap.get('id');
          if (id) {
               this.organizationId.set(id);
               this.isEditMode.set(true);
               this.loadOrganization(id);
          }
     }

     private loadOrganization(id: string): void {
          this.http.get<ApiResponse<Organization>>(`${environment.apiUrl}/organizations/${id}`).subscribe({
               next: res => {
                    const org = res.data;
                    this.form = {
                         name: org.organizationName || org.name,
                         organizationName: org.organizationName || org.name,
                         ruc: org.ruc || '',
                         email: org.email,
                         phone: org.phone || '',
                         address: org.address || '',
                         department: org.department || '',
                         province: org.province || '',
                         district: org.district || '',
                         logo: org.logoUrl || org.logo || ''
                    };
               }
          });
     }

     onSubmit(): void {
          if (!this.form.name || !this.form.ruc || !this.form.email) {
               this.alertService.warning('Campos requeridos', 'Complete todos los campos obligatorios');
               return;
          }

          if (!this.isEditMode() && (!this.adminForm.firstName || !this.adminForm.lastName || !this.adminForm.email || !this.adminForm.dni || !this.adminForm.password)) {
               this.alertService.warning('Campos requeridos', 'Complete todos los datos del administrador');
               return;
          }

          this.isSubmitting.set(true);

          if (this.isEditMode()) {
               this.updateOrganization();
          } else {
               this.createOrganization();
          }
     }

     private createOrganization(): void {
          const payload = {
               organization: this.form,
               admin: {
                    ...this.adminForm,
                    role: 'ADMIN'
               }
          };

          this.http.post<ApiResponse<Organization>>(`${environment.apiUrl}/organizations`, payload).subscribe({
               next: () => {
                    this.isSubmitting.set(false);
                    this.alertService.success('Creado', 'Organización creada exitosamente');
                    this.router.navigate(['/super-admin/organizations']);
               },
               error: () => {
                    this.isSubmitting.set(false);
               }
          });
     }

     private updateOrganization(): void {
          this.http.put<ApiResponse<Organization>>(`${environment.apiUrl}/organizations/${this.organizationId()}`, this.form).subscribe({
               next: () => {
                    this.isSubmitting.set(false);
                    this.alertService.success('Actualizado', 'Organización actualizada exitosamente');
                    this.router.navigate(['/super-admin/organizations']);
               },
               error: () => {
                    this.isSubmitting.set(false);
               }
          });
     }
}
