import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, Search, CreditCard, FileText, DollarSign, Calendar,
  ChevronLeft, ChevronRight, Download, Eye, X, Check, User as UserIcon,
  Droplets, MapPin, Filter, Printer, CheckCircle, AlertCircle, Clock
} from 'lucide-angular';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { environment } from '../../../../../environments/environment';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { CommercialService } from '../../../../core/services/commercial.service';
import { ApiResponse, User, WaterBox, WaterBoxAssignment } from '../../../../core/models';
import {
  Payment, CreatePaymentRequest, CreateReceiptRequest, Debt, PaymentMethod, ConceptType
} from '../../../../core/models/commercial.model';
import { Fare } from '../../../../core/models/organization.model';

interface MonthCell {
  month: number;
  year: number;
  label: string;
  status: 'paid' | 'pending' | 'selected' | 'future' | 'overdue';
  debtId?: string;
  amount?: number;
}

interface ExtraFare {
  fareId: string;
  fareType: string;
  description: string;
  amount: number;
}

interface UserWithBox {
  user: User;
  box?: WaterBox;
  assignment?: WaterBoxAssignment;
}

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Gestión de Pagos</h1>
          <p class="text-sm text-gray-500 mt-1">Registra pagos de cuotas mensuales y otros conceptos</p>
        </div>
        <div class="flex items-center gap-3">
          <button (click)="showPaymentHistory = !showPaymentHistory"
                  [class]="showPaymentHistory ? 'bg-violet-600 text-white' : 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-50'"
                  class="px-4 py-2 rounded-xl text-sm font-medium transition-all flex items-center gap-2">
            <lucide-icon [img]="clockIcon" [size]="16"></lucide-icon>
            Historial
          </button>
        </div>
      </div>

      @if (!showPaymentHistory) {
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <lucide-icon [img]="searchIcon" [size]="20" class="text-violet-600"></lucide-icon>
            Buscar Usuario
          </h2>
          <div class="flex flex-col sm:flex-row gap-3">
            <div class="relative flex-1">
              <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
              <input type="text" [(ngModel)]="searchTerm" (keyup.enter)="searchUsers()"
                     placeholder="Buscar por nombre, apellido o DNI..."
                     class="w-full pl-10 pr-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-300">
            </div>
            <button (click)="searchUsers()" [disabled]="!searchTerm.trim()"
                    class="px-6 py-2.5 bg-violet-600 text-white rounded-xl text-sm font-medium hover:bg-violet-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed">
              Buscar
            </button>
          </div>

          @if (searchResults().length > 0 && !selectedUserBox()) {
            <div class="mt-4 space-y-2 max-h-60 overflow-y-auto">
              @for (ub of searchResults(); track ub.user.id) {
                <button (click)="selectUser(ub)"
                        class="w-full flex items-center gap-3 p-3 rounded-xl border border-gray-100 hover:bg-violet-50 hover:border-violet-200 transition-all text-left">
                  <div class="w-10 h-10 rounded-full bg-violet-100 flex items-center justify-center text-violet-700 font-bold text-sm shrink-0">
                    {{ ub.user.lastName.charAt(0) }}{{ ub.user.firstName.charAt(0) }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-800 truncate">{{ ub.user.lastName }}, {{ ub.user.firstName }}</p>
                    <p class="text-xs text-gray-400">DNI: {{ ub.user.documentNumber }} · {{ ub.box?.boxCode || 'Sin caja' }}</p>
                  </div>
                  @if (ub.box) {
                    <span class="px-2 py-1 rounded-lg bg-emerald-50 text-emerald-700 text-xs font-medium">{{ ub.box.boxCode }}</span>
                  }
                </button>
              }
            </div>
          }
        </div>

        @if (selectedUserBox()) {
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-sm font-semibold text-gray-800 flex items-center gap-2">
                  <lucide-icon [img]="userIcon" [size]="16" class="text-violet-600"></lucide-icon>
                  Datos del Usuario
                </h3>
                <button (click)="clearSelection()" class="text-gray-400 hover:text-gray-600 transition-colors">
                  <lucide-icon [img]="xIcon" [size]="16"></lucide-icon>
                </button>
              </div>
              <div class="space-y-3">
                <div>
                  <p class="text-xs text-gray-400">Nombre completo</p>
                  <p class="text-sm font-medium text-gray-800">{{ selectedUserBox()!.user.lastName }}, {{ selectedUserBox()!.user.firstName }}</p>
                </div>
                <div class="grid grid-cols-2 gap-3">
                  <div>
                    <p class="text-xs text-gray-400">DNI</p>
                    <p class="text-sm text-gray-700">{{ selectedUserBox()!.user.documentNumber }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-400">Teléfono</p>
                    <p class="text-sm text-gray-700">{{ selectedUserBox()!.user.phone || '-' }}</p>
                  </div>
                </div>
                <div>
                  <p class="text-xs text-gray-400">Dirección</p>
                  <p class="text-sm text-gray-700">{{ selectedUserBox()!.user.address || '-' }}</p>
                </div>
              </div>
              @if (selectedUserBox()!.box) {
                <div class="mt-4 pt-4 border-t border-gray-100">
                  <h4 class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">Caja de Agua</h4>
                  <div class="space-y-2">
                    <div class="flex justify-between">
                      <span class="text-xs text-gray-400">N° Suministro</span>
                      <span class="text-sm font-medium text-violet-700">{{ selectedUserBox()!.box!.boxCode }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-xs text-gray-400">Tipo</span>
                      <span class="text-xs px-2 py-0.5 rounded bg-gray-100 text-gray-600">{{ getBoxTypeLabel(selectedUserBox()!.box!.boxType) }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-xs text-gray-400">Dirección</span>
                      <span class="text-xs text-gray-600">{{ selectedUserBox()!.box!.address || '-' }}</span>
                    </div>
                  </div>
                </div>
              }
              @if (monthlyFee() > 0) {
                <div class="mt-4 pt-3 border-t border-gray-100">
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-400">Tarifa mensual</span>
                    <span class="text-lg font-bold text-violet-700">S/ {{ monthlyFee().toFixed(2) }}</span>
                  </div>
                </div>
              }
            </div>

            <div class="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-sm font-semibold text-gray-800 flex items-center gap-2">
                  <lucide-icon [img]="calendarIcon" [size]="16" class="text-violet-600"></lucide-icon>
                  Estado de Pagos - {{ selectedYear() }}
                </h3>
                <div class="flex items-center gap-2">
                  <button (click)="changeYear(-1)" class="p-1.5 rounded-lg hover:bg-gray-100 transition-colors text-gray-500">
                    <lucide-icon [img]="chevronLeftIcon" [size]="16"></lucide-icon>
                  </button>
                  <span class="text-sm font-medium text-gray-700 min-w-[50px] text-center">{{ selectedYear() }}</span>
                  <button (click)="changeYear(1)" class="p-1.5 rounded-lg hover:bg-gray-100 transition-colors text-gray-500">
                    <lucide-icon [img]="chevronRightIcon" [size]="16"></lucide-icon>
                  </button>
                </div>
              </div>

              <div class="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-6 gap-2 mb-6">
                @for (cell of monthCells(); track cell.month + '-' + cell.year) {
                  <button (click)="toggleMonth(cell)"
                          [disabled]="cell.status === 'paid'"
                          [class]="getMonthCellClass(cell)"
                          class="p-3 rounded-xl text-center transition-all border relative">
                    <p class="text-xs font-medium">{{ cell.label }}</p>
                    @if (cell.status === 'paid') {
                      <lucide-icon [img]="checkIcon" [size]="14" class="mx-auto mt-1 text-emerald-600"></lucide-icon>
                    } @else if (cell.status === 'selected') {
                      <lucide-icon [img]="checkCircleIcon" [size]="14" class="mx-auto mt-1 text-violet-600"></lucide-icon>
                    } @else if (cell.status === 'overdue') {
                      <lucide-icon [img]="alertIcon" [size]="14" class="mx-auto mt-1 text-orange-500"></lucide-icon>
                    } @else if (cell.status === 'pending') {
                      <lucide-icon [img]="clockIcon" [size]="14" class="mx-auto mt-1 text-amber-400"></lucide-icon>
                    } @else {
                      <lucide-icon [img]="clockIcon" [size]="14" class="mx-auto mt-1 text-gray-300"></lucide-icon>
                    }
                  </button>
                }
              </div>

              <div class="flex flex-wrap gap-4 text-xs text-gray-500 mb-6">
                <span class="flex items-center gap-1.5"><span class="w-3 h-3 rounded bg-emerald-100 border border-emerald-300"></span> Pagado</span>
                <span class="flex items-center gap-1.5"><span class="w-3 h-3 rounded bg-orange-100 border border-orange-300"></span> Mora</span>
                <span class="flex items-center gap-1.5"><span class="w-3 h-3 rounded bg-amber-50 border border-amber-300"></span> Pendiente</span>
                <span class="flex items-center gap-1.5"><span class="w-3 h-3 rounded bg-violet-100 border border-violet-300"></span> Seleccionado</span>
                <span class="flex items-center gap-1.5"><span class="w-3 h-3 rounded bg-gray-50 border border-gray-200"></span> Futuro</span>
              </div>

              @if (selectedMonths().length > 0) {
                <div class="border-t border-gray-100 pt-4 space-y-4">
                  <h4 class="text-sm font-semibold text-gray-700">Detalle del pago</h4>

                  <div class="space-y-3">
                    @if (extraFares().length > 0) {
                      <div>
                        <label class="text-xs text-gray-500 mb-1.5 block">Conceptos adicionales</label>
                        <div class="flex gap-2">
                          <select [(ngModel)]="currentExtraFareId" class="flex-1 px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none">
                            <option value="">Seleccionar concepto...</option>
                            @for (f of extraFares(); track f.id) {
                              <option [value]="f.id">{{ f.fareTypeDisplayName || f.fareType }} — S/ {{ f.amount.toFixed(2) }}</option>
                            }
                          </select>
                          <button (click)="addExtraFare()" [disabled]="!currentExtraFareId"
                                  class="px-4 py-2 bg-violet-600 text-white rounded-xl text-sm font-medium hover:bg-violet-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed whitespace-nowrap">
                            Agregar
                          </button>
                        </div>
                      </div>
                    }

                    @if (selectedExtraFares().length > 0) {
                      <div class="space-y-2">
                        @for (extra of selectedExtraFares(); track $index) {
                          <div class="flex items-center gap-3 bg-blue-50/60 rounded-xl px-3 py-2">
                            <lucide-icon [img]="checkCircleIcon" [size]="16" class="text-blue-600 shrink-0"></lucide-icon>
                            <div class="flex-1 min-w-0">
                              <p class="text-sm font-medium text-gray-800 truncate">{{ extra.description }}</p>
                            </div>
                            <span class="text-sm font-bold text-blue-700">S/ {{ extra.amount.toFixed(2) }}</span>
                            <button (click)="removeExtraFare($index)" class="p-1 hover:bg-red-100 rounded-lg transition-colors text-red-500">
                              <lucide-icon [img]="xIcon" [size]="14"></lucide-icon>
                            </button>
                          </div>
                        }
                      </div>
                    }

                    <div>
                      <label class="text-xs text-gray-500 mb-1 block">Método de pago</label>
                      <div class="grid grid-cols-3 gap-2">
                        @for (m of paymentMethods; track m.value) {
                          <button (click)="paymentMethod = m.value"
                                  [class]="paymentMethod === m.value ? 'bg-violet-50 border-violet-300 text-violet-700' : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-gray-100'"
                                  class="px-3 py-2 rounded-xl text-xs font-medium border transition-all text-center">
                            {{ m.label }}
                          </button>
                        }
                      </div>
                    </div>

                    <div>
                      <label class="text-xs text-gray-500 mb-1 block">Nota (opcional)</label>
                      <input type="text" [(ngModel)]="paymentNotes"
                             class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none"
                             placeholder="Observaciones del pago...">
                    </div>
                  </div>

                  <div class="bg-violet-50 rounded-xl p-4 space-y-2">
                    <div class="flex justify-between text-sm">
                      <span class="text-gray-600">Meses seleccionados ({{ selectedMonths().length }})</span>
                      <span class="font-medium">S/ {{ (selectedMonths().length * monthlyFee()).toFixed(2) }}</span>
                    </div>
                    @for (extra of selectedExtraFares(); track $index) {
                      <div class="flex justify-between text-sm">
                        <span class="text-gray-600">{{ extra.description }}</span>
                        <span class="font-medium">S/ {{ extra.amount.toFixed(2) }}</span>
                      </div>
                    }
                    <div class="flex justify-between text-base font-bold pt-2 border-t border-violet-200">
                      <span class="text-gray-800">Total a pagar</span>
                      <span class="text-violet-700">S/ {{ totalPayment().toFixed(2) }}</span>
                    </div>
                  </div>

                  <button (click)="processPayment()" [disabled]="isProcessing()"
                          class="w-full py-3 bg-violet-600 text-white rounded-xl font-medium hover:bg-violet-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2">
                    @if (isProcessing()) {
                      <div class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      Procesando...
                    } @else {
                      <lucide-icon [img]="checkIcon" [size]="18"></lucide-icon>
                      Registrar Pago
                    }
                  </button>
                </div>
              }
            </div>
          </div>
        }
      } @else {
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center gap-3">
            <div class="relative flex-1">
              <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
              <input type="text" [(ngModel)]="historySearch" placeholder="Buscar pagos..."
                     class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-300">
            </div>
            <select [(ngModel)]="historyStatusFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none">
              <option value="">Todos los estados</option>
              <option value="COMPLETED">Completado</option>
              <option value="PENDING">Pendiente</option>
              <option value="CANCELLED">Cancelado</option>
            </select>
          </div>

          <div class="hidden md:block overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50/80">
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">N° Recibo</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Usuario</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Fecha</th>
                  <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Monto</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Método</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
                  <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (p of paginatedPayments(); track p.id) {
                  <tr class="hover:bg-gray-50/50 transition-colors">
                    <td class="px-4 py-3 text-sm font-medium text-violet-700">{{ p.receiptNumber || '-' }}</td>
                    <td class="px-4 py-3">
                      <p class="text-sm text-gray-800">{{ getUserName(p.userId) }}</p>
                    </td>
                    <td class="px-4 py-3 text-sm text-gray-600">{{ p.paymentDate | date:'dd/MM/yyyy' }}</td>
                    <td class="px-4 py-3 text-sm font-semibold text-gray-800 text-right">S/ {{ p.totalAmount.toFixed(2) }}</td>
                    <td class="px-4 py-3">
                      <span class="text-xs px-2 py-0.5 rounded-lg bg-gray-100 text-gray-600">{{ getMethodLabel(p.paymentMethod) }}</span>
                    </td>
                    <td class="px-4 py-3">
                      <span [class]="getStatusBadge(p.paymentStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">
                        {{ getStatusLabel(p.paymentStatus) }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-center">
                      <div class="flex items-center justify-center gap-1">
                        <button (click)="viewPaymentDetail(p)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-violet-600 transition-colors" title="Ver detalle">
                          <lucide-icon [img]="eyeIcon" [size]="15"></lucide-icon>
                        </button>
                        <button (click)="downloadPaymentReceipt(p)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-violet-600 transition-colors" title="Descargar recibo">
                          <lucide-icon [img]="downloadIcon" [size]="15"></lucide-icon>
                        </button>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr><td colspan="7" class="text-center py-12 text-gray-400 text-sm">No se encontraron pagos</td></tr>
                }
              </tbody>
            </table>
          </div>

          <div class="md:hidden divide-y divide-gray-50">
            @for (p of paginatedPayments(); track p.id) {
              <div class="p-4 space-y-2">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-violet-700">{{ p.receiptNumber || '-' }}</span>
                  <span [class]="getStatusBadge(p.paymentStatus)" class="text-xs px-2 py-1 rounded-lg font-medium">
                    {{ getStatusLabel(p.paymentStatus) }}
                  </span>
                </div>
                <p class="text-sm text-gray-800">{{ getUserName(p.userId) }}</p>
                <div class="flex items-center justify-between">
                  <span class="text-xs text-gray-400">{{ p.paymentDate | date:'dd/MM/yyyy' }} · {{ getMethodLabel(p.paymentMethod) }}</span>
                  <span class="text-sm font-bold text-gray-800">S/ {{ p.totalAmount.toFixed(2) }}</span>
                </div>
                <div class="flex gap-2 pt-1">
                  <button (click)="viewPaymentDetail(p)" class="flex-1 py-1.5 text-xs border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 transition-colors">Ver detalle</button>
                  <button (click)="downloadPaymentReceipt(p)" class="flex-1 py-1.5 text-xs border border-violet-200 rounded-lg text-violet-600 hover:bg-violet-50 transition-colors">Recibo</button>
                </div>
              </div>
            } @empty {
              <div class="p-8 text-center text-gray-400 text-sm">No se encontraron pagos</div>
            }
          </div>

          @if (filteredPayments().length > pageSize) {
            <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
              <span>{{ startIndex() + 1 }} - {{ endIndex() }} de {{ filteredPayments().length }}</span>
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
      }

      @if (showDetail()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="showDetail.set(false)">
          <div class="bg-white rounded-2xl shadow-xl max-w-lg w-full max-h-[80vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Detalle del Pago</h3>
              <button (click)="showDetail.set(false)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            @if (detailPayment()) {
              <div class="p-6 space-y-4">
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <p class="text-xs text-gray-400">N° Recibo</p>
                    <p class="text-sm font-medium text-violet-700">{{ detailPayment()!.receiptNumber }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-400">Fecha</p>
                    <p class="text-sm text-gray-700">{{ detailPayment()!.paymentDate | date:'dd/MM/yyyy' }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-400">Usuario</p>
                    <p class="text-sm text-gray-700">{{ getUserName(detailPayment()!.userId) }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-400">Método</p>
                    <p class="text-sm text-gray-700">{{ getMethodLabel(detailPayment()!.paymentMethod) }}</p>
                  </div>
                </div>
                @if (detailPayment()!.details && detailPayment()!.details!.length > 0) {
                  <div class="border-t border-gray-100 pt-4">
                    <h4 class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">Conceptos</h4>
                    <div class="space-y-2">
                      @for (d of detailPayment()!.details!; track d.id) {
                        <div class="flex items-center justify-between py-2 px-3 bg-gray-50 rounded-lg">
                          <div>
                            <p class="text-sm text-gray-700">{{ d.description }}</p>
                            @if (d.periodDescription) {
                              <p class="text-xs text-gray-400">{{ d.periodDescription }}</p>
                            }
                          </div>
                          <span class="text-sm font-medium text-gray-800">S/ {{ d.amount.toFixed(2) }}</span>
                        </div>
                      }
                    </div>
                  </div>
                }
                <div class="bg-violet-50 rounded-xl p-4 flex justify-between items-center">
                  <span class="text-sm font-medium text-gray-700">Total pagado</span>
                  <span class="text-xl font-bold text-violet-700">S/ {{ detailPayment()!.totalAmount.toFixed(2) }}</span>
                </div>
                @if (detailPayment()!.notes) {
                  <div class="bg-gray-50 rounded-xl p-3">
                    <p class="text-xs text-gray-400 mb-1">Notas</p>
                    <p class="text-sm text-gray-600">{{ detailPayment()!.notes }}</p>
                  </div>
                }
                <button (click)="downloadPaymentReceipt(detailPayment()!)"
                        class="w-full py-2.5 border border-violet-200 text-violet-600 rounded-xl text-sm font-medium hover:bg-violet-50 transition-all flex items-center justify-center gap-2">
                  <lucide-icon [img]="downloadIcon" [size]="16"></lucide-icon>
                  Descargar Recibo PDF
                </button>
              </div>
            }
          </div>
        </div>
      }
    </div>
  `
})
export class PaymentsComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private commercialService = inject(CommercialService);

  searchIcon = Search; creditCardIcon = CreditCard; fileTextIcon = FileText; dollarIcon = DollarSign;
  calendarIcon = Calendar; chevronLeftIcon = ChevronLeft; chevronRightIcon = ChevronRight;
  downloadIcon = Download; eyeIcon = Eye; xIcon = X; checkIcon = Check; userIcon = UserIcon;
  dropletsIcon = Droplets; mapPinIcon = MapPin; filterIcon = Filter; printerIcon = Printer;
  checkCircleIcon = CheckCircle; alertIcon = AlertCircle; clockIcon = Clock;

  searchTerm = '';
  searchResults = signal<UserWithBox[]>([]);
  selectedUserBox = signal<UserWithBox | null>(null);
  selectedYear = signal(new Date().getFullYear());
  selectedMonths = signal<MonthCell[]>([]);
  userDebts = signal<Debt[]>([]);
  monthlyFee = signal(0);
  isProcessing = signal(false);
  showPaymentHistory = false;

  selectedExtraFares = signal<ExtraFare[]>([]);
  currentExtraFareId = '';
  paymentMethod: PaymentMethod = 'CASH';
  paymentNotes = '';

  allPayments = signal<Payment[]>([]);
  historySearch = '';
  historyStatusFilter = '';
  currentPage = signal(1);
  pageSize = 10;

  showDetail = signal(false);
  detailPayment = signal<Payment | null>(null);

  allUsers = signal<User[]>([]);
  allBoxes = signal<WaterBox[]>([]);
  allAssignments = signal<WaterBoxAssignment[]>([]);
  fares = signal<Fare[]>([]);

  paymentMethods = [
    { value: 'CASH' as PaymentMethod, label: 'Efectivo' },
    { value: 'YAPE' as PaymentMethod, label: 'Yape' },
    { value: 'PLIN' as PaymentMethod, label: 'Plin' }
  ];

  monthCells = computed<MonthCell[]>(() => {
    const year = this.selectedYear();
    const debts = this.userDebts();
    const selected = this.selectedMonths();
    const now = new Date();
    const currentMonth = now.getMonth() + 1;
    const currentYear = now.getFullYear();
    const months = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

    return months.map((label, i) => {
      const month = i + 1;
      const debt = debts.find(d => d.periodMonth === month && d.periodYear === year);
      const isSelected = selected.some(s => s.month === month && s.year === year);
      const isPaid = debt?.debtStatus === 'PAID';
      const isOverdue = !isPaid && (year < currentYear || (year === currentYear && month < currentMonth));

      let status: MonthCell['status'] = 'pending';
      if (isPaid) status = 'paid';
      else if (isSelected) status = 'selected';
      else if (isOverdue) status = 'overdue';

      return { month, year, label, status, debtId: debt?.id, amount: debt?.totalAmount || this.monthlyFee() };
    });
  });

  extraFares = computed(() => this.fares().filter(f => f.fareType !== 'CUOTA_MENSUAL' && f.recordStatus === 'ACTIVE'));

  totalPayment = computed(() => {
    const base = this.selectedMonths().length * this.monthlyFee();
    const extrasTotal = this.selectedExtraFares().reduce((sum, e) => sum + e.amount, 0);
    return base + extrasTotal;
  });

  filteredPayments = computed(() => {
    let list = this.allPayments();
    if (this.historyStatusFilter) list = list.filter(p => p.paymentStatus === this.historyStatusFilter);
    if (this.historySearch.trim()) {
      const q = this.historySearch.toLowerCase().trim();
      list = list.filter(p => {
        const name = this.getUserName(p.userId).toLowerCase();
        return name.includes(q) || (p.receiptNumber || '').toLowerCase().includes(q) || (p.userId || '').toLowerCase().includes(q);
      });
    }
    return list.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  });

  startIndex = computed(() => (this.currentPage() - 1) * this.pageSize);
  endIndex = computed(() => Math.min(this.currentPage() * this.pageSize, this.filteredPayments().length));
  totalPages = computed(() => Math.ceil(this.filteredPayments().length / this.pageSize));
  paginatedPayments = computed(() => this.filteredPayments().slice(this.startIndex(), this.startIndex() + this.pageSize));

  ngOnInit(): void {
    this.loadBaseData();
  }

  private loadBaseData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.http.get<ApiResponse<User[]>>(`${environment.apiUrl}/users`).subscribe({
      next: r => {
        const users = (r.data || []).filter(u => u.organizationId === orgId && u.role === 'CLIENT');
        this.allUsers.set(users);
      }
    });

    this.http.get<ApiResponse<WaterBox[]>>(`${environment.apiUrl}/water-boxes`).subscribe({
      next: r => this.allBoxes.set((r.data || []).filter(b => b.organizationId === orgId))
    });

    this.http.get<ApiResponse<WaterBoxAssignment[]>>(`${environment.apiUrl}/water-box-assignments`).subscribe({
      next: r => this.allAssignments.set((r.data || []).filter(a => a.organizationId === orgId && a.assignmentStatus === 'ACTIVE'))
    });

    this.http.get<ApiResponse<Fare[]>>(`${environment.apiUrl}/fares`).subscribe({
      next: r => {
        const orgFares = (r.data || []).filter(f => f.organizationId === orgId && f.recordStatus === 'ACTIVE');
        this.fares.set(orgFares);
        const mf = orgFares.find(f => f.fareType === 'CUOTA_MENSUAL');
        if (mf) this.monthlyFee.set(mf.amount);
      }
    });

    this.commercialService.getPayments().subscribe({
      next: r => this.allPayments.set((r.data || []).filter(p => p.recordStatus === 'ACTIVE'))
    });
  }

  searchUsers(): void {
    if (!this.searchTerm.trim()) return;
    const q = this.searchTerm.toLowerCase().trim();
    const users = this.allUsers().filter(u =>
      (u.lastName || '').toLowerCase().includes(q) ||
      (u.firstName || '').toLowerCase().includes(q) ||
      (u.documentNumber || '').includes(q)
    );

    const results: UserWithBox[] = users.map(user => {
      const assignment = this.allAssignments().find(a => a.userId === user.id);
      const box = assignment ? this.allBoxes().find(b => b.id === assignment.waterBoxId) : undefined;
      return { user, box, assignment };
    });

    this.searchResults.set(results);
    this.selectedUserBox.set(null);
  }

  selectUser(ub: UserWithBox): void {
    this.selectedUserBox.set(ub);
    this.searchResults.set([]);
    this.selectedMonths.set([]);
    this.selectedExtraFares.set([]);
    this.currentExtraFareId = '';
    this.paymentMethod = 'CASH';
    this.paymentNotes = '';
    this.loadUserDebts(ub.user.id);
  }

  clearSelection(): void {
    this.selectedUserBox.set(null);
    this.searchResults.set([]);
    this.selectedMonths.set([]);
    this.selectedExtraFares.set([]);
    this.userDebts.set([]);
    this.searchTerm = '';
  }

  addExtraFare(): void {
    if (!this.currentExtraFareId) return;
    const fare = this.fares().find(f => f.id === this.currentExtraFareId);
    if (!fare) return;

    const extraFare: ExtraFare = {
      fareId: fare.id,
      fareType: fare.fareType,
      description: fare.fareTypeDisplayName || fare.fareType,
      amount: fare.amount
    };

    this.selectedExtraFares.update(list => [...list, extraFare]);
    this.currentExtraFareId = '';
  }

  removeExtraFare(index: number): void {
    this.selectedExtraFares.update(list => list.filter((_, i) => i !== index));
  }

  private loadUserDebts(userId: string): void {
    this.commercialService.getDebtsByUser(userId).subscribe({
      next: r => this.userDebts.set(r.data || []),
      error: () => this.userDebts.set([])
    });
  }

  toggleMonth(cell: MonthCell): void {
    if (cell.status === 'paid') return;
    const current = this.selectedMonths();
    const exists = current.findIndex(s => s.month === cell.month && s.year === cell.year);
    if (exists >= 0) {
      this.selectedMonths.set(current.filter((_, i) => i !== exists));
    } else {
      this.selectedMonths.set([...current, cell]);
    }
  }

  changeYear(delta: number): void {
    this.selectedYear.update(y => y + delta);
    this.selectedMonths.set([]);
  }

  processPayment(): void {
    const ub = this.selectedUserBox();
    if (!ub || this.selectedMonths().length === 0 || this.isProcessing()) return;

    const details: { paymentType: string; description: string; amount: number; periodMonth?: number; periodYear?: number }[] = [];

    for (const m of this.selectedMonths()) {
      details.push({
        paymentType: 'MONTHLY_FEE',
        description: `Cuota mensual - ${m.label} ${m.year}`,
        amount: this.monthlyFee(),
        periodMonth: m.month,
        periodYear: m.year
      });
    }

    // Add all extra fares
    for (const extra of this.selectedExtraFares()) {
      details.push({
        paymentType: extra.fareType,
        description: extra.description,
        amount: extra.amount
      });
    }

    const req: CreatePaymentRequest = {
      userId: ub.user.id,
      organizationId: this.authService.organizationId() || '',
      totalAmount: this.totalPayment(),
      paymentMethod: this.paymentMethod,
      notes: this.paymentNotes || undefined,
      details
    };

    this.isProcessing.set(true);
    this.alertService.loading('Procesando pago...');

    this.commercialService.createPayment(req).subscribe({
      next: (res) => {
        this.alertService.close();
        this.isProcessing.set(false);
        this.alertService.success('Pago registrado', `Pago por S/ ${this.totalPayment().toFixed(2)} registrado exitosamente`);
        const paidMonths = this.selectedMonths();
        this.selectedMonths.set([]);
        this.selectedExtraFares.set([]);
        this.currentExtraFareId = '';
        this.paymentNotes = '';

        // Update paid debts
        this.updatePaidDebts(paidMonths);

        this.loadUserDebts(ub.user.id);
        this.loadBaseData();

        // Get full payment with details before generating PDF and create receipt
        if (res.data?.id) {
          this.commercialService.getPayment(res.data.id).subscribe({
            next: (fullRes) => {
              if (fullRes.data) {
                this.generateReceiptPdf(fullRes.data, ub);
                this.createReceiptFromPayment(fullRes.data);
              }
            },
            error: () => {
              if (res.data) {
                this.generateReceiptPdf(res.data, ub);
                this.createReceiptFromPayment(res.data);
              }
            }
          });
        }
      },
      error: (err) => {
        this.alertService.close();
        this.isProcessing.set(false);
        const msg = err?.error?.message || 'Error al procesar el pago';
        this.alertService.error('Error', msg);
      }
    });
  }

  viewPaymentDetail(p: Payment): void {
    if (p.details && p.details.length > 0) {
      this.detailPayment.set(p);
      this.showDetail.set(true);
    } else {
      this.commercialService.getPayment(p.id).subscribe({
        next: r => {
          this.detailPayment.set(r.data || p);
          this.showDetail.set(true);
        },
        error: () => {
          this.detailPayment.set(p);
          this.showDetail.set(true);
        }
      });
    }
  }

  downloadPaymentReceipt(p: Payment): void {
    // Always fetch full payment with details
    this.commercialService.getPayment(p.id).subscribe({
      next: r => {
        const fullPayment = r.data || p;
        const user = this.allUsers().find(u => u.id === fullPayment.userId);
        const assignment = this.allAssignments().find(a => a.userId === fullPayment.userId);
        const box = assignment ? this.allBoxes().find(b => b.id === assignment.waterBoxId) : undefined;
        const ub: UserWithBox = { user: user || {} as User, box, assignment };
        this.generateReceiptPdf(fullPayment, ub);
      },
      error: () => {
        // Fallback to current data
        const user = this.allUsers().find(u => u.id === p.userId);
        const assignment = this.allAssignments().find(a => a.userId === p.userId);
        const box = assignment ? this.allBoxes().find(b => b.id === assignment.waterBoxId) : undefined;
        const ub: UserWithBox = { user: user || {} as User, box, assignment };
        this.generateReceiptPdf(p, ub);
      }
    });
  }

  private updatePaidDebts(paidMonths: MonthCell[]): void {
    const debts = this.userDebts();
    paidMonths.forEach(month => {
      const debt = debts.find(d => d.periodMonth === month.month && d.periodYear === month.year);
      if (debt && debt.id) {
        this.commercialService.updateDebt(debt.id, {
          debtStatus: 'PAID',
          pendingAmount: 0
        }).subscribe({
          next: () => console.log(`Debt ${debt.id} marked as PAID`),
          error: (err) => console.error('Error updating debt:', err)
        });
      }
    });
  }

  private createReceiptFromPayment(payment: Payment): void {
    if (!payment.details || payment.details.length === 0) return;

    // Get monthly details for period
    const monthlyDetails = payment.details.filter(d => d.paymentType === 'MONTHLY_FEE');
    if (monthlyDetails.length === 0) return;

    // For each monthly detail, create a receipt
    monthlyDetails.forEach(detail => {
      const receiptReq: CreateReceiptRequest = {
        userId: payment.userId,
        periodMonth: detail.periodMonth || 1,
        periodYear: detail.periodYear || new Date().getFullYear(),
        totalAmount: detail.amount,
        notes: `Pago registrado: ${payment.receiptNumber}`,
        details: [{
          conceptType: 'MONTHLY_FEE',
          description: detail.description,
          amount: detail.amount
        }]
      };

      this.commercialService.createReceipt(receiptReq).subscribe({
        next: () => console.log(`Receipt created for period ${detail.periodMonth}/${detail.periodYear}`),
        error: (err) => console.error('Error creating receipt:', err)
      });
    });
  }

  getUserName(userId: string): string {
    const u = this.allUsers().find(x => x.id === userId);
    return u ? `${u.lastName}, ${u.firstName}` : userId;
  }

  getMonthCellClass(cell: MonthCell): string {
    switch (cell.status) {
      case 'paid': return 'bg-emerald-50 border-emerald-200 text-emerald-700 cursor-default';
      case 'selected': return 'bg-violet-50 border-violet-300 text-violet-700 ring-2 ring-violet-200';
      case 'overdue': return 'bg-orange-50 border-orange-300 text-orange-700 hover:bg-orange-100 cursor-pointer';
      case 'pending': return 'bg-blue-50 border-blue-200 text-blue-600 hover:bg-blue-100 cursor-pointer';
      case 'future': return 'bg-blue-50 border-blue-200 text-blue-600 hover:bg-blue-100 cursor-pointer';
      default: return 'bg-gray-50 border-gray-200 text-gray-600';
    }
  }

  getMethodLabel(m: string): string {
    const map: Record<string, string> = { CASH: 'Efectivo', BANK_TRANSFER: 'Transferencia', CARD: 'Tarjeta', YAPE: 'Yape', PLIN: 'Plin' };
    return map[m] || m;
  }

  getStatusLabel(s: string): string {
    const map: Record<string, string> = { PENDING: 'Pendiente', COMPLETED: 'Completado', CANCELLED: 'Cancelado', FAILED: 'Fallido' };
    return map[s] || s;
  }

  getStatusBadge(s: string): string {
    const map: Record<string, string> = {
      COMPLETED: 'bg-emerald-50 text-emerald-700',
      PENDING: 'bg-amber-50 text-amber-700',
      CANCELLED: 'bg-red-50 text-red-600',
      FAILED: 'bg-red-50 text-red-600'
    };
    return map[s] || 'bg-gray-100 text-gray-600';
  }

  getConceptLabel(c: string): string {
    const map: Record<string, string> = {
      MONTHLY_FEE: 'Cuota mensual', LATE_FEE: 'Mora', ASSEMBLY_FINE: 'Multa por asamblea',
      WORK_FINE: 'Multa por faena', RECONNECTION_FEE: 'Reconexión', INSTALLATION_FEE: 'Instalación',
      TRANSFER_FEE: 'Transferencia', OTHER: 'Otro'
    };
    return map[c] || c;
  }

  getBoxTypeLabel(t: string): string {
    const map: Record<string, string> = { RESIDENTIAL: 'Residencial', COMMERCIAL: 'Comercial', COMMUNAL: 'Comunal', INSTITUTIONAL: 'Institucional' };
    return map[t] || t;
  }

  private generateReceiptPdf(payment: Payment, ub: UserWithBox): void {
    const doc = new jsPDF({ format: 'a4' });
    const org = this.authService.organization();
    const pw = 210; // A4 width in mm
    const mg = 15; // Larger margins
    const cw = pw - mg * 2;
    const dark = [15, 23, 42] as const;
    const mid = [100, 116, 139] as const;
    const accent = [71, 85, 105] as const;
    let y = 15;

    // ── Logo ──
    const logoUrl = org?.logoUrl || (org as any)?.['logo_url'] || (org as any)?.['logo'];
    if (logoUrl) {
      try { doc.addImage(logoUrl, 'PNG', pw / 2 - 15, y, 30, 30); y += 35; } catch { /* skip */ }
    }

    // ── Nombre organización ──
    doc.setFontSize(14);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...dark);
    const orgName = org?.organizationName || 'JASS';
    const orgNameLines = doc.splitTextToSize(orgName.toUpperCase(), cw);
    doc.text(orgNameLines, pw / 2, y, { align: 'center' });
    y += orgNameLines.length * 6;

    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(...mid);
    const loc = [org?.district, org?.province, org?.department].filter(Boolean).join(' - ');
    if (loc) { doc.text(loc, pw / 2, y, { align: 'center' }); y += 6; }

    y += 5;

    // ── Título RECIBO DE INGRESO ──
    doc.setFillColor(51, 65, 85);
    doc.roundedRect(mg, y, cw, 12, 2, 2, 'F');
    doc.setFontSize(14);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(255, 255, 255);
    doc.text('RECIBO DE PAGO', pw / 2, y + 8, { align: 'center' });
    y += 18;

    // ── N° y Fecha ──
    doc.setFontSize(11);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...accent);
    const receiptNum = (payment.receiptNumber || '').replace(/^PAY-/, '');
    doc.text(`N\u00b0 ${receiptNum}`, mg, y);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(...mid);
    doc.text(new Date(payment.paymentDate).toLocaleDateString('es-PE', { day: '2-digit', month: 'long', year: 'numeric' }), pw - mg, y, { align: 'right' });
    y += 8;

    doc.setDrawColor(220, 220, 220);
    doc.setLineWidth(0.5);
    doc.line(mg, y, pw - mg, y);
    y += 8;

    // ── Datos del usuario ──
    const userName = ub.user?.lastName ? `${ub.user.lastName}, ${ub.user.firstName}` : (payment.userFullName || '-');
    const drawField = (label: string, value: string) => {
      doc.setFontSize(10);
      doc.setFont('helvetica', 'normal');
      doc.setTextColor(...mid);
      doc.text(label, mg, y);
      doc.setFont('helvetica', 'bold');
      doc.setTextColor(...dark);
      const valueLines = doc.splitTextToSize(value, cw - 40);
      doc.text(valueLines, mg + 38, y);
      y += valueLines.length * 6 + 2;
    };

    drawField('Recibí de:', userName);
    drawField('DNI:', ub.user?.documentNumber || '-');
    if (ub.box?.boxCode) drawField('Suministro:', ub.box.boxCode);
    const addr = ub.user?.address || ub.box?.address || '';
    if (addr) drawField('Dirección:', addr);

    y += 3;
    doc.setDrawColor(220, 220, 220);
    doc.line(mg, y, pw - mg, y);
    y += 8;

    // ── Concepto ──
    const monthNames = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    const monthlyDetails = (payment.details || []).filter(d => d.paymentType === 'MONTHLY_FEE');
    const extraDetails = (payment.details || []).filter(d => d.paymentType !== 'MONTHLY_FEE');

    // Calculate period description
    let periodText = '';
    if (monthlyDetails.length > 0) {
      const sorted = [...monthlyDetails].sort((a, b) => {
        const ya = a.periodYear || 0, yb = b.periodYear || 0;
        return ya !== yb ? ya - yb : (a.periodMonth || 0) - (b.periodMonth || 0);
      });
      const first = sorted[0];
      const last = sorted[sorted.length - 1];
      if (sorted.length === 1) {
        periodText = `${monthNames[(first.periodMonth || 1) - 1]} ${first.periodYear}`;
      } else {
        periodText = `${monthNames[(first.periodMonth || 1) - 1]} ${first.periodYear} - ${monthNames[(last.periodMonth || 1) - 1]} ${last.periodYear}`;
      }
    }

    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(...mid);
    doc.text('Concepto de:', mg, y);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...dark);
    doc.text('Servicio de agua potable', mg + 38, y);
    y += 7;

    if (periodText) {
      doc.setFont('helvetica', 'normal');
      doc.setTextColor(...mid);
      doc.text('Período:', mg, y);
      doc.setFont('helvetica', 'bold');
      doc.setTextColor(...accent);
      doc.text(periodText, mg + 38, y);
      y += 8;
    }

    // ── Tabla de conceptos ──
    doc.setDrawColor(200, 200, 200);
    doc.setLineWidth(0.3);
    doc.line(mg, y, pw - mg, y);
    y += 6;

    const drawRow = (label: string, amount: string, bold = false) => {
      doc.setFontSize(11);
      doc.setFont('helvetica', bold ? 'bold' : 'normal');
      const rc: [number, number, number] = bold ? [dark[0], dark[1], dark[2]] : [mid[0], mid[1], mid[2]];
      doc.setTextColor(rc[0], rc[1], rc[2]);
      doc.text(label, mg + 2, y);
      doc.setTextColor(dark[0], dark[1], dark[2]);
      doc.setFont('helvetica', bold ? 'bold' : 'normal');
      doc.text(amount, pw - mg - 2, y, { align: 'right' });
      y += 7;
    };

    // Monthly fee line
    if (monthlyDetails.length > 0) {
      const monthlyTotal = monthlyDetails.reduce((sum, d) => sum + d.amount, 0);
      const monthLabel = monthlyDetails.length === 1
        ? 'Cuota mensual'
        : `Cuota mensual (${monthlyDetails.length} meses)`;
      drawRow(monthLabel, `S/ ${monthlyTotal.toFixed(2)}`);
    }

    // Extra concepts
    for (const d of extraDetails) {
      drawRow(d.description || this.getConceptLabel(d.paymentType), `S/ ${d.amount.toFixed(2)}`);
    }

    // If no details at all
    if (!payment.details || payment.details.length === 0) {
      drawRow('Pago realizado', `S/ ${payment.totalAmount.toFixed(2)}`);
    }

    y += 3;
    doc.setLineWidth(0.5);
    doc.setDrawColor(71, 85, 105);
    doc.line(mg, y, pw - mg, y);
    y += 8;

    // ── TOTAL ──
    doc.setFillColor(241, 245, 249);
    doc.roundedRect(mg, y - 3, cw, 14, 2, 2, 'F');
    doc.setFontSize(13);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...dark);
    doc.text('TOTAL:', mg + 5, y + 6);
    doc.setTextColor(...accent);
    doc.text(`S/ ${payment.totalAmount.toFixed(2)}`, pw - mg - 5, y + 6, { align: 'right' });
    y += 18;

    // ── Método de pago ──
    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(...mid);
    doc.text(`Método de pago: ${this.getMethodLabel(payment.paymentMethod)}`, mg, y);
    y += 6;

    if (payment.notes) {
      const noteLines = doc.splitTextToSize(`Nota: ${payment.notes}`, cw);
      doc.text(noteLines, mg, y);
      y += noteLines.length * 6;
    }
    y += 8;

    // ── Separador final ──
    doc.setDrawColor(220, 220, 220);
    doc.setLineWidth(0.3);
    doc.setLineDashPattern([2, 2], 0);
    doc.line(mg, y, pw - mg, y);
    y += 12;

    // ── Firma ──
    doc.setLineDashPattern([], 0);
    doc.setLineWidth(0.4);
    doc.setDrawColor(180, 180, 180);
    doc.line(pw / 2 - 30, y + 10, pw / 2 + 30, y + 10);
    doc.setFontSize(9);
    doc.setTextColor(160, 160, 160);
    doc.text('RECIBÍ CONFORME', pw / 2, y + 15, { align: 'center' });
    y += 25;

    // ── Footer ──
    doc.setFontSize(9);
    doc.setTextColor(180, 180, 180);
    doc.text('Este documento es su comprobante de pago oficial.', pw / 2, y, { align: 'center' });
    y += 5;
    doc.text('Consérvelo para cualquier reclamo.', pw / 2, y, { align: 'center' });
    y += 5;
    doc.text(orgName, pw / 2, y, { align: 'center' });

    doc.save(`Recibo_${payment.receiptNumber || 'pago'}_${new Date().toISOString().split('T')[0]}.pdf`);
  }
}
