import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { LoginService } from '../../../services/Login/login.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-relogin-modal',
  imports: [MatInputModule, MatFormFieldModule,FormsModule],
  templateUrl: './relogin-modal.component.html',
  styleUrl: './relogin-modal.component.css'
})

export class ReloginModalComponent {

  username: string = '';
  password: string = '';
  isLoading: boolean = false;

  constructor(private router: Router,public dialogRef: MatDialogRef<ReloginModalComponent>, private loginhandler: LoginService) {}

  closeModal(): void {
	this.dialogRef.close();

  }

  login() {
    
    this.isLoading = true;

    this.loginhandler.LoginUser(this.username, this.password, () => {
        this.isLoading = false;
        this.closeModal();
      });
    }

}
