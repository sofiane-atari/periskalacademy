import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JwtDecoderService } from '../JwtDecode/jwt-decoder.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  private baseUrl = environment.apiUrl;
  private apiUrl = `${this.baseUrl}/contact`;

  constructor(
    private http: HttpClient,
    private jwtDecoder: JwtDecoderService
  ) { }


  sendContactForm(email: string, message: string): Observable<any> {
    const userId = this.jwtDecoder.getId();
    const firstName = this.jwtDecoder.getFirstname();
    const lastName = this.jwtDecoder.getLastname();
    const username = this.jwtDecoder.getUsername();

    const contactData = {
      email: email,
      message: message,
      userId: userId,
      firstName: firstName,
      lastName: lastName,
      username: username
    };

    return this.http.post(this.apiUrl, contactData);
  }
}
