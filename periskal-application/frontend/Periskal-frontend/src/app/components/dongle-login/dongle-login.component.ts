import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../services/Authentication/authentication.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-dongle-login',
  imports: [TranslatePipe],
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
    const login = params['login'];
      if (!login) {
        this.errorMessage = '❌ No login code provided';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000); // 2-second delay before redirect to show the animation
        return;
      }
      try {
        const decodedLogin = decodeURIComponent(params['login']).replace(/ /g, '+');
        this.isLoading = true;
        this.errorMessage = '';
        this.authenticateUser(decodedLogin);
      } catch (error) {
        this.isLoading = false;
        this.errorMessage = '❌ Invalid login code format';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000); // 2-second delay before redirect to show the animation
      }
    });
  }


  private authenticateUser(login: string) {
    this.authService.decodeDongle(login).subscribe({
      next: (response) => {
        setTimeout(() => {
          this.isLoading = false;
          // Verify token exists before navigating
          if (this.authService.isAuthenticated()) {
            this.errorMessage = ''; // Clear any previous error
            setTimeout(() => {
              this.router.navigate(['/dashboard']);
            }, 2000); // 2-second delay to show success message
          } else {
            this.errorMessage = '❌ Authentication token not stored properly';
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 2000); // 2-second delay before redirect
          }
        }, 300);
      },
      error: (error) => {
              this.isLoading = false;
              this.errorMessage = error.status === 401 
                ? '❌ Invalid or expired login code'
                : '❌ Authentication failed. Please try again.';
              setTimeout(() => {
                this.router.navigate(['/login']);
              }, 2000); // 2-second delay before redirect
            }
    });
  }
}
