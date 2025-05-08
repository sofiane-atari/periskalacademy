import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-login-expiry',
  imports: [],
  templateUrl: './login-expiry.component.html',
  styleUrl: './login-expiry.component.css'
})
export class LoginExpiryComponent {

  constructor(public dialogRef: MatDialogRef<LoginExpiryComponent>) {}

  closeModal(): void {
    this.dialogRef.close();
  
    }

}
