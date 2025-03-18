import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, of, catchError, map } from 'rxjs';
import { AuthenticateResponse } from '../models/authenticate-response.model';
import { JwtDecoderService } from './JwtDecode/jwt-decoder.service';


@Injectable({ providedIn: 'root' })

export class AuthenticationService {
  private apiUrl = 'http://localhost:8080/api/auth/authenticate'; // change to proper api path later, really have to start using environment variables for this
  private baseUrl = 'http://localhost:8080/api';
  private readonly TOKEN_KEY = 'jwt_token';

  constructor(private http: HttpClient,
    private jwtDecoder: JwtDecoderService
  ) { }

  getAuthToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isTokenExpired(): boolean {
    return this.jwtDecoder.isTokenExpired();
  }

  isAuthenticated(): boolean {
    return !!this.getAuthToken() && !this.isTokenExpired(); // !! is a double negative , just a trick to convert the value to boolean.
  }

  authenticate(username: string, password: string): Observable<AuthenticateResponse> {
    return this.http.post<AuthenticateResponse>(this.apiUrl, { username, password }).pipe(
        tap(response => {
          if (response.token) {
            localStorage.setItem(this.TOKEN_KEY, response.token); // can store userdata in localstorage here later like usrer role maybe,or decode from token later instead of storing in localstorage
          }
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  validateToken(): Observable<boolean> {
    // If no token exists, return false immediately
    if (!this.getAuthToken()) {
      return of(false);
    }

    // Make a simple request to any protected endpoint to verify the token is valid on the server, genius way to validate token imo haha finally got this.
    return this.http.get<any>(`${this.baseUrl}/trainings`).pipe(
      tap(() => console.log('Token validated successfully')),
      map(() => true),
      catchError(error => {
        console.error('Token validation failed:', error);
        this.logout(); 
        return of(false);
      })
    );
  }

}
