import { RecordStatus } from './user.model';

export type MovementTypeInventory = 'IN' | 'OUT' | 'ADJUSTMENT';
export type PurchaseStatus = 'PENDING' | 'RECEIVED' | 'CANCELLED';

export interface Supplier {
     id: string;
     organizationId: string;
     supplierName: string;
     ruc?: string;
     phone?: string;
     email?: string;
     address?: string;
     contactPerson?: string;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
}

export interface CreateSupplierRequest {
     organizationId: string;
     supplierName: string;
     ruc?: string;
     phone?: string;
     email?: string;
     address?: string;
     contactPerson?: string;
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
     materialCode?: string;
     categoryId: string;
     materialName: string;
     unit: string;
     currentStock: number;
     minStock: number;
     unitPrice: number;
     recordStatus: RecordStatus;
     createdAt: string;
     createdBy?: string;
     updatedAt?: string;
     updatedBy?: string;
     categoryName?: string;
}

// Alias para uso en resolución de incidencias
export interface Product {
     productId: string;      // alias de id
     organizationId: string;
     categoryId: string;
     productName: string;     // alias de materialName
     unitOfMeasure: string;   // alias de unit
     currentStock: number;
     minStock: number;
     unitPrice: number;
     recordStatus: RecordStatus;
     createdAt: string;
     categoryName?: string;
}

export interface CreateMaterialRequest {
     organizationId: string;
     materialCode?: string;
     categoryId: string;
     materialName: string;
     unit: string;
     currentStock?: number;
     minStock: number;
     unitPrice: number;
}

export interface Purchase {
     id: string;
     organizationId: string;
     supplierId: string;
     purchaseCode: string;
     purchaseDate: string;
     totalAmount: number;
     purchaseStatus: PurchaseStatus;
     invoiceNumber?: string;
     createdBy: string;
     recordStatus: RecordStatus;
     createdAt: string;
     updatedAt?: string;
     updatedBy?: string;
     supplierName?: string;
     details?: PurchaseDetail[];
}

export interface PurchaseDetail {
     id: string;
     purchaseId: string;
     materialId: string;
     quantity: number;
     unitPrice: number;
     subtotal: number;
     createdAt?: string;
     materialName?: string;
}

export interface CreatePurchaseDetailRequest {
     materialId: string;
     quantity: number;
     unitPrice: number;
}

export interface CreatePurchaseRequest {
     organizationId: string;
     supplierId: string;
     purchaseDate?: string;
     totalAmount?: number;
     invoiceNumber?: string;
     details: CreatePurchaseDetailRequest[];
}

export interface InventoryMovement {
     id: string;
     organizationId: string;
     materialId: string;
     movementType: MovementTypeInventory;
     quantity: number;
     unitPrice?: number;
     previousStock: number;
     newStock: number;
     notes: string;
     referenceId?: string;
     referenceType?: string;
     createdBy: string;
     createdAt: string;
     materialName?: string;
}
