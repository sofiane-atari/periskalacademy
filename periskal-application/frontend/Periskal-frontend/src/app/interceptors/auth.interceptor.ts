import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthenticationService } from '../services/Authentication/authentication.service';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';



export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);
  
  
  if (authService.isAuthenticated()) {
    const token = authService.getAuthToken();
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(cloned).pipe( // Could probably handle UNAUTHORIZED status codes better then just logging them out, but for now this is fine
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          authService.logout();
          router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }

  // Handle expired tokens 
  if (authService.getAuthToken() && authService.isTokenExpired()) {
    // maybe add refresh token logic here later for now just logout
    authService.logout();
    router.navigate(['/login']);
  }

  return next(req);
};