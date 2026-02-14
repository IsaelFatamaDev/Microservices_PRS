import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, FileText, Search, ChevronLeft, ChevronRight, Eye,
  X, Download, Calendar, Filter, DollarSign
} from 'lucide-angular';
import { jsPDF } from 'jspdf';
import { environment } from '../../../../../environments/environment';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { CommercialService } from '../../../../core/services/commercial.service';
import { ApiResponse, User, WaterBox, WaterBoxAssignment } from '../../../../core/models';
import { Receipt } from '../../../../core/models/commercial.model';

@Component({
  selector: 'app-receipts',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Recibos</h1>
          <p class="text-sm text-gray-500 mt-1">Gestión y consulta de recibos de consumo</p>
        </div>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Total</span>
            <div class="p-2 rounded-xl bg-violet-50"><lucide-icon [img]="fileTextIcon" [size]="16" class="text-violet-600"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-gray-800">{{ allReceipts().length }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Pagados</span>
            <div class="p-2 rounded-xl bg-emerald-50"><lucide-icon [img]="dollarIcon" [size]="16" class="text-emerald-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-emerald-600">{{ paidCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Pendientes</span>
            <div class="p-2 rounded-xl bg-amber-50"><lucide-icon [img]="dollarIcon" [size]="16" class="text-amber-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-amber-600">{{ pendingCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Vencidos</span>
            <div class="p-2 rounded-xl bg-red-50"><lucide-icon [img]="dollarIcon" [size]="16" class="text-red-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-red-600">{{ overdueCount() }}</p>
        </div>
      </div>

      <!-- Tabla -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center gap-3">
          <div class="relative flex-1">
            <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
            <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar por usuario o N° recibo..."
                   class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-300">
          </div>
          <select [(ngModel)]="statusFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todos</option>
            <option value="PAID">Pagado</option>
            <option value="PENDING">Pendiente</option>
            <option value="OVERDUE">Vencido</option>
            <option value="PARTIAL">Parcial</option>
            <option value="CANCELLED">Cancelado</option>
          </select>
        </div>

        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50/80">
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">N° Recibo</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Usuario</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Período</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Emisión</th>
                <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Total</th>
                <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Pagado</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (r of paginatedReceipts(); track r.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3 text-sm font-medium text-violet-700">{{ r.receiptNumber }}</td>
                  <td class="px-4 py-3 text-sm text-gray-800">{{ getUserName(r.userId) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ getMonthLabel(r.periodMonth) }} {{ r.periodYear }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ r.issueDate | date:'dd/MM/yyyy' }}</td>
                  <td class="px-4 py-3 text-sm font-medium text-gray-800 text-right">S/ {{ r.totalAmount.toFixed(2) }}</td>
                  <td class="px-4 py-3 text-sm text-right" [class]="r.paidAmount >= r.totalAmount ? 'text-emerald-600 font-medium' : 'text-gray-600'">
                    S/ {{ r.paidAmount.toFixed(2) }}
                  </td>
                  <td class="px-4 py-3">
                    <span [class]="getStatusBadge(r.receiptStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ getStatusLabel(r.receiptStatus) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="viewDetail(r)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-violet-600 transition-colors" title="Ver detalle">
                        <lucide-icon [img]="eyeIcon" [size]="15"></lucide-icon>
                      </button>
                      <button (click)="downloadReceipt(r)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-violet-600 transition-colors" title="Descargar">
                        <lucide-icon [img]="downloadIcon" [size]="15"></lucide-icon>
                      </button>
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="8" class="text-center py-12 text-gray-400 text-sm">No se encontraron recibos</td></tr>
              }
            </tbody>
          </table>
        </div>

        <div class="md:hidden divide-y divide-gray-50">
          @for (r of paginatedReceipts(); track r.id) {
            <div class="p-4 space-y-2">
              <div class="flex items-center justify-between">
                <span class="text-sm font-medium text-violet-700">{{ r.receiptNumber }}</span>
                <span [class]="getStatusBadge(r.receiptStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">
                  {{ getStatusLabel(r.receiptStatus) }}
                </span>
              </div>
              <p class="text-sm text-gray-800">{{ getUserName(r.userId) }}</p>
              <div class="flex items-center justify-between text-xs text-gray-400">
                <span>{{ getMonthLabel(r.periodMonth) }} {{ r.periodYear }} · {{ r.issueDate | date:'dd/MM/yyyy' }}</span>
                <span class="text-sm font-bold text-gray-800">S/ {{ r.totalAmount.toFixed(2) }}</span>
              </div>
              <div class="flex gap-2 pt-1">
                <button (click)="viewDetail(r)" class="flex-1 py-1.5 text-xs border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 transition-colors">Ver detalle</button>
                <button (click)="downloadReceipt(r)" class="flex-1 py-1.5 text-xs border border-violet-200 rounded-lg text-violet-600 hover:bg-violet-50 transition-colors">Descargar</button>
              </div>
            </div>
          } @empty {
            <div class="p-8 text-center text-gray-400 text-sm">No se encontraron recibos</div>
          }
        </div>

        @if (filteredReceipts().length > pageSize) {
          <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
            <span>{{ (currentPage() - 1) * pageSize + 1 }} - {{ Math.min(currentPage() * pageSize, filteredReceipts().length) }} de {{ filteredReceipts().length }}</span>
            <div class="flex gap-1">
              <button (click)="currentPage.set(currentPage() - 1)" [disabled]="currentPage() <= 1"
                      class="p-1.5 rounded-lg hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors">
                <lucide-icon [img]="chevronLeftIcon" [size]="16"></lucide-icon>
              </button>
              <button (click)="currentPage.set(currentPage() + 1)" [disabled]="currentPage() >= totalPages()"
                      class="p-1.5 rounded-lg hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors">
                <lucide-icon [img]="chevronRightIcon" [size]="16"></lucide-icon>
              </button>
            </div>
          </div>
        }
      </div>

      <!-- Modal Detalle -->
      @if (showDetail()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="showDetail.set(false)">
          <div class="bg-white rounded-2xl shadow-xl max-w-lg w-full max-h-[80vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Detalle del Recibo</h3>
              <button (click)="showDetail.set(false)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            @if (detailReceipt()) {
              <div class="p-6 space-y-4">
                <div class="grid grid-cols-2 gap-4">
                  <div><p class="text-xs text-gray-400">N° Recibo</p><p class="text-sm font-medium text-violet-700">{{ detailReceipt()!.receiptNumber }}</p></div>
                  <div><p class="text-xs text-gray-400">Período</p><p class="text-sm text-gray-700">{{ getMonthLabel(detailReceipt()!.periodMonth) }} {{ detailReceipt()!.periodYear }}</p></div>
                  <div><p class="text-xs text-gray-400">Usuario</p><p class="text-sm text-gray-700">{{ getUserName(detailReceipt()!.userId) }}</p></div>
                  <div><p class="text-xs text-gray-400">Emisión</p><p class="text-sm text-gray-700">{{ detailReceipt()!.issueDate | date:'dd/MM/yyyy' }}</p></div>
                  <div><p class="text-xs text-gray-400">Vencimiento</p><p class="text-sm text-gray-700">{{ detailReceipt()!.dueDate | date:'dd/MM/yyyy' }}</p></div>
                  <div><p class="text-xs text-gray-400">Estado</p>
                    <span [class]="getStatusBadge(detailReceipt()!.receiptStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">{{ getStatusLabel(detailReceipt()!.receiptStatus) }}</span>
                  </div>
                </div>
                @if (detailReceipt()!.details && detailReceipt()!.details!.length > 0) {
                  <div class="border-t border-gray-100 pt-4">
                    <h4 class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">Conceptos</h4>
                    <div class="space-y-2">
                      @for (d of detailReceipt()!.details!; track d.id) {
                        <div class="flex items-center justify-between py-2 px-3 bg-gray-50 rounded-lg">
                          <p class="text-sm text-gray-700">{{ d.description }}</p>
                          <span class="text-sm font-medium text-gray-800">S/ {{ d.amount.toFixed(2) }}</span>
                        </div>
                      }
                    </div>
                  </div>
                }
                <div class="bg-violet-50 rounded-xl p-4">
                  <div class="flex justify-between text-sm mb-1">
                    <span class="text-gray-600">Total</span><span class="font-medium">S/ {{ detailReceipt()!.totalAmount.toFixed(2) }}</span>
                  </div>
                  <div class="flex justify-between text-sm mb-1">
                    <span class="text-gray-600">Pagado</span><span class="font-medium text-emerald-600">S/ {{ detailReceipt()!.paidAmount.toFixed(2) }}</span>
                  </div>
                  <div class="flex justify-between text-base font-bold pt-2 border-t border-violet-200">
                    <span class="text-gray-800">Pendiente</span>
                    <span [class]="detailReceipt()!.pendingAmount > 0 ? 'text-red-600' : 'text-emerald-600'">S/ {{ detailReceipt()!.pendingAmount.toFixed(2) }}</span>
                  </div>
                </div>
                @if (detailReceipt()!.notes) {
                  <div class="bg-gray-50 rounded-xl p-3">
                    <p class="text-xs text-gray-400 mb-1">Notas</p>
                    <p class="text-sm text-gray-600">{{ detailReceipt()!.notes }}</p>
                  </div>
                }
              </div>
            }
          </div>
        </div>
      }
    </div>
  `
})
export class ReceiptsComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private commercialService = inject(CommercialService);

  fileTextIcon = FileText; searchIcon = Search; chevronLeftIcon = ChevronLeft;
  chevronRightIcon = ChevronRight; eyeIcon = Eye; xIcon = X; downloadIcon = Download;
  calendarIcon = Calendar; filterIcon = Filter; dollarIcon = DollarSign;

  Math = Math;

  allReceipts = signal<Receipt[]>([]);
  allUsers = signal<User[]>([]);
  searchTerm = '';
  statusFilter = '';
  currentPage = signal(1);
  pageSize = 15;

  showDetail = signal(false);
  detailReceipt = signal<Receipt | null>(null);

  paidCount = computed(() => this.allReceipts().filter(r => r.receiptStatus === 'PAID').length);
  pendingCount = computed(() => this.allReceipts().filter(r => r.receiptStatus === 'PENDING' || r.receiptStatus === 'PARTIAL').length);
  overdueCount = computed(() => this.allReceipts().filter(r => r.receiptStatus === 'OVERDUE').length);

  filteredReceipts = computed(() => {
    let list = this.allReceipts();
    if (this.statusFilter) list = list.filter(r => r.receiptStatus === this.statusFilter);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(r =>
        this.getUserName(r.userId).toLowerCase().includes(q) ||
        r.receiptNumber.toLowerCase().includes(q)
      );
    }
    return list.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredReceipts().length / this.pageSize));
  paginatedReceipts = computed(() => {
    const s = (this.currentPage() - 1) * this.pageSize;
    return this.filteredReceipts().slice(s, s + this.pageSize);
  });

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.http.get<ApiResponse<User[]>>(`${environment.apiUrl}/users`).subscribe({
      next: r => this.allUsers.set((r.data || []).filter(u => u.organizationId === orgId))
    });

    this.commercialService.getReceipts().subscribe({
      next: r => this.allReceipts.set((r.data || []).filter(rc => rc.organizationId === orgId && rc.recordStatus === 'ACTIVE'))
    });
  }

  viewDetail(receipt: Receipt): void {
    if (receipt.details && receipt.details.length > 0) {
      this.detailReceipt.set(receipt);
      this.showDetail.set(true);
    } else {
      this.commercialService.getReceipt(receipt.id).subscribe({
        next: r => {
          this.detailReceipt.set(r.data || receipt);
          this.showDetail.set(true);
        },
        error: () => {
          this.detailReceipt.set(receipt);
          this.showDetail.set(true);
        }
      });
    }
  }

  downloadReceipt(receipt: Receipt): void {
    const doc = new jsPDF({ format: [80, 200] });
    const org = this.authService.organization();
    const pw = 80;
    const m = 5;
    const dark = [15, 23, 42] as const;
    const mid = [71, 85, 105] as const;
    const light = [120, 130, 150] as const;
    let y = 8;

    doc.setFontSize(8);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...dark);
    doc.text(org?.organizationName || 'JASS', pw / 2, y, { align: 'center' });
    y += 3.5;
    doc.setFontSize(5.5);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(...light);
    const loc = [org?.district, org?.province, org?.department].filter(Boolean).join(', ');
    if (loc) { doc.text(loc, pw / 2, y, { align: 'center' }); y += 3; }

    doc.setDrawColor(200, 200, 200);
    doc.setLineDashPattern([1, 1], 0);
    doc.line(m, y, pw - m, y);
    y += 4;

    doc.setFontSize(7);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...dark);
    doc.text('RECIBO DE CONSUMO', pw / 2, y, { align: 'center' });
    y += 4;

    doc.setFontSize(5.5);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(...mid);
    doc.text(`N\u00b0: ${receipt.receiptNumber}`, m, y);
    doc.text(`Emisi\u00f3n: ${new Date(receipt.issueDate).toLocaleDateString('es-PE')}`, pw - m, y, { align: 'right' });
    y += 3.5;
    doc.text(`Per\u00edodo: ${this.getMonthLabel(receipt.periodMonth)} ${receipt.periodYear}`, m, y);
    doc.text(`Vence: ${new Date(receipt.dueDate).toLocaleDateString('es-PE')}`, pw - m, y, { align: 'right' });
    y += 4;

    doc.setLineDashPattern([1, 1], 0);
    doc.line(m, y, pw - m, y);
    y += 4;

    doc.setTextColor(...mid);
    doc.text('Cliente:', m, y);
    doc.setTextColor(...dark);
    doc.setFont('helvetica', 'bold');
    doc.text(this.getUserName(receipt.userId), m + 14, y);
    y += 5;

    doc.setFont('helvetica', 'normal');
    doc.setLineDashPattern([1, 1], 0);
    doc.line(m, y, pw - m, y);
    y += 3;

    if (receipt.details && receipt.details.length > 0) {
      for (const d of receipt.details) {
        doc.setTextColor(...mid);
        doc.text(d.description, m, y);
        doc.setTextColor(...dark);
        doc.text(`S/ ${d.amount.toFixed(2)}`, pw - m, y, { align: 'right' });
        y += 3.5;
      }
    }

    y += 1;
    doc.setLineWidth(0.5);
    doc.setLineDashPattern([], 0);
    doc.line(m, y, pw - m, y);
    y += 4;

    doc.setFontSize(7);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...dark);
    doc.text('TOTAL:', m, y);
    doc.text(`S/ ${receipt.totalAmount.toFixed(2)}`, pw - m, y, { align: 'right' });
    y += 6;

    doc.setFontSize(5);
    doc.setTextColor(160, 160, 160);
    doc.text(org?.organizationName || '', pw / 2, y, { align: 'center' });

    (doc.internal.pageSize as any).setHeight(y + 8);
    doc.save(`Recibo_${receipt.receiptNumber}_${receipt.periodYear}.pdf`);
  }

  getUserName(userId: string): string {
    const u = this.allUsers().find(x => x.id === userId);
    return u ? `${u.lastName}, ${u.firstName}` : userId;
  }

  getMonthLabel(m: number): string {
    const months = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    return months[m - 1] || '';
  }

  getStatusLabel(s: string): string {
    const map: Record<string, string> = { PENDING: 'Pendiente', PARTIAL: 'Parcial', PAID: 'Pagado', OVERDUE: 'Vencido', CANCELLED: 'Cancelado' };
    return map[s] || s;
  }

  getStatusBadge(s: string): string {
    const map: Record<string, string> = {
      PAID: 'bg-emerald-50 text-emerald-700',
      PENDING: 'bg-amber-50 text-amber-700',
      PARTIAL: 'bg-blue-50 text-blue-700',
      OVERDUE: 'bg-red-50 text-red-600',
      CANCELLED: 'bg-gray-100 text-gray-500'
    };
    return map[s] || 'bg-gray-100 text-gray-600';
  }
}
