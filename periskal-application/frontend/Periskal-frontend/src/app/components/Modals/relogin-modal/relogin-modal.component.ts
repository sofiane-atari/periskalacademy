import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';


@Component({
  selector: 'app-relogin-modal',
  imports: [MatInputModule, MatFormFieldModule,FormsModule],
  templateUrl: './relogin-modal.component.html',
  styleUrl: './relogin-modal.component.css'
})

export class ReloginModalComponent {

  username: string = '';
  password: string = '';

  constructor(public dialogRef: MatDialogRef<ReloginModalComponent>) {}

  closeModal(): void {
	this.dialogRef.close();

  }

  login() {
    throw new Error('Method not implemented.');
    }

}
