import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

import { filter, Observable } from 'rxjs';
import { AuthenticationService } from './services/authentication.service';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TranslateModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'Periskal-frontend';
  showNavbar = true;

  lang: string = '';

  constructor(private router : Router, private authService: AuthenticationService, private translateService: TranslateService) {

    this.translateService.setDefaultLang('nl');
    this.translateService.use(localStorage.getItem('lang') || 'nl');

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.showNavbar = !this.isLoginPage();
    });
  }

  isLoginPage(): boolean {
    if (!this.router.url) return false;
    return this.router.url === '/login' || this.router.url.includes('decode-token');
  }

  ngOnInit(): void {   
    // Only validate token when not on login-related pages
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      // Check if we're on a page that needs authentication
      if (!this.isLoginPage()) {
        this.authService.validateToken().subscribe(isValid => {
          if (!isValid) {
            // Redirect to login if token is invalid
            this.router.navigate(['/login']);
          }
        });
        this.lang = localStorage.getItem('lang') || 'nl';
    }}
    )};

    ChangeLang(Lang: any) {
      const selectedLanguage = Lang.target.value;
  
      localStorage.setItem('lang', selectedLanguage);
  
      this.translateService.use(selectedLanguage); 
    }
}




