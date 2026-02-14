import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule, MessageSquareWarning, Search, Plus, Eye, X,
  ChevronLeft, ChevronRight, MessageSquare, Star, CheckCircle
} from 'lucide-angular';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { ClaimsService } from '../../../../core/services/claims.service';
import { Complaint, ComplaintCategory, CreateComplaintRequest, AddComplaintResponseRequest, ComplaintResponse } from '../../../../core/models/claims.model';
import { User, ApiResponse } from '../../../../core/models';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

interface WaterBox {
  id: string;
  boxCode: string;
}

@Component({
  selector: 'app-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Quejas</h1>
          <p class="text-sm text-gray-500 mt-1">Gestión de quejas y reclamos de usuarios</p>
        </div>
        <button (click)="openCreateModal()" class="px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white rounded-xl font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          <span>Nueva Queja</span>
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Total</span>
            <div class="p-2 rounded-xl bg-violet-50"><lucide-icon [img]="complaintIcon" [size]="16" class="text-violet-600"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-gray-800">{{ allComplaints().length }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Recibidas</span>
            <div class="p-2 rounded-xl bg-amber-50"><lucide-icon [img]="complaintIcon" [size]="16" class="text-amber-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-amber-600">{{ receivedCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">En Proceso</span>
            <div class="p-2 rounded-xl bg-blue-50"><lucide-icon [img]="complaintIcon" [size]="16" class="text-blue-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-blue-600">{{ inProgressCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Resueltas</span>
            <div class="p-2 rounded-xl bg-emerald-50"><lucide-icon [img]="checkIcon" [size]="16" class="text-emerald-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-emerald-600">{{ resolvedCount() }}</p>
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center gap-3">
          <div class="relative flex-1">
            <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
            <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar por código o asunto..."
                   class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 outline-none transition-all placeholder:text-gray-400">
          </div>
          <select [(ngModel)]="statusFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todos los Estados</option>
            <option value="RECEIVED">Recibida</option>
            <option value="IN_PROGRESS">En Proceso</option>
            <option value="RESOLVED">Resuelta</option>
            <option value="CLOSED">Cerrada</option>
          </select>
          <select [(ngModel)]="priorityFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todas las Prioridades</option>
            <option value="LOW">Baja</option>
            <option value="MEDIUM">Media</option>
            <option value="HIGH">Alta</option>
            <option value="URGENT">Urgente</option>
          </select>
        </div>

        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50/80">
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Código</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Asunto</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Usuario</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Prioridad</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Estado</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Fecha</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (c of paginatedComplaints(); track c.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3 text-sm font-medium text-violet-700">{{ c.complaintCode }}</td>
                  <td class="px-4 py-3 text-sm text-gray-800">{{ c.subject }}</td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ getUserName(c.userId) }}</td>
                  <td class="px-4 py-3">
                    <span [class]="getPriorityBadge(c.priority)" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ getPriorityLabel(c.priority) }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <span [class]="getStatusBadge(c.status)" class="text-xs px-2 py-1 rounded-lg font-medium">
                      {{ getStatusLabel(c.status) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ c.complaintDate | date:'dd/MM/yyyy' }}</td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="viewDetail(c)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-violet-600 transition-colors" title="Ver detalle">
                        <lucide-icon [img]="eyeIcon" [size]="15"></lucide-icon>
                      </button>
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="7" class="text-center py-12 text-gray-400 text-sm">No se encontraron quejas</td></tr>
              }
            </tbody>
          </table>
        </div>

        <!-- Mobile cards -->
        <div class="md:hidden divide-y divide-gray-50">
          @for (c of paginatedComplaints(); track c.id) {
            <div class="p-4 space-y-2">
              <div class="flex items-center justify-between">
                <span class="text-sm font-medium text-violet-700">{{ c.complaintCode }}</span>
                <span [class]="getStatusBadge(c.status)" class="text-xs px-2 py-1 rounded-lg font-medium">
                  {{ getStatusLabel(c.status) }}
                </span>
              </div>
              <p class="text-sm text-gray-800 font-medium">{{ c.subject }}</p>
              <div class="flex items-center gap-2">
                <span [class]="getPriorityBadge(c.priority)" class="text-xs px-2 py-1 rounded-lg font-medium">
                  {{ getPriorityLabel(c.priority) }}
                </span>
                <span class="text-xs text-gray-400">{{ c.complaintDate | date:'dd/MM/yyyy' }}</span>
              </div>
              <button (click)="viewDetail(c)" class="w-full py-1.5 text-xs border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 transition-colors">Ver detalle</button>
            </div>
          } @empty {
            <div class="p-8 text-center text-gray-400 text-sm">No se encontraron quejas</div>
          }
        </div>

        @if (filteredComplaints().length > pageSize) {
          <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
            <span>{{ (currentPage() - 1) * pageSize + 1 }} - {{ Math.min(currentPage() * pageSize, filteredComplaints().length) }} de {{ filteredComplaints().length }}</span>
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

      <!-- Create Modal -->
      @if (showCreateModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeCreateModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Nueva Queja</h3>
              <button (click)="closeCreateModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="saveComplaint()" class="p-6 space-y-4">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Categoría *</label>
                  <select [(ngModel)]="formData.categoryId" name="categoryId" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccionar...</option>
                    @for (cat of allComplaintCategories(); track cat.id) {
                      <option [value]="cat.id">{{cat.categoryName }}</option>
                    }
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Prioridad *</label>
                  <select [(ngModel)]="formData.priority" name="priority" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                    <option value="">Seleccionar...</option>
                    <option value="LOW">Baja</option>
                    <option value="MEDIUM">Media</option>
                    <option value="HIGH">Alta</option>
                    <option value="URGENT">Urgente</option>
                  </select>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Caja de Agua (Opcional)</label>
                <select [(ngModel)]="formData.waterBoxId" name="waterBoxId" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400">
                  <option value="">Ninguna</option>
                  @for (box of allWaterBoxes(); track box.id) {
                    <option [value]="box.id">{{ box.boxCode }}</option>
                  }
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Asunto *</label>
                <input type="text" [(ngModel)]="formData.subject" name="subject" required class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400" placeholder="Ej: Agua turbia en mi suministro">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Descripción *</label>
                <textarea [(ngModel)]="formData.description" name="description" required rows="4" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 resize-none" placeholder="Describe tu queja o reclamo"></textarea>
              </div>
              <div class="flex gap-3 pt-4">
                <button type="button" (click)="closeCreateModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">
                  Cancelar
                </button>
                <button type="submit" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-violet-600 hover:bg-violet-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                  {{ saving() ? 'Guardando...' : 'Guardar' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Detail Modal with Responses -->
      @if (showDetailModal() && selectedComplaint()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeDetailModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-3xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Detalle de Queja</h3>
              <button (click)="closeDetailModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <div class="p-6 space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div><p class="text-xs text-gray-400">Código</p><p class="text-sm font-medium text-violet-700">{{ selectedComplaint()!.complaintCode }}</p></div>
                <div><p class="text-xs text-gray-400">Fecha</p><p class="text-sm text-gray-700">{{ selectedComplaint()!.complaintDate | date:'dd/MM/yyyy HH:mm' }}</p></div>
                <div><p class="text-xs text-gray-400">Usuario</p><p class="text-sm text-gray-700">{{ getUserName(selectedComplaint()!.userId) }}</p></div>
                <div><p class="text-xs text-gray-400">Categoría</p><p class="text-sm text-gray-700">{{ getCategoryName(selectedComplaint()!.categoryId) }}</p></div>
                <div><p class="text-xs text-gray-400">Prioridad</p>
                  <span [class]="getPriorityBadge(selectedComplaint()!.priority)" class="text-xs px-2 py-1 rounded-lg font-medium inline-block">{{ getPriorityLabel(selectedComplaint()!.priority) }}</span>
                </div>
                <div><p class="text-xs text-gray-400">Estado</p>
                  <span [class]="getStatusBadge(selectedComplaint()!.status)" class="text-xs px-2 py-1 rounded-lg font-medium inline-block">{{ getStatusLabel(selectedComplaint()!.status) }}</span>
                </div>
              </div>
              <div><p class="text-xs text-gray-400 mb-1">Asunto</p><p class="text-sm font-medium text-gray-800">{{ selectedComplaint()!.subject }}</p></div>
              <div><p class="text-xs text-gray-400 mb-1">Descripción</p><p class="text-sm text-gray-700">{{ selectedComplaint()!.description }}</p></div>

              @if (selectedComplaint()!.satisfactionRating) {
                <div class="bg-emerald-50 rounded-xl p-4">
                  <p class="text-xs text-emerald-600 font-medium mb-2">Calificación de Satisfacción</p>
                  <div class="flex gap-1">
                    @for (n of [1,2,3,4,5]; track n) {
                      <lucide-icon [img]="starIcon" [size]="18" [class]="n <= selectedComplaint()!.satisfactionRating! ? 'text-yellow-400 fill-yellow-400' : 'text-gray-300'"></lucide-icon>
                    }
                  </div>
                </div>
              }

              @if (selectedComplaint()!.status === 'RESOLVED') {
                <div class="border-t border-gray-100 pt-4">
                  <button (click)="openCloseModal()" class="w-full px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-medium transition-colors">
                    Cerrar Queja
                  </button>
                </div>
              }
            </div>
          </div>
        </div>
      }

      <!-- Add Response Modal -->
      @if (showResponseModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-[60] flex items-center justify-center p-4" (click)="closeResponseModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-lg w-full" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Agregar Respuesta</h3>
              <button (click)="closeResponseModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="addResponse()" class="p-6 space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Tipo de Respuesta *</label>
                <div class="flex flex-col gap-2">
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input type="radio" [(ngModel)]="responseData.responseType" name="responseType" value="INVESTIGACION" required class="w-4 h-4 text-blue-600">
                    <span class="text-sm text-gray-700">Investigación</span>
                  </label>
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input type="radio" [(ngModel)]="responseData.responseType" name="responseType" value="SOLUCION" required class="w-4 h-4 text-emerald-600">
                    <span class="text-sm text-gray-700">Solución</span>
                  </label>
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input type="radio" [(ngModel)]="responseData.responseType" name="responseType" value="SEGUIMIENTO" required class="w-4 h-4 text-amber-600">
                    <span class="text-sm text-gray-700">Seguimiento</span>
                  </label>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Mensaje *</label>
                <textarea [(ngModel)]="responseData.message" name="message" required rows="4" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 resize-none" placeholder="Escribe tu respuesta"></textarea>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Notas Internas (Opcional)</label>
                <textarea [(ngModel)]="responseData.internalNotes" name="internalNotes" rows="2" class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-violet-500/20 focus:border-violet-400 resize-none" placeholder="Notas privadas"></textarea>
              </div>
              <div class="flex gap-3">
                <button type="button" (click)="closeResponseModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">Cancelar</button>
                <button type="submit" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50">
                  {{ saving() ? 'Enviando...' : 'Enviar Respuesta' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Close Complaint Modal (with rating) -->
      @if (showCloseModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-[60] flex items-center justify-center p-4" (click)="closeCloseModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-md w-full" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Cerrar Queja</h3>
              <button (click)="closeCloseModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="closeComplaint()" class="p-6 space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Calificación de Satisfacción (Opcional)</label>
                <div class="flex gap-1 justify-center">
                  @for (n of [1,2,3,4,5]; track n) {
                    <button type="button" (click)="satisfactionRating = n" class="p-2 hover:scale-110 transition-transform">
                      <lucide-icon [img]="starIcon" [size]="32" [class]="n <= satisfactionRating ? 'text-yellow-400 fill-yellow-400' : 'text-gray-300'"></lucide-icon>
                    </button>
                  }
                </div>
              </div>
              <div class="bg-gray-50 rounded-xl p-3 text-sm text-gray-600">
                <p>Al cerrar esta queja, ya no podrás modificarla ni agregar más respuestas.</p>
              </div>
              <div class="flex gap-3">
                <button type="button" (click)="closeCloseModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">Cancelar</button>
                <button type="submit" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50">
                  {{ saving() ? 'Cerrando...' : 'Cerrar Queja' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `
})
export class ComplaintsComponent implements OnInit {
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private claimsService = inject(ClaimsService);
  private http = inject(HttpClient);

  // Icons
  complaintIcon = MessageSquareWarning; searchIcon = Search; plusIcon = Plus; eyeIcon = Eye;
  xIcon = X; chevronLeftIcon = ChevronLeft; chevronRightIcon = ChevronRight;
  messageIcon = MessageSquare; starIcon = Star; checkIcon = CheckCircle;

  Math = Math;

  // Data signals
  allComplaints = signal<Complaint[]>([]);
  allComplaintCategories = signal<ComplaintCategory[]>([]);
  allUsers = signal<User[]>([]);
  allWaterBoxes = signal<WaterBox[]>([]);
  complaintResponses = signal<ComplaintResponse[]>([]);

  // Filters
  searchTerm = '';
  statusFilter = '';
  priorityFilter = '';

  // Pagination
  currentPage = signal(1);
  pageSize = 15;

  // Modals
  showCreateModal = signal(false);
  showDetailModal = signal(false);
  showResponseModal = signal(false);
  showCloseModal = signal(false);
  selectedComplaint = signal<Complaint | null>(null);
  saving = signal(false);

  // Form data
  formData: any = {};
  responseData: AddComplaintResponseRequest = { responseType: 'INVESTIGACION', message: '', internalNotes: '' };
  satisfactionRating = 0;

  // Computed
  receivedCount = computed(() => this.allComplaints().filter(c => c.status === 'RECEIVED').length);
  inProgressCount = computed(() => this.allComplaints().filter(c => c.status === 'IN_PROGRESS').length);
  resolvedCount = computed(() => this.allComplaints().filter(c => c.status === 'RESOLVED').length);

  filteredComplaints = computed(() => {
    let list = this.allComplaints();
    if (this.statusFilter) list = list.filter(c => c.status === this.statusFilter);
    if (this.priorityFilter) list = list.filter(c => c.priority === this.priorityFilter);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(c =>
        c.complaintCode.toLowerCase().includes(q) ||
        c.subject.toLowerCase().includes(q)
      );
    }
    return list.sort((a, b) => new Date(b.complaintDate).getTime() - new Date(a.complaintDate).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredComplaints().length / this.pageSize));
  paginatedComplaints = computed(() => {
    const s = (this.currentPage() - 1) * this.pageSize;
    return this.filteredComplaints().slice(s, s + this.pageSize);
  });

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.claimsService.getComplaintsByOrg(orgId).subscribe({
      next: r => this.allComplaints.set((r.data || []).filter(c => c.recordStatus === 'ACTIVE'))
    });

    this.claimsService.getActiveComplaintCategoriesByOrg(orgId).subscribe({
      next: r => this.allComplaintCategories.set(r.data || [])
    });

    this.http.get<ApiResponse<User[]>>(`${environment.apiUrl}/users`).subscribe({
      next: r => this.allUsers.set((r.data || []).filter(u => u.organizationId === orgId))
    });

    this.http.get<ApiResponse<WaterBox[]>>(`${environment.apiUrl}/water-boxes`).subscribe({
      next: r => this.allWaterBoxes.set(r.data || [])
    });
  }

  openCreateModal(): void {
    this.formData = {
      categoryId: '',
      waterBoxId: '',
      subject: '',
      description: '',
      priority: ''
    };
    this.showCreateModal.set(true);
  }

  closeCreateModal(): void {
    this.showCreateModal.set(false);
    this.formData = {};
  }

  saveComplaint(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    const req: CreateComplaintRequest = { ...this.formData, organizationId: orgId };

    this.saving.set(true);
    this.claimsService.createComplaint(req).subscribe({
      next: () => {
        this.alertService.success('Queja creada exitosamente');
        this.closeCreateModal();
        this.loadData();
        this.saving.set(false);
      },
      error: () => {
        this.alertService.error('Error al crear la queja');
        this.saving.set(false);
      }
    });
  }

  viewDetail(complaint: Complaint): void {
    this.selectedComplaint.set(complaint);
    this.showDetailModal.set(true);
    // TODO: Load responses for this complaint
    this.complaintResponses.set([]);
  }

  closeDetailModal(): void {
    this.showDetailModal.set(false);
    this.selectedComplaint.set(null);
    this.complaintResponses.set([]);
  }

  openResponseModal(): void {
    this.responseData = { responseType: 'INVESTIGACION', message: '', internalNotes: '' };
    this.showResponseModal.set(true);
  }

  closeResponseModal(): void {
    this.showResponseModal.set(false);
    this.responseData = { responseType: 'INVESTIGACION', message: '', internalNotes: '' };
  }

  addResponse(): void {
    const c = this.selectedComplaint();
    if (!c) return;

    this.saving.set(true);
    this.claimsService.addComplaintResponse(c.id, this.responseData).subscribe({
      next: () => {
        this.alertService.success('Respuesta agregada exitosamente');
        this.closeResponseModal();
        this.viewDetail(c); // Reload
        this.saving.set(false);
      },
      error: () => {
        this.alertService.error('Error al agregar respuesta');
        this.saving.set(false);
      }
    });
  }

  openCloseModal(): void {
    this.satisfactionRating = 0;
    this.showCloseModal.set(true);
  }

  closeCloseModal(): void {
    this.showCloseModal.set(false);
    this.satisfactionRating = 0;
  }

  closeComplaint(): void {
    const c = this.selectedComplaint();
    if (!c) return;

    this.saving.set(true);
    this.claimsService.closeComplaint(c.id, this.satisfactionRating > 0 ? this.satisfactionRating : undefined).subscribe({
      next: () => {
        this.alertService.success('Queja cerrada exitosamente');
        this.closeCloseModal();
        this.closeDetailModal();
        this.loadData();
        this.saving.set(false);
      },
      error: () => {
        this.alertService.error('Error al cerrar la queja');
        this.saving.set(false);
      }
    });
  }

  getUserName(userId: string): string {
    const u = this.allUsers().find(x => x.id === userId);
    return u ? `${u.lastName}, ${u.firstName}` : userId;
  }

  getCategoryName(categoryId: string): string {
    return this.allComplaintCategories().find(c => c.id === categoryId)?.categoryName || categoryId;
  }

  getPriorityLabel(p: string): string {
    const map: Record<string, string> = { LOW: 'Baja', MEDIUM: 'Media', HIGH: 'Alta', URGENT: 'Urgente' };
    return map[p] || p;
  }

  getPriorityBadge(p: string): string {
    const map: Record<string, string> = {
      LOW: 'bg-gray-100 text-gray-700',
      MEDIUM: 'bg-amber-50 text-amber-700',
      HIGH: 'bg-orange-50 text-orange-700',
      URGENT: 'bg-red-50 text-red-700'
    };
    return map[p] || 'bg-gray-100 text-gray-600';
  }

  getStatusLabel(s: string): string {
    const map: Record<string, string> = {
      RECEIVED: 'Recibida',
      IN_PROGRESS: 'En Proceso',
      RESOLVED: 'Resuelta',
      CLOSED: 'Cerrada'
    };
    return map[s] || s;
  }

  getStatusBadge(s: string): string {
    const map: Record<string, string> = {
      RECEIVED: 'bg-amber-50 text-amber-700',
      IN_PROGRESS: 'bg-blue-50 text-blue-700',
      RESOLVED: 'bg-emerald-50 text-emerald-700',
      CLOSED: 'bg-gray-100 text-gray-600'
    };
    return map[s] || 'bg-gray-100 text-gray-600';
  }

  getResponseTypeLabel(t: string): string {
    const map: Record<string, string> = {
      INVESTIGACION: 'Investigación',
      SOLUCION: 'Solución',
      SEGUIMIENTO: 'Seguimiento'
    };
    return map[t] || t;
  }

  getResponseTypeBadge(t: string): string {
    const map: Record<string, string> = {
      INVESTIGACION: 'bg-blue-100 text-blue-700',
      SOLUCION: 'bg-emerald-100 text-emerald-700',
      SEGUIMIENTO: 'bg-amber-100 text-amber-700'
    };
    return map[t] || 'bg-gray-100 text-gray-700';
  }
}
