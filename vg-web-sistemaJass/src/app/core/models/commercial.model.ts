import { RecordStatus } from './user.model';

export type DebtStatus = 'PENDING' | 'PARTIAL' | 'PAID' | 'CANCELLED';
export type ReceiptStatus = 'PENDING' | 'PARTIAL' | 'PAID' | 'OVERDUE' | 'CANCELLED';
export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'CANCELLED' | 'FAILED';
export type PaymentMethod = 'CASH' | 'BANK_TRANSFER' | 'CARD' | 'YAPE' | 'PLIN';
export type ConceptType = 'MONTHLY_FEE' | 'LATE_FEE' | 'ASSEMBLY_FINE' | 'WORK_FINE' | 'RECONNECTION_FEE' | 'INSTALLATION_FEE' | 'TRANSFER_FEE' | 'OTHER';
export type CutReason = 'NON_PAYMENT' | 'VIOLATION' | 'MAINTENANCE' | 'USER_REQUEST';
export type CutStatus = 'PENDING' | 'EXECUTED' | 'RECONNECTED' | 'CANCELLED';
export type PettyCashStatus = 'ACTIVE' | 'CLOSED' | 'AUDITED';
export type MovementType = 'IN' | 'OUT' | 'ADJUSTMENT';
export type MovementCategory = 'SUPPLIES' | 'TRANSPORT' | 'FOOD' | 'EMERGENCY' | 'OTHER';

export interface Debt {
  id: string;
  organizationId: string;
  recordStatus: RecordStatus;
  createdAt: string;
  createdBy: string;
  updatedAt?: string;
  updatedBy?: string;
  userId: string;
  userFullName?: string;
  periodMonth: number;
  periodYear: number;
  periodDescription?: string;
  originalAmount: number;
  pendingAmount: number;
  lateFee: number;
  totalAmount?: number;
  debtStatus: DebtStatus;
  dueDate: string;
}

export interface CreateDebtRequest {
  userId: string;
  periodMonth: number;
  periodYear: number;
  originalAmount: number;
}

export interface UpdateDebtRequest {
  pendingAmount?: number;
  lateFee?: number;
  debtStatus?: string;
}

export interface Receipt {
  id: string;
  organizationId: string;
  recordStatus: RecordStatus;
  createdAt: string;
  createdBy: string;
  updatedAt?: string;
  updatedBy?: string;
  receiptNumber: string;
  userId: string;
  userFullName?: string;
  periodMonth: number;
  periodYear: number;
  periodDescription?: string;
  issueDate: string;
  dueDate: string;
  totalAmount: number;
  paidAmount: number;
  pendingAmount: number;
  receiptStatus: ReceiptStatus;
  notes?: string;
  details?: ReceiptDetail[];
}

export interface ReceiptDetail {
  id: string;
  receiptId: string;
  conceptType: ConceptType;
  description: string;
  amount: number;
  createdAt: string;
}

export interface CreateReceiptRequest {
  userId: string;
  periodMonth: number;
  periodYear: number;
  totalAmount: number;
  notes?: string;
  details: CreateReceiptDetailRequest[];
}

export interface CreateReceiptDetailRequest {
  conceptType: string;
  description: string;
  amount: number;
}

export interface UpdateReceiptRequest {
  paidAmount?: number;
  receiptStatus?: string;
  notes?: string;
}

export interface Payment {
  id: string;
  organizationId: string;
  recordStatus: RecordStatus;
  createdAt: string;
  createdBy: string;
  updatedAt?: string;
  updatedBy?: string;
  userId: string;
  userFullName?: string;
  paymentDate: string;
  totalAmount: number;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  receiptNumber: string;
  notes?: string;
  details?: PaymentDetail[];
}

export interface PaymentDetail {
  id: string;
  paymentId: string;
  paymentType: ConceptType;
  description: string;
  amount: number;
  periodMonth?: number;
  periodYear?: number;
  periodDescription?: string;
  createdAt: string;
}

export interface CreatePaymentRequest {
  userId: string;
  totalAmount: number;
  paymentMethod: string;
  notes?: string;
  details: CreatePaymentDetailRequest[];
}

export interface CreatePaymentDetailRequest {
  paymentType: string;
  description: string;
  amount: number;
  periodMonth?: number;
  periodYear?: number;
}

export interface UpdatePaymentRequest {
  paymentStatus?: string;
  notes?: string;
}

export interface ServiceCut {
  id: string;
  organizationId: string;
  recordStatus: RecordStatus;
  createdAt: string;
  createdBy: string;
  updatedAt?: string;
  updatedBy?: string;
  userId: string;
  userFullName?: string;
  waterBoxId: string;
  scheduledDate: string;
  executedDate?: string;
  cutReason: CutReason;
  debtAmount: number;
  reconnectionDate?: string;
  reconnectionFeePaid: boolean;
  cutStatus: CutStatus;
  notes?: string;
}

export interface CreateServiceCutRequest {
  userId: string;
  waterBoxId: string;
  scheduledDate: string;
  cutReason: string;
  debtAmount?: number;
  notes?: string;
}

export interface UpdateServiceCutRequest {
  scheduledDate?: string;
  cutStatus?: string;
  notes?: string;
}

export interface PettyCash {
  id: string;
  organizationId: string;
  recordStatus: RecordStatus;
  createdAt: string;
  createdBy: string;
  updatedAt?: string;
  updatedBy?: string;
  responsibleUserId: string;
  responsibleUserName?: string;
  currentBalance: number;
  maxAmountLimit: number;
  pettyCashStatus: PettyCashStatus;
}

export interface CreatePettyCashRequest {
  responsibleUserId: string;
  initialBalance: number;
  maxAmountLimit: number;
}

export interface UpdatePettyCashRequest {
  responsibleUserId?: string;
  maxAmountLimit?: number;
  pettyCashStatus?: string;
}

export interface PettyCashMovement {
  id: string;
  organizationId: string;
  createdAt: string;
  createdBy: string;
  pettyCashId: string;
  movementDate: string;
  movementType: MovementType;
  amount: number;
  category: MovementCategory;
  description: string;
  voucherNumber?: string;
  previousBalance: number;
  newBalance: number;
}

export interface RegisterMovementRequest {
  pettyCashId: string;
  movementType: string;
  amount: number;
  category: string;
  description: string;
  voucherNumber?: string;
}
