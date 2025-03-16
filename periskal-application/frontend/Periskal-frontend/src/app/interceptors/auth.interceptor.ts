import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthenticationService } from '../services/authentication.service';



export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthenticationService);
  
  
  if (authService.isAuthenticated()) {
    const token = authService.getAuthToken();
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(cloned);
  }

  // Handle expired tokens 
  if (authService.getAuthToken() && authService.isTokenExpired()) {
    // maybe add refresh token logic here later for now just logout
    authService.logout();
  }

  return next(req);
};