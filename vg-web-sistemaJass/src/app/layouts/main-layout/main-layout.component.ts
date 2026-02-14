import { Component, computed, inject, signal, OnInit, OnDestroy, PLATFORM_ID, HostListener } from '@angular/core';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { RouterOutlet, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Role } from '../../core/models';
import { LucideAngularModule, Bell, Menu, Clock } from 'lucide-angular';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, LucideAngularModule, SidebarComponent, DatePipe],
  template: `
    <div class="min-h-screen bg-slate-50 flex">
      <app-sidebar
        [isOpen]="sidebarOpen()"
        (closeSidebar)="sidebarOpen.set(false)"
      ></app-sidebar>

      <div class="flex-1 transition-all duration-300"
           [class.lg:ml-72]="sidebarOpen()"
           [class.lg:ml-0]="!sidebarOpen()">
        <header class="h-20 bg-white/80 backdrop-blur-md shadow-sm border-b border-slate-200 flex items-center justify-between px-4 lg:px-8 sticky top-0 z-40 relative">
          <div class="flex items-center gap-4">
            <button
              (click)="toggleSidebar()"
              class="p-2 text-slate-600 hover:bg-slate-100 rounded-xl transition-colors">
              <lucide-icon [img]="menuIcon" [size]="24"></lucide-icon>
            </button>

            <div class="hidden md:flex flex-col">
              @if (authService.organization()) {
                <span class="text-xs font-semibold text-blue-600 uppercase tracking-wider">Organización</span>
                <span class="font-bold text-slate-800 text-lg leading-tight">{{ authService.organization()?.organizationName || authService.organization()?.name }}</span>
              }
            </div>
          </div>

          <div class="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 flex items-center gap-3 bg-slate-50/80 backdrop-blur-sm px-4 py-2 rounded-2xl border border-slate-200 shadow-sm z-10">
            <div class="hidden sm:block p-1.5 bg-white rounded-lg shadow-sm text-blue-600">
              <lucide-icon [img]="clockIcon" [size]="18"></lucide-icon>
            </div>
            <div class="flex flex-col items-center sm:items-start leading-none min-w-[80px] sm:min-w-[140px]">
              <span class="hidden sm:block text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-0.5 w-full truncate text-center sm:text-left">{{ currentDate() | date:'EEEE d, MMMM':undefined:'es' | titlecase }}</span>
              <span class="text-lg sm:text-xl font-black text-slate-800 tabular-nums tracking-tight w-full text-center sm:text-left">{{ currentDate() | date:'HH:mm:ss':undefined:'es' }}</span>
            </div>
          </div>

          <div class="flex items-center gap-6">
            <button class="relative p-2 text-slate-500 hover:bg-slate-100 rounded-xl transition-colors">
              <lucide-icon [img]="bellIcon" [size]="22"></lucide-icon>
              <span class="absolute top-2 right-2 w-2.5 h-2.5 bg-red-500 rounded-full border-2 border-white"></span>
            </button>

            <div class="h-8 w-px bg-slate-200 mx-2 hidden sm:block"></div>

            <a [routerLink]="profileRoute()" class="flex items-center gap-4 hover:bg-slate-50 rounded-xl px-3 py-2 transition-all group">
              <div class="hidden lg:block text-right">
                <p class="text-sm font-bold text-slate-800 group-hover:text-blue-600 transition-colors">{{ authService.userFullName() }}</p>
                <p class="text-xs text-slate-500 font-medium bg-slate-100 px-2 py-0.5 rounded-full inline-block">{{ roleLabel() }}</p>
              </div>
              <div class="w-10 h-10 bg-gradient-to-br from-blue-600 to-blue-700 text-white rounded-xl shadow-lg shadow-blue-600/20 flex items-center justify-center font-bold text-sm ring-2 ring-transparent group-hover:ring-blue-100 transition-all">
                {{ authService.userInitials() }}
              </div>
            </a>
          </div>
        </header>

        <main class="p-4 lg:p-8 max-w-7xl mx-auto">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  authService = inject(AuthService);
  private platformId = inject(PLATFORM_ID);

  sidebarOpen = signal(true);
  currentDate = signal(new Date());
  private timer: any;

  menuIcon = Menu;
  bellIcon = Bell;
  clockIcon = Clock;

  roleLabel = computed(() => {
    const role = this.authService.userRole();
    const labels: Record<string, string> = {
      SUPER_ADMIN: 'Super Admin',
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

  ngOnInit() {
    this.timer = setInterval(() => {
      this.currentDate.set(new Date());
    }, 1000);

    this.checkScreenSize();
  }

  ngOnDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  @HostListener('window:resize', [])
  onResize() {
    this.checkScreenSize();
  }

  private checkScreenSize() {
    if (isPlatformBrowser(this.platformId)) {
      if (window.innerWidth < 1024) {
        this.sidebarOpen.set(false);
      } else {
        this.sidebarOpen.set(true);
      }
    }
  }

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }
}
