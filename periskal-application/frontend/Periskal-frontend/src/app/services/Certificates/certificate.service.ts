import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({  providedIn: 'root'})


export class CertificateService {

  private baseUrl = 'http://localhost:8080/api';
  private apiUrl = '/certificate/generate';

  constructor(private http: HttpClient) { }

  downloadCertificate(): Observable<Blob> { return this.http.get(this.apiUrl, {responseType: 'blob'})};


}
