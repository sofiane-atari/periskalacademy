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

  ngOnInit() {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      this.router.navigate(['/trainingEmployee']);
    }
  }

  onLogin() {
    this.authService.authenticate(this.username, this.password).subscribe({
      next: (response) => {
        console.log('Auth Response:', response);
        if (response.token) {
          console.log('JWT Token:', response.token);
          alert('Login successful!');
          this.router.navigate(['/trainingEmployee']);
        } else {
          alert('Authentication failed');
        }
      },
      error: (error) => {
        if (error.status === 401) {
          alert('Invalid credentials');
        } else {
          alert('Something went wrong. Please try again later.');
        }
      }

    });
  }
}
