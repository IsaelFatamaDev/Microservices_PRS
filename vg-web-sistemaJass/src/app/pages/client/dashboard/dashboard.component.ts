import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';

@Component({
     selector: 'app-client-dashboard',
     standalone: true,
     imports: [CommonModule],
     template: `
          <div class="animate-fade-in">
               <div class="mb-8">
                    <h1 class="text-3xl font-bold text-gray-800">Bienvenido, {{ userName() }}</h1>
                    <p class="text-gray-600 mt-2">Panel de información de tu cuenta</p>
               </div>

               @if (loading()) {
                    <div class="flex justify-center items-center h-64">
                         <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                    </div>
               } @else {
                    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
                         <div class="bg-white rounded-xl shadow-lg p-6 border-l-4 border-blue-500">
                              <div class="flex items-center justify-between">
                                   <div>
                                        <p class="text-sm text-gray-500 uppercase tracking-wide">Mis Suministros</p>
                                        <p class="text-3xl font-bold text-gray-800 mt-2">{{ totalSupplies() }}</p>
                                   </div>
                                   <div class="bg-blue-100 p-3 rounded-full">
                                        <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
                                        </svg>
                                   </div>
                              </div>
                              <p class="text-sm text-gray-500 mt-2">Cajas de agua asignadas</p>
                         </div>

                         <div class="bg-white rounded-xl shadow-lg p-6 border-l-4 border-green-500">
                              <div class="flex items-center justify-between">
                                   <div>
                                        <p class="text-sm text-gray-500 uppercase tracking-wide">Pagos Realizados</p>
                                        <p class="text-3xl font-bold text-gray-800 mt-2">{{ totalPayments() }}</p>
                                   </div>
                                   <div class="bg-green-100 p-3 rounded-full">
                                        <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                                        </svg>
                                   </div>
                              </div>
                              <p class="text-sm text-gray-500 mt-2">Total de pagos registrados</p>
                         </div>

                         <div class="bg-white rounded-xl shadow-lg p-6 border-l-4 border-orange-500">
                              <div class="flex items-center justify-between">
                                   <div>
                                        <p class="text-sm text-gray-500 uppercase tracking-wide">Meses Pendientes</p>
                                        <p class="text-3xl font-bold text-gray-800 mt-2">{{ pendingMonths() }}</p>
                                   </div>
                                   <div class="bg-orange-100 p-3 rounded-full">
                                        <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                                        </svg>
                                   </div>
                              </div>
                              <p class="text-sm text-gray-500 mt-2">Por pagar a la fecha</p>
                         </div>
                    </div>

                    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                         <div class="bg-white rounded-xl shadow-lg p-6">
                              <h2 class="text-xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
                                   <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                                   </svg>
                                   Mi Organización
                              </h2>
                              @if (organization()) {
                                   <div class="space-y-3">
                                        <div class="flex items-center gap-4">
                                             @if (organization()?.logoUrl) {
                                                  <img [src]="organization()?.logoUrl" alt="Logo" class="w-16 h-16 rounded-lg object-cover">
                                             } @else {
                                                  <div class="w-16 h-16 bg-blue-100 rounded-lg flex items-center justify-center">
                                                       <span class="text-2xl font-bold text-blue-600">{{ organization()?.organizationName?.charAt(0) }}</span>
                                                  </div>
                                             }
                                             <div>
                                                  <h3 class="font-semibold text-gray-800">{{ organization()?.organizationName }}</h3>
                                                  <p class="text-sm text-gray-500">{{ organization()?.district }}, {{ organization()?.province }}</p>
                                             </div>
                                        </div>
                                        <div class="border-t pt-3 mt-3 space-y-2">
                                             <div class="flex justify-between">
                                                  <span class="text-gray-500">Departamento:</span>
                                                  <span class="font-medium">{{ organization()?.department }}</span>
                                             </div>
                                             <div class="flex justify-between">
                                                  <span class="text-gray-500">Dirección:</span>
                                                  <span class="font-medium">{{ organization()?.address }}</span>
                                             </div>
                                             <div class="flex justify-between">
                                                  <span class="text-gray-500">Teléfono:</span>
                                                  <span class="font-medium">{{ organization()?.phone || '-' }}</span>
                                             </div>
                                             <div class="flex justify-between">
                                                  <span class="text-gray-500">Email:</span>
                                                  <span class="font-medium">{{ organization()?.email || '-' }}</span>
                                             </div>
                                        </div>
                                   </div>
                              }
                         </div>

                         <div class="bg-white rounded-xl shadow-lg p-6">
                              <h2 class="text-xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
                                   <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
                                   </svg>
                                   Mis Cajas de Agua
                              </h2>
                              @if (waterBoxes().length > 0) {
                                   <div class="space-y-3">
                                        @for (box of waterBoxes(); track box.id) {
                                             <div class="p-4 bg-gray-50 rounded-lg border border-gray-200">
                                                  <div class="flex justify-between items-start">
                                                       <div>
                                                            <p class="font-semibold text-gray-800">Suministro #{{ box.supplyNumber }}</p>
                                                            <p class="text-sm text-gray-500">{{ box.boxType }}</p>
                                                       </div>
                                                       <span
                                                            class="px-2 py-1 text-xs rounded-full"
                                                            [class.bg-green-100]="box.recordStatus === 'ACTIVE'"
                                                            [class.text-green-700]="box.recordStatus === 'ACTIVE'"
                                                            [class.bg-red-100]="box.recordStatus !== 'ACTIVE'"
                                                            [class.text-red-700]="box.recordStatus !== 'ACTIVE'">
                                                            {{ box.recordStatus === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                                                       </span>
                                                  </div>
                                                  <div class="mt-2 text-sm text-gray-500">
                                                       <p>Instalación: {{ box.installationDate | date:'dd/MM/yyyy' }}</p>
                                                       <p>Dirección: {{ box.address }}</p>
                                                  </div>
                                             </div>
                                        }
                                   </div>
                              } @else {
                                   <div class="text-center py-8 text-gray-500">
                                        <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
                                        </svg>
                                        <p>No tienes cajas de agua asignadas</p>
                                   </div>
                              }
                         </div>
                    </div>

                    <div class="mt-6 bg-white rounded-xl shadow-lg p-6">
                         <h2 class="text-xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
                              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                              </svg>
                              Últimos Pagos
                         </h2>
                         @if (recentPayments().length > 0) {
                              <div class="overflow-x-auto">
                                   <table class="w-full">
                                        <thead class="bg-gray-50">
                                             <tr>
                                                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
                                                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Recibo</th>
                                                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Concepto</th>
                                                  <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Monto</th>
                                             </tr>
                                        </thead>
                                        <tbody class="divide-y divide-gray-200">
                                             @for (payment of recentPayments(); track payment.id) {
                                                  <tr class="hover:bg-gray-50">
                                                       <td class="px-4 py-3 text-sm text-gray-600">{{ payment.paymentDate | date:'dd/MM/yyyy' }}</td>
                                                       <td class="px-4 py-3 text-sm font-medium text-gray-800">{{ payment.receiptNumber }}</td>
                                                       <td class="px-4 py-3 text-sm text-gray-600">{{ payment.paymentConcept }}</td>
                                                       <td class="px-4 py-3 text-sm text-right font-semibold text-green-600">S/ {{ payment.amount | number:'1.2-2' }}</td>
                                                  </tr>
                                             }
                                        </tbody>
                                   </table>
                              </div>
                              <div class="mt-4 text-center">
                                   <a routerLink="/client/payments" class="text-blue-600 hover:text-blue-800 font-medium text-sm">
                                        Ver todos los pagos →
                                   </a>
                              </div>
                         } @else {
                              <div class="text-center py-8 text-gray-500">
                                   <svg class="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                                   </svg>
                                   <p>No tienes pagos registrados</p>
                              </div>
                         }
                    </div>
               }
          </div>
     `,
     styles: [`
          .animate-fade-in {
               animation: fadeIn 0.5s ease-out;
          }
          @keyframes fadeIn {
               from { opacity: 0; transform: translateY(20px); }
               to { opacity: 1; transform: translateY(0); }
          }
     `]
})
export class ClientDashboardComponent implements OnInit {
     private http = inject(HttpClient);
     private authService = inject(AuthService);

     loading = signal(true);
     userName = signal('');
     organization = signal<any>(null);
     waterBoxes = signal<any[]>([]);
     recentPayments = signal<any[]>([]);
     totalSupplies = signal(0);
     totalPayments = signal(0);
     pendingMonths = signal(0);

     ngOnInit(): void {
          this.loadUserData();
          this.loadDashboardData();
     }

     private loadUserData(): void {
          const user = this.authService.user();
          if (user) {
               this.userName.set(`${user.firstName} ${user.lastName}`);
          }
     }

     private loadDashboardData(): void {
          const user = this.authService.user();
          if (!user) return;

          this.http.get<any>(`${environment.apiUrl}/organizations/${user.organizationId}`)
               .subscribe({
                    next: (response) => {
                         this.organization.set(response.data);
                    }
               });

          this.http.get<any>(`${environment.apiUrl}/water-boxes/user/${user.id}`)
               .subscribe({
                    next: (response) => {
                         const boxes = response.data || [];
                         this.waterBoxes.set(boxes);
                         this.totalSupplies.set(boxes.length);
                    }
               });

          this.http.get<any>(`${environment.apiUrl}/payments/user/${user.id}?size=5`)
               .subscribe({
                    next: (response) => {
                         const payments = response.data?.content || response.data || [];
                         this.recentPayments.set(payments);
                         this.totalPayments.set(response.data?.totalElements || payments.length);
                         this.loading.set(false);
                    },
                    error: () => {
                         this.loading.set(false);
                    }
               });
     }
}
