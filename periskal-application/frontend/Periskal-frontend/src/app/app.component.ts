import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Observable } from 'rxjs';
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

  constructor(private router : Router, private authService: AuthenticationService) {}

  isLoginPage() : boolean {
    return this.router.url === '/login'
  }

   ngOnInit(): void {   
    // Validate token on dashboard load
    this.authService.validateToken().subscribe(isValid => {
      if (!isValid) {
        // Redirect to login if token is invalid
        this.router.navigate(['/login']);
      }
    });
  }

}