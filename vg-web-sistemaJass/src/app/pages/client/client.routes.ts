import { Routes } from '@angular/router';

export const CLIENT_ROUTES: Routes = [
     {
          path: '',
          redirectTo: 'dashboard',
          pathMatch: 'full'
     },
     {
          path: 'dashboard',
          loadComponent: () => import('./dashboard/dashboard.component').then(m => m.ClientDashboardComponent)
     },
     {
          path: 'payments',
          loadComponent: () => import('./payments/payments.component').then(m => m.ClientPaymentsComponent)
     },
     {
          path: 'profile',
          loadComponent: () => import('./profile/profile.component').then(m => m.ClientProfileComponent)
     }
];
