import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { CertificateService } from '../../../services/Certificates/certificate.service';
import { finalize } from 'rxjs';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-certificate-dialog',
  templateUrl: './certificatedialog.component.html',
  standalone: true,
  imports: [MatIcon, TranslatePipe],
  styleUrls: ['./certificatedialog.component.css']
})
export class CertificateDialogComponent {
  isDownloading = false;
  certificateId: number | null = null;

  constructor(
    public dialogRef: MatDialogRef<CertificateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      examName: string;
      issueDate?: string;
      validity?: string;
      status: 'passed' | 'failed';
      certificateId?: number; // Added to receive certificateId
    },
    private router: Router,
    private certificateService: CertificateService
  ) {
    this.certificateId = data.certificateId || null;
  }

  get passed(): boolean {
    return this.data.status === 'passed';
  }

  downloadCertificate(): void {
    if (this.isDownloading || !this.certificateId) {
      console.warn('Download attempted but certificateId is missing or download in progress');
      return;
    }
    this.isDownloading = true;
    this.certificateService.downloadCertificateById(this.certificateId).pipe(
      finalize(() => {
        this.isDownloading = false;
      })
    ).subscribe({
      next: (blob: Blob) => {
        const fileName = `Certificate_For_${this.data.examName.replace(/\s+/g, '_')}.pdf`;
        saveAs(blob, fileName);
      },
      error: (error: any) => {
        console.error('Download failed', error);
        alert('Failed to download certificate. Please try again.');
      }
    });
  }

  goToExamsPage(): void {
    this.router.navigate(['/exams']);
    this.dialogRef.close();
  }

  retryExam(): void {
    this.router.navigate(['/exam', this.data.examName]);
    this.dialogRef.close();
  }

  goToCertificatesPage(): void {
    this.router.navigate(['/certificates'], { queryParams: { newCertificateId: this.certificateId } });
    this.dialogRef.close();
  }
}