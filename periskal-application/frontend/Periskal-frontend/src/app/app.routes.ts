import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },  // Login route
    { path: '**', redirectTo: 'login' }            // Redirect all unknown routes to login
  ];

