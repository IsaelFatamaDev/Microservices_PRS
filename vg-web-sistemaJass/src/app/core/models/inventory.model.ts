import { RecordStatus } from './user.model';

export type MovementTypeInventory = 'ENTRY' | 'EXIT' | 'ADJUSTMENT';
export type PurchaseStatus = 'PENDING' | 'RECEIVED' | 'CANCELLED';

export interface Supplier {
     id: string;
     organizationId: string;
     supplierName: string;
     ruc?: string;
     phone?: string;
     email?: string;
     address?: string;
     recordStatus: RecordStatus;
     createdAt: string;
}

export interface CreateSupplierRequest {
     organizationId: string;
     supplierName: string;
     ruc?: string;
     phone?: string;
     email?: string;
     address?: string;
}

export interface ProductCategory {
     id: string;
     organizationId: string;
     categoryName: string;
     description?: string;
     recordStatus: RecordStatus;
     createdAt: string;
}

export interface Material {
     id: string;
     organizationId: string;
     categoryId: string;
     materialName: string;
     description?: string;
     unit: string;
     currentStock: number;
     minStock: number;
     unitCost: number;
     recordStatus: RecordStatus;
     createdAt: string;
     categoryName?: string;
}

export interface CreateMaterialRequest {
     organizationId: string;
     categoryId: string;
     materialName: string;
     description?: string;
     unit: string;
     minStock: number;
     unitCost: number;
}

export interface Purchase {
     id: string;
     organizationId: string;
     supplierId: string;
     purchaseNumber: string;
     purchaseDate: string;
     totalAmount: number;
     purchaseStatus: PurchaseStatus;
     registeredBy: string;
     recordStatus: RecordStatus;
     createdAt: string;
     supplierName?: string;
     details?: PurchaseDetail[];
}

export interface PurchaseDetail {
     id: string;
     purchaseId: string;
     materialId: string;
     quantity: number;
     unitCost: number;
     totalCost: number;
     materialName?: string;
}

export interface CreatePurchaseRequest {
     organizationId: string;
     supplierId: string;
     details: CreatePurchaseDetailRequest[];
}

export interface CreatePurchaseDetailRequest {
     materialId?: string;
     materialName?: string;
     categoryId?: string;
     quantity: number;
     unitCost: number;
}

export interface InventoryMovement {
     id: string;
     organizationId: string;
     materialId: string;
     movementType: MovementTypeInventory;
     quantity: number;
     previousStock: number;
     newStock: number;
     reason: string;
     referenceId?: string;
     registeredBy: string;
     movementDate: string;
     createdAt: string;
     materialName?: string;
}
