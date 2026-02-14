import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import {
  Debt, CreateDebtRequest, UpdateDebtRequest,
  Receipt, CreateReceiptRequest, UpdateReceiptRequest,
  Payment, CreatePaymentRequest, UpdatePaymentRequest,
  ServiceCut, CreateServiceCutRequest, UpdateServiceCutRequest,
  PettyCash, CreatePettyCashRequest, UpdatePettyCashRequest,
  PettyCashMovement, RegisterMovementRequest
} from '../models/commercial.model';

@Injectable({ providedIn: 'root' })
export class CommercialService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  createDebt(req: CreateDebtRequest): Observable<ApiResponse<Debt>> {
    return this.http.post<ApiResponse<Debt>>(`${this.base}/debts`, req);
  }
  getDebt(id: string): Observable<ApiResponse<Debt>> {
    return this.http.get<ApiResponse<Debt>>(`${this.base}/debts/${id}`);
  }
  getDebts(status?: string, userId?: string): Observable<ApiResponse<Debt[]>> {
    let p = new HttpParams();
    if (status) p = p.set('status', status);
    if (userId) p = p.set('userId', userId);
    p = p.set('page', '0').set('size', '500');
    return this.http.get<ApiResponse<Debt[]>>(`${this.base}/debts`, { params: p });
  }
  getDebtsByUser(userId: string): Observable<ApiResponse<Debt[]>> {
    return this.http.get<ApiResponse<Debt[]>>(`${this.base}/debts/user/${userId}`);
  }
  getPendingDebts(): Observable<ApiResponse<Debt[]>> {
    return this.http.get<ApiResponse<Debt[]>>(`${this.base}/debts/pending`);
  }
  getUserTotalDebt(userId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(`${this.base}/debts/user/${userId}/total`);
  }
  updateDebt(id: string, req: UpdateDebtRequest): Observable<ApiResponse<Debt>> {
    return this.http.put<ApiResponse<Debt>>(`${this.base}/debts/${id}`, req);
  }
  deleteDebt(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/debts/${id}`);
  }
  restoreDebt(id: string): Observable<ApiResponse<Debt>> {
    return this.http.patch<ApiResponse<Debt>>(`${this.base}/debts/${id}/restore`, {});
  }

  createReceipt(req: CreateReceiptRequest): Observable<ApiResponse<Receipt>> {
    return this.http.post<ApiResponse<Receipt>>(`${this.base}/receipts`, req);
  }
  getReceipt(id: string): Observable<ApiResponse<Receipt>> {
    return this.http.get<ApiResponse<Receipt>>(`${this.base}/receipts/${id}`);
  }
  getReceipts(status?: string, userId?: string): Observable<ApiResponse<Receipt[]>> {
    let p = new HttpParams();
    if (status) p = p.set('status', status);
    if (userId) p = p.set('userId', userId);
    p = p.set('page', '0').set('size', '500');
    return this.http.get<ApiResponse<Receipt[]>>(`${this.base}/receipts`, { params: p });
  }
  getReceiptsByUser(userId: string): Observable<ApiResponse<Receipt[]>> {
    return this.http.get<ApiResponse<Receipt[]>>(`${this.base}/receipts/user/${userId}`);
  }
  getReceiptsByPeriod(month: number, year: number): Observable<ApiResponse<Receipt[]>> {
    const p = new HttpParams().set('month', month).set('year', year);
    return this.http.get<ApiResponse<Receipt[]>>(`${this.base}/receipts/period`, { params: p });
  }
  updateReceipt(id: string, req: UpdateReceiptRequest): Observable<ApiResponse<Receipt>> {
    return this.http.put<ApiResponse<Receipt>>(`${this.base}/receipts/${id}`, req);
  }
  deleteReceipt(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/receipts/${id}`);
  }
  restoreReceipt(id: string): Observable<ApiResponse<Receipt>> {
    return this.http.patch<ApiResponse<Receipt>>(`${this.base}/receipts/${id}/restore`, {});
  }

  createPayment(req: CreatePaymentRequest): Observable<ApiResponse<Payment>> {
    return this.http.post<ApiResponse<Payment>>(`${this.base}/payments`, req);
  }
  getPayment(id: string): Observable<ApiResponse<Payment>> {
    return this.http.get<ApiResponse<Payment>>(`${this.base}/payments/${id}`);
  }
  getPayments(status?: string, userId?: string): Observable<ApiResponse<Payment[]>> {
    let p = new HttpParams();
    if (status) p = p.set('status', status);
    if (userId) p = p.set('userId', userId);
    p = p.set('page', '0').set('size', '500');
    return this.http.get<ApiResponse<Payment[]>>(`${this.base}/payments`, { params: p });
  }
  getPaymentsByUser(userId: string): Observable<ApiResponse<Payment[]>> {
    return this.http.get<ApiResponse<Payment[]>>(`${this.base}/payments/user/${userId}`);
  }
  updatePayment(id: string, req: UpdatePaymentRequest): Observable<ApiResponse<Payment>> {
    return this.http.put<ApiResponse<Payment>>(`${this.base}/payments/${id}`, req);
  }
  deletePayment(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/payments/${id}`);
  }
  restorePayment(id: string): Observable<ApiResponse<Payment>> {
    return this.http.patch<ApiResponse<Payment>>(`${this.base}/payments/${id}/restore`, {});
  }

  createServiceCut(req: CreateServiceCutRequest): Observable<ApiResponse<ServiceCut>> {
    return this.http.post<ApiResponse<ServiceCut>>(`${this.base}/service-cuts`, req);
  }
  getServiceCut(id: string): Observable<ApiResponse<ServiceCut>> {
    return this.http.get<ApiResponse<ServiceCut>>(`${this.base}/service-cuts/${id}`);
  }
  getServiceCuts(status?: string, userId?: string): Observable<ApiResponse<ServiceCut[]>> {
    let p = new HttpParams();
    if (status) p = p.set('status', status);
    if (userId) p = p.set('userId', userId);
    p = p.set('page', '0').set('size', '500');
    return this.http.get<ApiResponse<ServiceCut[]>>(`${this.base}/service-cuts`, { params: p });
  }
  getServiceCutsByUser(userId: string): Observable<ApiResponse<ServiceCut[]>> {
    return this.http.get<ApiResponse<ServiceCut[]>>(`${this.base}/service-cuts/user/${userId}`);
  }
  getPendingCuts(): Observable<ApiResponse<ServiceCut[]>> {
    return this.http.get<ApiResponse<ServiceCut[]>>(`${this.base}/service-cuts/pending`);
  }
  updateServiceCut(id: string, req: UpdateServiceCutRequest): Observable<ApiResponse<ServiceCut>> {
    return this.http.put<ApiResponse<ServiceCut>>(`${this.base}/service-cuts/${id}`, req);
  }
  executeServiceCut(id: string): Observable<ApiResponse<ServiceCut>> {
    return this.http.patch<ApiResponse<ServiceCut>>(`${this.base}/service-cuts/${id}/execute`, {});
  }
  reconnectServiceCut(id: string): Observable<ApiResponse<ServiceCut>> {
    return this.http.patch<ApiResponse<ServiceCut>>(`${this.base}/service-cuts/${id}/reconnect`, {});
  }
  deleteServiceCut(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/service-cuts/${id}`);
  }
  restoreServiceCut(id: string): Observable<ApiResponse<ServiceCut>> {
    return this.http.patch<ApiResponse<ServiceCut>>(`${this.base}/service-cuts/${id}/restore`, {});
  }

  createPettyCash(req: CreatePettyCashRequest): Observable<ApiResponse<PettyCash>> {
    return this.http.post<ApiResponse<PettyCash>>(`${this.base}/petty-cash`, req);
  }
  getPettyCash(id: string): Observable<ApiResponse<PettyCash>> {
    return this.http.get<ApiResponse<PettyCash>>(`${this.base}/petty-cash/${id}`);
  }
  getActivePettyCash(): Observable<ApiResponse<PettyCash>> {
    return this.http.get<ApiResponse<PettyCash>>(`${this.base}/petty-cash/active`);
  }
  getAllPettyCash(status?: string): Observable<ApiResponse<PettyCash[]>> {
    let p = new HttpParams().set('page', '0').set('size', '500');
    if (status) p = p.set('status', status);
    return this.http.get<ApiResponse<PettyCash[]>>(`${this.base}/petty-cash`, { params: p });
  }
  getPettyCashMovements(pettyCashId: string): Observable<ApiResponse<PettyCashMovement[]>> {
    return this.http.get<ApiResponse<PettyCashMovement[]>>(`${this.base}/petty-cash/${pettyCashId}/movements`);
  }
  registerMovement(req: RegisterMovementRequest): Observable<ApiResponse<PettyCashMovement>> {
    return this.http.post<ApiResponse<PettyCashMovement>>(`${this.base}/petty-cash/movements`, req);
  }
  updatePettyCash(id: string, req: UpdatePettyCashRequest): Observable<ApiResponse<PettyCash>> {
    return this.http.put<ApiResponse<PettyCash>>(`${this.base}/petty-cash/${id}`, req);
  }
  deletePettyCash(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/petty-cash/${id}`);
  }
  restorePettyCash(id: string): Observable<ApiResponse<PettyCash>> {
    return this.http.patch<ApiResponse<PettyCash>>(`${this.base}/petty-cash/${id}/restore`, {});
  }
}
