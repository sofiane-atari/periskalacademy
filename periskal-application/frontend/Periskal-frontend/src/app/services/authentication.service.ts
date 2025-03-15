import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthenticateResponse } from '../models/authenticate-response.model';


@Injectable({ providedIn: 'root' })

export class AuthenticationService {
  private apiUrl = 'http://localhost:8080/api/auth/authenticate'; // change to proper api path later

  constructor(private http: HttpClient) { }

  authenticate(username: string, password: string): Observable<AuthenticateResponse> {
    return this.http.post<AuthenticateResponse>(this.apiUrl, { username, password }).pipe(
        tap(response => {
          if (response.token) {
            localStorage.setItem('jwt_token', response.token); // can store userdata in localstorage here later like usrer role maybe,or decode from token later instead of storing in localstorage
          }
        })
      );
  }

  getAuthToken(): string | null {
    return localStorage.getItem('jwt_token');
  }
  
  logout(): void {
    localStorage.removeItem('jwt_token');
  }

}