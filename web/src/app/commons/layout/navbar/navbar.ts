import {Component} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [
    MatToolbar,
    MatIconButton,
    MatIcon,
    MatTooltip,
    MatButton,
    RouterLink,
    TranslatePipe,
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  /*readonly*/ isAuthenticated = false //Později nějak zjistit jestli je přihlášený nebo ne
  readonly isAdmin = true // Později z rolí
  currentLanguage = 'cs';

  constructor(private translate: TranslateService) {
    const current = this.translate.getCurrentLang();
    const fallback = this.translate.getFallbackLang();

    this.currentLanguage = current || fallback || 'cs';
  }

  toggleLanguage() {
    this.currentLanguage = this.currentLanguage === 'cs' ? 'en' : 'cs';
    this.translate.use(this.currentLanguage);
  }

//   Až bude backend, tak smazat!!!
  login() {
    this.isAuthenticated = true;
  }

  // Funkce pro simulaci odhlášení
  logout() {
    this.isAuthenticated = false;
  }


}
