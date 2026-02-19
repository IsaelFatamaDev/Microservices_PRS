const NAME_PATTERN = /[^a-zA-Z\u00e1\u00e9\u00ed\u00f3\u00fa\u00c1\u00c9\u00cd\u00d3\u00da\u00f1\u00d1\u00fc\u00dc\s]/g;
const DIGITS_ONLY = /[^0-9]/g;
const ALPHANUMERIC_ONLY = /[^a-zA-Z0-9]/g;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function sanitizeName(value: string): string {
  return value.replace(NAME_PATTERN, '');
}

export function sanitizeDocument(value: string, documentType: string): string {
  if (documentType === 'DNI') return value.replace(DIGITS_ONLY, '').slice(0, 8);
  return value.replace(ALPHANUMERIC_ONLY, '').slice(0, 20);
}

export function sanitizePhone(value: string): string {
  return value.replace(DIGITS_ONLY, '').slice(0, 9);
}

export function validateName(value: string, fieldLabel: string): string {
  if (!value || !value.trim()) return `${fieldLabel} es obligatorio`;
  return '';
}

export function validateDocument(value: string, documentType: string): string {
  if (!value || !value.trim()) return 'El N\u00b0 de documento es obligatorio';
  if (documentType === 'DNI') {
    if (!/^\d+$/.test(value)) return 'El DNI solo permite n\u00fameros';
    if (value.length !== 8) return 'El DNI debe tener exactamente 8 d\u00edgitos';
  } else {
    if (!/^[a-zA-Z0-9]+$/.test(value)) return 'El CNE solo permite letras y n\u00fameros';
    if (value.length < 1 || value.length > 20) return 'El CNE debe tener entre 1 y 20 caracteres';
  }
  return '';
}

export function validateEmail(value: string, required: boolean = false): string {
  if (!value || !value.trim()) {
    return required ? 'El correo electr\u00f3nico es obligatorio' : '';
  }
  if (!EMAIL_REGEX.test(value)) return 'Ingrese un correo electr\u00f3nico v\u00e1lido (ej: user@dominio.com)';
  return '';
}

export function validatePhone(value: string, required: boolean = false): string {
  if (!value || !value.trim()) {
    return required ? 'El tel\u00e9fono es obligatorio' : '';
  }
  if (!/^\d+$/.test(value)) return 'Solo se permiten n\u00fameros';
  if (!value.startsWith('9')) return 'El n\u00famero debe comenzar con 9';
  if (value.length !== 9) return 'Debe tener exactamente 9 d\u00edgitos';
  return '';
}
