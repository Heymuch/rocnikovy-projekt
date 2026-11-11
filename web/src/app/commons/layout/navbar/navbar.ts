import {Component} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [
    MatToolbar,
    MatIcon,
    MatButton,
    RouterLink,
    TranslatePipe,
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  /*readonly*/ isAuthenticated = true
  readonly isAdmin = true
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
