import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
     Material,
     CreateMaterialRequest,
     Supplier,
     CreateSupplierRequest,
     ProductCategory,
     Purchase,
     CreatePurchaseRequest,
     CreatePurchaseDetailRequest,
     InventoryMovement,
     ApiResponse
} from '../models';

@Injectable({
     providedIn: 'root'
})
export class InventoryService {
     private http = inject(HttpClient);
     private apiUrl = environment.apiUrl;

     // ==================== MATERIALS ====================
     getMaterials(): Observable<Material[]> {
          return this.http.get<ApiResponse<Material[]>>(`${this.apiUrl}/materials`)
               .pipe(map(res => res.data || []));
     }

     getMaterialById(id: string): Observable<Material> {
          return this.http.get<ApiResponse<Material>>(`${this.apiUrl}/materials/${id}`)
               .pipe(map(res => res.data!));
     }

     getActiveMaterials(): Observable<Material[]> {
          return this.http.get<ApiResponse<Material[]>>(`${this.apiUrl}/materials/active`)
               .pipe(map(res => res.data || []));
     }

     getMaterialsByCategory(categoryId: string): Observable<Material[]> {
          return this.http.get<ApiResponse<Material[]>>(`${this.apiUrl}/materials/category/${categoryId}`)
               .pipe(map(res => res.data || []));
     }

     createMaterial(request: CreateMaterialRequest): Observable<Material> {
          return this.http.post<ApiResponse<Material>>(`${this.apiUrl}/materials`, request)
               .pipe(map(res => res.data!));
     }

     updateMaterial(id: string, request: Partial<CreateMaterialRequest>): Observable<Material> {
          return this.http.put<ApiResponse<Material>>(`${this.apiUrl}/materials/${id}`, request)
               .pipe(map(res => res.data!));
     }

     deleteMaterial(id: string): Observable<void> {
          return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/materials/${id}`)
               .pipe(map(() => undefined));
     }

     restoreMaterial(id: string): Observable<Material> {
          return this.http.patch<ApiResponse<Material>>(`${this.apiUrl}/materials/${id}/restore`, {})
               .pipe(map(res => res.data!));
     }

     // ==================== SUPPLIERS ====================
     getSuppliers(): Observable<Supplier[]> {
          return this.http.get<ApiResponse<Supplier[]>>(`${this.apiUrl}/suppliers`)
               .pipe(map(res => res.data || []));
     }

     getSupplierById(id: string): Observable<Supplier> {
          return this.http.get<ApiResponse<Supplier>>(`${this.apiUrl}/suppliers/${id}`)
               .pipe(map(res => res.data!));
     }

     getActiveSuppliers(): Observable<Supplier[]> {
          return this.http.get<ApiResponse<Supplier[]>>(`${this.apiUrl}/suppliers/active`)
               .pipe(map(res => res.data || []));
     }

     createSupplier(request: CreateSupplierRequest): Observable<Supplier> {
          return this.http.post<ApiResponse<Supplier>>(`${this.apiUrl}/suppliers`, request)
               .pipe(map(res => res.data!));
     }

     updateSupplier(id: string, request: Partial<CreateSupplierRequest>): Observable<Supplier> {
          return this.http.put<ApiResponse<Supplier>>(`${this.apiUrl}/suppliers/${id}`, request)
               .pipe(map(res => res.data!));
     }

     deleteSupplier(id: string): Observable<void> {
          return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/suppliers/${id}`)
               .pipe(map(() => undefined));
     }

     restoreSupplier(id: string): Observable<Supplier> {
          return this.http.patch<ApiResponse<Supplier>>(`${this.apiUrl}/suppliers/${id}/restore`, {})
               .pipe(map(res => res.data!));
     }

     // ==================== CATEGORIES ====================
     getCategories(): Observable<ProductCategory[]> {
          return this.http.get<ApiResponse<ProductCategory[]>>(`${this.apiUrl}/product-categories`)
               .pipe(map(res => res.data || []));
     }

     getCategoryById(id: string): Observable<ProductCategory> {
          return this.http.get<ApiResponse<ProductCategory>>(`${this.apiUrl}/product-categories/${id}`)
               .pipe(map(res => res.data!));
     }

     getActiveCategories(): Observable<ProductCategory[]> {
          return this.http.get<ApiResponse<ProductCategory[]>>(`${this.apiUrl}/product-categories/active`)
               .pipe(map(res => res.data || []));
     }

     createCategory(request: { organizationId: string; categoryName: string; description?: string }): Observable<ProductCategory> {
          return this.http.post<ApiResponse<ProductCategory>>(`${this.apiUrl}/product-categories`, request)
               .pipe(map(res => res.data!));
     }

     updateCategory(id: string, request: { categoryName: string; description?: string }): Observable<ProductCategory> {
          return this.http.put<ApiResponse<ProductCategory>>(`${this.apiUrl}/product-categories/${id}`, request)
               .pipe(map(res => res.data!));
     }

     deleteCategory(id: string): Observable<void> {
          return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/product-categories/${id}`)
               .pipe(map(() => undefined));
     }

     restoreCategory(id: string): Observable<ProductCategory> {
          return this.http.patch<ApiResponse<ProductCategory>>(`${this.apiUrl}/product-categories/${id}/restore`, {})
               .pipe(map(res => res.data!));
     }

     // ==================== PURCHASES ====================
     getPurchases(): Observable<Purchase[]> {
          return this.http.get<ApiResponse<Purchase[]>>(`${this.apiUrl}/purchases`)
               .pipe(map(res => res.data || []));
     }

     getPurchaseById(id: string): Observable<Purchase> {
          return this.http.get<ApiResponse<Purchase>>(`${this.apiUrl}/purchases/${id}`)
               .pipe(map(res => res.data!));
     }

     getActivePurchases(): Observable<Purchase[]> {
          return this.http.get<ApiResponse<Purchase[]>>(`${this.apiUrl}/purchases/active`)
               .pipe(map(res => res.data || []));
     }

     getPurchasesBySupplier(supplierId: string): Observable<Purchase[]> {
          return this.http.get<ApiResponse<Purchase[]>>(`${this.apiUrl}/purchases/supplier/${supplierId}`)
               .pipe(map(res => res.data || []));
     }

     createPurchase(request: CreatePurchaseRequest): Observable<Purchase> {
          return this.http.post<ApiResponse<Purchase>>(`${this.apiUrl}/purchases`, request)
               .pipe(map(res => res.data!));
     }

     updatePurchase(id: string, request: CreatePurchaseRequest): Observable<Purchase> {
          return this.http.put<ApiResponse<Purchase>>(`${this.apiUrl}/purchases/${id}`, request)
               .pipe(map(res => res.data!));
     }

     deletePurchase(id: string): Observable<void> {
          return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/purchases/${id}`)
               .pipe(map(() => undefined));
     }

     restorePurchase(id: string): Observable<Purchase> {
          return this.http.patch<ApiResponse<Purchase>>(`${this.apiUrl}/purchases/${id}/restore`, {})
               .pipe(map(res => res.data!));
     }

     // ==================== INVENTORY MOVEMENTS ====================
     getMovements(): Observable<InventoryMovement[]> {
          return this.http.get<ApiResponse<InventoryMovement[]>>(`${this.apiUrl}/inventory-movements`)
               .pipe(map(res => res.data || []));
     }

     getMovementById(id: string): Observable<InventoryMovement> {
          return this.http.get<ApiResponse<InventoryMovement>>(`${this.apiUrl}/inventory-movements/${id}`)
               .pipe(map(res => res.data!));
     }

     getMovementsByMaterial(materialId: string): Observable<InventoryMovement[]> {
          return this.http.get<ApiResponse<InventoryMovement[]>>(`${this.apiUrl}/inventory-movements/material/${materialId}`)
               .pipe(map(res => res.data || []));
     }

     createMovement(request: {
          organizationId: string;
          materialId: string;
          movementType: 'IN' | 'OUT' | 'ADJUSTMENT';
          quantity: number;
          notes: string;
          referenceId?: string;
     }): Observable<InventoryMovement> {
          return this.http.post<ApiResponse<InventoryMovement>>(`${this.apiUrl}/inventory-movements`, request)
               .pipe(map(res => res.data!));
     }
}
