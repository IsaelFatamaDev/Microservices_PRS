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
    <div>
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Organizaciones</h1>
          <p class="text-gray-500">Gestión de JASS registradas</p>
        </div>
        <a routerLink="/super-admin/organizations/new"
           class="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          <lucide-icon [img]="plusIcon" [size]="20"></lucide-icon>
          Nueva Organización
        </a>
      </div>

      <div class="bg-white rounded-xl shadow-sm">
        <div class="p-4 border-b border-gray-200">
          <div class="flex flex-col sm:flex-row gap-4">
            <div class="relative flex-1">
              <lucide-icon [img]="searchIcon" [size]="20" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
              <input
                type="text"
                [(ngModel)]="searchTerm"
                (input)="onSearch()"
                placeholder="Buscar organización..."
                class="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <select
              [(ngModel)]="statusFilter"
              (change)="loadOrganizations()"
              class="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              <option value="A">Activos</option>
              <option value="I">Inactivos</option>
              <option value="">Todos</option>
            </select>
          </div>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Organización</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">RUC</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ubicación</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              @if (isLoading()) {
                <tr>
                  <td colspan="5" class="px-6 py-12 text-center text-gray-500">
                    <div class="flex items-center justify-center gap-2">
                      <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                      Cargando...
                    </div>
                  </td>
                </tr>
              } @else if (organizations().length === 0) {
                <tr>
                  <td colspan="5" class="px-6 py-12 text-center">
                    <lucide-icon [img]="buildingIcon" [size]="48" class="text-gray-300 mx-auto mb-3"></lucide-icon>
                    <p class="text-gray-500">No se encontraron organizaciones</p>
                  </td>
                </tr>
              } @else {
                @for (org of organizations(); track org.id) {
                  <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                          @if (org.logoUrl || org.logo) {
                            <img [src]="org.logoUrl || org.logo" [alt]="org.organizationName || org.name" class="w-full h-full object-cover rounded-lg">
                          } @else {
                            <lucide-icon [img]="buildingIcon" [size]="20" class="text-blue-600"></lucide-icon>
                          }
                        </div>
                        <div>
                          <p class="font-medium text-gray-800">{{ org.organizationName || org.name }}</p>
                          <p class="text-sm text-gray-500">{{ org.email }}</p>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-gray-600">{{ org.ruc || '-' }}</td>
                    <td class="px-6 py-4 text-gray-600">{{ org.district }}, {{ org.province }}</td>
                    <td class="px-6 py-4">
                      <span
                        class="inline-flex px-2.5 py-1 text-xs font-medium rounded-full"
                        [class.bg-green-100]="org.recordStatus === 'A' || org.recordStatus === 'ACTIVE'"
                        [class.text-green-700]="org.recordStatus === 'A' || org.recordStatus === 'ACTIVE'"
                        [class.bg-red-100]="org.recordStatus === 'I' || org.recordStatus === 'INACTIVE'"
                        [class.text-red-700]="org.recordStatus === 'I' || org.recordStatus === 'INACTIVE'">
                        {{ org.recordStatus === 'A' || org.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-2">
                        <a [routerLink]="['/super-admin/organizations', org.id]"
                           class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                          <lucide-icon [img]="editIcon" [size]="18"></lucide-icon>
                        </a>
                        @if (org.recordStatus === 'A' || org.recordStatus === 'ACTIVE') {
                          <button
                            (click)="deleteOrganization(org)"
                            class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                            <lucide-icon [img]="trashIcon" [size]="18"></lucide-icon>
                          </button>
                        } @else {
                          <button
                            (click)="restoreOrganization(org)"
                            class="p-2 text-green-600 hover:bg-green-50 rounded-lg transition-colors">
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
          <div class="p-4 border-t border-gray-200 flex items-center justify-between">
            <p class="text-sm text-gray-500">
              Mostrando {{ (currentPage() - 1) * pageSize() + 1 }} - {{ Math.min(currentPage() * pageSize(), totalElements()) }} de {{ totalElements() }}
            </p>
            <div class="flex gap-2">
              <button
                (click)="goToPage(currentPage() - 1)"
                [disabled]="currentPage() === 1"
                class="px-3 py-1.5 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50">
                Anterior
              </button>
              <button
                (click)="goToPage(currentPage() + 1)"
                [disabled]="currentPage() >= totalPages()"
                class="px-3 py-1.5 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50">
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
                    this.organizations.set(res.data.content);
                    this.totalElements.set(res.data.totalElements);
                    this.totalPages.set(res.data.totalPages);
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
