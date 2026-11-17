# Keycloak

Autentizace uživatelů přes OpenID Connect. Kontejner `keycloak` v `docker-compose.yml` běží v dev
režimu (`start-dev`) bez perzistentního úložiště: při každém startu se realm znovu naimportuje
z `realm-feicargo.json`. Změny provedené v administraci se po restartu ztratí – trvalé změny patří
do tohoto souboru.

- Administrace: http://localhost:8180/admin (`admin` / `password`, realm `master`)
- Realm: `feicargo`, issuer `http://localhost:8180/realms/feicargo`
- OIDC discovery: http://localhost:8180/realms/feicargo/.well-known/openid-configuration
- JWKS (veřejné klíče pro ověření JWT): http://localhost:8180/realms/feicargo/protocol/openid-connect/certs

## Klienti

| Client ID         | Typ          | Použití                                                                    |
|-------------------|--------------|----------------------------------------------------------------------------|
| `feicargo-web`    | veřejný      | Angular (`web/`), Authorization Code flow + PKCE (S256), `http://localhost:4200` |
| `feicargo-server` | resource server | Ktor (`server/`), sám se nepřihlašuje, jen ověřuje JWT                  |

Access token vydaný pro `feicargo-web` obsahuje:

- `aud` obsahuje `feicargo-server` – server má audience kontrolovat,
- `sub` = ID uživatele (UUID, pevně dané v importu),
- `roles` = realm role uživatele (`admin` / `customer`), plus `email`, `name`.

Server tedy ověřuje podpis (JWKS), `iss` = `http://localhost:8180/realms/feicargo`,
`aud` = `feicargo-server` a expiraci.

## Uživatelé

Heslo všech uživatelů je `password`. Přihlásit se lze uživatelským jménem i e-mailem.

| Jméno            | E-mail                         | Role       | ID (`sub`)                             |
|------------------|--------------------------------|------------|----------------------------------------|
| Albus Dumbledore | `albus.dumbledore@example.com` | `admin`    | `a1b05d0e-0000-4000-8000-000000000001` |
| Harry Potter     | `harry.potter@example.com`     | `customer` | `a1b05d0e-0000-4000-8000-000000000002` |
| Ron Weasley      | `ron.weasley@example.com`      | `customer` | `a1b05d0e-0000-4000-8000-000000000003` |
