import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-certificate-dialog',
  templateUrl: './certificatedialog.component.html',
  imports: [MatIcon],
  styleUrls: ['./certificatedialog.component.css']
})
export class CertificateDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<CertificateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      examName: string;
      issueDate: string;
      validity: string;
      certificateUrl: string;
    }
  ) { }

  downloadCertificate(): void {
    window.open(this.data.certificateUrl, '_blank');
  }
}
