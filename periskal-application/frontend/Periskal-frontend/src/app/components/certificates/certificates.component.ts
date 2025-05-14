import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { CertificateService } from '../../services/Certificates/certificate.service';
import { finalize } from 'rxjs';
import { saveAs } from 'file-saver';

// Define Certificate interface
interface Certificate {
  name: string;
  issueDate: string;
  expiryDate: string;
  training: string;
  readyForDownload: boolean;
}

@Component({
  selector: 'app-certificates',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './certificates.component.html',
  styleUrl: './certificates.component.css'
})
export class CertificatesComponent {
  // Dummy certificate data 
  certificates: Certificate[] = [
    {
      name: 'Certificate Map Reading',
      issueDate: '15/05/2025',
      expiryDate: '15/05/2030',
      training: 'ECDIS Map Skills',
      readyForDownload: true
    },
    {
      name: 'Navigation Certificate',
      issueDate: '10/02/2025',
      expiryDate: '10/02/2028',
      training: 'Advanced Navigation',
      readyForDownload: false
    }
  ];

  isDownloading = false;

  constructor(private certificateService: CertificateService) {}

  downloadCertificate() {
    if (this.isDownloading) {
      return;
    }
    this.isDownloading = true;

    this.certificateService.downloadCertificate().pipe(
      finalize(() => this.isDownloading = false)
    ).subscribe({
      next: (blob: Blob) => {
        saveAs(blob, 'Certificate.pdf');
      },
      error: (error) => {
        console.error('Download failed:', error);
      }
    });
  }
}
