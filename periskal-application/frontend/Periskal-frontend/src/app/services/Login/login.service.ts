import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../Authentication/authentication.service';
import { JwtDecoderService } from '../JwtDecode/jwt-decoder.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  username: string = '';
  password: string = '';
  errorMessage: string | null = null;
  showPassword = false;

  constructor(
      private router: Router,
      private authService: AuthenticationService,
      private jwtDecoder: JwtDecoderService,
    ) { }

  LoginUser(username: string, password: string, onSuccess: (token: string) => void) {
    this.errorMessage = null;

    if (!username || !password) {
      alert('Please enter both username and password');
      return;
    }

    this.authService.authenticate(username, password).subscribe({
      next: (response) => {
        if (response.token) {
          onSuccess(response.token); 

        } else {
          alert('Authentication failed');
        }
      },
      error: (error) => {
        if (error.status === 401) {
          alert('Invalid username or password');
        } else if (error.status === 0) {
          alert('Cannot connect to server. Please check your connection.');
        } else {
          alert('An error occurred. Please try again later.');
        }
      }
    });

  }


}
