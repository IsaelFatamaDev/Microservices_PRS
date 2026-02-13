import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, ArrowLeft, Save, Loader2 } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { User, ApiResponse, CreateUserRequest, UpdateUserRequest, Zone, WaterBox } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
     selector: 'app-user-form',
     standalone: true,
     imports: [CommonModule, FormsModule, RouterLink, LucideAngularModule],
     template: `
    <div>
      <div class="flex items-center gap-4 mb-6">
        <a routerLink="/admin/users" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
          <lucide-icon [img]="backIcon" [size]="24" class="text-gray-600"></lucide-icon>
        </a>
        <div>
          <h1 class="text-2xl font-bold text-gray-800">{{ isEditMode() ? 'Editar' : 'Nuevo' }} Usuario</h1>
          <p class="text-gray-500">{{ isEditMode() ? 'Modifica los datos del usuario' : 'Registra un nuevo usuario' }}</p>
        </div>
      </div>

      <form (ngSubmit)="onSubmit()" class="bg-white rounded-xl shadow-sm p-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Nombres *</label>
            <input
              type="text"
              [(ngModel)]="form.firstName"
              name="firstName"
              required
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Juan">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Apellidos *</label>
            <input
              type="text"
              [(ngModel)]="form.lastName"
              name="lastName"
              required
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Pérez García">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">DNI *</label>
            <input
              type="text"
              [(ngModel)]="form.dni"
              name="dni"
              required
              maxlength="8"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="12345678">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Email *</label>
            <input
              type="email"
              [(ngModel)]="form.email"
              name="email"
              required
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="usuario@email.com">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Teléfono *</label>
            <input
              type="text"
              [(ngModel)]="form.phone"
              name="phone"
              required
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="987654321">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Rol *</label>
            <select
              [(ngModel)]="form.role"
              name="role"
              required
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              <option value="">Seleccionar rol</option>
              <option value="OPERATOR">Operador</option>
              <option value="CLIENT">Cliente</option>
            </select>
          </div>

          @if (!isEditMode()) {
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Contraseña *</label>
              <input
                type="password"
                [(ngModel)]="form.password"
                name="password"
                required
                class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="••••••••">
            </div>
          }

          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 mb-1.5">Dirección</label>
            <input
              type="text"
              [(ngModel)]="form.address"
              name="address"
              class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Av. Principal 123">
          </div>

          @if (form.role === 'CLIENT') {
            <div class="md:col-span-2 border-t border-gray-200 pt-6 mt-2">
              <h3 class="text-lg font-semibold text-gray-800 mb-4">Asignación de Caja de Agua</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Zona</label>
                  <select
                    [(ngModel)]="selectedZoneId"
                    name="zone"
                    (change)="onZoneChange()"
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                    <option value="">Seleccionar zona</option>
                    @for (zone of zones(); track zone.id) {
                      <option [value]="zone.id">{{ zone.name }}</option>
                    }
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Caja de Agua</label>
                  <select
                    [(ngModel)]="selectedWaterBoxId"
                    name="waterBox"
                    [disabled]="!selectedZoneId"
                    class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:bg-gray-100">
                    <option value="">Seleccionar caja</option>
                    @for (box of filteredWaterBoxes(); track box.id) {
                      <option [value]="box.id">{{ box.code }} - {{ box.description }}</option>
                    }
                  </select>
                </div>
              </div>
            </div>
          }
        </div>

        <div class="flex items-center justify-end gap-4 mt-8 pt-6 border-t border-gray-200">
          <a routerLink="/admin/users" class="px-6 py-2.5 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors">
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
export class UserFormComponent implements OnInit {
     private http = inject(HttpClient);
     private router = inject(Router);
     private route = inject(ActivatedRoute);
     private alertService = inject(AlertService);
     private authService = inject(AuthService);

     isEditMode = signal(false);
     isSubmitting = signal(false);
     userId = signal<string | null>(null);

     zones = signal<Zone[]>([]);
     waterBoxes = signal<WaterBox[]>([]);
     filteredWaterBoxes = signal<WaterBox[]>([]);

     selectedZoneId = '';
     selectedWaterBoxId = '';

     form: CreateUserRequest & { password?: string } = {
          firstName: '',
          lastName: '',
          dni: '',
          email: '',
          phone: '',
          address: '',
          role: 'CLIENT',
          password: '',
          organizationId: ''
     };

     backIcon = ArrowLeft;
     saveIcon = Save;
     loaderIcon = Loader2;

     ngOnInit(): void {
          this.form.organizationId = this.authService.organizationId() || '';

          const id = this.route.snapshot.paramMap.get('id');
          if (id) {
               this.userId.set(id);
               this.isEditMode.set(true);
               this.loadUser(id);
          }

          this.loadZones();
          this.loadWaterBoxes();
     }

     private loadUser(id: string): void {
          this.http.get<ApiResponse<User>>(`${environment.apiUrl}/users/${id}`).subscribe({
               next: res => {
                    const user = res.data;
                    this.form = {
                         firstName: user.firstName,
                         lastName: user.lastName,
                         dni: user.dni,
                         email: user.email,
                         phone: user.phone || '',
                         address: user.address || '',
                         role: user.role,
                         organizationId: user.organizationId
                    };
               }
          });
     }

     private loadZones(): void {
          const orgId = this.authService.organizationId();
          this.http.get<ApiResponse<Zone[]>>(`${environment.apiUrl}/zones`, { params: { organizationId: orgId || '' } }).subscribe({
               next: res => this.zones.set(res.data)
          });
     }

     private loadWaterBoxes(): void {
          const orgId = this.authService.organizationId();
          this.http.get<ApiResponse<WaterBox[]>>(`${environment.apiUrl}/water-boxes`, {
               params: { organizationId: orgId || '', status: 'AVAILABLE' }
          }).subscribe({
               next: res => this.waterBoxes.set(res.data)
          });
     }

     onZoneChange(): void {
          if (this.selectedZoneId) {
               this.filteredWaterBoxes.set(
                    this.waterBoxes().filter(box => box.zoneId === this.selectedZoneId)
               );
          } else {
               this.filteredWaterBoxes.set([]);
          }
          this.selectedWaterBoxId = '';
     }

     onSubmit(): void {
          if (!this.form.firstName || !this.form.lastName || !this.form.dni || !this.form.email || !this.form.role) {
               this.alertService.warning('Campos requeridos', 'Complete todos los campos obligatorios');
               return;
          }

          if (!this.isEditMode() && !this.form.password) {
               this.alertService.warning('Campos requeridos', 'La contraseña es obligatoria');
               return;
          }

          this.isSubmitting.set(true);

          if (this.isEditMode()) {
               this.updateUser();
          } else {
               this.createUser();
          }
     }

     private createUser(): void {
          const payload: any = { ...this.form };

          if (this.form.role === 'CLIENT' && this.selectedWaterBoxId) {
               payload.waterBoxId = this.selectedWaterBoxId;
          }

          this.http.post<ApiResponse<User>>(`${environment.apiUrl}/users`, payload).subscribe({
               next: () => {
                    this.isSubmitting.set(false);
                    this.alertService.success('Creado', 'Usuario creado exitosamente');
                    this.router.navigate(['/admin/users']);
               },
               error: () => {
                    this.isSubmitting.set(false);
               }
          });
     }

     private updateUser(): void {
          const { password, ...updateData } = this.form;

          this.http.put<ApiResponse<User>>(`${environment.apiUrl}/users/${this.userId()}`, updateData).subscribe({
               next: () => {
                    this.isSubmitting.set(false);
                    this.alertService.success('Actualizado', 'Usuario actualizado exitosamente');
                    this.router.navigate(['/admin/users']);
               },
               error: () => {
                    this.isSubmitting.set(false);
               }
          });
     }
}
