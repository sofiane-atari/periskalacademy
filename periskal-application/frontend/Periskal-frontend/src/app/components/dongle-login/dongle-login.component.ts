import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../services/authenticationServiceDongle/dongle-authentication.service';

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
        setTimeout(() => this.router.navigate(['/login']), 3000);
        return;
      }else{
        let decodedLogin = decodeURIComponent(params['login']).replace(/ /g, '+');

        this.isLoading = true;
        this.errorMessage = '';
    
        this.authenticateUser(decodedLogin);
      }
    });
  }

  // Functie om de token te authenticeren
  private authenticateUser(login: string) {

    this.authService.authenticate(login).subscribe({
      next: (response) => {
        setTimeout(() => {
          this.router.navigate(['/trainingEmployee']); 
        }, 2000);
      },
      error: (error) => {
        this.errorMessage = '❌ Invalid token!';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      }
    });
  }
}
