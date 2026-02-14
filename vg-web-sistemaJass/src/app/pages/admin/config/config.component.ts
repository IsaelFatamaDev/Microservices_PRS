import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
     LucideAngularModule, Building2, MapPin, DollarSign, Settings,
     Upload, X, Save, Loader2, Mail, Phone, Map as MapIcon
} from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { Organization, ApiResponse } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';
import { UbigeoService } from '../../../core/services/ubigeo.service';

import { ZonesComponent } from './zones/zones.component';
import { FaresComponent } from './fares/fares.component';
import { ParametersComponent } from './parameters/parameters.component';

type ConfigTab = 'organization' | 'zones' | 'fares' | 'parameters';

interface TabItem {
     key: ConfigTab;
     label: string;
     icon: any;
     color: string;
     bgActive: string;
     textActive: string;
}

@Component({
     selector: 'app-config',
     standalone: true,
     imports: [
          CommonModule, FormsModule, LucideAngularModule,
          ZonesComponent, FaresComponent, ParametersComponent
     ],
     template: `
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Configuración</h1>
        <p class="text-sm text-gray-500 mt-0.5">Administra todos los ajustes de la organización</p>
      </div>

      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-1.5">
        <div class="flex gap-1 overflow-x-auto">
          @for (tab of tabs; track tab.key) {
            <button
              (click)="activeTab.set(tab.key)"
              class="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-medium transition-all whitespace-nowrap"
              [class]="activeTab() === tab.key
                ? tab.bgActive + ' ' + tab.textActive + ' shadow-sm'
                : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'">
              <lucide-icon [img]="tab.icon" [size]="18"></lucide-icon>
              <span>{{ tab.label }}</span>
            </button>
          }
        </div>
      </div>

      <div>
        @switch (activeTab()) {
          @case ('organization') {
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
              @if (orgLoading()) {
                <div class="p-16 text-center">
                  <div class="inline-flex items-center gap-2.5 text-gray-500">
                    <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                    <span class="text-sm">Cargando datos de la organización...</span>
                  </div>
                </div>
              } @else {
                <div class="border-b border-gray-100 bg-linear-to-r from-gray-50 to-white">
                  <div class="flex items-center gap-3 px-6 py-4">
                    <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
                      <lucide-icon [img]="buildingIcon" [size]="20" class="text-blue-600"></lucide-icon>
                    </div>
                    <div>
                      <h2 class="text-lg font-bold text-gray-900">Datos de la Organización</h2>
                      <p class="text-xs text-gray-400">Información general, logo y ubicación</p>
                    </div>
                  </div>
                </div>

                <div class="p-6">
                  <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
                    <div class="lg:col-span-4 flex flex-col items-center">
                      <div class="w-full aspect-square max-w-[240px] relative rounded-2xl border-2 border-dashed border-gray-200 bg-gray-50 flex flex-col items-center justify-center cursor-pointer hover:bg-gray-100 hover:border-blue-400 transition-all group overflow-hidden"
                           (click)="logoInput.click()">
                        <input #logoInput type="file" (change)="onLogoSelected($event)" accept="image/*" class="hidden">

                        @if (orgForm.logoUrl) {
                          <img [src]="orgForm.logoUrl" alt="Logo" class="w-full h-full object-contain p-4">
                          <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center backdrop-blur-sm">
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

                    <div class="lg:col-span-8 space-y-5">
                      <div>
                        <label class="block text-sm font-semibold text-gray-800 mb-1.5">Nombre de la Organización <span class="text-red-500">*</span></label>
                        <div class="relative">
                          <div class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                            <lucide-icon [img]="buildingIcon" [size]="18"></lucide-icon>
                          </div>
                          <input
                            type="text"
                            [(ngModel)]="orgForm.organizationName"
                            class="w-full pl-11 pr-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm font-medium placeholder:text-gray-300 transition-all"
                            placeholder="Ej. JASS San Pedro del Valle">
                        </div>
                      </div>

                      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label class="block text-sm font-semibold text-gray-800 mb-1.5">Correo Electrónico</label>
                          <div class="relative">
                            <div class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                              <lucide-icon [img]="mailIcon" [size]="18"></lucide-icon>
                            </div>
                            <input
                              type="email"
                              [(ngModel)]="orgForm.email"
                              class="w-full pl-11 pr-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm placeholder:text-gray-300 transition-all"
                              placeholder="ejemplo&#64;jass.com">
                          </div>
                        </div>
                        <div>
                          <label class="block text-sm font-semibold text-gray-800 mb-1.5">Teléfono</label>
                          <div class="relative">
                            <div class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                              <lucide-icon [img]="phoneIcon" [size]="18"></lucide-icon>
                            </div>
                            <input
                              type="text"
                              [(ngModel)]="orgForm.phone"
                              maxlength="9"
                              (input)="onOrgPhoneInput($event)"
                              class="w-full pl-11 pr-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm placeholder:text-gray-300 transition-all font-mono"
                              placeholder="987654321">
                          </div>
                        </div>
                      </div>

                      <div>
                        <label class="block text-sm font-semibold text-gray-800 mb-1.5">Dirección</label>
                        <div class="relative">
                          <div class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                            <lucide-icon [img]="mapPinIcon" [size]="18"></lucide-icon>
                          </div>
                          <input
                            type="text"
                            [(ngModel)]="orgForm.address"
                            class="w-full pl-11 pr-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm placeholder:text-gray-300 transition-all"
                            placeholder="Av. Principal S/N, Sector 2">
                        </div>
                      </div>

                      <div class="bg-gray-50 rounded-xl p-4 space-y-3 border border-gray-100">
                        <div class="flex items-center gap-2 mb-1">
                          <lucide-icon [img]="mapIcon" [size]="16" class="text-gray-500"></lucide-icon>
                          <span class="text-sm font-semibold text-gray-700">Ubicación Geográfica</span>
                        </div>
                        <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                          <select
                            [(ngModel)]="orgForm.department"
                            (ngModelChange)="onDepartmentChange()"
                            [style.color]="orgForm.department ? '#111827' : '#9ca3af'"
                            class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                            <option value="" style="color: #9ca3af">Departamento</option>
                            @for (dep of departments(); track dep) {
                              <option [value]="dep" style="color: #111827">{{ dep }}</option>
                            }
                          </select>
                          <select
                            [(ngModel)]="orgForm.province"
                            (ngModelChange)="onProvinceChange()"
                            [disabled]="!orgForm.department"
                            [style.color]="orgForm.province ? '#111827' : '#9ca3af'"
                            class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all disabled:bg-gray-100 disabled:text-gray-400">
                            <option value="" style="color: #9ca3af">Provincia</option>
                            @for (prov of provinces(); track prov) {
                              <option [value]="prov" style="color: #111827">{{ prov }}</option>
                            }
                          </select>
                          <select
                            [(ngModel)]="orgForm.district"
                            [disabled]="!orgForm.province"
                            [style.color]="orgForm.district ? '#111827' : '#9ca3af'"
                            class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all disabled:bg-gray-100 disabled:text-gray-400">
                            <option value="" style="color: #9ca3af">Distrito</option>
                            @for (dist of districts(); track dist) {
                              <option [value]="dist" style="color: #111827">{{ dist }}</option>
                            }
                          </select>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="flex justify-end mt-6 pt-5 border-t border-gray-100">
                    <button
                      (click)="saveOrganization()"
                      [disabled]="orgSaving() || !orgForm.organizationName"
                      class="inline-flex items-center gap-2 px-6 py-2.5 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium shadow-sm">
                      @if (orgSaving()) {
                        <lucide-icon [img]="loaderIcon" [size]="18" class="animate-spin"></lucide-icon>
                        <span>Guardando...</span>
                      } @else {
                        <lucide-icon [img]="saveIcon" [size]="18"></lucide-icon>
                        <span>Guardar Cambios</span>
                      }
                    </button>
                  </div>
                </div>
              }
            </div>
          }
          @case ('zones') {
            <app-zones></app-zones>
          }
          @case ('fares') {
            <app-fares></app-fares>
          }
          @case ('parameters') {
            <app-parameters></app-parameters>
          }
        }
      </div>
    </div>
  `
})
export class ConfigComponent implements OnInit {
     private http = inject(HttpClient);
     private alertService = inject(AlertService);
     private authService = inject(AuthService);
     private ubigeoService = inject(UbigeoService);

     activeTab = signal<ConfigTab>('organization');
     orgLoading = signal(false);
     orgSaving = signal(false);

     departments = signal<string[]>([]);
     provinces = signal<string[]>([]);
     districts = signal<string[]>([]);

     orgForm = {
          organizationName: '',
          email: '',
          phone: '',
          address: '',
          department: '',
          province: '',
          district: '',
          logoUrl: ''
     };

     // Icons
     buildingIcon = Building2;
     mapPinIcon = MapPin;
     mapIcon = MapIcon;
     uploadIcon = Upload;
     closeIcon = X;
     saveIcon = Save;
     loaderIcon = Loader2;
     mailIcon = Mail;
     phoneIcon = Phone;

     tabs: TabItem[] = [
          { key: 'organization', label: 'Organización', icon: Building2, color: 'blue', bgActive: 'bg-blue-50', textActive: 'text-blue-700' },
          { key: 'zones', label: 'Zonas y Calles', icon: MapPin, color: 'indigo', bgActive: 'bg-indigo-50', textActive: 'text-indigo-700' },
          { key: 'fares', label: 'Tarifas', icon: DollarSign, color: 'emerald', bgActive: 'bg-emerald-50', textActive: 'text-emerald-700' },
          { key: 'parameters', label: 'Parámetros', icon: Settings, color: 'amber', bgActive: 'bg-amber-50', textActive: 'text-amber-700' }
     ];

     private get headers() {
          return { 'X-User-Id': this.authService.userId() || '', 'Content-Type': 'application/json' };
     }

     ngOnInit(): void {
          this.loadOrganization();
          this.ubigeoService.ensureDataLoaded().subscribe(() => {
               this.departments.set(this.ubigeoService.getDepartments());
          });
     }

     loadOrganization(): void {
          const orgId = this.authService.organizationId();
          if (!orgId) return;
          this.orgLoading.set(true);
          this.http.get<ApiResponse<Organization>>(`${environment.apiUrl}/organizations/${orgId}`).subscribe({
               next: res => {
                    const org = res.data;
                    this.orgForm = {
                         organizationName: org.organizationName || org.name || '',
                         email: org.email || '',
                         phone: org.phone || '',
                         address: org.address || '',
                         department: org.department || '',
                         province: org.province || '',
                         district: org.district || '',
                         logoUrl: org.logoUrl || org.logo_url || org.logo || ''
                    };
                    // Populate ubigeo dropdowns
                    if (org.department) {
                         this.provinces.set(this.ubigeoService.getProvinces(org.department));
                         if (org.province) {
                              this.districts.set(this.ubigeoService.getDistricts(org.department, org.province));
                         }
                    }
                    this.orgLoading.set(false);
               },
               error: () => {
                    this.orgLoading.set(false);
                    this.alertService.error('Error', 'No se pudo cargar la organización');
               }
          });
     }

     onDepartmentChange(): void {
          this.orgForm.province = '';
          this.orgForm.district = '';
          this.districts.set([]);
          if (this.orgForm.department) {
               this.provinces.set(this.ubigeoService.getProvinces(this.orgForm.department));
          } else {
               this.provinces.set([]);
          }
     }

     onProvinceChange(): void {
          this.orgForm.district = '';
          if (this.orgForm.department && this.orgForm.province) {
               this.districts.set(this.ubigeoService.getDistricts(this.orgForm.department, this.orgForm.province));
          } else {
               this.districts.set([]);
          }
     }

     onLogoSelected(event: Event): void {
          const input = event.target as HTMLInputElement;
          if (input.files && input.files[0]) {
               const file = input.files[0];
               if (file.size > 5 * 1024 * 1024) {
                    this.alertService.warning('Archivo muy grande', 'El logo no debe superar los 5MB');
                    return;
               }
               const reader = new FileReader();
               reader.onload = (e) => {
                    this.orgForm.logoUrl = e.target?.result as string;
               };
               reader.readAsDataURL(file);
          }
     }

     removeLogo(event: Event): void {
          event.stopPropagation();
          this.orgForm.logoUrl = '';
     }

     onOrgPhoneInput(event: Event): void {
          const input = event.target as HTMLInputElement;
          input.value = input.value.replace(/[^0-9]/g, '').slice(0, 9);
          this.orgForm.phone = input.value;
     }

     saveOrganization(): void {
          if (!this.orgForm.organizationName.trim()) {
               this.alertService.warning('Campo requerido', 'El nombre de la organización es obligatorio');
               return;
          }
          const orgId = this.authService.organizationId();
          if (!orgId) return;

          this.orgSaving.set(true);
          this.http.put<ApiResponse<Organization>>(
               `${environment.apiUrl}/organizations/${orgId}`,
               {
                    organizationName: this.orgForm.organizationName.trim(),
                    email: this.orgForm.email.trim() || undefined,
                    phone: this.orgForm.phone.trim() || undefined,
                    address: this.orgForm.address.trim() || undefined,
                    department: this.orgForm.department || undefined,
                    province: this.orgForm.province || undefined,
                    district: this.orgForm.district || undefined,
                    logo: this.orgForm.logoUrl || undefined
               },
               { headers: this.headers }
          ).subscribe({
               next: (res) => {
                    this.orgSaving.set(false);
                    this.alertService.success('Guardado', 'Datos de la organización actualizados');
                    // Update sidebar org name in real-time
                    if (res.data) {
                         this.authService.setOrganization(res.data);
                    }
               },
               error: () => {
                    this.orgSaving.set(false);
                    this.alertService.error('Error', 'No se pudo guardar los cambios');
               }
          });
     }
}
