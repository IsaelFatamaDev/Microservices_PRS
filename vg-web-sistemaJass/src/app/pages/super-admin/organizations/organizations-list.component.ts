import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Plus, Search, Edit, Trash2, RotateCcw, Building2 } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { Organization, ApiResponse, RecordStatus } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-organizations-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-slate-800 tracking-tight">Organizaciones</h1>
          <p class="text-slate-500">Gestión de JASS registradas</p>
        </div>
        <a routerLink="/super-admin/organizations/new"
           class="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white font-medium rounded-xl hover:bg-blue-700 transition-colors shadow-sm hover:shadow-md">
          <lucide-icon [img]="plusIcon" [size]="20"></lucide-icon>
          Nueva Organización
        </a>
      </div>

      <div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div class="p-4 border-b border-slate-200 bg-slate-50/50">
          <div class="flex flex-col sm:flex-row gap-4">
            <div class="relative flex-1">
              <lucide-icon [img]="searchIcon" [size]="20" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"></lucide-icon>
              <input
                type="text"
                [(ngModel)]="searchTerm"
                (input)="onSearch()"
                placeholder="Buscar organización por nombre, RUC..."
                class="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all placeholder:text-slate-400 text-slate-700">
            </div>
            <select
              [(ngModel)]="statusFilter"
              (change)="loadOrganizations()"
              class="px-4 py-2.5 bg-white border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent text-slate-700">
              <option value="ACTIVE">Activos</option>
              <option value="INACTIVE">Inactivos</option>
              <option value="">Todos</option>
            </select>
          </div>
        </div>

        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead class="bg-slate-50 border-b border-slate-200">
              <tr>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider w-16">N°</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Organización</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Ubicación</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider text-center">Teléfono</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Estado</th>
                <th class="px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              @if (isLoading()) {
                <tr>
                  <td colspan="6" class="px-6 py-12 text-center text-slate-500">
                    <div class="flex items-center justify-center gap-3">
                      <div class="w-6 h-6 border-2 border-slate-300 border-t-blue-600 rounded-full animate-spin"></div>
                      <span class="font-medium">Cargando datos...</span>
                    </div>
                  </td>
                </tr>
              } @else if (organizations().length === 0) {
                <tr>
                  <td colspan="6" class="px-6 py-16 text-center">
                    <div class="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4">
                      <lucide-icon [img]="buildingIcon" [size]="32" class="text-slate-300"></lucide-icon>
                    </div>
                    <h3 class="text-slate-800 font-medium mb-1">No se encontraron organizaciones</h3>
                    <p class="text-slate-500 text-sm">Intenta ajustar los filtros de búsqueda</p>
                  </td>
                </tr>
              } @else {
                @for (org of organizations(); track org.id; let i = $index) {
                  <tr class="hover:bg-slate-50/80 transition-colors">
                    <td class="px-6 py-4 text-slate-500 text-sm font-medium">{{ i + 1 }}</td>
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-4">
                        <div class="w-10 h-10 bg-white border border-slate-200 rounded-lg flex items-center justify-center overflow-hidden shrink-0">
                          @if (org.logoUrl || org.logo) {
                            <img [src]="org.logoUrl || org.logo" [alt]="org.organizationName || org.name" class="w-full h-full object-cover">
                          } @else {
                            <lucide-icon [img]="buildingIcon" [size]="20" class="text-slate-400"></lucide-icon>
                          }
                        </div>
                        <div>
                          <p class="font-semibold text-slate-700 leading-tight">{{ org.organizationName || org.name }}</p>
                          <p class="text-sm text-slate-500">{{ org.email }}</p>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="flex flex-col">
                        <span class="text-slate-700 text-sm font-medium">{{ org.district }}</span>
                        <span class="text-slate-500 text-xs">{{ org.province }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <span class="text-slate-600 font-medium text-sm">{{ org.phone || '-' }}</span>
                    </td>
                    <td class="px-6 py-4">
                      <span
                        class="inline-flex items-center px-2.5 py-0.5 text-xs font-medium rounded-full border shrink-0"
                        [class.bg-emerald-50]="org.recordStatus === 'ACTIVE'"
                        [class.text-emerald-700]="org.recordStatus === 'ACTIVE'"
                        [class.border-emerald-100]="org.recordStatus === 'ACTIVE'"
                        [class.bg-slate-100]="org.recordStatus === 'INACTIVE'"
                        [class.text-slate-600]="org.recordStatus === 'INACTIVE'"
                        [class.border-slate-200]="org.recordStatus === 'INACTIVE'">
                        <span class="w-1.5 h-1.5 rounded-full mr-1.5"
                              [class.bg-emerald-500]="org.recordStatus === 'ACTIVE'"
                              [class.bg-slate-400]="org.recordStatus === 'INACTIVE'"></span>
                        {{ org.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-1">
                        <a [routerLink]="['/super-admin/organizations', org.id]"
                           class="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all"
                           title="Editar">
                          <lucide-icon [img]="editIcon" [size]="18"></lucide-icon>
                        </a>
                        @if (org.recordStatus === 'ACTIVE') {
                          <button
                            (click)="deleteOrganization(org)"
                            class="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all"
                            title="Desactivar">
                            <lucide-icon [img]="trashIcon" [size]="18"></lucide-icon>
                          </button>
                        } @else {
                          <button
                            (click)="restoreOrganization(org)"
                            class="p-2 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-xl transition-all"
                            title="Restaurar">
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

        <div class="md:hidden divide-y divide-slate-100">
          @if (isLoading()) {
            <div class="p-8 text-center">
              <div class="w-6 h-6 border-2 border-slate-300 border-t-blue-600 rounded-full animate-spin mx-auto mb-3"></div>
              <span class="text-slate-500 font-medium">Cargando...</span>
            </div>
          } @else if (organizations().length === 0) {
            <div class="p-12 text-center">
              <lucide-icon [img]="buildingIcon" [size]="32" class="text-slate-300 mx-auto mb-4"></lucide-icon>
              <h3 class="text-slate-800 font-medium mb-1">No hay organizaciones</h3>
            </div>
          } @else {
            @for (org of organizations(); track org.id; let i = $index) {
              <div class="p-4 space-y-4">
                <div class="flex items-center justify-between gap-4">
                  <div class="flex items-center gap-3">
                    <span class="text-slate-400 font-bold text-lg">#{{ i + 1 }}</span>
                    <div class="w-12 h-12 bg-white border border-slate-200 rounded-xl flex items-center justify-center overflow-hidden">
                      @if (org.logoUrl || org.logo) {
                        <img [src]="org.logoUrl || org.logo" [alt]="org.organizationName || org.name" class="w-full h-full object-cover">
                      } @else {
                        <lucide-icon [img]="buildingIcon" [size]="24" class="text-slate-400"></lucide-icon>
                      }
                    </div>
                    <div>
                      <h4 class="font-bold text-slate-800 leading-tight">{{ org.organizationName || org.name }}</h4>
                      <p class="text-xs text-slate-500">{{ org.email }}</p>
                    </div>
                  </div>
                  <span
                    class="inline-flex items-center px-2 py-0.5 text-[10px] font-bold rounded-full border uppercase tracking-wider"
                    [class.bg-emerald-50]="org.recordStatus === 'ACTIVE'"
                    [class.text-emerald-700]="org.recordStatus === 'ACTIVE'"
                    [class.border-emerald-200]="org.recordStatus === 'ACTIVE'"
                    [class.bg-slate-50]="org.recordStatus === 'INACTIVE'"
                    [class.text-slate-500]="org.recordStatus === 'INACTIVE'"
                    [class.border-slate-200]="org.recordStatus === 'INACTIVE'">
                    {{ org.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                  </span>
                </div>

                <div class="grid grid-cols-2 gap-4 bg-slate-50 rounded-xl p-3">
                  <div>
                    <p class="text-[10px] text-slate-400 font-bold uppercase tracking-widest mb-1">Ubicación</p>
                    <p class="text-sm text-slate-700 font-medium">{{ org.district }}</p>
                    <p class="text-xs text-slate-500">{{ org.province }}</p>
                  </div>
                  <div>
                    <p class="text-[10px] text-slate-400 font-bold uppercase tracking-widest mb-1">Teléfono</p>
                    <p class="text-sm text-slate-700 font-medium">{{ org.phone || '-' }}</p>
                  </div>
                </div>

                <div class="flex items-center justify-end gap-2 pt-2">
                  <a [routerLink]="['/super-admin/organizations', org.id]"
                     class="flex-1 flex items-center justify-center gap-2 px-3 py-2 bg-slate-100 text-slate-700 rounded-xl font-bold text-sm hover:bg-slate-200 transition-colors">
                    <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                    Editar
                  </a>
                  @if (org.recordStatus === 'ACTIVE') {
                    <button
                      (click)="deleteOrganization(org)"
                      class="flex-1 flex items-center justify-center gap-2 px-3 py-2 bg-red-50 text-red-600 rounded-xl font-bold text-sm hover:bg-red-100 transition-colors">
                      <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                      Eliminar
                    </button>
                  } @else {
                    <button
                      (click)="restoreOrganization(org)"
                      class="flex-1 flex items-center justify-center gap-2 px-3 py-2 bg-emerald-50 text-emerald-600 rounded-xl font-bold text-sm hover:bg-emerald-100 transition-colors">
                      <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                      Restaurar
                    </button>
                  }
                </div>
              </div>
            }
          }
        </div>
      </div>
    </div>
  `
})
export class OrganizationsListComponent implements OnInit {
  private http = inject(HttpClient);
  private alertService = inject(AlertService);
  private authService = inject(AuthService);

  organizations = signal<Organization[]>([]);
  isLoading = signal(false);
  searchTerm = '';
  statusFilter: RecordStatus | '' = 'ACTIVE' as RecordStatus;

  totalElements = signal(0);

  Math = Math;

  plusIcon = Plus;
  searchIcon = Search;
  editIcon = Edit;
  trashIcon = Trash2;
  restoreIcon = RotateCcw;
  buildingIcon = Building2;

  ngOnInit(): void {
    this.loadOrganizations();
  }

  loadOrganizations(): void {
    this.isLoading.set(true);
    let url = `${environment.apiUrl}/organizations`;

    if (this.statusFilter === 'INACTIVE' || !this.statusFilter) {
      url = `${environment.apiUrl}/organizations/all`;
    }

    this.http.get<ApiResponse<Organization[]>>(url).subscribe({
      next: res => {
        let data = res.data || [];

        if (this.statusFilter) {
          data = data.filter(org => {
            const status = org.recordStatus;
            if (this.statusFilter === 'ACTIVE') return status === 'ACTIVE';
            if (this.statusFilter === 'INACTIVE') return status === 'INACTIVE';
            return true;
          });
        }

        if (this.searchTerm) {
          const term = this.searchTerm.toLowerCase();
          data = data.filter(org =>
            (org.organizationName?.toLowerCase().includes(term)) ||
            (org.name?.toLowerCase().includes(term)) ||
            (org.district?.toLowerCase().includes(term)) ||
            (org.email?.toLowerCase().includes(term))
          );
        }

        // Sort by createdAt descending
        data.sort((a, b) => {
          const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
          const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
          return dateB - dateA;
        });

        this.organizations.set(data);
        this.totalElements.set(data.length);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  onSearch(): void {
    this.statusFilter = ''; // Reset status when searching globally or keep? Better keep.
    this.loadOrganizations();
  }

  async deleteOrganization(org: Organization): Promise<void> {
    const orgName = org.organizationName || org.name || 'esta organización';
    const result = await this.alertService.confirmDelete(orgName);
    if (result.isConfirmed) {
      const userId = this.authService.userId();
      const options = {
        headers: { 'X-User-Id': userId || '' },
        body: { reason: 'Eliminación desde panel administrativo' }
      };

      this.http.delete(`${environment.apiUrl}/organizations/${org.id}`, options).subscribe({
        next: () => {
          this.alertService.success('Eliminado', `${orgName} ha sido eliminado`);
          this.loadOrganizations();
        },
        error: (err) => {
          console.error('Error delete:', err);
          this.alertService.error('Error', 'No se pudo eliminar la organización');
        }
      });
    }
  }

  async restoreOrganization(org: Organization): Promise<void> {
    const orgName = org.organizationName || org.name || 'esta organización';
    const result = await this.alertService.confirmRestore(orgName);
    if (result.isConfirmed) {
      const userId = this.authService.userId();
      const headers = { 'X-User-Id': userId || '' };

      this.http.patch(`${environment.apiUrl}/organizations/${org.id}/restore`, {}, { headers }).subscribe({
        next: () => {
          this.alertService.success('Restaurado', `${orgName} ha sido restaurado`);
          this.loadOrganizations();
        },
        error: (err) => {
          console.error('Error restore:', err);
          this.alertService.error('Error', 'No se pudo restaurar la organización');
        }
      });
    }
  }
}
