import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Certificate } from '../../models/Certificate.model';
import { JwtDecoderService } from '../JwtDecode/jwt-decoder.service';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CertificateService {
  private baseUrl = environment.apiUrl;
  private certificatesUrl = `${this.baseUrl}/certificate`;

  constructor(
    private http: HttpClient,
    private jwtDecoder: JwtDecoderService
  ) { }

  getCertificatesByUserId(userId: string) {
    return this.http.get<Certificate[]>(`${this.certificatesUrl}/user/${userId}`);
  }

  //Get all certificates for the current logged-in user
  getUserCertificates(): Observable<Certificate[]> {
    const userId = this.jwtDecoder.getId();
    return this.http.get<Certificate[]>(`${this.certificatesUrl}/user/${userId}`);
  }


  //Get a certificate by its ID
  getCertificateById(id: number): Observable<Certificate> {
    return this.http.get<Certificate>(`${this.certificatesUrl}/${id}`);
  }



  downloadCertificateById(certificateId: number): Observable<Blob> {

    // Get user information from JWT token
    const firstName = this.jwtDecoder.getFirstname() || '';
    const lastName = this.jwtDecoder.getLastname() || '';
    const shipName = this.jwtDecoder.getShipname() || '';

    // Create query parameters with user information
    const params = {
      firstName,
      lastName,
      shipName
    };

    return this.http.get(`${this.certificatesUrl}/generate/${certificateId}`, {
      params,
      responseType: 'blob'
    });
  }


  //Save a new certificate or update an existing ones
  saveCertificate(certificate: Certificate): Observable<Certificate> {
    return this.http.post<Certificate>(`${this.certificatesUrl}/save`, certificate);
  }
}
