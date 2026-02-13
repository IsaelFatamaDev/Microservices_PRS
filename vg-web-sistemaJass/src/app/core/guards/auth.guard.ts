import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
     const authService = inject(AuthService);
     const router = inject(Router);

     if (authService.isAuthenticated()) {
          return true;
     }

     router.navigate(['/auth/login']);
     return false;
};

export const guestGuard: CanActivateFn = () => {
     const authService = inject(AuthService);
     const router = inject(Router);

     if (!authService.isAuthenticated()) {
          return true;
     }

     const role = authService.userRole();
     switch (role) {
          case 'SUPER_ADMIN':
               router.navigate(['/super-admin/dashboard']);
               break;
          case 'ADMIN':
               router.navigate(['/admin/dashboard']);
               break;
          case 'OPERATOR':
               router.navigate(['/operator/dashboard']);
               break;
          case 'CLIENT':
               router.navigate(['/client/dashboard']);
               break;
          default:
               router.navigate(['/']);
     }
     return false;
};
