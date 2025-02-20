import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs'; 
import { AuthenticateResponse } from '../models/authenticate-response.model'; 


@Injectable({ providedIn: 'root' })

export class AuthenticationService {
  private apiUrl = 'http://localhost:4200/api/auth/authenticate'; // change to proper api path later

  constructor(private http: HttpClient) {}

  authenticate(username: string, password: string): Observable<AuthenticateResponse> {
    return this.http.post<AuthenticateResponse>(this.apiUrl, { username, password });
  }
}