import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LucideAngularModule, Plus, Search, Edit, Trash2, RotateCcw, Users, Filter } from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { User, ApiResponse, PageResponse, RecordStatus, Role } from '../../../core';
import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
     selector: 'app-users-list',
     standalone: true,
     imports: [CommonModule, RouterLink, FormsModule, LucideAngularModule],
     template: `
    <div>
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Usuarios</h1>
          <p class="text-gray-500">Gestión de usuarios de la organización</p>
        </div>
        <a routerLink="/admin/users/new"
           class="inline-flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          <lucide-icon [img]="plusIcon" [size]="20"></lucide-icon>
          Nuevo Usuario
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
                placeholder="Buscar por nombre, DNI o email..."
                class="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <select
              [(ngModel)]="roleFilter"
              (change)="loadUsers()"
              class="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              <option value="">Todos los roles</option>
              <option value="OPERATOR">Operadores</option>
              <option value="CLIENT">Clientes</option>
            </select>
            <select
              [(ngModel)]="statusFilter"
              (change)="loadUsers()"
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
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Usuario</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">DNI</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Teléfono</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rol</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              @if (isLoading()) {
                <tr>
                  <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                    <div class="flex items-center justify-center gap-2">
                      <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                      Cargando...
                    </div>
                  </td>
                </tr>
              } @else if (users().length === 0) {
                <tr>
                  <td colspan="6" class="px-6 py-12 text-center">
                    <lucide-icon [img]="usersIcon" [size]="48" class="text-gray-300 mx-auto mb-3"></lucide-icon>
                    <p class="text-gray-500">No se encontraron usuarios</p>
                  </td>
                </tr>
              } @else {
                @for (user of users(); track user.id) {
                  <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-semibold text-sm">
                          {{ user.firstName.charAt(0) }}{{ user.lastName.charAt(0) }}
                        </div>
                        <div>
                          <p class="font-medium text-gray-800">{{ user.firstName }} {{ user.lastName }}</p>
                          <p class="text-sm text-gray-500">{{ user.email }}</p>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-gray-600">{{ user.documentNumber || user.dni }}</td>
                    <td class="px-6 py-4 text-gray-600">{{ user.phone || '-' }}</td>
                    <td class="px-6 py-4">
                      <span
                        class="inline-flex px-2.5 py-1 text-xs font-medium rounded-full"
                        [class.bg-purple-100]="user.role === 'OPERATOR'"
                        [class.text-purple-700]="user.role === 'OPERATOR'"
                        [class.bg-blue-100]="user.role === 'CLIENT'"
                        [class.text-blue-700]="user.role === 'CLIENT'">
                        {{ user.role === 'OPERATOR' ? 'Operador' : 'Cliente' }}
                      </span>
                    </td>
                    <td class="px-6 py-4">
                      <span
                        class="inline-flex px-2.5 py-1 text-xs font-medium rounded-full"
                        [class.bg-green-100]="user.recordStatus === 'A' || user.recordStatus === 'ACTIVE'"
                        [class.text-green-700]="user.recordStatus === 'A' || user.recordStatus === 'ACTIVE'"
                        [class.bg-red-100]="user.recordStatus === 'I' || user.recordStatus === 'INACTIVE'"
                        [class.text-red-700]="user.recordStatus === 'I' || user.recordStatus === 'INACTIVE'">
                        {{ user.recordStatus === 'A' || user.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-2">
                        <a [routerLink]="['/admin/users', user.id]"
                           class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                          <lucide-icon [img]="editIcon" [size]="18"></lucide-icon>
                        </a>
                        @if (user.recordStatus === 'A' || user.recordStatus === 'ACTIVE') {
                          <button
                            (click)="deleteUser(user)"
                            class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                            <lucide-icon [img]="trashIcon" [size]="18"></lucide-icon>
                          </button>
                        } @else {
                          <button
                            (click)="restoreUser(user)"
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
export class UsersListComponent implements OnInit {
     private http = inject(HttpClient);
     private alertService = inject(AlertService);
     private authService = inject(AuthService);

     users = signal<User[]>([]);
     isLoading = signal(false);
     searchTerm = '';
     statusFilter: RecordStatus | '' = 'A' as RecordStatus;
     roleFilter: Role | '' = '';

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
     usersIcon = Users;
     filterIcon = Filter;

     ngOnInit(): void {
          this.loadUsers();
     }

     loadUsers(): void {
          this.isLoading.set(true);
          const orgId = this.authService.organizationId();

          const params: any = {
               page: this.currentPage() - 1,
               size: this.pageSize(),
               organizationId: orgId
          };

          if (this.statusFilter) {
               params.recordStatus = this.statusFilter;
          }

          if (this.roleFilter) {
               params.role = this.roleFilter;
          }

          if (this.searchTerm) {
               params.search = this.searchTerm;
          }

          this.http.get<ApiResponse<PageResponse<User>>>(`${environment.apiUrl}/users`, { params }).subscribe({
               next: res => {
                    this.users.set(res.data.content);
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
          this.loadUsers();
     }

     goToPage(page: number): void {
          this.currentPage.set(page);
          this.loadUsers();
     }

     async deleteUser(user: User): Promise<void> {
          const result = await this.alertService.confirmDelete(`${user.firstName} ${user.lastName}`);
          if (result.isConfirmed) {
               this.http.delete(`${environment.apiUrl}/users/${user.id}`).subscribe({
                    next: () => {
                         this.alertService.success('Eliminado', 'Usuario eliminado correctamente');
                         this.loadUsers();
                    }
               });
          }
     }

     async restoreUser(user: User): Promise<void> {
          const result = await this.alertService.confirmRestore(`${user.firstName} ${user.lastName}`);
          if (result.isConfirmed) {
               this.http.patch(`${environment.apiUrl}/users/${user.id}/restore`, {}).subscribe({
                    next: () => {
                         this.alertService.success('Restaurado', 'Usuario restaurado correctamente');
                         this.loadUsers();
                    }
               });
          }
     }
}
