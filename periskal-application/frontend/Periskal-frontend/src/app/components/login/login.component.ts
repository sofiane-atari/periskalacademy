import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthenticationService } from '../../services/Authentication/authentication.service';
import { JwtDecoderService } from '../../services/JwtDecode/jwt-decoder.service';
import { LoginService } from '../../services/Login/login.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  isLoading: boolean = false;
  errorMessage: string | null = null;
  showPassword = false;


  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private jwtDecoder: JwtDecoderService,
    private loginhandler: LoginService
  ) { }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      const userRole = this.jwtDecoder.getUserRole(); // debug
      console.log('User already authenticated. Role:', userRole); // debug
      this.router.navigate(['/trainingEmployee']); // change to /dashboard when we have a Single Dashboard with Conditional Rendering
    }
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
    const passwordField = document.getElementById('password') as HTMLInputElement;
    const toggleButton = document.querySelector('.toggle-password');

    if (passwordField) {
      passwordField.type = this.showPassword ? 'text' : 'password';
    }

    if (toggleButton) {
      toggleButton.classList.toggle('show-password', this.showPassword);
    }
  }

  onLogin() {

    this.isLoading = true;
    this.errorMessage = null;

    this.loginhandler.LoginUser(this.username, this.password, () => {
      this.isLoading = false;
      this.router.navigate(['/trainingEmployee']); // change to /dashboard when we have a Single Dashboard with Conditional Rendering
    },

    );

  }

}
