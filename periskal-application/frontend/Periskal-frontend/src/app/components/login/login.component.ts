import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(
    private router: Router,
    private authService: AuthenticationService,
  ) { }

  onLogin() {
    this.authService.authenticate(this.username, this.password).subscribe({
      next: (response) => {
        if (response) {
          this.router.navigate(['/dashboard']);
        } else {
          alert('Authentication failed');
        }
      },
      error: (error) => {
        alert('Error during authentication');
      },    
    });
  }
}
