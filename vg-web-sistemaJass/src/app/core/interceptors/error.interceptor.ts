import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AlertService } from '../services/alert.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
     const alertService = inject(AlertService);

     return next(req).pipe(
          catchError((error: HttpErrorResponse) => {
               let message = 'Ocurrió un error inesperado';

               if (error.status === 0) {
                    message = 'No se pudo conectar con el servidor';
               } else if (error.error?.message) {
                    message = error.error.message;
               } else if (error.status === 400) {
                    message = 'Solicitud incorrecta';
               } else if (error.status === 403) {
                    message = 'Acceso denegado';
               } else if (error.status === 404) {
                    message = 'Recurso no encontrado';
               } else if (error.status === 409) {
                    message = 'El recurso ya existe';
               } else if (error.status >= 500) {
                    message = 'Error en el servidor';
               }

               if (error.status !== 401) {
                    alertService.error('Error', message);
               }

               return throwError(() => error);
          })
     );
};
