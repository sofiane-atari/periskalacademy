import { Injectable } from '@angular/core';
import { JwtDecoderService } from './JwtDecode/jwt-decoder.service';

@Injectable({
  providedIn: 'root'
})
export class RoleAuthorizationService {
  constructor(private jwtDecoderService: JwtDecoderService) {}
  
  hasPermission(requiredPermissions: string[]): boolean {
    const userRole = this.jwtDecoderService.getUserRole();
    // If user has Admin or Support role, they can do everything
    if (userRole === 'ADMIN' || userRole === 'Support') {
      return true;
    }
    
    // Otherwise, check if user role is in the required permissions
    return requiredPermissions.includes(userRole || '');
  }
  
  canEditContent(): boolean {
    return this.hasPermission(['ADMIN', 'Support']);
  }
  
  canAddContent(): boolean {
    return this.hasPermission(['ADMIN', 'Support']);
  }
}
