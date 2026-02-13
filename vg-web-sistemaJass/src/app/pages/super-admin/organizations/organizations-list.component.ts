import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Plus, Search, Edit, Trash2, RotateCcw, Building2 } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { Organization, ApiResponse, PageResponse, RecordStatus } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';

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
              <option value="A">Activos</option>
              <option value="I">Inactivos</option>
              <option value="">Todos</option>
            </select>
          </div>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-slate-50 border-b border-slate-200">
              <tr>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Organización</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">RUC</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Ubicación</th>
                <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Estado</th>
                <th class="px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              @if (isLoading()) {
                <tr>
                  <td colspan="5" class="px-6 py-12 text-center text-slate-500">
                    <div class="flex items-center justify-center gap-3">
                      <div class="w-6 h-6 border-2 border-slate-300 border-t-blue-600 rounded-full animate-spin"></div>
                      <span class="font-medium">Cargando datos...</span>
                    </div>
                  </td>
                </tr>
              } @else if (organizations().length === 0) {
                <tr>
                  <td colspan="5" class="px-6 py-16 text-center">
                    <div class="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4">
                      <lucide-icon [img]="buildingIcon" [size]="32" class="text-slate-300"></lucide-icon>
                    </div>
                    <h3 class="text-slate-800 font-medium mb-1">No se encontraron organizaciones</h3>
                    <p class="text-slate-500 text-sm">Intenta ajustar los filtros de búsqueda</p>
                  </td>
                </tr>
              } @else {
                @for (org of organizations(); track org.id) {
                  <tr class="hover:bg-slate-50/80 transition-colors">
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
                          <p class="font-semibold text-slate-700">{{ org.organizationName || org.name }}</p>
                          <p class="text-sm text-slate-500">{{ org.email }}</p>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-slate-600 font-medium text-sm">{{ org.ruc || '-' }}</td>
                    <td class="px-6 py-4">
                      <div class="flex flex-col">
                        <span class="text-slate-700 text-sm font-medium">{{ org.district }}</span>
                        <span class="text-slate-500 text-xs">{{ org.province }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <span
                        class="inline-flex items-center px-2.5 py-0.5 text-xs font-medium rounded-full border"
                        [class.bg-emerald-50]="org.recordStatus === 'A' || org.recordStatus === 'ACTIVE'"
                        [class.text-emerald-700]="org.recordStatus === 'A' || org.recordStatus === 'ACTIVE'"
                        [class.border-emerald-100]="org.recordStatus === 'A' || org.recordStatus === 'ACTIVE'"
                        [class.bg-slate-100]="org.recordStatus === 'I' || org.recordStatus === 'INACTIVE'"
                        [class.text-slate-600]="org.recordStatus === 'I' || org.recordStatus === 'INACTIVE'"
                        [class.border-slate-200]="org.recordStatus === 'I' || org.recordStatus === 'INACTIVE'">
                        <span class="w-1.5 h-1.5 rounded-full mr-1.5" 
                              [class.bg-emerald-500]="org.recordStatus === 'A' || org.recordStatus === 'ACTIVE'"
                              [class.bg-slate-400]="org.recordStatus === 'I' || org.recordStatus === 'INACTIVE'"></span>
                        {{ org.recordStatus === 'A' || org.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-2">
                        <a [routerLink]="['/super-admin/organizations', org.id]"
                           class="p-2 text-slate-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all"
                           title="Editar">
                          <lucide-icon [img]="editIcon" [size]="18"></lucide-icon>
                        </a>
                        @if (org.recordStatus === 'A' || org.recordStatus === 'ACTIVE') {
                          <button
                            (click)="deleteOrganization(org)"
                            class="p-2 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                            title="Desactivar">
                            <lucide-icon [img]="trashIcon" [size]="18"></lucide-icon>
                          </button>
                        } @else {
                          <button
                            (click)="restoreOrganization(org)"
                            class="p-2 text-slate-500 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-all"
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

        @if (totalPages() > 1) {
          <div class="px-6 py-4 border-t border-slate-200 bg-slate-50/50 flex items-center justify-between">
            <p class="text-sm text-slate-500">
              Mostrando <span class="font-medium text-slate-700">{{ (currentPage() - 1) * pageSize() + 1 }}</span> - <span class="font-medium text-slate-700">{{ Math.min(currentPage() * pageSize(), totalElements()) }}</span> de <span class="font-medium text-slate-700">{{ totalElements() }}</span>
            </p>
            <div class="flex gap-2">
              <button
                (click)="goToPage(currentPage() - 1)"
                [disabled]="currentPage() === 1"
                class="px-3 py-1.5 bg-white border border-slate-300 rounded-lg text-slate-600 text-sm font-medium hover:bg-slate-50 hover:text-slate-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors shadow-sm">
                Anterior
              </button>
              <button
                (click)="goToPage(currentPage() + 1)"
                [disabled]="currentPage() >= totalPages()"
                class="px-3 py-1.5 bg-white border border-slate-300 rounded-lg text-slate-600 text-sm font-medium hover:bg-slate-50 hover:text-slate-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors shadow-sm">
                Siguiente
              </button>
            </div>
          </div>
        }
      </div>
    </div>
  `
})
export class OrganizationsListComponent implements OnInit {
  private http = inject(HttpClient);
  private alertService = inject(AlertService);

  organizations = signal<Organization[]>([]);
  isLoading = signal(false);
  searchTerm = '';
  statusFilter: RecordStatus | '' = 'A' as RecordStatus;

  currentPage = signal(1);
  pageSize = signal(10);
  totalElements = signal(0);
  totalPages = signal(0);

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
    const params: any = {
      page: this.currentPage() - 1,
      size: this.pageSize()
    };

    if (this.statusFilter) {
      params.recordStatus = this.statusFilter;
    }

    if (this.searchTerm) {
      params.search = this.searchTerm;
    }

    this.http.get<ApiResponse<PageResponse<Organization>>>(`${environment.apiUrl}/organizations`, { params }).subscribe({
      next: res => {
        this.organizations.set(res.data?.content || []);
        this.totalElements.set(res.data?.totalElements || 0);
        this.totalPages.set(res.data?.totalPages || 0);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  onSearch(): void {
    this.currentPage.set(1);
    this.loadOrganizations();
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadOrganizations();
  }

  async deleteOrganization(org: Organization): Promise<void> {
    const orgName = org.organizationName || org.name || 'esta organización';
    const result = await this.alertService.confirmDelete(orgName);
    if (result.isConfirmed) {
      this.http.delete(`${environment.apiUrl}/organizations/${org.id}`).subscribe({
        next: () => {
          this.alertService.success('Eliminado', `${orgName} ha sido eliminado`);
          this.loadOrganizations();
        }
      });
    }
  }

  async restoreOrganization(org: Organization): Promise<void> {
    const orgName = org.organizationName || org.name || 'esta organización';
    const result = await this.alertService.confirmRestore(orgName);
    if (result.isConfirmed) {
      this.http.patch(`${environment.apiUrl}/organizations/${org.id}/restore`, {}).subscribe({
        next: () => {
          this.alertService.success('Restaurado', `${orgName} ha sido restaurado`);
          this.loadOrganizations();
        }
      });
    }
  }
}
