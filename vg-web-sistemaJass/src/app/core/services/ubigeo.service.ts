import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, tap } from 'rxjs';

export interface UbigeoData {
    [department: string]: {
        [province: string]: {
            [district: string]: {
                ubigeo: string;
                id: number;
                inei?: string;
            };
        };
    };
}

@Injectable({
    providedIn: 'root'
})
export class UbigeoService {
    private http = inject(HttpClient);
    private ubigeoData = signal<UbigeoData | null>(null);

    constructor() {
        this.loadUbigeoData().subscribe();
    }

    private loadUbigeoData(): Observable<UbigeoData> {
        return this.http.get<UbigeoData>('assets/peru.json').pipe(
            tap(data => this.ubigeoData.set(data))
        );
    }

    getDepartments(): string[] {
        const data = this.ubigeoData();
        return data ? Object.keys(data).sort() : [];
    }

    getProvinces(department: string): string[] {
        const data = this.ubigeoData();
        if (!data || !data[department]) return [];
        return Object.keys(data[department]).sort();
    }

    getDistricts(department: string, province: string): string[] {
        const data = this.ubigeoData();
        if (!data || !data[department] || !data[department][province]) return [];
        return Object.keys(data[department][province]).sort();
    }

    // Ensure data is loaded before use
    ensureDataLoaded(): Observable<UbigeoData> {
        if (this.ubigeoData()) {
            return new Observable(observer => {
                observer.next(this.ubigeoData()!);
                observer.complete();
            });
        }
        return this.loadUbigeoData();
    }
}
