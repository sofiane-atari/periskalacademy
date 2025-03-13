import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  sub: string;
  role: string;
  exp: number;
  iat: number;
}

@Injectable({
  providedIn: 'root'
})
export class JwtDecoderService {
  private readonly TOKEN_KEY = 'jwt_token'; 

  constructor() {}

  private getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private parseJwt(): JwtPayload | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      return jwtDecode<JwtPayload>(token);
    } catch (error) {
      console.error('Error decoding JWT:', error);
      return null;
    }
  }

  getUsername(): string | null {
    return this.parseJwt()?.sub ?? null;
  }

  getUserRole(): string | null {
    return this.parseJwt()?.role ?? null;
  }
}

