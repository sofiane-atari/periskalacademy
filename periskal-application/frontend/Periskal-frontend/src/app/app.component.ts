import { Component, OnInit } from '@angular/core';
import {
  NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { RoleAuthorizationService } from './services/RoleAuthorization/role-authorization.service';
import { filter, Observable } from 'rxjs';
import { AuthenticationService } from './services/Authentication/authentication.service';
import { JwtDecoderService } from '../app/services/JwtDecode/jwt-decoder.service';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})

export class AppComponent implements OnInit {
  title = 'Periskal-frontend';
  showNavbar = true;
  mobileMenuOpen = false;
  userName?: string;
  userRole = "";

  lang: string = '';

  constructor(private router: Router
    , private authService: AuthenticationService
    , private translateService: TranslateService
    , public roleAuth: RoleAuthorizationService
    , private jwtDecoderService: JwtDecoderService
  ) {

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

  goToHome() {
    this.router.navigate(['/dashboard'])
  }

  ngOnInit(): void {
    this.refreshUserData();
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
          } else {
            this.refreshUserData();
          }
        });
        this.lang = localStorage.getItem('lang') || 'nl';
      } else {
        this.userName = undefined;
        this.userRole = '';
      }
    }
    )

    this.authService.ExpiryTimeChecker();

  };

  ChangeLang(Lang: any) {
    const selectedLanguage = Lang.target.value;

    localStorage.setItem('lang', selectedLanguage);

    this.translateService.use(selectedLanguage);
    this.reloadPage();
  }

  reloadPage(): void {
    window.location.reload();
  }
  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }
  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
  refreshUserData(): void {
    this.userName = this.jwtDecoderService.getUsername()!;
    this.userRole = this.jwtDecoderService.getUserRole()!;
  }
}




