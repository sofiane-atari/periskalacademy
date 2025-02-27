import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  public apiUrl = 'http://localhost:8080/api/auth/decode-token';  // Zorg ervoor dat dit correct is

  constructor(private http: HttpClient) {}

  authenticate(login: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?login=${login}`);
  }
}
