import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';

export const authGuard = () => {
    const router = inject(Router);
    const authService = inject(AuthenticationService);
  
    // Check both token existence and validity
    if (!authService.isAuthenticated()) {
      router.navigate(['/login']);
      return false;
    }
    return true;
  };
  