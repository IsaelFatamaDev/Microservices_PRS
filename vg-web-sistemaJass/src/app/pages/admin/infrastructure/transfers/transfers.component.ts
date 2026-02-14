import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule, Truck, Search, ChevronLeft, ChevronRight, ChevronsLeft,
  ChevronsRight, X, FileText, UserPlus, Droplets, ArrowRight, AlertCircle,
  Download, Phone, MapPin, UserCheck, Hash, Filter
} from 'lucide-angular';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { environment } from '../../../../../environments/environment';
import {
  ApiResponse, WaterBox, WaterBoxAssignment, WaterBoxTransfer,
  TransferWaterBoxRequest, User, Zone, CreateUserRequest
} from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';

interface TransferRow extends WaterBoxTransfer {
  fromUserName?: string;
  toUserName?: string;
  boxCode?: string;
}

type TransferStep = 'idle' | 'selectOwner' | 'selectBox' | 'createUser' | 'confirm';

@Component({
  selector: 'app-transfers',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-5">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Transferencias</h1>
          <p class="text-sm text-gray-500 mt-0.5">Gestión de transferencias de Suministros</p>
        </div>
        <div class="flex items-center gap-3">
          <div class="flex items-center gap-2 px-3 py-1.5 bg-violet-50 text-violet-700 rounded-xl border border-violet-100">
            <lucide-icon [img]="truckIcon" [size]="16"></lucide-icon>
            <span class="text-sm font-semibold">{{ allTransfers().length }}</span>
            <span class="text-xs">Total</span>
          </div>
          <button (click)="startTransfer()"
            class="inline-flex items-center gap-2 px-4 py-2.5 bg-violet-600 text-white rounded-xl hover:bg-violet-700 transition-all text-sm font-medium shadow-sm">
            <lucide-icon [img]="truckIcon" [size]="18"></lucide-icon>
            <span class="hidden sm:inline">Nueva Transferencia</span>
          </button>
        </div>
      </div>

      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-4 border-b border-gray-100 bg-gray-50/30">
          <div class="flex flex-col lg:flex-row gap-3">
            <div class="relative flex-1">
              <div class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                <lucide-icon [img]="searchIcon" [size]="18"></lucide-icon>
              </div>
              <input type="text"
                [ngModel]="searchTerm()"
                (ngModelChange)="searchTerm.set($event); currentPage.set(1)"
                placeholder="Buscar por código de caja, nombre..."
                class="w-full pl-11 pr-4 py-2.5 border border-gray-200 rounded-xl bg-white text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all">
            </div>
          </div>
        </div>

        @if (isLoading()) {
          <div class="p-16 text-center">
            <div class="inline-flex items-center gap-2.5 text-gray-500">
              <div class="w-5 h-5 border-2 border-violet-600 border-t-transparent rounded-full animate-spin"></div>
              <span class="text-sm">Cargando transferencias...</span>
            </div>
          </div>
        } @else if (filteredTransfers().length === 0) {
          <div class="p-16 text-center">
            <div class="w-16 h-16 mx-auto mb-4 bg-gray-100 rounded-2xl flex items-center justify-center">
              <lucide-icon [img]="truckIcon" [size]="32" class="text-gray-400"></lucide-icon>
            </div>
            <p class="text-gray-500 font-medium">No se encontraron transferencias</p>
            <p class="text-gray-400 text-sm mt-1">Las transferencias realizadas aparecerán aquí</p>
          </div>
        } @else {

          <div class="hidden lg:block overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50/80 border-b border-gray-100">
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-14">#</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Suministro</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">De (Origen)</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">A (Destino)</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Fecha</th>
                  <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Motivo</th>
                  <th class="px-4 py-3 text-right text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-20">PDF</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (tr of paginatedTransfers(); track tr.id; let i = $index) {
                  <tr class="hover:bg-violet-50/30 transition-colors group">
                    <td class="px-4 py-3.5">
                      <span class="inline-flex items-center justify-center w-7 h-7 bg-violet-50 text-violet-600 rounded-lg text-xs font-bold">
                        {{ (currentPage() - 1) * pageSize + i + 1 }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5">
                      <span class="font-bold text-gray-800 font-mono text-sm">{{ tr.boxCode || '-' }}</span>
                    </td>
                    <td class="px-4 py-3.5 text-sm text-gray-700">{{ tr.fromUserName || tr.fromUserId }}</td>
                    <td class="px-4 py-3.5 text-sm text-gray-700">{{ tr.toUserName || tr.toUserId }}</td>
                    <td class="px-4 py-3.5 text-sm text-gray-500">{{ tr.transferDate | date:'dd/MM/yyyy HH:mm' }}</td>
                    <td class="px-4 py-3.5 text-sm text-gray-500 max-w-50 truncate">{{ tr.notes || '-' }}</td>
                    <td class="px-4 py-3.5 text-right">
                      <button (click)="downloadTransferPdf(tr)"
                        class="p-2 text-gray-500 hover:text-violet-600 hover:bg-violet-50 rounded-lg transition-all opacity-60 group-hover:opacity-100">
                        <lucide-icon [img]="downloadIcon" [size]="16"></lucide-icon>
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="lg:hidden p-3 sm:p-4 space-y-3">
            @for (tr of paginatedTransfers(); track tr.id; let i = $index) {
              <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden hover:shadow-md transition-shadow">
                <div class="flex items-stretch">
                  <div class="w-11 bg-linear-to-b from-violet-500 to-violet-600 flex items-center justify-center text-white font-bold text-sm shrink-0">
                    {{ (currentPage() - 1) * pageSize + i + 1 }}
                  </div>
                  <div class="flex-1 p-3.5 min-w-0">
                    <div class="flex items-start justify-between gap-2 mb-1.5">
                      <h4 class="font-bold text-gray-800 text-sm font-mono">{{ tr.boxCode || '-' }}</h4>
                      <span class="text-[10px] text-gray-400">{{ tr.transferDate | date:'dd/MM/yyyy' }}</span>
                    </div>
                    <div class="space-y-1 mb-2">
                      <div class="flex items-center gap-1.5 text-xs">
                        <span class="text-red-500 font-medium">De:</span>
                        <span class="text-gray-600">{{ tr.fromUserName || tr.fromUserId }}</span>
                      </div>
                      <div class="flex items-center gap-1.5 text-xs">
                        <span class="text-emerald-500 font-medium">A:</span>
                        <span class="text-gray-600">{{ tr.toUserName || tr.toUserId }}</span>
                      </div>
                      @if (tr.notes) {
                        <p class="text-xs text-gray-400 truncate">{{ tr.notes }}</p>
                      }
                    </div>
                    <div class="flex justify-end pt-1 border-t border-gray-100">
                      <button (click)="downloadTransferPdf(tr)"
                        class="p-1.5 text-violet-600 hover:bg-violet-50 rounded-lg transition-colors">
                        <lucide-icon [img]="downloadIcon" [size]="16"></lucide-icon>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            }
          </div>
        }

        @if (totalPages() > 1) {
          <div class="p-4 border-t border-gray-100 flex flex-col sm:flex-row items-center justify-between gap-3">
            <p class="text-sm text-gray-500">
              Mostrando <span class="font-semibold text-gray-700">{{ startIndex() + 1 }}</span> -
              <span class="font-semibold text-gray-700">{{ endIndex() }}</span> de
              <span class="font-semibold text-gray-700">{{ filteredTransfers().length }}</span>
            </p>
            <div class="flex items-center gap-1">
              <button (click)="currentPage.set(1)" [disabled]="currentPage() === 1"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="chevronsLeftIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
              <button (click)="currentPage.set(currentPage() - 1)" [disabled]="currentPage() === 1"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="prevIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
              @for (page of visiblePages(); track page) {
                <button (click)="currentPage.set(page)"
                  class="min-w-9 h-9 px-2 border rounded-lg text-sm font-medium transition-colors"
                  [class]="page === currentPage() ? 'bg-violet-600 text-white border-violet-600' : 'border-gray-200 text-gray-600 hover:bg-gray-50'">
                  {{ page }}
                </button>
              }
              <button (click)="currentPage.set(currentPage() + 1)" [disabled]="currentPage() >= totalPages()"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="nextIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
              <button (click)="currentPage.set(totalPages())" [disabled]="currentPage() >= totalPages()"
                class="p-2 border border-gray-200 rounded-lg disabled:opacity-30 hover:bg-gray-50 transition-colors">
                <lucide-icon [img]="chevronsRightIcon" [size]="16" class="text-gray-600"></lucide-icon>
              </button>
            </div>
          </div>
        }
      </div>

      @if (transferStep() !== 'idle') {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" (click)="cancelTransfer()"></div>
          <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto z-10">

            <div class="sticky top-0 bg-white border-b border-gray-100 px-6 py-4 flex items-center justify-between rounded-t-2xl z-20">
              <div>
                <h2 class="text-lg font-bold text-gray-800">Nueva Transferencia</h2>
                <p class="text-xs text-gray-400 mt-0.5">Paso {{ currentStepNumber() }} de 4</p>
              </div>
              <div class="flex items-center gap-3">
                <div class="hidden sm:flex items-center gap-1.5">
                  @for (s of ['selectOwner','selectBox','createUser','confirm']; track s; let si = $index) {
                    <div class="w-2.5 h-2.5 rounded-full transition-all"
                         [class]="stepIndex(s) <= stepIndex(transferStep()) ? 'bg-violet-600 scale-110' : 'bg-gray-200'"></div>
                  }
                </div>
                <button (click)="cancelTransfer()" class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-all">
                  <lucide-icon [img]="closeIcon" [size]="20"></lucide-icon>
                </button>
              </div>
            </div>

            <div class="p-6">

              @if (transferStep() === 'selectOwner') {
                <div class="space-y-4">
                  <div class="text-center mb-6">
                    <div class="w-14 h-14 mx-auto mb-3 bg-violet-100 rounded-2xl flex items-center justify-center">
                      <lucide-icon [img]="searchIcon" [size]="28" class="text-violet-600"></lucide-icon>
                    </div>
                    <h3 class="font-bold text-gray-800">Buscar propietario actual</h3>
                    <p class="text-sm text-gray-500 mt-1">Busca al usuario que desea transferir su caja de agua</p>
                  </div>

                  <div class="relative">
                    <div class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                      <lucide-icon [img]="searchIcon" [size]="18"></lucide-icon>
                    </div>
                    <input type="text"
                      [(ngModel)]="ownerSearchTerm"
                      (ngModelChange)="filterOwners()"
                      placeholder="Buscar por apellido, nombre o DNI..."
                      class="w-full pl-11 pr-4 py-3 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400 placeholder:text-gray-300">
                  </div>

                  <div class="max-h-64 overflow-y-auto space-y-2 border border-gray-100 rounded-xl p-2">
                    @if (filteredOwners().length === 0) {
                      <div class="text-center py-8 text-gray-400 text-sm">
                        <lucide-icon [img]="alertIcon" [size]="24" class="mx-auto mb-2 text-gray-300"></lucide-icon>
                        <p>No se encontraron usuarios con cajas asignadas</p>
                      </div>
                    }
                    @for (user of filteredOwners(); track user.id) {
                      <button (click)="selectOwner(user)"
                        class="w-full text-left p-3 rounded-xl border transition-all hover:shadow-sm"
                        [class]="selectedOwner()?.id === user.id ? 'border-violet-300 bg-violet-50' : 'border-gray-100 hover:border-gray-200 hover:bg-gray-50'">
                        <div class="flex items-center gap-3">
                          <div class="w-10 h-10 rounded-full bg-violet-100 text-violet-700 flex items-center justify-center text-xs font-bold shrink-0">
                            {{ (user.lastName || '').charAt(0) }}{{ (user.firstName || '').charAt(0) }}
                          </div>
                          <div class="min-w-0 flex-1">
                            <p class="font-semibold text-gray-800 text-sm truncate">{{ user.lastName }}, {{ user.firstName }}</p>
                            <p class="text-xs text-gray-400">DNI: {{ user.documentNumber }} · {{ user.phone || 'Sin teléfono' }}</p>
                          </div>
                          @if (selectedOwner()?.id === user.id) {
                            <div class="w-5 h-5 bg-violet-600 rounded-full flex items-center justify-center shrink-0">
                              <svg class="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"></path></svg>
                            </div>
                          }
                        </div>
                      </button>
                    }
                  </div>

                  <div class="flex justify-end pt-2">
                    <button (click)="goToStep('selectBox')"
                      [disabled]="!selectedOwner()"
                      class="px-6 py-2.5 bg-violet-600 text-white rounded-xl hover:bg-violet-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium">
                      Siguiente
                    </button>
                  </div>
                </div>
              }

              @if (transferStep() === 'selectBox') {
                <div class="space-y-4">
                  <div class="text-center mb-6">
                    <div class="w-14 h-14 mx-auto mb-3 bg-cyan-100 rounded-2xl flex items-center justify-center">
                      <lucide-icon [img]="dropletsIcon" [size]="28" class="text-cyan-600"></lucide-icon>
                    </div>
                    <h3 class="font-bold text-gray-800">Seleccionar caja a transferir</h3>
                    <p class="text-sm text-gray-500 mt-1">
                      Cajas de <span class="font-semibold text-gray-700">{{ selectedOwner()?.lastName }}, {{ selectedOwner()?.firstName }}</span>
                    </p>
                  </div>

                  @if (ownerBoxes().length === 0) {
                    <div class="text-center py-8 text-gray-400 text-sm border border-gray-100 rounded-xl">
                      <lucide-icon [img]="alertIcon" [size]="24" class="mx-auto mb-2 text-gray-300"></lucide-icon>
                      <p>Este usuario no tiene cajas disponibles para transferir</p>
                    </div>
                  }

                  <div class="space-y-2">
                    @for (box of ownerBoxes(); track box.id) {
                      <button (click)="selectBox(box)"
                        class="w-full text-left p-4 rounded-xl border transition-all hover:shadow-sm"
                        [class]="selectedBox()?.id === box.id ? 'border-cyan-300 bg-cyan-50' : 'border-gray-100 hover:border-gray-200 hover:bg-gray-50'">
                        <div class="flex items-center justify-between">
                          <div>
                            <p class="font-bold text-gray-800 font-mono">{{ box.boxCode }}</p>
                            <p class="text-xs text-gray-500 mt-0.5">
                              {{ getBoxTypeLabel(box.boxType) }} · {{ box.address || 'Sin dirección' }}
                            </p>
                          </div>
                          <div class="flex items-center gap-2">
                            <span class="inline-flex items-center gap-1 px-2.5 py-1 text-[11px] font-semibold rounded-full"
                                  [class]="box.isActive ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-700'">
                              {{ box.isActive ? 'Activo' : 'Suspendido' }}
                            </span>
                            @if (selectedBox()?.id === box.id) {
                              <div class="w-5 h-5 bg-cyan-600 rounded-full flex items-center justify-center">
                                <svg class="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"></path></svg>
                              </div>
                            }
                          </div>
                        </div>
                      </button>
                    }
                  </div>

                  <div class="flex justify-between pt-2">
                    <button (click)="goToStep('selectOwner')"
                      class="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-all text-sm font-medium">
                      Atrás
                    </button>
                    <button (click)="goToStep('createUser')"
                      [disabled]="!selectedBox()"
                      class="px-6 py-2.5 bg-violet-600 text-white rounded-xl hover:bg-violet-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium">
                      Siguiente
                    </button>
                  </div>
                </div>
              }

              @if (transferStep() === 'createUser') {
                <div class="space-y-4">
                  <div class="text-center mb-6">
                    <div class="w-14 h-14 mx-auto mb-3 bg-emerald-100 rounded-2xl flex items-center justify-center">
                      <lucide-icon [img]="userPlusIcon" [size]="28" class="text-emerald-600"></lucide-icon>
                    </div>
                    <h3 class="font-bold text-gray-800">Datos del nuevo responsable</h3>
                    <p class="text-sm text-gray-500 mt-1">Ingresa los datos de la persona que recibirá la caja</p>
                  </div>

                  <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">Apellidos <span class="text-red-500">*</span></label>
                      <input type="text" [(ngModel)]="newUser.lastName"
                        class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400 placeholder:text-gray-300"
                        [ngClass]="formErrors['lastName'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                        placeholder="Ej: García López">
                      @if (formErrors['lastName']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['lastName'] }}</p>
                      }
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">Nombres <span class="text-red-500">*</span></label>
                      <input type="text" [(ngModel)]="newUser.firstName"
                        class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400 placeholder:text-gray-300"
                        [ngClass]="formErrors['firstName'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                        placeholder="Ej: Juan Carlos">
                      @if (formErrors['firstName']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['firstName'] }}</p>
                      }
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">Tipo de documento</label>
                      <select [(ngModel)]="newUser.documentType" (ngModelChange)="validateForm()"
                        class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400">
                        <option value="DNI">DNI</option>
                        <option value="CNE">CNE</option>
                      </select>
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">N° Documento <span class="text-red-500">*</span></label>
                      <input type="text" [(ngModel)]="newUser.documentNumber"
                        (input)="onDocumentInput($event)"
                        [maxlength]="newUser.documentType === 'DNI' ? 8 : 20"
                        class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400 font-mono placeholder:text-gray-300"
                        [ngClass]="formErrors['documentNumber'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                        placeholder="Ej: 12345678">
                      @if (formErrors['documentNumber']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['documentNumber'] }}</p>
                      }
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">Teléfono</label>
                      <input type="text" [(ngModel)]="newUser.phone" maxlength="9"
                        (input)="onPhoneInput($event)"
                        class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400 font-mono placeholder:text-gray-300"
                        [ngClass]="formErrors['phone'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                        placeholder="Ej: 987654321">
                      @if (formErrors['phone']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['phone'] }}</p>
                      }
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">Email</label>
                      <input type="email" [(ngModel)]="newUser.email"
                        (ngModelChange)="validateForm()"
                        class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400 placeholder:text-gray-300"
                        [ngClass]="formErrors['email'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                        placeholder="Ej: correo@ejemplo.com">
                      @if (formErrors['email']) {
                        <p class="mt-1 text-xs text-red-500">{{ formErrors['email'] }}</p>
                      }
                    </div>
                    <div class="sm:col-span-2">
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">Dirección</label>
                      <input type="text" [(ngModel)]="newUser.address"
                        class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400 placeholder:text-gray-300"
                        placeholder="Ej: Jr. Los Alamos 123">
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">Zona</label>
                      <select [(ngModel)]="newUser.zoneId" (ngModelChange)="onZoneChange($event)"
                        class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400">
                        <option value="">Seleccione zona</option>
                        @for (zone of zones(); track zone.id) {
                          <option [value]="zone.id">{{ zone.zoneName || zone.name }}</option>
                        }
                      </select>
                    </div>
                    <div>
                      <label class="block text-xs font-medium text-gray-600 mb-1.5">Calle</label>
                      <select [(ngModel)]="newUser.streetId"
                        class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400"
                        [disabled]="filteredStreets().length === 0 && !newUser.zoneId">
                        <option value="">{{ !newUser.zoneId ? 'Primero seleccione zona' : (filteredStreets().length === 0 ? 'Cargando calles...' : 'Seleccione calle') }}</option>
                        @for (street of filteredStreets(); track street.id) {
                          <option [value]="street.id">{{ street.fullStreetName || street.streetName || street.name }}</option>
                        }
                      </select>
                    </div>
                  </div>

                  <div>
                    <label class="block text-xs font-medium text-gray-600 mb-1.5">Motivo de la transferencia <span class="text-red-500">*</span></label>
                    <textarea [(ngModel)]="transferNotes" (ngModelChange)="validateForm()" rows="3"
                      class="w-full px-4 py-2.5 border rounded-xl text-sm focus:ring-2 focus:ring-violet-300 focus:border-violet-400 resize-none placeholder:text-gray-300"
                      [ngClass]="formErrors['notes'] ? 'border-red-300 bg-red-50/30' : 'border-gray-200'"
                      placeholder="Ej: Venta de propiedad, cambio de titularidad..."></textarea>
                    @if (formErrors['notes']) {
                      <p class="mt-1 text-xs text-red-500">{{ formErrors['notes'] }}</p>
                    }
                  </div>

                  <div class="flex justify-between pt-2">
                    <button (click)="goToStep('selectBox')"
                      class="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-all text-sm font-medium">
                      Atrás
                    </button>
                    <button (click)="goToStep('confirm')"
                      [disabled]="!isNewUserValid()"
                      class="px-6 py-2.5 bg-violet-600 text-white rounded-xl hover:bg-violet-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium">
                      Siguiente
                    </button>
                  </div>
                </div>
              }

              @if (transferStep() === 'confirm') {
                <div class="space-y-4">
                  <div class="text-center mb-6">
                    <div class="w-14 h-14 mx-auto mb-3 bg-amber-100 rounded-2xl flex items-center justify-center">
                      <lucide-icon [img]="fileTextIcon" [size]="28" class="text-amber-600"></lucide-icon>
                    </div>
                    <h3 class="font-bold text-gray-800">Confirmar transferencia</h3>
                    <p class="text-sm text-gray-500 mt-1">Revisa los datos antes de confirmar</p>
                  </div>

                  <div class="bg-gray-50 rounded-xl p-4 space-y-4">
                    <div class="flex items-center gap-3 p-3 bg-red-50/50 rounded-lg border border-red-100">
                      <div class="w-10 h-10 rounded-full bg-red-100 text-red-700 flex items-center justify-center text-xs font-bold shrink-0">
                        {{ (selectedOwner()?.lastName || '').charAt(0) }}{{ (selectedOwner()?.firstName || '').charAt(0) }}
                      </div>
                      <div>
                        <p class="text-[10px] font-semibold text-red-500 uppercase">Transfiere</p>
                        <p class="font-semibold text-gray-800 text-sm">{{ selectedOwner()?.lastName }}, {{ selectedOwner()?.firstName }}</p>
                        <p class="text-xs text-gray-400">DNI: {{ selectedOwner()?.documentNumber }}</p>
                      </div>
                    </div>

                    <div class="flex items-center gap-3 justify-center">
                      <lucide-icon [img]="arrowRightIcon" [size]="20" class="text-violet-400"></lucide-icon>
                      <span class="px-3 py-1.5 bg-violet-100 text-violet-700 rounded-lg font-mono text-sm font-bold">
                        {{ selectedBox()?.boxCode }}
                      </span>
                      <lucide-icon [img]="arrowRightIcon" [size]="20" class="text-violet-400"></lucide-icon>
                    </div>

                    <div class="flex items-center gap-3 p-3 bg-emerald-50/50 rounded-lg border border-emerald-100">
                      <div class="w-10 h-10 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center text-xs font-bold shrink-0">
                        {{ (newUser.lastName || '').charAt(0) }}{{ (newUser.firstName || '').charAt(0) }}
                      </div>
                      <div>
                        <p class="text-[10px] font-semibold text-emerald-500 uppercase">Recibe</p>
                        <p class="font-semibold text-gray-800 text-sm">{{ newUser.lastName }}, {{ newUser.firstName }}</p>
                        <p class="text-xs text-gray-400">DNI: {{ newUser.documentNumber }}</p>
                      </div>
                    </div>

                    @if (transferNotes) {
                      <div class="p-3 bg-white rounded-lg border border-gray-200">
                        <p class="text-[10px] font-semibold text-gray-500 uppercase mb-1">Motivo</p>
                        <p class="text-sm text-gray-700">{{ transferNotes }}</p>
                      </div>
                    }
                  </div>

                  @if (ownerBoxes().length <= 1) {
                    <div class="flex items-start gap-2 p-3 bg-amber-50 rounded-xl border border-amber-200 text-sm">
                      <lucide-icon [img]="alertIcon" [size]="18" class="text-amber-600 shrink-0 mt-0.5"></lucide-icon>
                      <p class="text-amber-700">
                        <span class="font-semibold">{{ selectedOwner()?.lastName }}, {{ selectedOwner()?.firstName }}</span>
                        solo tiene esta caja. Al transferirla, su cuenta quedará <span class="font-bold">inactiva</span>.
                      </p>
                    </div>
                  }

                  <div class="flex justify-between pt-2">
                    <button (click)="goToStep('createUser')"
                      class="px-5 py-2.5 border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-all text-sm font-medium">
                      Atrás
                    </button>
                    <button (click)="executeTransfer()"
                      [disabled]="isSubmitting()"
                      class="px-6 py-2.5 bg-violet-600 text-white rounded-xl hover:bg-violet-700 disabled:bg-gray-400 transition-all text-sm font-medium flex items-center gap-2">
                      @if (isSubmitting()) {
                        <div class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                        Procesando...
                      } @else {
                        <lucide-icon [img]="truckIcon" [size]="16"></lucide-icon>
                        Confirmar Transferencia
                      }
                    </button>
                  </div>
                </div>
              }

            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class TransfersComponent implements OnInit {
  private http = inject(HttpClient);
  private alertService = inject(AlertService);
  private authService = inject(AuthService);

  allTransfers = signal<TransferRow[]>([]);
  isLoading = signal(false);
  currentPage = signal(1);
  searchTerm = signal('');
  readonly pageSize = 10;

  transferStep = signal<TransferStep>('idle');
  isSubmitting = signal(false);

  allUsers = signal<User[]>([]);
  filteredOwners = signal<User[]>([]);
  selectedOwner = signal<User | null>(null);
  ownerBoxes = signal<WaterBox[]>([]);
  selectedBox = signal<WaterBox | null>(null);
  zones = signal<Zone[]>([]);

  ownerSearchTerm = '';
  transferNotes = '';
  newUser: any = this.emptyNewUser();
  formErrors: Record<string, string> = {};

  truckIcon = Truck; searchIcon = Search; prevIcon = ChevronLeft;
  nextIcon = ChevronRight; chevronsLeftIcon = ChevronsLeft;
  chevronsRightIcon = ChevronsRight; closeIcon = X;
  fileTextIcon = FileText; userPlusIcon = UserPlus;
  dropletsIcon = Droplets; arrowRightIcon = ArrowRight;
  alertIcon = AlertCircle; downloadIcon = Download;
  phoneIcon = Phone; mapPinIcon = MapPin; userCheckIcon = UserCheck;
  hashIcon = Hash; filterIcon = Filter;

  private userMap = new Map<string, User>();
  private boxMap = new Map<string, WaterBox>();
  private zoneMap = new Map<string, string>();

  private get headers() {
    return { 'X-User-Id': this.authService.userId() || '', 'Content-Type': 'application/json' };
  }

  filteredTransfers = computed(() => {
    let transfers = this.allTransfers();
    const term = this.searchTerm().trim().toLowerCase();
    if (term) {
      transfers = transfers.filter(t =>
        (t.boxCode || '').toLowerCase().includes(term) ||
        (t.fromUserName || '').toLowerCase().includes(term) ||
        (t.toUserName || '').toLowerCase().includes(term) ||
        (t.notes || '').toLowerCase().includes(term)
      );
    }
    return transfers.sort((a, b) => new Date(b.transferDate).getTime() - new Date(a.transferDate).getTime());
  });

  totalPages = computed(() => Math.ceil(this.filteredTransfers().length / this.pageSize) || 1);
  startIndex = computed(() => (this.currentPage() - 1) * this.pageSize);
  endIndex = computed(() => Math.min(this.currentPage() * this.pageSize, this.filteredTransfers().length));
  paginatedTransfers = computed(() => this.filteredTransfers().slice(this.startIndex(), this.startIndex() + this.pageSize));

  visiblePages = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    const pages: number[] = [];
    let start = Math.max(1, current - 2);
    const end = Math.min(total, start + 4);
    if (end - start < 4) start = Math.max(1, end - 4);
    for (let i = start; i <= end; i++) pages.push(i);
    return pages;
  });

  currentStepNumber = computed(() => {
    const map: Record<TransferStep, number> = {
      idle: 0, selectOwner: 1, selectBox: 2, createUser: 3, confirm: 4
    };
    return map[this.transferStep()];
  });

  filteredStreets = signal<any[]>([]);

  ngOnInit(): void {
    this.loadTransfers();
    this.loadZones();
  }

  stepIndex(step: string): number {
    const map: Record<string, number> = { selectOwner: 0, selectBox: 1, createUser: 2, confirm: 3 };
    return map[step] ?? 0;
  }

  loadTransfers(): void {
    this.isLoading.set(true);
    const orgId = this.authService.organizationId();

    this.http.get<ApiResponse<User[]>>(
      `${environment.apiUrl}/users/organization/${orgId}`,
      { params: { includeInactive: 'true' }, headers: this.headers }
    ).subscribe({
      next: usersRes => {
        this.userMap.clear();
        (usersRes.data || []).forEach(u => this.userMap.set(u.id, u));
        this.allUsers.set((usersRes.data || []).filter(u => u.role === 'CLIENT' && u.recordStatus === 'ACTIVE'));

        this.http.get<ApiResponse<WaterBox[]>>(
          `${environment.apiUrl}/water-boxes`,
          { headers: this.headers }
        ).subscribe({
          next: boxRes => {
            this.boxMap.clear();
            (boxRes.data || []).filter(b => b.organizationId === orgId)
              .forEach(b => this.boxMap.set(b.id, b));

            this.http.get<ApiResponse<WaterBoxTransfer[]>>(
              `${environment.apiUrl}/water-box-transfers`,
              { headers: this.headers }
            ).subscribe({
              next: trRes => {
                const transfers: TransferRow[] = (trRes.data || [])
                  .filter(t => t.organizationId === orgId)
                  .map(t => {
                    const fromUser = this.userMap.get(t.fromUserId);
                    const toUser = this.userMap.get(t.toUserId);
                    const box = this.boxMap.get(t.waterBoxId);
                    return {
                      ...t,
                      fromUserName: fromUser ? `${fromUser.lastName}, ${fromUser.firstName}` : undefined,
                      toUserName: toUser ? `${toUser.lastName}, ${toUser.firstName}` : undefined,
                      boxCode: box?.boxCode
                    };
                  });
                this.allTransfers.set(transfers);
                this.isLoading.set(false);
              },
              error: () => { this.isLoading.set(false); this.alertService.error('Error', 'No se pudieron cargar las transferencias'); }
            });
          },
          error: () => { this.isLoading.set(false); }
        });
      },
      error: () => { this.isLoading.set(false); this.alertService.error('Error', 'Error cargando datos'); }
    });
  }

  loadZones(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;
    this.http.get<ApiResponse<Zone[]>>(
      `${environment.apiUrl}/zones/organization/${orgId}`,
      { headers: this.headers }
    ).subscribe({
      next: res => {
        const z = (res.data || []).filter(zo => zo.recordStatus === 'ACTIVE');
        this.zones.set(z);
        this.zoneMap.clear();
        z.forEach(zo => this.zoneMap.set(zo.id, zo.zoneName || zo.name || ''));
      }
    });
  }

  startTransfer(): void {
    this.resetTransferState();
    this.transferStep.set('selectOwner');
    this.loadUsersWithBoxes();
  }

  cancelTransfer(): void {
    this.transferStep.set('idle');
    this.resetTransferState();
  }

  goToStep(step: TransferStep): void {
    if (step === 'selectBox') this.loadOwnerBoxes();
    this.transferStep.set(step);
  }

  private loadUsersWithBoxes(): void {
    const orgId = this.authService.organizationId();
    this.http.get<ApiResponse<WaterBoxAssignment[]>>(
      `${environment.apiUrl}/water-box-assignments`,
      { headers: this.headers }
    ).subscribe({
      next: res => {
        const activeAssignments = (res.data || []).filter(a => a.assignmentStatus === 'ACTIVE');
        const userIdsWithBoxes = new Set(activeAssignments.map(a => a.userId));
        const ownersWithBoxes = this.allUsers().filter(u => userIdsWithBoxes.has(u.id));
        this.filteredOwners.set(ownersWithBoxes);
      }
    });
  }

  filterOwners(): void {
    const term = this.ownerSearchTerm.trim().toLowerCase();
    if (!term) {
      this.loadUsersWithBoxes();
      return;
    }

    this.http.get<ApiResponse<WaterBoxAssignment[]>>(
      `${environment.apiUrl}/water-box-assignments`,
      { headers: this.headers }
    ).subscribe({
      next: res => {
        const activeAssignments = (res.data || []).filter(a => a.assignmentStatus === 'ACTIVE');
        const userIdsWithBoxes = new Set(activeAssignments.map(a => a.userId));
        const filtered = this.allUsers().filter(u =>
          userIdsWithBoxes.has(u.id) && (
            (u.lastName || '').toLowerCase().includes(term) ||
            (u.firstName || '').toLowerCase().includes(term) ||
            (u.documentNumber || '').includes(term)
          )
        );
        this.filteredOwners.set(filtered);
      }
    });
  }

  selectOwner(user: User): void {
    this.selectedOwner.set(user);
  }

  selectBox(box: WaterBox): void {
    this.selectedBox.set(box);
    if (box.zoneId) this.newUser.zoneId = box.zoneId;
    if (box.streetId) this.newUser.streetId = box.streetId;
    if (box.address) this.newUser.address = box.address;
  }

  private loadOwnerBoxes(): void {
    const owner = this.selectedOwner();
    if (!owner) return;

    this.http.get<ApiResponse<WaterBoxAssignment[]>>(
      `${environment.apiUrl}/water-box-assignments/user/${owner.id}`,
      { headers: this.headers }
    ).subscribe({
      next: res => {
        const activeAssignments = (res.data || []).filter(a => a.assignmentStatus === 'ACTIVE');
        const boxIds = activeAssignments.map(a => a.waterBoxId);
        const boxes = boxIds.map(id => this.boxMap.get(id)).filter((b): b is WaterBox => !!b && b.recordStatus === 'ACTIVE');
        this.ownerBoxes.set(boxes);
      }
    });
  }

  isNewUserValid(): boolean {
    this.validateForm();
    return Object.keys(this.formErrors).length === 0 && !!(
      this.newUser.lastName?.trim() &&
      this.newUser.firstName?.trim() &&
      this.newUser.documentNumber?.trim() &&
      this.transferNotes?.trim()
    );
  }

  validateForm(): void {
    this.formErrors = {};
    if (!this.newUser.lastName?.trim()) this.formErrors['lastName'] = 'Los apellidos son obligatorios';
    if (!this.newUser.firstName?.trim()) this.formErrors['firstName'] = 'Los nombres son obligatorios';
    if (!this.newUser.documentNumber?.trim()) {
      this.formErrors['documentNumber'] = 'El N° de documento es obligatorio';
    } else if (this.newUser.documentType === 'DNI' && this.newUser.documentNumber.length !== 8) {
      this.formErrors['documentNumber'] = 'El DNI debe tener exactamente 8 dígitos';
    }
    if (this.newUser.phone && this.newUser.phone.length > 0) {
      if (!this.newUser.phone.startsWith('9')) this.formErrors['phone'] = 'Debe comenzar con 9';
      else if (this.newUser.phone.length !== 9) this.formErrors['phone'] = 'Debe tener 9 dígitos';
    }
    if (this.newUser.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.newUser.email)) {
      this.formErrors['email'] = 'Ingrese un correo válido';
    }
    if (!this.transferNotes?.trim()) this.formErrors['notes'] = 'El motivo es obligatorio';
  }

  onPhoneInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '').slice(0, 9);
    this.newUser.phone = input.value;
    this.validateForm();
  }

  onDocumentInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
    this.newUser.documentNumber = input.value;
    this.validateForm();
  }

  onZoneChange(zoneId: string): void {
    this.newUser.streetId = '';
    if (!zoneId) {
      this.filteredStreets.set([]);
      return;
    }
    this.http.get<ApiResponse<any[]>>(
      `${environment.apiUrl}/streets/zone/${zoneId}`,
      { headers: this.headers }
    ).subscribe({
      next: res => this.filteredStreets.set((res.data || []).filter((s: any) => s.recordStatus === 'ACTIVE')),
      error: () => this.filteredStreets.set([])
    });
  }

  executeTransfer(): void {
    const owner = this.selectedOwner();
    const box = this.selectedBox();
    if (!owner || !box || !this.isNewUserValid()) return;

    this.isSubmitting.set(true);
    const orgId = this.authService.organizationId()!;

    const createUserReq: CreateUserRequest = {
      organizationId: orgId,
      firstName: this.newUser.firstName.trim(),
      lastName: this.newUser.lastName.trim(),
      documentType: this.newUser.documentType || 'DNI',
      documentNumber: this.newUser.documentNumber.trim(),
      email: this.newUser.email?.trim() || undefined,
      phone: this.newUser.phone?.trim() || undefined,
      address: this.newUser.address?.trim() || undefined,
      zoneId: this.newUser.zoneId || box.zoneId || undefined,
      streetId: this.newUser.streetId || box.streetId || undefined,
      role: 'CLIENT'
    };

    this.http.post<ApiResponse<User>>(
      `${environment.apiUrl}/users`,
      createUserReq,
      { headers: this.headers }
    ).subscribe({
      next: userRes => {
        const newUserId = userRes.data?.id;
        if (!newUserId) {
          this.isSubmitting.set(false);
          this.alertService.error('Error', 'No se pudo crear el usuario');
          return;
        }

        const transferReq: TransferWaterBoxRequest = {
          waterBoxId: box.id,
          fromUserId: owner.id,
          toUserId: newUserId,
          transferFee: 0,
          notes: this.transferNotes.trim()
        };

        this.http.post<ApiResponse<WaterBoxTransfer>>(
          `${environment.apiUrl}/water-box-transfers`,
          transferReq,
          { headers: this.headers }
        ).subscribe({
          next: trRes => {
            this.isSubmitting.set(false);

            if (this.ownerBoxes().length <= 1) {
              this.http.delete(
                `${environment.apiUrl}/users/${owner.id}`,
                { headers: this.headers, params: { reason: 'Transferencia de única caja de agua' } }
              ).subscribe();
            }

            const transferData = trRes.data;
            this.alertService.success('Transferencia exitosa', 'La caja de agua ha sido transferida correctamente');
            this.transferStep.set('idle');
            this.resetTransferState();
            this.loadTransfers();

            if (transferData) {
              this.generateTransferPdf(
                transferData,
                owner,
                { ...createUserReq, id: newUserId } as any,
                box
              );
            }
          },
          error: (err) => {
            this.isSubmitting.set(false);
            const msg = err.error?.message || 'No se pudo completar la transferencia';
            this.alertService.error('Error', msg);
          }
        });
      },
      error: (err) => {
        this.isSubmitting.set(false);
        const msg = err.error?.message || 'No se pudo crear el nuevo usuario';
        this.alertService.error('Error', msg);
      }
    });
  }

  downloadTransferPdf(tr: TransferRow): void {
    const fromUser = this.userMap.get(tr.fromUserId);
    const toUser = this.userMap.get(tr.toUserId);
    const box = this.boxMap.get(tr.waterBoxId);
    this.generateTransferPdf(tr, fromUser, toUser, box);
  }

  private generateTransferPdf(
    transfer: WaterBoxTransfer | TransferRow,
    fromUser: User | undefined,
    toUser: User | any | undefined,
    box: WaterBox | undefined
  ): void {
    const doc = new jsPDF();
    const org = this.authService.organization();
    const pw = doc.internal.pageSize.getWidth();
    const ph = doc.internal.pageSize.getHeight();
    const m = 25;
    const cw = pw - m * 2;
    const dark = [15, 23, 42] as const;
    const mid = [51, 65, 85] as const;
    const light = [100, 116, 139] as const;
    const subtle = [148, 163, 184] as const;
    const accent = [30, 58, 138] as const;
    const logoUrl = org?.logoUrl || (org as any)?.logo_url || (org as any)?.logo;
    const fromName = fromUser ? `${fromUser.lastName}, ${fromUser.firstName}` : transfer.fromUserId;
    const toName = toUser ? `${toUser.lastName}, ${toUser.firstName}` : transfer.toUserId;
    const date = new Date(transfer.transferDate).toLocaleDateString('es-PE', {
      day: '2-digit', month: 'long', year: 'numeric'
    });
    const boxCode = box?.boxCode || (transfer as TransferRow).boxCode || '-';

    let y = 18;

    doc.setDrawColor(...dark);
    doc.setLineWidth(0.8);
    doc.line(m, y - 4, pw - m, y - 4);
    doc.setLineWidth(0.3);
    doc.line(m, y - 2, pw - m, y - 2);

    if (logoUrl) {
      try { doc.addImage(logoUrl, 'PNG', m, y, 14, 14); } catch { }
    }

    const hx = logoUrl ? m + 18 : pw / 2;
    const ha: any = logoUrl ? 'left' : 'center';
    doc.setTextColor(...dark);
    doc.setFontSize(14);
    doc.setFont('helvetica', 'bold');
    doc.text(org?.organizationName || 'JASS', hx, y + 5, { align: ha });
    doc.setFontSize(8);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(...light);
    const loc = [org?.district, org?.province, org?.department].filter(Boolean).join(', ');
    if (loc) doc.text(loc.toUpperCase(), hx, y + 10, { align: ha });
    if (org?.address) doc.text(org.address, hx, y + 14, { align: ha });

    y = 42;
    doc.setFillColor(241, 245, 249);
    doc.roundedRect(m, y, cw, 14, 1, 1, 'F');
    doc.setTextColor(...dark);
    doc.setFontSize(11);
    doc.setFont('helvetica', 'bold');
    doc.text('ACTA DE TRANSFERENCIA DE CAJA DE AGUA', pw / 2, y + 9.5, { align: 'center' });

    y = 64;
    const allTrs = this.allTransfers();
    const trIdx = allTrs.findIndex(t => t.id === (transfer as any).id);
    const trNum = trIdx >= 0 ? allTrs.length - trIdx : 1;
    const trNumStr = String(trNum).padStart(5, '0');
    doc.setFontSize(8);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(...light);
    doc.text('Transferencia', m, y);
    doc.text('Fecha de emisión', pw / 2, y);
    y += 5;
    doc.setFontSize(10);
    doc.setTextColor(...dark);
    doc.setFont('helvetica', 'bold');
    doc.text(`TRANSFERENCIA N°${trNumStr}`, m, y);
    doc.setFont('helvetica', 'normal');
    doc.text(date, pw / 2, y);

    y += 10;
    doc.setDrawColor(...subtle);
    doc.setLineWidth(0.2);
    doc.line(m, y, pw - m, y);

    y += 8;
    this.pdfSectionHeader(doc, 'INFORMACIÓN DE LA CAJA DE AGUA', m, y, accent);
    y += 7;
    autoTable(doc, {
      startY: y,
      body: [
        ['Código', boxCode],
        ['Tipo', this.getBoxTypeLabel(box?.boxType || '')],
        ['Dirección', box?.address || '-']
      ],
      theme: 'plain',
      styles: { fontSize: 9, cellPadding: { top: 2.5, bottom: 2.5, left: 4, right: 4 }, textColor: dark as any },
      columnStyles: {
        0: { fontStyle: 'bold', cellWidth: 40, textColor: mid as any },
        1: { cellWidth: cw - 40 }
      },
      margin: { left: m, right: m },
      tableLineColor: [226, 232, 240] as any,
      tableLineWidth: 0.2
    });

    y = (doc as any).lastAutoTable.finalY + 6;
    doc.setDrawColor(...subtle);
    doc.setLineWidth(0.2);
    doc.line(m, y, pw - m, y);

    y += 8;
    this.pdfSectionHeader(doc, 'PARTE CEDENTE (Quien transfiere)', m, y, accent);
    y += 3;
    autoTable(doc, {
      startY: y,
      body: [
        ['Nombres y Apellidos', fromName],
        ['Documento de Identidad', `${fromUser?.documentType || 'DNI'} ${fromUser?.documentNumber || '-'}`],
        ['Teléfono', fromUser?.phone || '-'],
        ['Dirección', fromUser?.address || '-']
      ],
      theme: 'plain',
      styles: { fontSize: 9, cellPadding: { top: 2, bottom: 2, left: 4, right: 4 }, textColor: dark as any },
      columnStyles: {
        0: { fontStyle: 'bold', cellWidth: 50, textColor: mid as any },
        1: { cellWidth: cw - 50 }
      },
      margin: { left: m, right: m }
    });

    y = (doc as any).lastAutoTable.finalY + 6;
    this.pdfSectionHeader(doc, 'PARTE CESIONARIA (Quien recibe)', m, y, accent);
    y += 3;
    autoTable(doc, {
      startY: y,
      body: [
        ['Nombres y Apellidos', toName],
        ['Documento de Identidad', `${toUser?.documentType || 'DNI'} ${toUser?.documentNumber || '-'}`],
        ['Teléfono', toUser?.phone || '-'],
        ['Dirección', toUser?.address || '-']
      ],
      theme: 'plain',
      styles: { fontSize: 9, cellPadding: { top: 2, bottom: 2, left: 4, right: 4 }, textColor: dark as any },
      columnStyles: {
        0: { fontStyle: 'bold', cellWidth: 50, textColor: mid as any },
        1: { cellWidth: cw - 50 }
      },
      margin: { left: m, right: m }
    });

    y = (doc as any).lastAutoTable.finalY + 4;

    if (transfer.notes) {
      y += 4;
      doc.setDrawColor(...subtle);
      doc.setLineWidth(0.2);
      doc.line(m, y, pw - m, y);
      y += 8;
      this.pdfSectionHeader(doc, 'MOTIVO DE LA TRANSFERENCIA', m, y, accent);
      y += 6;
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(9);
      doc.setTextColor(...dark);
      const lines = doc.splitTextToSize(transfer.notes, cw - 8);
      doc.text(lines, m + 4, y);
      y += lines.length * 4.5 + 4;
    }

    y += 4;
    doc.setDrawColor(...subtle);
    doc.setLineWidth(0.2);
    doc.line(m, y, pw - m, y);

    y += 6;
    doc.setFillColor(241, 245, 249);
    doc.setFont('helvetica', 'italic');
    doc.setFontSize(8);
    doc.setTextColor(...mid);
    const clause = 'Por medio de la presente, las partes declaran su conformidad con la transferencia de la caja de agua '
      + `identificada con el código ${boxCode}, quedando el cesionario como nuevo titular y responsable de la misma.`;
    const clauseLines = doc.splitTextToSize(clause, cw - 16);
    const clauseH = clauseLines.length * 4 + 10;
    doc.roundedRect(m, y, cw, clauseH, 1, 1, 'F');
    doc.text(clauseLines, m + 8, y + 6);

    const sigY = y + clauseH + 12;
    const sigW = 65;
    const gap = cw - sigW * 2;
    const lx = m;
    const rx = m + sigW + gap;

    doc.setDrawColor(...dark);
    doc.setLineWidth(0.4);
    doc.line(lx + 5, sigY, lx + sigW - 5, sigY);
    doc.line(rx + 5, sigY, rx + sigW - 5, sigY);

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    doc.setTextColor(...dark);
    doc.text('CEDENTE', lx + sigW / 2, sigY + 5, { align: 'center' });
    doc.text('CESIONARIO', rx + sigW / 2, sigY + 5, { align: 'center' });

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7.5);
    doc.setTextColor(...mid);
    doc.text(fromName, lx + sigW / 2, sigY + 10, { align: 'center' });
    doc.text(`${fromUser?.documentType || 'DNI'}: ${fromUser?.documentNumber || '-'}`, lx + sigW / 2, sigY + 14, { align: 'center' });
    doc.text(toName, rx + sigW / 2, sigY + 10, { align: 'center' });
    doc.text(`${toUser?.documentType || 'DNI'}: ${toUser?.documentNumber || '-'}`, rx + sigW / 2, sigY + 14, { align: 'center' });

    doc.setFontSize(6.5);
    doc.setTextColor(...subtle);
    doc.setFont('helvetica', 'normal');

    doc.save(`Transferencia_${boxCode}_${new Date().toISOString().split('T')[0]}.pdf`);
  }

  private pdfSectionHeader(doc: jsPDF, title: string, x: number, y: number, color: readonly number[]): void {
    doc.setFillColor(color[0], color[1], color[2]);
    doc.rect(x, y - 3.5, 2.5, 5, 'F');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9.5);
    doc.setTextColor(color[0], color[1], color[2]);
    doc.text(title, x + 5, y + 0.5);
  }

  getBoxTypeLabel(type: string): string {
    const map: Record<string, string> = {
      RESIDENTIAL: 'Residencial', COMMERCIAL: 'Comercial',
      COMMUNAL: 'Comunal', INSTITUTIONAL: 'Institucional'
    };
    return map[type] || type || '-';
  }

  private emptyNewUser(): any {
    return {
      firstName: '', lastName: '', documentType: 'DNI',
      documentNumber: '', phone: '', email: '',
      address: '', zoneId: '', streetId: ''
    };
  }

  private resetTransferState(): void {
    this.selectedOwner.set(null);
    this.selectedBox.set(null);
    this.ownerBoxes.set([]);
    this.filteredOwners.set([]);
    this.ownerSearchTerm = '';
    this.transferNotes = '';
    this.newUser = this.emptyNewUser();
  }
}
