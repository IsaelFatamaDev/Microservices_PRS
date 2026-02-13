import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
     selector: 'app-forbidden',
     standalone: true,
     imports: [RouterLink],
     template: `
    <div class="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div class="text-center">
        <img src="assets/Gotita.png" alt="JASS" class="w-24 h-24 mx-auto mb-6 opacity-50">
        <h1 class="text-6xl font-bold text-gray-300 mb-2">403</h1>
        <h2 class="text-2xl font-semibold text-gray-700 mb-4">Acceso Denegado</h2>
        <p class="text-gray-500 mb-8">No tienes permisos para acceder a esta página.</p>
        <a routerLink="/auth/login" class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          Volver al inicio
        </a>
      </div>
    </div>
  `
})
export class ForbiddenComponent { }
