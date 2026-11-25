import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, take } from 'rxjs';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const oidc = inject(OidcSecurityService);
  const auth = inject(AuthService);
  const router = inject(Router);

  return oidc.isAuthenticated$.pipe(
    take(1),
    map(({ isAuthenticated }) => {
      if (!isAuthenticated) {
        oidc.authorize();
        return false;
      }

      const requiredRoles: string[] = route.data['roles'] || [];
      if (requiredRoles.length === 0) {
        return true;
      }

      const hasAccess = requiredRoles.some((role) => auth.hasRole(role));
      return hasAccess ? true : router.createUrlTree(['/']);
    })
  );
};
