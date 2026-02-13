import { Component, signal, inject, OnDestroy, OnInit, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LucideAngularModule, Eye, EyeOff, Mail, Lock, Loader2, User, MapPin, Shield, Zap, CheckCircle, ArrowRight } from 'lucide-angular';
import { AuthService } from '../../../core/services/auth.service';
import { AlertService } from '../../../core/services/alert.service';
import { EvolutionApiService } from '../../../core/services/evolution-api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="h-screen w-screen flex overflow-hidden">
      <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-slate-800 via-slate-900 to-slate-950 relative">
        <div class="absolute inset-0 overflow-hidden">
          <div class="absolute top-20 left-20 w-32 h-32 bg-cyan-500/10 rounded-full blur-3xl"></div>
          <div class="absolute bottom-40 right-20 w-48 h-48 bg-blue-500/10 rounded-full blur-3xl"></div>
          <div class="absolute top-1/2 left-1/4 w-24 h-24 bg-teal-500/10 rounded-full blur-2xl"></div>
        </div>

        <div class="absolute top-16 left-16 w-14 h-14 bg-cyan-500/20 rounded-full flex items-center justify-center">
          <lucide-icon [img]="mapPinIcon" [size]="20" class="text-cyan-400"></lucide-icon>
        </div>
        <div class="absolute bottom-24 left-24 w-10 h-10 bg-blue-500/20 rounded-full flex items-center justify-center">
          <lucide-icon [img]="mapPinIcon" [size]="14" class="text-blue-400"></lucide-icon>
        </div>
        <div class="absolute top-1/3 right-20 w-8 h-8 bg-teal-500/20 rounded-full flex items-center justify-center">
          <lucide-icon [img]="mapPinIcon" [size]="12" class="text-teal-400"></lucide-icon>
        </div>

        <div class="relative z-10 flex flex-col justify-center px-12 w-full">
          <div class="flex items-center justify-center mb-6">
            <div class="relative">
              <div class="absolute -inset-3 bg-cyan-500/20 rounded-full blur-xl"></div>
              <lucide-icon [img]="mapPinIcon" [size]="56" class="text-cyan-400 relative"></lucide-icon>
            </div>
          </div>

          <h1 class="text-4xl font-bold text-white text-center mb-3 tracking-tight">JASS</h1>
          <p class="text-lg text-cyan-300 text-center mb-1">Junta Administradora de Servicios de Saneamiento</p>
          <p class="text-base text-slate-400 text-center mb-8">Sistema Digital Corporativo</p>

          <p class="text-slate-300 text-center mb-8 max-w-sm mx-auto leading-relaxed text-sm">
            Gestión Integral de Servicios de Agua y Saneamiento para comunidades
          </p>

          <div class="space-y-3 max-w-xs mx-auto">
            <div class="flex items-center gap-3 text-white/90">
              <div class="w-9 h-9 bg-emerald-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                <lucide-icon [img]="checkIcon" [size]="18" class="text-emerald-400"></lucide-icon>
              </div>
              <span class="text-sm">Acceso Seguro 24/7</span>
            </div>
            <div class="flex items-center gap-3 text-white/90">
              <div class="w-9 h-9 bg-yellow-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                <lucide-icon [img]="zapIcon" [size]="18" class="text-yellow-400"></lucide-icon>
              </div>
              <span class="text-sm">Procesos Optimizados</span>
            </div>
            <div class="flex items-center gap-3 text-white/90">
              <div class="w-9 h-9 bg-blue-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                <lucide-icon [img]="shieldIcon" [size]="18" class="text-blue-400"></lucide-icon>
              </div>
              <span class="text-sm">Confidencialidad Garantizada</span>
            </div>
          </div>
        </div>
      </div>

      <div class="w-full lg:w-1/2 flex items-center justify-center p-6 bg-gray-50 overflow-auto">
        <div class="w-full max-w-md">
          <div class="bg-white rounded-2xl shadow-xl p-8 border border-gray-100">
            <div class="flex flex-col items-center mb-6">
              <div class="w-16 h-16 bg-gradient-to-br from-slate-700 to-slate-900 rounded-full flex items-center justify-center mb-4 shadow-lg">
                <lucide-icon [img]="userIcon" [size]="28" class="text-white"></lucide-icon>
              </div>

              <h2 class="text-2xl font-bold text-gray-800 mb-1">Bienvenido</h2>
              <p class="text-gray-500 text-center text-sm">Ingrese sus credenciales para acceder al sistema</p>
            </div>

            <form (ngSubmit)="onSubmit()" class="space-y-5">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Usuario</label>
                <div class="relative">
                  <span class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                    <lucide-icon [img]="mailIcon" [size]="18"></lucide-icon>
                  </span>
                  <input
                    type="email"
                    [(ngModel)]="email"
                    name="email"
                    required
                    autocomplete="email"
                    class="w-full pl-11 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-slate-800 focus:border-transparent transition-all bg-gray-50 focus:bg-white text-sm"
                    placeholder="correo@ejemplo.com">
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Contraseña</label>
                <div class="relative">
                  <span class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                    <lucide-icon [img]="lockIcon" [size]="18"></lucide-icon>
                  </span>
                  <input
                    [type]="showPassword() ? 'text' : 'password'"
                    [(ngModel)]="password"
                    name="password"
                    required
                    autocomplete="current-password"
                    class="w-full pl-11 pr-11 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-slate-800 focus:border-transparent transition-all bg-gray-50 focus:bg-white text-sm"
                    placeholder="Ingrese su contraseña">
                  <button
                    type="button"
                    (click)="togglePassword()"
                    class="absolute right-3.5 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors">
                    <lucide-icon [img]="showPassword() ? eyeOffIcon : eyeIcon" [size]="18"></lucide-icon>
                  </button>
                </div>
              </div>

              <div class="flex items-center justify-between text-sm">
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="checkbox" [(ngModel)]="rememberMe" name="remember" class="w-4 h-4 rounded border-gray-300 text-slate-800 focus:ring-slate-800">
                  <span class="text-gray-600">Recordar sesión</span>
                </label>
                <button type="button" class="text-slate-700 hover:text-slate-900 hover:underline font-medium">
                  ¿Olvidó su contraseña?
                </button>
              </div>

              <button
                type="submit"
                [disabled]="isLoading()"
                class="w-full py-3.5 bg-gradient-to-r from-slate-800 to-slate-900 hover:from-slate-900 hover:to-slate-950 disabled:from-slate-400 disabled:to-slate-500 text-white font-semibold rounded-xl transition-all flex items-center justify-center gap-2 shadow-lg hover:shadow-xl text-sm">
                @if (isLoading()) {
                  <lucide-icon [img]="loaderIcon" [size]="18" class="animate-spin"></lucide-icon>
                  Iniciando sesión...
                } @else {
                  Iniciar Sesión
                  <lucide-icon [img]="arrowRightIcon" [size]="18"></lucide-icon>
                }
              </button>
            </form>

            <div class="mt-6 pt-5 border-t border-gray-100 text-center">
              <p class="text-gray-400 text-xs">
                © 2025 JASS - Sistema Digital Corporativo<br>
                Todos los derechos reservados
              </p>
            </div>
          </div>

          <div class="lg:hidden mt-6 text-center">
            <div class="flex items-center justify-center gap-2 mb-1">
              <lucide-icon [img]="mapPinIcon" [size]="20" class="text-slate-700"></lucide-icon>
              <span class="text-lg font-bold text-slate-800">JASS Digital</span>
            </div>
            <p class="text-xs text-gray-500">Sistema de Gestión de Agua y Saneamiento</p>
          </div>
        </div>
      </div>
    </div>

    @if (showQrModal()) {
      <div class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
        <div class="bg-white rounded-2xl shadow-2xl p-8 max-w-md w-full">
          <div class="text-center mb-6">
            <div class="w-16 h-16 bg-gradient-to-br from-green-500 to-emerald-600 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-white" viewBox="0 0 24 24" fill="currentColor">
                <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
              </svg>
            </div>
            <h2 class="text-xl font-bold text-gray-800">Conectar WhatsApp</h2>
            <p class="text-gray-500 text-sm mt-1">Escanea el código QR con WhatsApp para continuar</p>
          </div>

          <div class="flex items-center justify-center mb-6">
            @if (evolutionApi.connectionStatus() === 'connecting') {
              <div class="w-64 h-64 bg-gray-100 rounded-xl flex items-center justify-center">
                <div class="text-center">
                  <lucide-icon [img]="loaderIcon" [size]="40" class="animate-spin text-slate-700 mx-auto mb-2"></lucide-icon>
                  <p class="text-gray-500 text-sm">Generando código QR...</p>
                </div>
              </div>
            } @else if (evolutionApi.connectionStatus() === 'qr_ready' && evolutionApi.qrCode()) {
              <img [src]="evolutionApi.qrCode()" alt="QR Code" class="w-64 h-64 rounded-xl border border-gray-200">
            } @else if (evolutionApi.connectionStatus() === 'connected') {
              <div class="w-64 h-64 bg-green-50 rounded-xl flex items-center justify-center">
                <div class="text-center">
                  <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
                    <lucide-icon [img]="checkIcon" [size]="40" class="text-green-600"></lucide-icon>
                  </div>
                  <p class="text-green-700 font-semibold">¡Conectado!</p>
                  <p class="text-green-600 text-sm">{{ evolutionApi.phoneNumber() }}</p>
                </div>
              </div>
            } @else {
              <div class="w-64 h-64 bg-red-50 rounded-xl flex items-center justify-center">
                <div class="text-center">
                  <p class="text-red-600 font-medium">Error de conexión</p>
                  <button
                    (click)="retryQr()"
                    class="mt-3 px-4 py-2 bg-red-600 text-white rounded-lg text-sm hover:bg-red-700 transition-colors">
                    Reintentar
                  </button>
                </div>
              </div>
            }
          </div>

          <p class="text-center text-gray-400 text-xs">
            Este paso es obligatorio para administradores
          </p>
        </div>
      </div>
    }
  `
})
export class LoginComponent implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  evolutionApi = inject(EvolutionApiService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  email = '';
  password = '';
  rememberMe = false;

  showPassword = signal(false);
  isLoading = signal(false);
  isAnimating = signal(false);
  isSuccess = signal(false);
  showQrModal = signal(false);

  mailIcon = Mail;
  lockIcon = Lock;
  eyeIcon = Eye;
  eyeOffIcon = EyeOff;
  loaderIcon = Loader2;
  userIcon = User;
  mapPinIcon = MapPin;
  shieldIcon = Shield;
  zapIcon = Zap;
  checkIcon = CheckCircle;
  arrowRightIcon = ArrowRight;

  private pollingSubscription: any;
  private animationInterval: any;

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.startIdleAnimation();
    }
  }

  ngOnDestroy(): void {
    if (this.animationInterval) {
      clearInterval(this.animationInterval);
    }
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  private startIdleAnimation(): void {
    this.animationInterval = setInterval(() => {
      if (!this.isLoading()) {
        this.isAnimating.set(true);
        setTimeout(() => this.isAnimating.set(false), 1000);
      }
    }, 5000);
  }

  togglePassword(): void {
    this.showPassword.update(v => !v);
  }

  async onSubmit(): Promise<void> {
    if (!this.email || !this.password) {
      this.alertService.warning('Campos requeridos', 'Por favor complete todos los campos');
      return;
    }

    this.isLoading.set(true);

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        this.authService.loadUserProfile().subscribe({
          next: () => {
            const role = this.authService.userRole();

            if (role === 'ADMIN') {
              this.handleAdminLogin();
            } else {
              this.completeLogin();
            }
          },
          error: () => {
            this.isLoading.set(false);
          }
        });
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  private handleAdminLogin(): void {
    this.showQrModal.set(true);
    this.isLoading.set(false);

    this.evolutionApi.checkConnection().subscribe(status => {
      if (status.state === 'open') {
        this.completeLogin();
      } else {
        this.evolutionApi.getQrCode().subscribe({
          next: () => {
            this.startPolling();
          },
          error: () => {
            this.alertService.error('Error', 'No se pudo generar el código QR');
          }
        });
      }
    });
  }

  private startPolling(): void {
    this.pollingSubscription = this.evolutionApi.startPolling().subscribe({
      next: res => {
        const state = res?.state || res?.instance?.state || res?.instance?.status || res?.status;
        if (state === 'open') {
          setTimeout(() => this.completeLogin(), 1000);
        }
      },
      error: () => { }
    });
  }

  retryQr(): void {
    this.evolutionApi.getQrCode().subscribe({
      next: () => this.startPolling(),
      error: () => this.alertService.error('Error', 'No se pudo generar el código QR')
    });
  }

  private completeLogin(): void {
    this.showQrModal.set(false);
    this.isSuccess.set(true);
    this.alertService.success('¡Bienvenido!', `Hola ${this.authService.userFullName()}`);

    const role = this.authService.userRole();
    const orgId = this.authService.organizationId();

    if (orgId && role !== 'SUPER_ADMIN') {
      this.authService.loadOrganization(orgId).subscribe({
        next: () => this.redirectByRole(role!),
        error: () => this.redirectByRole(role!)
      });
    } else {
      this.redirectByRole(role!);
    }
  }

  private redirectByRole(role: string): void {
    switch (role) {
      case 'SUPER_ADMIN':
        this.router.navigate(['/super-admin/dashboard']);
        break;
      case 'ADMIN':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'OPERATOR':
        this.router.navigate(['/operator/dashboard']);
        break;
      case 'CLIENT':
        this.router.navigate(['/client/dashboard']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }
}
