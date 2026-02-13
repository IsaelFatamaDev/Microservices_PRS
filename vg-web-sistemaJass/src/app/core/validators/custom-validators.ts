import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
    static noNumbers(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) return null;
            const hasNumber = /\d/.test(control.value);
            return hasNumber ? { hasNumber: true } : null;
        };
    }

    static dniStructure(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) return null;
            const valid = /^\d{8}$/.test(control.value);
            return valid ? null : { invalidDni: true };
        };
    }

    static cneStructure(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) return null;
            // Asumiendo CNE como alfanumérico de 20 caracteres según requerimiento
            const valid = /^[a-zA-Z0-9]{20}$/.test(control.value);
            return valid ? null : { invalidCne: true };
        };
    }

    static phoneStructure(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) return null;
            const valid = /^9\d{8}$/.test(control.value);
            return valid ? null : { invalidPhone: true };
        };
    }
}
