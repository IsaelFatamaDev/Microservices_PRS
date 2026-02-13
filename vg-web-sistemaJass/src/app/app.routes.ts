import { Routes } from '@angular/router';
import { authGuard, guestGuard, roleGuard } from './core';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';

export const routes: Routes = [
     {
          path: 'auth',
          component: AuthLayoutComponent,
          canActivate: [guestGuard],
          loadChildren: () => import('./pages/auth/auth.routes').then(m => m.AUTH_ROUTES)
     },
     {
          path: 'super-admin',
          component: MainLayoutComponent,
          canActivate: [authGuard, roleGuard],
          data: { roles: ['SUPER_ADMIN'] },
          loadChildren: () => import('./pages/super-admin/super-admin.routes').then(m => m.SUPER_ADMIN_ROUTES)
     },
     {
          path: 'admin',
          component: MainLayoutComponent,
          canActivate: [authGuard, roleGuard],
          data: { roles: ['ADMIN'] },
          loadChildren: () => import('./pages/admin/admin.routes').then(m => m.ADMIN_ROUTES)
     },
     {
          path: 'client',
          component: MainLayoutComponent,
          canActivate: [authGuard, roleGuard],
          data: { roles: ['CLIENT'] },
          loadChildren: () => import('./pages/client/client.routes').then(m => m.CLIENT_ROUTES)
     },
     {
          path: 'forbidden',
          loadComponent: () => import('./pages/errors/forbidden.component').then(m => m.ForbiddenComponent)
     },
     {
          path: '',
          redirectTo: 'auth/login',
          pathMatch: 'full'
     },
     {
          path: '**',
          loadComponent: () => import('./pages/errors/not-found.component').then(m => m.NotFoundComponent)
     }
];
