import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';

@Component({
  selector: 'app-certificate-dialog',
  templateUrl: './certificatedialog.component.html',
  standalone: true,
  imports: [MatIcon],
  styleUrls: ['./certificatedialog.component.css']
})
export class CertificateDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<CertificateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      examName: string;
      issueDate?: string;
      validity?: string;
      status: 'passed' | 'failed';
    },
    private router: Router
  ) { }

  get passed(): boolean {
    return this.data.status === 'passed';
  }

  goToExamsPage(): void {
    this.router.navigate(['/exams']);
    this.dialogRef.close();
  }

  retryExam(): void {
    this.router.navigate(['/exam', this.data.examName]);
    this.dialogRef.close();
  }
}