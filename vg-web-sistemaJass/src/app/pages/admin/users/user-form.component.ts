import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
     LucideAngularModule, ArrowLeft, Save, Loader2, UserCog, UserCheck,
     MapPin, Phone, Mail, Droplets, FileText, Hash, ChevronRight, Printer, CheckCircle
} from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import {
     User, ApiResponse, CreateUserRequest, UpdateUserRequest, Zone, Street,
     Role, DocumentType, CreateWaterBoxRequest, AssignWaterBoxRequest, BoxType
} from '../../../core';
import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
     selector: 'app-user-form',
     standalone: true,
     imports: [CommonModule, FormsModule, RouterLink, LucideAngularModule],
     template: `
    <div class="max-w-4xl mx-auto space-y-5">
      <!-- Header -->
      <div class="flex items-center gap-4">
        <a routerLink="/admin/users" class="p-2.5 hover:bg-gray-100 rounded-xl transition-colors">
          <lucide-icon [img]="backIcon" [size]="20" class="text-gray-600"></lucide-icon>
        </a>
        <div>
          <h1 class="text-2xl font-bold text-gray-800">
            {{ isEditMode() ? 'Editar' : 'Nuevo' }} {{ selectedRole === 'OPERATOR' ? 'Operador' : 'Cliente' }}
          </h1>
          <p class="text-sm text-gray-500 mt-0.5">
            {{ isEditMode() ? 'Modifica los datos del usuario' : 'Completa la información para registrar' }}
          </p>
        </div>
      </div>

      <form (ngSubmit)="onSubmit()" class="space-y-5">

        <!-- Role Selection (create only) -->
        @if (!isEditMode()) {
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <h3 class="text-sm font-semibold text-gray-700 uppercase tracking-wider mb-4">Tipo de Usuario</h3>
            <div class="grid grid-cols-2 gap-4">
              <button type="button" (click)="selectedRole = 'OPERATOR'"
                class="relative p-4 rounded-xl border-2 text-left transition-all"
                [class]="selectedRole === 'OPERATOR'
                  ? 'border-purple-500 bg-purple-50/50 ring-1 ring-purple-200'
                  : 'border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50'">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-xl flex items-center justify-center"
                       [class]="selectedRole === 'OPERATOR' ? 'bg-purple-100 text-purple-600' : 'bg-gray-100 text-gray-400'">
                    <lucide-icon [img]="userCogIcon" [size]="20"></lucide-icon>
                  </div>
                  <div>
                    <p class="font-semibold text-sm" [class]="selectedRole === 'OPERATOR' ? 'text-purple-700' : 'text-gray-700'">Operador</p>
                    <p class="text-xs text-gray-400">Realiza trabajos de campo</p>
                  </div>
                </div>
                @if (selectedRole === 'OPERATOR') {
                  <div class="absolute top-2.5 right-2.5">
                    <lucide-icon [img]="checkIcon" [size]="18" class="text-purple-500"></lucide-icon>
                  </div>
                }
              </button>

              <button type="button" (click)="selectedRole = 'CLIENT'"
                class="relative p-4 rounded-xl border-2 text-left transition-all"
                [class]="selectedRole === 'CLIENT'
                  ? 'border-blue-500 bg-blue-50/50 ring-1 ring-blue-200'
                  : 'border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50'">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-xl flex items-center justify-center"
                       [class]="selectedRole === 'CLIENT' ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-400'">
                    <lucide-icon [img]="userCheckIcon" [size]="20"></lucide-icon>
                  </div>
                  <div>
                    <p class="font-semibold text-sm" [class]="selectedRole === 'CLIENT' ? 'text-blue-700' : 'text-gray-700'">Cliente</p>
                    <p class="text-xs text-gray-400">Usuario del servicio de agua</p>
                  </div>
                </div>
                @if (selectedRole === 'CLIENT') {
                  <div class="absolute top-2.5 right-2.5">
                    <lucide-icon [img]="checkIcon" [size]="18" class="text-blue-500"></lucide-icon>
                  </div>
                }
              </button>
            </div>
          </div>
        }

        <!-- Personal Data -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <h3 class="text-sm font-semibold text-gray-700 uppercase tracking-wider mb-4 flex items-center gap-2">
            <lucide-icon [img]="fileIcon" [size]="16" class="text-gray-400"></lucide-icon>
            Datos Personales
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Apellidos <span class="text-red-500">*</span></label>
              <input type="text" [ngModel]="form.lastName" name="lastName" required
                (ngModelChange)="sanitizeName('lastName', $event)"
                class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all"
                [ngClass]="fieldErrors()['lastName'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                placeholder="Pérez García">
              @if (fieldErrors()['lastName']) {
                <p class="mt-1 text-xs text-red-500">{{ fieldErrors()['lastName'] }}</p>
              }
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Nombres <span class="text-red-500">*</span></label>
              <input type="text" [ngModel]="form.firstName" name="firstName" required
                (ngModelChange)="sanitizeName('firstName', $event)"
                class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all"
                [ngClass]="fieldErrors()['firstName'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                placeholder="Juan Carlos">
              @if (fieldErrors()['firstName']) {
                <p class="mt-1 text-xs text-red-500">{{ fieldErrors()['firstName'] }}</p>
              }
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Tipo de Documento <span class="text-red-500">*</span></label>
              <select [(ngModel)]="form.documentType" name="documentType" required
                (ngModelChange)="onDocumentTypeChange()"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                <option value="DNI">DNI</option>
                <option value="CNE">CNE</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">N° Documento <span class="text-red-500">*</span></label>
              <input type="text" [ngModel]="form.documentNumber" name="documentNumber" required
                (ngModelChange)="sanitizeDocument($event)" (blur)="checkDuplicate('documentNumber')"
                [maxlength]="form.documentType === 'DNI' ? 8 : 20"
                class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all font-mono"
                [ngClass]="fieldErrors()['documentNumber'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                [placeholder]="form.documentType === 'DNI' ? '12345678' : 'A1B2C3D4E5F6G7H8I9J0'">
              @if (fieldErrors()['documentNumber']) {
                <p class="mt-1 text-xs text-red-500">{{ fieldErrors()['documentNumber'] }}</p>
              }
            </div>
          </div>
        </div>

        <!-- Contact Info -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <h3 class="text-sm font-semibold text-gray-700 uppercase tracking-wider mb-4 flex items-center gap-2">
            <lucide-icon [img]="phoneIcon" [size]="16" class="text-gray-400"></lucide-icon>
            Datos de Contacto
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">
                Teléfono
                @if (selectedRole === 'CLIENT') {
                  <span class="text-gray-400 text-xs font-normal">(opcional)</span>
                } @else {
                  <span class="text-red-500">*</span>
                }
              </label>
              <input type="text" [ngModel]="form.phone" name="phone"
                [required]="selectedRole === 'OPERATOR'"
                (ngModelChange)="sanitizePhone($event)" (blur)="checkDuplicate('phone')"
                maxlength="9"
                class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all font-mono"
                [ngClass]="fieldErrors()['phone'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                placeholder="987654321">
              @if (fieldErrors()['phone']) {
                <p class="mt-1 text-xs text-red-500">{{ fieldErrors()['phone'] }}</p>
              }
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">
                Email
                @if (selectedRole === 'CLIENT') {
                  <span class="text-gray-400 text-xs font-normal">(opcional)</span>
                } @else {
                  <span class="text-red-500">*</span>
                }
              </label>
              <input type="email" [(ngModel)]="form.email" name="email"
                [required]="selectedRole === 'OPERATOR'"
                (blur)="validateEmail()"
                class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all"
                [ngClass]="fieldErrors()['email'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                placeholder="usuario&#64;email.com">
              @if (fieldErrors()['email']) {
                <p class="mt-1 text-xs text-red-500">{{ fieldErrors()['email'] }}</p>
              }
            </div>
          </div>

          <!-- WhatsApp toggle -->
          @if (!isEditMode()) {
            <div class="mt-4 pt-4 border-t border-gray-100">
              <label class="flex items-center gap-3 cursor-pointer group">
                <div class="relative">
                  <input type="checkbox" [(ngModel)]="hasWhatsApp" name="hasWhatsApp" class="sr-only peer">
                  <div class="w-11 h-6 rounded-full transition-colors peer-checked:bg-green-500 bg-gray-300"></div>
                  <div class="absolute left-0.5 top-0.5 w-5 h-5 rounded-full bg-white transition-transform shadow-sm peer-checked:translate-x-5"></div>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-700 group-hover:text-gray-900">El usuario tiene WhatsApp</p>
                  <p class="text-xs text-gray-400">
                    {{ hasWhatsApp ? 'Las credenciales se enviarán por WhatsApp' : 'Se generará un reporte de credenciales para imprimir' }}
                  </p>
                </div>
              </label>
            </div>
          }
        </div>

        <!-- Location (CLIENT & OPERATOR) -->
        @if (selectedRole === 'CLIENT' || selectedRole === 'OPERATOR') {
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <h3 class="text-sm font-semibold text-gray-700 uppercase tracking-wider mb-4 flex items-center gap-2">
              <lucide-icon [img]="mapPinIcon" [size]="16" class="text-gray-400"></lucide-icon>
              Ubicación
            </h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Zona <span class="text-red-500">*</span></label>
                <select [(ngModel)]="selectedZoneId" name="zone" (ngModelChange)="onZoneChange()" required
                  [style.color]="selectedZoneId ? '#111827' : '#9ca3af'"
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                  <option value="" style="color: #9ca3af">Seleccionar zona</option>
                  @for (zone of zones(); track zone.id) {
                    <option [value]="zone.id" style="color: #111827">{{ zone.zoneName || zone.name }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Calle <span class="text-red-500">*</span></label>
                <select [(ngModel)]="selectedStreetId" name="street" [disabled]="!selectedZoneId" required
                  [style.color]="selectedStreetId ? '#111827' : '#9ca3af'"
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all disabled:bg-gray-50 disabled:text-gray-400">
                  <option value="" style="color: #9ca3af">{{ selectedZoneId ? 'Seleccionar calle' : 'Primero seleccione zona' }}</option>
                  @for (street of filteredStreets(); track street.id) {
                    <option [value]="street.id" style="color: #111827">{{ street.fullStreetName || street.streetName || street.name }}</option>
                  }
                </select>
              </div>
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Dirección <span class="text-red-500">*</span></label>
                <input type="text" [(ngModel)]="form.address" name="address" required
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all"
                  placeholder="Jr. Principal 123, Mz A Lt 5">
              </div>
            </div>
          </div>
        }

        <!-- Water Box (CLIENT + create only) -->
        @if (selectedRole === 'CLIENT' && !isEditMode()) {
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <h3 class="text-sm font-semibold text-gray-700 uppercase tracking-wider mb-4 flex items-center gap-2">
              <lucide-icon [img]="dropletsIcon" [size]="16" class="text-gray-400"></lucide-icon>
              Caja de Agua
            </h3>
            <p class="text-xs text-gray-400 mb-4">Se creará y asignará automáticamente al registrar el cliente</p>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Tipo de Caja <span class="text-red-500">*</span></label>
                <select [(ngModel)]="selectedBoxType" name="boxType" required
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                  <option value="RESIDENTIAL">Residencial</option>
                  <option value="COMMERCIAL">Comercial</option>
                  <option value="COMMUNAL">Comunal</option>
                  <option value="INSTITUTIONAL">Institucional</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">N° Suministro</label>
                <div class="w-full px-4 py-2.5 border border-gray-100 rounded-xl text-sm bg-gray-50 text-gray-500 font-mono">
                  {{ generatedBoxCode() || 'Se generará automáticamente' }}
                </div>
              </div>
            </div>
            <div class="mt-4 p-3 bg-blue-50 rounded-xl border border-blue-100">
              <div class="flex items-start gap-2">
                <lucide-icon [img]="chevronIcon" [size]="14" class="text-blue-500 mt-0.5 shrink-0"></lucide-icon>
                <div class="text-xs text-blue-600 space-y-1">
                  <p><strong>Zona/Calle/Dirección:</strong> se copiarán de la ubicación del cliente</p>
                  <p><strong>Fecha de instalación:</strong> fecha actual de registro</p>
                </div>
              </div>
            </div>
          </div>
        }

        <!-- Steps indicator (create only) -->
        @if (!isEditMode() && selectedRole === 'CLIENT') {
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <h3 class="text-sm font-semibold text-gray-700 uppercase tracking-wider mb-3">Proceso de Registro</h3>
            <div class="flex items-center gap-2 text-xs text-gray-500">
              <div class="flex items-center gap-1.5">
                <div class="w-6 h-6 rounded-full flex items-center justify-center text-white text-xs font-bold"
                     [class]="currentStep() >= 1 ? 'bg-blue-600' : 'bg-gray-300'">1</div>
                <span>Crear usuario</span>
              </div>
              <lucide-icon [img]="chevronIcon" [size]="14" class="text-gray-300"></lucide-icon>
              <div class="flex items-center gap-1.5">
                <div class="w-6 h-6 rounded-full flex items-center justify-center text-white text-xs font-bold"
                     [class]="currentStep() >= 2 ? 'bg-blue-600' : 'bg-gray-300'">2</div>
                <span>Crear caja de agua</span>
              </div>
              <lucide-icon [img]="chevronIcon" [size]="14" class="text-gray-300"></lucide-icon>
              <div class="flex items-center gap-1.5">
                <div class="w-6 h-6 rounded-full flex items-center justify-center text-white text-xs font-bold"
                     [class]="currentStep() >= 3 ? 'bg-blue-600' : 'bg-gray-300'">3</div>
                <span>Asignar caja</span>
              </div>
            </div>
          </div>
        }

        <!-- Submit -->
        <div class="flex items-center justify-end gap-3">
          <a routerLink="/admin/users"
             class="px-6 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 text-sm font-medium transition-all">
            Cancelar
          </a>
          <button type="submit" [disabled]="isSubmitting()"
            class="inline-flex items-center gap-2 px-6 py-2.5 text-white rounded-xl text-sm font-medium shadow-sm transition-all disabled:opacity-50"
            [class]="selectedRole === 'OPERATOR' ? 'bg-purple-600 hover:bg-purple-700' : 'bg-blue-600 hover:bg-blue-700'">
            @if (isSubmitting()) {
              <lucide-icon [img]="loaderIcon" [size]="18" class="animate-spin"></lucide-icon>
              <span>{{ stepMessage() }}</span>
            } @else {
              <lucide-icon [img]="saveIcon" [size]="18"></lucide-icon>
              <span>{{ isEditMode() ? 'Actualizar' : 'Registrar' }}</span>
            }
          </button>
        </div>
      </form>

      <!-- Credential Report Modal -->
      @if (showCredentialReport()) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
          <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden" id="credentialReport">
            <div class="p-6 text-center border-b border-gray-100"
                 [class]="selectedRole === 'OPERATOR' ? 'bg-purple-50' : 'bg-blue-50'">
              <div class="w-14 h-14 rounded-2xl mx-auto mb-3 flex items-center justify-center"
                   [class]="selectedRole === 'OPERATOR' ? 'bg-purple-100' : 'bg-blue-100'">
                <lucide-icon [img]="checkIcon" [size]="28"
                  [class]="selectedRole === 'OPERATOR' ? 'text-purple-600' : 'text-blue-600'"></lucide-icon>
              </div>
              <h3 class="text-lg font-bold text-gray-800">Usuario Registrado</h3>
              <p class="text-sm text-gray-500 mt-1">Reporte de credenciales para entregar al usuario</p>
            </div>
            <div class="p-6 space-y-3">
              <div class="bg-gray-50 rounded-xl p-4 space-y-2.5 text-sm">
                <div class="flex justify-between">
                  <span class="text-gray-500">Nombre:</span>
                  <span class="font-semibold text-gray-800">{{ createdUserFullName() }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-gray-500">{{ form.documentType }}:</span>
                  <span class="font-semibold text-gray-800 font-mono">{{ form.documentNumber }}</span>
                </div>
                @if (form.phone) {
                  <div class="flex justify-between">
                    <span class="text-gray-500">Teléfono:</span>
                    <span class="font-semibold text-gray-800 font-mono">{{ form.phone }}</span>
                  </div>
                }
                <div class="border-t border-gray-200 pt-2.5">
                  <p class="text-xs text-amber-600 font-medium">
                    La contraseña inicial es el número de documento. El usuario debe cambiarla en su primer inicio de sesión.
                  </p>
                </div>
              </div>
            </div>
            <div class="p-4 bg-gray-50 border-t border-gray-100 flex gap-3">
              <button (click)="printReport()" type="button"
                class="flex-1 inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-gray-800 text-white rounded-xl text-sm font-medium hover:bg-gray-900 transition-all">
                <lucide-icon [img]="printerIcon" [size]="16"></lucide-icon>
                Imprimir
              </button>
              <button (click)="closeAndNavigate()" type="button"
                class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl text-sm font-medium hover:bg-gray-100 transition-all">
                Cerrar
              </button>
            </div>
          </div>
        </div>
      }
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
     currentStep = signal(0);
     stepMessage = signal('Procesando...');
     showCredentialReport = signal(false);

     zones = signal<Zone[]>([]);
     allStreets = signal<Street[]>([]);
     filteredStreets = signal<Street[]>([]);
     generatedBoxCode = signal('');

     selectedRole: Role = 'CLIENT';
     selectedZoneId = '';
     selectedStreetId = '';
     selectedBoxType: BoxType = 'RESIDENTIAL';
     hasWhatsApp = true;
     fieldErrors = signal<Record<string, string>>({});
     private allOrgUsers = signal<User[]>([]);

     form: {
          firstName: string; lastName: string; documentType: DocumentType;
          documentNumber: string; email: string; phone: string; address: string;
     } = {
               firstName: '', lastName: '', documentType: 'DNI',
               documentNumber: '', email: '', phone: '', address: ''
          };

     backIcon = ArrowLeft; saveIcon = Save; loaderIcon = Loader2;
     userCogIcon = UserCog; userCheckIcon = UserCheck;
     mapPinIcon = MapPin; phoneIcon = Phone; mailIcon = Mail;
     dropletsIcon = Droplets; fileIcon = FileText; hashIcon = Hash;
     chevronIcon = ChevronRight; printerIcon = Printer; checkIcon = CheckCircle;

     createdUserFullName = computed(() => `${this.form.lastName}, ${this.form.firstName}`);

     private get headers() {
          return { 'X-User-Id': this.authService.userId() || '', 'Content-Type': 'application/json' };
     }

     private get orgId(): string {
          return this.authService.organizationId() || '';
     }

     ngOnInit(): void {
          const id = this.route.snapshot.paramMap.get('id');
          const roleParam = this.route.snapshot.queryParamMap.get('role');

          if (roleParam === 'OPERATOR' || roleParam === 'CLIENT') {
               this.selectedRole = roleParam;
          }

          if (id) {
               this.userId.set(id);
               this.isEditMode.set(true);
               this.loadUser(id);
          }

          this.loadZones();
          this.loadStreets();
          this.generateBoxCode();
          this.loadAllUsers();
     }

     onZoneChange(): void {
          this.selectedStreetId = '';
          if (this.selectedZoneId) {
               this.filteredStreets.set(
                    this.allStreets().filter(s => s.zoneId === this.selectedZoneId && s.recordStatus === 'ACTIVE')
               );
          } else {
               this.filteredStreets.set([]);
          }
     }

     onSubmit(): void {
          const currentErrors = this.fieldErrors();
          if (Object.keys(currentErrors).length > 0) {
               this.alertService.warning('Datos inválidos', Object.values(currentErrors)[0]);
               return;
          }
          if (!this.form.firstName || !this.form.lastName || !this.form.documentNumber) {
               this.alertService.warning('Campos requeridos', 'Complete apellidos, nombres y N° de documento');
               return;
          }
          if (this.form.documentType === 'DNI' && this.form.documentNumber.length !== 8) {
               this.alertService.warning('Documento inválido', 'El DNI debe tener exactamente 8 dígitos numéricos');
               return;
          }
          if (this.form.phone && this.form.phone.length > 0 && !this.form.phone.startsWith('9')) {
               this.alertService.warning('Teléfono inválido', 'El número debe comenzar con 9');
               return;
          }
          if (this.form.phone && this.form.phone.length > 0 && this.form.phone.length !== 9) {
               this.alertService.warning('Teléfono inválido', 'El número debe tener 9 dígitos');
               return;
          }
          if (this.form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
               this.alertService.warning('Email inválido', 'Ingrese un correo electrónico válido');
               return;
          }
          if (this.selectedRole === 'OPERATOR' && (!this.form.email || !this.form.phone)) {
               this.alertService.warning('Campos requeridos', 'Para operadores el email y teléfono son obligatorios');
               return;
          }
          if (!this.isEditMode() && (!this.selectedZoneId || !this.selectedStreetId)) {
               this.alertService.warning('Campos requeridos', 'Seleccione zona y calle');
               return;
          }

          this.isSubmitting.set(true);
          if (this.isEditMode()) {
               this.updateUser();
          } else {
               this.createUserFlow();
          }
     }

     printReport(): void {
          const el = document.getElementById('credentialReport');
          if (!el) return;

          const printWindow = window.open('', '_blank', 'width=400,height=500');
          if (!printWindow) return;

          printWindow.document.write(`
      <html><head><title>Credenciales</title>
      <style>
        body { font-family: Arial, sans-serif; padding: 24px; max-width: 360px; margin: 0 auto; }
        h2 { text-align: center; font-size: 16px; margin-bottom: 4px; }
        p.sub { text-align: center; font-size: 12px; color: #666; margin-bottom: 16px; }
        .row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #eee; font-size: 13px; }
        .label { color: #666; }
        .value { font-weight: bold; }
        .note { margin-top: 16px; padding: 10px; background: #fff8e1; border-radius: 8px; font-size: 11px; color: #a66d00; }
      </style></head><body>
        <h2>${this.authService.organization()?.organizationName || 'JASS'}</h2>
        <p class="sub">Reporte de Credenciales</p>
        <div class="row"><span class="label">Nombre:</span><span class="value">${this.form.lastName}, ${this.form.firstName}</span></div>
        <div class="row"><span class="label">${this.form.documentType}:</span><span class="value">${this.form.documentNumber}</span></div>
        ${this.form.phone ? `<div class="row"><span class="label">Teléfono:</span><span class="value">${this.form.phone}</span></div>` : ''}
        <div class="note">La contraseña inicial es su número de documento. Cámbiela en su primer inicio de sesión.</div>
      </body></html>
    `);
          printWindow.document.close();
          printWindow.print();
     }

     closeAndNavigate(): void {
          this.showCredentialReport.set(false);
          this.router.navigate(['/admin/users']);
     }

     // --- Validation methods ---

     sanitizeName(field: 'firstName' | 'lastName', value: string): void {
          this.form[field] = value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]/g, '');
     }

     sanitizeDocument(value: string): void {
          if (this.form.documentType === 'DNI') {
               this.form.documentNumber = value.replace(/\D/g, '').slice(0, 8);
          } else {
               this.form.documentNumber = value.replace(/[^a-zA-Z0-9]/g, '').slice(0, 20);
          }
          this.clearFieldError('documentNumber');
     }

     onDocumentTypeChange(): void {
          this.form.documentNumber = '';
          this.clearFieldError('documentNumber');
     }

     sanitizePhone(value: string): void {
          this.form.phone = value.replace(/\D/g, '').slice(0, 9);
          if (this.form.phone.length > 0 && !this.form.phone.startsWith('9')) {
               this.setFieldError('phone', 'El número debe comenzar con 9');
          } else {
               this.clearFieldError('phone');
          }
     }

     validateEmail(): void {
          if (!this.form.email) {
               this.clearFieldError('email');
               return;
          }
          const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
          if (!emailRegex.test(this.form.email)) {
               this.setFieldError('email', 'Ingrese un correo electrónico válido');
          } else {
               this.clearFieldError('email');
               this.checkDuplicate('email');
          }
     }

     checkDuplicate(field: 'documentNumber' | 'email' | 'phone'): void {
          const value = field === 'documentNumber' ? this.form.documentNumber
               : field === 'email' ? this.form.email : this.form.phone;
          if (!value) return;

          const editId = this.userId();
          const duplicate = this.allOrgUsers().find(u => {
               if (editId && u.id === editId) return false;
               if (field === 'documentNumber') return (u.documentNumber || u.dni) === value;
               if (field === 'email') return u.email === value;
               if (field === 'phone') return u.phone === value;
               return false;
          });

          if (duplicate) {
               const labels: Record<string, string> = { documentNumber: 'N° documento', email: 'Email', phone: 'Teléfono' };
               this.setFieldError(field, `${labels[field]} ya registrado: ${duplicate.lastName}, ${duplicate.firstName}`);
          } else {
               this.clearFieldError(field);
          }
     }

     private setFieldError(field: string, msg: string): void {
          this.fieldErrors.update(e => ({ ...e, [field]: msg }));
     }

     private clearFieldError(field: string): void {
          this.fieldErrors.update(e => {
               const copy = { ...e };
               delete copy[field];
               return copy;
          });
     }

     private loadAllUsers(): void {
          this.http.get<ApiResponse<User[]>>(
               `${environment.apiUrl}/users/organization/${this.orgId}`,
               { params: { includeInactive: 'true' }, headers: this.headers }
          ).subscribe({
               next: res => this.allOrgUsers.set(res.data || [])
          });
     }

     // --- Private methods ---

     private loadUser(id: string): void {
          this.http.get<ApiResponse<User>>(`${environment.apiUrl}/users/${id}`, { headers: this.headers }).subscribe({
               next: res => {
                    const u = res.data;
                    this.selectedRole = u.role;
                    this.form = {
                         firstName: u.firstName, lastName: u.lastName,
                         documentType: u.documentType || 'DNI',
                         documentNumber: u.documentNumber || u.dni || '',
                         email: u.email || '', phone: u.phone || '',
                         address: u.address || ''
                    };
                    this.selectedZoneId = u.zoneId || '';
                    const savedStreetId = u.streetId || '';
                    this.onZoneChange();
                    this.selectedStreetId = savedStreetId;
               },
               error: () => {
                    this.alertService.error('Error', 'No se pudo cargar el usuario');
                    this.router.navigate(['/admin/users']);
               }
          });
     }

     private loadZones(): void {
          this.http.get<ApiResponse<Zone[]>>(
               `${environment.apiUrl}/zones/organization/${this.orgId}`,
               { headers: this.headers }
          ).subscribe({
               next: res => this.zones.set((res.data || []).filter(z => z.recordStatus === 'ACTIVE'))
          });
     }

     private loadStreets(): void {
          this.http.get<ApiResponse<Street[]>>(
               `${environment.apiUrl}/streets`,
               { headers: this.headers }
          ).subscribe({
               next: res => {
                    this.allStreets.set((res.data || []).filter(s => s.recordStatus === 'ACTIVE'));
                    if (this.selectedZoneId) this.onZoneChange();
               }
          });
     }

     private generateBoxCode(): void {
          this.http.get<ApiResponse<any[]>>(
               `${environment.apiUrl}/water-boxes`,
               { headers: this.headers }
          ).subscribe({
               next: res => {
                    const count = (res.data || []).length;
                    this.generatedBoxCode.set(`SUP-${String(count + 1).padStart(5, '0')}`);
               },
               error: () => this.generatedBoxCode.set(`SUP-00001`)
          });
     }

     private createUserFlow(): void {
          if (this.selectedRole === 'CLIENT') {
               this.createClientWithWaterBox();
          } else {
               this.createOperator();
          }
     }

     private createOperator(): void {
          const payload: CreateUserRequest = {
               organizationId: this.orgId,
               firstName: this.form.firstName,
               lastName: this.form.lastName,
               documentType: this.form.documentType,
               documentNumber: this.form.documentNumber,
               email: this.form.email || undefined,
               phone: this.form.phone || undefined,
               address: this.form.address || undefined,
               zoneId: this.selectedZoneId || undefined,
               streetId: this.selectedStreetId || undefined,
               role: 'OPERATOR'
          };

          this.stepMessage.set('Creando operador...');
          this.http.post<ApiResponse<User>>(`${environment.apiUrl}/users`, payload, { headers: this.headers }).subscribe({
               next: () => {
                    this.isSubmitting.set(false);
                    if (!this.hasWhatsApp) {
                         this.showCredentialReport.set(true);
                    } else {
                         this.alertService.success('Registrado', 'Operador registrado correctamente');
                         this.router.navigate(['/admin/users']);
                    }
               },
               error: (err) => {
                    this.isSubmitting.set(false);
                    const msg = err.error?.message || 'No se pudo crear el operador';
                    this.alertService.error('Error', msg);
               }
          });
     }

     private createClientWithWaterBox(): void {
          // Step 1: Create user
          this.currentStep.set(1);
          this.stepMessage.set('Creando usuario...');

          const userPayload: CreateUserRequest = {
               organizationId: this.orgId,
               firstName: this.form.firstName,
               lastName: this.form.lastName,
               documentType: this.form.documentType,
               documentNumber: this.form.documentNumber,
               email: this.form.email || undefined,
               phone: this.form.phone || undefined,
               address: this.form.address || undefined,
               zoneId: this.selectedZoneId,
               streetId: this.selectedStreetId,
               role: 'CLIENT'
          };

          this.http.post<ApiResponse<User>>(`${environment.apiUrl}/users`, userPayload, { headers: this.headers }).subscribe({
               next: (userRes) => {
                    const newUser = userRes.data;
                    const newUserId = newUser?.id;
                    if (!newUserId) {
                         this.isSubmitting.set(false);
                         this.alertService.error('Error', 'No se obtuvo el ID del usuario creado');
                         return;
                    }

                    // Step 2: Create water box
                    this.currentStep.set(2);
                    this.stepMessage.set('Creando caja de agua...');

                    const boxPayload: CreateWaterBoxRequest = {
                         organizationId: this.orgId,
                         boxCode: this.generatedBoxCode() || `SUP-${Date.now()}`,
                         boxType: this.selectedBoxType,
                         installationDate: new Date().toISOString().split('.')[0],
                         zoneId: this.selectedZoneId,
                         streetId: this.selectedStreetId,
                         address: this.form.address
                    };

                    this.http.post<ApiResponse<any>>(`${environment.apiUrl}/water-boxes`, boxPayload, { headers: this.headers }).subscribe({
                         next: (boxRes) => {
                              const newBox = boxRes.data;
                              const waterBoxId = newBox?.id;
                              if (!waterBoxId) {
                                   this.isSubmitting.set(false);
                                   this.alertService.warning('Parcial', 'Usuario creado pero no se pudo crear la caja de agua');
                                   this.navigateOrReport();
                                   return;
                              }

                              // Step 3: Assign water box to user
                              this.currentStep.set(3);
                              this.stepMessage.set('Asignando caja de agua...');

                              const assignPayload: AssignWaterBoxRequest = {
                                   waterBoxId: waterBoxId,
                                   userId: newUserId
                              };

                              this.http.post<ApiResponse<any>>(
                                   `${environment.apiUrl}/water-box-assignments`, assignPayload, { headers: this.headers }
                              ).subscribe({
                                   next: () => {
                                        this.isSubmitting.set(false);
                                        this.navigateOrReport();
                                   },
                                   error: () => {
                                        this.isSubmitting.set(false);
                                        this.alertService.warning('Parcial', 'Usuario y caja creados, pero la asignación falló');
                                        this.navigateOrReport();
                                   }
                              });
                         },
                         error: () => {
                              this.isSubmitting.set(false);
                              this.alertService.warning('Parcial', 'Usuario creado pero no se pudo crear la caja de agua');
                              this.navigateOrReport();
                         }
                    });
               },
               error: (err) => {
                    this.isSubmitting.set(false);
                    this.currentStep.set(0);
                    const msg = err.error?.message || 'No se pudo crear el usuario';
                    this.alertService.error('Error', msg);
               }
          });
     }

     private navigateOrReport(): void {
          if (!this.hasWhatsApp) {
               this.showCredentialReport.set(true);
          } else {
               this.alertService.success('Registrado', 'Cliente registrado correctamente con caja de agua');
               this.router.navigate(['/admin/users']);
          }
     }

     private updateUser(): void {
          const payload: UpdateUserRequest = {
               firstName: this.form.firstName,
               lastName: this.form.lastName,
               email: this.form.email || undefined,
               phone: this.form.phone || undefined,
               address: this.form.address || undefined
          };

          this.stepMessage.set('Actualizando...');
          this.http.put<ApiResponse<User>>(
               `${environment.apiUrl}/users/${this.userId()}`,
               payload,
               { headers: this.headers }
          ).subscribe({
               next: () => {
                    this.isSubmitting.set(false);
                    this.alertService.success('Actualizado', 'Usuario actualizado correctamente');
                    this.router.navigate(['/admin/users']);
               },
               error: (err) => {
                    this.isSubmitting.set(false);
                    const msg = err.error?.message || 'No se pudo actualizar el usuario';
                    this.alertService.error('Error', msg);
               }
          });
     }
}
