import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Wallet } from 'lucide-angular';

@Component({
     selector: 'app-petty-cash',
     standalone: true,
     imports: [CommonModule, LucideAngularModule],
     template: `
    <div>
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Caja Chica</h1>
        <p class="text-gray-500">Control de caja chica y movimientos</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-12 text-center">
        <lucide-icon [img]="walletIcon" [size]="64" class="text-gray-300 mx-auto mb-4"></lucide-icon>
        <p class="text-gray-500">Módulo en desarrollo</p>
      </div>
    </div>
  `
})
export class PettyCashComponent {
     walletIcon = Wallet;
}
