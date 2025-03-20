import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../services/authentication.service';

@Component({
  selector: 'app-dongle-login',
  templateUrl: './dongle-login.component.html',
  styleUrls: ['./dongle-login.component.css']
})
export class DongleLoginComponent implements OnInit {
  isLoading: boolean = false; // Show loading state
  errorMessage: string = '';  // Show error message if login fails

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthenticationService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {

      if (!params['login']) {
        this.errorMessage = '❌ Token not found';
        console.error('Login token missing in query parameters');
        return;
      }
      try {
        const decodedLogin = decodeURIComponent(params['login']).replace(/ /g, '+');
        this.isLoading = true;
        this.errorMessage = '';
        this.authenticateUser(decodedLogin);
      } catch (error) {
        console.error('Token decoding failed', error);
      }
    });
  }

  // Functie om de token te authenticeren
  private authenticateUser(login: string) {
    this.authService.decodeDongle(login).subscribe({
      next: (response) => {
        setTimeout(() => {
          console.log('Dongle authentication successful', response);
          localStorage.setItem('jwt_token', response.token); // Ensure token is stored
          this.router.navigate(['/trainingEmployee']); 
        }, 2000);
      },
      error: (error) => {
        console.error('Dongle authentication failed', error);
        this.errorMessage = '❌ Authentication failed. Please try again.';
        this.isLoading = false; // Stop loading state
      }
    });
  }
}  

