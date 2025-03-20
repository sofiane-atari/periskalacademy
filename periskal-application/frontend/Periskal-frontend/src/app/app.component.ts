import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter, Observable } from 'rxjs';
import { AuthenticationService } from './services/authentication.service';



@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent  {
  title = 'Periskal-frontend';
  showNavbar = true;

  constructor(private router : Router, private authService: AuthenticationService) {

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.showNavbar = !this.isLoginPage();
    });
  }

  isLoginPage(): boolean {
    if (!this.router.url) return false;
    return this.router.url === '/login' || this.router.url.includes('/decode-token');
  }


   ngOnInit(): void {   
    // Only validate token when not on login-related pages
    if (!this.isLoginPage()) {
      this.authService.validateToken().subscribe(isValid => {
        if (!isValid) {
          // Redirect to login if token is invalid
          this.router.navigate(['/login']);
        }
      });
    }
  }

}
