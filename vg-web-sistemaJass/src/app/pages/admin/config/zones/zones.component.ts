import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom, forkJoin } from 'rxjs';
import {
  LucideAngularModule, Plus, Edit, Trash2, RotateCcw,
  MapPin, X, Save, Loader2, Map, Eye
} from 'lucide-angular';
import { environment } from '../../../../../environments/environment';
import { Zone, Street, ApiResponse } from '../../../../core';
import { AlertService } from '../../../../core/services/alert.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-zones',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  template: `
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Zonas y Calles</h1>
        <p class="text-sm text-gray-500 mt-0.5">Gestión de zonas y calles de la organización</p>
      </div>

      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="border-b border-gray-200 bg-gray-50/30">
          <div class="flex">
            <button
              (click)="activeTab.set('zones')"
              class="flex-1 sm:flex-none px-6 py-3.5 text-sm font-medium transition-all relative"
              [class.text-blue-700]="activeTab() === 'zones'"
              [class.text-gray-500]="activeTab() !== 'zones'">
              <div class="flex items-center justify-center gap-2">
                <lucide-icon [img]="mapPinIcon" [size]="18"></lucide-icon>
                <span>Zonas</span>
                <span class="text-xs font-bold px-2 py-0.5 rounded-full"
                      [class.bg-blue-100]="activeTab() === 'zones'"
                      [class.text-blue-700]="activeTab() === 'zones'"
                      [class.bg-gray-200]="activeTab() !== 'zones'"
                      [class.text-gray-600]="activeTab() !== 'zones'">
                  {{ zones().length }}
                </span>
              </div>
              @if (activeTab() === 'zones') {
                <div class="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-t"></div>
              }
            </button>
            <button
              (click)="activeTab.set('streets')"
              class="flex-1 sm:flex-none px-6 py-3.5 text-sm font-medium transition-all relative"
              [class.text-blue-700]="activeTab() === 'streets'"
              [class.text-gray-500]="activeTab() !== 'streets'">
              <div class="flex items-center justify-center gap-2">
                <lucide-icon [img]="mapIcon" [size]="18"></lucide-icon>
                <span>Calles</span>
                <span class="text-xs font-bold px-2 py-0.5 rounded-full"
                      [class.bg-blue-100]="activeTab() === 'streets'"
                      [class.text-blue-700]="activeTab() === 'streets'"
                      [class.bg-gray-200]="activeTab() !== 'streets'"
                      [class.text-gray-600]="activeTab() !== 'streets'">
                  {{ allStreets().length }}
                </span>
              </div>
              @if (activeTab() === 'streets') {
                <div class="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-t"></div>
              }
            </button>
          </div>
        </div>

        @if (activeTab() === 'zones') {
          <div class="p-4 bg-gray-50/50 border-b border-gray-100">
            <div class="flex flex-col sm:flex-row gap-3">
              <select
                [(ngModel)]="zoneStatusFilter"
                class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white">
                <option value="ALL">Todos los estados</option>
                <option value="ACTIVE">Activos</option>
                <option value="INACTIVE">Inactivos</option>
              </select>
              <div class="flex-1"></div>
              <button
                (click)="openZoneModal()"
                class="inline-flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium shadow-sm">
                <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
                Nueva Zona
              </button>
            </div>
          </div>

          @if (isLoadingZones()) {
            <div class="p-16 text-center">
              <div class="inline-flex items-center gap-2.5 text-gray-500">
                <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                <span class="text-sm">Cargando zonas...</span>
              </div>
            </div>
          } @else if (filteredZones.length === 0) {
            <div class="p-16 text-center">
              <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
                <lucide-icon [img]="mapPinIcon" [size]="32" class="text-gray-400"></lucide-icon>
              </div>
              <p class="text-gray-600 font-medium mb-1">No se encontraron zonas</p>
              <p class="text-sm text-gray-400">Crea una nueva zona para comenzar</p>
            </div>
          } @else {
            <div class="hidden lg:block overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="bg-gray-50/80 border-b border-gray-100">
                    <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-14">#</th>
                    <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Nombre</th>
                    <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Descripción</th>
                    <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-24">Calles</th>
                    <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-24">Estado</th>
                    <th class="px-4 py-3 text-right text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-28">Acciones</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-50">
                  @for (zone of filteredZones; track zone.id; let i = $index) {
                    <tr class="hover:bg-blue-50/30 transition-colors">
                      <td class="px-4 py-3.5">
                        <span class="inline-flex items-center justify-center w-7 h-7 bg-blue-50 text-blue-600 rounded-lg text-xs font-bold">{{ i + 1 }}</span>
                      </td>
                      <td class="px-4 py-3.5 font-semibold text-gray-800">{{ zone.zoneName }}</td>
                      <td class="px-4 py-3.5 text-sm text-gray-500 max-w-xs truncate">{{ zone.description || '—' }}</td>
                      <td class="px-4 py-3.5 text-center">
                        <button
                          (click)="viewStreetsForZone(zone)"
                          class="inline-flex items-center gap-1.5 text-sm text-blue-600 hover:text-blue-800 font-medium transition-colors">
                          <lucide-icon [img]="eyeIcon" [size]="14"></lucide-icon>
                          {{ getStreetCountForZone(zone.id) }}
                        </button>
                      </td>
                      <td class="px-4 py-3.5 text-center">
                        <span class="inline-flex px-2.5 py-1 text-[11px] font-semibold rounded-full"
                              [class.bg-emerald-50]="isActive(zone.recordStatus)"
                              [class.text-emerald-700]="isActive(zone.recordStatus)"
                              [class.bg-red-50]="!isActive(zone.recordStatus)"
                              [class.text-red-600]="!isActive(zone.recordStatus)">
                          {{ isActive(zone.recordStatus) ? 'Activo' : 'Inactivo' }}
                        </span>
                      </td>
                      <td class="px-4 py-3.5 text-right">
                        <div class="flex items-center justify-end gap-1">
                          <button (click)="editZone(zone)" class="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors" title="Editar">
                            <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                          </button>
                          @if (isActive(zone.recordStatus)) {
                            <button (click)="deleteZone(zone)" class="p-2 text-red-500 hover:bg-red-100 rounded-lg transition-colors" title="Eliminar">
                              <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                            </button>
                          } @else {
                            <button (click)="restoreZone(zone)" class="p-2 text-emerald-600 hover:bg-emerald-100 rounded-lg transition-colors" title="Restaurar">
                              <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                            </button>
                          }
                        </div>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>

            <div class="lg:hidden p-3 sm:p-4 space-y-3">
              @for (zone of filteredZones; track zone.id; let i = $index) {
                <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden hover:shadow-md transition-shadow">
                  <div class="flex items-stretch">
                    <div class="w-11 bg-linear-to-b from-blue-500 to-blue-600 flex items-center justify-center text-white font-bold text-sm shrink-0">
                      {{ i + 1 }}
                    </div>
                    <div class="flex-1 p-3.5 min-w-0">
                      <div class="flex items-start justify-between gap-2 mb-1">
                        <h4 class="font-semibold text-gray-800 text-sm truncate">{{ zone.zoneName }}</h4>
                        <span class="inline-flex px-2 py-0.5 text-[10px] font-bold rounded-full shrink-0"
                              [class.bg-emerald-50]="isActive(zone.recordStatus)"
                              [class.text-emerald-700]="isActive(zone.recordStatus)"
                              [class.bg-red-50]="!isActive(zone.recordStatus)"
                              [class.text-red-600]="!isActive(zone.recordStatus)">
                          {{ isActive(zone.recordStatus) ? 'Activo' : 'Inactivo' }}
                        </span>
                      </div>
                      @if (zone.description) {
                        <p class="text-xs text-gray-500 mb-2 line-clamp-2">{{ zone.description }}</p>
                      }
                      <div class="flex items-center justify-between pt-1">
                        <button
                          (click)="viewStreetsForZone(zone)"
                          class="text-xs text-blue-600 font-semibold hover:text-blue-800 transition-colors">
                          {{ getStreetCountForZone(zone.id) }} calles
                        </button>
                        <div class="flex items-center gap-1">
                          <button (click)="editZone(zone)" class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                            <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                          </button>
                          @if (isActive(zone.recordStatus)) {
                            <button (click)="deleteZone(zone)" class="p-1.5 text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                              <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                            </button>
                          } @else {
                            <button (click)="restoreZone(zone)" class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors">
                              <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                            </button>
                          }
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              }
            </div>
          }
        }

        @if (activeTab() === 'streets') {
          <div class="p-4 bg-gray-50/50 border-b border-gray-100">
            <div class="flex flex-col sm:flex-row gap-3">
              <select
                [(ngModel)]="streetZoneFilter"
                class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white">
                <option value="">Todas las zonas</option>
                @for (zone of activeZones; track zone.id) {
                  <option [value]="zone.id">{{ zone.zoneName }}</option>
                }
              </select>
              <select
                [(ngModel)]="streetStatusFilter"
                class="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white">
                <option value="ALL">Todos los estados</option>
                <option value="ACTIVE">Activos</option>
                <option value="INACTIVE">Inactivos</option>
              </select>
              <div class="flex-1"></div>
              <button
                (click)="openStreetModal()"
                class="inline-flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium shadow-sm">
                <lucide-icon [img]="plusIcon" [size]="18"></lucide-icon>
                Nueva Calle
              </button>
            </div>
          </div>

          @if (isLoadingStreets()) {
            <div class="p-16 text-center">
              <div class="inline-flex items-center gap-2.5 text-gray-500">
                <div class="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                <span class="text-sm">Cargando calles...</span>
              </div>
            </div>
          } @else if (filteredStreets.length === 0) {
            <div class="p-16 text-center">
              <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
                <lucide-icon [img]="mapIcon" [size]="32" class="text-gray-400"></lucide-icon>
              </div>
              <p class="text-gray-600 font-medium mb-1">No se encontraron calles</p>
              <p class="text-sm text-gray-400">Crea una nueva calle para comenzar</p>
            </div>
          } @else {
            <div class="hidden lg:block overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="bg-gray-50/80 border-b border-gray-100">
                    <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-14">#</th>
                    <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-20">Tipo</th>
                    <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Nombre Completo</th>
                    <th class="px-4 py-3 text-left text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Zona</th>
                    <th class="px-4 py-3 text-center text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-24">Estado</th>
                    <th class="px-4 py-3 text-right text-[11px] font-semibold text-gray-500 uppercase tracking-wider w-28">Acciones</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-50">
                  @for (street of filteredStreets; track street.id; let i = $index) {
                    <tr class="hover:bg-indigo-50/30 transition-colors">
                      <td class="px-4 py-3.5">
                        <span class="inline-flex items-center justify-center w-7 h-7 bg-indigo-50 text-indigo-600 rounded-lg text-xs font-bold">{{ i + 1 }}</span>
                      </td>
                      <td class="px-4 py-3.5">
                        <span class="inline-flex px-2 py-0.5 text-[11px] font-bold rounded bg-slate-100 text-slate-600">{{ street.streetType }}</span>
                      </td>
                      <td class="px-4 py-3.5 font-semibold text-gray-800">{{ street.fullStreetName || (getStreetTypeLabel(street.streetType) + ' ' + street.streetName) }}</td>
                      <td class="px-4 py-3.5 text-sm text-gray-500">{{ getZoneName(street.zoneId) }}</td>
                      <td class="px-4 py-3.5 text-center">
                        <span class="inline-flex px-2.5 py-1 text-[11px] font-semibold rounded-full"
                              [class.bg-emerald-50]="isActive(street.recordStatus)"
                              [class.text-emerald-700]="isActive(street.recordStatus)"
                              [class.bg-red-50]="!isActive(street.recordStatus)"
                              [class.text-red-600]="!isActive(street.recordStatus)">
                          {{ isActive(street.recordStatus) ? 'Activo' : 'Inactivo' }}
                        </span>
                      </td>
                      <td class="px-4 py-3.5 text-right">
                        <div class="flex items-center justify-end gap-1">
                          <button (click)="editStreet(street)" class="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors" title="Editar">
                            <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                          </button>
                          @if (isActive(street.recordStatus)) {
                            <button (click)="deleteStreet(street)" class="p-2 text-red-500 hover:bg-red-100 rounded-lg transition-colors" title="Eliminar">
                              <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                            </button>
                          } @else {
                            <button (click)="restoreStreet(street)" class="p-2 text-emerald-600 hover:bg-emerald-100 rounded-lg transition-colors" title="Restaurar">
                              <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                            </button>
                          }
                        </div>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>

            <div class="lg:hidden p-3 sm:p-4 space-y-3">
              @for (street of filteredStreets; track street.id; let i = $index) {
                <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden hover:shadow-md transition-shadow">
                  <div class="flex items-stretch">
                    <div class="w-11 bg-linear-to-b from-indigo-500 to-indigo-600 flex items-center justify-center text-white font-bold text-sm shrink-0">
                      {{ i + 1 }}
                    </div>
                    <div class="flex-1 p-3.5 min-w-0">
                      <div class="flex items-start justify-between gap-2 mb-1">
                        <div class="min-w-0">
                          <span class="inline-flex px-1.5 py-0.5 text-[10px] font-bold rounded bg-slate-100 text-slate-600 mb-1">{{ street.streetType }}</span>
                          <h4 class="font-semibold text-gray-800 text-sm truncate">{{ street.fullStreetName || (getStreetTypeLabel(street.streetType) + ' ' + street.streetName) }}</h4>
                        </div>
                        <span class="inline-flex px-2 py-0.5 text-[10px] font-bold rounded-full shrink-0"
                              [class.bg-emerald-50]="isActive(street.recordStatus)"
                              [class.text-emerald-700]="isActive(street.recordStatus)"
                              [class.bg-red-50]="!isActive(street.recordStatus)"
                              [class.text-red-600]="!isActive(street.recordStatus)">
                          {{ isActive(street.recordStatus) ? 'Activo' : 'Inactivo' }}
                        </span>
                      </div>
                      <p class="text-xs text-gray-500 mb-2">Zona: {{ getZoneName(street.zoneId) }}</p>
                      <div class="flex items-center justify-end gap-1">
                        <button (click)="editStreet(street)" class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                          <lucide-icon [img]="editIcon" [size]="16"></lucide-icon>
                        </button>
                        @if (isActive(street.recordStatus)) {
                          <button (click)="deleteStreet(street)" class="p-1.5 text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                            <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                          </button>
                        } @else {
                          <button (click)="restoreStreet(street)" class="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors">
                            <lucide-icon [img]="restoreIcon" [size]="16"></lucide-icon>
                          </button>
                        }
                      </div>
                    </div>
                  </div>
                </div>
              }
            </div>
          }
        }
      </div>
    </div>

    @if (showZoneModal()) {
      <div class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4" (click)="closeZoneModal()">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-hidden flex flex-col" (click)="$event.stopPropagation()">
          <div class="border-b border-gray-100 bg-linear-to-r from-gray-50 to-white shrink-0">
            <div class="flex items-center gap-3 px-5 py-4">
              <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
                <lucide-icon [img]="mapPinIcon" [size]="20" class="text-blue-600"></lucide-icon>
              </div>
              <div class="flex-1 min-w-0">
                <h2 class="text-lg font-bold text-gray-900">{{ editingZone() ? 'Editar' : 'Nueva' }} Zona</h2>
                <p class="text-xs text-gray-400">Configuración de zona geográfica</p>
              </div>
              <button (click)="closeZoneModal()" class="p-2 hover:bg-gray-100 rounded-xl transition-colors">
                <lucide-icon [img]="closeIcon" [size]="18" class="text-gray-400"></lucide-icon>
              </button>
            </div>
          </div>
          <form (ngSubmit)="saveZone()" class="flex-1 overflow-y-auto">
            <div class="p-5 space-y-4">
            <div>
              <label class="block text-sm font-semibold text-gray-800 mb-1.5">Nombre de la zona <span class="text-red-500">*</span></label>
              <input
                type="text"
                [(ngModel)]="zoneForm.zoneName"
                name="zoneName"
                required
                class="w-full px-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm placeholder:text-gray-300 transition-all"
                placeholder="Ej: Zona A, Zona Central">
            </div>
            <div>
              <label class="block text-sm font-semibold text-gray-800 mb-1.5">Descripción</label>
              <textarea
                [(ngModel)]="zoneForm.description"
                name="description"
                rows="3"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 text-sm resize-none placeholder:text-gray-300 transition-all"
                placeholder="Descripción opcional de la zona"></textarea>
            </div>

            @if (!editingZone()) {
              <div class="border-t border-gray-100 pt-4">
                <div class="flex items-center justify-between mb-3">
                  <label class="text-sm font-medium text-gray-700">Calles de esta zona</label>
                  <button type="button" (click)="addInlineStreet()" class="text-sm text-blue-600 font-semibold hover:text-blue-800 transition-colors">
                    + Agregar calle
                  </button>
                </div>
                @for (st of inlineStreets; track $index; let idx = $index) {
                  <div class="flex gap-2 mb-2">
                    <select
                      [(ngModel)]="st.streetType"
                      [name]="'ist_' + idx"
                      class="w-28 px-2 py-2 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 bg-white shrink-0 transition-all">
                      @for (tp of streetTypes; track tp.value) {
                        <option [value]="tp.value">{{ tp.label }}</option>
                      }
                    </select>
                    <input
                      type="text"
                      [(ngModel)]="st.streetName"
                      [name]="'isn_' + idx"
                      class="flex-1 min-w-0 px-3 py-2 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all"
                      placeholder="Nombre de la calle">
                    <button type="button" (click)="removeInlineStreet(idx)" class="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors shrink-0">
                      <lucide-icon [img]="trashIcon" [size]="16"></lucide-icon>
                    </button>
                  </div>
                }
                @if (inlineStreets.length === 0) {
                  <p class="text-xs text-gray-400 text-center py-3 bg-gray-50 rounded-lg">Sin calles agregadas (puede agregar después)</p>
                }
              </div>
            }

            </div>
            <div class="flex items-center justify-end gap-3 px-5 py-4 border-t border-gray-100 bg-gray-50/30 shrink-0">
              <button
                type="button"
                (click)="closeZoneModal()"
                class="px-4 py-2.5 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-xl transition-all text-sm font-medium">
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="isSubmitting()"
                class="inline-flex items-center gap-2 px-5 py-2.5 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium shadow-sm">
                @if (isSubmitting()) {
                  <lucide-icon [img]="loaderIcon" [size]="18" class="animate-spin"></lucide-icon>
                  <span>Guardando...</span>
                } @else {
                  <lucide-icon [img]="saveIcon" [size]="18"></lucide-icon>
                  <span>{{ editingZone() ? 'Actualizar' : 'Crear Zona' }}</span>
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    }

    @if (showStreetModal()) {
      <div class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4" (click)="closeStreetModal()">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden" (click)="$event.stopPropagation()">
          <div class="border-b border-gray-100 bg-linear-to-r from-gray-50 to-white">
            <div class="flex items-center gap-3 px-5 py-4">
              <div class="w-10 h-10 bg-indigo-100 rounded-xl flex items-center justify-center">
                <lucide-icon [img]="mapIcon" [size]="20" class="text-indigo-600"></lucide-icon>
              </div>
              <div class="flex-1 min-w-0">
                <h2 class="text-lg font-bold text-gray-900">{{ editingStreet() ? 'Editar' : 'Nueva' }} Calle</h2>
                <p class="text-xs text-gray-400">Registro de calle en zona</p>
              </div>
              <button (click)="closeStreetModal()" class="p-2 hover:bg-gray-100 rounded-xl transition-colors">
                <lucide-icon [img]="closeIcon" [size]="18" class="text-gray-400"></lucide-icon>
              </button>
            </div>
          </div>
          <form (ngSubmit)="saveStreet()" class="p-5 space-y-4">
            <div>
              <label class="block text-sm font-semibold text-gray-800 mb-1.5">Zona <span class="text-red-500">*</span></label>
              <select
                [(ngModel)]="streetForm.zoneId"
                name="zoneId"
                required
                [disabled]="!!editingStreet()"
                [style.color]="streetForm.zoneId ? '#111827' : '#9ca3af'"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 disabled:bg-gray-100 disabled:cursor-not-allowed transition-all">
                <option value="" disabled>Seleccionar zona</option>
                @for (zone of activeZones; track zone.id) {
                  <option [value]="zone.id" style="color: #111827">{{ zone.zoneName }}</option>
                }
              </select>
            </div>
            <div class="grid grid-cols-5 gap-3">
              <div class="col-span-2">
                <label class="block text-sm font-semibold text-gray-800 mb-1.5">Tipo <span class="text-red-500">*</span></label>
                <select
                  [(ngModel)]="streetForm.streetType"
                  name="streetType"
                  required
                  class="w-full px-3 py-2.5 border border-gray-200 rounded-xl text-sm bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 transition-all">
                  @for (tp of streetTypes; track tp.value) {
                    <option [value]="tp.value">{{ tp.label }}</option>
                  }
                </select>
              </div>
              <div class="col-span-3">
                <label class="block text-sm font-semibold text-gray-800 mb-1.5">Nombre <span class="text-red-500">*</span></label>
                <input
                  type="text"
                  [(ngModel)]="streetForm.streetName"
                  name="streetName"
                  required
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm bg-gray-50/80 focus:bg-white focus:ring-2 focus:ring-gray-300 focus:border-gray-400 placeholder:text-gray-300 transition-all"
                  placeholder="Ej: Los Rosales">
              </div>
            </div>
            <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-100">
              <button
                type="button"
                (click)="closeStreetModal()"
                class="px-4 py-2.5 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-xl transition-all text-sm font-medium">
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="isSubmitting()"
                class="inline-flex items-center gap-2 px-5 py-2.5 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:bg-gray-300 disabled:text-gray-500 transition-all text-sm font-medium shadow-sm">
                @if (isSubmitting()) {
                  <lucide-icon [img]="loaderIcon" [size]="18" class="animate-spin"></lucide-icon>
                  <span>Guardando...</span>
                } @else {
                  <lucide-icon [img]="saveIcon" [size]="18"></lucide-icon>
                  <span>{{ editingStreet() ? 'Actualizar' : 'Crear Calle' }}</span>
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    }
  `
})
export class ZonesComponent implements OnInit {
  private http = inject(HttpClient);
  private alertService = inject(AlertService);
  private authService = inject(AuthService);

  activeTab = signal<'zones' | 'streets'>('zones');
  zones = signal<Zone[]>([]);
  allStreets = signal<Street[]>([]);
  isLoadingZones = signal(false);
  isLoadingStreets = signal(false);
  showZoneModal = signal(false);
  showStreetModal = signal(false);
  editingZone = signal<Zone | null>(null);
  editingStreet = signal<Street | null>(null);
  isSubmitting = signal(false);

  zoneStatusFilter = 'ALL';
  streetZoneFilter = '';
  streetStatusFilter = 'ALL';

  zoneForm = { zoneName: '', description: '' };
  streetForm = { zoneId: '', streetType: 'CALLE', streetName: '' };
  inlineStreets: { streetType: string; streetName: string }[] = [];

  plusIcon = Plus;
  editIcon = Edit;
  trashIcon = Trash2;
  restoreIcon = RotateCcw;
  mapPinIcon = MapPin;
  closeIcon = X;
  saveIcon = Save;
  loaderIcon = Loader2;
  mapIcon = Map;
  eyeIcon = Eye;

  streetTypes = [
    { value: 'JR', label: 'Jr.' },
    { value: 'AV', label: 'Av.' },
    { value: 'CALLE', label: 'Calle' },
    { value: 'PASAJE', label: 'Psje.' }
  ];

  private get headers() {
    return { 'X-User-Id': this.authService.userId() || '', 'Content-Type': 'application/json' };
  }

  get filteredZones(): Zone[] {
    const items = this.zones();
    if (this.zoneStatusFilter === 'ACTIVE') return items.filter(z => this.isActive(z.recordStatus));
    if (this.zoneStatusFilter === 'INACTIVE') return items.filter(z => !this.isActive(z.recordStatus));
    return items;
  }

  get filteredStreets(): Street[] {
    let items = this.allStreets();
    if (this.streetZoneFilter) items = items.filter(s => s.zoneId === this.streetZoneFilter);
    if (this.streetStatusFilter === 'ACTIVE') items = items.filter(s => this.isActive(s.recordStatus));
    if (this.streetStatusFilter === 'INACTIVE') items = items.filter(s => !this.isActive(s.recordStatus));
    return items;
  }

  get activeZones(): Zone[] {
    return this.zones().filter(z => this.isActive(z.recordStatus));
  }

  isActive(status: string): boolean {
    return status === 'ACTIVE';
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loadZones();
  }

  loadZones(): void {
    this.isLoadingZones.set(true);
    const orgId = this.authService.organizationId();
    this.http.get<ApiResponse<Zone[]>>(`${environment.apiUrl}/zones/organization/${orgId}`).subscribe({
      next: res => {
        this.zones.set(res.data || []);
        this.isLoadingZones.set(false);
        this.loadStreets();
      },
      error: () => this.isLoadingZones.set(false)
    });
  }

  loadStreets(): void {
    const zones = this.zones();
    if (zones.length === 0) {
      this.allStreets.set([]);
      this.isLoadingStreets.set(false);
      return;
    }
    this.isLoadingStreets.set(true);
    const requests = zones.map(z =>
      this.http.get<ApiResponse<Street[]>>(`${environment.apiUrl}/streets/zone/${z.id}`)
    );
    forkJoin(requests).subscribe({
      next: results => {
        this.allStreets.set(results.flatMap(r => r.data || []));
        this.isLoadingStreets.set(false);
      },
      error: () => this.isLoadingStreets.set(false)
    });
  }

  getStreetCountForZone(zoneId: string): number {
    return this.allStreets().filter(s => s.zoneId === zoneId).length;
  }

  getZoneName(zoneId: string): string {
    return this.zones().find(z => z.id === zoneId)?.zoneName || '—';
  }

  getStreetTypeLabel(type: string): string {
    return this.streetTypes.find(t => t.value === type)?.label || type;
  }

  viewStreetsForZone(zone: Zone): void {
    this.streetZoneFilter = zone.id;
    this.streetStatusFilter = 'ALL';
    this.activeTab.set('streets');
  }

  openZoneModal(zone?: Zone): void {
    if (zone) {
      this.editingZone.set(zone);
      this.zoneForm = { zoneName: zone.zoneName, description: zone.description || '' };
      this.inlineStreets = [];
    } else {
      this.editingZone.set(null);
      this.zoneForm = { zoneName: '', description: '' };
      this.inlineStreets = [];
    }
    this.showZoneModal.set(true);
  }

  closeZoneModal(): void {
    this.showZoneModal.set(false);
    this.editingZone.set(null);
  }

  editZone(zone: Zone): void {
    this.openZoneModal(zone);
  }

  addInlineStreet(): void {
    this.inlineStreets.push({ streetType: 'CALLE', streetName: '' });
  }

  removeInlineStreet(index: number): void {
    this.inlineStreets.splice(index, 1);
  }

  async saveZone(): Promise<void> {
    if (!this.zoneForm.zoneName.trim()) {
      this.alertService.warning('Campo requerido', 'El nombre de la zona es obligatorio');
      return;
    }

    this.isSubmitting.set(true);
    const orgId = this.authService.organizationId();

    if (this.editingZone()) {
      this.http.put<ApiResponse<Zone>>(
        `${environment.apiUrl}/zones/${this.editingZone()!.id}`,
        { zoneName: this.zoneForm.zoneName.trim(), description: this.zoneForm.description.trim() },
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.alertService.success('Actualizado', 'Zona actualizada correctamente');
          this.closeZoneModal();
          this.loadData();
        },
        error: () => {
          this.isSubmitting.set(false);
          this.alertService.error('Error', 'No se pudo actualizar la zona');
        }
      });
    } else {
      try {
        const zoneRes = await firstValueFrom(
          this.http.post<ApiResponse<Zone>>(`${environment.apiUrl}/zones`, {
            organizationId: orgId,
            zoneName: this.zoneForm.zoneName.trim(),
            description: this.zoneForm.description.trim()
          }, { headers: this.headers })
        );

        const zoneId = zoneRes.data.id;
        const validStreets = this.inlineStreets.filter(s => s.streetName.trim());

        for (const st of validStreets) {
          await firstValueFrom(
            this.http.post(`${environment.apiUrl}/streets`, {
              organizationId: orgId,
              zoneId,
              streetType: st.streetType,
              streetName: st.streetName.trim()
            }, { headers: this.headers })
          );
        }

        this.isSubmitting.set(false);
        const msg = validStreets.length > 0
          ? `Zona creada con ${validStreets.length} calle(s)`
          : 'Zona creada correctamente';
        this.alertService.success('Creado', msg);
        this.closeZoneModal();
        this.loadData();
      } catch {
        this.isSubmitting.set(false);
        this.alertService.error('Error', 'No se pudo crear la zona');
      }
    }
  }

  async deleteZone(zone: Zone): Promise<void> {
    const result = await this.alertService.confirmDelete(zone.zoneName);
    if (result.isConfirmed) {
      this.http.delete<ApiResponse<Zone>>(`${environment.apiUrl}/zones/${zone.id}`, {
        headers: this.headers,
        body: { reason: 'Eliminado por el administrador' }
      }).subscribe({
        next: () => {
          this.alertService.success('Eliminado', 'Zona eliminada correctamente');
          this.loadData();
        },
        error: () => this.alertService.error('Error', 'No se pudo eliminar la zona')
      });
    }
  }

  async restoreZone(zone: Zone): Promise<void> {
    const result = await this.alertService.confirmRestore(zone.zoneName);
    if (result.isConfirmed) {
      this.http.patch<ApiResponse<Zone>>(
        `${environment.apiUrl}/zones/${zone.id}/restore`,
        {},
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Restaurado', 'Zona restaurada correctamente');
          this.loadData();
        },
        error: () => this.alertService.error('Error', 'No se pudo restaurar la zona')
      });
    }
  }

  openStreetModal(street?: Street): void {
    if (street) {
      this.editingStreet.set(street);
      this.streetForm = { zoneId: street.zoneId, streetType: street.streetType, streetName: street.streetName };
    } else {
      this.editingStreet.set(null);
      this.streetForm = {
        zoneId: this.streetZoneFilter || (this.activeZones.length > 0 ? this.activeZones[0].id : ''),
        streetType: 'CALLE',
        streetName: ''
      };
    }
    this.showStreetModal.set(true);
  }

  closeStreetModal(): void {
    this.showStreetModal.set(false);
    this.editingStreet.set(null);
  }

  editStreet(street: Street): void {
    this.openStreetModal(street);
  }

  saveStreet(): void {
    if (!this.streetForm.zoneId || !this.streetForm.streetType || !this.streetForm.streetName.trim()) {
      this.alertService.warning('Campos requeridos', 'Complete todos los campos obligatorios');
      return;
    }

    this.isSubmitting.set(true);
    const orgId = this.authService.organizationId();

    if (this.editingStreet()) {
      this.http.put<ApiResponse<Street>>(
        `${environment.apiUrl}/streets/${this.editingStreet()!.id}`,
        { streetType: this.streetForm.streetType, streetName: this.streetForm.streetName.trim() },
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.alertService.success('Actualizado', 'Calle actualizada correctamente');
          this.closeStreetModal();
          this.loadStreets();
        },
        error: () => {
          this.isSubmitting.set(false);
          this.alertService.error('Error', 'No se pudo actualizar la calle');
        }
      });
    } else {
      this.http.post<ApiResponse<Street>>(`${environment.apiUrl}/streets`, {
        organizationId: orgId,
        zoneId: this.streetForm.zoneId,
        streetType: this.streetForm.streetType,
        streetName: this.streetForm.streetName.trim()
      }, { headers: this.headers }).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.alertService.success('Creado', 'Calle creada correctamente');
          this.closeStreetModal();
          this.loadStreets();
        },
        error: () => {
          this.isSubmitting.set(false);
          this.alertService.error('Error', 'No se pudo crear la calle');
        }
      });
    }
  }

  async deleteStreet(street: Street): Promise<void> {
    const name = street.fullStreetName || `${this.getStreetTypeLabel(street.streetType)} ${street.streetName}`;
    const result = await this.alertService.confirmDelete(name);
    if (result.isConfirmed) {
      this.http.delete<ApiResponse<Street>>(`${environment.apiUrl}/streets/${street.id}`, {
        headers: this.headers,
        body: { reason: 'Eliminado por el administrador' }
      }).subscribe({
        next: () => {
          this.alertService.success('Eliminado', 'Calle eliminada correctamente');
          this.loadStreets();
        },
        error: () => this.alertService.error('Error', 'No se pudo eliminar la calle')
      });
    }
  }

  async restoreStreet(street: Street): Promise<void> {
    const name = street.fullStreetName || `${this.getStreetTypeLabel(street.streetType)} ${street.streetName}`;
    const result = await this.alertService.confirmRestore(name);
    if (result.isConfirmed) {
      this.http.patch<ApiResponse<Street>>(
        `${environment.apiUrl}/streets/${street.id}/restore`,
        {},
        { headers: this.headers }
      ).subscribe({
        next: () => {
          this.alertService.success('Restaurado', 'Calle restaurada correctamente');
          this.loadStreets();
        },
        error: () => this.alertService.error('Error', 'No se pudo restaurar la calle')
      });
    }
  }
}
