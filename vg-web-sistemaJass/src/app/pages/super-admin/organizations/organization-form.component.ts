import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, ArrowLeft, Save, Loader2, Upload, X, ArrowRight, Check, Building2, Mail, Phone, MapPin, Hash, User, FileText } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { Organization, ApiResponse } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';
import { UbigeoService } from '../../../core/services/ubigeo.service';
import { CustomValidators } from '../../../core/validators/custom-validators';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-organization-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, LucideAngularModule],
  template: `
    <div class="max-w-6xl mx-auto px-4 py-8">
      <div class="flex items-center gap-4 mb-8">
        <a routerLink="/super-admin/organizations" class="p-2.5 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 hover:border-gray-300 transition-all shadow-sm group">
          <lucide-icon [img]="backIcon" [size]="20" class="text-gray-500 group-hover:text-gray-700"></lucide-icon>
        </a>
        <div>
          <h1 class="text-2xl font-bold text-gray-900 tracking-tight">{{ isEditMode() ? 'Editar Organización' : 'Nueva Organización' }}</h1>
          <p class="text-sm text-gray-500 font-medium mt-1">{{ isEditMode() ? 'Actualice los datos de la JASS' : 'Registre una nueva JASS en el sistema' }}</p>
        </div>
      </div>

      @if (!isEditMode()) {
        <div class="mb-10 max-w-3xl mx-auto">
          <div class="relative">
            <div class="absolute left-0 top-1/2 -translate-y-1/2 w-full h-1 bg-gray-100 rounded-full -z-10"></div>
            <div class="absolute left-0 top-1/2 -translate-y-1/2 h-1 bg-blue-600 rounded-full transition-all duration-500 -z-10" [style.width]="currentStep() === 1 ? '50%' : '100%'"></div>
            
            <div class="flex justify-between w-full">
              <div class="flex flex-col items-center gap-3 cursor-pointer" (click)="currentStep() === 2 ? prevStep() : null">
                <div class="w-12 h-12 rounded-full flex items-center justify-center text-lg font-bold transition-all duration-300 shadow-md border-4"
                     [ngClass]="currentStep() >= 1 ? 'bg-blue-600 border-blue-50 text-white shadow-blue-200' : 'bg-white border-gray-100 text-gray-400'">
                  1
                </div>
                <span class="text-sm font-semibold tracking-wide" [ngClass]="currentStep() >= 1 ? 'text-blue-700' : 'text-gray-400'">Datos JASS</span>
              </div>

              <div class="flex flex-col items-center gap-3">
                <div class="w-12 h-12 rounded-full flex items-center justify-center text-lg font-bold transition-all duration-300 shadow-md border-4"
                     [ngClass]="currentStep() >= 2 ? 'bg-blue-600 border-blue-50 text-white shadow-blue-200' : 'bg-white border-gray-100 text-gray-400'">
                  2
                </div>
                <span class="text-sm font-semibold tracking-wide" [ngClass]="currentStep() >= 2 ? 'text-blue-700' : 'text-gray-400'">Administrador</span>
              </div>
            </div>
          </div>
        </div>
      }

      <div class="bg-white rounded-2xl shadow-xl shadow-gray-100/50 border border-gray-100 overflow-hidden">
        <div [class.hidden]="currentStep() !== 1">
          <form [formGroup]="orgForm" class="p-8 md:p-10">
            <div class="flex items-center gap-3 mb-8 pb-4 border-b border-gray-100">
              <div class="p-2 bg-blue-50 rounded-lg text-blue-600">
                <lucide-icon [img]="buildingIcon" [size]="24"></lucide-icon>
              </div>
              <h3 class="text-xl font-bold text-gray-900">Información General</h3>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-12 gap-8">
              <div class="md:col-span-4 flex flex-col items-center">
                <div class="w-full aspect-square max-w-[280px] relative rounded-2xl border-2 border-dashed border-gray-200 bg-gray-50 flex flex-col items-center justify-center cursor-pointer hover:bg-gray-100 hover:border-blue-400 transition-all group overflow-hidden"
                     (click)="fileInput.click()">
                  <input #fileInput type="file" (change)="onFileSelected($event)" accept="image/*" class="hidden">
                  
                  @if (logoPreview()) {
                    <img [src]="logoPreview()" alt="Logo preview" class="w-full h-full object-contain p-4">
                    <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2 backdrop-blur-sm">
                      <span class="text-white font-medium text-sm">Cambiar Logo</span>
                    </div>
                    <button type="button" (click)="removeLogo($event)" class="absolute top-2 right-2 p-1.5 bg-white text-red-500 rounded-full shadow-md hover:bg-red-50 z-20">
                      <lucide-icon [img]="closeIcon" [size]="16"></lucide-icon>
                    </button>
                  } @else {
                    <div class="p-4 bg-white rounded-full shadow-sm mb-3 group-hover:scale-110 transition-transform duration-300">
                      <lucide-icon [img]="uploadIcon" [size]="32" class="text-blue-500"></lucide-icon>
                    </div>
                    <p class="text-sm font-semibold text-gray-700">Subir Logotipo</p>
                    <p class="text-xs text-center text-gray-400 mt-1 px-4">Click para seleccionar<br>(Máx. 5MB)</p>
                  }
                </div>
              </div>

              <div class="md:col-span-8 grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="md:col-span-2">
                  <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Nombre de la Organización *</label>
                  <div class="relative group">
                    <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                      <lucide-icon [img]="buildingIcon" [size]="18"></lucide-icon>
                    </div>
                    <input formControlName="organizationName" type="text" 
                           class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                           placeholder="Ej. JASS San Pedro del Valle">
                  </div>
                  @if (orgForm.get('organizationName')?.touched && orgForm.get('organizationName')?.invalid) {
                    <small class="text-red-500 text-xs font-semibold mt-1.5 flex items-center gap-1">
                      <lucide-icon [img]="closeIcon" [size]="12"></lucide-icon> El nombre es requerido
                    </small>
                  }
                </div>

                <div>
                  <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Correo Electrónico *</label>
                  <div class="relative group">
                    <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                      <lucide-icon [img]="mailIcon" [size]="18"></lucide-icon>
                    </div>
                    <input formControlName="email" type="email" 
                           class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                           placeholder="ejemplo@jass.com">
                  </div>
                </div>

                <div>
                  <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Teléfono</label>
                  <div class="relative group">
                    <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                      <lucide-icon [img]="phoneIcon" [size]="18"></lucide-icon>
                    </div>
                    <input formControlName="phone" type="text" maxlength="9" (input)="onPhoneInput($event)"
                           class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                           placeholder="987654321">
                  </div>
                </div>

                <div class="md:col-span-2">
                  <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Dirección Referencial</label>
                  <div class="relative group">
                    <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                      <lucide-icon [img]="mapPinIcon" [size]="18"></lucide-icon>
                    </div>
                    <input formControlName="address" type="text" 
                           class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                           placeholder="Av. Principal S/N, Sector 2">
                  </div>
                </div>

                <div class="md:col-span-2 grid grid-cols-1 md:grid-cols-3 gap-4 p-4 bg-gray-50 rounded-xl border border-gray-100">
                  <div class="md:col-span-3 mb-1 flex items-center gap-2">
                    <lucide-icon [img]="mapPinIcon" [size]="16" class="text-gray-400"></lucide-icon>
                    <span class="text-xs font-bold text-gray-500 uppercase">Ubicación Geográfica</span>
                  </div>
                  
                  <div>
                    <select formControlName="department" (change)="onDepartmentChange()" 
                            class="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none text-gray-700 text-sm font-medium">
                      <option value="">Departamento</option>
                      @for (dep of departments(); track dep) {
                        <option [value]="dep">{{ dep }}</option>
                      }
                    </select>
                  </div>

                  <div>
                    <select formControlName="province" (change)="onProvinceChange()" 
                            class="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none text-gray-700 text-sm font-medium disabled:bg-gray-100 disabled:text-gray-400"
                            [disabled]="!orgForm.get('department')?.value">
                      <option value="">Provincia</option>
                      @for (prov of provinces(); track prov) {
                        <option [value]="prov">{{ prov }}</option>
                      }
                    </select>
                  </div>

                  <div>
                    <select formControlName="district" 
                            class="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none text-gray-700 text-sm font-medium disabled:bg-gray-100 disabled:text-gray-400"
                            [disabled]="!orgForm.get('province')?.value">
                      <option value="">Distrito</option>
                      @for (dist of districts(); track dist) {
                        <option [value]="dist">{{ dist }}</option>
                      }
                    </select>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </div>

        <div [class.hidden]="currentStep() !== 2">
          <form [formGroup]="adminForm" class="p-8 md:p-10">
            <div class="flex items-center gap-3 mb-8 pb-4 border-b border-gray-100">
              <div class="p-2 bg-blue-50 rounded-lg text-blue-600">
                <lucide-icon [img]="userIcon" [size]="24"></lucide-icon>
              </div>
              <h3 class="text-xl font-bold text-gray-900">Datos del Administrador</h3>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-4xl mx-auto">
              <div>
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Tipo Documento</label>
                <div class="relative">
                  <select formControlName="documentType" class="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none text-gray-900 font-medium appearance-none">
                    <option value="DNI">DNI (Documento Nacional de Identidad)</option>
                    <option value="CNE">carnet de Extranjería</option>
                  </select>
                  <div class="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-gray-400">
                    <lucide-icon [img]="hashIcon" [size]="16"></lucide-icon>
                  </div>
                </div>
              </div>

              <div>
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Número Documento *</label>
                <div class="relative group">
                  <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                    <lucide-icon [img]="fileTextIcon" [size]="18"></lucide-icon>
                  </div>
                  <input formControlName="documentNumber" type="text" (input)="onDocumentInput($event)"
                         class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                         [placeholder]="adminForm.get('documentType')?.value === 'DNI' ? '8 dígitos' : 'Número de documento'"
                         [maxlength]="adminForm.get('documentType')?.value === 'DNI' ? 8 : 20">
                </div>
              </div>

              <div>
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Nombres *</label>
                <div class="relative group">
                  <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                    <lucide-icon [img]="userIcon" [size]="18"></lucide-icon>
                  </div>
                  <input formControlName="firstName" type="text" 
                         class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                         placeholder="Ingresar nombres">
                </div>
              </div>

              <div>
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Apellidos *</label>
                <div class="relative group">
                  <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                    <lucide-icon [img]="userIcon" [size]="18"></lucide-icon>
                  </div>
                  <input formControlName="lastName" type="text" 
                         class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                         placeholder="Ingresar apellidos">
                </div>
              </div>

              <div class="md:col-span-2">
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Correo Personal *</label>
                <div class="relative group">
                  <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                    <lucide-icon [img]="mailIcon" [size]="18"></lucide-icon>
                  </div>
                  <input formControlName="email" type="email" 
                         class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                         placeholder="admin@personal.com">
                </div>
              </div>

              <div>
                <label class="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">Teléfono (Opcional)</label>
                <div class="relative group">
                  <div class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                    <lucide-icon [img]="phoneIcon" [size]="18"></lucide-icon>
                  </div>
                  <input formControlName="phone" type="text" maxlength="9" (input)="onPhoneInput($event)"
                         class="w-full pl-10 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 transition-all outline-none placeholder:text-gray-300 font-medium text-gray-900" 
                         placeholder="987654321">
                </div>
              </div>
            </div>
          </form>
        </div>

        <div class="px-8 py-6 bg-gray-50 border-t border-gray-100 flex items-center justify-between">
          @if (currentStep() === 2 && !isEditMode()) {
            <button type="button" (click)="prevStep()" class="text-gray-500 font-semibold hover:text-gray-800 px-4 py-2 transition-colors flex items-center gap-2">
              <lucide-icon [img]="backIcon" [size]="18"></lucide-icon> Atrás
            </button>
          } @else {
             <div></div> 
          }

          <div class="flex gap-4">
            <a routerLink="/super-admin/organizations" class="px-6 py-3 border border-gray-200 text-gray-600 rounded-xl hover:bg-white hover:text-gray-800 hover:border-gray-300 transition-all font-semibold shadow-sm">
              Cancelar
            </a>

            @if (!isEditMode() && currentStep() === 1) {
              <button type="button" (click)="nextStep()" 
                      [disabled]="orgForm.invalid"
                      class="inline-flex items-center gap-2 px-8 py-3 bg-gray-900 text-white rounded-xl hover:bg-black disabled:bg-gray-300 disabled:text-gray-500 disabled:cursor-not-allowed transition-all font-semibold shadow-lg hover:shadow-xl hover:-translate-y-0.5">
                Siguiente
                <lucide-icon [img]="nextIcon" [size]="18"></lucide-icon>
              </button>
            }

            @if (isEditMode() || currentStep() === 2) {
              <button type="button" (click)="onSubmit()"
                      [disabled]="isSubmitting() || (isEditMode() ? orgForm.invalid : adminForm.invalid)"
                      class="inline-flex items-center gap-2 px-8 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed transition-all font-semibold shadow-lg shadow-blue-200 hover:shadow-xl hover:shadow-blue-300 hover:-translate-y-0.5">
                @if (isSubmitting()) {
                  <lucide-icon [img]="loaderIcon" [size]="20" class="animate-spin"></lucide-icon>
                  Procesando...
                } @else {
                  <lucide-icon [img]="saveIcon" [size]="20"></lucide-icon>
                  {{ isEditMode() ? 'Guardar Cambios' : 'Confirmar Registro' }}
                }
              </button>
            }
          </div>
        </div>
      </div>
    </div>
  `
})
export class OrganizationFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private alertService = inject(AlertService);
  private ubigeoService = inject(UbigeoService);

  backIcon = ArrowLeft;
  uploadIcon = Upload;
  closeIcon = X;
  nextIcon = ArrowRight;
  saveIcon = Save;
  loaderIcon = Loader2;
  checkIcon = Check;
  buildingIcon = Building2;
  mailIcon = Mail;
  phoneIcon = Phone;
  mapPinIcon = MapPin;
  hashIcon = Hash;
  userIcon = User;
  fileTextIcon = FileText;
  // lockIcon removed as password field is gone

  isEditMode = signal(false);
  isSubmitting = signal(false);
  currentStep = signal(1);
  organizationId = signal<string | null>(null);
  logoPreview = signal<string | null>(null);

  departments = signal<string[]>([]);
  provinces = signal<string[]>([]);
  districts = signal<string[]>([]);

  orgForm: FormGroup = this.fb.group({
    organizationName: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.pattern(/^9\d{8}$/)]],
    address: [''],
    department: ['', Validators.required],
    province: [{ value: '', disabled: true }, Validators.required],
    district: [{ value: '', disabled: true }, Validators.required],
    logoUrl: ['']
  });

  adminForm: FormGroup = this.fb.group({
    documentType: ['DNI', Validators.required],
    documentNumber: ['', [Validators.required, CustomValidators.dniStructure()]],
    firstName: ['', [Validators.required, CustomValidators.noNumbers()]],
    lastName: ['', [Validators.required, CustomValidators.noNumbers()]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [CustomValidators.phoneStructure()]]
  });

  ngOnInit(): void {
    this.ubigeoService.ensureDataLoaded().subscribe(() => {
      this.departments.set(this.ubigeoService.getDepartments());
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.organizationId.set(id);
      this.isEditMode.set(true);
      this.loadOrganization(id);
    }

    this.adminForm.get('documentType')?.valueChanges.subscribe(type => {
      const docControl = this.adminForm.get('documentNumber');
      if (type === 'DNI') {
        docControl?.setValidators([Validators.required, CustomValidators.dniStructure()]);
      } else {
        docControl?.setValidators([Validators.required, CustomValidators.cneStructure()]);
      }
      docControl?.updateValueAndValidity();
      docControl?.setValue('');
    });
  }

  onDocumentInput(event: Event) {
    const input = event.target as HTMLInputElement;
    const type = this.adminForm.get('documentType')?.value;

    if (type === 'DNI') {
      // Allow only numbers
      input.value = input.value.replace(/[^0-9]/g, '');
      this.adminForm.get('documentNumber')?.setValue(input.value);
    }
  }

  onPhoneInput(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
    this.orgForm.get('phone')?.setValue(input.value); // Ensure this updates the correct form based on usage
  }

  onDepartmentChange() {
    const dep = this.orgForm.get('department')?.value;
    if (dep) {
      this.provinces.set(this.ubigeoService.getProvinces(dep));
      this.orgForm.get('province')?.enable();
      this.orgForm.get('province')?.setValue('');
      this.orgForm.get('district')?.disable();
      this.orgForm.get('district')?.setValue('');
    } else {
      this.provinces.set([]);
      this.orgForm.get('province')?.disable();
      this.orgForm.get('district')?.disable();
    }
  }

  onProvinceChange() {
    const dep = this.orgForm.get('department')?.value;
    const prov = this.orgForm.get('province')?.value;
    if (dep && prov) {
      this.districts.set(this.ubigeoService.getDistricts(dep, prov));
      this.orgForm.get('district')?.enable();
      this.orgForm.get('district')?.setValue('');
    } else {
      this.districts.set([]);
      this.orgForm.get('district')?.disable();
    }
  }

  nextStep() {
    if (this.orgForm.invalid) {
      this.orgForm.markAllAsTouched();
      return;
    }
    this.currentStep.set(2);
  }

  prevStep() {
    this.currentStep.set(1);
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];

      if (file.size > 5 * 1024 * 1024) {
        this.alertService.warning('Archivo muy grande', 'El logo no debe superar los 5MB');
        return;
      }

      const reader = new FileReader();
      reader.onload = (e) => {
        const base64 = e.target?.result as string;
        this.logoPreview.set(base64);
        this.orgForm.patchValue({ logoUrl: base64 });
      };
      reader.onerror = () => {
        this.alertService.error('Error', 'No se pudo leer el archivo');
      };
      reader.readAsDataURL(file);
    }
  }

  removeLogo(event: Event) {
    event.stopPropagation();
    this.logoPreview.set(null);
    this.orgForm.patchValue({ logoUrl: '' });
  }

  private loadOrganization(id: string): void {
    this.http.get<ApiResponse<Organization>>(`${environment.apiUrl}/organizations/${id}`).subscribe({
      next: res => {
        const org = res.data;

        if (org.department) {
          this.ubigeoService.ensureDataLoaded().subscribe(() => {
            this.departments.set(this.ubigeoService.getDepartments());
            this.provinces.set(this.ubigeoService.getProvinces(org.department));
            if (org.province) {
              this.districts.set(this.ubigeoService.getDistricts(org.department, org.province));
            }

            this.orgForm.patchValue({
              organizationName: org.organizationName || org.name,
              email: org.email,
              phone: org.phone,
              address: org.address,
              department: org.department,
              province: org.province,
              district: org.district,
              logoUrl: org.logoUrl || org.logo
            });
            this.orgForm.get('province')?.enable();
            this.orgForm.get('district')?.enable();

            if (org.logoUrl || org.logo) {
              this.logoPreview.set(org.logoUrl || org.logo || null);
            }
          });
        } else {
          this.orgForm.patchValue({
            organizationName: org.organizationName || org.name,
            email: org.email,
            phone: org.phone,
            address: org.address,
            logoUrl: org.logoUrl || org.logo
          });
          if (org.logoUrl || org.logo) {
            this.logoPreview.set(org.logoUrl || org.logo || null);
          }
        }
      },
      error: () => {
        this.alertService.error('Error', 'No se pudo cargar la organización');
        this.router.navigate(['/super-admin/organizations']);
      }
    });
  }

  onSubmit(): void {
    if (this.isEditMode()) {
      if (this.orgForm.invalid) {
        this.orgForm.markAllAsTouched();
        return;
      }
      this.updateOrganization();
    } else {
      if (this.adminForm.invalid) {
        this.adminForm.markAllAsTouched();
        return;
      }
      this.createFullFlow();
    }
  }

  private createFullFlow(): void {
    this.isSubmitting.set(true);
    const orgData = this.orgForm.value;

    this.http.post<ApiResponse<Organization>>(`${environment.apiUrl}/organizations`, orgData)
      .pipe(
        switchMap(res => {
          const orgId = res.data.id;
          const adminData = {
            ...this.adminForm.value,
            organizationId: orgId,
            role: 'ADMIN',
            address: orgData.address || 'Dirección de Organización',
          };
          // Create admin user
          // Password not in DTO locally, assume backend generates it or handles it
          return this.http.post(`${environment.apiUrl}/users`, adminData);
        })
      )
      .subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.alertService.success('¡Registro Exitoso!', 'La JASS y su administrador han sido creados.');
          this.router.navigate(['/super-admin/organizations']);
        },
        error: (err) => {
          this.isSubmitting.set(false);
          console.error(err);
          this.alertService.error('Atención', 'La organización se creó pero hubo un error al crear el administrador. Verifique la lista de organizaciones.');
          this.router.navigate(['/super-admin/organizations']);
        }
      });
  }

  private updateOrganization(): void {
    this.isSubmitting.set(true);
    this.http.put<ApiResponse<Organization>>(`${environment.apiUrl}/organizations/${this.organizationId()}`, this.orgForm.value)
      .subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.alertService.success('Actualizado', 'Datos guardados correctamente');
          this.router.navigate(['/super-admin/organizations']);
        },
        error: () => {
          this.isSubmitting.set(false);
          this.alertService.error('Error', 'No se pudo actualizar la organización');
        }
      });
  }
}
