import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
     {
          path: 'dashboard',
          loadComponent: () => import('./dashboard/dashboard.component').then(m => m.AdminDashboardComponent)
     },
     {
          path: 'users',
          loadComponent: () => import('./users/users-list.component').then(m => m.UsersListComponent)
     },
     {
          path: 'users/new',
          loadComponent: () => import('./users/user-form.component').then(m => m.UserFormComponent)
     },
     {
          path: 'users/:id',
          loadComponent: () => import('./users/user-form.component').then(m => m.UserFormComponent)
     },
     {
          path: 'config',
          loadComponent: () => import('./config/config.component').then(m => m.ConfigComponent)
     },
     {
          path: 'infrastructure/water-boxes',
          loadComponent: () => import('./infrastructure/water-boxes/water-boxes.component').then(m => m.WaterBoxesComponent)
     },
     {
          path: 'infrastructure/transfers',
          loadComponent: () => import('./infrastructure/transfers/transfers.component').then(m => m.TransfersComponent)
     },
     {
          path: 'commercial/receipts',
          loadComponent: () => import('./commercial/receipts/receipts.component').then(m => m.ReceiptsComponent)
     },
     {
          path: 'commercial/payments',
          loadComponent: () => import('./commercial/payments/payments.component').then(m => m.PaymentsComponent)
     },
     {
          path: 'commercial/debts',
          loadComponent: () => import('./commercial/debts/debts.component').then(m => m.DebtsComponent)
     },
     {
          path: 'commercial/cuts',
          loadComponent: () => import('./commercial/cuts/cuts.component').then(m => m.CutsComponent)
     },
     {
          path: 'commercial/petty-cash',
          loadComponent: () => import('./commercial/petty-cash/petty-cash.component').then(m => m.PettyCashComponent)
     },
     {
          path: 'inventory/suppliers',
          loadComponent: () => import('./inventory/suppliers/suppliers.component').then(m => m.SuppliersComponent)
     },
     {
          path: 'inventory/materials',
          loadComponent: () => import('./inventory/materials/materials.component').then(m => m.MaterialsComponent)
     },
     {
          path: 'inventory/purchases',
          loadComponent: () => import('./inventory/purchases/purchases.component').then(m => m.PurchasesComponent)
     },
     {
          path: 'inventory/movements',
          loadComponent: () => import('./inventory/movements/movements.component').then(m => m.MovementsComponent)
     },
     {
          path: 'claims/complaints',
          loadComponent: () => import('./claims/complaints/complaints.component').then(m => m.ComplaintsComponent)
     },
     {
          path: 'claims/incidents',
          loadComponent: () => import('./claims/incidents/incidents.component').then(m => m.IncidentsComponent)
     },
     {
          path: 'claims/incident-types',
          loadComponent: () => import('./claims/incident-types/incident-types.component').then(m => m.IncidentTypesComponent)
     },
     {
          path: 'claims/complaint-categories',
          loadComponent: () => import('./claims/complaint-categories/complaint-categories.component').then(m => m.ComplaintCategoriesComponent)
     },
     {
          path: 'distribution/programs',
          loadComponent: () => import('./distribution/programs/programs.component').then(m => m.ProgramsComponent)
     },
     {
          path: 'distribution/routes',
          loadComponent: () => import('./distribution/routes/routes.component').then(m => m.RoutesComponent)
     },
     {
          path: 'distribution/schedules',
          loadComponent: () => import('./distribution/schedules/schedules.component').then(m => m.SchedulesComponent)
     },
     {
          path: 'notifications',
          loadComponent: () => import('./notifications/notifications.component').then(m => m.NotificationsComponent)
     },
     {
          path: 'reports',
          loadComponent: () => import('./reports/reports.component').then(m => m.ReportsComponent)
     },
     {
          path: 'profile',
          loadComponent: () => import('./profile/profile.component').then(m => m.AdminProfileComponent)
     },
     {
          path: '',
          redirectTo: 'dashboard',
          pathMatch: 'full'
     }
];
