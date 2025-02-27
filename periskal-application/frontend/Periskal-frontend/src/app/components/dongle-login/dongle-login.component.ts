import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../services/authentication-service.service';

@Component({
  selector: 'app-dongle-login',
  templateUrl: './dongle-login.component.html',
  styleUrls: ['./dongle-login.component.css']
})
export class DongleLoginComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthenticationService,
  ) {}

  ngOnInit(): void {    
    // Lees de token uit de URL (query parameters)
    this.route.queryParams.subscribe(params => {
      const login = params['login']; // Haal de token uit de URL
      if (login) {
        this.authenticateUser(login); // Start de authenticatie
      } else {
        console.error('❌ Geen login token gevonden in de URL.');
      }
    });
  }

  // Functie om de token te authenticeren
  private authenticateUser(login: string) {

    // Call de authenticate service
    this.authService.authenticate(login).subscribe({
      next: (response) => {
        console.log('✅ Login succesvol:', response);
        this.router.navigate(['/trainingEmployee']); // Redirect bij succes
      },
      error: (error) => {
        console.error('❌ Login mislukt:', error);
        this.router.navigate(['/decode-token']); // Blijf op loginpagina bij fout
      }
    });
  }
}
