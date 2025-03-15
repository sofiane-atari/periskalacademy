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

