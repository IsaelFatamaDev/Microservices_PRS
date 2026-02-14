import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, Wallet, Plus, TrendingUp, TrendingDown, Search,
  ChevronLeft, ChevronRight, X, DollarSign, ArrowUpCircle, ArrowDownCircle,
  Calendar, Filter, Eye, BarChart3, RefreshCw
} from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { CommercialService } from '../../../../core/services/commercial.service';
import { ApiResponse, User } from '../../../../core/models';
import {
  PettyCash, PettyCashMovement, RegisterMovementRequest,
  CreatePettyCashRequest, MovementType, MovementCategory
} from '../../../../core/models/commercial.model';

@Component({
  selector: 'app-petty-cash',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <!-- Encabezado -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Caja Chica</h1>
          <p class="text-sm text-gray-500 mt-1">Control de movimientos de ingresos y egresos</p>
        </div>
        <div class="flex items-center gap-3">
          @if (!activePettyCash()) {
            <button (click)="showCreateModal.set(true)"
                    class="px-4 py-2 bg-violet-600 text-white rounded-xl text-sm font-medium hover:bg-violet-700 transition-all flex items-center gap-2">
              <lucide-icon [img]="plusIcon" [size]="16"></lucide-icon>
              Crear Caja Chica
            </button>
          } @else {
            <button (click)="showMovementModal.set(true)"
                    class="px-4 py-2 bg-violet-600 text-white rounded-xl text-sm font-medium hover:bg-violet-700 transition-all flex items-center gap-2">
              <lucide-icon [img]="plusIcon" [size]="16"></lucide-icon>
              Registrar Movimiento
            </button>
          }
        </div>
      </div>

      <!-- Tarjetas estadísticas -->
      @if (activePettyCash()) {
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Saldo Actual</span>
              <div class="p-2 rounded-xl bg-violet-50"><lucide-icon [img]="walletIcon" [size]="18" class="text-violet-600"></lucide-icon></div>
            </div>
            <p class="text-2xl font-bold text-gray-800">S/ {{ activePettyCash()!.currentBalance.toFixed(2) }}</p>
            <p class="text-xs text-gray-400 mt-1">Límite: S/ {{ activePettyCash()!.maxAmountLimit.toFixed(2) }}</p>
          </div>
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Ingresos (mes)</span>
              <div class="p-2 rounded-xl bg-emerald-50"><lucide-icon [img]="trendingUpIcon" [size]="18" class="text-emerald-600"></lucide-icon></div>
            </div>
            <p class="text-2xl font-bold text-emerald-600">S/ {{ monthlyIncome().toFixed(2) }}</p>
            <p class="text-xs text-gray-400 mt-1">{{ monthlyIncomeCount() }} movimientos</p>
          </div>
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Egresos (mes)</span>
              <div class="p-2 rounded-xl bg-red-50"><lucide-icon [img]="trendingDownIcon" [size]="18" class="text-red-600"></lucide-icon></div>
            </div>
            <p class="text-2xl font-bold text-red-600">S/ {{ monthlyExpense().toFixed(2) }}</p>
            <p class="text-xs text-gray-400 mt-1">{{ monthlyExpenseCount() }} movimientos</p>
          </div>
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Total Movimientos</span>
              <div class="p-2 rounded-xl bg-blue-50"><lucide-icon [img]="barChartIcon" [size]="18" class="text-blue-600"></lucide-icon></div>
            </div>
            <p class="text-2xl font-bold text-gray-800">{{ allMovements().length }}</p>
            <p class="text-xs text-gray-400 mt-1">Responsable: {{ activePettyCash()!.responsibleUserName || getResponsibleName() }}</p>
          </div>
        </div>
      }

      <!-- Tabla de movimientos -->
      @if (activePettyCash()) {
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center gap-3">
            <div class="relative flex-1">
              <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
              <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar movimiento..."
                     class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-300">
            </div>
            <div class="flex gap-2">
              <select [(ngModel)]="typeFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
                <option value="">Todos</option>
                <option value="IN">Ingresos</option>
                <option value="OUT">Egresos</option>
                <option value="ADJUSTMENT">Ajustes</option>
              </select>
              <select [(ngModel)]="categoryFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
                <option value="">Todas las categorías</option>
                <option value="SUPPLIES">Suministros</option>
                <option value="TRANSPORT">Transporte</option>
                <option value="FOOD">Alimentación</option>
                <option value="EMERGENCY">Emergencia</option>
                <option value="OTHER">Otro</option>
              </select>
              <select [(ngModel)]="monthFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
                <option value="0">Todos los meses</option>
                @for (m of monthOptions; track m.value) {
                  <option [value]="m.value">{{ m.label }}</option>
                }
              </select>
            </div>
          </div>

          <!-- Desktop -->
          <div class="hidden md:block overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50/80">
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Fecha</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Tipo</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Categoría</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Descripción</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Voucher</th>
                  <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Monto</th>
                  <th class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Saldo</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (mv of paginatedMovements(); track mv.id) {
                  <tr class="hover:bg-gray-50/50 transition-colors">
                    <td class="px-4 py-3 text-sm text-gray-600">{{ mv.movementDate | date:'dd/MM/yyyy' }}</td>
                    <td class="px-4 py-3">
                      <span [class]="getTypeBadge(mv.movementType)" class="text-xs px-2 py-1 rounded-lg font-medium flex items-center gap-1 w-fit">
                        <lucide-icon [img]="mv.movementType === 'IN' ? arrowUpIcon : arrowDownIcon" [size]="12"></lucide-icon>
                        {{ getTypeLabel(mv.movementType) }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-xs text-gray-600">{{ getCategoryLabel(mv.category) }}</td>
                    <td class="px-4 py-3 text-sm text-gray-700 max-w-[200px] truncate">{{ mv.description }}</td>
                    <td class="px-4 py-3 text-xs text-gray-500">{{ mv.voucherNumber || '-' }}</td>
                    <td class="px-4 py-3 text-right">
                      <span [class]="mv.movementType === 'IN' ? 'text-emerald-600' : 'text-red-600'" class="text-sm font-semibold">
                        {{ mv.movementType === 'IN' ? '+' : '-' }} S/ {{ mv.amount.toFixed(2) }}
                      </span>
                    </td>
                    <td class="px-4 py-3 text-right text-sm font-medium text-gray-800">S/ {{ mv.newBalance.toFixed(2) }}</td>
                  </tr>
                } @empty {
                  <tr><td colspan="7" class="text-center py-12 text-gray-400 text-sm">No se encontraron movimientos</td></tr>
                }
              </tbody>
            </table>
          </div>

          <!-- Mobile -->
          <div class="md:hidden divide-y divide-gray-50">
            @for (mv of paginatedMovements(); track mv.id) {
              <div class="p-4 space-y-2">
                <div class="flex items-center justify-between">
                  <span [class]="getTypeBadge(mv.movementType)" class="text-xs px-2 py-1 rounded-lg font-medium flex items-center gap-1">
                    <lucide-icon [img]="mv.movementType === 'IN' ? arrowUpIcon : arrowDownIcon" [size]="12"></lucide-icon>
                    {{ getTypeLabel(mv.movementType) }}
                  </span>
                  <span [class]="mv.movementType === 'IN' ? 'text-emerald-600' : 'text-red-600'" class="text-sm font-bold">
                    {{ mv.movementType === 'IN' ? '+' : '-' }} S/ {{ mv.amount.toFixed(2) }}
                  </span>
                </div>
                <p class="text-sm text-gray-700">{{ mv.description }}</p>
                <div class="flex items-center justify-between text-xs text-gray-400">
                  <span>{{ mv.movementDate | date:'dd/MM/yyyy' }} · {{ getCategoryLabel(mv.category) }}</span>
                  <span class="font-medium text-gray-600">Saldo: S/ {{ mv.newBalance.toFixed(2) }}</span>
                </div>
              </div>
            } @empty {
              <div class="p-8 text-center text-gray-400 text-sm">No se encontraron movimientos</div>
            }
          </div>

          @if (filteredMovements().length > pageSize) {
            <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
              <span>{{ startIdx() + 1 }} - {{ endIdx() }} de {{ filteredMovements().length }}</span>
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
      } @else {
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
          <lucide-icon [img]="walletIcon" [size]="48" class="text-gray-300 mx-auto mb-4"></lucide-icon>
          <p class="text-gray-500 mb-4">No hay caja chica activa</p>
          <button (click)="showCreateModal.set(true)" class="px-4 py-2 bg-violet-600 text-white rounded-xl text-sm font-medium hover:bg-violet-700 transition-all">
            Crear Caja Chica
          </button>
        </div>
      }

      <!-- Modal Crear Caja Chica -->
      @if (showCreateModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="showCreateModal.set(false)">
          <div class="bg-white rounded-2xl shadow-xl max-w-md w-full" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Crear Caja Chica</h3>
              <button (click)="showCreateModal.set(false)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <div class="p-6 space-y-4">
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Responsable</label>
                <select [(ngModel)]="newCash.responsibleUserId" class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
                  <option value="">Seleccionar responsable</option>
                  @for (u of adminUsers(); track u.id) {
                    <option [value]="u.id">{{ u.lastName }}, {{ u.firstName }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Saldo inicial (S/)</label>
                <input type="number" [(ngModel)]="newCash.initialBalance" min="0" step="0.01"
                       class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none" placeholder="0.00">
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Límite máximo (S/)</label>
                <input type="number" [(ngModel)]="newCash.maxAmountLimit" min="0" step="0.01"
                       class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none" placeholder="0.00">
              </div>
              <button (click)="createPettyCash()" [disabled]="!newCash.responsibleUserId || newCash.initialBalance <= 0"
                      class="w-full py-2.5 bg-violet-600 text-white rounded-xl font-medium hover:bg-violet-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed">
                Crear Caja Chica
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Modal Registrar Movimiento -->
      @if (showMovementModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="showMovementModal.set(false)">
          <div class="bg-white rounded-2xl shadow-xl max-w-md w-full" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Registrar Movimiento</h3>
              <button (click)="showMovementModal.set(false)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <div class="p-6 space-y-4">
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Tipo de movimiento</label>
                <div class="grid grid-cols-2 gap-2">
                  <button (click)="newMovement.movementType = 'IN'"
                          [class]="newMovement.movementType === 'IN' ? 'bg-emerald-50 border-emerald-300 text-emerald-700' : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-gray-100'"
                          class="px-3 py-2.5 rounded-xl text-sm font-medium border transition-all flex items-center justify-center gap-2">
                    <lucide-icon [img]="arrowUpIcon" [size]="16"></lucide-icon> Ingreso
                  </button>
                  <button (click)="newMovement.movementType = 'OUT'"
                          [class]="newMovement.movementType === 'OUT' ? 'bg-red-50 border-red-300 text-red-700' : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-gray-100'"
                          class="px-3 py-2.5 rounded-xl text-sm font-medium border transition-all flex items-center justify-center gap-2">
                    <lucide-icon [img]="arrowDownIcon" [size]="16"></lucide-icon> Egreso
                  </button>
                </div>
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Categoría</label>
                <select [(ngModel)]="newMovement.category" class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
                  <option value="">Seleccionar categoría</option>
                  <option value="SUPPLIES">Suministros</option>
                  <option value="TRANSPORT">Transporte</option>
                  <option value="FOOD">Alimentación</option>
                  <option value="EMERGENCY">Emergencia</option>
                  <option value="OTHER">Otro</option>
                </select>
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Monto (S/)</label>
                <input type="number" [(ngModel)]="newMovement.amount" min="0.01" step="0.01"
                       class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none" placeholder="0.00">
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">Descripción</label>
                <input type="text" [(ngModel)]="newMovement.description"
                       class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none" placeholder="Descripción del movimiento">
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">N° Voucher (opcional)</label>
                <input type="text" [(ngModel)]="newMovement.voucherNumber"
                       class="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none" placeholder="N° de comprobante">
              </div>
              <button (click)="registerMovement()"
                      [disabled]="!newMovement.movementType || !newMovement.category || newMovement.amount <= 0 || !newMovement.description"
                      class="w-full py-2.5 bg-violet-600 text-white rounded-xl font-medium hover:bg-violet-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed">
                Registrar Movimiento
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class PettyCashComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private commercialService = inject(CommercialService);

  walletIcon = Wallet; plusIcon = Plus; trendingUpIcon = TrendingUp; trendingDownIcon = TrendingDown;
  searchIcon = Search; chevronLeftIcon = ChevronLeft; chevronRightIcon = ChevronRight; xIcon = X;
  dollarIcon = DollarSign; arrowUpIcon = ArrowUpCircle; arrowDownIcon = ArrowDownCircle;
  calendarIcon = Calendar; filterIcon = Filter; eyeIcon = Eye; barChartIcon = BarChart3; refreshIcon = RefreshCw;

  activePettyCash = signal<PettyCash | null>(null);
  allMovements = signal<PettyCashMovement[]>([]);
  adminUsers = signal<User[]>([]);

  searchTerm = '';
  typeFilter = '';
  categoryFilter = '';
  monthFilter = '0';
  currentPage = signal(1);
  pageSize = 15;

  showCreateModal = signal(false);
  showMovementModal = signal(false);

  newCash = { responsibleUserId: '', initialBalance: 0, maxAmountLimit: 0 };
  newMovement = { movementType: '' as string, category: '' as string, amount: 0, description: '', voucherNumber: '' };

  monthOptions = [
    { value: '1', label: 'Enero' }, { value: '2', label: 'Febrero' }, { value: '3', label: 'Marzo' },
    { value: '4', label: 'Abril' }, { value: '5', label: 'Mayo' }, { value: '6', label: 'Junio' },
    { value: '7', label: 'Julio' }, { value: '8', label: 'Agosto' }, { value: '9', label: 'Septiembre' },
    { value: '10', label: 'Octubre' }, { value: '11', label: 'Noviembre' }, { value: '12', label: 'Diciembre' }
  ];

  private currentMonthMovements = computed(() => {
    const now = new Date();
    return this.allMovements().filter(m => {
      const d = new Date(m.movementDate);
      return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear();
    });
  });

  monthlyIncome = computed(() => this.currentMonthMovements().filter(m => m.movementType === 'IN').reduce((s, m) => s + m.amount, 0));
  monthlyExpense = computed(() => this.currentMonthMovements().filter(m => m.movementType === 'OUT').reduce((s, m) => s + m.amount, 0));
  monthlyIncomeCount = computed(() => this.currentMonthMovements().filter(m => m.movementType === 'IN').length);
  monthlyExpenseCount = computed(() => this.currentMonthMovements().filter(m => m.movementType === 'OUT').length);

  filteredMovements = computed(() => {
    let list = this.allMovements();
    if (this.typeFilter) list = list.filter(m => m.movementType === this.typeFilter);
    if (this.categoryFilter) list = list.filter(m => m.category === this.categoryFilter);
    if (this.monthFilter !== '0') {
      const mf = parseInt(this.monthFilter);
      list = list.filter(m => new Date(m.movementDate).getMonth() + 1 === mf);
    }
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(m => m.description.toLowerCase().includes(q) || (m.voucherNumber || '').toLowerCase().includes(q));
    }
    return list.sort((a, b) => new Date(b.movementDate).getTime() - new Date(a.movementDate).getTime());
  });

  startIdx = computed(() => (this.currentPage() - 1) * this.pageSize);
  endIdx = computed(() => Math.min(this.currentPage() * this.pageSize, this.filteredMovements().length));
  totalPages = computed(() => Math.ceil(this.filteredMovements().length / this.pageSize));
  paginatedMovements = computed(() => this.filteredMovements().slice(this.startIdx(), this.startIdx() + this.pageSize));

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.http.get<ApiResponse<User[]>>(`${environment.apiUrl}/users`).subscribe({
      next: r => this.adminUsers.set((r.data || []).filter(u => u.organizationId === orgId && (u.role === 'ADMIN' || u.role === 'OPERATOR')))
    });

    this.commercialService.getActivePettyCash().subscribe({
      next: r => {
        if (r?.data) {
          this.activePettyCash.set(r.data);
          this.loadMovements(r.data.id);
        }
      },
      error: () => { }
    });
  }

  private loadMovements(pettyCashId: string): void {
    this.commercialService.getPettyCashMovements(pettyCashId).subscribe({
      next: r => this.allMovements.set(r.data || []),
      error: () => this.allMovements.set([])
    });
  }

  createPettyCash(): void {
    if (!this.newCash.responsibleUserId || this.newCash.initialBalance <= 0) return;
    const req: CreatePettyCashRequest = {
      responsibleUserId: this.newCash.responsibleUserId,
      initialBalance: this.newCash.initialBalance,
      maxAmountLimit: this.newCash.maxAmountLimit || this.newCash.initialBalance * 2
    };
    this.alertService.loading('Creando caja chica...');
    this.commercialService.createPettyCash(req).subscribe({
      next: r => {
        this.alertService.close();
        this.alertService.success('Caja chica creada');
        this.showCreateModal.set(false);
        this.activePettyCash.set(r.data || null);
        if (r.data) this.loadMovements(r.data.id);
      },
      error: (err) => {
        this.alertService.close();
        this.alertService.error('Error', err?.error?.message || 'Error al crear caja chica');
      }
    });
  }

  registerMovement(): void {
    const pc = this.activePettyCash();
    if (!pc || !this.newMovement.movementType || !this.newMovement.category || this.newMovement.amount <= 0) return;

    const req: RegisterMovementRequest = {
      pettyCashId: pc.id,
      movementType: this.newMovement.movementType,
      amount: this.newMovement.amount,
      category: this.newMovement.category,
      description: this.newMovement.description,
      voucherNumber: this.newMovement.voucherNumber || undefined
    };

    this.alertService.loading('Registrando movimiento...');
    this.commercialService.registerMovement(req).subscribe({
      next: () => {
        this.alertService.close();
        this.alertService.success('Movimiento registrado');
        this.showMovementModal.set(false);
        this.newMovement = { movementType: '', category: '', amount: 0, description: '', voucherNumber: '' };
        this.loadData();
      },
      error: (err) => {
        this.alertService.close();
        this.alertService.error('Error', err?.error?.message || 'Error al registrar movimiento');
      }
    });
  }

  getResponsibleName(): string {
    const pc = this.activePettyCash();
    if (!pc) return '-';
    const u = this.adminUsers().find(x => x.id === pc.responsibleUserId);
    return u ? `${u.lastName}, ${u.firstName}` : '-';
  }

  getTypeLabel(t: string): string {
    const map: Record<string, string> = { IN: 'Ingreso', OUT: 'Egreso', ADJUSTMENT: 'Ajuste' };
    return map[t] || t;
  }

  getTypeBadge(t: string): string {
    const map: Record<string, string> = {
      IN: 'bg-emerald-50 text-emerald-700',
      OUT: 'bg-red-50 text-red-600',
      ADJUSTMENT: 'bg-amber-50 text-amber-700'
    };
    return map[t] || 'bg-gray-100 text-gray-600';
  }

  getCategoryLabel(c: string): string {
    const map: Record<string, string> = {
      SUPPLIES: 'Suministros', TRANSPORT: 'Transporte', FOOD: 'Alimentación',
      EMERGENCY: 'Emergencia', OTHER: 'Otro'
    };
    return map[c] || c;
  }
}
