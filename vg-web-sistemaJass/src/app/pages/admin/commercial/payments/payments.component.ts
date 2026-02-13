import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, CreditCard } from 'lucide-angular';

@Component({
     selector: 'app-payments',
     standalone: true,
     imports: [CommonModule, LucideAngularModule],
     template: `
    <div>
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Pagos</h1>
        <p class="text-gray-500">Registro y gestión de pagos</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-12 text-center">
        <lucide-icon [img]="creditCardIcon" [size]="64" class="text-gray-300 mx-auto mb-4"></lucide-icon>
        <p class="text-gray-500">Módulo en desarrollo</p>
      </div>
    </div>
  `
})
export class PaymentsComponent {
     creditCardIcon = CreditCard;
}
