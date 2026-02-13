import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

let isRefreshing = false;

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
     const authService = inject(AuthService);
     const token = authService.accessToken();

     if (!token || req.url.includes('/auth/login') || req.url.includes('/auth/refresh')) {
          return next(req);
     }

     const clonedReq = req.clone({
          setHeaders: { Authorization: `Bearer ${token}` }
     });

     return next(clonedReq).pipe(
          catchError((error: HttpErrorResponse) => {
               if (error.status === 401 && !isRefreshing) {
                    isRefreshing = true;
                    return authService.refreshToken().pipe(
                         switchMap(() => {
                              isRefreshing = false;
                              const newToken = authService.accessToken();
                              const retryReq = req.clone({
                                   setHeaders: { Authorization: `Bearer ${newToken}` }
                              });
                              return next(retryReq);
                         }),
                         catchError(refreshError => {
                              isRefreshing = false;
                              authService.logout();
                              return throwError(() => refreshError);
                         })
                    );
               }
               return throwError(() => error);
          })
     );
};
