import { Routes } from '@angular/router';

export const SUPER_ADMIN_ROUTES: Routes = [
     {
          path: 'dashboard',
          loadComponent: () => import('./dashboard/dashboard.component').then(m => m.SuperAdminDashboardComponent)
     },
     {
          path: 'organizations',
          loadComponent: () => import('./organizations/organizations-list.component').then(m => m.OrganizationsListComponent)
     },
     {
          path: 'organizations/new',
          loadComponent: () => import('./organizations/organization-form.component').then(m => m.OrganizationFormComponent)
     },
     {
          path: 'organizations/:id',
          loadComponent: () => import('./organizations/organization-form.component').then(m => m.OrganizationFormComponent)
     },
     {
          path: 'profile',
          loadComponent: () => import('./profile/profile.component').then(m => m.SuperAdminProfileComponent)
     },
     {
          path: '',
          redirectTo: 'organizations',
          pathMatch: 'full'
     }
];
