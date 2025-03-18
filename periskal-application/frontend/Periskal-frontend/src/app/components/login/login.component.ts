import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../services/authentication.service';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';

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
  isLoading: boolean = false;
  errorMessage: string | null = null;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private jwtDecoder: JwtDecoderService,
  ) { }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      const userRole = this.jwtDecoder.getUserRole(); // debug
      console.log('User already authenticated. Role:', userRole); // debug
      this.router.navigate(['/trainingEmployee']); // change to /dashboard when we have a Single Dashboard with Conditional Rendering
    }
  }


  onLogin() {
    this.isLoading = true;
    this.errorMessage = null;

    if (!this.username || !this.password) {
      alert('Please enter both username and password');
      this.isLoading = false;
      return;
    }

    this.authService.authenticate(this.username, this.password).subscribe({
      next: (response) => {
        if (response.token) {
          console.log('Authentication successful'); // debug
          this.router.navigate(['/trainingEmployee']); // change to /dashboard when we have a Single Dashboard with Conditional Rendering
        } else {
          alert('Authentication failed');
        }
        this.isLoading = false;
      },
      error: (error) => {
        if (error.status === 401) {
          alert('Invalid username or password');
        } else if (error.status === 0) {
          alert('Cannot connect to server. Please check your connection.');
        } else {
          alert('An error occurred. Please try again later.');
        }
        console.error('Login error:', error);
        this.isLoading = false;
      }
    });

  }
}
