import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, NgModule, OnInit } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'Periskal-frontend';

  lang:string = '';

  constructor(
    private router: Router, private translateService: TranslateService
  ) 
  {
    this.translateService.setDefaultLang('nl')
  }

  ngOnInit(): void {
    this.lang = localStorage.getItem('lang') || 'nl';
    this.translateService.use(this.lang);
  }

  isLoginPage(): boolean {
    return this.router.url === '/login';
  }

  ChangeLang(Lang: any) {
    const selectedLanguage = Lang.target.value;
    console.log(selectedLanguage);

    localStorage.setItem('lang', selectedLanguage);

    this.translateService.use(selectedLanguage);
  }
}
