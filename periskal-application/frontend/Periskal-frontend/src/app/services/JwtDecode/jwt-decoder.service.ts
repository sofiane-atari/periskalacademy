import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  sub: string;
  role: string;
  id: string;
  firstname: string;
  lastname: string;
  shipname: string;
  products: string[];
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
      return null;
    }
  }

  isTokenExpired(): boolean {
    const payload = this.parseJwt();
    if (!payload || !payload.exp) return true; 
    return payload.exp * 1000 < Date.now(); 
  }

  getTokenTimeToExpiry(): number | null {
    const payload = this.parseJwt();
    if (!payload || !payload.exp) return null;
    return Math.max(0, payload.exp - Math.floor(Date.now() / 1000));
  }

  getDecodedToken(): JwtPayload | null {
    return this.parseJwt();
  }

  getUsername(): string | null {
    return this.parseJwt()?.sub ?? null;
  }

  getUserRole(): string | null {
    return this.parseJwt()?.role ?? null;
  }

  getId(): string | null {
    return this.parseJwt()?.id ?? null;
  }

  getFirstname(): string | null {
    return this.parseJwt()?.firstname ?? null;
  }

  getLastname(): string | null {
    return this.parseJwt()?.lastname ?? null;
  }

  getProducts(): string[] | null {
    return this.parseJwt()?.products ?? null;
  }

  getShipname(): string | null {
        return this.parseJwt()?.shipname ?? null;
    }

}

