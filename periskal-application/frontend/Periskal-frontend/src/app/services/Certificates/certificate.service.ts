import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Certificate } from '../../models/Certificate.model';
import { JwtDecoderService } from '../JwtDecode/jwt-decoder.service';

@Injectable({ providedIn: 'root' })
export class CertificateService {
  private baseUrl = 'http://localhost:8080/api';
  private certificatesUrl = `${this.baseUrl}/certificate`;

  constructor(
    private http: HttpClient,
    private jwtDecoder: JwtDecoderService
  ) { }

  /**
   * Get all certificates for the current logged-in user
   */
  getUserCertificates(): Observable<Certificate[]> {
    const userId = this.jwtDecoder.getId();
    return this.http.get<Certificate[]>(`${this.certificatesUrl}/user/${userId}`);
  }

  /**
   * Get a certificate by its ID
   * @param id Certificate ID
   */
  getCertificateById(id: number): Observable<Certificate> {
    return this.http.get<Certificate>(`${this.certificatesUrl}/${id}`);
  }

  /**
   * Download a certificate by ID
   * @param certificateId The ID of the certificate to download
   */
  downloadCertificateById(certificateId: number): Observable<Blob> {
    return this.http.get(`${this.certificatesUrl}/generate/${certificateId}`, { responseType: 'blob' });
  }

  /**
   * Save a new certificate or update an existing one
   * @param certificate The certificate data to save
   */
  saveCertificate(certificate: Certificate): Observable<Certificate> {
    return this.http.post<Certificate>(`${this.certificatesUrl}/save`, certificate);
  }
}
