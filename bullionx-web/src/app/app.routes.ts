import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./app/features/auth/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./app/features/auth/register/register.component')
      .then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./app/features/dashboard/dashboard/dashboard.component')
      .then(m => m.DashboardComponent)
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];