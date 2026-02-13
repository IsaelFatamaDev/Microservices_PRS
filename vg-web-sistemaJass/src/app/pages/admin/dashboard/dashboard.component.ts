import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import {
  LucideAngularModule,
  Users,
  Droplets,
  Coins,
  AlertCircle,
  Building2,
  MapPin,
  Phone,
  Mail,
  ArrowRight,
  UserPlus,
  Banknote,
  Settings,
  BarChart3,
  ShieldCheck,
  Clock
} from 'lucide-angular';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import { ApiResponse, PageResponse } from '../../../core';

interface DashboardStats {
  totalUsers: number;
  totalWaterBoxes: number;
  pendingDebts: number;
  activeComplaints: number;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, LucideAngularModule],
  templateUrl: './dashboard.component.html'
})
export class AdminDashboardComponent implements OnInit {
  private http = inject(HttpClient);
  public authService = inject(AuthService);

  organization = this.authService.organization;

  stats = signal<DashboardStats>({
    totalUsers: 0,
    totalWaterBoxes: 0,
    pendingDebts: 0,
    activeComplaints: 0
  });

  usersIcon = Users;
  dropletsIcon = Droplets;
  coinsIcon = Coins;
  alertCircleIcon = AlertCircle;
  buildingIcon = Building2;
  mapPinIcon = MapPin;
  phoneIcon = Phone;
  mailIcon = Mail;
  arrowRightIcon = ArrowRight;
  userPlusIcon = UserPlus;
  banknoteIcon = Banknote;
  settingsIcon = Settings;
  barChartIcon = BarChart3;
  shieldIcon = ShieldCheck;
  clockIcon = Clock;

  ngOnInit(): void {
    console.log('Dashboard: Organization Signal:', this.organization());
    this.loadStats();
  }

  private loadStats(): void {
    const orgId = this.authService.organizationId();
    if (!orgId) return;

    const fetchCount = (url: string) =>
      this.http.get<ApiResponse<PageResponse<any>>>(`${environment.apiUrl}/${url}`, {
        params: { organizationId: orgId, page: 0, size: 1 }
      });

    fetchCount('users').subscribe({
      next: res => this.updateStat('totalUsers', res.data?.totalElements || 0),
      error: () => console.warn('Failed to load users count')
    });

    fetchCount('water-boxes').subscribe({
      next: res => this.updateStat('totalWaterBoxes', res.data?.totalElements || 0),
      error: () => console.warn('Failed to load water boxes count')
    });

    fetchCount('debts').subscribe({
      next: res => this.updateStat('pendingDebts', res.data?.totalElements || 0),
      error: () => console.warn('Failed to load debts count')
    });

    fetchCount('complaints').subscribe({
      next: res => this.updateStat('activeComplaints', res.data?.totalElements || 0),
      error: () => console.warn('Failed to load complaints count')
    });
  }

  private updateStat(key: keyof DashboardStats, value: number) {
    this.stats.update(s => ({ ...s, [key]: value }));
  }
}
