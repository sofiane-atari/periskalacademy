import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { CertificateService } from '../../services/Certificates/certificate.service';
import { finalize } from 'rxjs';
import { saveAs } from 'file-saver';
import { Certificate } from '../../models/Certificate.model';

@Component({
  selector: 'app-certificates',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './certificates.component.html',
  styleUrl: './certificates.component.css'
})
export class CertificatesComponent implements OnInit {
  // Certificates data
  certificates: Certificate[] = [];
  isLoading = false;
  isDownloading = false;
  loadError = false;

  constructor(private certificateService: CertificateService) {}

  ngOnInit() {
    this.loadCertificates();
  }


  //Load certificates from the backend
  loadCertificates() {
    this.isLoading = true;
    this.loadError = false;

    this.certificateService.getUserCertificates().pipe(
      finalize(() => this.isLoading = false)
    ).subscribe({
      next: (data) => {
        this.certificates = data;
        //TODO: Remove after testing
        // If no certificates are returned, use dummy data for testing 
        if (this.certificates.length === 0) {
          this.loadDummyCertificates();
        }
      },
      error: (error: any) => {
        console.error('Failed to load certificates:', error);
        this.loadError = true;
      }
    });
  }


  //Download a certificate
  downloadCertificate(certificateId: number) {
    if (this.isDownloading) {
      return;
    }
    this.isDownloading = true;

    const downloadObservable = this.certificateService.downloadCertificateById(certificateId);

    downloadObservable.pipe(finalize(() => this.isDownloading = false)).subscribe({
      next: (blob: Blob) => {
        const defaultName = 'Certificate.pdf';
        const fileName = certificateId 
          ? this.getCertificateFileName(certificateId) || defaultName
          : defaultName;
        saveAs(blob, fileName);
      },
      error: (error: any) => {
        console.error('Download failed:', error);
      }
    });
  }

  //Get the file name for the certificate
  private getCertificateFileName(certificateId: number): string | null {
    const certificate = this.certificates.find(c => c.id === certificateId);
    if (certificate) {
      return certificate.name.replace(/\s+/g, '_') + '.pdf';
    }
    return null;
  }



 //remove after testing
  private loadDummyCertificates() {
    this.certificates = [
      {
        id: 1,
        name: 'Certificate Map Reading',
        issueDate: '15/05/2025',
        expiryDate: '15/05/2030',
        training: 'ECDIS Map Skills',
        readyForDownload: true,
        userId: 'user123'
      },
      {
        id: 2,
        name: 'Navigation Certificate',
        issueDate: '10/02/2025',
        expiryDate: '10/02/2028',
        training: 'Advanced Navigation',
        readyForDownload: false,
        userId: 'user123'
      }
    ];
  }
}
