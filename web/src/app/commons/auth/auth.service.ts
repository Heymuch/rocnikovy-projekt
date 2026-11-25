import { Injectable, inject, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly oidc = inject(OidcSecurityService);

  readonly isAuthenticated = toSignal(
    this.oidc.isAuthenticated$.pipe(map((res) => res.isAuthenticated)),
    { initialValue: false }
  );

  readonly userData = toSignal(
    this.oidc.userData$.pipe(map((res) => res.userData)),
    { initialValue: null }
  );

  private readonly accessToken = toSignal(this.oidc.getAccessToken(), { initialValue: '' });

  readonly roles = computed<string[]>(() => {
    const token = this.accessToken();
    if (!token) return [];
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload?.roles ?? payload?.realm_access?.roles ?? [];
    } catch {
      return [];
    }
  });

  readonly isAdmin = computed(() => this.roles().includes('admin'));
  readonly isCustomer = computed(() => this.roles().includes('customer'));
  readonly username = computed(() => this.userData()?.name ?? this.userData()?.email ?? '');

  hasRole(role: string): boolean {
    return this.roles().includes(role);
  }

  logout(): void {
    this.oidc.logoff().subscribe();
  }
}
