import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule, Search, Plus, Eye, X, Edit, Trash2, RotateCcw,
  ChevronLeft, ChevronRight, FlaskConical, CheckCircle, AlertTriangle, XCircle
} from 'lucide-angular';
import { AuthService } from '../../../../core/services/auth.service';
import { AlertService } from '../../../../core/services/alert.service';
import { WaterQualityService } from '../../../../core/services/water-quality.service';
import { QualityTest, CreateQualityTestRequest, UpdateQualityTestRequest, TestingPoint } from '../../../../core/models/water-quality.model';

@Component({
  selector: 'app-quality-tests',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Pruebas de Calidad</h1>
          <p class="text-sm text-gray-500 mt-1">Gestión de pruebas y análisis de calidad del agua</p>
        </div>
        <button (click)="openCreateModal()" class="px-4 py-2.5 bg-teal-600 hover:bg-teal-700 text-white rounded-xl font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
          <span>Nueva Prueba</span>
        </button>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Total</span>
            <div class="p-2 rounded-xl bg-teal-50"><lucide-icon [img]="flaskIcon" [size]="16" class="text-teal-600"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-gray-800">{{ allTests().length }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Aceptable</span>
            <div class="p-2 rounded-xl bg-emerald-50"><lucide-icon [img]="checkIcon" [size]="16" class="text-emerald-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-emerald-600">{{ acceptableCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Advertencia</span>
            <div class="p-2 rounded-xl bg-amber-50"><lucide-icon [img]="warningIcon" [size]="16" class="text-amber-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-amber-600">{{ warningCount() }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-400 uppercase tracking-wider">Crítico</span>
            <div class="p-2 rounded-xl bg-red-50"><lucide-icon [img]="criticalIcon" [size]="16" class="text-red-500"></lucide-icon></div>
          </div>
          <p class="text-2xl font-bold text-red-600">{{ criticalCount() }}</p>
        </div>
      </div>

      <!-- Table -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center gap-3">
          <div class="relative flex-1">
            <lucide-icon [img]="searchIcon" [size]="16" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></lucide-icon>
            <input type="text" [(ngModel)]="searchTerm" placeholder="Buscar por punto o tipo..."
                   class="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400 outline-none transition-all placeholder:text-gray-400">
          </div>
          <select [(ngModel)]="typeFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todos los Tipos</option>
            <option value="BACTERIOLOGICO">Bacteriológico</option>
            <option value="FISICOQUIMICO">Fisicoquímico</option>
            <option value="CLORO_RESIDUAL">Cloro Residual</option>
            <option value="TURBIDEZ">Turbidez</option>
            <option value="PH">pH</option>
            <option value="COMPLETO">Completo</option>
          </select>
          <select [(ngModel)]="resultFilter" class="px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm outline-none">
            <option value="">Todos los Resultados</option>
            <option value="ACCEPTABLE">Aceptable</option>
            <option value="WARNING">Advertencia</option>
            <option value="CRITICAL">Crítico</option>
          </select>
        </div>

        <!-- Desktop table -->
        <div class="hidden md:block overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="bg-gray-50/80">
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Punto</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Tipo</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Fecha</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Cloro</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">pH</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Turbidez</th>
                <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Resultado</th>
                <th class="text-center text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">Acciones</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              @for (test of paginatedTests(); track test.id) {
                <tr class="hover:bg-gray-50/50 transition-colors">
                  <td class="px-4 py-3 text-sm font-medium text-teal-700">{{ getPointName(test.testingPointId) }}</td>
                  <td class="px-4 py-3">
                    <span class="text-xs px-2 py-1 rounded-lg font-medium bg-slate-100 text-slate-700">{{ getTestTypeLabel(test.testType) }}</span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-600">{{ test.testDate | date:'dd/MM/yyyy' }}</td>
                  <td class="px-4 py-3 text-sm text-gray-700 text-center font-mono">{{ test.chlorineLevel != null ? test.chlorineLevel : '—' }}</td>
                  <td class="px-4 py-3 text-sm text-gray-700 text-center font-mono">{{ test.phLevel != null ? test.phLevel : '—' }}</td>
                  <td class="px-4 py-3 text-sm text-gray-700 text-center font-mono">{{ test.turbidityLevel != null ? test.turbidityLevel : '—' }}</td>
                  <td class="px-4 py-3">
                    <span class="text-xs px-2 py-1 rounded-lg font-medium" [class]="getResultBadge(test.testResult)">
                      {{ getResultLabel(test.testResult) }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button (click)="viewDetail(test)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-teal-600 transition-colors" title="Ver detalle">
                        <lucide-icon [img]="eyeIcon" [size]="15"></lucide-icon>
                      </button>
                      @if (test.recordStatus === 'ACTIVE') {
                        <button (click)="openEditModal(test)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-blue-600 transition-colors" title="Editar">
                          <lucide-icon [img]="editIcon" [size]="15"></lucide-icon>
                        </button>
                        <button (click)="confirmDelete(test)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-red-600 transition-colors" title="Eliminar">
                          <lucide-icon [img]="trashIcon" [size]="15"></lucide-icon>
                        </button>
                      } @else {
                        <button (click)="restoreTest(test)" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-emerald-600 transition-colors" title="Restaurar">
                          <lucide-icon [img]="restoreIcon" [size]="15"></lucide-icon>
                        </button>
                      }
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="8" class="text-center py-12 text-gray-400 text-sm">No se encontraron pruebas de calidad</td></tr>
              }
            </tbody>
          </table>
        </div>

        <!-- Mobile cards -->
        <div class="md:hidden divide-y divide-gray-50">
          @for (test of paginatedTests(); track test.id) {
            <div class="p-4 space-y-2">
              <div class="flex items-center justify-between">
                <span class="text-sm font-medium text-teal-700">{{ getPointName(test.testingPointId) }}</span>
                <span class="text-xs px-2 py-1 rounded-lg font-medium" [class]="getResultBadge(test.testResult)">
                  {{ getResultLabel(test.testResult) }}
                </span>
              </div>
              <div class="flex items-center gap-2 flex-wrap">
                <span class="text-xs px-2 py-1 rounded-lg font-medium bg-slate-100 text-slate-700">{{ getTestTypeLabel(test.testType) }}</span>
                <span class="text-xs text-gray-400">{{ test.testDate | date:'dd/MM/yyyy' }}</span>
              </div>
              <div class="flex items-center gap-4 text-xs text-gray-500">
                <span>Cl: <strong class="text-gray-700">{{ test.chlorineLevel }}</strong></span>
                <span>pH: <strong class="text-gray-700">{{ test.phLevel }}</strong></span>
                <span>Turb: <strong class="text-gray-700">{{ test.turbidityLevel }}</strong></span>
              </div>
              <div class="flex gap-2 pt-1">
                <button (click)="viewDetail(test)" class="flex-1 py-1.5 text-xs border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 transition-colors">Ver detalle</button>
                @if (test.recordStatus === 'ACTIVE') {
                  <button (click)="openEditModal(test)" class="flex-1 py-1.5 text-xs border border-blue-200 rounded-lg text-blue-600 hover:bg-blue-50 transition-colors">Editar</button>
                  <button (click)="confirmDelete(test)" class="flex-1 py-1.5 text-xs border border-red-200 rounded-lg text-red-600 hover:bg-red-50 transition-colors">Eliminar</button>
                } @else {
                  <button (click)="restoreTest(test)" class="flex-1 py-1.5 text-xs border border-emerald-200 rounded-lg text-emerald-600 hover:bg-emerald-50 transition-colors">Restaurar</button>
                }
              </div>
            </div>
          } @empty {
            <div class="p-8 text-center text-gray-400 text-sm">No se encontraron pruebas de calidad</div>
          }
        </div>

        <!-- Pagination -->
        @if (filteredTests().length > pageSize) {
          <div class="p-4 border-t border-gray-100 flex items-center justify-between text-sm text-gray-500">
            <span>{{ (currentPage() - 1) * pageSize + 1 }} - {{ Math.min(currentPage() * pageSize, filteredTests().length) }} de {{ filteredTests().length }}</span>
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

      <!-- Create / Edit Modal -->
      @if (showFormModal()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeFormModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">{{ editingTest() ? 'Editar Prueba de Calidad' : 'Nueva Prueba de Calidad' }}</h3>
              <button (click)="closeFormModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <form (ngSubmit)="saveTest()" class="p-6 space-y-4">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Punto de Muestreo *</label>
                  <select [(ngModel)]="formData.testingPointId" name="testingPointId" required
                          class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400">
                    <option value="">Seleccionar...</option>
                    @for (point of activePoints(); track point.id) {
                      <option [value]="point.id">{{ point.pointName }}</option>
                    }
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tipo de Prueba *</label>
                  <select [(ngModel)]="formData.testType" name="testType" required
                          class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400">
                    <option value="">Seleccionar...</option>
                    <option value="BACTERIOLOGICO">Bacteriológico</option>
                    <option value="FISICOQUIMICO">Fisicoquímico</option>
                    <option value="CLORO_RESIDUAL">Cloro Residual</option>
                    <option value="TURBIDEZ">Turbidez</option>
                    <option value="PH">pH</option>
                    <option value="COMPLETO">Completo</option>
                  </select>
                </div>
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Fecha de Prueba</label>
                  <input type="datetime-local" [(ngModel)]="formData.testDate" name="testDate"
                         class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Resultado *</label>
                  <select [(ngModel)]="formData.testResult" name="testResult" required
                          class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400">
                    <option value="">Seleccionar...</option>
                    <option value="ACCEPTABLE">Aceptable</option>
                    <option value="WARNING">Advertencia</option>
                    <option value="CRITICAL">Crítico</option>
                  </select>
                </div>
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Nivel de Cloro (mg/L)</label>
                  <input type="number" [(ngModel)]="formData.chlorineLevel" name="chlorineLevel" step="0.01" min="0"
                         class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400"
                         placeholder="0.00">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Nivel de pH</label>
                  <input type="number" [(ngModel)]="formData.phLevel" name="phLevel" step="0.01" min="0" max="14"
                         class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400"
                         placeholder="7.00">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Turbidez (NTU)</label>
                  <input type="number" [(ngModel)]="formData.turbidityLevel" name="turbidityLevel" step="0.01" min="0"
                         class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400"
                         placeholder="0.00">
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Observaciones</label>
                <textarea [(ngModel)]="formData.observations" name="observations" rows="3"
                          class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400 resize-none"
                          placeholder="Observaciones adicionales sobre la prueba"></textarea>
              </div>
              <div class="flex items-center gap-6">
                <label class="flex items-center gap-2 cursor-pointer">
                  <input type="checkbox" [(ngModel)]="formData.treatmentApplied" name="treatmentApplied" class="w-4 h-4 text-teal-600 rounded">
                  <span class="text-sm text-gray-700">Tratamiento Aplicado</span>
                </label>
              </div>
              @if (formData.treatmentApplied) {
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Descripción del Tratamiento</label>
                  <textarea [(ngModel)]="formData.treatmentDescription" name="treatmentDescription" rows="2"
                            class="w-full px-3 py-2 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-400 resize-none"
                            placeholder="Describa el tratamiento aplicado"></textarea>
                </div>
              }
              <div class="flex gap-3 pt-4">
                <button type="button" (click)="closeFormModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">
                  Cancelar
                </button>
                <button type="submit" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-teal-600 hover:bg-teal-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed">
                  {{ saving() ? 'Guardando...' : (editingTest() ? 'Actualizar' : 'Guardar') }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Detail Modal -->
      @if (showDetailModal() && selectedTest()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeDetailModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[85vh] overflow-y-auto" (click)="$event.stopPropagation()">
            <div class="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-800">Detalle de Prueba de Calidad</h3>
              <button (click)="closeDetailModal()" class="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 transition-colors">
                <lucide-icon [img]="xIcon" [size]="18"></lucide-icon>
              </button>
            </div>
            <div class="p-6 space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div><p class="text-xs text-gray-400">Punto de Muestreo</p><p class="text-sm font-medium text-teal-700">{{ getPointName(selectedTest()!.testingPointId) }}</p></div>
                <div><p class="text-xs text-gray-400">Tipo de Prueba</p><p class="text-sm text-gray-700">{{ getTestTypeLabel(selectedTest()!.testType) }}</p></div>
                <div><p class="text-xs text-gray-400">Fecha</p><p class="text-sm text-gray-700">{{ selectedTest()!.testDate | date:'dd/MM/yyyy HH:mm' }}</p></div>
                <div><p class="text-xs text-gray-400">Resultado</p>
                  <span class="text-xs px-2 py-1 rounded-lg font-medium inline-block" [class]="getResultBadge(selectedTest()!.testResult)">{{ getResultLabel(selectedTest()!.testResult) }}</span>
                </div>
              </div>

              <div class="grid grid-cols-3 gap-4 p-4 bg-gray-50 rounded-xl">
                <div class="text-center">
                  <p class="text-xs text-gray-400 mb-1">Cloro (mg/L)</p>
                  <p class="text-lg font-bold text-gray-800">{{ selectedTest()!.chlorineLevel }}</p>
                </div>
                <div class="text-center">
                  <p class="text-xs text-gray-400 mb-1">pH</p>
                  <p class="text-lg font-bold text-gray-800">{{ selectedTest()!.phLevel }}</p>
                </div>
                <div class="text-center">
                  <p class="text-xs text-gray-400 mb-1">Turbidez (NTU)</p>
                  <p class="text-lg font-bold text-gray-800">{{ selectedTest()!.turbidityLevel }}</p>
                </div>
              </div>

              @if (selectedTest()!.observations) {
                <div><p class="text-xs text-gray-400 mb-1">Observaciones</p><p class="text-sm text-gray-700">{{ selectedTest()!.observations }}</p></div>
              }
              @if (selectedTest()!.treatmentApplied) {
                <div class="bg-amber-50 rounded-xl p-4">
                  <p class="text-xs text-amber-600 font-medium mb-1">Tratamiento Aplicado</p>
                  <p class="text-sm text-amber-800">{{ selectedTest()!.treatmentDescription || 'Sin descripción' }}</p>
                </div>
              }
              <div class="grid grid-cols-2 gap-4 pt-2 border-t border-gray-100">
                <div><p class="text-xs text-gray-400">Creado</p><p class="text-sm text-gray-700">{{ selectedTest()!.createdAt | date:'dd/MM/yyyy HH:mm' }}</p></div>
                <div><p class="text-xs text-gray-400">Actualizado</p><p class="text-sm text-gray-700">{{ selectedTest()!.updatedAt ? (selectedTest()!.updatedAt | date:'dd/MM/yyyy HH:mm') : '—' }}</p></div>
              </div>
            </div>
          </div>
        </div>
      }

      <!-- Delete Confirmation Modal -->
      @if (showDeleteModal() && selectedTest()) {
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4" (click)="closeDeleteModal()">
          <div class="bg-white rounded-2xl shadow-xl max-w-md w-full" (click)="$event.stopPropagation()">
            <div class="p-6 text-center space-y-4">
              <div class="mx-auto w-12 h-12 rounded-full bg-red-50 flex items-center justify-center">
                <lucide-icon [img]="trashIcon" [size]="24" class="text-red-500"></lucide-icon>
              </div>
              <div>
                <h3 class="text-lg font-semibold text-gray-800">Eliminar Prueba de Calidad</h3>
                <p class="text-sm text-gray-500 mt-1">¿Está seguro de eliminar esta prueba? Esta acción se puede revertir.</p>
              </div>
              <div class="flex gap-3">
                <button (click)="closeDeleteModal()" class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors">Cancelar</button>
                <button (click)="deleteTest()" [disabled]="saving()" class="flex-1 px-4 py-2.5 bg-red-600 hover:bg-red-700 text-white rounded-xl font-medium transition-colors disabled:opacity-50">
                  {{ saving() ? 'Eliminando...' : 'Eliminar' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class QualityTestsComponent implements OnInit {
  private authService = inject(AuthService);
  private alertService = inject(AlertService);
  private wqService = inject(WaterQualityService);

  // Icons
  searchIcon = Search; plusIcon = Plus; eyeIcon = Eye; xIcon = X;
  editIcon = Edit; trashIcon = Trash2; restoreIcon = RotateCcw;
  chevronLeftIcon = ChevronLeft; chevronRightIcon = ChevronRight;
  flaskIcon = FlaskConical; checkIcon = CheckCircle; warningIcon = AlertTriangle; criticalIcon = XCircle;

  Math = Math;

  // Data
  allTests = signal<QualityTest[]>([]);
  allPoints = signal<TestingPoint[]>([]);

  // Filters
  searchTerm = '';
  typeFilter = '';
  resultFilter = '';

  // Pagination
  currentPage = signal(1);
  pageSize = 15;

  // Modals
  showFormModal = signal(false);
  showDetailModal = signal(false);
  showDeleteModal = signal(false);
  selectedTest = signal<QualityTest | null>(null);
  editingTest = signal<QualityTest | null>(null);
  saving = signal(false);

  // Form
  formData: any = {};

  // Computed
  activePoints = computed(() => this.allPoints().filter(p => p.recordStatus === 'ACTIVE'));
  acceptableCount = computed(() => this.allTests().filter(t => t.testResult === 'ACCEPTABLE').length);
  warningCount = computed(() => this.allTests().filter(t => t.testResult === 'WARNING').length);
  criticalCount = computed(() => this.allTests().filter(t => t.testResult === 'CRITICAL').length);

  filteredTests = computed(() => {
    let list = this.allTests();
    if (this.typeFilter) list = list.filter(t => t.testType === this.typeFilter);
    if (this.resultFilter) list = list.filter(t => t.testResult === this.resultFilter);
    if (this.searchTerm.trim()) {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(t =>
        this.getPointName(t.testingPointId).toLowerCase().includes(q) ||
        this.getTestTypeLabel(t.testType).toLowerCase().includes(q)
      );
    }
    return list.sort((a, b) => new Date(b.testDate || b.createdAt).getTime() - new Date(a.testDate || a.createdAt).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredTests().length / this.pageSize));
  paginatedTests = computed(() => {
    const s = (this.currentPage() - 1) * this.pageSize;
    return this.filteredTests().slice(s, s + this.pageSize);
  });

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.wqService.getQualityTestsByOrg(orgId).subscribe({
      next: r => this.allTests.set(r.data || [])
    });

    this.wqService.getTestingPointsByOrg(orgId).subscribe({
      next: r => this.allPoints.set(r.data || [])
    });
  }

  // ============================================================================
  // MODALS
  // ============================================================================

  openCreateModal(): void {
    this.editingTest.set(null);
    this.formData = {
      testingPointId: '', testType: '', testDate: '', testResult: '',
      chlorineLevel: null, phLevel: null, turbidityLevel: null,
      observations: '', treatmentApplied: false, treatmentDescription: ''
    };
    this.showFormModal.set(true);
  }

  openEditModal(test: QualityTest): void {
    this.editingTest.set(test);
    this.formData = {
      testingPointId: test.testingPointId,
      testType: test.testType,
      testDate: test.testDate ? test.testDate.substring(0, 16) : '',
      testResult: test.testResult,
      chlorineLevel: test.chlorineLevel,
      phLevel: test.phLevel,
      turbidityLevel: test.turbidityLevel,
      observations: test.observations || '',
      treatmentApplied: test.treatmentApplied || false,
      treatmentDescription: test.treatmentDescription || ''
    };
    this.showFormModal.set(true);
  }

  closeFormModal(): void {
    this.showFormModal.set(false);
    this.editingTest.set(null);
    this.formData = {};
  }

  viewDetail(test: QualityTest): void {
    this.selectedTest.set(test);
    this.showDetailModal.set(true);
  }

  closeDetailModal(): void {
    this.showDetailModal.set(false);
    this.selectedTest.set(null);
  }

  confirmDelete(test: QualityTest): void {
    this.selectedTest.set(test);
    this.showDeleteModal.set(true);
  }

  closeDeleteModal(): void {
    this.showDeleteModal.set(false);
    this.selectedTest.set(null);
  }

  // ============================================================================
  // CRUD
  // ============================================================================

  saveTest(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    this.saving.set(true);
    const editing = this.editingTest();

    if (editing) {
      const req: UpdateQualityTestRequest = { ...this.formData };
      this.wqService.updateQualityTest(editing.id, req).subscribe({
        next: () => {
          this.alertService.success('Prueba de calidad actualizada exitosamente');
          this.closeFormModal();
          this.loadData();
          this.saving.set(false);
        },
        error: () => {
          this.alertService.error('Error al actualizar la prueba de calidad');
          this.saving.set(false);
        }
      });
    } else {
      const req: CreateQualityTestRequest = { ...this.formData, organizationId: orgId };
      this.wqService.createQualityTest(req).subscribe({
        next: () => {
          this.alertService.success('Prueba de calidad creada exitosamente');
          this.closeFormModal();
          this.loadData();
          this.saving.set(false);
        },
        error: () => {
          this.alertService.error('Error al crear la prueba de calidad');
          this.saving.set(false);
        }
      });
    }
  }

  deleteTest(): void {
    const test = this.selectedTest();
    if (!test) return;

    this.saving.set(true);
    this.wqService.deleteQualityTest(test.id).subscribe({
      next: () => {
        this.alertService.success('Prueba de calidad eliminada exitosamente');
        this.closeDeleteModal();
        this.loadData();
        this.saving.set(false);
      },
      error: () => {
        this.alertService.error('Error al eliminar la prueba de calidad');
        this.saving.set(false);
      }
    });
  }

  restoreTest(test: QualityTest): void {
    this.wqService.restoreQualityTest(test.id).subscribe({
      next: () => {
        this.alertService.success('Prueba de calidad restaurada exitosamente');
        this.loadData();
      },
      error: () => {
        this.alertService.error('Error al restaurar la prueba de calidad');
      }
    });
  }

  // ============================================================================
  // HELPERS
  // ============================================================================

  getPointName(pointId: string): string {
    if (!pointId) return '—';
    return this.allPoints().find(p => p.id === pointId)?.pointName || pointId;
  }

  getTestTypeLabel(type: string): string {
    const map: Record<string, string> = {
      BACTERIOLOGICO: 'Bacteriológico',
      FISICOQUIMICO: 'Fisicoquímico',
      CLORO_RESIDUAL: 'Cloro Residual',
      TURBIDEZ: 'Turbidez',
      PH: 'pH',
      COMPLETO: 'Completo'
    };
    return map[type] || type;
  }

  getResultLabel(result: string): string {
    const map: Record<string, string> = { ACCEPTABLE: 'Aceptable', WARNING: 'Advertencia', CRITICAL: 'Crítico' };
    return map[result] || result;
  }

  getResultBadge(result: string): string {
    const map: Record<string, string> = {
      ACCEPTABLE: 'bg-emerald-50 text-emerald-700',
      WARNING: 'bg-amber-50 text-amber-700',
      CRITICAL: 'bg-red-50 text-red-700'
    };
    return map[result] || 'bg-gray-100 text-gray-600';
  }
}
