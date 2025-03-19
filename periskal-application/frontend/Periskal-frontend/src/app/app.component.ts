import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, NgModule, OnInit } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'Periskal-frontend';

  lang: string = '';

  constructor(
    private router: Router,
    private translateService: TranslateService
  ) {
    this.translateService.setDefaultLang('nl');
    this.translateService.use(localStorage.getItem('lang') || 'nl');
  }

  ngOnInit(): void {
    this.lang = localStorage.getItem('lang') || 'nl';
  }

  isLoginPage(): boolean {
    return this.router.url === '/login';
  }

  ChangeLang(Lang: any) {
    const selectedLanguage = Lang.target.value;

    localStorage.setItem('lang', selectedLanguage);

    this.translateService.use(selectedLanguage);
  }
}
