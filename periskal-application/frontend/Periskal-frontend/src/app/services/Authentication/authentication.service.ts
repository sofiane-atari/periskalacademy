import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, of, catchError, map } from 'rxjs';
import { AuthenticateResponse } from '../../models/authenticate-response.model';
import { JwtDecoderService } from '../JwtDecode/jwt-decoder.service';
import { ReloginModalComponent } from '../../components/Modals/relogin-modal/relogin-modal.component';
import { MatDialog } from '@angular/material/dialog';
import { LoginExpiryComponent } from '../../components/Modals/login-expiry/login-expiry.component';


@Injectable({ providedIn: 'root' })

export class AuthenticationService {
  private baseUrl = 'http://localhost:8080/api'; // change to proper api path later, really have to start using environment variables for these urls
  private authUrl  = `${this.baseUrl}/auth/authenticate`; // Main authentication endpoint
  private dongleTokenUrl = `${this.baseUrl}/auth/decode-token`; // Dongle authentication endpoint
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly SKIPPERS_LIST_KEY = 'skippers_List';
  private readonly bufferTime = 15 * 60; // 15 minutes 

  constructor(
    private http: HttpClient,
    private jwtDecoder: JwtDecoderService,
    private dialog: MatDialog  
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


  private handleAuthSuccess(response: any): void {
    if (response.token) {
      localStorage.setItem(this.TOKEN_KEY, response.token);
    }
    if (response.skippers && response.skippers.length > 0) {
      localStorage.setItem(this.SKIPPERS_LIST_KEY, JSON.stringify(response.skippers));
    }
  }

  authenticate(username: string, password: string): Observable<AuthenticateResponse> {
    return this.http.post<AuthenticateResponse>(this.authUrl, { username, password }).pipe(
        tap(response => this.handleAuthSuccess(response))
      );
  }

  decodeDongle(login: string): Observable<any> {
    return this.http.get<any>(`${this.dongleTokenUrl}?login=${login}`).pipe(
      tap(response => this.handleAuthSuccess(response))
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.SKIPPERS_LIST_KEY);
  }

  validateToken(): Observable<boolean> {
    // If no token exists, return false immediately
    if (!this.getAuthToken()) {
      return of(false);
    }

    // Make a simple request to any protected endpoint to verify the token is valid on the server, genius way to validate token imo haha finally got this.
    return this.http.get<any>(`${this.baseUrl}/trainings`).pipe(
      map(() => true),
      catchError(error => {
        this.logout(); 
        return of(false);
      })
    );
  }

  ExpiryTimeChecker() {

    const timeToExpiry = this.jwtDecoder.getTokenTimeToExpiry(); 
    if (timeToExpiry === null) {
      this.logout(); // If we can't get the expiry time, we assume the token is invalid
      return;
    }

    const role = this.jwtDecoder.getUserRole(); 

    const triggerTime = (timeToExpiry - this.bufferTime) * 1000

    if (role === 'Administrator' || role === 'Fleetmanager' || role === 'Support') {
      if (triggerTime <= 0) {
        
        this.promptReLogin(); 

      }
      else {
        setTimeout(() => {
          this.promptReLogin(); 
        }, triggerTime);
      }
    }
    else if (role === 'Skipper') {
      if (triggerTime <= 0) {
        
        this.promptLoginExpiry();

      }
      else {
        setTimeout(() => {
          this.promptLoginExpiry();
        }, triggerTime);
      }
    }
  }

  promptReLogin() {
    
    this.dialog.open(ReloginModalComponent);

  }

  promptLoginExpiry() {
    this.dialog.open(LoginExpiryComponent)
  }
}
