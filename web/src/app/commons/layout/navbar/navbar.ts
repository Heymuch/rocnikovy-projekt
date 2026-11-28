import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { RouterLink } from '@angular/router';
import { AuthService } from 'src/app/commons/auth/auth.service';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-navbar',
  imports: [MatToolbar, MatIcon, MatButton, RouterLink, TranslatePipe],
  templateUrl: './navbar.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './navbar.scss',
})
export class Navbar {
  readonly #authService = inject(AuthService);
  readonly #securityService = inject(OidcSecurityService);
  readonly isAdmin = this.#authService.isAdmin();
  readonly isCustomer = this.#authService.isCustomer();
  readonly isAuthenticated = this.#authService.isAuthenticated();
  readonly username = this.#authService.username;

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

  login(): void {
    this.#securityService.authorize();
  }

  logout(): void {
    this.#authService.logout();
  }
}
