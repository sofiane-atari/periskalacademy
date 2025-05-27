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

      if (!params['login']) {
        return;
      }
      try {
        const decodedLogin = decodeURIComponent(params['login']).replace(/ /g, '+');
        this.isLoading = true;
        this.errorMessage = '';
        this.authenticateUser(decodedLogin);
      } catch (error) {
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
            this.router.navigate(['/trainingEmployee']);
          }
        }, 300);
      },
      error: (error) => {
        this.isLoading = false;
      }
    });
  }
}
