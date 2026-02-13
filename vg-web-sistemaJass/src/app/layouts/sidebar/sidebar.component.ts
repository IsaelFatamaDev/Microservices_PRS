import { Component, computed, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Role } from '../../core/models';
import {
    LucideAngularModule,
    Home,
    Users,
    Building2,
    MapPin,
    Route,
    DollarSign,
    Package,
    ClipboardList,
    Bell,
    Settings,
    LogOut,
    X,
    ChevronDown,
    Droplets,
    FileText,
    BarChart3,
    Calendar,
    Truck,
    AlertCircle,
    CreditCard
} from 'lucide-angular';

interface MenuItem {
    label: string;
    icon: any;
    route: string;
    roles: Role[];
    children?: MenuItem[];
}

@Component({
    selector: 'app-sidebar',
    standalone: true,
    imports: [CommonModule, RouterLink, RouterLinkActive, LucideAngularModule],
    template: `
    <aside
      class="fixed inset-y-0 left-0 z-50 w-72 bg-white text-slate-700 transform transition-transform duration-300 ease-in-out shadow-xl flex flex-col border-r border-slate-200"
      [class.-translate-x-full]="!isOpen()"
      [class.translate-x-0]="isOpen()"
    >
      <div class="h-20 flex items-center justify-between px-6 border-b border-slate-100 bg-white">
        <div class="flex items-center gap-3">
          <div class="relative">
            <img src="assets/Gotita.png" alt="JASS" class="w-10 h-10 object-contain relative z-10">
          </div>
          <div>
            <h1 class="font-bold text-xl text-slate-800 tracking-tight">JASS Digital</h1>
            <p class="text-[10px] text-slate-500 font-medium uppercase tracking-wider">Sistema de Gestión</p>
          </div>
        </div>
        <button (click)="closeSidebar.emit()" class="lg:hidden p-1 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-colors">
          <lucide-icon [img]="closeIcon" [size]="20"></lucide-icon>
        </button>
      </div>

      <nav class="flex-1 overflow-y-auto py-6 px-4 custom-scrollbar">
        <div class="mb-2 px-2 text-xs font-bold text-slate-400 uppercase tracking-widest">Menu Principal</div>
        <ul class="space-y-1">
          @for (item of filteredMenuItems(); track item.route) {
            @if (!item.children) {
              <li>
                <a
                  [routerLink]="item.route"
                  routerLinkActive="bg-blue-50 text-blue-700 font-semibold"
                  #rla="routerLinkActive"
                  [routerLinkActiveOptions]="{exact: false}"
                  class="group flex items-center gap-3 px-3 py-3 rounded-lg hover:bg-slate-50 hover:text-slate-900 transition-all duration-200 text-slate-600"
                >
                  <lucide-icon [img]="item.icon" [size]="20" class="group-hover:scale-105 transition-transform duration-200" [class.text-blue-600]="rla.isActive"></lucide-icon>
                  <span class="text-sm">{{ item.label }}</span>
                </a>
              </li>
            } @else {
              <li>
                <button
                  (click)="toggleSubmenu(item.route)"
                  class="w-full flex items-center justify-between px-3 py-3 rounded-lg hover:bg-slate-50 hover:text-slate-900 transition-all duration-200 group text-slate-600"
                  [class.text-blue-700]="openSubmenus().includes(item.route)"
                  [class.bg-slate-50]="openSubmenus().includes(item.route)"
                >
                  <div class="flex items-center gap-3">
                    <lucide-icon [img]="item.icon" [size]="20" class="group-hover:scale-105 transition-transform duration-200"></lucide-icon>
                    <span class="text-sm font-medium">{{ item.label }}</span>
                  </div>
                  <lucide-icon
                    [img]="chevronIcon"
                    [size]="16"
                    class="transition-transform duration-200 text-slate-400 group-hover:text-slate-600"
                    [class.rotate-180]="openSubmenus().includes(item.route)"
                  >
                  </lucide-icon>
                </button>
                @if (openSubmenus().includes(item.route)) {
                  <div class="overflow-hidden transition-all duration-300 ease-in-out">
                    <ul class="mt-1 ml-4 pl-4 border-l border-slate-200 space-y-1 py-1">
                      @for (child of item.children; track child.route) {
                        <li>
                          <a
                            [routerLink]="child.route"
                            routerLinkActive="text-blue-700 font-medium bg-blue-50/50"
                            class="flex items-center gap-2 px-3 py-2 rounded-lg hover:text-slate-900 hover:bg-slate-50 transition-colors text-sm text-slate-500"
                          >
                            <span class="w-1.5 h-1.5 rounded-full bg-slate-300 group-hover:bg-slate-400 transition-colors" routerLinkActive="bg-blue-500"></span>
                            <span>{{ child.label }}</span>
                          </a>
                        </li>
                      }
                    </ul>
                  </div>
                }
              </li>
            }
          }
        </ul>
      </nav>

      <div class="p-4 border-t border-slate-100 bg-slate-50/50">
        <button
          (click)="logout()"
          class="w-full flex items-center justify-center gap-2 px-4 py-3 text-slate-600 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all duration-200 group border border-slate-200 hover:border-red-100 bg-white shadow-sm"
        >
          <lucide-icon [img]="logoutIcon" [size]="18" class="group-hover:-translate-x-1 transition-transform"></lucide-icon>
          <span class="text-sm font-medium">Cerrar Sesión</span>
        </button>
      </div>
    </aside>

    @if (isOpen()) {
      <div
        (click)="closeSidebar.emit()"
        class="fixed inset-0 bg-slate-900/20 backdrop-blur-sm z-40 lg:hidden transition-opacity duration-300"
      ></div>
    }
  `,
    styles: [`
    .custom-scrollbar::-webkit-scrollbar {
      width: 4px;
    }
    .custom-scrollbar::-webkit-scrollbar-track {
      background: transparent;
    }
    .custom-scrollbar::-webkit-scrollbar-thumb {
      background: #cbd5e1;
      border-radius: 4px;
    }
    .custom-scrollbar::-webkit-scrollbar-thumb:hover {
      background: #94a3b8;
    }
  `]
})
export class SidebarComponent {
    authService = inject(AuthService);
    isOpen = input.required<boolean>();
    closeSidebar = output<void>();

    openSubmenus = signal<string[]>([]);

    closeIcon = X;
    chevronIcon = ChevronDown;
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
