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
      console.log('Query Params:', params); // <-- Check if params exist
  
      if (!params['login']) {
        this.errorMessage = '❌ Token not found';
        console.error('Login token missing in query parameters');
        return;
      }
  
      try {
        const decodedLogin = decodeURIComponent(params['login']).replace(/ /g, '+');
        console.log('Decoded Login:', decodedLogin); // <-- Log token before API call
  
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
    console.log('Calling decodeDongle with login:', login); // <-- Check if function runs
  
    this.authService.decodeDongle(login).subscribe({
      next: (response) => {
        console.log('Dongle authentication successful', response);
        setTimeout(() => {
          localStorage.setItem('jwt_token', response.token);
          this.router.navigate(['/trainingEmployee']);
        }, 2000);
      },
      error: (error) => {
        console.error('Dongle authentication failed', error);
        this.errorMessage = '❌ Authentication failed. Please try again.';
        this.isLoading = false;
      }
    });
  }
}
