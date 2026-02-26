import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, ArrowLeft, Building2, Mail, Phone, MapPin,
  User, UserPlus, Edit, Trash2, RotateCcw, Shield, Calendar, Hash,
  FileText, Save, Loader2, X, Eye, EyeOff
} from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { Organization, ApiResponse, User as UserModel } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';
import {
  sanitizeName, sanitizeDocument, sanitizePhone,
  validateEmail as sharedValidateEmail,
  validatePhone as sharedValidatePhone
} from '../../../core/validators/input-sanitizers';

@Component({
  selector: 'app-organization-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, LucideAngularModule],
  template: `
    <div class="max-w-6xl mx-auto px-4 py-8 space-y-8">
      <div class="flex items-center gap-4">
        <a routerLink="/super-admin/organizations" class="p-2.5 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 transition-all shadow-sm">
          <lucide-icon [img]="backIcon" [size]="20" class="text-gray-500"></lucide-icon>
        </a>
        <div class="flex-1">
          <h1 class="text-2xl font-bold text-gray-900 tracking-tight">Detalle de Organización</h1>
          <p class="text-sm text-gray-500 mt-0.5">Información completa y gestión del administrador</p>
        </div>
        <a [routerLink]="['/super-admin/organizations', orgId()]"
           class="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white font-medium rounded-xl hover:bg-blue-700 transition-colors shadow-sm">
          <lucide-icon [img]="editIcon" [size]="18"></lucide-icon>
          Editar Organización
        </a>
      </div>

      @if (isLoading()) {
        <div class="flex items-center justify-center py-20">
          <div class="w-8 h-8 border-3 border-gray-200 border-t-blue-600 rounded-full animate-spin"></div>
        </div>
      } @else if (org()) {
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-1 space-y-6">
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div class="bg-linear-to-br from-blue-600 to-indigo-700 p-6 flex flex-col items-center">
                <div class="w-20 h-20 bg-white rounded-2xl flex items-center justify-center overflow-hidden shadow-lg mb-3">
                  @if (org()?.logoUrl || org()?.logo) {
                    <img [src]="org()?.logoUrl || org()?.logo" [alt]="org()?.organizationName" class="w-full h-full object-cover">
                  } @else {
                    <lucide-icon [img]="buildingIcon" [size]="36" class="text-blue-500"></lucide-icon>
                  }
                </div>
                <h2 class="text-lg font-bold text-white text-center">{{ org()?.organizationName || org()?.name }}</h2>
                <span class="mt-2 px-3 py-1 text-xs font-bold rounded-full"
                      [ngClass]="org()?.recordStatus === 'ACTIVE' ? 'bg-emerald-400/20 text-emerald-100' : 'bg-red-400/20 text-red-100'">
                  {{ org()?.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                </span>
              </div>
              <div class="p-5 space-y-4">
                <div class="flex items-start gap-3">
                  <lucide-icon [img]="mailIcon" [size]="18" class="text-gray-400 mt-0.5 shrink-0"></lucide-icon>
                  <div>
                    <p class="text-xs text-gray-400 font-medium uppercase">Correo</p>
                    <p class="text-sm text-gray-700 font-medium">{{ org()?.email || '-' }}</p>
                  </div>
                </div>
                <div class="flex items-start gap-3">
                  <lucide-icon [img]="phoneIcon" [size]="18" class="text-gray-400 mt-0.5 shrink-0"></lucide-icon>
                  <div>
                    <p class="text-xs text-gray-400 font-medium uppercase">Teléfono</p>
                    <p class="text-sm text-gray-700 font-medium">{{ org()?.phone || '-' }}</p>
                  </div>
                </div>
                <div class="flex items-start gap-3">
                  <lucide-icon [img]="mapPinIcon" [size]="18" class="text-gray-400 mt-0.5 shrink-0"></lucide-icon>
                  <div>
                    <p class="text-xs text-gray-400 font-medium uppercase">Ubicación</p>
                    <p class="text-sm text-gray-700 font-medium">{{ org()?.district }}</p>
                    <p class="text-xs text-gray-500">{{ org()?.province }}, {{ org()?.department }}</p>
                  </div>
                </div>
                @if (org()?.address) {
                  <div class="flex items-start gap-3">
                    <lucide-icon [img]="mapPinIcon" [size]="18" class="text-gray-400 mt-0.5 shrink-0"></lucide-icon>
                    <div>
                      <p class="text-xs text-gray-400 font-medium uppercase">Dirección</p>
                      <p class="text-sm text-gray-700 font-medium">{{ org()?.address }}</p>
                    </div>
                  </div>
                }
                <div class="flex items-start gap-3">
                  <lucide-icon [img]="calendarIcon" [size]="18" class="text-gray-400 mt-0.5 shrink-0"></lucide-icon>
                  <div>
                    <p class="text-xs text-gray-400 font-medium uppercase">Fecha de registro</p>
                    <p class="text-sm text-gray-700 font-medium">{{ org()?.createdAt | date:'dd/MM/yyyy HH:mm' }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="lg:col-span-2 space-y-6">
            <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
              <div class="px-6 py-5 border-b border-gray-100 flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <div class="p-2 bg-indigo-50 rounded-lg">
                    <lucide-icon [img]="shieldIcon" [size]="20" class="text-indigo-600"></lucide-icon>
                  </div>
                  <div>
                    <h3 class="text-lg font-bold text-gray-900">Administrador</h3>
                    <p class="text-xs text-gray-500">Responsable de la gestión de esta JASS</p>
                  </div>
                </div>
                @if (!admin() && !showCreateForm()) {
                  <button (click)="showCreateForm.set(true)"
                          class="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-xl hover:bg-indigo-700 transition-colors">
                    <lucide-icon [img]="userPlusIcon" [size]="16"></lucide-icon>
                    Crear Administrador
                  </button>
                }
              </div>

              @if (isLoadingAdmin()) {
                <div class="p-8 flex items-center justify-center">
                  <div class="w-6 h-6 border-2 border-gray-200 border-t-indigo-600 rounded-full animate-spin"></div>
                </div>
              } @else if (showCreateForm()) {
                <div class="p-6 space-y-6">
                  <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
                    <div>
                      <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-1.5">Tipo Documento</label>
                      <select [(ngModel)]="newAdmin.documentType" (change)="onDocTypeChange()"
                              class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400">
                        <option value="DNI">DNI</option>
                        <option value="CNE">Carnet de Extranjería</option>
                      </select>
                    </div>
                    <div>
                      <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-1.5">N° Documento *</label>
                      <input type="text" [(ngModel)]="newAdmin.documentNumber" (input)="onDocumentInput($event)"
                             [maxlength]="newAdmin.documentType === 'DNI' ? 8 : 20"
                             [placeholder]="newAdmin.documentType === 'DNI' ? '8 dígitos' : 'N° documento'"
                             class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400"
                             [ngClass]="formErrors['documentNumber'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'">
                      @if (formErrors['documentNumber']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['documentNumber'] }}</p>
                      }
                    </div>
                    <div>
                      <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-1.5">Apellidos *</label>
                      <input type="text" [(ngModel)]="newAdmin.lastName" (input)="onNameInput($event, 'lastName')"
                             placeholder="Apellidos del administrador"
                             class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400"
                             [ngClass]="formErrors['lastName'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'">
                      @if (formErrors['lastName']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['lastName'] }}</p>
                      }
                    </div>
                    <div>
                      <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-1.5">Nombres *</label>
                      <input type="text" [(ngModel)]="newAdmin.firstName" (input)="onNameInput($event, 'firstName')"
                             placeholder="Nombres del administrador"
                             class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400"
                             [ngClass]="formErrors['firstName'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'">
                      @if (formErrors['firstName']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['firstName'] }}</p>
                      }
                    </div>
                    <div>
                      <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-1.5">Correo *</label>
                      <input type="email" [(ngModel)]="newAdmin.email"
                             placeholder="admin@correo.com"
                             class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400"
                             [ngClass]="formErrors['email'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'">
                      @if (formErrors['email']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['email'] }}</p>
                      }
                    </div>
                    <div>
                      <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-1.5">Teléfono</label>
                      <input type="text" [(ngModel)]="newAdmin.phone" (input)="onPhoneInput($event)" maxlength="9"
                             placeholder="987654321"
                             class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-indigo-300 focus:border-indigo-400"
                             [ngClass]="formErrors['phone'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'">
                      @if (formErrors['phone']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['phone'] }}</p>
                      }
                    </div>
                  </div>
                  <div class="flex items-center justify-end gap-3 pt-2">
                    <button (click)="cancelCreate()"
                            class="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 font-medium text-sm transition-colors">
                      Cancelar
                    </button>
                    <button (click)="createAdmin()" [disabled]="isSubmitting()"
                            class="inline-flex items-center gap-2 px-5 py-2.5 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 disabled:bg-indigo-300 font-medium text-sm transition-colors">
                      @if (isSubmitting()) {
                        <lucide-icon [img]="loaderIcon" [size]="16" class="animate-spin"></lucide-icon>
                        Creando...
                      } @else {
                        <lucide-icon [img]="saveIcon" [size]="16"></lucide-icon>
                        Crear Administrador
                      }
                    </button>
                  </div>
                </div>
              } @else if (admin()) {
                <div class="p-6">
                  <div class="flex items-start gap-5">
                    <div class="w-14 h-14 rounded-2xl flex items-center justify-center text-xl font-bold shrink-0"
                         [ngClass]="admin()?.recordStatus === 'ACTIVE' ? 'bg-indigo-100 text-indigo-700' : 'bg-gray-100 text-gray-500'">
                      {{ admin()?.firstName?.charAt(0) }}{{ admin()?.lastName?.charAt(0) }}
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-3 mb-1">
                        <h4 class="text-lg font-bold text-gray-900">{{ admin()?.lastName }}, {{ admin()?.firstName }}</h4>
                        <span class="px-2.5 py-0.5 text-xs font-bold rounded-full border"
                              [ngClass]="admin()?.recordStatus === 'ACTIVE'
                                ? 'bg-emerald-50 text-emerald-700 border-emerald-100'
                                : 'bg-red-50 text-red-600 border-red-100'">
                          {{ admin()?.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                        </span>
                      </div>
                      <p class="text-sm text-gray-500 mb-4">{{ admin()?.roleDisplayName || 'Administrador' }}</p>

                      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div class="flex items-center gap-2.5">
                          <lucide-icon [img]="hashIcon" [size]="16" class="text-gray-400"></lucide-icon>
                          <div>
                            <p class="text-[10px] text-gray-400 font-bold uppercase">{{ admin()?.documentType }}</p>
                            <p class="text-sm text-gray-700 font-medium">{{ admin()?.documentNumber }}</p>
                          </div>
                        </div>
                        <div class="flex items-center gap-2.5">
                          <lucide-icon [img]="mailIcon" [size]="16" class="text-gray-400"></lucide-icon>
                          <div>
                            <p class="text-[10px] text-gray-400 font-bold uppercase">Correo</p>
                            <p class="text-sm text-gray-700 font-medium truncate">{{ admin()?.email || '-' }}</p>
                          </div>
                        </div>
                        <div class="flex items-center gap-2.5">
                          <lucide-icon [img]="phoneIcon" [size]="16" class="text-gray-400"></lucide-icon>
                          <div>
                            <p class="text-[10px] text-gray-400 font-bold uppercase">Teléfono</p>
                            <p class="text-sm text-gray-700 font-medium">{{ admin()?.phone || '-' }}</p>
                          </div>
                        </div>
                        <div class="flex items-center gap-2.5">
                          <lucide-icon [img]="calendarIcon" [size]="16" class="text-gray-400"></lucide-icon>
                          <div>
                            <p class="text-[10px] text-gray-400 font-bold uppercase">Registrado</p>
                            <p class="text-sm text-gray-700 font-medium">{{ admin()?.createdAt | date:'dd/MM/yyyy' }}</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="flex items-center justify-end gap-2 mt-6 pt-4 border-t border-gray-100">
                    @if (admin()?.recordStatus === 'ACTIVE') {
                      <button (click)="deleteAdmin()" [disabled]="isSubmitting()"
                              class="inline-flex items-center gap-2 px-4 py-2 text-red-600 bg-red-50 rounded-xl hover:bg-red-100 font-medium text-sm transition-colors">
                        <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                        Eliminar Admin
                      </button>
                    } @else {
                      <button (click)="restoreAdmin()" [disabled]="isSubmitting()"
                              class="inline-flex items-center gap-2 px-4 py-2 text-emerald-600 bg-emerald-50 rounded-xl hover:bg-emerald-100 font-medium text-sm transition-colors">
                        <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                        Restaurar Admin
                      </button>
                    }
                  </div>
                </div>
              } @else {
                <div class="p-12 text-center">
                  <div class="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-4">
                    <lucide-icon [img]="userIcon" [size]="32" class="text-gray-300"></lucide-icon>
                  </div>
                  <h4 class="font-medium text-gray-700 mb-1">Sin administrador asignado</h4>
                  <p class="text-sm text-gray-500 mb-4">Esta organización no tiene un administrador registrado</p>
                  <button (click)="showCreateForm.set(true)"
                          class="inline-flex items-center gap-2 px-4 py-2.5 bg-indigo-600 text-white text-sm font-medium rounded-xl hover:bg-indigo-700 transition-colors">
                    <lucide-icon [img]="userPlusIcon" [size]="16"></lucide-icon>
                    Crear Administrador
                  </button>
                </div>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class OrganizationDetailComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private alertService = inject(AlertService);
  private authService = inject(AuthService);

  backIcon = ArrowLeft;
  buildingIcon = Building2;
  mailIcon = Mail;
  phoneIcon = Phone;
  mapPinIcon = MapPin;
  userIcon = User;
  userPlusIcon = UserPlus;
  editIcon = Edit;
  trashIcon = Trash2;
  restoreIcon = RotateCcw;
  shieldIcon = Shield;
  calendarIcon = Calendar;
  hashIcon = Hash;
  fileTextIcon = FileText;
  saveIcon = Save;
  loaderIcon = Loader2;
  closeIcon = X;

  orgId = signal<string>('');
  org = signal<Organization | null>(null);
  admin = signal<UserModel | null>(null);
  allOrgUsers = signal<UserModel[]>([]);
  isLoading = signal(true);
  isLoadingAdmin = signal(false);
  isSubmitting = signal(false);
  showCreateForm = signal(false);

  formErrors: Record<string, string> = {};

  newAdmin = {
    firstName: '', lastName: '', documentType: 'DNI' as 'DNI' | 'CNE',
    documentNumber: '', email: '', phone: ''
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/super-admin/organizations']);
      return;
    }
    this.orgId.set(id);
    this.loadOrganization();
    this.loadAdmin();
  }

  private loadOrganization(): void {
    this.isLoading.set(true);
    this.http.get<ApiResponse<Organization>>(`${environment.apiUrl}/organizations/${this.orgId()}`).subscribe({
      next: res => {
        this.org.set(res.data);
        this.isLoading.set(false);
      },
      error: () => {
        this.alertService.error('Error', 'No se pudo cargar la organización');
        this.router.navigate(['/super-admin/organizations']);
      }
    });
  }

  private loadAdmin(): void {
    this.isLoadingAdmin.set(true);
    this.http.get<ApiResponse<UserModel[]>>(`${environment.apiUrl}/users/organization/${this.orgId()}?includeInactive=true`).subscribe({
      next: res => {
        const users = res.data || [];
        this.allOrgUsers.set(users);
        const adminUser = users.find(u => u.role === 'ADMIN');
        this.admin.set(adminUser || null);
        this.isLoadingAdmin.set(false);
      },
      error: () => {
        this.isLoadingAdmin.set(false);
      }
    });
  }

  onNameInput(event: Event, field: 'firstName' | 'lastName'): void {
    const input = event.target as HTMLInputElement;
    input.value = sanitizeName(input.value);
    (this.newAdmin as any)[field] = input.value;
  }

  onDocumentInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = sanitizeDocument(input.value, this.newAdmin.documentType);
    this.newAdmin.documentNumber = input.value;
  }

  onPhoneInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = sanitizePhone(input.value);
    this.newAdmin.phone = input.value;
  }

  onDocTypeChange(): void {
    this.newAdmin.documentNumber = '';
  }

  private validateForm(): boolean {
    this.formErrors = {};
    this.newAdmin.firstName = sanitizeName(this.newAdmin.firstName);
    this.newAdmin.lastName = sanitizeName(this.newAdmin.lastName);
    if (this.newAdmin.documentNumber) this.newAdmin.documentNumber = sanitizeDocument(this.newAdmin.documentNumber, this.newAdmin.documentType);
    if (this.newAdmin.phone) this.newAdmin.phone = sanitizePhone(this.newAdmin.phone);

    if (!this.newAdmin.lastName.trim()) this.formErrors['lastName'] = 'Los apellidos son obligatorios';
    if (!this.newAdmin.firstName.trim()) this.formErrors['firstName'] = 'Los nombres son obligatorios';
    if (!this.newAdmin.documentNumber.trim()) {
      this.formErrors['documentNumber'] = 'El N° de documento es obligatorio';
    } else if (this.newAdmin.documentType === 'DNI' && this.newAdmin.documentNumber.length !== 8) {
      this.formErrors['documentNumber'] = 'El DNI debe tener 8 dígitos';
    }
    if (!this.newAdmin.email.trim()) {
      this.formErrors['email'] = 'El correo es obligatorio';
    } else {
      const emailErr = sharedValidateEmail(this.newAdmin.email);
      if (emailErr) this.formErrors['email'] = emailErr;
    }
    if (this.newAdmin.phone) {
      const phoneErr = sharedValidatePhone(this.newAdmin.phone);
      if (phoneErr) this.formErrors['phone'] = phoneErr;
    }

    return Object.keys(this.formErrors).length === 0;
  }

  createAdmin(): void {
    if (!this.validateForm()) return;

    this.isSubmitting.set(true);
    const body = {
      organizationId: this.orgId(),
      firstName: this.newAdmin.firstName.trim(),
      lastName: this.newAdmin.lastName.trim(),
      documentType: this.newAdmin.documentType,
      documentNumber: this.newAdmin.documentNumber.trim(),
      email: this.newAdmin.email.trim(),
      phone: this.newAdmin.phone?.trim() || undefined,
      address: this.org()?.address || 'Dirección de Organización',
      role: 'ADMIN'
    };

    this.http.post<ApiResponse<UserModel>>(`${environment.apiUrl}/users`, body).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.alertService.success('Administrador creado', 'El administrador ha sido registrado exitosamente');
        this.showCreateForm.set(false);
        this.resetForm();
        this.loadAdmin();
      },
      error: err => {
        this.isSubmitting.set(false);
        const msg = err.error?.message || err.error?.errors?.[0]?.message || 'No se pudo crear el administrador';
        this.alertService.error('Error', msg);
      }
    });
  }

  async deleteAdmin(): Promise<void> {
    const a = this.admin();
    if (!a) return;
    const result = await this.alertService.confirmDelete(`${a.lastName}, ${a.firstName}`);
    if (!result.isConfirmed) return;

    this.isSubmitting.set(true);
    this.http.delete<ApiResponse<UserModel>>(`${environment.apiUrl}/users/${a.id}`).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.alertService.success('Eliminado', 'El administrador ha sido desactivado');
        this.loadAdmin();
      },
      error: () => {
        this.isSubmitting.set(false);
        this.alertService.error('Error', 'No se pudo eliminar el administrador');
      }
    });
  }

  async restoreAdmin(): Promise<void> {
    const a = this.admin();
    if (!a) return;
    const result = await this.alertService.confirmRestore(`${a.lastName}, ${a.firstName}`);
    if (!result.isConfirmed) return;

    this.isSubmitting.set(true);
    this.http.patch<ApiResponse<UserModel>>(`${environment.apiUrl}/users/${a.id}/restore`, {}).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.alertService.success('Restaurado', 'El administrador ha sido restaurado');
        this.loadAdmin();
      },
      error: () => {
        this.isSubmitting.set(false);
        this.alertService.error('Error', 'No se pudo restaurar el administrador');
      }
    });
  }

  cancelCreate(): void {
    this.showCreateForm.set(false);
    this.resetForm();
  }

  private resetForm(): void {
    this.newAdmin = { firstName: '', lastName: '', documentType: 'DNI', documentNumber: '', email: '', phone: '' };
    this.formErrors = {};
  }
}
