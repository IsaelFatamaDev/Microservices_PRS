import { HttpInterceptorFn, HttpErrorResponse, HttpContextToken } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AlertService } from '../services/alert.service';

/** Set to true in HttpContext to suppress the global error alert for a request */
export const SKIP_ERROR_ALERT = new HttpContextToken<boolean>(() => false);

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
     const alertService = inject(AlertService);

     return next(req).pipe(
          catchError((error: HttpErrorResponse) => {
               if (req.context.get(SKIP_ERROR_ALERT)) {
                    return throwError(() => error);
               }

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
