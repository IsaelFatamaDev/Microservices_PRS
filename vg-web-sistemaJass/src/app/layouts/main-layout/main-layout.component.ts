import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Role } from '../../core/models';
import { LucideAngularModule, Home, Users, Building2, MapPin, Route, DollarSign, Package, ClipboardList, Bell, Settings, LogOut, Menu, X, ChevronDown, Droplets, FileText, BarChart3, Calendar, Truck, AlertCircle, User, CreditCard } from 'lucide-angular';

interface MenuItem {
     label: string;
     icon: any;
     route: string;
     roles: Role[];
     children?: MenuItem[];
}

@Component({
     selector: 'app-main-layout',
     standalone: true,
     imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, LucideAngularModule],
     template: `
    <div class="min-h-screen bg-gray-100 flex">
      <aside
        class="fixed inset-y-0 left-0 z-50 w-64 bg-blue-800 text-white transform transition-transform duration-300 lg:translate-x-0"
        [class.-translate-x-full]="!sidebarOpen()"
        [class.translate-x-0]="sidebarOpen()">

        <div class="h-16 flex items-center justify-between px-4 border-b border-blue-700">
          <div class="flex items-center gap-3">
            <img src="assets/Gotita.png" alt="JASS" class="w-10 h-10 object-contain">
            <div>
              <h1 class="font-bold text-lg leading-tight">JASS Digital</h1>
              <p class="text-xs text-blue-300">Sistema de Gestión</p>
            </div>
          </div>
          <button (click)="toggleSidebar()" class="lg:hidden p-1 hover:bg-blue-700 rounded">
            <lucide-icon [img]="closeIcon" [size]="20"></lucide-icon>
          </button>
        </div>

        <nav class="flex-1 overflow-y-auto py-4">
          <ul class="space-y-1 px-3">
            @for (item of filteredMenuItems(); track item.route) {
              @if (!item.children) {
                <li>
                  <a [routerLink]="item.route"
                     routerLinkActive="bg-blue-900 text-white"
                     class="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-blue-700 transition-colors">
                    <lucide-icon [img]="item.icon" [size]="20"></lucide-icon>
                    <span class="text-sm font-medium">{{ item.label }}</span>
                  </a>
                </li>
              } @else {
                <li>
                  <button
                    (click)="toggleSubmenu(item.route)"
                    class="w-full flex items-center justify-between px-3 py-2.5 rounded-lg hover:bg-blue-700 transition-colors">
                    <div class="flex items-center gap-3">
                      <lucide-icon [img]="item.icon" [size]="20"></lucide-icon>
                      <span class="text-sm font-medium">{{ item.label }}</span>
                    </div>
                    <lucide-icon
                      [img]="chevronIcon"
                      [size]="16"
                      class="transition-transform"
                      [class.rotate-180]="openSubmenus().includes(item.route)">
                    </lucide-icon>
                  </button>
                  @if (openSubmenus().includes(item.route)) {
                    <ul class="mt-1 ml-6 space-y-1">
                      @for (child of item.children; track child.route) {
                        <li>
                          <a [routerLink]="child.route"
                             routerLinkActive="bg-blue-900 text-white"
                             class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-blue-700 transition-colors text-sm">
                            <lucide-icon [img]="child.icon" [size]="16"></lucide-icon>
                            <span>{{ child.label }}</span>
                          </a>
                        </li>
                      }
                    </ul>
                  }
                </li>
              }
            }
          </ul>
        </nav>

        <div class="p-4 border-t border-blue-700">
          <button
            (click)="logout()"
            class="w-full flex items-center gap-3 px-3 py-2.5 text-red-300 hover:bg-red-600 hover:text-white rounded-lg transition-colors">
            <lucide-icon [img]="logoutIcon" [size]="20"></lucide-icon>
            <span class="text-sm font-medium">Cerrar sesión</span>
          </button>
        </div>
      </aside>

      <div class="flex-1 lg:ml-64">
        <header class="h-16 bg-white shadow-sm flex items-center justify-between px-4 lg:px-6 sticky top-0 z-40">
          <button
            (click)="toggleSidebar()"
            class="lg:hidden p-2 hover:bg-gray-100 rounded-lg">
            <lucide-icon [img]="menuIcon" [size]="24"></lucide-icon>
          </button>

          <div class="hidden lg:flex items-center gap-2">
            @if (authService.organization()) {
              <span class="text-sm text-gray-500">Organización:</span>
              <span class="font-medium text-gray-800">{{ authService.organization()?.organizationName || authService.organization()?.name }}</span>
            }
          </div>

          <div class="flex items-center gap-4">
            <button class="relative p-2 hover:bg-gray-100 rounded-lg">
              <lucide-icon [img]="bellIcon" [size]="20" class="text-gray-600"></lucide-icon>
              <span class="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
            </button>

            <div class="flex items-center gap-3">
              <a [routerLink]="profileRoute()" class="flex items-center gap-3 hover:bg-gray-50 rounded-lg px-2 py-1 transition-colors">
                <div class="w-9 h-9 bg-blue-600 text-white rounded-full flex items-center justify-center font-semibold text-sm">
                  {{ authService.userInitials() }}
                </div>
                <div class="hidden sm:block text-right">
                  <p class="text-sm font-medium text-gray-800">{{ authService.userFullName() }}</p>
                  <p class="text-xs text-gray-500">{{ roleLabel() }}</p>
                </div>
              </a>
            </div>
          </div>
        </header>

        <main class="p-4 lg:p-6">
          <router-outlet></router-outlet>
        </main>
      </div>

      @if (sidebarOpen()) {
        <div
          (click)="toggleSidebar()"
          class="fixed inset-0 bg-black/50 z-40 lg:hidden">
        </div>
      }
    </div>
  `
})
export class MainLayoutComponent {
     authService = inject(AuthService);

     sidebarOpen = signal(false);
     openSubmenus = signal<string[]>([]);

     menuIcon = Menu;
     closeIcon = X;
     chevronIcon = ChevronDown;
     bellIcon = Bell;
     logoutIcon = LogOut;

     private menuItems: MenuItem[] = [
          { label: 'Inicio', icon: Home, route: '/super-admin/dashboard', roles: ['SUPER_ADMIN'] },
          { label: 'Organizaciones', icon: Building2, route: '/super-admin/organizations', roles: ['SUPER_ADMIN'] },

          { label: 'Dashboard', icon: Home, route: '/admin/dashboard', roles: ['ADMIN'] },
          { label: 'Usuarios', icon: Users, route: '/admin/users', roles: ['ADMIN'] },
          {
               label: 'Configuración',
               icon: Settings,
               route: '/admin/config',
               roles: ['ADMIN'],
               children: [
                    { label: 'Zonas', icon: MapPin, route: '/admin/config/zones', roles: ['ADMIN'] },
                    { label: 'Calles', icon: Route, route: '/admin/config/streets', roles: ['ADMIN'] },
                    { label: 'Tarifas', icon: DollarSign, route: '/admin/config/fares', roles: ['ADMIN'] },
                    { label: 'Parámetros', icon: Settings, route: '/admin/config/parameters', roles: ['ADMIN'] }
               ]
          },
          {
               label: 'Infraestructura',
               icon: Droplets,
               route: '/admin/infrastructure',
               roles: ['ADMIN'],
               children: [
                    { label: 'Cajas de Agua', icon: Droplets, route: '/admin/infrastructure/water-boxes', roles: ['ADMIN'] },
                    { label: 'Transferencias', icon: Truck, route: '/admin/infrastructure/transfers', roles: ['ADMIN'] }
               ]
          },
          {
               label: 'Comercial',
               icon: DollarSign,
               route: '/admin/commercial',
               roles: ['ADMIN'],
               children: [
                    { label: 'Recibos', icon: FileText, route: '/admin/commercial/receipts', roles: ['ADMIN'] },
                    { label: 'Pagos', icon: CreditCard, route: '/admin/commercial/payments', roles: ['ADMIN'] },
                    { label: 'Deudas', icon: AlertCircle, route: '/admin/commercial/debts', roles: ['ADMIN'] },
                    { label: 'Cortes', icon: X, route: '/admin/commercial/cuts', roles: ['ADMIN'] },
                    { label: 'Caja Chica', icon: DollarSign, route: '/admin/commercial/petty-cash', roles: ['ADMIN'] }
               ]
          },
          {
               label: 'Inventario',
               icon: Package,
               route: '/admin/inventory',
               roles: ['ADMIN'],
               children: [
                    { label: 'Proveedores', icon: Building2, route: '/admin/inventory/suppliers', roles: ['ADMIN'] },
                    { label: 'Materiales', icon: Package, route: '/admin/inventory/materials', roles: ['ADMIN'] },
                    { label: 'Compras', icon: ClipboardList, route: '/admin/inventory/purchases', roles: ['ADMIN'] },
                    { label: 'Movimientos', icon: Truck, route: '/admin/inventory/movements', roles: ['ADMIN'] }
               ]
          },
          {
               label: 'Reclamos',
               icon: AlertCircle,
               route: '/admin/claims',
               roles: ['ADMIN'],
               children: [
                    { label: 'Quejas', icon: AlertCircle, route: '/admin/claims/complaints', roles: ['ADMIN'] },
                    { label: 'Incidencias', icon: AlertCircle, route: '/admin/claims/incidents', roles: ['ADMIN'] }
               ]
          },
          {
               label: 'Distribución',
               icon: Truck,
               route: '/admin/distribution',
               roles: ['ADMIN'],
               children: [
                    { label: 'Programas', icon: Calendar, route: '/admin/distribution/programs', roles: ['ADMIN'] },
                    { label: 'Rutas', icon: Route, route: '/admin/distribution/routes', roles: ['ADMIN'] },
                    { label: 'Horarios', icon: Calendar, route: '/admin/distribution/schedules', roles: ['ADMIN'] }
               ]
          },
          { label: 'Notificaciones', icon: Bell, route: '/admin/notifications', roles: ['ADMIN'] },
          { label: 'Reportes', icon: BarChart3, route: '/admin/reports', roles: ['ADMIN'] },

          { label: 'Dashboard', icon: Home, route: '/operator/dashboard', roles: ['OPERATOR'] },
          { label: 'Usuarios', icon: Users, route: '/operator/users', roles: ['OPERATOR'] },
          { label: 'Recibos', icon: FileText, route: '/operator/receipts', roles: ['OPERATOR'] },
          { label: 'Pagos', icon: CreditCard, route: '/operator/payments', roles: ['OPERATOR'] },
          { label: 'Quejas', icon: AlertCircle, route: '/operator/complaints', roles: ['OPERATOR'] },

          { label: 'Inicio', icon: Home, route: '/client/dashboard', roles: ['CLIENT'] },
          { label: 'Mis Recibos', icon: FileText, route: '/client/receipts', roles: ['CLIENT'] },
          { label: 'Mis Pagos', icon: CreditCard, route: '/client/payments', roles: ['CLIENT'] },
          { label: 'Nueva Queja', icon: AlertCircle, route: '/client/complaints', roles: ['CLIENT'] },
     ];

     filteredMenuItems = computed(() => {
          const role = this.authService.userRole();
          if (!role) return [];

          return this.menuItems.filter(item => item.roles.includes(role)).map(item => {
               if (item.children) {
                    return {
                         ...item,
                         children: item.children.filter(child => child.roles.includes(role))
                    };
               }
               return item;
          });
     });

     roleLabel = computed(() => {
          const role = this.authService.userRole();
          const labels: Record<Role, string> = {
               SUPER_ADMIN: 'Super Administrador',
               ADMIN: 'Administrador',
               OPERATOR: 'Operador',
               CLIENT: 'Cliente'
          };
          return role ? labels[role] : '';
     });

     profileRoute = computed(() => {
          const role = this.authService.userRole();
          if (role === 'SUPER_ADMIN') return '/super-admin/profile';
          if (role === 'ADMIN') return '/admin/profile';
          if (role === 'OPERATOR') return '/operator/profile';
          return '/client/profile';
     });

     toggleSidebar(): void {
          this.sidebarOpen.update(v => !v);
     }

     toggleSubmenu(route: string): void {
          this.openSubmenus.update(menus => {
               if (menus.includes(route)) {
                    return menus.filter(m => m !== route);
               }
               return [...menus, route];
          });
     }

     logout(): void {
          this.authService.logout();
     }
}
