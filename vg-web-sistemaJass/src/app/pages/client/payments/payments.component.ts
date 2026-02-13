import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
     selector: 'app-client-payments',
     standalone: true,
     imports: [CommonModule, FormsModule],
     template: `
          <div class="animate-fade-in">
               <div class="mb-6">
                    <h1 class="text-2xl font-bold text-gray-800">Mis Pagos</h1>
                    <p class="text-gray-600 mt-1">Historial de todos tus pagos realizados</p>
               </div>

               <div class="bg-white rounded-xl shadow-lg overflow-hidden">
                    <div class="p-4 border-b bg-gray-50 flex flex-col md:flex-row gap-4 justify-between items-center">
                         <div class="flex gap-4 flex-wrap">
                              <select
                                   [(ngModel)]="selectedYear"
                                   (ngModelChange)="loadPayments()"
                                   class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                                   <option value="">Todos los años</option>
                                   @for (year of years; track year) {
                                        <option [value]="year">{{ year }}</option>
                                   }
                              </select>
                              <select
                                   [(ngModel)]="selectedMonth"
                                   (ngModelChange)="loadPayments()"
                                   class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                                   <option value="">Todos los meses</option>
                                   @for (month of months; track month.value) {
                                        <option [value]="month.value">{{ month.label }}</option>
                                   }
                              </select>
                         </div>
                         <div class="flex gap-2">
                              <button
                                   (click)="exportToPDF()"
                                   class="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
                                   <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                                   </svg>
                                   Exportar PDF
                              </button>
                         </div>
                    </div>

                    @if (loading()) {
                         <div class="flex justify-center items-center h-64">
                              <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                         </div>
                    } @else if (payments().length === 0) {
                         <div class="text-center py-16 text-gray-500">
                              <svg class="w-16 h-16 mx-auto mb-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                              </svg>
                              <p class="text-lg">No se encontraron pagos</p>
                         </div>
                    } @else {
                         <div class="overflow-x-auto">
                              <table class="w-full">
                                   <thead class="bg-gray-50">
                                        <tr>
                                             <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
                                             <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Recibo</th>
                                             <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Suministro</th>
                                             <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Concepto</th>
                                             <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Meses</th>
                                             <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Monto</th>
                                             <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Acciones</th>
                                        </tr>
                                   </thead>
                                   <tbody class="divide-y divide-gray-200">
                                        @for (payment of payments(); track payment.id) {
                                             <tr class="hover:bg-gray-50">
                                                  <td class="px-4 py-3 text-sm text-gray-600">{{ payment.paymentDate | date:'dd/MM/yyyy' }}</td>
                                                  <td class="px-4 py-3 text-sm font-medium text-gray-800">{{ payment.receiptNumber }}</td>
                                                  <td class="px-4 py-3 text-sm text-gray-600">{{ payment.supplyNumber }}</td>
                                                  <td class="px-4 py-3 text-sm text-gray-600">{{ payment.paymentConcept }}</td>
                                                  <td class="px-4 py-3 text-sm text-gray-600">{{ payment.monthsPaid || '-' }}</td>
                                                  <td class="px-4 py-3 text-sm text-right font-semibold text-green-600">S/ {{ payment.amount | number:'1.2-2' }}</td>
                                                  <td class="px-4 py-3 text-center">
                                                       <button
                                                            (click)="downloadReceipt(payment)"
                                                            class="text-blue-600 hover:text-blue-800 transition"
                                                            title="Descargar recibo">
                                                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                                                            </svg>
                                                       </button>
                                                  </td>
                                             </tr>
                                        }
                                   </tbody>
                              </table>
                         </div>

                         <div class="p-4 border-t bg-gray-50 flex justify-between items-center">
                              <p class="text-sm text-gray-600">
                                   Mostrando {{ payments().length }} de {{ totalElements() }} registros
                              </p>
                              <div class="flex gap-2">
                                   <button
                                        (click)="previousPage()"
                                        [disabled]="currentPage() === 0"
                                        class="px-3 py-1 border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100">
                                        Anterior
                                   </button>
                                   <span class="px-3 py-1">{{ currentPage() + 1 }} / {{ totalPages() }}</span>
                                   <button
                                        (click)="nextPage()"
                                        [disabled]="currentPage() >= totalPages() - 1"
                                        class="px-3 py-1 border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100">
                                        Siguiente
                                   </button>
                              </div>
                         </div>
                    }
               </div>
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
export class ClientPaymentsComponent implements OnInit {
     private http = inject(HttpClient);
     private authService = inject(AuthService);

     loading = signal(true);
     payments = signal<any[]>([]);
     currentPage = signal(0);
     totalPages = signal(0);
     totalElements = signal(0);
     organization = signal<any>(null);

     selectedYear = '';
     selectedMonth = '';
     years: number[] = [];
     months = [
          { value: '01', label: 'Enero' },
          { value: '02', label: 'Febrero' },
          { value: '03', label: 'Marzo' },
          { value: '04', label: 'Abril' },
          { value: '05', label: 'Mayo' },
          { value: '06', label: 'Junio' },
          { value: '07', label: 'Julio' },
          { value: '08', label: 'Agosto' },
          { value: '09', label: 'Septiembre' },
          { value: '10', label: 'Octubre' },
          { value: '11', label: 'Noviembre' },
          { value: '12', label: 'Diciembre' }
     ];

     ngOnInit(): void {
          const currentYear = new Date().getFullYear();
          for (let y = currentYear; y >= currentYear - 5; y--) {
               this.years.push(y);
          }
          this.loadOrganization();
          this.loadPayments();
     }

     private loadOrganization(): void {
          const user = this.authService.user();
          if (user?.organizationId) {
               this.http.get<any>(`${environment.apiUrl}/organizations/${user.organizationId}`)
                    .subscribe({
                         next: (response) => {
                              this.organization.set(response.data);
                         }
                    });
          }
     }

     loadPayments(): void {
          this.loading.set(true);
          const user = this.authService.user();
          if (!user) return;

          let url = `${environment.apiUrl}/payments/user/${user.id}?page=${this.currentPage()}&size=10`;

          if (this.selectedYear) {
               url += `&year=${this.selectedYear}`;
          }
          if (this.selectedMonth) {
               url += `&month=${this.selectedMonth}`;
          }

          this.http.get<any>(url)
               .subscribe({
                    next: (response) => {
                         this.payments.set(response.data?.content || response.data || []);
                         this.totalPages.set(response.data?.totalPages || 1);
                         this.totalElements.set(response.data?.totalElements || this.payments().length);
                         this.loading.set(false);
                    },
                    error: () => {
                         this.loading.set(false);
                    }
               });
     }

     previousPage(): void {
          if (this.currentPage() > 0) {
               this.currentPage.update(v => v - 1);
               this.loadPayments();
          }
     }

     nextPage(): void {
          if (this.currentPage() < this.totalPages() - 1) {
               this.currentPage.update(v => v + 1);
               this.loadPayments();
          }
     }

     downloadReceipt(payment: any): void {
          const doc = new jsPDF();
          const org = this.organization();
          const user = this.authService.user();

          doc.setFontSize(18);
          doc.setFont('helvetica', 'bold');
          doc.text(org?.organizationName || 'JASS', 105, 20, { align: 'center' });

          doc.setFontSize(10);
          doc.setFont('helvetica', 'normal');
          doc.text(`${org?.district || ''}, ${org?.province || ''} - ${org?.department || ''}`, 105, 28, { align: 'center' });
          doc.text(`Dirección: ${org?.address || ''}`, 105, 34, { align: 'center' });

          doc.setFontSize(14);
          doc.setFont('helvetica', 'bold');
          doc.text('RECIBO DE PAGO', 105, 50, { align: 'center' });
          doc.text(`N° ${payment.receiptNumber}`, 105, 58, { align: 'center' });

          doc.setFontSize(10);
          doc.setFont('helvetica', 'normal');

          const startY = 70;
          doc.text(`Cliente: ${user?.firstName} ${user?.lastName}`, 20, startY);
          doc.text(`DNI: ${user?.documentNumber}`, 20, startY + 7);
          doc.text(`Dirección: ${user?.address || ''}`, 20, startY + 14);
          doc.text(`N° Suministro: ${payment.supplyNumber}`, 20, startY + 21);

          doc.text(`Fecha: ${new Date(payment.paymentDate).toLocaleDateString('es-PE')}`, 130, startY);

          autoTable(doc, {
               startY: startY + 35,
               head: [['Concepto', 'Meses', 'Monto']],
               body: [
                    [payment.paymentConcept, payment.monthsPaid || '-', `S/ ${payment.amount.toFixed(2)}`]
               ],
               foot: [['', 'TOTAL:', `S/ ${payment.amount.toFixed(2)}`]],
               theme: 'grid',
               headStyles: { fillColor: [59, 130, 246] },
               footStyles: { fillColor: [229, 231, 235], textColor: [0, 0, 0], fontStyle: 'bold' }
          });

          const finalY = (doc as any).lastAutoTable.finalY + 20;
          doc.setFontSize(9);
          doc.text('Este documento es comprobante oficial de pago.', 105, finalY, { align: 'center' });
          doc.text('Conserve este recibo para cualquier reclamo.', 105, finalY + 6, { align: 'center' });

          doc.save(`recibo_${payment.receiptNumber}.pdf`);
     }

     exportToPDF(): void {
          const doc = new jsPDF();
          const org = this.organization();
          const user = this.authService.user();

          doc.setFontSize(16);
          doc.setFont('helvetica', 'bold');
          doc.text(org?.organizationName || 'JASS', 105, 15, { align: 'center' });

          doc.setFontSize(12);
          doc.text('HISTORIAL DE PAGOS', 105, 25, { align: 'center' });

          doc.setFontSize(10);
          doc.setFont('helvetica', 'normal');
          doc.text(`Cliente: ${user?.firstName} ${user?.lastName}`, 20, 38);
          doc.text(`Fecha de generación: ${new Date().toLocaleDateString('es-PE')}`, 20, 45);

          const tableData = this.payments().map((p, i) => [
               i + 1,
               new Date(p.paymentDate).toLocaleDateString('es-PE'),
               p.receiptNumber,
               p.supplyNumber,
               p.paymentConcept,
               `S/ ${p.amount.toFixed(2)}`
          ]);

          autoTable(doc, {
               startY: 55,
               head: [['#', 'Fecha', 'N° Recibo', 'Suministro', 'Concepto', 'Monto']],
               body: tableData,
               theme: 'grid',
               headStyles: { fillColor: [59, 130, 246] },
               styles: { fontSize: 8 }
          });

          const total = this.payments().reduce((sum, p) => sum + p.amount, 0);
          const finalY = (doc as any).lastAutoTable.finalY + 10;
          doc.setFont('helvetica', 'bold');
          doc.text(`Total: S/ ${total.toFixed(2)}`, 190, finalY, { align: 'right' });

          doc.save('historial_pagos.pdf');
     }
}
