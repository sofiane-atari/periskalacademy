import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Faq } from '../../models/Faq.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FaqService {

  private readonly apiUrl = `${environment.apiUrl}/faq`;

  constructor(private http: HttpClient) {}

  getFaqs(): Observable<Faq[]> {
    return this.http.get<Faq[]>(this.apiUrl);
  }

  getFaq(id: number): Observable<Faq> {
    return this.http.get<Faq>(`${this.apiUrl}/${id}`);
  }

  createFaq(faq: Faq): Observable<Faq> {
    return this.http.post<Faq>(this.apiUrl, faq);
  }

  updateFaq(id: number, faq: Faq): Observable<Faq> {
    return this.http.put<Faq>(`${this.apiUrl}/${id}`, faq);
  }

  deleteFaq(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}