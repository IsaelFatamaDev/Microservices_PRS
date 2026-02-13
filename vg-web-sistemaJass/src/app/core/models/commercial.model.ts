import { RecordStatus } from './user.model';

export type PaymentStatus = 'PENDING' | 'PAID' | 'OVERDUE' | 'CANCELLED';
export type PaymentMethod = 'CASH' | 'TRANSFER' | 'YAPE' | 'PLIN';
export type ReceiptStatus = 'PENDING' | 'PAID' | 'CANCELLED';
export type ServiceCutStatus = 'SCHEDULED' | 'EXECUTED' | 'RESTORED' | 'CANCELLED';
export type MovementType = 'INCOME' | 'EXPENSE';

export interface Receipt {
     id: string;
     organizationId: string;
     userId: string;
     waterBoxId: string;
     receiptNumber: string;
     issueMonth: string;
     issueYear: number;
     issueDate: string;
     dueDate: string;
     amount: number;
     receiptStatus: ReceiptStatus;
     recordStatus: RecordStatus;
     createdAt: string;
     userName?: string;
     supplyNumber?: string;
}

export interface Payment {
     id: string;
     organizationId: string;
     userId: string;
     receiptId: string;
     paymentNumber: string;
     paymentDate: string;
     amount: number;
     paymentMethod: PaymentMethod;
     concept: string;
     monthsPaid: string[];
     processedBy: string;
     recordStatus: RecordStatus;
     createdAt: string;
     userName?: string;
     receiptNumber?: string;
     userPhone?: string;
}

export interface CreatePaymentRequest {
     organizationId: string;
     userId: string;
     receiptId?: string;
     amount: number;
     paymentMethod: PaymentMethod;
     concept: string;
     monthsPaid: string[];
}

export interface Debt {
     id: string;
     organizationId: string;
     userId: string;
     waterBoxId: string;
     totalDebt: number;
     monthsOwed: number;
     lastPaymentDate?: string;
     recordStatus: RecordStatus;
     userName?: string;
     supplyNumber?: string;
     zoneName?: string;
}

export interface ServiceCut {
     id: string;
     organizationId: string;
     userId: string;
     waterBoxId: string;
     cutDate: string;
     cutReason: string;
     cutStatus: ServiceCutStatus;
     restorationDate?: string;
     executedBy: string;
     recordStatus: RecordStatus;
     createdAt: string;
     userName?: string;
     supplyNumber?: string;
}

export interface PettyCash {
     id: string;
     organizationId: string;
     currentBalance: number;
     lastUpdated: string;
}

export interface PettyCashMovement {
     id: string;
     organizationId: string;
     movementType: MovementType;
     amount: number;
     concept: string;
     referenceId?: string;
     movementDate: string;
     registeredBy: string;
     createdAt: string;
}
